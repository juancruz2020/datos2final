
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;

import org.example.consola.TablaConsola;
import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.example.mongoDB.dao.FacturaMongoDAO;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.dao.ReporteMongoDAO;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;

import org.example.mongoDB.model.Factura;
import org.example.mongoDB.model.Reporte;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReporteService {

    // =========================================================
    // PRECIO FIJO DE CADA REPORTE PARA CLIENTES
    // =========================================================

    private static final BigDecimal PRECIO_REPORTE =
            new BigDecimal("5000.00");

    private final ReporteMongoDAO reporteDAO;
    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private final FacturaMongoDAO facturaDAO;
    private final ClienteMongoDAO clienteDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReporteService() {

        this.reporteDAO = new ReporteMongoDAO();
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
        this.facturaDAO = new FacturaMongoDAO();
        this.clienteDAO = new ClienteMongoDAO();
    }

    // =========================================================
    // CREAR REPORTE
    // =========================================================

    public void crearReporte(
            String usuarioId,
            String tipo,
            String formato
    ) {

        validarObjectId(
                usuarioId,
                "El ID del usuario no es válido."
        );

        Document usuario = usuarioDAO.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario seleccionado no existe."
            );
        }

        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de reporte es obligatorio."
            );
        }

        // -----------------------------------------------------
        // VALIDAR FORMATO
        // -----------------------------------------------------

        if (formato == null || formato.isBlank()) {
            throw new IllegalArgumentException(
                    "El formato es obligatorio."
            );
        }

        // -----------------------------------------------------
        // IDENTIFICAR ROL
        // -----------------------------------------------------

        ObjectId rolId = usuario.getObjectId("rol_id");

        if (rolId == null) {
            throw new SecurityException(
                    "El usuario no tiene un rol asignado."
            );
        }

        Document rol = rolDAO.buscarPorId(
                rolId.toHexString()
        );

        if (rol == null) {
            throw new SecurityException(
                    "No se encontró el rol del usuario."
            );
        }

        String descripcionRol = rol.getString("descripcion");

        String nombreRol = descripcionRol == null
                ? ""
                : descripcionRol.trim().toUpperCase(Locale.ROOT);

        if (!"ADMINISTRADOR".equals(nombreRol)
                && !"CLIENTE".equals(nombreRol)) {

            throw new SecurityException(
                    "No tenés permisos para generar reportes."
            );
        }

        // -----------------------------------------------------
        // ADMINISTRADOR: REPORTE GRATUITO
        // -----------------------------------------------------

        if ("ADMINISTRADOR".equals(nombreRol)) {

            Reporte reporte = new Reporte(
                    null,
                    usuarioId,
                    tipo.trim(),
                    new Date(),
                    formato.trim(),
                    "GENERADO"
            );

            reporteDAO.agregar(reporte);

            AppLog.info(
                    ReporteService.class,
                    "Reporte de administrador registrado sin facturación."
            );

            return;
        }

        // -----------------------------------------------------
        // CLIENTE: IDENTIFICAR CLIENTE ASOCIADO
        // -----------------------------------------------------

        ObjectId idCliente = usuario.getObjectId("cliente_id");

        if (idCliente == null) {
            throw new SecurityException(
                    "El usuario no tiene un cliente asociado."
            );
        }

        String clienteId = idCliente.toHexString();

        Document cliente = clienteDAO.buscarPorId(clienteId);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente asociado no existe."
            );
        }

        if (!"ACTIVO".equalsIgnoreCase(
                cliente.getString("estado")
        )) {

            throw new IllegalStateException(
                    "No se puede facturar un reporte a un cliente inactivo."
            );
        }

        // -----------------------------------------------------
        // CLIENTE: REPORTE FACTURABLE POR $5000
        // -----------------------------------------------------

        Date fechaActual = new Date();

        Reporte reporte = new Reporte(
                null,
                usuarioId,
                tipo.trim(),
                fechaActual,
                formato.trim(),
                "GENERADO"
        );

        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fechaActual);
        calendario.add(Calendar.DAY_OF_MONTH, 30);

        Date fechaVencimiento = calendario.getTime();

        Factura factura = new Factura(
                null,
                clienteId,
                fechaActual,
                fechaVencimiento,
                PRECIO_REPORTE,
                "PENDIENTE"
        );

        // -----------------------------------------------------
        // GUARDAR REPORTE Y FACTURA EN UNA TRANSACCIÓN
        // -----------------------------------------------------

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                reporteDAO.agregar(
                        session,
                        reporte
                );

                facturaDAO.agregarFacturaReporte(
                        session,
                        factura,
                        reporte.getId()
                );

                session.commitTransaction();

                AppLog.info(
                        ReporteService.class,
                        "Reporte de cliente registrado con factura de $"
                                + PRECIO_REPORTE.toPlainString()
                );

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // =========================================================
    // LISTAR REPORTES
    // =========================================================

    public List<Document> listarReportes() {

        return TablaConsola.mostrar(
                "ReporteService.listarReportes",
                reporteDAO.listarTodos()
        );
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
}
