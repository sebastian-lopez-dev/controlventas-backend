package com.negocio.controlventas.dto;

import java.time.LocalDate;
import java.util.List;

public class SalidaMercaderiaRequest {

    private LocalDate fechaSalida;
    private String destino;
    private String vendedor;
    private String observaciones;
    private List<DetalleSalidaRequest> detalles;

    public SalidaMercaderiaRequest() {
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones = observaciones;
    }

    public List<DetalleSalidaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(
            List<DetalleSalidaRequest> detalles) {

        this.detalles = detalles;
    }
}