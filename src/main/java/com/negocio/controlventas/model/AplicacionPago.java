package com.negocio.controlventas.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "aplicaciones_pago",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "id_pago",
                                "id_cuota"
                        })
        })
public class AplicacionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aplicacion")
    private Long idAplicacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    @JsonIgnore
    private Pago pago;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cuota", nullable = false)
    private Cuota cuota;

    @Column(
            name = "monto_aplicado",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal montoAplicado;

    public AplicacionPago() {
    }

    public Long getIdAplicacion() {
        return idAplicacion;
    }

    public void setIdAplicacion(
            Long idAplicacion) {

        this.idAplicacion = idAplicacion;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Cuota getCuota() {
        return cuota;
    }

    public void setCuota(Cuota cuota) {
        this.cuota = cuota;
    }

    public BigDecimal getMontoAplicado() {
        return montoAplicado;
    }

    public void setMontoAplicado(
            BigDecimal montoAplicado) {

        this.montoAplicado = montoAplicado;
    }
}
