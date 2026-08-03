package com.negocio.controlventas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuotas_prestamo")
public class CuotaPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota_prestamo")
    private Long idCuotaPrestamo;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_prestamo",
            nullable = false
    )
    private Prestamo prestamo;

    @Column(
            name = "numero_cuota",
            nullable = false
    )
    private Integer numeroCuota;

    @Column(
            name = "fecha_vencimiento",
            nullable = false
    )
    private LocalDate fechaVencimiento;

    @Column(
            name = "monto_cuota",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal montoCuota;

    @Column(
            name = "monto_pagado",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal montoPagado;

    @Column(
            name = "saldo_pendiente",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal saldoPendiente;

    @Column(
            name = "estado",
            nullable = false,
            length = 20
    )
    private String estado;

    @Column(
            name = "fecha_registro",
            nullable = false
    )
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void antesDeGuardar() {
        fechaRegistro = LocalDateTime.now();

        if (montoPagado == null) {
            montoPagado = BigDecimal.ZERO;
        }

        if (saldoPendiente == null) {
            saldoPendiente = montoCuota;
        }

        if (estado == null || estado.isBlank()) {
            estado = "PENDIENTE";
        }
    }

    public Long getIdCuotaPrestamo() {
        return idCuotaPrestamo;
    }

    public void setIdCuotaPrestamo(Long idCuotaPrestamo) {
        this.idCuotaPrestamo = idCuotaPrestamo;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(
            LocalDate fechaVencimiento
    ) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(
            BigDecimal saldoPendiente
    ) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(
            LocalDateTime fechaRegistro
    ) {
        this.fechaRegistro = fechaRegistro;
    }
}