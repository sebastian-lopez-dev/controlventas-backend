package com.negocio.controlventas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "detalles_salida", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "id_salida",
                "id_producto"
        })
})
public class DetalleSalida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_salida")
    private Long idDetalleSalida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_salida", nullable = false)
    @JsonIgnore
    private SalidaMercaderia salida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_cargada", nullable = false)
    private Integer cantidadCargada;

    @Column(name = "cantidad_vendida", nullable = false)
    private Integer cantidadVendida = 0;

    @Column(name = "cantidad_contada")
    private Integer cantidadContada;

    @Column
    private Integer diferencia;

    @Column(length = 300)
    private String observaciones;

    public DetalleSalida() {
    }

    @PrePersist
    public void antesDeGuardar() {

        if (cantidadVendida == null) {
            cantidadVendida = 0;
        }
    }

    @Transient
    public Integer getStockEsperado() {

        int cargada = cantidadCargada == null
                ? 0
                : cantidadCargada;

        int vendida = cantidadVendida == null
                ? 0
                : cantidadVendida;

        return cargada - vendida;
    }

    public Long getIdDetalleSalida() {
        return idDetalleSalida;
    }

    public void setIdDetalleSalida(
            Long idDetalleSalida) {

        this.idDetalleSalida = idDetalleSalida;
    }

    public SalidaMercaderia getSalida() {
        return salida;
    }

    public void setSalida(
            SalidaMercaderia salida) {

        this.salida = salida;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidadCargada() {
        return cantidadCargada;
    }

    public void setCantidadCargada(
            Integer cantidadCargada) {

        this.cantidadCargada = cantidadCargada;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(
            Integer cantidadVendida) {

        this.cantidadVendida = cantidadVendida;
    }

    public Integer getCantidadContada() {
        return cantidadContada;
    }

    public void setCantidadContada(
            Integer cantidadContada) {

        this.cantidadContada = cantidadContada;
    }

    public Integer getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(Integer diferencia) {
        this.diferencia = diferencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones = observaciones;
    }
}