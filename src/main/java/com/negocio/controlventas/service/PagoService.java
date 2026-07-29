package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.negocio.controlventas.dto.PagoRequest;
import com.negocio.controlventas.model.AplicacionPago;
import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.model.Cuota;
import com.negocio.controlventas.model.EstadoCuota;
import com.negocio.controlventas.model.EstadoPago;
import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.model.MetodoPago;
import com.negocio.controlventas.model.Pago;
import com.negocio.controlventas.model.VentaCredito;
import com.negocio.controlventas.repository.CobradorRepository;
import com.negocio.controlventas.repository.CuotaRepository;
import com.negocio.controlventas.repository.PagoRepository;
import com.negocio.controlventas.repository.VentaCreditoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaCreditoRepository ventaRepository;
    private final CobradorRepository cobradorRepository;
    private final CuotaRepository cuotaRepository;

    public PagoService(
            PagoRepository pagoRepository,
            VentaCreditoRepository ventaRepository,
            CobradorRepository cobradorRepository,
            CuotaRepository cuotaRepository) {

        this.pagoRepository = pagoRepository;
        this.ventaRepository = ventaRepository;
        this.cobradorRepository = cobradorRepository;
        this.cuotaRepository = cuotaRepository;
    }

    @Transactional
    public Pago registrarPago(PagoRequest solicitud) {

        validarSolicitud(solicitud);

        VentaCredito venta = buscarVenta(solicitud.getIdVenta());

        validarVentaActiva(venta);

        Cobrador cobrador = buscarCobrador(
                solicitud.getIdCobrador());

        validarCobradorAsignado(
                venta,
                cobrador);

        if (solicitud.getMonto()
                .compareTo(
                        venta.getSaldoPendiente()) > 0) {

            throw new IllegalArgumentException(
                    "El pago no puede ser mayor "
                            + "que el saldo pendiente de S/"
                            + venta.getSaldoPendiente());
        }

        List<Cuota> cuotas = cuotaRepository
                .findByVenta_IdVentaOrderByNumeroCuotaAsc(
                        venta.getIdVenta());

        if (cuotas.isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta todavía no tiene cuotas");
        }

        BigDecimal saldoAnterior = venta.getSaldoPendiente();

        BigDecimal saldoNuevo = saldoAnterior.subtract(
                solicitud.getMonto());

        Pago pago = crearPago(
                solicitud,
                venta,
                cobrador,
                saldoAnterior,
                saldoNuevo);

        Pago pagoGuardado = pagoRepository.save(pago);

        String codigoPago = String.format(
                "PAG-%06d",
                pagoGuardado.getIdPago());

        pagoGuardado.setCodigoPago(codigoPago);

        aplicarPagoEnCuotas(
                pagoGuardado,
                cuotas,
                solicitud.getMonto());

        venta.setSaldoPendiente(saldoNuevo);

        if (saldoNuevo
                .compareTo(BigDecimal.ZERO) == 0) {

            venta.setEstado(EstadoVenta.PAGADA);
        }

        ventaRepository.save(venta);

        return pagoRepository.save(pagoGuardado);
    }

    private VentaCredito buscarVenta(
            Long idVenta) {

        return ventaRepository
                .findById(idVenta)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la venta con el ID "
                                + idVenta));
    }

    private Cobrador buscarCobrador(
            Long idCobrador) {

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

        return cobrador;
    }

    private void validarVentaActiva(
            VentaCredito venta) {

        if (venta.getEstado() != EstadoVenta.ACTIVA) {
            throw new IllegalArgumentException(
                    "La venta no está activa");
        }
    }

    private void validarCobradorAsignado(
            VentaCredito venta,
            Cobrador cobrador) {

        if (venta.getCobrador() == null) {
            throw new IllegalArgumentException(
                    "El contrato no tiene un cobrador asignado");
        }

        if (!venta.getCobrador()
                .getIdCobrador()
                .equals(cobrador.getIdCobrador())) {

            throw new IllegalArgumentException(
                    "El contrato no está asignado "
                            + "al cobrador seleccionado");
        }
    }

    private Pago crearPago(
            PagoRequest solicitud,
            VentaCredito venta,
            Cobrador cobrador,
            BigDecimal saldoAnterior,
            BigDecimal saldoNuevo) {

        Pago pago = new Pago();

        pago.setVenta(venta);
        pago.setCobrador(cobrador);
        pago.setMontoTotal(
                solicitud.getMonto());
        pago.setMetodoPago(
                solicitud.getMetodoPago());
        pago.setSaldoAnterior(saldoAnterior);
        pago.setSaldoNuevo(saldoNuevo);
        pago.setNumeroOperacion(
                solicitud.getNumeroOperacion());
        pago.setObservaciones(
                solicitud.getObservaciones());
        pago.setEstado(EstadoPago.REGISTRADO);

        if (solicitud.getFechaPago() == null) {
            pago.setFechaPago(
                    LocalDateTime.now());
        } else {
            pago.setFechaPago(
                    solicitud.getFechaPago());
        }

        return pago;
    }

    private void aplicarPagoEnCuotas(
            Pago pago,
            List<Cuota> cuotas,
            BigDecimal montoRecibido) {

        BigDecimal montoPendiente = montoRecibido;

        for (Cuota cuota : cuotas) {

            if (montoPendiente
                    .compareTo(BigDecimal.ZERO) <= 0) {

                break;
            }

            if (cuota.getSaldoCuota()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                continue;
            }

            BigDecimal montoAplicado = montoPendiente.min(
                    cuota.getSaldoCuota());

            cuota.setMontoPagado(
                    cuota.getMontoPagado()
                            .add(montoAplicado));

            cuota.setSaldoCuota(
                    cuota.getSaldoCuota()
                            .subtract(montoAplicado));

            if (cuota.getSaldoCuota()
                    .compareTo(BigDecimal.ZERO) == 0) {

                cuota.setEstado(
                        EstadoCuota.PAGADA);

            } else {

                cuota.setEstado(
                        EstadoCuota.PARCIAL);
            }

            cuotaRepository.save(cuota);

            AplicacionPago aplicacion = new AplicacionPago();

            aplicacion.setPago(pago);
            aplicacion.setCuota(cuota);
            aplicacion.setMontoAplicado(
                    montoAplicado);

            pago.getAplicaciones().add(
                    aplicacion);

            montoPendiente = montoPendiente.subtract(
                    montoAplicado);
        }

        if (montoPendiente
                .compareTo(BigDecimal.ZERO) != 0) {

            throw new IllegalArgumentException(
                    "No se pudo aplicar completamente el pago");
        }
    }

    private void validarSolicitud(
            PagoRequest solicitud) {

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "Los datos del pago son obligatorios");
        }

        if (solicitud.getIdVenta() == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria");
        }

        if (solicitud.getIdCobrador() == null) {
            throw new IllegalArgumentException(
                    "El cobrador es obligatorio");
        }

        if (solicitud.getMonto() == null
                || solicitud.getMonto()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero");
        }

        if (solicitud.getMetodoPago() == null) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio");
        }

        if (solicitud.getMetodoPago() != MetodoPago.EFECTIVO
                && (solicitud.getNumeroOperacion() == null
                        || solicitud.getNumeroOperacion()
                                .isBlank())) {

            throw new IllegalArgumentException(
                    "Debe ingresar el número de operación");
        }
    }

    public List<Pago> listarPagos() {

        return pagoRepository.findAll();
    }

    public Pago buscarPagoPorId(Long idPago) {

        return pagoRepository
                .findById(idPago)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el pago con el ID "
                                + idPago));
    }

    public List<Pago> listarPagosDeVenta(
            Long idVenta) {

        buscarVenta(idVenta);

        return pagoRepository
                .findByVenta_IdVentaOrderByFechaPagoDesc(
                        idVenta);
    }

    public List<Pago> listarPagosDelCobrador(
            Long idCobrador) {

        buscarCobrador(idCobrador);

        return pagoRepository
                .findByCobrador_IdCobradorOrderByFechaPagoDesc(
                        idCobrador);
    }
}
