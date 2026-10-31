
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;
import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.ContenedorMongoDAO;
import org.example.mongoDB.dao.SensorMongoDAO;
import org.example.mongoDB.model.Sensor;
import org.example.cassandra.alertas.service.AlertasCassandraService;

import com.mongodb.client.ClientSession;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class SensorService {

    private final SensorMongoDAO sensorDAO;
    private final ContenedorMongoDAO contenedorDAO;
    private AlertasCassandraService alertasCassandra;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SensorService() {
        this.sensorDAO = new SensorMongoDAO();
        this.contenedorDAO = new ContenedorMongoDAO();
    }

    // =========================================================
    // CREAR SENSOR
    // =========================================================

    public void crearSensor(
            String contenedorId,
            String tipo,
            String fabricante,
            Date fechaInstalacion
    ) {

        validarObjectId(
                contenedorId,
                "El ID del contenedor no es válido."
        );

        if (!contenedorDAO.existePorId(contenedorId)) {
            throw new IllegalArgumentException(
                    "El contenedor seleccionado no existe."
            );
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de sensor es obligatorio."
            );
        }

        if (fabricante == null || fabricante.isBlank()) {
            throw new IllegalArgumentException(
                    "El fabricante es obligatorio."
            );
        }

        if (fechaInstalacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de instalación es obligatoria."
            );
        }

        Sensor sensor = new Sensor(
                null,
                contenedorId,
                tipo.trim(),
                fabricante.trim(),
                fechaInstalacion,
                "ACTIVO"
        );

        sensorDAO.agregar(sensor);
        AppLog.info(SensorService.class, "Sensor creado.");
    }

    // =========================================================
    // MODIFICAR SENSOR
    // =========================================================

    public void modificarSensor(
            String id,
            String contenedorId,
            String tipo,
            String fabricante,
            Date fechaInstalacion,
            String estado
    ) {

        validarObjectId(
                id,
                "El ID del sensor no es válido."
        );

        if (!sensorDAO.existePorId(id)) {
            throw new IllegalArgumentException(
                    "El sensor no existe."
            );
        }

        Document estadoAnterior = sensorDAO.buscarPorId(id);
        String estadoAnteriorTexto = estadoAnterior == null
                ? null
                : estadoAnterior.getString("estado");

        validarObjectId(
                contenedorId,
                "El ID del contenedor no es válido."
        );

        if (!contenedorDAO.existePorId(contenedorId)) {
            throw new IllegalArgumentException(
                    "El contenedor seleccionado no existe."
            );
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de sensor es obligatorio."
            );
        }

        if (fabricante == null || fabricante.isBlank()) {
            throw new IllegalArgumentException(
                    "El fabricante es obligatorio."
            );
        }

        if (fechaInstalacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de instalación es obligatoria."
            );
        }

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        Sensor sensor = new Sensor(
                id,
                contenedorId,
                tipo.trim(),
                fabricante.trim(),
                fechaInstalacion,
                estado.trim()
        );

        sensorDAO.modificar(sensor);

        if (!"INACTIVO".equalsIgnoreCase(estadoAnteriorTexto)
                && "INACTIVO".equalsIgnoreCase(estado.trim())) {
            generarAlertaSensorInactivo(sensor, estadoAnterior);
        }
    }

    private void generarAlertaSensorInactivo(Sensor sensor, Document estadoAnterior) {
        String codigo = estadoAnterior.getString("codigo");
        String identificador = codigo == null ? sensor.getId() : codigo;
        String descripcion = "El sensor " + identificador
                + " (ID " + sensor.getId() + ") del contenedor "
                + sensor.getContenedorId() + " fue marcado como INACTIVO.";
        try {
            obtenerServicioAlertas().generarAlerta("Sensor inactivo", descripcion);
        } catch (RuntimeException ex) {
            AppLog.error(SensorService.class,
                    "El sensor se actualizó, pero no se pudo registrar la alerta en Cassandra.", ex);
        }
    }

    private AlertasCassandraService obtenerServicioAlertas() {
        if (alertasCassandra == null) {
            alertasCassandra = new AlertasCassandraService();
        }
        return alertasCassandra;
    }

    // =========================================================
    // ELIMINAR SENSOR
    // =========================================================

    public void eliminarSensor(String id) {

        validarObjectId(
                id,
                "El ID del sensor no es válido."
        );

        if (!sensorDAO.existePorId(id)) {
            throw new IllegalArgumentException(
                    "El sensor no existe."
            );
        }

        try (ClientSession session = MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {
                sensorDAO.eliminar(session, id);

                session.commitTransaction();

                AppLog.info(
                        SensorService.class,
                        "Sensor eliminado."
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
    // LISTAR SENSORES
    // =========================================================

    public List<Document> listarSensores() {
        return TablaConsola.mostrar(
                "SensorService.listarSensores",
                sensorDAO.listarTodos()
        );
    }

    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(String id, String mensaje) {

        if (id == null || id.isBlank() || !ObjectId.isValid(id)) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
