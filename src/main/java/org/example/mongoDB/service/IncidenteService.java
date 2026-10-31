package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.IncidenteMongoDAO;
import org.example.logging.AppLog;
import org.example.mongoDB.model.Incidente;
import org.example.neo4j.controller.ControllerNeo4j;
import org.example.cassandra.alertas.service.AlertasCassandraService;

import java.util.Date;
import java.util.List;

public class IncidenteService {

    private final IncidenteMongoDAO incidenteDAO;
    private final ControllerNeo4j neo4j;
    private AlertasCassandraService alertasCassandra;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public IncidenteService() {

        this.incidenteDAO =
                new IncidenteMongoDAO();

        this.neo4j = new ControllerNeo4j();
    }


    // =========================================================
    // CREAR INCIDENTE
    // =========================================================

    public void crearIncidente(
            String envioId,
            String tipo,
            String severidad,
            String descripcion
    ) {

        // -----------------------------------------------------
        // VALIDAR ENVÍO
        // -----------------------------------------------------

        validarEnvioEnNeo4j(envioId);


        if (neo4j.buscarEnvioPorId(envioId) == null) {

            throw new IllegalArgumentException(
                    "El envío seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de incidente es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR SEVERIDAD
        // -----------------------------------------------------

        if (severidad == null
                || severidad.isBlank()) {

            throw new IllegalArgumentException(
                    "La severidad es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DESCRIPCIÓN
        // -----------------------------------------------------

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new IllegalArgumentException(
                    "La descripción es obligatoria."
            );
        }


        // -----------------------------------------------------
        // CREAR INCIDENTE
        // -----------------------------------------------------

        Incidente incidente =
                new Incidente(
                        null,
                        envioId,
                        tipo.trim(),
                        new Date(),
                        severidad.trim(),
                        "ABIERTO",
                        descripcion.trim()
                );


        incidenteDAO.agregar(
                incidente
        );
        generarAlertaIncidente(incidente);
        AppLog.info(IncidenteService.class, "Incidente logístico creado.");
    }


    // =========================================================
    // MODIFICAR INCIDENTE
    // =========================================================

    public void modificarIncidente(
            String id,
            String envioId,
            String tipo,
            String severidad,
            String estado,
            String descripcion
    ) {

        // -----------------------------------------------------
        // VALIDAR INCIDENTE
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        Document existente =
                incidenteDAO.buscarPorId(
                        id
                );


        if (existente == null) {

            throw new IllegalArgumentException(
                    "El incidente no existe."
            );
        }

        String estadoAnterior = existente.getString("estado");


        // -----------------------------------------------------
        // VALIDAR ENVÍO
        // -----------------------------------------------------

        validarEnvioEnNeo4j(envioId);


        if (neo4j.buscarEnvioPorId(envioId) == null) {

            throw new IllegalArgumentException(
                    "El envío seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de incidente es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR SEVERIDAD
        // -----------------------------------------------------

        if (severidad == null
                || severidad.isBlank()) {

            throw new IllegalArgumentException(
                    "La severidad es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR ESTADO
        // -----------------------------------------------------

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DESCRIPCIÓN
        // -----------------------------------------------------

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new IllegalArgumentException(
                    "La descripción es obligatoria."
            );
        }


        // -----------------------------------------------------
        // CONSERVAR FECHA ORIGINAL
        // -----------------------------------------------------

        Date fecha =
                existente.getDate(
                        "fecha"
                );


        // -----------------------------------------------------
        // CREAR OBJETO MODIFICADO
        // -----------------------------------------------------

        Incidente incidente =
                new Incidente(
                        id,
                        envioId,
                        tipo.trim(),
                        fecha,
                        severidad.trim(),
                        estado.trim(),
                        descripcion.trim()
                );


        // -----------------------------------------------------
        // ACTUALIZAR EN MONGODB
        // -----------------------------------------------------

        incidenteDAO.modificar(
                incidente
        );

        if (!"ABIERTO".equalsIgnoreCase(estadoAnterior)
                && "ABIERTO".equalsIgnoreCase(estado.trim())) {
            generarAlertaIncidente(incidente);
        }
    }

    private void generarAlertaIncidente(Incidente incidente) {
        String nombre = "Incidente " + incidente.getTipo().trim();
        String descripcion = "Envío " + incidente.getEnvioId()
                + ": " + incidente.getDescripcion()
                + " Severidad: " + incidente.getSeveridad()
                + ". Estado: " + incidente.getEstado() + ".";
        try {
            obtenerServicioAlertas().generarAlerta(nombre, descripcion);
        } catch (RuntimeException ex) {
            AppLog.error(IncidenteService.class,
                    "El incidente se guardó, pero no se pudo registrar la alerta en Cassandra.", ex);
        }
    }

    private AlertasCassandraService obtenerServicioAlertas() {
        if (alertasCassandra == null) {
            alertasCassandra = new AlertasCassandraService();
        }
        return alertasCassandra;
    }


    // =========================================================
    // ELIMINAR INCIDENTE
    // =========================================================

    public void eliminarIncidente(
            String id
    ) {

        // -----------------------------------------------------
        // VALIDAR ID
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        // -----------------------------------------------------
        // VERIFICAR EXISTENCIA
        // -----------------------------------------------------

        if (!incidenteDAO.existePorId(id)) {

            throw new IllegalArgumentException(
                    "El incidente no existe."
            );
        }


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        incidenteDAO.eliminar(
                id
        );
    }


    // =========================================================
    // LISTAR INCIDENTES
    // =========================================================

    public List<Document> listarIncidentes() {

        return TablaConsola.mostrar("IncidenteService.listarIncidentes", incidenteDAO.listarTodos());
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        Document incidente =
                incidenteDAO.buscarPorId(
                        id
                );


        if (incidente == null) {

            throw new IllegalArgumentException(
                    "El incidente no existe."
            );
        }


        return TablaConsola.mostrar("IncidenteService.buscarPorId", incidente);
    }


    // =========================================================
    // BUSCAR POR ENVÍO
    // =========================================================

    public List<Document> buscarPorEnvio(
            String envioId
    ) {

        validarEnvioEnNeo4j(envioId);


        return TablaConsola.mostrar("IncidenteService.buscarPorEnvio", incidenteDAO.buscarPorEnvio(
                envioId
        ));
    }


    // =========================================================
    // BUSCAR INCIDENTES ABIERTOS
    // =========================================================

    public List<Document> buscarAbiertos() {

        return TablaConsola.mostrar("IncidenteService.buscarAbiertos", incidenteDAO.buscarAbiertos());
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

    private void validarEnvioEnNeo4j(String envioId) {
        if (envioId == null || envioId.isBlank()
                || neo4j.buscarEnvioPorId(envioId) == null) {
            throw new IllegalArgumentException(
                    "El envío seleccionado no existe en Neo4j."
            );
        }
    }
}
