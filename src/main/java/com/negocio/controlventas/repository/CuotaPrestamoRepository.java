package com.negocio.controlventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.CuotaPrestamo;

public interface CuotaPrestamoRepository
        extends JpaRepository<CuotaPrestamo, Long> {

    List<CuotaPrestamo> findByPrestamo_IdPrestamoOrderByNumeroCuotaAsc(
            Long idPrestamo);

    void deleteByPrestamo_IdPrestamo(Long idPrestamo);
}