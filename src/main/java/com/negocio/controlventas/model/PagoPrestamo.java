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
@Table(name = "pagos_prestamo")
public class PagoPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_prestamo")
    private Long idPagoPrestamo;

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
            name = "monto_pago",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal montoPago;

    @Column(
            name = "fecha_pago",
            nullable = false
    )
    private LocalDate fechaPago;

    @Column(
            name = "saldo_anterior",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal saldoAnterior;

    @Column(
            name = "saldo_posterior",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal saldoPosterior;

    @Column(
            name = "observaciones",
            length = 500
    )
    private String observaciones;

    @Column(
            name = "fecha_registro",
            nullable = false
    )
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void antesDeGuardar() {
        fechaRegistro = LocalDateTime.now();

        if (fechaPago == null) {
            fechaPago = LocalDate.now();
        }
    }

    public Long getIdPagoPrestamo() {
        return idPagoPrestamo;
    }

    public void setIdPagoPrestamo(Long idPagoPrestamo) {
        this.idPagoPrestamo = idPagoPrestamo;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoPosterior() {
        return saldoPosterior;
    }

    public void setSaldoPosterior(BigDecimal saldoPosterior) {
        this.saldoPosterior = saldoPosterior;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}