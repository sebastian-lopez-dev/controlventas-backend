package com.negocio.controlventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.Cliente;

public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {

    boolean existsByDni(String dni);

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByCodigoCliente(
            String codigoCliente);

    List<Cliente> findByActivoTrueOrderByApellidoPaternoAscNombresAsc();

    long countByActivoTrue();
}