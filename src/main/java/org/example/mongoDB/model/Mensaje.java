package org.example.mongoDB.model;

import java.util.Date;

public class Mensaje {

    private String id;
    private String conversacionId;
    private String remitenteId;
    private String contenido;
    private Date fechaEnvio;

    public Mensaje(
            String id,
            String conversacionId,
            String remitenteId,
            String contenido,
            Date fechaEnvio
    ) {
        this.id = id;
        this.conversacionId = conversacionId;
        this.remitenteId = remitenteId;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(
            String conversacionId
    ) {
        this.conversacionId = conversacionId;
    }

    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(
            String remitenteId
    ) {
        this.remitenteId = remitenteId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(
            String contenido
    ) {
        this.contenido = contenido;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(
            Date fechaEnvio
    ) {
        this.fechaEnvio = fechaEnvio;
    }
}