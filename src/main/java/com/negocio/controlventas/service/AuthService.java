package com.negocio.controlventas.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.negocio.controlventas.dto.LoginRequest;
import com.negocio.controlventas.dto.LoginResponse;
import com.negocio.controlventas.model.Usuario;
import com.negocio.controlventas.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    public AuthService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtEncoder jwtEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse iniciarSesion(
        LoginRequest solicitud
    ) {

        String nombreUsuario =
            solicitud.getUsuario().trim();

        Usuario usuario = usuarioRepository
            .findByUsuarioIgnoreCaseAndActivoTrue(
                nombreUsuario
            )
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario o contraseña incorrectos"
                )
            );

        boolean contrasenaCorrecta =
            passwordEncoder.matches(
                solicitud.getContrasena(),
                usuario.getContrasena()
            );

        if (!contrasenaCorrecta) {

            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Usuario o contraseña incorrectos"
            );
        }

        String token = generarToken(usuario);

        return new LoginResponse(
            token,
            usuario.getIdUsuario(),
            usuario.getNombreCompleto(),
            usuario.getRol(),
            usuario.getIdCobrador()
        );
    }

    private String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        Instant vencimiento = ahora.plus(
            expirationMinutes,
            ChronoUnit.MINUTES
        );

        JwtClaimsSet.Builder claims =
            JwtClaimsSet.builder()
                .issuer("controlventas")
                .issuedAt(ahora)
                .expiresAt(vencimiento)
                .subject(usuario.getUsuario())
                .claim(
                    "idUsuario",
                    usuario.getIdUsuario()
                )
                .claim(
                    "nombreCompleto",
                    usuario.getNombreCompleto()
                )
                .claim(
                    "rol",
                    usuario.getRol().name()
                );

        if (usuario.getIdCobrador() != null) {

            claims.claim(
                "idCobrador",
                usuario.getIdCobrador()
            );
        }

        JwsHeader encabezado = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        return jwtEncoder
            .encode(
                JwtEncoderParameters.from(
                    encabezado,
                    claims.build()
                )
            )
            .getTokenValue();
    }

}