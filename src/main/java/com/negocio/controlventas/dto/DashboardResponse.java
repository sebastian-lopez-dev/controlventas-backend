package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DashboardResponse(

        LocalDateTime fechaConsulta,

        long productosActivos,
        long stockAlmacen,
        long stockEnCarro,
        int productosConStockBajo,

        long clientesActivos,
        long salidasAbiertas,

        long ventasActivas,
        BigDecimal deudaPendiente,
        BigDecimal totalVendido,

        BigDecimal totalCobrado,
        BigDecimal cobradoHoy
) {
}
