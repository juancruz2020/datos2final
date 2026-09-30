package org.example.neo4j.model;

public class Sensor {

    private final String id;
    private final String tipo;

    public Sensor(String id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }
}