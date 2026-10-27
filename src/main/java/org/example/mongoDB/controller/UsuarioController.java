package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.model.Usuario;
import org.example.mongoDB.service.UsuarioService;

import java.util.List;

class UsuarioController {

    private final UsuarioService usuarioService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    UsuarioController() {

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


}