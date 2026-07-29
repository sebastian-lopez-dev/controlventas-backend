package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.util.ArrayList;
import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.repository.CobradorRepository;
import com.negocio.controlventas.model.Cuota;
import com.negocio.controlventas.model.EstadoCuota;
import com.negocio.controlventas.repository.CuotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.negocio.controlventas.dto.DetalleVentaRequest;
import com.negocio.controlventas.dto.VentaCreditoRequest;
import com.negocio.controlventas.model.Cliente;
import com.negocio.controlventas.model.DetalleSalida;
import com.negocio.controlventas.model.DetalleVenta;
import com.negocio.controlventas.model.EstadoSalida;
import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.model.ModalidadPago;
import com.negocio.controlventas.model.Producto;
import com.negocio.controlventas.model.SalidaMercaderia;
import com.negocio.controlventas.model.VentaCredito;
import com.negocio.controlventas.repository.ClienteRepository;
import com.negocio.controlventas.repository.DetalleSalidaRepository;
import com.negocio.controlventas.repository.SalidaMercaderiaRepository;
import com.negocio.controlventas.repository.VentaCreditoRepository;

@Service
public class VentaCreditoService {

        private final VentaCreditoRepository ventaRepository;
        private final ClienteRepository clienteRepository;
        private final SalidaMercaderiaRepository salidaRepository;
        private final DetalleSalidaRepository detalleSalidaRepository;
        private final CuotaRepository cuotaRepository;
        private final CobradorRepository cobradorRepository;

        public VentaCreditoService(
                        VentaCreditoRepository ventaRepository,
                        ClienteRepository clienteRepository,
                        SalidaMercaderiaRepository salidaRepository,
                        DetalleSalidaRepository detalleSalidaRepository,
                        CuotaRepository cuotaRepository,
                        CobradorRepository cobradorRepository) {

                this.ventaRepository = ventaRepository;
                this.clienteRepository = clienteRepository;
                this.salidaRepository = salidaRepository;
                this.detalleSalidaRepository = detalleSalidaRepository;
                this.cuotaRepository = cuotaRepository;
                this.cobradorRepository = cobradorRepository;
        }

        public List<VentaCredito> listarVentas() {
                return ventaRepository.findAll();
        }

