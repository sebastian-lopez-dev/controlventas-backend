package com.negocio.controlventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.Pago;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.negocio.controlventas.model.EstadoPago;
import java.math.BigDecimal;

public interface PagoRepository
        extends JpaRepository<Pago, Long> {

    Optional<Pago> findByCodigoPago(
            String codigoPago);

    List<Pago> findByVenta_IdVentaOrderByFechaPagoDesc(
            Long idVenta);

    List<Pago> findByCobrador_IdCobradorOrderByFechaPagoDesc(
            Long idCobrador);

    @Query("""
            SELECT p
            FROM Pago p
            WHERE p.cobrador.idCobrador = :idCobrador
            AND p.fechaPago >= :inicio
            AND p.fechaPago < :fin
            AND p.estado = :estado
            ORDER BY p.fechaPago ASC
            """)
    List<Pago> buscarPagosDelDia(
            @Param("idCobrador") Long idCobrador,

            @Param("inicio") LocalDateTime inicio,

            @Param("fin") LocalDateTime fin,

            @Param("estado") EstadoPago estado);

    @Query("""
            SELECT COALESCE(SUM(p.montoTotal), 0)
            FROM Pago p
            WHERE p.estado = :estado
            """)
    BigDecimal sumarTotalCobrado(
            @Param("estado") EstadoPago estado);

    @Query("""
            SELECT COALESCE(SUM(p.montoTotal), 0)
            FROM Pago p
            WHERE p.fechaPago >= :inicio
            AND p.fechaPago < :fin
            AND p.estado = :estado
            """)
    BigDecimal sumarCobradoEntreFechas(
            @Param("inicio") LocalDateTime inicio,

            @Param("fin") LocalDateTime fin,

            @Param("estado") EstadoPago estado);
}