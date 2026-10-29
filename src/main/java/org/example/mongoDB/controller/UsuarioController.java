
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
        this.usuarioService = new UsuarioService();
    }

    // =========================================================
    // CREAR USUARIO
    // =========================================================

    public Usuario crearUsuario(
            String clienteId,
            String proveedorId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseña
    ) {
        return usuarioService.crearUsuario(
                clienteId,
                proveedorId,
                rolId,
                nombre,
                apellido,
                email,
                contraseña
        );
    }

    // =========================================================
    // MODIFICAR USUARIO
    // =========================================================

    public void modificarUsuario(
            String usuarioId,
            String clienteId,
            String proveedorId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseña,
            String estado
    ) {
        usuarioService.modificarUsuario(
                usuarioId,
                clienteId,
                proveedorId,
                rolId,
                nombre,
                apellido,
                email,
                contraseña,
                estado
        );
    }

    // =========================================================
    // ELIMINAR USUARIO
    // =========================================================

    public void eliminarUsuario(String usuarioId) {
        usuarioService.eliminarUsuario(usuarioId);
    }

    // =========================================================
    // BUSCAR USUARIO POR EMAIL
    // =========================================================

    public Document buscarPorEmail(String email) {
        return usuarioService.buscarPorEmail(email);
    }

    // =========================================================
    // LISTAR TODOS LOS USUARIOS
    // =========================================================

    public List<Document> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }
}
