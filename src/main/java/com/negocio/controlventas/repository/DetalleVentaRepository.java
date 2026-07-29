package com.negocio.controlventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.DetalleVenta;

public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta>
            findByVenta_IdVenta(Long idVenta);
}