        public VentaCredito buscarVentaPorId(Long idVenta) {

                return ventaRepository.findById(idVenta)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No existe la venta con el ID "
                                                                + idVenta));
        }

        @Transactional
        public VentaCredito registrarVenta(
                        VentaCreditoRequest solicitud) {

                validarSolicitud(solicitud);

                Cliente cliente = clienteRepository
                                .findById(solicitud.getIdCliente())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No existe el cliente con el ID "
                                                                + solicitud
                                                                                .getIdCliente()));

                if (!Boolean.TRUE.equals(cliente.getActivo())) {
                        throw new IllegalArgumentException(
                                        "El cliente está desactivado");
                }

                SalidaMercaderia salida = salidaRepository
                                .findById(solicitud.getIdSalida())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No existe la salida con el ID "
                                                                + solicitud
                                                                                .getIdSalida()));

                if (salida.getEstado() != EstadoSalida.ABIERTA) {
                        throw new IllegalArgumentException(
                                        "Solo se pueden registrar ventas "
                                                        + "en una salida abierta");
                }

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

                VentaCredito venta = new VentaCredito();

                venta.setCliente(cliente);
                venta.setSalida(salida);
                venta.setCobrador(cobrador);
                venta.setFechaVenta(solicitud.getFechaVenta());
                venta.setModalidadPago(
                                solicitud.getModalidadPago());
                venta.setMontoCuota(
                                solicitud.getMontoCuota());
                venta.setFechaPrimerPago(
                                solicitud.getFechaPrimerPago());
                venta.setObservaciones(
                                solicitud.getObservaciones());
                venta.setFirmaCliente(
                                solicitud.getFirmaCliente());
                venta.setEstado(EstadoVenta.ACTIVA);

                if (solicitud.getDiaPago() != null) {
                        venta.setDiaPago(
                                        solicitud.getDiaPago()
                                                        .trim()
                                                        .toUpperCase());
                }

                Set<Long> productosVendidos = new HashSet<>();

                BigDecimal totalVenta = BigDecimal.ZERO;

                for (DetalleVentaRequest datosDetalle : solicitud.getDetalles()) {

                        validarDetalle(datosDetalle);

                        if (!productosVendidos.add(
                                        datosDetalle.getIdProducto())) {

                                throw new IllegalArgumentException(
                                                "Un producto no puede repetirse "
                                                                + "en la misma venta");
                        }

                        DetalleSalida detalleSalida = detalleSalidaRepository
                                        .findBySalida_IdSalidaAndProducto_IdProducto(
                                                        solicitud.getIdSalida(),
                                                        datosDetalle.getIdProducto())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "El producto con ID "
                                                                        + datosDetalle
                                                                                        .getIdProducto()
                                                                        + " no está cargado "
                                                                        + "en el carro"));

                        Producto producto = detalleSalida.getProducto();

                        if (!Boolean.TRUE.equals(
                                        producto.getActivo())) {

                                throw new IllegalArgumentException(
                                                "El producto "
                                                                + producto.getNombre()
                                                                + " está desactivado");
                        }

                        int disponibleEnCarro = detalleSalida.getStockEsperado();

                        if (datosDetalle.getCantidad() > disponibleEnCarro) {

                                throw new IllegalArgumentException(
                                                "Stock insuficiente en el carro para "
                                                                + producto.getNombre()
                                                                + ". Disponible: "
                                                                + disponibleEnCarro
                                                                + ", solicitado: "
                                                                + datosDetalle.getCantidad());
                        }

                        BigDecimal precio = datosDetalle.getPrecioUnitario();

                        if (precio == null) {
                                precio = producto.getPrecioVenta();
                        }

                        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                                throw new IllegalArgumentException(
                                                "El precio debe ser mayor que cero");
                        }

                        BigDecimal subtotal = precio.multiply(
                                        BigDecimal.valueOf(
                                                        datosDetalle.getCantidad()));

                        DetalleVenta detalleVenta = new DetalleVenta();

                        detalleVenta.setVenta(venta);
                        detalleVenta.setProducto(producto);
                        detalleVenta.setCantidad(
                                        datosDetalle.getCantidad());
                        detalleVenta.setPrecioUnitario(precio);
                        detalleVenta.setSubtotal(subtotal);
                        detalleVenta.setNombreProducto(
                                        producto.getNombre());
                        detalleVenta.setModelo(
                                        producto.getModelo());
                        detalleVenta.setColor(
                                        producto.getColor());
                        detalleVenta.setTalla(
                                        producto.getTalla());

                        venta.getDetalles().add(detalleVenta);

                        detalleSalida.setCantidadVendida(
                                        detalleSalida.getCantidadVendida()
                                                        + datosDetalle.getCantidad());

                        detalleSalidaRepository.save(detalleSalida);

                        totalVenta = totalVenta.add(subtotal);
                }

                BigDecimal cuotaInicial = solicitud.getCuotaInicial() == null
                                ? BigDecimal.ZERO
                                : solicitud.getCuotaInicial();

                if (cuotaInicial.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException(
                                        "La cuota inicial no puede ser negativa");
                }

                if (cuotaInicial.compareTo(totalVenta) >= 0) {
                        throw new IllegalArgumentException(
                                        "En una venta al crédito, la cuota inicial "
                                                        + "debe ser menor que el total");
                }

                BigDecimal saldoPendiente = totalVenta.subtract(cuotaInicial);

                if (solicitud.getMontoCuota()
                                .compareTo(saldoPendiente) > 0) {

                        throw new IllegalArgumentException(
                                        "El monto de la cuota no puede ser "
                                                        + "mayor que el saldo pendiente");
                }

                venta.setTotalVenta(totalVenta);
                venta.setCuotaInicial(cuotaInicial);
                venta.setSaldoPendiente(saldoPendiente);

                VentaCredito ventaGuardada = ventaRepository.save(venta);

                String numeroContrato = String.format(
                                "CTR-%06d",
                                ventaGuardada.getIdVenta());

                ventaGuardada.setNumeroContrato(
                                numeroContrato);

                VentaCredito ventaFinal = ventaRepository.save(ventaGuardada);

                generarCuotasInternas(ventaFinal);

                return ventaFinal;
        }

        private void validarSolicitud(
                        VentaCreditoRequest solicitud) {

                if (solicitud == null) {
                        throw new IllegalArgumentException(
                                        "Los datos de la venta son obligatorios");
                }

                if (solicitud.getIdCliente() == null) {
                        throw new IllegalArgumentException(
                                        "El cliente es obligatorio");
                }

                if (solicitud.getIdSalida() == null) {
                        throw new IllegalArgumentException(
                                        "La salida es obligatoria");
                }

                if (solicitud.getIdCobrador() == null) {
                        throw new IllegalArgumentException(
                                        "El cobrador es obligatorio");
                }

                if (solicitud.getFechaVenta() == null) {
                        throw new IllegalArgumentException(
                                        "La fecha de venta es obligatoria");
                }

                if (solicitud.getModalidadPago() == null) {
                        throw new IllegalArgumentException(
                                        "La modalidad de pago es obligatoria");
                }

                if (solicitud.getMontoCuota() == null
                                || solicitud.getMontoCuota()
                                                .compareTo(BigDecimal.ZERO) <= 0) {

                        throw new IllegalArgumentException(
                                        "El monto de la cuota debe ser mayor que cero");
                }

                if (solicitud.getFechaPrimerPago() == null) {
                        throw new IllegalArgumentException(
                                        "La fecha del primer pago es obligatoria");
                }

                if (solicitud.getFechaPrimerPago()
                                .isBefore(solicitud.getFechaVenta())) {

                        throw new IllegalArgumentException(
                                        "El primer pago no puede ser anterior "
                                                        + "a la fecha de venta");
                }

                if (solicitud.getModalidadPago() == ModalidadPago.SEMANAL
                                && (solicitud.getDiaPago() == null
                                                || solicitud.getDiaPago().isBlank())) {

                        throw new IllegalArgumentException(
                                        "Debe indicar el día de pago semanal");
                }

                if (solicitud.getFirmaCliente() == null
                                || solicitud.getFirmaCliente().isBlank()) {

                        throw new IllegalArgumentException(
                                        "La firma del cliente es obligatoria");
                }

                if (!solicitud.getFirmaCliente()
                                .startsWith("data:image/png;base64,")) {

                        throw new IllegalArgumentException(
                                        "El formato de la firma no es válido");
                }

                if (solicitud.getFirmaCliente().length() > 1_500_000) {

                        throw new IllegalArgumentException(
                                        "La firma es demasiado grande");
                }

                if (solicitud.getDetalles() == null
                                || solicitud.getDetalles().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Debe agregar al menos un producto");
                }
        }

        private void validarDetalle(
                        DetalleVentaRequest detalle) {

                if (detalle.getIdProducto() == null) {
                        throw new IllegalArgumentException(
                                        "El producto es obligatorio");
                }

                if (detalle.getCantidad() == null
                                || detalle.getCantidad() <= 0) {

                        throw new IllegalArgumentException(
                                        "La cantidad vendida debe ser mayor que cero");
                }
        }

        public List<Cuota> listarCuotas(Long idVenta) {

                buscarVentaPorId(idVenta);

                return cuotaRepository
                                .findByVenta_IdVentaOrderByNumeroCuotaAsc(
                                                idVenta);
        }

        @Transactional
        public List<Cuota> generarCuotas(
                        Long idVenta) {

                VentaCredito venta = buscarVentaPorId(idVenta);

                if (cuotaRepository
                                .existsByVenta_IdVenta(idVenta)) {

                        throw new IllegalArgumentException(
                                        "Las cuotas de esta venta ya fueron generadas");
                }

                return generarCuotasInternas(venta);
        }

        private List<Cuota> generarCuotasInternas(
                        VentaCredito venta) {

                List<Cuota> cuotas = new ArrayList<>();

                BigDecimal saldoRestante = venta.getSaldoPendiente();

                LocalDate fechaCuota = venta.getFechaPrimerPago();

                int numeroCuota = 1;

                while (saldoRestante
                                .compareTo(BigDecimal.ZERO) > 0) {

                        BigDecimal montoDeLaCuota = venta.getMontoCuota()
                                        .min(saldoRestante);

                        Cuota cuota = new Cuota();

                        cuota.setVenta(venta);
                        cuota.setNumeroCuota(numeroCuota);
                        cuota.setFechaVencimiento(fechaCuota);
                        cuota.setMontoProgramado(
                                        montoDeLaCuota);
                        cuota.setMontoPagado(
                                        BigDecimal.ZERO);
                        cuota.setSaldoCuota(
                                        montoDeLaCuota);
                        cuota.setEstado(
                                        EstadoCuota.PENDIENTE);

                        cuotas.add(cuota);

                        saldoRestante = saldoRestante.subtract(
                                        montoDeLaCuota);

                        fechaCuota = calcularSiguienteFecha(
                                        fechaCuota,
                                        venta.getModalidadPago());

                        numeroCuota++;
                }

                return cuotaRepository.saveAll(cuotas);
        }

        private LocalDate calcularSiguienteFecha(
                        LocalDate fechaActual,
                        ModalidadPago modalidadPago) {

                return switch (modalidadPago) {

                        case DIARIO ->
                                fechaActual.plusDays(1);

                        case SEMANAL ->
                                fechaActual.plusWeeks(1);

                        case QUINCENAL ->
                                fechaActual.plusDays(15);

                        case MENSUAL ->
                                fechaActual.plusMonths(1);
                };
        }

        @Transactional
        public VentaCredito asignarCobrador(
                        Long idVenta,
                        Long idCobrador) {

                VentaCredito venta = buscarVentaPorId(idVenta);

                if (venta.getEstado() != EstadoVenta.ACTIVA) {
                        throw new IllegalArgumentException(
                                        "Solo se puede asignar un cobrador "
                                                        + "a una venta activa");
                }

                if (idCobrador == null) {
                        throw new IllegalArgumentException(
                                        "El cobrador es obligatorio");
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

                venta.setCobrador(cobrador);

                return ventaRepository.save(venta);
        }

        public List<VentaCredito> listarVentasDelCobrador(
                        Long idCobrador) {

                cobradorRepository
                                .findById(idCobrador)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No existe el cobrador con el ID "
                                                                + idCobrador));

                return ventaRepository
                                .findByCobrador_IdCobradorAndEstadoOrderByFechaVentaDesc(
                                                idCobrador,
                                                EstadoVenta.ACTIVA);
        }
}