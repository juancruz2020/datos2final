package org.example.mongoDB.model;

import java.util.Date;

public class Reporte {

    private String id;
    private String usuarioId;
    private String tipo;
    private Date fechaGeneracion;
    private String formato;
    private String estado;

    public Reporte(String id, String usuarioId, String tipo,
                   Date fechaGeneracion, String formato, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.formato = formato;
        this.estado = estado;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }


    public String getTipo() {
        return tipo;
    }


    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }


    public String getFormato() {
        return formato;
    }


    public String getEstado() {
        return estado;
    }

}