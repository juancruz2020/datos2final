
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.mongoDB.model.Usuario;

import java.util.Date;
import java.util.List;

public class UsuarioService {

    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private final ClienteMongoDAO clienteDAO;

    // =========================
    // CONSTRUCTOR
    // =========================

    public UsuarioService() {
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
        this.clienteDAO = new ClienteMongoDAO();
    }

    // =========================
    // CREAR USUARIO
    // =========================

    public Usuario crearUsuario(
            String clienteId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseña
    ) {

        validarTexto(nombre, "El nombre es obligatorio.");
        validarTexto(apellido, "El apellido es obligatorio.");
        validarTexto(email, "El email es obligatorio.");
        validarTexto(contraseña, "La contraseña es obligatoria.");

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );

        String emailNormalizado = email.trim().toLowerCase();

        if (!emailNormalizado.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$"
        )) {
            throw new IllegalArgumentException(
                    "Ingresá un email válido. Ejemplo: usuario@gmail.com"
            );
        }


        // VALIDAR EMAIL REPETIDO

        if (usuarioDAO.buscarPorEmail(emailNormalizado) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese email."
            );
        }

        // OBTENER ROL

        Document rol = rolDAO.buscarPorId(rolId);

        if (rol == null) {
            throw new IllegalArgumentException(
                    "El rol indicado no existe."
            );
        }

        String descripcionRol = rol.getString("descripcion");

        validarTexto(
                descripcionRol,
                "El rol no tiene una descripción válida."
        );

        // VALIDAR CLIENTE SEGÚN ROL

        String clienteIdFinal = normalizarClienteId(
                clienteId,
                descripcionRol
        );

        // VALIDAR EXISTENCIA Y CUENTA ÚNICA

        if (clienteIdFinal != null) {

            if (!clienteDAO.existePorId(clienteIdFinal)) {
                throw new IllegalArgumentException(
                        "El cliente seleccionado no existe."
                );
            }

            if (usuarioDAO.existeUsuarioPorCliente(clienteIdFinal)) {
                throw new IllegalArgumentException(
                        "Este cliente ya tiene una cuenta de usuario asociada."
                );
            }
        }

        // MANTENEMOS EL COMPORTAMIENTO ACTUAL
        // DE CONTRASEÑAS DE PRUEBA SIN ENCRIPTAR

        Usuario usuario = new Usuario(
                null,
                clienteIdFinal,
                rolId,
                nombre.trim(),
                apellido.trim(),
                emailNormalizado,
                contraseña,
                "ACTIVO",
                new Date()
        );

        usuarioDAO.agregar(usuario);
        AppLog.info(UsuarioService.class, "Usuario creado.");

        return usuario;
    }


    // =========================
    // ELIMINAR USUARIO
    // =========================

    public void eliminarUsuario(String usuarioId) {

        validarUsuarioExistente(usuarioId);

        usuarioDAO.eliminar(usuarioId);
        AppLog.info(UsuarioService.class, "Usuario eliminado.");
    }


    // =========================
    // BUSCAR POR EMAIL
    // =========================

    public Document buscarPorEmail(String email) {

        validarTexto(
                email,
                "El email es obligatorio."
        );

        return TablaConsola.mostrar("UsuarioService.buscarPorEmail", usuarioDAO.buscarPorEmail(
                email.trim().toLowerCase()
        ));
    }

    // =========================
    // LISTAR USUARIOS
    // =========================

    public List<Document> listarUsuarios() {
        return TablaConsola.mostrar("UsuarioService.listarUsuarios", usuarioDAO.listarTodos());
    }


    // =========================
    // VALIDAR USUARIO EXISTENTE
    // =========================

    private void validarUsuarioExistente(String usuarioId) {

        validarObjectId(
                usuarioId,
                "El ID del usuario no es válido."
        );

        if (!usuarioDAO.existePorId(usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario no existe."
            );
        }
    }

    // =========================
    // VALIDAR OBJECT ID
    // =========================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(mensaje);
        }
    }

    // =========================
    // VALIDAR TEXTO
    // =========================

    private void validarTexto(
            String valor,
            String mensaje
    ) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    // =========================
    // VERIFICAR ROL SIN CLIENTE
    // =========================

    private boolean esRolSinCliente(String descripcionRol) {

        return "Admin".equalsIgnoreCase(descripcionRol)
                || "Administrador".equalsIgnoreCase(descripcionRol)
                || "Operador".equalsIgnoreCase(descripcionRol);
    }

    // =========================
    // NORMALIZAR CLIENTE SEGÚN ROL
    // =========================

    private String normalizarClienteId(
            String clienteId,
            String descripcionRol
    ) {

        // CLIENTE: EMPRESA OBLIGATORIA

        if ("Cliente".equalsIgnoreCase(descripcionRol)) {

            validarObjectId(
                    clienteId,
                    "El usuario con rol Cliente debe tener una empresa asociada."
            );

            return clienteId;
        }

        // ADMIN Y OPERADOR: SIN EMPRESA

        if (esRolSinCliente(descripcionRol)) {
            return null;
        }

        // OTROS ROLES

        if (clienteId == null || clienteId.isBlank()) {
            return null;
        }

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        return clienteId;
    }
}
