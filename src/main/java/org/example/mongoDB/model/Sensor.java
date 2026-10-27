
package org.example.mongoDB.model;

import java.util.Date;

public class Sensor {

    private String id;
    private String codigo;
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
        this.codigo = null;
        this.contenedorId = contenedorId;
        this.tipo = tipo;
        this.fabricante = fabricante;
        this.fechaInstalacion = fechaInstalacion;
        this.estado = estado;
    }


    // =========================
    // GETTERS Y SETTERS
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getContenedorId() {
        return contenedorId;
    }


    public String getTipo() {
        return tipo;
    }


    public String getFabricante() {
        return fabricante;
    }


    public Date getFechaInstalacion() {
        return fechaInstalacion;
    }


    public String getEstado() {
        return estado;
    }

}
