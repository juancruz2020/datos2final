package org.example.mongoDB.model;

public class Contenedor {

    private String id;
    private String codigoInternacional;
    private String tipo;
    private double capacidad;
    private String estado;

    public Contenedor(
            String id,
            String codigoInternacional,
            String tipo,
            double capacidad,
            String estado
    ) {
        this.id = id;
        this.codigoInternacional = codigoInternacional;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoInternacional() {
        return codigoInternacional;
    }

    public void setCodigoInternacional(String codigoInternacional) {
        this.codigoInternacional = codigoInternacional;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(double capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}