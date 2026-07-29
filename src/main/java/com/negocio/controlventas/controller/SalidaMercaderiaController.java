package com.negocio.controlventas.controller;

import java.util.List;
import java.util.Map;
import com.negocio.controlventas.dto.DetalleSalidaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.negocio.controlventas.dto.CierreSalidaRequest;
import com.negocio.controlventas.dto.CantidadRequest;
import com.negocio.controlventas.dto.SalidaMercaderiaRequest;
import com.negocio.controlventas.model.DetalleSalida;
import com.negocio.controlventas.model.SalidaMercaderia;
import com.negocio.controlventas.service.SalidaMercaderiaService;

@RestController
@RequestMapping("/api/salidas")
@CrossOrigin(origins = "*")
public class SalidaMercaderiaController {

    private final SalidaMercaderiaService salidaService;

    public SalidaMercaderiaController(
            SalidaMercaderiaService salidaService) {

        this.salidaService = salidaService;
    }

    @GetMapping
    public List<SalidaMercaderia> listarSalidas() {
        return salidaService.listarSalidas();
    }

    @GetMapping("/{idSalida}")
    public ResponseEntity<?> buscarSalida(
            @PathVariable Long idSalida) {

        try {
            return ResponseEntity.ok(
                    salidaService.buscarSalidaPorId(
                            idSalida));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarSalida(
            @RequestBody SalidaMercaderiaRequest solicitud) {

        try {
            SalidaMercaderia salidaGuardada = salidaService.registrarSalida(
                    solicitud);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(salidaGuardada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PostMapping("/{idSalida}/productos")
    public ResponseEntity<?> agregarProducto(
            @PathVariable Long idSalida,
            @RequestBody DetalleSalidaRequest solicitud) {

        try {
            SalidaMercaderia salidaActualizada = salidaService.agregarProducto(
                    idSalida,
                    solicitud);

            return ResponseEntity.ok(
                    salidaActualizada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PutMapping("/{idSalida}/productos/{idProducto}")
    public ResponseEntity<?> corregirCantidad(
            @PathVariable Long idSalida,
            @PathVariable Long idProducto,
            @RequestBody CantidadRequest solicitud) {

        try {
            SalidaMercaderia salidaActualizada = salidaService
                    .corregirCantidadCargada(
                            idSalida,
                            idProducto,
                            solicitud.getCantidad());

            return ResponseEntity.ok(
                    salidaActualizada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @DeleteMapping("/{idSalida}/productos/{idProducto}")
    public ResponseEntity<?> retirarProducto(
            @PathVariable Long idSalida,
            @PathVariable Long idProducto) {

        try {
            SalidaMercaderia salidaActualizada = salidaService.retirarProducto(
                    idSalida,
                    idProducto);

            return ResponseEntity.ok(
                    salidaActualizada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @PutMapping("/{idSalida}/cerrar")
    public ResponseEntity<?> cerrarSalida(
            @PathVariable Long idSalida,
            @RequestBody CierreSalidaRequest solicitud) {

        try {
            SalidaMercaderia salidaCerrada = salidaService.cerrarSalida(
                    idSalida,
                    solicitud);

            return ResponseEntity.ok(
                    salidaCerrada);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/{idSalida}/diferencias")
    public ResponseEntity<?> listarDiferencias(
            @PathVariable Long idSalida) {

        try {
            return ResponseEntity.ok(
                    salidaService.listarDiferencias(
                            idSalida));

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            error.getMessage()));
        }
    }

    @GetMapping("/diferencias")
    public List<DetalleSalida> listarTodasLasDiferencias() {

        return salidaService
                .listarTodasLasDiferencias();
    }
}