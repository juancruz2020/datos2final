package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.mongoDB.model.Usuario;

import java.util.Date;
import java.util.List;

public class UsuarioService {

    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UsuarioService() {

        this.usuarioDAO =
                new UsuarioMongoDAO();

        this.rolDAO =
                new RolMongoDAO();
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

        // -----------------------------------------------------
        // VALIDAR DATOS OBLIGATORIOS
        // -----------------------------------------------------

        validarTexto(
                nombre,
                "El nombre es obligatorio."
        );

        validarTexto(
                apellido,
                "El apellido es obligatorio."
        );

        validarTexto(
                email,
                "El email es obligatorio."
        );

        validarTexto(
                contraseña,
                "La contraseña es obligatoria."
        );

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );


        // -----------------------------------------------------
        // NORMALIZAR EMAIL
        // -----------------------------------------------------

        String emailNormalizado =
                email.trim().toLowerCase();


        // -----------------------------------------------------
        // VERIFICAR EMAIL REPETIDO
        // -----------------------------------------------------

        Document existente =
                usuarioDAO.buscarPorEmail(
                        emailNormalizado
                );

        if (existente != null) {

            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese email."
            );
        }


        // -----------------------------------------------------
        // OBTENER ROL
        // -----------------------------------------------------

        Document rol =
                rolDAO.buscarPorId(
                        rolId
                );

        if (rol == null) {

            throw new IllegalArgumentException(
                    "El rol indicado no existe."
            );
        }


        String descripcionRol =
                rol.getString(
                        "descripcion"
                );

        if (descripcionRol == null
                || descripcionRol.isBlank()) {

            throw new IllegalStateException(
                    "El rol no tiene una descripción válida."
            );
        }


        // -----------------------------------------------------
        // VALIDAR CLIENTE SEGÚN ROL
        // -----------------------------------------------------

        String clienteIdFinal =
                normalizarClienteId(
                        clienteId,
                        descripcionRol
                );


        // -----------------------------------------------------
        // GUARDAR CONTRASEÑA SIN ENCRIPTAR
        // -----------------------------------------------------

        String contraseñaEncriptada =
                contraseña;


        // -----------------------------------------------------
        // CREAR MODELO
        // -----------------------------------------------------

        Usuario usuario =
                new Usuario(
                        null,
                        clienteIdFinal,
                        rolId,
                        nombre.trim(),
                        apellido.trim(),
                        emailNormalizado,
                        contraseñaEncriptada,
                        "ACTIVO",
                        new Date()
                );


        // -----------------------------------------------------
        // GUARDAR EN MONGODB
        // -----------------------------------------------------

        usuarioDAO.agregar(
                usuario
        );


        return usuario;
    }


    // =========================================================
    // CAMBIAR CONTRASEÑA
    // =========================================================

    public void cambiarContraseña(
            String usuarioId,
            String nuevaContraseña
    ) {

        validarUsuarioExistente(
                usuarioId
        );

        validarTexto(
                nuevaContraseña,
                "La nueva contraseña es obligatoria."
        );


        // -----------------------------------------------------
        // GUARDAR NUEVA CONTRASEÑA SIN ENCRIPTAR
        // -----------------------------------------------------

        String contraseñaEncriptada =
                nuevaContraseña;


        usuarioDAO.cambiarContraseña(
                usuarioId,
                contraseñaEncriptada
        );
    }


    // =========================================================
    // CAMBIAR ROL
    // =========================================================

    public void cambiarRol(
            String usuarioId,
            String rolId
    ) {

        validarUsuarioExistente(
                usuarioId
        );

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );


        Document rol =
                rolDAO.buscarPorId(
                        rolId
                );


        if (rol == null) {

            throw new IllegalArgumentException(
                    "El rol indicado no existe."
            );
        }


        // -----------------------------------------------------
        // SI SE CAMBIA A CLIENTE,
        // DEBE TENER CLIENTE ASOCIADO
        // -----------------------------------------------------

        String descripcionRol =
                rol.getString(
                        "descripcion"
                );


        if ("Cliente".equalsIgnoreCase(
                descripcionRol
        )) {

            Document usuario =
                    usuarioDAO.buscarPorId(
                            usuarioId
                    );


            ObjectId clienteId =
                    usuario.getObjectId(
                            "cliente_id"
                    );


            if (clienteId == null) {

                throw new IllegalArgumentException(
                        "No se puede asignar el rol Cliente porque el usuario no tiene un cliente asociado."
                );
            }
        }


        usuarioDAO.cambiarRol(
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

        validarUsuarioExistente(
                usuarioId
        );


        usuarioDAO.cambiarEstado(
                usuarioId,
                "ACTIVO"
        );
    }


    // =========================================================
    // DESACTIVAR USUARIO
    // =========================================================

    public void desactivarUsuario(
            String usuarioId
    ) {

        validarUsuarioExistente(
                usuarioId
        );


        usuarioDAO.cambiarEstado(
                usuarioId,
                "INACTIVO"
        );
    }


    // =========================================================
    // ELIMINAR USUARIO
    // =========================================================

    public void eliminarUsuario(
            String usuarioId
    ) {

        validarUsuarioExistente(
                usuarioId
        );


        usuarioDAO.eliminar(
                usuarioId
        );
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String usuarioId
    ) {

        validarUsuarioExistente(
                usuarioId
        );


        return usuarioDAO.buscarPorId(
                usuarioId
        );
    }


    // =========================================================
    // BUSCAR POR EMAIL
    // =========================================================

    public Document buscarPorEmail(
            String email
    ) {

        validarTexto(
                email,
                "El email es obligatorio."
        );


        return usuarioDAO.buscarPorEmail(
                email.trim().toLowerCase()
        );
    }


    // =========================================================
    // LISTAR USUARIOS
    // =========================================================

    public List<Document> listarUsuarios() {

        return usuarioDAO.listarTodos();
    }


    // =========================================================
    // LISTAR POR CLIENTE
    // =========================================================

    public List<Document> listarPorCliente(
            String clienteId
    ) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        return usuarioDAO.buscarPorCliente(
                clienteId
        );
    }


    // =========================================================
    // LISTAR POR ROL
    // =========================================================

    public List<Document> listarPorRol(
            String rolId
    ) {

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );


        return usuarioDAO.buscarPorRol(
                rolId
        );
    }


    // =========================================================
    // VALIDAR USUARIO EXISTENTE
    // =========================================================

    private void validarUsuarioExistente(
            String usuarioId
    ) {

        validarObjectId(
                usuarioId,
                "El ID del usuario no es válido."
        );


        if (!usuarioDAO.existePorId(
                usuarioId
        )) {

            throw new IllegalArgumentException(
                    "El usuario no existe."
            );
        }
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }


    // =========================================================
    // VALIDAR TEXTO
    // =========================================================

    private void validarTexto(
            String valor,
            String mensaje
    ) {

        if (valor == null
                || valor.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }


    // =========================================================
    // NORMALIZAR CLIENTE SEGÚN ROL
    // =========================================================

    private String normalizarClienteId(
            String clienteId,
            String descripcionRol
    ) {

        // -----------------------------------------------------
        // CLIENTE
        // cliente_id obligatorio
        // -----------------------------------------------------

        if ("Cliente".equalsIgnoreCase(
                descripcionRol
        )) {

            validarObjectId(
                    clienteId,
                    "El usuario con rol Cliente debe tener un cliente asociado."
            );

            return clienteId;
        }


        // -----------------------------------------------------
        // ADMIN / OPERADOR
        // cliente_id opcional
        // -----------------------------------------------------

        if (clienteId == null
                || clienteId.isBlank()) {

            return null;
        }


        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        return clienteId;
    }
}