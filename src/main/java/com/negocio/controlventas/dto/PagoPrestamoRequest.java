package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoPrestamoRequest(

        BigDecimal montoPago,

        LocalDate fechaPago,

        String observaciones

) {
}