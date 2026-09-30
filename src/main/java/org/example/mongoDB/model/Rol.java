package org.example.mongoDB.model;

public class Rol {

    private String id;
    private String descripcion;
    private int nivelPermisos;

    public Rol(String id, String descripcion, int nivelPermisos) {
        this.id = id;
        this.descripcion = descripcion;
        this.nivelPermisos = nivelPermisos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNivelPermisos() {
        return nivelPermisos;
    }

    public void setNivelPermisos(int nivelPermisos) {
        this.nivelPermisos = nivelPermisos;
    }
}