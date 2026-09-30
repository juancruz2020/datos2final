package org.example.neo4j.model;

public class Envio {

    private String id;
    private String estado;
    private String prioridad;

    public Envio(
            String id,
            String estado,
            String prioridad
    ) {
        this.id = id;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public String getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getPrioridad() {
        return prioridad;
    }
}