package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.negocio.controlventas.model.MetodoPago;

public class PagoRequest {

    private Long idVenta;
    private Long idCobrador;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private LocalDateTime fechaPago;
    private String numeroOperacion;
    private String observaciones;

    public PagoRequest() {
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public Long getIdCobrador() {
        return idCobrador;
    }

    public void setIdCobrador(Long idCobrador) {
        this.idCobrador = idCobrador;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(
            MetodoPago metodoPago) {

        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(
            LocalDateTime fechaPago) {

        this.fechaPago = fechaPago;
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
}