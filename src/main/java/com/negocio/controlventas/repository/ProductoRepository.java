package com.negocio.controlventas.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByActivoTrueOrderByNombreAsc();

    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.activo = true
            AND p.stockAlmacen <= p.stockMinimo
            ORDER BY p.stockAlmacen ASC
            """)
    List<Producto> buscarProductosConStockBajo();
    long countByActivoTrue();

@Query("""
        SELECT COALESCE(SUM(p.stockAlmacen), 0)
        FROM Producto p
        WHERE p.activo = true
        """)
Long sumarStockAlmacen();
}