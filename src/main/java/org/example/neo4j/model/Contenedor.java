package org.example.neo4j.model;

public class Contenedor {

    private final String id;
    private final String codigo;
    private final String tipo;

    public Contenedor(
            String id,
            String codigo,
            String tipo
    ) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }
}