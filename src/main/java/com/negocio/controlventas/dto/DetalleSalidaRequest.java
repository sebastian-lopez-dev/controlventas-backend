package com.negocio.controlventas.dto;

public class DetalleSalidaRequest {

    private Long idProducto;
    private Integer cantidad;

    public DetalleSalidaRequest() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}