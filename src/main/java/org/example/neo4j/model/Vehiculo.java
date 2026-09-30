package org.example.neo4j.model;

public class Vehiculo {

    private final String id;
    private final String patente;
    private final String tipo;

    public Vehiculo(String id, String patente, String tipo) {
        this.id = id;
        this.patente = patente;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public String getTipo() {
        return tipo;
    }
}