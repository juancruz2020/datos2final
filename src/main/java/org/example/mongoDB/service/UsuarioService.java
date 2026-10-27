
package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
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

        return usuario;
    }

    // =========================
    // CAMBIAR CONTRASEÑA
    // =========================

    public void cambiarContraseña(
            String usuarioId,
            String nuevaContraseña
    ) {

        validarUsuarioExistente(usuarioId);

        validarTexto(
                nuevaContraseña,
                "La nueva contraseña es obligatoria."
        );

        usuarioDAO.cambiarContraseña(
                usuarioId,
                nuevaContraseña
        );
    }

    // =========================
    // CAMBIAR ROL
    // =========================

    public void cambiarRol(
            String usuarioId,
            String rolId
    ) {

        validarUsuarioExistente(usuarioId);

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );

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

        Document usuario = usuarioDAO.buscarPorId(usuarioId);

        ObjectId clienteId = usuario.getObjectId("cliente_id");

        // SI CAMBIA A CLIENTE

        if ("Cliente".equalsIgnoreCase(descripcionRol)) {

            if (clienteId == null) {
                throw new IllegalArgumentException(
                        "No se puede asignar el rol Cliente porque el usuario no tiene una empresa asociada."
                );
            }

            String clienteIdString = clienteId.toHexString();

            if (!clienteDAO.existePorId(clienteIdString)) {
                throw new IllegalArgumentException(
                        "La empresa asociada al usuario no existe."
                );
            }

            List<Document> usuariosDelCliente =
                    usuarioDAO.buscarPorCliente(clienteIdString);

            for (Document otroUsuario : usuariosDelCliente) {

                if (!otroUsuario.getObjectId("_id")
                        .equals(new ObjectId(usuarioId))) {

                    throw new IllegalArgumentException(
                            "Este cliente ya tiene otra cuenta de usuario asociada."
                    );
                }
            }
        }

        // SI CAMBIA A ADMIN U OPERADOR,
        // SE QUITA LA EMPRESA ASOCIADA

        if (esRolSinCliente(descripcionRol)) {

            Usuario usuarioActualizado = new Usuario(
                    usuarioId,
                    null,
                    rolId,
                    usuario.getString("nombre"),
                    usuario.getString("apellido"),
                    usuario.getString("email"),
                    usuario.getString("contraseña_encriptada"),
                    usuario.getString("estado"),
                    usuario.getDate("fecha_registro")
            );

            usuarioDAO.modificar(usuarioActualizado);

            return;
        }

        usuarioDAO.cambiarRol(usuarioId, rolId);
    }

    // =========================
    // ACTIVAR USUARIO
    // =========================

    public void activarUsuario(String usuarioId) {

        validarUsuarioExistente(usuarioId);

        usuarioDAO.cambiarEstado(
                usuarioId,
                "ACTIVO"
        );
    }

    // =========================
    // DESACTIVAR USUARIO
    // =========================

    public void desactivarUsuario(String usuarioId) {

        validarUsuarioExistente(usuarioId);

        usuarioDAO.cambiarEstado(
                usuarioId,
                "INACTIVO"
        );
    }

    // =========================
    // ELIMINAR USUARIO
    // =========================

    public void eliminarUsuario(String usuarioId) {

        validarUsuarioExistente(usuarioId);

        usuarioDAO.eliminar(usuarioId);
    }

    // =========================
    // BUSCAR POR ID
    // =========================

    public Document buscarPorId(String usuarioId) {

        validarUsuarioExistente(usuarioId);

        return usuarioDAO.buscarPorId(usuarioId);
    }

    // =========================
    // BUSCAR POR EMAIL
    // =========================

    public Document buscarPorEmail(String email) {

        validarTexto(
                email,
                "El email es obligatorio."
        );

        return usuarioDAO.buscarPorEmail(
                email.trim().toLowerCase()
        );
    }

    // =========================
    // LISTAR USUARIOS
    // =========================

    public List<Document> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    // =========================
    // LISTAR POR CLIENTE
    // =========================

    public List<Document> listarPorCliente(String clienteId) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        return usuarioDAO.buscarPorCliente(clienteId);
    }

    // =========================
    // LISTAR POR ROL
    // =========================

    public List<Document> listarPorRol(String rolId) {

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );

        return usuarioDAO.buscarPorRol(rolId);
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
