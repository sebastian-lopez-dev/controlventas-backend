package com.negocio.controlventas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.negocio.controlventas.model.Cobrador;

public interface CobradorRepository
        extends JpaRepository<Cobrador, Long> {

    boolean existsByDni(String dni);

    Optional<Cobrador> findByDni(String dni);

    Optional<Cobrador> findByCodigoCobrador(
            String codigoCobrador);

    List<Cobrador>
            findByActivoTrueOrderByApellidoPaternoAscNombresAsc();
}