package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrestamoRequest(

        Long idCliente,

        BigDecimal montoCapital,

        BigDecimal porcentajeInteres,

        String modalidadPago,

        BigDecimal montoCuota,

        LocalDate fechaDesembolso,

        LocalDate fechaPrimerPago,

        String observaciones

) {
}