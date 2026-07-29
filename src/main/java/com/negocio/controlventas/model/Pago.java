package com.negocio.controlventas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(
            name = "codigo_pago",
            unique = true,
            length = 20)
    private String codigoPago;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonIgnoreProperties({
            "detalles",
            "salida",
            "cliente",
            "cobrador"
    })
    private VentaCredito venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cobrador", nullable = false)
    private Cobrador cobrador;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(
            name = "monto_total",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "metodo_pago",
            nullable = false,
            length = 30)
    private MetodoPago metodoPago;

    @Column(
            name = "saldo_anterior",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal saldoAnterior;

    @Column(
            name = "saldo_nuevo",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal saldoNuevo;

    @Column(name = "numero_operacion", length = 100)
    private String numeroOperacion;

    @Column(length = 300)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.REGISTRADO;

    @OneToMany(
            mappedBy = "pago",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<AplicacionPago> aplicaciones =
            new ArrayList<>();

    public Pago() {
    }

    @PrePersist
    public void antesDeGuardar() {

        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoPago.REGISTRADO;
        }
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public String getCodigoPago() {
        return codigoPago;
    }

    public void setCodigoPago(String codigoPago) {
        this.codigoPago = codigoPago;
    }

    public VentaCredito getVenta() {
        return venta;
    }

    public void setVenta(VentaCredito venta) {
        this.venta = venta;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(
            LocalDateTime fechaPago) {

        this.fechaPago = fechaPago;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(
            BigDecimal montoTotal) {

        this.montoTotal = montoTotal;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(
            MetodoPago metodoPago) {

        this.metodoPago = metodoPago;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(
            BigDecimal saldoAnterior) {

        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoNuevo() {
        return saldoNuevo;
    }

    public void setSaldoNuevo(
            BigDecimal saldoNuevo) {

        this.saldoNuevo = saldoNuevo;
    }

    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(
            String numeroOperacion) {

        this.numeroOperacion = numeroOperacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones = observaciones;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public List<AplicacionPago> getAplicaciones() {
        return aplicaciones;
    }

    public void setAplicaciones(
            List<AplicacionPago> aplicaciones) {

        this.aplicaciones = aplicaciones;
    }
}