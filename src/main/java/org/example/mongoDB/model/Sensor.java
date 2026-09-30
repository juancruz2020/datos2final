package org.example.mongoDB.model;

import java.util.Date;

public class Sensor {

    private String id;
    private String contenedorId;
    private String tipo;
    private String fabricante;
    private Date fechaInstalacion;
    private String estado;

    public Sensor(
            String id,
            String contenedorId,
            String tipo,
            String fabricante,
            Date fechaInstalacion,
            String estado
    ) {
        this.id = id;
        this.contenedorId = contenedorId;
        this.tipo = tipo;
        this.fabricante = fabricante;
        this.fechaInstalacion = fechaInstalacion;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenedorId() {
        return contenedorId;
    }

    public void setContenedorId(String contenedorId) {
        this.contenedorId = contenedorId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public Date getFechaInstalacion() {
        return fechaInstalacion;
    }

    public void setFechaInstalacion(Date fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}