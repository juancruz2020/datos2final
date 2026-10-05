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

        validarContenedores(
                contenedoresIds
        );


        // -----------------------------------------------------
        // VALIDAR ORIGEN
        // -----------------------------------------------------

        validarTexto(
                ciudadOrigen,
                "La ciudad de origen es obligatoria."
        );

        validarTexto(
                paisOrigen,
                "El país de origen es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR DESTINO
        // -----------------------------------------------------

        validarTexto(
                ciudadDestino,
                "La ciudad de destino es obligatoria."
        );

        validarTexto(
                paisDestino,
                "El país de destino es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR PRIORIDAD
        // -----------------------------------------------------

        validarTexto(
                prioridad,
                "La prioridad es obligatoria."
        );


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
    // MODIFICAR ENVÍO
    // =========================================================

    public void modificarEnvio(
            String id,
            String clienteId,
            List<String> contenedoresIds,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String estado,
            String prioridad
    ) {

        // -----------------------------------------------------
        // VALIDAR ENVÍO
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del envío no es válido."
        );

        Document existente =
                envioDAO.buscarPorId(
                        id
                );

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El envío no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR CLIENTE
        // -----------------------------------------------------

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        if (clienteDAO.buscarPorId(clienteId) == null) {

            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR CONTENEDORES
        // -----------------------------------------------------

        validarContenedores(
                contenedoresIds
        );


        // -----------------------------------------------------
        // VALIDAR ORIGEN
        // -----------------------------------------------------

        validarTexto(
                ciudadOrigen,
                "La ciudad de origen es obligatoria."
        );

        validarTexto(
                paisOrigen,
                "El país de origen es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR DESTINO
        // -----------------------------------------------------

        validarTexto(
                ciudadDestino,
                "La ciudad de destino es obligatoria."
        );

        validarTexto(
                paisDestino,
                "El país de destino es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR ESTADO
        // -----------------------------------------------------

        validarTexto(
                estado,
                "El estado es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR PRIORIDAD
        // -----------------------------------------------------

        validarTexto(
                prioridad,
                "La prioridad es obligatoria."
        );


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
        // CONSERVAR FECHA DE CREACIÓN
        // -----------------------------------------------------

        Date fechaCreacion =
                existente.getDate(
                        "fecha_creacion"
                );

        if (fechaCreacion == null) {

            fechaCreacion =
                    new Date();
        }


        // -----------------------------------------------------
        // CONSERVAR TRAMOS EXISTENTES
        // -----------------------------------------------------

        List<Tramo> tramos =
                convertirTramosExistentes(
                        existente
                );


        // -----------------------------------------------------
        // CREAR OBJETO ACTUALIZADO
        // -----------------------------------------------------

        Envio envio =
                new Envio(
                        id,
                        clienteId,
                        new ArrayList<>(contenedoresIds),
                        fechaCreacion,
                        origen,
                        destino,
                        estado.trim(),
                        prioridad.trim(),
                        tramos
                );


        // -----------------------------------------------------
        // MODIFICAR
        // -----------------------------------------------------

        envioDAO.modificar(
                envio
        );
    }


    // =========================================================
    // ELIMINAR ENVÍO
    // =========================================================

    public void eliminarEnvio(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del envío no es válido."
        );

        if (!envioDAO.existePorId(id)) {

            throw new IllegalArgumentException(
                    "El envío no existe."
            );
        }

        envioDAO.eliminar(
                id
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

        validarTexto(
                estado,
                "El estado es obligatorio."
        );

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

        validarTexto(
                pais,
                "El país es obligatorio."
        );

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

        if (!envioDAO.existePorId(envioId)) {

            throw new IllegalArgumentException(
                    "El envío seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR MEDIO DE TRANSPORTE
        // -----------------------------------------------------

        validarTexto(
                medioTransporte,
                "El medio de transporte es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR ORIGEN
        // -----------------------------------------------------

        validarTexto(
                origen,
                "El origen del tramo es obligatorio."
        );


        // -----------------------------------------------------
        // VALIDAR DESTINO
        // -----------------------------------------------------

        validarTexto(
                destino,
                "El destino del tramo es obligatorio."
        );


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
    // VALIDAR CONTENEDORES
    // =========================================================

    private void validarContenedores(
            List<String> contenedoresIds
    ) {

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
    }


    // =========================================================
    // CONVERTIR TRAMOS EXISTENTES
    // =========================================================

    private List<Tramo> convertirTramosExistentes(
            Document envio
    ) {

        List<Tramo> tramos =
                new ArrayList<>();

        List<Document> documentos =
                envio.getList(
                        "tramos",
                        Document.class
                );

        if (documentos == null) {

            return tramos;
        }

        for (Document documento : documentos) {

            Tramo tramo =
                    new Tramo(
                            documento.getString(
                                    "medio_transporte"
                            ),
                            documento.getString(
                                    "origen"
                            ),
                            documento.getString(
                                    "destino"
                            ),
                            documento.getDate(
                                    "fecha_salida"
                            ),
                            documento.getDate(
                                    "fecha_llegada_estimada"
                            )
                    );

            tramos.add(
                    tramo
            );
        }

        return tramos;
    }


    // =========================================================
    // VALIDAR TEXTO
    // =========================================================

    private void validarTexto(
            String texto,
            String mensaje
    ) {

        if (texto == null
                || texto.isBlank()) {

            throw new IllegalArgumentException(
                    mensaje
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
}