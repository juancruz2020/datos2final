
package org.example.mongoDB.model;

public class Cliente {

    private String id;
    private String codigo;
    private String razonSocial;
    private String cuit;
    private String email;
    private String telefono;
    private Direccion direccion;
    private String pais;
    private String estado;

    // =========================
    // CONSTRUCTOR ORIGINAL
    // =========================

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
        this.codigo = null;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.pais = pais;
        this.estado = estado;
    }


    // =========================
    // GETTERS Y SETTERS
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getRazonSocial() {
        return razonSocial;
    }


    public String getCuit() {
        return cuit;
    }


    public String getEmail() {
        return email;
    }


    public String getTelefono() {
        return telefono;
    }


    public Direccion getDireccion() {
        return direccion;
    }


    public String getPais() {
        return pais;
    }


    public String getEstado() {
        return estado;
    }

}
