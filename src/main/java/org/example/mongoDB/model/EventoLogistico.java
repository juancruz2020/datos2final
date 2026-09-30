package org.example.mongoDB.model;

import java.util.Date;

public class EventoLogistico {

    private String id;
    private String envioId;
    private Date fechaHora;
    private String tipoEvento;
    private String ubicacion;
    private String descripcion;

    public EventoLogistico(
            String id,
            String envioId,
            Date fechaHora,
            String tipoEvento,
            String ubicacion,
            String descripcion
    ) {
        this.id = id;
        this.envioId = envioId;
        this.fechaHora = fechaHora;
        this.tipoEvento = tipoEvento;
        this.ubicacion = ubicacion;
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

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}