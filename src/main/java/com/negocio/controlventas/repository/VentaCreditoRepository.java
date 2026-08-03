package com.negocio.controlventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.EstadoVenta;
import com.negocio.controlventas.model.VentaCredito;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface VentaCreditoRepository
        extends JpaRepository<VentaCredito, Long> {

    Optional<VentaCredito> findByNumeroContrato(
            String numeroContrato);

    List<VentaCredito> findByCliente_IdClienteOrderByFechaVentaDesc(
            Long idCliente);

    List<VentaCredito> findByEstadoOrderByFechaVentaDesc(
            EstadoVenta estado);

    List<VentaCredito> findByCobrador_IdCobradorAndEstadoOrderByFechaVentaDesc(
            Long idCobrador,
            EstadoVenta estado);

            long countByEstado(EstadoVenta estado);

@Query("""
        SELECT COALESCE(SUM(v.saldoPendiente), 0)
        FROM VentaCredito v
        WHERE v.estado = :estado
        """)
BigDecimal sumarSaldoPorEstado(
        @Param("estado")
        EstadoVenta estado);

@Query("""
        SELECT COALESCE(SUM(v.totalVenta), 0)
        FROM VentaCredito v
        WHERE v.estado <> :estadoAnulado
        """)
BigDecimal sumarTotalVendido(
        @Param("estadoAnulado")
        EstadoVenta estadoAnulado);

        boolean existsByCliente_IdCliente(Long idCliente);
}