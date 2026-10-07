package org.example.mongoDB.model;

import java.util.Date;
import java.util.List;

public class Conversacion {

    private String id;
    private String nombre;
    private String tipo;
    private List<String> participantesIds;
    private Date fechaCreacion;

    public Conversacion(
            String id,
            String nombre,
            String tipo,
            List<String> participantesIds,
            Date fechaCreacion
    ) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.participantesIds = participantesIds;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getParticipantesIds() {
        return participantesIds;
    }

    public void setParticipantesIds(
            List<String> participantesIds
    ) {
        this.participantesIds = participantesIds;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            Date fechaCreacion
    ) {
        this.fechaCreacion = fechaCreacion;
    }
}