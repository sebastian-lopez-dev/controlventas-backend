package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.negocio.controlventas.dto.DashboardResponse;
import com.negocio.controlventas.model.EstadoPago;
import com.negocio.controlventas.model.EstadoSalida;
import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.repository.ClienteRepository;
import com.negocio.controlventas.repository.DetalleSalidaRepository;
import com.negocio.controlventas.repository.PagoRepository;
import com.negocio.controlventas.repository.ProductoRepository;
import com.negocio.controlventas.repository.SalidaMercaderiaRepository;
import com.negocio.controlventas.repository.VentaCreditoRepository;

@Service
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final SalidaMercaderiaRepository salidaRepository;
    private final DetalleSalidaRepository detalleSalidaRepository;
    private final VentaCreditoRepository ventaRepository;
    private final PagoRepository pagoRepository;

    public DashboardService(
            ProductoRepository productoRepository,
            ClienteRepository clienteRepository,
            SalidaMercaderiaRepository salidaRepository,
            DetalleSalidaRepository detalleSalidaRepository,
            VentaCreditoRepository ventaRepository,
            PagoRepository pagoRepository) {

        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.salidaRepository = salidaRepository;
        this.detalleSalidaRepository =
                detalleSalidaRepository;
        this.ventaRepository = ventaRepository;
        this.pagoRepository = pagoRepository;
    }

    public DashboardResponse obtenerResumen() {

        LocalDateTime ahora =
                LocalDateTime.now();

        LocalDate hoy =
                LocalDate.now();

        LocalDateTime inicioHoy =
                hoy.atStartOfDay();

        LocalDateTime finHoy =
                hoy.plusDays(1)
                        .atStartOfDay();

        long productosActivos =
                productoRepository
                        .countByActivoTrue();

        long stockAlmacen =
                productoRepository
                        .sumarStockAlmacen();

        long stockEnCarro =
                detalleSalidaRepository
                        .sumarStockEnCarro(
                                EstadoSalida.ABIERTA);

        int productosConStockBajo =
                productoRepository
                        .buscarProductosConStockBajo()
                        .size();

        long clientesActivos =
                clienteRepository
                        .countByActivoTrue();

        long salidasAbiertas =
                salidaRepository
                        .countByEstado(
                                EstadoSalida.ABIERTA);

        long ventasActivas =
                ventaRepository
                        .countByEstado(
                                EstadoVenta.ACTIVA);

        BigDecimal deudaPendiente =
                ventaRepository
                        .sumarSaldoPorEstado(
                                EstadoVenta.ACTIVA);

        BigDecimal totalVendido =
                ventaRepository
                        .sumarTotalVendido(
                                EstadoVenta.ANULADA);

        BigDecimal totalCobrado =
                pagoRepository
                        .sumarTotalCobrado(
                                EstadoPago.REGISTRADO);

        BigDecimal cobradoHoy =
                pagoRepository
                        .sumarCobradoEntreFechas(
                                inicioHoy,
                                finHoy,
                                EstadoPago.REGISTRADO);

        return new DashboardResponse(
                ahora,

                productosActivos,
                stockAlmacen,
                stockEnCarro,
                productosConStockBajo,

                clientesActivos,
                salidasAbiertas,

                ventasActivas,
                deudaPendiente,
                totalVendido,

                totalCobrado,
                cobradoHoy
        );
    }
}