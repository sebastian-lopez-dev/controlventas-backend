package com.negocio.controlventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.negocio.controlventas.model.EstadoCuota;

public record CobranzaPendienteResponse(

                Long idCuota,
                Integer numeroCuota,
                LocalDate fechaVencimiento,
                BigDecimal montoPendienteCuota,
                EstadoCuota estadoCuota,

                Long idVenta,
                String numeroContrato,
                BigDecimal saldoContrato,

                Long idCliente,
                String codigoCliente,
                String nombreCliente,
                String celular,
                String direccion,
                String zona,

                long diasAtraso) {
}