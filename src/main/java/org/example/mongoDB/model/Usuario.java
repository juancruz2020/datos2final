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

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getRolId() {
        return rolId;
    }

    public void setRolId(String rolId) {
        this.rolId = rolId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseñaEncriptada() {
        return contraseñaEncriptada;
    }

    public void setContraseñaEncriptada(String contraseñaEncriptada) {
        this.contraseñaEncriptada = contraseñaEncriptada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}