package com.negocio.controlventas.config;

import org.springframework.beans.factory.annotation.Value;
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
            PasswordEncoder passwordEncoder,
            @Value("${app.initial.admin-password:}")
            String contrasenaAdministrador,
            @Value("${app.initial.gregorio-password:}")
            String contrasenaGregorio) {

        return argumentos -> {

            crearAdministrador(
                    usuarioRepository,
                    passwordEncoder,
                    contrasenaAdministrador);

            crearUsuarioGregorio(
                    usuarioRepository,
                    passwordEncoder,
                    contrasenaGregorio);
        };
    }

    private void crearAdministrador(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String contrasena) {

        boolean existe =
                usuarioRepository
                        .existsByUsuarioIgnoreCase(
                                "admin");

        if (existe || contrasena.isBlank()) {
            return;
        }

        Usuario administrador = new Usuario();

        administrador.setUsuario("admin");
        administrador.setContrasena(
                passwordEncoder.encode(contrasena));
        administrador.setNombreCompleto(
                "Administrador");
        administrador.setRol(
                RolUsuario.ADMINISTRADOR);
        administrador.setActivo(true);
        administrador.setIdCobrador(null);

        usuarioRepository.save(administrador);
    }

    private void crearUsuarioGregorio(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String contrasena) {

        boolean existe =
                usuarioRepository
                        .existsByUsuarioIgnoreCase(
                                "gregorio");

        if (existe || contrasena.isBlank()) {
            return;
        }

        Usuario gregorio = new Usuario();

        gregorio.setUsuario("gregorio");
        gregorio.setContrasena(
                passwordEncoder.encode(contrasena));
        gregorio.setNombreCompleto("Gregorio");
        gregorio.setRol(RolUsuario.COBRADOR);
        gregorio.setActivo(true);
        gregorio.setIdCobrador(null);

        usuarioRepository.save(gregorio);
    }
}