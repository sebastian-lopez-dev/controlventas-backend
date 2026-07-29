package com.negocio.controlventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.negocio.controlventas.model.Usuario;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuarioIgnoreCaseAndActivoTrue(
        String usuario
    );

    boolean existsByUsuarioIgnoreCase(String usuario);

}