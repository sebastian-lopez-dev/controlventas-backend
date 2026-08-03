package com.negocio.controlventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.Prestamo;

public interface PrestamoRepository
        extends JpaRepository<Prestamo, Long> {

    Optional<Prestamo> findByNumeroPrestamo(
            String numeroPrestamo
    );

    List<Prestamo> findAllByOrderByFechaRegistroDesc();
}