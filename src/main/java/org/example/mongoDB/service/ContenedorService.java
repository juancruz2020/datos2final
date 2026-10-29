
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;
import org.example.logging.AppLog;
import org.example.conecciones.MongoSingleton;

import org.example.mongoDB.dao.ContenedorMongoDAO;
import org.example.mongoDB.dao.SensorMongoDAO;
import org.example.mongoDB.model.Contenedor;

import org.example.neo4j.controller.ControllerNeo4j;

import com.mongodb.client.ClientSession;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class ContenedorService {

    private final ContenedorMongoDAO contenedorDAO;

    public ContenedorService() {
        this.contenedorDAO = new ContenedorMongoDAO();
    }

    // =========================================================
    // CREAR CONTENEDOR
    // =========================================================

    public void crearContenedor(
            String codigoInternacional,
            String tipo,
            double capacidad
    ) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (capacidad <= 0 || !Double.isFinite(capacidad)) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un número mayor a 0."
            );
        }

        Contenedor contenedor = new Contenedor(
                null,
                null,
                tipo.trim(),
                capacidad,
                "DISPONIBLE"
        );

        contenedorDAO.agregar(contenedor);

        AppLog.info(ContenedorService.class, "Contenedor creado.");
    }

    // =========================================================
    // MODIFICAR CONTENEDOR
    // =========================================================

    public void modificarContenedor(
            String id,
            String codigoInternacional,
            String tipo,
            double capacidad,
            String estado
    ) {

        validarObjectId(id);

        Document actual = contenedorDAO.buscarPorId(id);

        if (actual == null) {
            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (capacidad <= 0 || !Double.isFinite(capacidad)) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un número mayor a 0."
            );
        }

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        String codigoOriginal = actual.getString("codigo_internacional");

        Contenedor contenedor = new Contenedor(
                id,
                codigoOriginal,
                tipo.trim(),
                capacidad,
                estado.trim()
        );

        contenedorDAO.modificar(contenedor);
    }

    // =========================================================
    // ELIMINAR CONTENEDOR
    // =========================================================

    public void eliminarContenedor(String id) {

        validarObjectId(id);

        if (!contenedorDAO.existePorId(id)) {
            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }

        // Verificar que no tenga envíos activos.
        for (Map<String, Object> envio : new ControllerNeo4j().listarEnvios()) {

            Object contenedores = envio.get("contenedoresIds");

            if (!(contenedores instanceof List<?> ids) || !ids.contains(id)) {
                continue;
            }

            String estado = (String) envio.get("estado");

            if (estado == null ||
                    (!estado.equalsIgnoreCase("ENTREGADO") &&
                     !estado.equalsIgnoreCase("FINALIZADO") &&
                     !estado.equalsIgnoreCase("CANCELADO"))) {

                throw new IllegalStateException(
                        "No se puede eliminar el contenedor porque tiene un envío activo."
                );
            }
        }

        // Obtener los sensores asociados.
        SensorMongoDAO sensorDAO = new SensorMongoDAO();
        List<Document> sensores = sensorDAO.buscarPorContenedor(id);

        // Eliminar sensores y contenedor en una misma transacción.
        try (ClientSession session = MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {
                for (Document sensor : sensores) {
                    sensorDAO.eliminar(
                            session,
                            sensor.getObjectId("_id").toHexString()
                    );
                }

                contenedorDAO.eliminar(session, id);

                session.commitTransaction();

                AppLog.info(
                        ContenedorService.class,
                        "Contenedor y sensores asociados eliminados."
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
    // LISTAR CONTENEDORES
    // =========================================================

    public List<Document> listarContenedores() {
        return TablaConsola.mostrar(
                "ContenedorService.listarContenedores",
                contenedorDAO.listarTodos()
        );
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(String id) {

        validarObjectId(id);

        Document contenedor = contenedorDAO.buscarPorId(id);

        if (contenedor == null) {
            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }

        return TablaConsola.mostrar(
                "ContenedorService.buscarPorId",
                contenedor
        );
    }

    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(String id) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    "El ID del contenedor no es válido."
            );
        }
    }
}
