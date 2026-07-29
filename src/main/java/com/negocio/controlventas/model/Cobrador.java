package com.negocio.controlventas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cobradores")
public class Cobrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cobrador")
    private Long idCobrador;

    @Column(
            name = "codigo_cobrador",
            unique = true,
            length = 20)
    private String codigoCobrador;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(
            name = "apellido_paterno",
            nullable = false,
            length = 100)
    private String apellidoPaterno;

    @Column(
            name = "apellido_materno",
            length = 100)
    private String apellidoMaterno;

    @Column(unique = true, length = 8)
    private String dni;

    @Column(length = 9)
    private String celular;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(
            name = "fecha_registro",
            nullable = false,
            updatable = false)
    private LocalDateTime fechaRegistro;

    public Cobrador() {
    }

    @PrePersist
    public void antesDeGuardar() {

        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }

        if (activo == null) {
            activo = true;
        }
    }

    public Long getIdCobrador() {
        return idCobrador;
    }

    public void setIdCobrador(Long idCobrador) {
        this.idCobrador = idCobrador;
    }

    public String getCodigoCobrador() {
        return codigoCobrador;
    }

    public void setCodigoCobrador(
            String codigoCobrador) {

        this.codigoCobrador = codigoCobrador;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(
            String apellidoPaterno) {

        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(
            String apellidoMaterno) {

        this.apellidoMaterno = apellidoMaterno;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(
            LocalDateTime fechaRegistro) {

        this.fechaRegistro = fechaRegistro;
    }
}