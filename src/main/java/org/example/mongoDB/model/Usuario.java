package org.example.mongoDB.model;

import java.util.Date;

public class Usuario {

    private String id;
    private String clienteId;
    private String rolId;
    private String nombre;
    private String apellido;
    private String email;
    private String contraseñaEncriptada;
    private String estado;
    private Date fechaRegistro;

    public Usuario(
            String id,
            String clienteId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseñaEncriptada,
            String estado,
            Date fechaRegistro
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.rolId = rolId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contraseñaEncriptada = contraseñaEncriptada;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }


    public String getRolId() {
        return rolId;
    }


    public String getNombre() {
        return nombre;
    }


    public String getApellido() {
        return apellido;
    }


    public String getEmail() {
        return email;
    }


    public String getContraseñaEncriptada() {
        return contraseñaEncriptada;
    }


    public String getEstado() {
        return estado;
    }


    public Date getFechaRegistro() {
        return fechaRegistro;
    }

}
