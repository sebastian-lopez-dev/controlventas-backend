package com.negocio.controlventas.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negocio.controlventas.model.Producto;
import com.negocio.controlventas.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @PostMapping
    public ResponseEntity<?> guardarProducto(
            @RequestBody Producto producto) {

        try {
            Producto productoGuardado = productoService.guardarProducto(producto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(productoGuardado);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", error.getMessage()));
        }
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<?> buscarProductoPorId(
            @PathVariable Long idProducto) {

        try {
            return ResponseEntity.ok(
                    productoService.buscarProductoPorId(idProducto));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", error.getMessage()));
        }
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long idProducto,
            @RequestBody Producto producto) {

        try {
            Producto productoActualizado = productoService.actualizarProducto(
                    idProducto,
                    producto);

            return ResponseEntity.ok(productoActualizado);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", error.getMessage()));
        }
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<?> desactivarProducto(
            @PathVariable Long idProducto) {

        try {
            Producto productoDesactivado = productoService.desactivarProducto(idProducto);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Producto desactivado correctamente",
                            "idProducto", productoDesactivado.getIdProducto(),
                            "activo", productoDesactivado.getActivo()));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", error.getMessage()));
        }
    }

    @DeleteMapping("/{idProducto}/definitivo")
    public ResponseEntity<?> eliminarProductoDefinitivamente(
            @PathVariable Long idProducto) {

        try {
            productoService
                    .eliminarProductoDefinitivamente(
                            idProducto);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Producto eliminado definitivamente",
                            "idProducto",
                            idProducto));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/activos")
    public List<Producto> listarProductosActivos() {

        return productoService.listarProductosActivos();
    }

    @GetMapping("/stock-bajo")
    public List<Producto> listarProductosConStockBajo() {

        return productoService
                .listarProductosConStockBajo();
    }
}