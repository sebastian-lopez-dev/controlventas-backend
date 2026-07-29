package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.negocio.controlventas.dto.ResumenCobranzaResponse;
import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.model.Cuota;
import com.negocio.controlventas.model.EstadoPago;
import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.model.Pago;
import com.negocio.controlventas.repository.CobradorRepository;
import com.negocio.controlventas.repository.CuotaRepository;
import com.negocio.controlventas.repository.PagoRepository;

@Service
public class ResumenCobranzaService {

    private final CobradorRepository cobradorRepository;
    private final CuotaRepository cuotaRepository;
    private final PagoRepository pagoRepository;

    public ResumenCobranzaService(
            CobradorRepository cobradorRepository,
            CuotaRepository cuotaRepository,
            PagoRepository pagoRepository) {

        this.cobradorRepository = cobradorRepository;
        this.cuotaRepository = cuotaRepository;
        this.pagoRepository = pagoRepository;
    }

    public ResumenCobranzaResponse obtenerResumen(
            Long idCobrador,
            LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria");
        }

        Cobrador cobrador =
                cobradorRepository
                        .findById(idCobrador)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No existe el cobrador con el ID "
                                                + idCobrador));

        List<Cuota> cuotasPendientes =
                cuotaRepository.buscarCobrosHastaFecha(
                        idCobrador,
                        fecha,
                        EstadoVenta.ACTIVA);

        LocalDateTime inicio =
                fecha.atStartOfDay();

        LocalDateTime fin =
                fecha.plusDays(1)
                        .atStartOfDay();

        List<Pago> pagos =
                pagoRepository.buscarPagosDelDia(
                        idCobrador,
                        inicio,
                        fin,
                        EstadoPago.REGISTRADO);

        BigDecimal totalPendiente =
                BigDecimal.ZERO;

        for (Cuota cuota : cuotasPendientes) {

            totalPendiente =
                    totalPendiente.add(
                            cuota.getSaldoCuota());
        }

        BigDecimal totalCobrado =
                BigDecimal.ZERO;

        BigDecimal totalEfectivo =
                BigDecimal.ZERO;

        BigDecimal totalYape =
                BigDecimal.ZERO;

        BigDecimal totalPlin =
                BigDecimal.ZERO;

        BigDecimal totalTransferencia =
                BigDecimal.ZERO;

        for (Pago pago : pagos) {

            BigDecimal monto =
                    pago.getMontoTotal();

            totalCobrado =
                    totalCobrado.add(monto);

            switch (pago.getMetodoPago()) {

                case EFECTIVO ->
                        totalEfectivo =
                                totalEfectivo.add(monto);

                case YAPE ->
                        totalYape =
                                totalYape.add(monto);

                case PLIN ->
                        totalPlin =
                                totalPlin.add(monto);

                case TRANSFERENCIA ->
                        totalTransferencia =
                                totalTransferencia.add(monto);
            }
        }

        long contratosPendientes =
                cuotasPendientes.stream()
                        .map(cuota ->
                                cuota.getVenta()
                                        .getIdVenta())
                        .distinct()
                        .count();

        String nombreCobrador =
                cobrador.getNombres()
                        + " "
                        + cobrador.getApellidoPaterno();

        return new ResumenCobranzaResponse(
                fecha,

                cobrador.getIdCobrador(),
                cobrador.getCodigoCobrador(),
                nombreCobrador,

                contratosPendientes,
                cuotasPendientes.size(),
                totalPendiente,

                pagos.size(),
                totalCobrado,

                totalEfectivo,
                totalYape,
                totalPlin,
                totalTransferencia
        );
    }
}