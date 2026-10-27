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


    public void setId(String id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }


    public String getEventoId() {
        return eventoId;
    }


    public String getTipo() {
        return tipo;
    }


    public Date getFecha() {
        return fecha;
    }


    public String getEstado() {
        return estado;
    }

}