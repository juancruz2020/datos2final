package org.example.neo4j.model;

public class Ubicacion {

    private final String id;
    private final String ciudad;
    private final String pais;

    public Ubicacion(
            String id,
            String ciudad,
            String pais
    ) {
        this.id = id;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    public String getId() {
        return id;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getPais() {
        return pais;
    }
}