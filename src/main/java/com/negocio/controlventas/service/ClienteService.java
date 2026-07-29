package com.negocio.controlventas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.negocio.controlventas.model.Cliente;
import com.negocio.controlventas.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(
            ClienteRepository clienteRepository) {

        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente guardarCliente(Cliente cliente) {

        if (cliente.getNombres() == null
                || cliente.getNombres().isBlank()) {

            throw new IllegalArgumentException(
                    "Los nombres son obligatorios");
        }

        if (cliente.getApellidoPaterno() == null
                || cliente.getApellidoPaterno().isBlank()) {

            throw new IllegalArgumentException(
                    "El apellido paterno es obligatorio");
        }

        validarDni(cliente);
        validarCelular(cliente);

        cliente.setNombres(
                cliente.getNombres().trim());

        cliente.setApellidoPaterno(
                cliente.getApellidoPaterno().trim());

        if (cliente.getApellidoMaterno() != null) {
            cliente.setApellidoMaterno(
                    cliente.getApellidoMaterno().trim());
        }

        cliente.setCodigoCliente(null);
        cliente.setActivo(true);

        Cliente clienteGuardado = clienteRepository.save(cliente);

        String codigoGenerado = String.format(
                "CLI-%05d",
                clienteGuardado.getIdCliente());

        clienteGuardado.setCodigoCliente(codigoGenerado);

        return clienteRepository.save(clienteGuardado);
    }

    private void validarDni(Cliente cliente) {

        if (cliente.getDni() == null
                || cliente.getDni().isBlank()) {

            cliente.setDni(null);
            return;
        }

        String dniLimpio = cliente.getDni().trim();

        if (!dniLimpio.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 números");
        }

        if (clienteRepository.existsByDni(dniLimpio)) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con el DNI "
                            + dniLimpio);
        }

        cliente.setDni(dniLimpio);
    }

    private void validarCelular(Cliente cliente) {

        if (cliente.getCelular() == null
                || cliente.getCelular().isBlank()) {

            cliente.setCelular(null);
            return;
        }

        String celularLimpio = cliente.getCelular().trim();

        if (!celularLimpio.matches("\\d{9}")) {
            throw new IllegalArgumentException(
                    "El celular debe contener 9 números");
        }

        cliente.setCelular(celularLimpio);
    }

    public Cliente buscarClientePorId(Long idCliente) {

        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un cliente con el ID "
                                + idCliente));
    }

    public Cliente buscarClientePorDni(String dni) {

        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 números");
        }

        return clienteRepository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un cliente con el DNI "
                                + dni));
    }

    public Cliente buscarClientePorCodigo(
            String codigoCliente) {

        if (codigoCliente == null
                || codigoCliente.isBlank()) {

            throw new IllegalArgumentException(
                    "El código del cliente es obligatorio");
        }

        String codigoLimpio = codigoCliente.trim().toUpperCase();

        return clienteRepository
                .findByCodigoCliente(codigoLimpio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un cliente con el código "
                                + codigoLimpio));
    }

    @Transactional
    public Cliente actualizarCliente(
            Long idCliente,
            Cliente datosNuevos) {

        Cliente clienteActual = buscarClientePorId(idCliente);

        if (datosNuevos.getNombres() == null
                || datosNuevos.getNombres().isBlank()) {

            throw new IllegalArgumentException(
                    "Los nombres son obligatorios");
        }

        if (datosNuevos.getApellidoPaterno() == null
                || datosNuevos.getApellidoPaterno().isBlank()) {

            throw new IllegalArgumentException(
                    "El apellido paterno es obligatorio");
        }

        String dniLimpio = null;

        if (datosNuevos.getDni() != null
                && !datosNuevos.getDni().isBlank()) {

            dniLimpio = datosNuevos.getDni().trim();

            if (!dniLimpio.matches("\\d{8}")) {
                throw new IllegalArgumentException(
                        "El DNI debe contener exactamente 8 números");
            }

            Cliente clienteConMismoDni = clienteRepository
                    .findByDni(dniLimpio)
                    .orElse(null);

            if (clienteConMismoDni != null
                    && !clienteConMismoDni.getIdCliente()
                            .equals(idCliente)) {

                throw new IllegalArgumentException(
                        "Ya existe otro cliente con el DNI "
                                + dniLimpio);
            }
        }

        validarCelular(datosNuevos);

        clienteActual.setNombres(
                datosNuevos.getNombres().trim());

        clienteActual.setApellidoPaterno(
                datosNuevos.getApellidoPaterno().trim());

        clienteActual.setApellidoMaterno(
                datosNuevos.getApellidoMaterno());

        clienteActual.setDni(dniLimpio);

        clienteActual.setCelular(
                datosNuevos.getCelular());

        clienteActual.setDireccion(
                datosNuevos.getDireccion());

        clienteActual.setDistrito(
                datosNuevos.getDistrito());

        clienteActual.setZona(
                datosNuevos.getZona());

        clienteActual.setReferencia(
                datosNuevos.getReferencia());

        clienteActual.setPuestoTrabajo(
                datosNuevos.getPuestoTrabajo());

        if (datosNuevos.getActivo() != null) {
            clienteActual.setActivo(
                    datosNuevos.getActivo());
        }

        return clienteRepository.save(clienteActual);
    }

    @Transactional
    public Cliente desactivarCliente(Long idCliente) {

        Cliente cliente = buscarClientePorId(idCliente);

        if (Boolean.FALSE.equals(cliente.getActivo())) {
            throw new IllegalArgumentException(
                    "El cliente ya está desactivado");
        }

        cliente.setActivo(false);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente activarCliente(Long idCliente) {

        Cliente cliente = buscarClientePorId(idCliente);

        if (Boolean.TRUE.equals(cliente.getActivo())) {
            throw new IllegalArgumentException(
                    "El cliente ya está activo");
        }

        cliente.setActivo(true);

        return clienteRepository.save(cliente);
    }
}