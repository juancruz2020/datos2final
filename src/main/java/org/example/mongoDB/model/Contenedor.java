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


    public void setCodigoInternacional(String codigoInternacional) {
        this.codigoInternacional = codigoInternacional;
    }

    public String getTipo() {
        return tipo;
    }


    public double getCapacidad() {
        return capacidad;
    }


    public String getEstado() {
        return estado;
    }

}