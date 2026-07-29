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

import com.negocio.controlventas.model.Cobrador;
import com.negocio.controlventas.service.CobradorService;

@RestController
@RequestMapping("/api/cobradores")
@CrossOrigin(origins = "*")
public class CobradorController {

    private final CobradorService cobradorService;

    public CobradorController(
            CobradorService cobradorService) {

        this.cobradorService = cobradorService;
    }

    @GetMapping
    public List<Cobrador> listarCobradores() {

        return cobradorService.listarCobradores();
    }

    @GetMapping("/activos")
    public List<Cobrador> listarCobradoresActivos() {

        return cobradorService
                .listarCobradoresActivos();
    }

    @GetMapping("/{idCobrador}")
    public ResponseEntity<?> buscarCobrador(
            @PathVariable Long idCobrador) {

        try {
            return ResponseEntity.ok(
                    cobradorService
                            .buscarCobradorPorId(
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
    public ResponseEntity<?> guardarCobrador(
            @RequestBody Cobrador cobrador) {

        try {
            Cobrador cobradorGuardado =
                    cobradorService
                            .guardarCobrador(cobrador);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(cobradorGuardado);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }
}