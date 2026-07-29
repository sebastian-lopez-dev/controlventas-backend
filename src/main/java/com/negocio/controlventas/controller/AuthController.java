package com.negocio.controlventas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negocio.controlventas.dto.LoginRequest;
import com.negocio.controlventas.dto.LoginResponse;
import com.negocio.controlventas.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
        AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest solicitud
    ) {

        LoginResponse respuesta =
            authService.iniciarSesion(solicitud);

        return ResponseEntity.ok(respuesta);
    }

}