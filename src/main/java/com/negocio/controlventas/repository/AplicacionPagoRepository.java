package com.negocio.controlventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.AplicacionPago;

public interface AplicacionPagoRepository
        extends JpaRepository<AplicacionPago, Long> {

    List<AplicacionPago>
            findByPago_IdPago(Long idPago);

    List<AplicacionPago>
            findByCuota_IdCuota(Long idCuota);
}