package com.negocio.controlventas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "cuotas",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "id_venta",
                                "numero_cuota"
                        })
        })
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Long idCuota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonIgnore
    private VentaCredito venta;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(
            name = "fecha_vencimiento",
            nullable = false)
    private LocalDate fechaVencimiento;

    @Column(
            name = "monto_programado",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal montoProgramado;

    @Column(
            name = "monto_pagado",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal montoPagado =
            BigDecimal.ZERO;

    @Column(
            name = "saldo_cuota",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal saldoCuota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuota estado =
            EstadoCuota.PENDIENTE;

    public Cuota() {
    }

    @PrePersist
    public void antesDeGuardar() {

        if (montoPagado == null) {
            montoPagado = BigDecimal.ZERO;
        }

        if (saldoCuota == null) {
            saldoCuota = montoProgramado;
        }

        if (estado == null) {
            estado = EstadoCuota.PENDIENTE;
        }
    }

    public Long getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Long idCuota) {
        this.idCuota = idCuota;
    }

    public VentaCredito getVenta() {
        return venta;
    }

    public void setVenta(VentaCredito venta) {
        this.venta = venta;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(
            Integer numeroCuota) {

        this.numeroCuota = numeroCuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(
            LocalDate fechaVencimiento) {

        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getMontoProgramado() {
        return montoProgramado;
    }

    public void setMontoProgramado(
            BigDecimal montoProgramado) {

        this.montoProgramado = montoProgramado;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(
            BigDecimal montoPagado) {

        this.montoPagado = montoPagado;
    }

    public BigDecimal getSaldoCuota() {
        return saldoCuota;
    }

    public void setSaldoCuota(
            BigDecimal saldoCuota) {

        this.saldoCuota = saldoCuota;
    }

    public EstadoCuota getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuota estado) {
        this.estado = estado;
    }
}