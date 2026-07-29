package com.negocio.controlventas.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negocio.controlventas.dto.NoPagoRequest;
import com.negocio.controlventas.model.VisitaCobranza;
import com.negocio.controlventas.service.VisitaCobranzaService;

@RestController
@RequestMapping("/api/visitas-cobranza")
public class VisitaCobranzaController {

    private final VisitaCobranzaService
        visitaService;

    public VisitaCobranzaController(
        VisitaCobranzaService visitaService
    ) {
        this.visitaService = visitaService;
    }

    @PostMapping("/no-pago")
    public ResponseEntity<?> registrarNoPago(
        @RequestBody NoPagoRequest solicitud
    ) {
        try {
            VisitaCobranza visita =
                visitaService.registrarNoPago(
                    solicitud
                );

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(visita);

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

    @GetMapping("/cobrador/{idCobrador}")
    public ResponseEntity<?> listarPorCobrador(
        @PathVariable Long idCobrador
    ) {
        try {
            return ResponseEntity.ok(
                visitaService.listarPorCobrador(
                    idCobrador
                )
            );

        } catch (IllegalArgumentException error) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    Map.of(
                        "mensaje",
                        error.getMessage()
                    )
                );
        }
    }

    @GetMapping("/cuota/{idCuota}")
    public ResponseEntity<?> listarPorCuota(
        @PathVariable Long idCuota
    ) {
        try {
            return ResponseEntity.ok(
                visitaService.listarPorCuota(
                    idCuota
                )
            );

        } catch (IllegalArgumentException error) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    Map.of(
                        "mensaje",
                        error.getMessage()
                    )
                );
        }
    }

}