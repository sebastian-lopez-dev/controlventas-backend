package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.negocio.controlventas.dto.NoPagoRequest;
import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.model.Cuota;
import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.model.MotivoNoPago;
import com.negocio.controlventas.model.VisitaCobranza;
import com.negocio.controlventas.repository.CobradorRepository;
import com.negocio.controlventas.repository.CuotaRepository;
import com.negocio.controlventas.repository.VisitaCobranzaRepository;

import com.negocio.controlventas.dto.VisitaCobranzaResponse;
import com.negocio.controlventas.model.Cliente;
import com.negocio.controlventas.model.VentaCredito;

@Service
public class VisitaCobranzaService {

    private final VisitaCobranzaRepository visitaRepository;

    private final CuotaRepository cuotaRepository;
    private final CobradorRepository cobradorRepository;

    public VisitaCobranzaService(
            VisitaCobranzaRepository visitaRepository,
            CuotaRepository cuotaRepository,
            CobradorRepository cobradorRepository) {
        this.visitaRepository = visitaRepository;
        this.cuotaRepository = cuotaRepository;
        this.cobradorRepository = cobradorRepository;
    }

    @Transactional
    public VisitaCobranza registrarNoPago(
            NoPagoRequest solicitud) {
        validarSolicitud(solicitud);

        Cuota cuota = cuotaRepository
                .findById(solicitud.getIdCuota())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la cuota con el ID "
                                + solicitud.getIdCuota()));

        Cobrador cobrador = cobradorRepository
                .findById(solicitud.getIdCobrador())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el cobrador con el ID "
                                + solicitud.getIdCobrador()));

        if (!Boolean.TRUE.equals(
                cobrador.getActivo())) {
            throw new IllegalArgumentException(
                    "El cobrador está desactivado");
        }

        if (cuota.getVenta().getEstado() != EstadoVenta.ACTIVA) {
            throw new IllegalArgumentException(
                    "La venta no está activa");
        }

        if (cuota.getSaldoCuota()
                .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La cuota ya está pagada");
        }

        Cobrador cobradorAsignado = cuota.getVenta().getCobrador();

        if (cobradorAsignado == null) {
            throw new IllegalArgumentException(
                    "El contrato no tiene cobrador asignado");
        }

        if (!cobradorAsignado.getIdCobrador()
                .equals(cobrador.getIdCobrador())) {
            throw new IllegalArgumentException(
                    "La cuota no pertenece a este cobrador");
        }

        if (solicitud.getMotivo() == MotivoNoPago.PROMETIO_PAGAR
                && solicitud.getProximaFecha() == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la nueva fecha prometida");
        }

        if (solicitud.getProximaFecha() != null
                && solicitud.getProximaFecha()
                        .isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La próxima fecha no puede ser anterior a hoy");
        }

        if (solicitud.getMotivo() == MotivoNoPago.OTRO
                && (solicitud.getObservaciones() == null
                        || solicitud.getObservaciones().isBlank())) {
            throw new IllegalArgumentException(
                    "Debes explicar el motivo");
        }

        VisitaCobranza visita = new VisitaCobranza();

        visita.setCuota(cuota);
        visita.setCobrador(cobrador);
        visita.setMotivo(solicitud.getMotivo());
        visita.setObservaciones(
                limpiarTexto(solicitud.getObservaciones()));
        visita.setProximaFecha(
                solicitud.getProximaFecha());

        if (solicitud.getFechaVisita() == null) {
            visita.setFechaVisita(
                    LocalDateTime.now());
        } else {
            visita.setFechaVisita(
                    solicitud.getFechaVisita());
        }

        return visitaRepository.save(visita);
    }

    public List<VisitaCobranzaResponse> listarPorCobrador(Long idCobrador) {

        cobradorRepository
                .findById(idCobrador)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el cobrador con el ID "
                                + idCobrador));

        return visitaRepository
                .findByCobrador_IdCobradorOrderByFechaVisitaDesc(
                        idCobrador)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    public List<VisitaCobranzaResponse> listarPorCuota(Long idCuota) {

        cuotaRepository
                .findById(idCuota)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la cuota con el ID "
                                + idCuota));

        return visitaRepository
                .findByCuota_IdCuotaOrderByFechaVisitaDesc(
                        idCuota)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    private VisitaCobranzaResponse convertirRespuesta(
            VisitaCobranza visita) {
        Cuota cuota = visita.getCuota();

        VentaCredito venta = cuota.getVenta();

        Cliente cliente = venta.getCliente();

        String nombreCompleto = cliente.getNombres()
                + " "
                + cliente.getApellidoPaterno();

        if (cliente.getApellidoMaterno() != null
                && !cliente.getApellidoMaterno().isBlank()) {
            nombreCompleto += " " + cliente.getApellidoMaterno();
        }

        return new VisitaCobranzaResponse(
                visita.getIdVisita(),
                visita.getFechaVisita(),
                visita.getMotivo(),
                visita.getObservaciones(),
                visita.getProximaFecha(),

                cuota.getIdCuota(),
                cuota.getNumeroCuota(),

                venta.getIdVenta(),
                venta.getNumeroContrato(),

                cliente.getIdCliente(),
                cliente.getCodigoCliente(),
                nombreCompleto);
    }

    private void validarSolicitud(
            NoPagoRequest solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "Los datos de la visita son obligatorios");
        }

        if (solicitud.getIdCuota() == null) {
            throw new IllegalArgumentException(
                    "La cuota es obligatoria");
        }

        if (solicitud.getIdCobrador() == null) {
            throw new IllegalArgumentException(
                    "El cobrador es obligatorio");
        }

        if (solicitud.getMotivo() == null) {
            throw new IllegalArgumentException(
                    "El motivo es obligatorio");
        }
    }

    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

}