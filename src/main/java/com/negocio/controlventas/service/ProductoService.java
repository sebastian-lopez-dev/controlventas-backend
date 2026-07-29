package com.negocio.controlventas.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.negocio.controlventas.model.Producto;
import com.negocio.controlventas.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto guardarProducto(Producto producto) {

        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new IllegalArgumentException(
                    "El código del producto es obligatorio");
        }

        String codigoLimpio = producto.getCodigo().trim().toUpperCase();

        if (productoRepository.existsByCodigo(codigoLimpio)) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con el código " + codigoLimpio);
        }

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio");
        }

        if (producto.getPrecioVenta() == null
                || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El precio no puede ser negativo");
        }

        if (producto.getStockAlmacen() == null
                || producto.getStockAlmacen() < 0) {

            throw new IllegalArgumentException(
                    "El stock del almacén no puede ser negativo");
        }

        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(0);
        }

        if (producto.getStockMinimo() < 0) {
            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo");
        }

        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }

        producto.setCodigo(codigoLimpio);
        producto.setNombre(producto.getNombre().trim());

        return productoRepository.save(producto);
    }

    public Producto buscarProductoPorId(Long idProducto) {

        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un producto con el ID " + idProducto));
    }

    public Producto actualizarProducto(
            Long idProducto,
            Producto datosNuevos) {

        Producto productoActual = buscarProductoPorId(idProducto);

        if (datosNuevos.getCodigo() == null
                || datosNuevos.getCodigo().isBlank()) {

            throw new IllegalArgumentException(
                    "El código es obligatorio");
        }

        String codigoLimpio = datosNuevos.getCodigo().trim().toUpperCase();

        productoRepository.findByCodigo(codigoLimpio)
                .ifPresent(productoEncontrado -> {

                    if (!productoEncontrado.getIdProducto()
                            .equals(idProducto)) {

                        throw new IllegalArgumentException(
                                "Ya existe otro producto con el código "
                                        + codigoLimpio);
                    }
                });

        if (datosNuevos.getNombre() == null
                || datosNuevos.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (datosNuevos.getPrecioVenta() == null
                || datosNuevos.getPrecioVenta()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El precio no puede ser negativo");
        }

        if (datosNuevos.getStockAlmacen() == null
                || datosNuevos.getStockAlmacen() < 0) {

            throw new IllegalArgumentException(
                    "El stock no puede ser negativo");
        }

        productoActual.setCodigo(codigoLimpio);
        productoActual.setNombre(
                datosNuevos.getNombre().trim());

        productoActual.setCategoria(
                datosNuevos.getCategoria());

        productoActual.setMarca(
                datosNuevos.getMarca());

        productoActual.setModelo(
                datosNuevos.getModelo());

        productoActual.setColor(
                datosNuevos.getColor());

        productoActual.setTalla(
                datosNuevos.getTalla());

        productoActual.setPrecioVenta(
                datosNuevos.getPrecioVenta());

        productoActual.setStockAlmacen(
                datosNuevos.getStockAlmacen());

        if (datosNuevos.getStockMinimo() != null) {
            productoActual.setStockMinimo(
                    datosNuevos.getStockMinimo());
        }

        if (datosNuevos.getActivo() != null) {
            productoActual.setActivo(
                    datosNuevos.getActivo());
        }

        return productoRepository.save(productoActual);
    }

    public Producto desactivarProducto(Long idProducto) {

        Producto producto = buscarProductoPorId(idProducto);

        if (Boolean.FALSE.equals(producto.getActivo())) {
            throw new IllegalArgumentException(
                    "El producto ya se encuentra desactivado");
        }

        producto.setActivo(false);

        return productoRepository.save(producto);
    }

    public List<Producto> listarProductosActivos() {
        return productoRepository
                .findByActivoTrueOrderByNombreAsc();
    }

    public List<Producto> listarProductosConStockBajo() {
        return productoRepository
                .buscarProductosConStockBajo();
    }
}