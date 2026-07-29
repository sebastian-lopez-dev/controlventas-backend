package com.negocio.controlventas.dto;

import com.negocio.controlventas.model.RolUsuario;

public class LoginResponse {

    private String token;
    private String tipoToken;
    private Long idUsuario;
    private String nombreCompleto;
    private RolUsuario rol;
    private Long idCobrador;

    public LoginResponse(
        String token,
        Long idUsuario,
        String nombreCompleto,
        RolUsuario rol,
        Long idCobrador
    ) {
        this.token = token;
        this.tipoToken = "Bearer";
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.idCobrador = idCobrador;
    }

    public String getToken() {
        return token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public Long getIdCobrador() {
        return idCobrador;
    }

}