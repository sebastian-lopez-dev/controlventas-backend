package com.negocio.controlventas.dto;

public class ConteoProductoRequest {

    private Long idProducto;
    private Integer cantidadContada;
    private String observaciones;

    public ConteoProductoRequest() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidadContada() {
        return cantidadContada;
    }

    public void setCantidadContada(
            Integer cantidadContada) {

        this.cantidadContada = cantidadContada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones = observaciones;
    }
}