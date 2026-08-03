package com.negocio.controlventas.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.negocio.controlventas.dto.PagoPrestamoRequest;
import com.negocio.controlventas.dto.PrestamoRequest;
import com.negocio.controlventas.service.PrestamoService;
import org.springframework.web.bind.annotation.DeleteMapping;
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(
            PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<?> listarPrestamos() {
        return ResponseEntity.ok(
                prestamoService.listarPrestamos());
    }

    @GetMapping("/{idPrestamo}/cuotas")
    public ResponseEntity<?> listarCuotas(
            @PathVariable Long idPrestamo) {
        try {
            return ResponseEntity.ok(
                    prestamoService.listarCuotas(idPrestamo));

        } catch (IllegalArgumentException error) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    error.getMessage()));
        }
    }

    @GetMapping("/{idPrestamo}/pagos")
    public ResponseEntity<?> listarPagos(
            @PathVariable Long idPrestamo) {
        try {
            return ResponseEntity.ok(
                    prestamoService.listarPagos(idPrestamo));

        } catch (IllegalArgumentException error) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    error.getMessage()));
        }
    }

    @GetMapping("/pagos")
    public ResponseEntity<?> listarTodosLosPagos() {
        return ResponseEntity.ok(
                prestamoService.listarTodosLosPagos());
    }

    @PostMapping("/{idPrestamo}/pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable Long idPrestamo,
            @RequestBody PagoPrestamoRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            prestamoService.registrarPago(
                                    idPrestamo,
                                    request));

        } catch (IllegalArgumentException error) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    error.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> guardarPrestamo(
            @RequestBody PrestamoRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            prestamoService.guardarPrestamo(
                                    request));

        } catch (IllegalArgumentException error) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    error.getMessage()));
        }
    }

    @DeleteMapping("/{idPrestamo}")
public ResponseEntity<?> eliminarPrestamo(
        @PathVariable Long idPrestamo
) {
    try {
        prestamoService.eliminarPrestamo(idPrestamo);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Préstamo eliminado correctamente."
                )
        );

    } catch (IllegalArgumentException error) {
        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "mensaje",
                                error.getMessage()
                        )
                );
    }
}
}