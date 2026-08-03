package com.negocio.controlventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.DetalleSalida;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.negocio.controlventas.model.EstadoSalida;

public interface DetalleSalidaRepository
                extends JpaRepository<DetalleSalida, Long> {

        List<DetalleSalida> findBySalida_IdSalida(Long idSalida);

        Optional<DetalleSalida> findBySalida_IdSalidaAndProducto_IdProducto(
                        Long idSalida,
                        Long idProducto);

        List<DetalleSalida> findBySalida_IdSalidaAndDiferenciaNot(
                        Long idSalida,
                        Integer diferencia);

        List<DetalleSalida> findByDiferenciaNot(Integer diferencia);

        @Query("""
                        SELECT COALESCE(
                            SUM(
                                d.cantidadCargada
                                - d.cantidadVendida
                            ),
                            0
                        )
                        FROM DetalleSalida d
                        WHERE d.salida.estado = :estado
                        """)
        Long sumarStockEnCarro(
                        @Param("estado") EstadoSalida estado);

        boolean existsByProducto_IdProducto(
                        Long idProducto);

}