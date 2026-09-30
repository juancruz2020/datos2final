package org.example.neo4j.model;

public class Operador {

    private final String id;
    private final String nombre;

    public Operador(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}