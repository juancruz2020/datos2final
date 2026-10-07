package org.example.mongoDB.model;

public class Vehiculo {

    private String id;
    private String tipo;
    private String identificacion;
    private String empresaOperadora;
    private String estado;

    public Vehiculo(
            String id,
            String tipo,
            String identificacion,
            String empresaOperadora,
            String estado
    ) {
        this.id = id;
        this.tipo = tipo;
        this.identificacion = identificacion;
        this.empresaOperadora = empresaOperadora;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getEmpresaOperadora() {
        return empresaOperadora;
    }

    public void setEmpresaOperadora(String empresaOperadora) {
        this.empresaOperadora = empresaOperadora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}