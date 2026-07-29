package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.negocio.controlventas.model.ModalidadPago;

public class VentaCreditoRequest {

    private Long idCliente;
    private Long idSalida;
    private Long idCobrador;
    private LocalDate fechaVenta;
    private BigDecimal cuotaInicial;
    private ModalidadPago modalidadPago;
    private BigDecimal montoCuota;
    private String diaPago;
    private LocalDate fechaPrimerPago;
    private String observaciones;
    private String firmaCliente;
    private List<DetalleVentaRequest> detalles;

    public VentaCreditoRequest() {
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdSalida() {
        return idSalida;
    }

    public void setIdSalida(Long idSalida) {
        this.idSalida = idSalida;
    }

    public Long getIdCobrador() {
        return idCobrador;
    }

    public void setIdCobrador(
            Long idCobrador) {

        this.idCobrador = idCobrador;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(
            BigDecimal cuotaInicial) {

        this.cuotaInicial = cuotaInicial;
    }

    public ModalidadPago getModalidadPago() {
        return modalidadPago;
    }

    public void setModalidadPago(
            ModalidadPago modalidadPago) {

        this.modalidadPago = modalidadPago;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public String getDiaPago() {
        return diaPago;
    }

    public void setDiaPago(String diaPago) {
        this.diaPago = diaPago;
    }

    public LocalDate getFechaPrimerPago() {
        return fechaPrimerPago;
    }

    public void setFechaPrimerPago(
            LocalDate fechaPrimerPago) {

        this.fechaPrimerPago = fechaPrimerPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones = observaciones;
    }

    public String getFirmaCliente() {
        return firmaCliente;
    }

    public void setFirmaCliente(
            String firmaCliente) {

        this.firmaCliente = firmaCliente;
    }

    public List<DetalleVentaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(
            List<DetalleVentaRequest> detalles) {

        this.detalles = detalles;
    }
}