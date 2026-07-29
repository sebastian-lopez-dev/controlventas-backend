package com.negocio.controlventas.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.negocio.controlventas.model.RolUsuario;
import com.negocio.controlventas.model.Usuario;
import com.negocio.controlventas.repository.UsuarioRepository;

@Configuration
public class DatosInicialesConfig {

    @Bean
    public ApplicationRunner crearUsuariosIniciales(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {

        return argumentos -> {

            crearAdministrador(
                usuarioRepository,
                passwordEncoder
            );

            crearUsuarioGregorio(
                usuarioRepository,
                passwordEncoder
            );
        };
    }

    private void crearAdministrador(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {

        boolean administradorExiste =
            usuarioRepository.existsByUsuarioIgnoreCase(
                "admin"
            );

        if (administradorExiste) {
            return;
        }

        Usuario administrador = new Usuario();

        administrador.setUsuario("admin");
        administrador.setContrasena(
            passwordEncoder.encode("Admin2026*")
        );
        administrador.setNombreCompleto(
            "Administrador"
        );
        administrador.setRol(
            RolUsuario.ADMINISTRADOR
        );
        administrador.setActivo(true);
        administrador.setIdCobrador(null);

        usuarioRepository.save(administrador);
    }

    private void crearUsuarioGregorio(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {

        boolean gregorioExiste =
            usuarioRepository.existsByUsuarioIgnoreCase(
                "gregorio"
            );

        if (gregorioExiste) {
            return;
        }

        Usuario gregorio = new Usuario();

        gregorio.setUsuario("gregorio");
        gregorio.setContrasena(
            passwordEncoder.encode("Gregorio2026*")
        );
        gregorio.setNombreCompleto(
            "Gregorio"
        );
        gregorio.setRol(
            RolUsuario.COBRADOR
        );
        gregorio.setActivo(true);

        /*
         * Lo asignaremos al cobrador correcto
         * después de revisar su ID en MySQL.
         */
        gregorio.setIdCobrador(null);

        usuarioRepository.save(gregorio);
    }

}