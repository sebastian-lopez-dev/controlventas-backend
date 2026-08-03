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
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Long idPrestamo;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_cliente",
            nullable = false
    )
    private Cliente cliente;

    @Column(
            name = "numero_prestamo",
            length = 30,
            unique = true
    )
    private String numeroPrestamo;

    @Column(
            name = "monto_capital",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal montoCapital;

    @Column(
            name = "porcentaje_interes",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal porcentajeInteres;

    @Column(
            name = "monto_interes",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal montoInteres;

    @Column(
            name = "deuda_total",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal deudaTotal;

    @Column(
            name = "saldo_pendiente",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal saldoPendiente;

    @Column(
            name = "modalidad_pago",
            nullable = false,
            length = 20
    )
    private String modalidadPago;

    @Column(
            name = "monto_cuota",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal montoCuota;

    @Column(
            name = "fecha_desembolso",
            nullable = false
    )
    private LocalDate fechaDesembolso;

    @Column(
            name = "fecha_primer_pago",
            nullable = false
    )
    private LocalDate fechaPrimerPago;

    @Column(
            name = "fecha_vencimiento"
    )
    private LocalDate fechaVencimiento;

    @Column(
            name = "estado",
            nullable = false,
            length = 20
    )
    private String estado;

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

        if (estado == null || estado.isBlank()) {
            estado = "ACTIVO";
        }
    }

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Long idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNumeroPrestamo() {
        return numeroPrestamo;
    }

    public void setNumeroPrestamo(String numeroPrestamo) {
        this.numeroPrestamo = numeroPrestamo;
    }

    public BigDecimal getMontoCapital() {
        return montoCapital;
    }

    public void setMontoCapital(BigDecimal montoCapital) {
        this.montoCapital = montoCapital;
    }

    public BigDecimal getPorcentajeInteres() {
        return porcentajeInteres;
    }

    public void setPorcentajeInteres(
            BigDecimal porcentajeInteres
    ) {
        this.porcentajeInteres = porcentajeInteres;
    }

    public BigDecimal getMontoInteres() {
        return montoInteres;
    }

    public void setMontoInteres(BigDecimal montoInteres) {
        this.montoInteres = montoInteres;
    }

    public BigDecimal getDeudaTotal() {
        return deudaTotal;
    }

    public void setDeudaTotal(BigDecimal deudaTotal) {
        this.deudaTotal = deudaTotal;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(
            BigDecimal saldoPendiente
    ) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getModalidadPago() {
        return modalidadPago;
    }

    public void setModalidadPago(String modalidadPago) {
        this.modalidadPago = modalidadPago;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public LocalDate getFechaDesembolso() {
        return fechaDesembolso;
    }

    public void setFechaDesembolso(
            LocalDate fechaDesembolso
    ) {
        this.fechaDesembolso = fechaDesembolso;
    }

    public LocalDate getFechaPrimerPago() {
        return fechaPrimerPago;
    }

    public void setFechaPrimerPago(
            LocalDate fechaPrimerPago
    ) {
        this.fechaPrimerPago = fechaPrimerPago;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(
            LocalDate fechaVencimiento
    ) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones
    ) {
        this.observaciones = observaciones;
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