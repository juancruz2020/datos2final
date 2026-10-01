package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.model.Usuario;
import org.example.mongoDB.service.UsuarioService;

import java.util.List;

public class UsuarioController {

    private final UsuarioService usuarioService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UsuarioController() {

        this.usuarioService =
                new UsuarioService();
    }


    // =========================================================
    // CREAR USUARIO
    // =========================================================

    public Usuario crearUsuario(
            String clienteId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseña
    ) {

        return usuarioService.crearUsuario(
                clienteId,
                rolId,
                nombre,
                apellido,
                email,
                contraseña
        );
    }


    // =========================================================
    // CAMBIAR CONTRASEÑA
    // =========================================================

    public void cambiarContraseña(
            String usuarioId,
            String nuevaContraseña
    ) {

        usuarioService.cambiarContraseña(
                usuarioId,
                nuevaContraseña
        );
    }


    // =========================================================
    // CAMBIAR ROL
    // =========================================================

    public void cambiarRol(
            String usuarioId,
            String rolId
    ) {

        usuarioService.cambiarRol(
                usuarioId,
                rolId
        );
    }


    // =========================================================
    // ACTIVAR USUARIO
    // =========================================================

    public void activarUsuario(
            String usuarioId
    ) {

        usuarioService.activarUsuario(
                usuarioId
        );
    }


    // =========================================================
    // DESACTIVAR USUARIO
    // =========================================================

    public void desactivarUsuario(
            String usuarioId
    ) {

        usuarioService.desactivarUsuario(
                usuarioId
        );
    }


    // =========================================================
    // ELIMINAR USUARIO
    // =========================================================

    public void eliminarUsuario(
            String usuarioId
    ) {

        usuarioService.eliminarUsuario(
                usuarioId
        );
    }


    // =========================================================
    // BUSCAR USUARIO POR ID
    // =========================================================

    public Document buscarPorId(
            String usuarioId
    ) {

        return usuarioService.buscarPorId(
                usuarioId
        );
    }


    // =========================================================
    // BUSCAR USUARIO POR EMAIL
    // =========================================================

    public Document buscarPorEmail(
            String email
    ) {

        return usuarioService.buscarPorEmail(
                email
        );
    }


    // =========================================================
    // LISTAR TODOS LOS USUARIOS
    // =========================================================

    public List<Document> listarUsuarios() {

        return usuarioService.listarUsuarios();
    }


    // =========================================================
    // LISTAR USUARIOS POR CLIENTE
    // =========================================================

    public List<Document> listarPorCliente(
            String clienteId
    ) {

        return usuarioService.listarPorCliente(
                clienteId
        );
    }


    // =========================================================
    // LISTAR USUARIOS POR ROL
    // =========================================================

    public List<Document> listarPorRol(
            String rolId
    ) {

        return usuarioService.listarPorRol(
                rolId
        );
    }
}