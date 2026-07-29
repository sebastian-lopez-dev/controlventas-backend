package com.negocio.controlventas.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.negocio.controlventas.dto.CobranzaPendienteResponse;
import com.negocio.controlventas.model.Cliente;
import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.model.Cuota;
import com.negocio.controlventas.model.EstadoCuota;
import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.model.VentaCredito;
import com.negocio.controlventas.repository.CobradorRepository;
import com.negocio.controlventas.repository.CuotaRepository;

@Service
public class CobranzaService {

        private final CuotaRepository cuotaRepository;
        private final CobradorRepository cobradorRepository;

        public CobranzaService(
                        CuotaRepository cuotaRepository,
                        CobradorRepository cobradorRepository) {

                this.cuotaRepository = cuotaRepository;
                this.cobradorRepository = cobradorRepository;
        }

        public List<CobranzaPendienteResponse> listarCobranzas(
                        Long idCobrador,
                        LocalDate fechaConsulta) {

                if (fechaConsulta == null) {
                        throw new IllegalArgumentException(
                                        "La fecha es obligatoria");
                }

                Cobrador cobrador = cobradorRepository
                                .findById(idCobrador)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No existe el cobrador con el ID "
                                                                + idCobrador));

                if (!Boolean.TRUE.equals(
                                cobrador.getActivo())) {

                        throw new IllegalArgumentException(
                                        "El cobrador está desactivado");
                }

                List<Cuota> cuotas = cuotaRepository.buscarCobrosHastaFecha(
                                idCobrador,
                                fechaConsulta,
                                EstadoVenta.ACTIVA);

                List<CobranzaPendienteResponse> respuesta = new ArrayList<>();

                for (Cuota cuota : cuotas) {

                        respuesta.add(
                                        convertirRespuesta(
                                                        cuota,
                                                        fechaConsulta));
                }

                return respuesta;
        }

        private CobranzaPendienteResponse convertirRespuesta(
                        Cuota cuota,
                        LocalDate fechaConsulta) {

                VentaCredito venta = cuota.getVenta();

                Cliente cliente = venta.getCliente();

                long diasAtraso = 0;

                EstadoCuota estadoMostrado;

                if (cuota.getFechaVencimiento()
                                .isBefore(fechaConsulta)) {

                        diasAtraso = ChronoUnit.DAYS.between(
                                        cuota.getFechaVencimiento(),
                                        fechaConsulta);

                        estadoMostrado = EstadoCuota.VENCIDA;

                } else if (cuota.getMontoPagado()
                                .signum() > 0) {

                        estadoMostrado = EstadoCuota.PARCIAL;

                } else {

                        estadoMostrado = EstadoCuota.PENDIENTE;
                }

                String nombreCompleto = cliente.getNombres()
                                + " "
                                + cliente.getApellidoPaterno();

                if (cliente.getApellidoMaterno() != null
                                && !cliente.getApellidoMaterno()
                                                .isBlank()) {

                        nombreCompleto += " " + cliente.getApellidoMaterno();
                }

                return new CobranzaPendienteResponse(
                                cuota.getIdCuota(),
                                cuota.getNumeroCuota(),
                                cuota.getFechaVencimiento(),
                                cuota.getSaldoCuota(),
                                estadoMostrado,

                                venta.getIdVenta(),
                                venta.getNumeroContrato(),
                                venta.getSaldoPendiente(),

                                cliente.getIdCliente(),
                                cliente.getCodigoCliente(),
                                nombreCompleto,
                                cliente.getCelular(),
                                cliente.getDireccion(),
                                cliente.getZona(),

                                diasAtraso);
        }
}