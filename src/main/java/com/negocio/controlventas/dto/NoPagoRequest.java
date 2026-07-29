package com.negocio.controlventas.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.negocio.controlventas.model.MotivoNoPago;

public class NoPagoRequest {

    private Long idCuota;
    private Long idCobrador;
    private LocalDateTime fechaVisita;
    private MotivoNoPago motivo;
    private String observaciones;
    private LocalDate proximaFecha;

    public NoPagoRequest() {
    }

    public Long getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Long idCuota) {
        this.idCuota = idCuota;
    }

    public Long getIdCobrador() {
        return idCobrador;
    }

    public void setIdCobrador(
        Long idCobrador
    ) {
        this.idCobrador = idCobrador;
    }

    public LocalDateTime getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(
        LocalDateTime fechaVisita
    ) {
        this.fechaVisita = fechaVisita;
    }

    public MotivoNoPago getMotivo() {
        return motivo;
    }

    public void setMotivo(
        MotivoNoPago motivo
    ) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
        String observaciones
    ) {
        this.observaciones = observaciones;
    }

    public LocalDate getProximaFecha() {
        return proximaFecha;
    }

    public void setProximaFecha(
        LocalDate proximaFecha
    ) {
        this.proximaFecha = proximaFecha;
    }

}