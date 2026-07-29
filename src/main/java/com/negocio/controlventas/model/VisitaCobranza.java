package com.negocio.controlventas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "visitas_cobranza")
public class VisitaCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visita")
    private Long idVisita;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_cuota",
        nullable = false
    )
    @JsonIgnoreProperties("venta")
    private Cuota cuota;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "id_cobrador",
        nullable = false
    )
    private Cobrador cobrador;

    @Column(
        name = "fecha_visita",
        nullable = false
    )
    private LocalDateTime fechaVisita;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "motivo",
        nullable = false,
        length = 40
    )
    private MotivoNoPago motivo;

    @Column(length = 300)
    private String observaciones;

    @Column(name = "proxima_fecha")
    private LocalDate proximaFecha;

    public VisitaCobranza() {
    }

    @PrePersist
    public void antesDeGuardar() {

        if (fechaVisita == null) {
            fechaVisita = LocalDateTime.now();
        }
    }

    public Long getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(Long idVisita) {
        this.idVisita = idVisita;
    }

    public Cuota getCuota() {
        return cuota;
    }

    public void setCuota(Cuota cuota) {
        this.cuota = cuota;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(
        Cobrador cobrador
    ) {
        this.cobrador = cobrador;
    }

    public LocalDateTime getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(
        LocalDateTime fechaVisita
    ) {
        this.fechaVisita = fechaVisita;
    }

    public MotivoNoPago getMotivo() {
        return motivo;
    }

    public void setMotivo(
        MotivoNoPago motivo
    ) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
        String observaciones
    ) {
        this.observaciones = observaciones;
    }

    public LocalDate getProximaFecha() {
        return proximaFecha;
    }

    public void setProximaFecha(
        LocalDate proximaFecha
    ) {
        this.proximaFecha = proximaFecha;
    }

}