package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumenCobranzaResponse(

        LocalDate fecha,

        Long idCobrador,
        String codigoCobrador,
        String nombreCobrador,

        long contratosPendientes,
        int cuotasPendientes,
        BigDecimal totalPendiente,

        int cantidadPagos,
        BigDecimal totalCobrado,

        BigDecimal totalEfectivo,
        BigDecimal totalYape,
        BigDecimal totalPlin,
        BigDecimal totalTransferencia
) {
}