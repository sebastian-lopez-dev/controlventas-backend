package com.negocio.controlventas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.negocio.controlventas.model.Cuota;
import com.negocio.controlventas.model.EstadoCuota;
import com.negocio.controlventas.model.EstadoVenta;

public interface CuotaRepository
        extends JpaRepository<Cuota, Long> {

    List<Cuota> findByVenta_IdVentaOrderByNumeroCuotaAsc(
            Long idVenta);

    boolean existsByVenta_IdVenta(Long idVenta);

    List<Cuota> findByEstadoOrderByFechaVencimientoAsc(
            EstadoCuota estado);

    @Query("""
            SELECT DISTINCT c
            FROM Cuota c
            JOIN FETCH c.venta v
            JOIN FETCH v.cliente cl
            LEFT JOIN FETCH v.cobrador co
            WHERE co.idCobrador = :idCobrador
            AND v.estado = :estadoVenta
            AND c.fechaVencimiento <= :fecha
            AND c.saldoCuota > 0
            ORDER BY c.fechaVencimiento ASC,
                     c.numeroCuota ASC
            """)
    List<Cuota> buscarCobrosHastaFecha(
            @Param("idCobrador") Long idCobrador,
            @Param("fecha") LocalDate fecha,
            @Param("estadoVenta") EstadoVenta estadoVenta);
}
