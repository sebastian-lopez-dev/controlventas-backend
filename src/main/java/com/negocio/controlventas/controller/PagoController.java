package com.negocio.controlventas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negocio.controlventas.dto.PagoRequest;
import com.negocio.controlventas.model.Pago;
import com.negocio.controlventas.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> listarPagos() {

        return pagoService.listarPagos();
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<?> buscarPago(
            @PathVariable Long idPago) {

        try {
            return ResponseEntity.ok(
                    pagoService.buscarPagoPorId(
                            idPago));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<?> listarPagosDeVenta(
            @PathVariable Long idVenta) {

        try {
            return ResponseEntity.ok(
                    pagoService.listarPagosDeVenta(
                            idVenta));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/cobrador/{idCobrador}")
    public ResponseEntity<?> listarPagosDelCobrador(
            @PathVariable Long idCobrador) {

        try {
            return ResponseEntity.ok(
                    pagoService.listarPagosDelCobrador(
                            idCobrador));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarPago(
            @RequestBody PagoRequest solicitud) {

        try {
            Pago pagoGuardado =
                    pagoService.registrarPago(
                            solicitud);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(pagoGuardado);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }
}