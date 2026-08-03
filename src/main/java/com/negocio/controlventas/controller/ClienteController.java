package com.negocio.controlventas.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.negocio.controlventas.model.Cliente;
import com.negocio.controlventas.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

        private final ClienteService clienteService;

        public ClienteController(
                        ClienteService clienteService) {

                this.clienteService = clienteService;
        }

        @GetMapping
        public List<Cliente> listarClientes() {
                return clienteService.listarClientes();
        }

        @PostMapping
        public ResponseEntity<?> guardarCliente(
                        @RequestBody Cliente cliente) {

                try {
                        Cliente clienteGuardado = clienteService.guardarCliente(cliente);

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(clienteGuardado);

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @GetMapping("/{idCliente}")
        public ResponseEntity<?> buscarClientePorId(
                        @PathVariable Long idCliente) {

                try {
                        return ResponseEntity.ok(
                                        clienteService.buscarClientePorId(idCliente));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @GetMapping("/dni/{dni}")
        public ResponseEntity<?> buscarClientePorDni(
                        @PathVariable String dni) {

                try {
                        return ResponseEntity.ok(
                                        clienteService.buscarClientePorDni(dni));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @GetMapping("/codigo/{codigoCliente}")
        public ResponseEntity<?> buscarClientePorCodigo(
                        @PathVariable String codigoCliente) {

                try {
                        return ResponseEntity.ok(
                                        clienteService.buscarClientePorCodigo(
                                                        codigoCliente));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @PutMapping("/{idCliente}")
        public ResponseEntity<?> actualizarCliente(
                        @PathVariable Long idCliente,
                        @RequestBody Cliente cliente) {

                try {
                        Cliente clienteActualizado = clienteService.actualizarCliente(
                                        idCliente,
                                        cliente);

                        return ResponseEntity.ok(clienteActualizado);

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @DeleteMapping("/{idCliente}")
        public ResponseEntity<?> desactivarCliente(
                        @PathVariable Long idCliente) {

                try {
                        Cliente clienteDesactivado = clienteService.desactivarCliente(
                                        idCliente);

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "mensaje",
                                                        "Cliente desactivado correctamente",
                                                        "idCliente",
                                                        clienteDesactivado.getIdCliente(),
                                                        "activo",
                                                        clienteDesactivado.getActivo()));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @PutMapping("/{idCliente}/activar")
        public ResponseEntity<?> activarCliente(
                        @PathVariable Long idCliente) {

                try {
                        Cliente clienteActivado = clienteService.activarCliente(
                                        idCliente);

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "mensaje",
                                                        "Cliente activado correctamente",
                                                        "idCliente",
                                                        clienteActivado.getIdCliente(),
                                                        "activo",
                                                        clienteActivado.getActivo()));

                } catch (IllegalArgumentException error) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(Map.of(
                                                        "mensaje",
                                                        error.getMessage()));
                }
        }

        @DeleteMapping("/{idCliente}/definitivo")
public ResponseEntity<?> eliminarClienteDefinitivamente(
        @PathVariable Long idCliente) {

    try {
        clienteService.eliminarClienteDefinitivamente(
                idCliente);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Cliente eliminado correctamente",
                        "idCliente",
                        idCliente));

    } catch (IllegalArgumentException error) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "mensaje",
                        error.getMessage()));
    }
}
}