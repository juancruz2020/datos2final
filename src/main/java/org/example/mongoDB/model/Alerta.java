package org.example.mongoDB.model;

import java.util.Date;

public class Alerta {

    private String id;
    private String sensorId;
    private String eventoId;
    private String tipo;
    private Date fecha;
    private String estado;

    public Alerta(
            String id,
            String sensorId,
            String eventoId,
            String tipo,
            Date fecha,
            String estado
    ) {
        this.id = id;
        this.sensorId = sensorId;
        this.eventoId = eventoId;
        this.tipo = tipo;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getEventoId() {
        return eventoId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}