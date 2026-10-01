package org.example.usuarios.service;

import org.bson.Document;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.mindrot.jbcrypt.BCrypt;

public class AutenticacionService {

    private final UsuarioMongoDAO usuarioDAO;

    public AutenticacionService() {
        this.usuarioDAO = new UsuarioMongoDAO();
    }


    // =========================
    // AUTENTICAR USUARIO
    // =========================

    public Document autenticar(String email, String contraseña) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio"
            );
        }

        if (contraseña == null || contraseña.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }

        Document usuario = usuarioDAO.buscarPorEmail(email.trim());

        if (usuario == null) {
            return null;
        }

        String estado = usuario.getString("estado");

        if (estado == null || !"ACTIVO".equalsIgnoreCase(estado)) {
            throw new SecurityException(
                    "El usuario no se encuentra activo"
            );
        }

        String contraseñaEncriptada =
                usuario.getString("contraseña_encriptada");

        if (contraseñaEncriptada == null ||
                contraseñaEncriptada.isBlank()) {
            throw new IllegalStateException(
                    "El usuario no tiene una contraseña configurada"
            );
        }

        boolean contraseñaCorrecta;

        try {
            contraseñaCorrecta = BCrypt.checkpw(
                    contraseña,
                    contraseñaEncriptada
            );
        } catch (IllegalArgumentException e) {

            // El valor almacenado no es un hash BCrypt válido.
            return null;
        }

        if (!contraseñaCorrecta) {
            return null;
        }

        return usuario;
    }


    // =========================
    // ENCRIPTAR CONTRASEÑA
    // =========================

    public String encriptarContraseña(String contraseña) {

        if (contraseña == null || contraseña.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }

        return BCrypt.hashpw(
                contraseña,
                BCrypt.gensalt()
        );
    }
}