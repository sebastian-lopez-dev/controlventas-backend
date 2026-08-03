package com.negocio.controlventas.controller;

import java.util.List;
import java.util.Map;
import com.negocio.controlventas.model.Cuota;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.negocio.controlventas.dto.AsignarCobradorRequest;
import com.negocio.controlventas.dto.VentaCreditoRequest;
import com.negocio.controlventas.model.VentaCredito;
import com.negocio.controlventas.service.VentaCreditoService;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaCreditoController {

    private final VentaCreditoService ventaService;

    public VentaCreditoController(
            VentaCreditoService ventaService) {

        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaCredito> listarVentas() {
        return ventaService.listarVentas();
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<?> buscarVenta(
            @PathVariable Long idVenta) {

        try {
            return ResponseEntity.ok(
                    ventaService.buscarVentaPorId(
                            idVenta));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(
            @RequestBody VentaCreditoRequest solicitud) {

        try {
            VentaCredito ventaGuardada = ventaService.registrarVenta(
                    solicitud);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ventaGuardada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/{idVenta}/cuotas")
    public ResponseEntity<?> listarCuotas(
            @PathVariable Long idVenta) {

        try {
            List<Cuota> cuotas = ventaService.listarCuotas(idVenta);

            return ResponseEntity.ok(cuotas);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PostMapping("/{idVenta}/generar-cuotas")
    public ResponseEntity<?> generarCuotas(
            @PathVariable Long idVenta) {

        try {
            List<Cuota> cuotas = ventaService.generarCuotas(idVenta);

            return ResponseEntity.ok(cuotas);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PutMapping("/{idVenta}/cobrador")
    public ResponseEntity<?> asignarCobrador(
            @PathVariable Long idVenta,
            @RequestBody AsignarCobradorRequest solicitud) {

        try {
            VentaCredito ventaActualizada = ventaService.asignarCobrador(
                    idVenta,
                    solicitud.getIdCobrador());

            return ResponseEntity.ok(
                    ventaActualizada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/cobrador/{idCobrador}")
    public ResponseEntity<?> listarVentasDelCobrador(
            @PathVariable Long idCobrador) {

        try {
            return ResponseEntity.ok(
                    ventaService
                            .listarVentasDelCobrador(
                                    idCobrador));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }

    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<?> eliminarVenta(
            @PathVariable Long idVenta) {

        try {
            ventaService.eliminarVenta(idVenta);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Contrato eliminado correctamente",
                            "idVenta",
                            idVenta));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }
}