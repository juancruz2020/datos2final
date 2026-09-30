package org.example.neo4j.model;

public class Cliente {

    private String id;
    private String razonSocial;
    private String cuit;

    public Cliente(String id, String razonSocial, String cuit) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
    }

    public String getId() {
        return id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getCuit() {
        return cuit;
    }
}