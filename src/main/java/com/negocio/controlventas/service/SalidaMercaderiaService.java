package com.negocio.controlventas.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.negocio.controlventas.repository.DetalleSalidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import com.negocio.controlventas.dto.CierreSalidaRequest;
import com.negocio.controlventas.dto.ConteoProductoRequest;
import com.negocio.controlventas.dto.DetalleSalidaRequest;
import com.negocio.controlventas.dto.SalidaMercaderiaRequest;
import com.negocio.controlventas.model.DetalleSalida;
import com.negocio.controlventas.model.EstadoSalida;
import com.negocio.controlventas.model.Producto;
import com.negocio.controlventas.model.SalidaMercaderia;
import com.negocio.controlventas.repository.ProductoRepository;
import com.negocio.controlventas.repository.SalidaMercaderiaRepository;

@Service
public class SalidaMercaderiaService {

    private final SalidaMercaderiaRepository salidaRepository;
    private final ProductoRepository productoRepository;
    private final DetalleSalidaRepository detalleSalidaRepository;

    public SalidaMercaderiaService(
            SalidaMercaderiaRepository salidaRepository,
            ProductoRepository productoRepository,
            DetalleSalidaRepository detalleSalidaRepository) {

        this.salidaRepository = salidaRepository;
        this.productoRepository = productoRepository;
        this.detalleSalidaRepository = detalleSalidaRepository;
    }

    public List<SalidaMercaderia> listarSalidas() {
        return salidaRepository.findAll();
    }

