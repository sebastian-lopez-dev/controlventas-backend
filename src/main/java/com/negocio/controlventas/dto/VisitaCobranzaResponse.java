package com.negocio.controlventas.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.negocio.controlventas.model.MotivoNoPago;

public record VisitaCobranzaResponse(

    Long idVisita,
    LocalDateTime fechaVisita,
    MotivoNoPago motivo,
    String observaciones,
    LocalDate proximaFecha,

    Long idCuota,
    Integer numeroCuota,

    Long idVenta,
    String numeroContrato,

    Long idCliente,
    String codigoCliente,
    String nombreCliente

) {
}