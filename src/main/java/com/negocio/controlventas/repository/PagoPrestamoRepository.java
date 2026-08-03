package com.negocio.controlventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.PagoPrestamo;

public interface PagoPrestamoRepository
        extends JpaRepository<PagoPrestamo, Long> {

    List<PagoPrestamo>
            findByPrestamo_IdPrestamoOrderByFechaRegistroDesc(
                    Long idPrestamo
            );


            List<PagoPrestamo> findAllByOrderByFechaRegistroDesc();
            void deleteByPrestamo_IdPrestamo(Long idPrestamo);
}