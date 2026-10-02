package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.dao.ContenedorMongoDAO;
import org.example.mongoDB.dao.EnvioMongoDAO;
import org.example.mongoDB.model.Envio;
import org.example.mongoDB.model.Tramo;
import org.example.mongoDB.model.Ubicacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnvioService {

    private final EnvioMongoDAO envioDAO;

    private final ClienteMongoDAO clienteDAO;

    private final ContenedorMongoDAO contenedorDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EnvioService() {

        this.envioDAO =
                new EnvioMongoDAO();

        this.clienteDAO =
                new ClienteMongoDAO();

        this.contenedorDAO =
                new ContenedorMongoDAO();
    }


    // =========================================================
    // CREAR ENVÍO
    // =========================================================

    public void crearEnvio(
            String clienteId,
            List<String> contenedoresIds,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String prioridad
    ) {

        // -----------------------------------------------------
        // VALIDAR CLIENTE
        // -----------------------------------------------------

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        Document cliente =
                clienteDAO.buscarPorId(
                        clienteId
                );


        if (cliente == null) {

            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR CONTENEDORES
        // -----------------------------------------------------

        if (contenedoresIds == null
                || contenedoresIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar al menos un contenedor."
            );
        }


        for (String contenedorId : contenedoresIds) {

            validarObjectId(
                    contenedorId,
                    "Uno de los IDs de contenedor no es válido."
            );


            if (!contenedorDAO.existePorId(
                    contenedorId
            )) {

                throw new IllegalArgumentException(
                        "Uno de los contenedores seleccionados no existe."
                );
            }
        }


        // -----------------------------------------------------
        // VALIDAR ORIGEN
        // -----------------------------------------------------

        if (ciudadOrigen == null
                || ciudadOrigen.isBlank()) {

            throw new IllegalArgumentException(
                    "La ciudad de origen es obligatoria."
            );
        }


        if (paisOrigen == null
                || paisOrigen.isBlank()) {

            throw new IllegalArgumentException(
                    "El país de origen es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DESTINO
        // -----------------------------------------------------

        if (ciudadDestino == null
                || ciudadDestino.isBlank()) {

            throw new IllegalArgumentException(
                    "La ciudad de destino es obligatoria."
            );
        }


        if (paisDestino == null
                || paisDestino.isBlank()) {

            throw new IllegalArgumentException(
                    "El país de destino es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR PRIORIDAD
        // -----------------------------------------------------

        if (prioridad == null
                || prioridad.isBlank()) {

            throw new IllegalArgumentException(
                    "La prioridad es obligatoria."
            );
        }


        // -----------------------------------------------------
        // CREAR UBICACIONES
        // -----------------------------------------------------

        Ubicacion origen =
                new Ubicacion(
                        ciudadOrigen.trim(),
                        paisOrigen.trim()
                );


        Ubicacion destino =
                new Ubicacion(
                        ciudadDestino.trim(),
                        paisDestino.trim()
                );


        // -----------------------------------------------------
        // CREAR ENVÍO
        // -----------------------------------------------------

        Envio envio =
                new Envio(
                        null,
                        clienteId,
                        new ArrayList<>(contenedoresIds),
                        new Date(),
                        origen,
                        destino,
                        "PENDIENTE",
                        prioridad.trim(),
                        new ArrayList<>()
                );


        envioDAO.agregar(
                envio
        );
    }


    // =========================================================
    // LISTAR ENVÍOS
    // =========================================================

    public List<Document> listarEnvios() {

        return envioDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del envío no es válido."
        );


        Document envio =
                envioDAO.buscarPorId(
                        id
                );


        if (envio == null) {

            throw new IllegalArgumentException(
                    "El envío no existe."
            );
        }


        return envio;
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del envío no es válido."
        );


        return envioDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return envioDAO.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR CLIENTE
    // =========================================================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        return envioDAO.buscarPorCliente(
                clienteId
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        return envioDAO.buscarPorEstado(
                estado.trim()
        );
    }


    // =========================================================
    // BUSCAR POR PAÍS
    // =========================================================

    public List<Document> buscarPorPais(
            String pais
    ) {

        if (pais == null
                || pais.isBlank()) {

            throw new IllegalArgumentException(
                    "El país es obligatorio."
            );
        }


        return envioDAO.buscarPorPais(
                pais.trim()
        );
    }


    // =========================================================
    // BUSCAR DEMORADOS
    // =========================================================

    public List<Document> buscarDemorados() {

        return envioDAO.buscarDemorados();
    }


    // =========================================================
    // AGREGAR TRAMO
    // =========================================================

    public void agregarTramo(
            String envioId,
            String medioTransporte,
            String origen,
            String destino,
            Date fechaSalida,
            Date fechaLlegadaEstimada
    ) {

        // -----------------------------------------------------
        // VALIDAR ENVÍO
        // -----------------------------------------------------

        validarObjectId(
                envioId,
                "El ID del envío no es válido."
        );


        if (!envioDAO.existePorId(
                envioId
        )) {

            throw new IllegalArgumentException(
                    "El envío seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR MEDIO DE TRANSPORTE
        // -----------------------------------------------------

        if (medioTransporte == null
                || medioTransporte.isBlank()) {

            throw new IllegalArgumentException(
                    "El medio de transporte es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR ORIGEN
        // -----------------------------------------------------

        if (origen == null
                || origen.isBlank()) {

            throw new IllegalArgumentException(
                    "El origen del tramo es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DESTINO
        // -----------------------------------------------------

        if (destino == null
                || destino.isBlank()) {

            throw new IllegalArgumentException(
                    "El destino del tramo es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR FECHA DE SALIDA
        // -----------------------------------------------------

        if (fechaSalida == null) {

            throw new IllegalArgumentException(
                    "La fecha de salida es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR FECHA DE LLEGADA
        // -----------------------------------------------------

        if (fechaLlegadaEstimada == null) {

            throw new IllegalArgumentException(
                    "La fecha de llegada estimada es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR ORDEN DE FECHAS
        // -----------------------------------------------------

        if (fechaLlegadaEstimada.before(
                fechaSalida
        )) {

            throw new IllegalArgumentException(
                    "La fecha de llegada no puede ser anterior a la fecha de salida."
            );
        }


        // -----------------------------------------------------
        // CREAR TRAMO
        // -----------------------------------------------------

        Tramo tramo =
                new Tramo(
                        medioTransporte.trim(),
                        origen.trim(),
                        destino.trim(),
                        fechaSalida,
                        fechaLlegadaEstimada
                );


        // -----------------------------------------------------
        // AGREGAR TRAMO AL ENVÍO
        // -----------------------------------------------------

        envioDAO.agregarTramo(
                envioId,
                tramo
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