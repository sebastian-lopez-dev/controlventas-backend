package com.negocio.controlventas.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negocio.controlventas.service.CobranzaService;
import com.negocio.controlventas.service.ResumenCobranzaService;

@RestController
@RequestMapping("/api/cobranzas")
@CrossOrigin(origins = "*")
public class CobranzaController {

        private final CobranzaService cobranzaService;
        private final ResumenCobranzaService resumenService;

        public CobranzaController(
                        CobranzaService cobranzaService,
                        ResumenCobranzaService resumenService) {

                this.cobranzaService = cobranzaService;
                this.resumenService = resumenService;
        }

        @GetMapping("/cobrador/{idCobrador}/fecha/{fecha}")
        public ResponseEntity<?> listarPorFecha(
                        @PathVariable Long idCobrador,

                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

                try {
                        return ResponseEntity.ok(
                                        cobranzaService
                                                        .listarCobranzas(
                                                                        idCobrador,
                                                                        fecha));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @GetMapping("/cobrador/{idCobrador}/hoy")
        public ResponseEntity<?> listarDeHoy(
                        @PathVariable Long idCobrador) {

                try {
                        return ResponseEntity.ok(
                                        cobranzaService
                                                        .listarCobranzas(
                                                                        idCobrador,
                                                                        LocalDate.now()));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @GetMapping("/cobrador/{idCobrador}/resumen/{fecha}")
        public ResponseEntity<?> obtenerResumen(
                        @PathVariable Long idCobrador,

                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

                try {
                        return ResponseEntity.ok(
                                        resumenService.obtenerResumen(
                                                        idCobrador,
                                                        fecha));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }
}