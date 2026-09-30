package org.example.mongoDB.model;

import java.util.Date;

public class Incidente {

    private String id;
    private String envioId;
    private String tipo;
    private Date fecha;
    private String severidad;
    private String estado;
    private String descripcion;

    public Incidente(
            String id,
            String envioId,
            String tipo,
            Date fecha,
            String severidad,
            String estado,
            String descripcion
    ) {
        this.id = id;
        this.envioId = envioId;
        this.tipo = tipo;
        this.fecha = fecha;
        this.severidad = severidad;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvioId() {
        return envioId;
    }

    public void setEnvioId(String envioId) {
        this.envioId = envioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}