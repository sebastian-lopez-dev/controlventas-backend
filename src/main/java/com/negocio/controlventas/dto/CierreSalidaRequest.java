package com.negocio.controlventas.dto;

import java.util.List;

public class CierreSalidaRequest {

    private List<ConteoProductoRequest> conteos;

    public CierreSalidaRequest() {
    }

    public List<ConteoProductoRequest> getConteos() {
        return conteos;
    }

    public void setConteos(
            List<ConteoProductoRequest> conteos) {

        this.conteos = conteos;
    }
}