    public SalidaMercaderia buscarSalidaPorId(
            Long idSalida) {

        return salidaRepository.findById(idSalida)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la salida con el ID "
                                + idSalida));
    }

    @Transactional
    public SalidaMercaderia registrarSalida(
            SalidaMercaderiaRequest solicitud) {

        validarDatosGenerales(solicitud);

        if (salidaRepository.existsByEstado(
        EstadoSalida.ABIERTA)) {

    throw new IllegalArgumentException(
            "Ya existe una salida abierta. " +
            "Primero debes finalizarla");
}

        SalidaMercaderia salida = new SalidaMercaderia();

        salida.setFechaSalida(
                solicitud.getFechaSalida());

        salida.setDestino(
                solicitud.getDestino().trim());

        salida.setVendedor(
                solicitud.getVendedor().trim());

        salida.setObservaciones(
                solicitud.getObservaciones());

        salida.setEstado(EstadoSalida.ABIERTA);

        Set<Long> productosAgregados = new HashSet<>();

        for (DetalleSalidaRequest datosDetalle : solicitud.getDetalles()) {

            validarDetalle(datosDetalle);

            if (!productosAgregados.add(
                    datosDetalle.getIdProducto())) {

                throw new IllegalArgumentException(
                        "Un producto no puede repetirse "
                                + "dentro de la misma salida");
            }

            Producto producto = productoRepository
                    .findById(
                            datosDetalle.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe el producto con el ID "
                                    + datosDetalle
                                            .getIdProducto()));

            if (!Boolean.TRUE.equals(
                    producto.getActivo())) {

                throw new IllegalArgumentException(
                        "El producto "
                                + producto.getNombre()
                                + " está desactivado");
            }

            int cantidadSolicitada = datosDetalle.getCantidad();

            if (producto.getStockAlmacen() < cantidadSolicitada) {

                throw new IllegalArgumentException(
                        "Stock insuficiente para "
                                + producto.getNombre()
                                + ". Disponible: "
                                + producto.getStockAlmacen()
                                + ", solicitado: "
                                + cantidadSolicitada);
            }

            producto.setStockAlmacen(
                    producto.getStockAlmacen()
                            - cantidadSolicitada);

            productoRepository.save(producto);

            DetalleSalida detalle = new DetalleSalida();

            detalle.setSalida(salida);
            detalle.setProducto(producto);
            detalle.setCantidadCargada(
                    cantidadSolicitada);
            detalle.setCantidadVendida(0);

            salida.getDetalles().add(detalle);
        }

        return salidaRepository.save(salida);
    }

    private void validarDatosGenerales(
            SalidaMercaderiaRequest solicitud) {

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "Los datos de la salida son obligatorios");
        }

        if (solicitud.getFechaSalida() == null) {
            throw new IllegalArgumentException(
                    "La fecha de salida es obligatoria");
        }

        if (solicitud.getDestino() == null
                || solicitud.getDestino().isBlank()) {

            throw new IllegalArgumentException(
                    "El destino es obligatorio");
        }

        if (solicitud.getVendedor() == null
                || solicitud.getVendedor().isBlank()) {

            throw new IllegalArgumentException(
                    "El vendedor es obligatorio");
        }

        if (solicitud.getDetalles() == null
                || solicitud.getDetalles().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe agregar al menos un producto");
        }
    }

    private void validarDetalle(
            DetalleSalidaRequest detalle) {

        if (detalle.getIdProducto() == null) {
            throw new IllegalArgumentException(
                    "El ID del producto es obligatorio");
        }

        if (detalle.getCantidad() == null
                || detalle.getCantidad() <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad cargada debe ser mayor que cero");
        }
    }

    @Transactional
    public SalidaMercaderia agregarProducto(
            Long idSalida,
            DetalleSalidaRequest solicitud) {

        SalidaMercaderia salida = buscarSalidaPorId(idSalida);

        if (salida.getEstado() != EstadoSalida.ABIERTA) {
            throw new IllegalArgumentException(
                    "Solamente se pueden agregar productos "
                            + "a una salida abierta");
        }

        validarDetalle(solicitud);

        Producto producto = productoRepository
                .findById(solicitud.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el producto con el ID "
                                + solicitud
                                        .getIdProducto()));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException(
                    "El producto "
                            + producto.getNombre()
                            + " está desactivado");
        }

        int cantidadNueva = solicitud.getCantidad();

        if (producto.getStockAlmacen() < cantidadNueva) {

            throw new IllegalArgumentException(
                    "Stock insuficiente para "
                            + producto.getNombre()
                            + ". Disponible: "
                            + producto.getStockAlmacen()
                            + ", solicitado: "
                            + cantidadNueva);
        }

        DetalleSalida detalleExistente = detalleSalidaRepository
                .findBySalida_IdSalidaAndProducto_IdProducto(
                        idSalida,
                        producto.getIdProducto())
                .orElse(null);

        if (detalleExistente == null) {

            DetalleSalida nuevoDetalle = new DetalleSalida();

            nuevoDetalle.setSalida(salida);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidadCargada(
                    cantidadNueva);
            nuevoDetalle.setCantidadVendida(0);

            salida.getDetalles().add(nuevoDetalle);

        } else {

            detalleExistente.setCantidadCargada(
                    detalleExistente.getCantidadCargada()
                            + cantidadNueva);
        }

        producto.setStockAlmacen(
                producto.getStockAlmacen()
                        - cantidadNueva);

        productoRepository.save(producto);

        return salidaRepository.save(salida);
    }

    @Transactional
    public SalidaMercaderia corregirCantidadCargada(
            Long idSalida,
            Long idProducto,
            Integer nuevaCantidad) {

        SalidaMercaderia salida = buscarSalidaPorId(idSalida);

        if (salida.getEstado() != EstadoSalida.ABIERTA) {
            throw new IllegalArgumentException(
                    "Solamente se puede modificar "
                            + "una salida abierta");
        }

        if (nuevaCantidad == null
                || nuevaCantidad <= 0) {

            throw new IllegalArgumentException(
                    "La nueva cantidad debe ser mayor que cero");
        }

        DetalleSalida detalle = detalleSalidaRepository
                .findBySalida_IdSalidaAndProducto_IdProducto(
                        idSalida,
                        idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto no está cargado "
                                + "en esta salida"));

        if (nuevaCantidad < detalle.getCantidadVendida()) {

            throw new IllegalArgumentException(
                    "La cantidad cargada no puede ser menor "
                            + "que la cantidad ya vendida");
        }

        Producto producto = detalle.getProducto();

        int cantidadAnterior = detalle.getCantidadCargada();

        int diferencia = nuevaCantidad - cantidadAnterior;

        if (diferencia > 0) {

            if (!Boolean.TRUE.equals(
                    producto.getActivo())) {

                throw new IllegalArgumentException(
                        "El producto está desactivado");
            }

            if (producto.getStockAlmacen() < diferencia) {

                throw new IllegalArgumentException(
                        "Stock insuficiente. Disponible: "
                                + producto.getStockAlmacen()
                                + ", cantidad adicional: "
                                + diferencia);
            }

            producto.setStockAlmacen(
                    producto.getStockAlmacen()
                            - diferencia);

        } else if (diferencia < 0) {

            int cantidadDevuelta = -diferencia;

            producto.setStockAlmacen(
                    producto.getStockAlmacen()
                            + cantidadDevuelta);
        }

        detalle.setCantidadCargada(
                nuevaCantidad);

        productoRepository.save(producto);
        detalleSalidaRepository.save(detalle);

        return salida;
    }

    @Transactional
    public SalidaMercaderia retirarProducto(
            Long idSalida,
            Long idProducto) {

        SalidaMercaderia salida = buscarSalidaPorId(idSalida);

        if (salida.getEstado() != EstadoSalida.ABIERTA) {
            throw new IllegalArgumentException(
                    "Solamente se puede modificar "
                            + "una salida abierta");
        }

        DetalleSalida detalle = detalleSalidaRepository
                .findBySalida_IdSalidaAndProducto_IdProducto(
                        idSalida,
                        idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto no está cargado "
                                + "en esta salida"));

        if (detalle.getCantidadVendida() > 0) {
            throw new IllegalArgumentException(
                    "No se puede retirar el producto porque "
                            + "ya tiene ventas registradas");
        }

        Producto producto = detalle.getProducto();

        producto.setStockAlmacen(
                producto.getStockAlmacen()
                        + detalle.getCantidadCargada());

        productoRepository.save(producto);

        salida.getDetalles().remove(detalle);

        return salidaRepository.save(salida);
    }

   @Transactional
