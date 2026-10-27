
package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;

import java.util.Locale;

public class PermisosService {

    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;

    // =========================
    // CONSTRUCTOR
    // =========================

    public PermisosService() {
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
    }

    // =========================
    // OBTENER ROL DEL USUARIO
    // =========================

    public String obtenerRolUsuario(String usuarioId) {

        if (usuarioId == null
                || !ObjectId.isValid(usuarioId)) {

            throw new IllegalArgumentException(
                    "El ID del usuario no es válido"
            );
        }

        Document usuario =
                usuarioDAO.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe"
            );
        }

        ObjectId rolId =
                usuario.getObjectId("rol_id");

        if (rolId == null) {
            throw new IllegalStateException(
                    "El usuario no tiene un rol asignado"
            );
        }

        Document rol =
                rolDAO.buscarPorId(rolId.toHexString());

        if (rol == null) {
            throw new IllegalStateException(
                    "El rol asignado al usuario no existe"
            );
        }

        String descripcion =
                rol.getString("descripcion");

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new IllegalStateException(
                    "El rol no tiene una descripción válida"
            );
        }

        return descripcion.trim();
    }

    // =========================
    // VERIFICAR ADMINISTRADOR
    // =========================

    public boolean esAdmin(String usuarioId) {

        return "Administrador".equalsIgnoreCase(
                obtenerRolUsuario(usuarioId)
        );
    }


    // =========================
    // VERIFICAR CLIENTE
    // =========================

    public boolean esCliente(String usuarioId) {

        return "Cliente".equalsIgnoreCase(
                obtenerRolUsuario(usuarioId)
        );
    }

    // =========================
    // EXIGIR ADMINISTRADOR
    // =========================

    public void exigirAdmin(String usuarioId) {

        if (!esAdmin(usuarioId)) {

            throw new SecurityException(
                    "Esta operación requiere rol Administrador"
            );
        }
    }

    // =========================
    // EXIGIR ADMINISTRADOR
    // U OPERADOR
    // =========================

    public void exigirAdminUOperador(
            String usuarioId
    ) {

        String rol = obtenerRolUsuario(usuarioId);

        boolean permitido =
                "Administrador".equalsIgnoreCase(rol)
                        || "Operador".equalsIgnoreCase(rol);

        if (!permitido) {

            throw new SecurityException(
                    "Esta operación requiere rol Administrador u Operador"
            );
        }
    }


    // =========================
    // OBTENER CLIENTE DEL USUARIO
    // =========================

    public String obtenerClienteId(
            String usuarioId
    ) {

        if (usuarioId == null
                || !ObjectId.isValid(usuarioId)) {

            throw new IllegalArgumentException(
                    "El ID del usuario no es válido"
            );
        }

        Document usuario =
                usuarioDAO.buscarPorId(usuarioId);

        if (usuario == null) {

            throw new IllegalArgumentException(
                    "El usuario no existe"
            );
        }

        ObjectId clienteId =
                usuario.getObjectId("cliente_id");

        if (clienteId == null) {
            return null;
        }

        return clienteId.toHexString();
    }


    // =========================
    // VERIFICAR ACCESO A UNA VISTA
    // =========================

    public boolean puedeAccederVista(
            String usuarioId,
            String vista
    ) {

        if (vista == null || vista.isBlank()) {
            return false;
        }

        String rol = obtenerRolUsuario(usuarioId);

        String vistaNormalizada =
                vista.trim().toUpperCase(Locale.ROOT);

        // =========================
        // ADMINISTRADOR
        // =========================

        if ("Administrador".equalsIgnoreCase(rol)) {
            return true;
        }

        // =========================
        // OPERADOR
        // =========================

        if ("Operador".equalsIgnoreCase(rol)) {

            return switch (vistaNormalizada) {

                case "ENVIOS",
                     "CLIENTES",
                     "CONTENEDORES",
                     "SENSORES",
                     "MONITOREO",
                     "TRAZABILIDAD",
                     "SEGUIMIENTO",
                     "SEGUIMIENTO_GPS",
                     "SEGUIMIENTO_ENVIO",
                     "COMUNICACIONES",
                     "RIESGOS",
                     "FACTURACION",
                     "CUENTAS_CORRIENTES",
                     "VEHICULOS",
                     "EVENTOS_LOGISTICOS" -> true;

                default -> false;
            };
        }


        // =========================
        // CLIENTE
        // =========================

        if ("Cliente".equalsIgnoreCase(rol)) {

            return switch (vistaNormalizada) {

                case "ENVIOS",
                    "REPORTES",
                    "COMUNICACIONES",
                    "FACTURACION",
                    "SEGUIMIENTO_ENVIO",
                    "RIESGOS",
                    "EVENTOS_LOGISTICOS" -> true;

                default -> false;
            };
        }


        // =========================
        // ROL DESCONOCIDO
        // =========================

        return false;
    }
}
