package org.example.mongoDB.model;

public class Cliente {

    private String id;
    private String razonSocial;
    private String cuit;
    private String email;
    private String telefono;
    private Direccion direccion;
    private String pais;
    private String estado;

    public Cliente(
            String id,
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            Direccion direccion,
            String pais,
            String estado
    ) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.pais = pais;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}