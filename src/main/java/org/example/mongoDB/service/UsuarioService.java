
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.dao.ProveedorMongoDAO;
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
    private final ProveedorMongoDAO proveedorDAO;

    // =========================
    // CONSTRUCTOR
    // =========================

    public UsuarioService() {
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
        this.clienteDAO = new ClienteMongoDAO();
        this.proveedorDAO = new ProveedorMongoDAO();
    }

    // =========================
    // CREAR USUARIO
    // MÉTODO ORIGINAL
    // =========================

    public Usuario crearUsuario(
            String clienteId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseña
    ) {

        return crearUsuario(
                clienteId,
                null,
                rolId,
                nombre,
                apellido,
                email,
                contraseña
        );
    }

    // =========================
    // CREAR USUARIO
    // CON PROVEEDOR
    // =========================

    public Usuario crearUsuario(
            String clienteId,
            String proveedorId,
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

        // =========================
        // VALIDAR EMAIL REPETIDO
        // =========================

        if (usuarioDAO.buscarPorEmail(emailNormalizado) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese email."
            );
        }

        // =========================
        // OBTENER ROL
        // =========================

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

        // =========================
        // VALIDAR ASOCIACIONES SEGÚN ROL
        // =========================

        String clienteIdFinal = normalizarClienteId(
                clienteId,
                descripcionRol
        );

        String proveedorIdFinal = normalizarProveedorId(
                proveedorId,
                descripcionRol
        );

        // =========================
        // VALIDAR CLIENTE
        // =========================

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

        // =========================
        // VALIDAR PROVEEDOR
        // =========================

        if (proveedorIdFinal != null) {

            if (!proveedorDAO.existePorId(proveedorIdFinal)) {
                throw new IllegalArgumentException(
                        "El proveedor seleccionado no existe."
                );
            }

            if (usuarioDAO.existeUsuarioPorProveedor(proveedorIdFinal)) {
                throw new IllegalArgumentException(
                        "Este proveedor ya tiene una cuenta de usuario asociada."
                );
            }
        }

        // MANTENEMOS EL COMPORTAMIENTO ACTUAL
        // DE CONTRASEÑAS DE PRUEBA SIN ENCRIPTAR

        Usuario usuario = new Usuario(
                null,
                clienteIdFinal,
                proveedorIdFinal,
                rolId,
                nombre.trim(),
                apellido.trim(),
                emailNormalizado,
                contraseña,
                "ACTIVO",
                new Date()
        );

        usuarioDAO.agregar(usuario);

        AppLog.info(
                UsuarioService.class,
                "Usuario creado."
        );

        return usuario;
    }

    // =========================
    // MODIFICAR USUARIO
    // MÉTODO ORIGINAL
    // =========================

    public void modificarUsuario(
            String usuarioId,
            String clienteId,
            String rolId,
            String nombre,
            String apellido,
            String email,
            String contraseña,
            String estado
    ) {

        validarUsuarioExistente(usuarioId);

        // Recuperamos la asociación original del proveedor
        // para no perderla al usar el método existente.

        Document actual = usuarioDAO.buscarPorId(usuarioId);

        ObjectId proveedorAsociado =
                actual.getObjectId("proveedor_id");

        String proveedorId = proveedorAsociado == null
                ? null
                : proveedorAsociado.toHexString();

        modificarUsuario(
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

    // =========================
    // MODIFICAR USUARIO
    // CON PROVEEDOR
    // =========================

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

        validarUsuarioExistente(usuarioId);

        validarTexto(nombre, "El nombre es obligatorio.");
        validarTexto(apellido, "El apellido es obligatorio.");
        validarTexto(email, "El email es obligatorio.");
        validarTexto(estado, "El estado es obligatorio.");

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );

        Document actual = usuarioDAO.buscarPorId(usuarioId);
        Document rol = rolDAO.buscarPorId(rolId);

        if (rol == null) {
            throw new IllegalArgumentException(
                    "El rol indicado no existe."
            );
        }

        // =========================
        // VALIDAR EMAIL
        // =========================

        String emailNormalizado = email.trim().toLowerCase();

        if (!emailNormalizado.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$"
        )) {
            throw new IllegalArgumentException(
                    "Ingresá un email válido."
            );
        }

        Document otroUsuario =
                usuarioDAO.buscarPorEmail(emailNormalizado);

        if (otroUsuario != null
                && !otroUsuario.getObjectId("_id")
                .toHexString().equals(usuarioId)) {

            throw new IllegalArgumentException(
                    "Ya existe otro usuario con ese email."
            );
        }

        // =========================
        // VALIDAR ROL
        // =========================

        String descripcionRol =
                rol.getString("descripcion");

        validarTexto(
                descripcionRol,
                "El rol no tiene una descripción válida."
        );

        // =========================
        // NORMALIZAR ASOCIACIONES
        // =========================

        String clienteIdFinal = normalizarClienteId(
                clienteId,
                descripcionRol
        );

        String proveedorIdFinal = normalizarProveedorId(
                proveedorId,
                descripcionRol
        );

        // =========================
        // VALIDAR CLIENTE
        // =========================

        if (clienteIdFinal != null) {

            if (!clienteDAO.existePorId(clienteIdFinal)) {
                throw new IllegalArgumentException(
                        "El cliente seleccionado no existe."
                );
            }

            for (Document usuario : usuarioDAO.listarTodos()) {

                ObjectId asociado =
                        usuario.getObjectId("cliente_id");

                if (asociado != null
                        && asociado.toHexString().equals(clienteIdFinal)
                        && !usuario.getObjectId("_id")
                        .toHexString().equals(usuarioId)) {

                    throw new IllegalArgumentException(
                            "Este cliente ya tiene otro usuario asociado."
                    );
                }
            }
        }

        // =========================
        // VALIDAR PROVEEDOR
        // =========================

        if (proveedorIdFinal != null) {

            if (!proveedorDAO.existePorId(proveedorIdFinal)) {
                throw new IllegalArgumentException(
                        "El proveedor seleccionado no existe."
                );
            }

            for (Document usuario : usuarioDAO.listarTodos()) {

                ObjectId asociado =
                        usuario.getObjectId("proveedor_id");

                if (asociado != null
                        && asociado.toHexString().equals(proveedorIdFinal)
                        && !usuario.getObjectId("_id")
                        .toHexString().equals(usuarioId)) {

                    throw new IllegalArgumentException(
                            "Este proveedor ya tiene otro usuario asociado."
                    );
                }
            }
        }

        // =========================
        // VALIDAR ESTADO
        // =========================

        if (!"ACTIVO".equalsIgnoreCase(estado)
                && !"INACTIVO".equalsIgnoreCase(estado)) {

            throw new IllegalArgumentException(
                    "El estado debe ser ACTIVO o INACTIVO."
            );
        }

        // =========================
        // CONSERVAR CONTRASEÑA
        // =========================

        String contraseñaFinal =
                contraseña == null || contraseña.isBlank()
                        ? actual.getString("contraseña_encriptada")
                        : contraseña;

        // =========================
        // CONSTRUIR USUARIO
        // =========================

        Usuario usuario = new Usuario(
                usuarioId,
                clienteIdFinal,
                proveedorIdFinal,
                rolId,
                nombre.trim(),
                apellido.trim(),
                emailNormalizado,
                contraseñaFinal,
                estado.toUpperCase(),
                actual.getDate("fecha_registro")
        );

        usuarioDAO.modificar(usuario);

        AppLog.info(
                UsuarioService.class,
                "Usuario modificado."
        );
    }

    // =========================
    // ELIMINAR USUARIO
    // =========================

    public void eliminarUsuario(String usuarioId) {

        validarUsuarioExistente(usuarioId);

        usuarioDAO.eliminar(usuarioId);

        AppLog.info(
                UsuarioService.class,
                "Usuario eliminado."
        );
    }

    // =========================
    // BUSCAR POR EMAIL
    // =========================

    public Document buscarPorEmail(String email) {

        validarTexto(
                email,
                "El email es obligatorio."
        );

        return TablaConsola.mostrar(
                "UsuarioService.buscarPorEmail",
                usuarioDAO.buscarPorEmail(
                        email.trim().toLowerCase()
                )
        );
    }

    // =========================
    // LISTAR USUARIOS
    // =========================

    public List<Document> listarUsuarios() {

        return TablaConsola.mostrar(
                "UsuarioService.listarUsuarios",
                usuarioDAO.listarTodos()
        );
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

        // PROVEEDOR: NO TIENE CLIENTE ASOCIADO

        if ("Proveedor".equalsIgnoreCase(descripcionRol)) {
            return null;
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

    // =========================
    // NORMALIZAR PROVEEDOR SEGÚN ROL
    // =========================

    private String normalizarProveedorId(
            String proveedorId,
            String descripcionRol
    ) {

        // PROVEEDOR: ASOCIACIÓN OBLIGATORIA

        if ("Proveedor".equalsIgnoreCase(descripcionRol)) {

            validarObjectId(
                    proveedorId,
                    "El usuario con rol Proveedor "
                            + "debe tener un proveedor asociado."
            );

            return proveedorId;
        }

        // CLIENTE, ADMINISTRADOR Y OPERADOR:
        // NO TIENEN PROVEEDOR ASOCIADO

        return null;
    }
}