public SalidaMercaderia cerrarSalida(
        Long idSalida,
        CierreSalidaRequest solicitud) {

    SalidaMercaderia salida =
            buscarSalidaPorId(idSalida);

    if (salida.getEstado()
            != EstadoSalida.ABIERTA) {

        throw new IllegalArgumentException(
                "La salida ya está cerrada o cancelada");
    }

    List<DetalleSalida> detalles =
            detalleSalidaRepository
                    .findBySalida_IdSalida(idSalida);

    List<ConteoProductoRequest> conteos =
            solicitud == null
                    || solicitud.getConteos() == null
                    ? List.of()
                    : solicitud.getConteos();

    /*
     * Permite cerrar una salida vacía.
     * Esto sirve si fue creada accidentalmente
     * o si retiraron toda la mercadería.
     */
    if (detalles.isEmpty()) {

        if (!conteos.isEmpty()) {
            throw new IllegalArgumentException(
                    "La salida no tiene productos para contar");
        }

        salida.setEstado(EstadoSalida.CERRADA);
        salida.setFechaCierre(LocalDateTime.now());

        return salidaRepository.save(salida);
    }

    if (conteos.isEmpty()) {
        throw new IllegalArgumentException(
                "Debe ingresar el conteo de los productos");
    }

    if (conteos.size() != detalles.size()) {
        throw new IllegalArgumentException(
                "Debe contar todos los productos " +
                "cargados en la salida");
    }

    Set<Long> productosContados =
            new HashSet<>();

    for (ConteoProductoRequest conteo : conteos) {

        if (conteo.getIdProducto() == null) {
            throw new IllegalArgumentException(
                    "El producto del conteo es obligatorio");
        }

        if (conteo.getCantidadContada() == null
                || conteo.getCantidadContada() < 0) {

            throw new IllegalArgumentException(
                    "La cantidad contada no puede ser negativa");
        }

        if (!productosContados.add(
                conteo.getIdProducto())) {

            throw new IllegalArgumentException(
                    "Un producto no puede contarse dos veces");
        }

        DetalleSalida detalle =
                detalleSalidaRepository
                        .findBySalida_IdSalidaAndProducto_IdProducto(
                                idSalida,
                                conteo.getIdProducto())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El producto con ID "
                                                + conteo.getIdProducto()
                                                + " no pertenece a esta salida"));

        int stockEsperado =
                detalle.getStockEsperado();

        int diferencia =
                conteo.getCantidadContada()
                        - stockEsperado;

        if (diferencia != 0
                && (conteo.getObservaciones() == null
                || conteo.getObservaciones().isBlank())) {

            throw new IllegalArgumentException(
                    "Debe registrar una observación para "
                            + detalle.getProducto().getNombre());
        }

        detalle.setCantidadContada(
                conteo.getCantidadContada());

        detalle.setDiferencia(diferencia);

        detalle.setObservaciones(
                conteo.getObservaciones());

        /*
         * Solamente la cantidad que tu papá
         * contó regresa al almacén.
         */
        Producto producto =
                detalle.getProducto();

        producto.setStockAlmacen(
                producto.getStockAlmacen()
                        + conteo.getCantidadContada());

        productoRepository.save(producto);
        detalleSalidaRepository.save(detalle);
    }

    salida.setEstado(EstadoSalida.CERRADA);
    salida.setFechaCierre(LocalDateTime.now());

    return salidaRepository.save(salida);
}

    public List<DetalleSalida> listarDiferencias(
            Long idSalida) {

        buscarSalidaPorId(idSalida);

        return detalleSalidaRepository
                .findBySalida_IdSalidaAndDiferenciaNot(
                        idSalida,
                        0);
    }

    public List<DetalleSalida> listarTodasLasDiferencias() {

        return detalleSalidaRepository
                .findByDiferenciaNot(0);
    }
}