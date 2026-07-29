package com.negocio.controlventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.negocio.controlventas.model.VisitaCobranza;

@Repository
public interface VisitaCobranzaRepository
        extends JpaRepository<
            VisitaCobranza,
            Long
        > {

    List<VisitaCobranza>
        findByCobrador_IdCobradorOrderByFechaVisitaDesc(
            Long idCobrador
        );

    List<VisitaCobranza>
        findByCuota_IdCuotaOrderByFechaVisitaDesc(
            Long idCuota
        );

}