package com.negocio.controlventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.EstadoSalida;
import com.negocio.controlventas.model.SalidaMercaderia;

public interface SalidaMercaderiaRepository
        extends JpaRepository<SalidaMercaderia, Long> {

    List<SalidaMercaderia> findByEstadoOrderByFechaSalidaDesc(
            EstadoSalida estado);

    long countByEstado(EstadoSalida estado);
}