package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.neo4j.controller.ControllerNeo4j;

import java.util.Date;
import java.util.List;

public class EnvioController {

    private final ControllerNeo4j neo4j;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EnvioController() {

        this.neo4j = new ControllerNeo4j();
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

        throw new UnsupportedOperationException("Los envíos se gestionan en Neo4j.");
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

        throw new UnsupportedOperationException("Los envíos se gestionan en Neo4j.");
    }


    // =========================================================
    // ELIMINAR ENVÍO
    // =========================================================

    public void eliminarEnvio(
            String id
    ) {

        throw new UnsupportedOperationException("Los envíos se gestionan en Neo4j.");
    }


    // =========================================================
    // LISTAR ENVÍOS
    // =========================================================

    public List<Document> listarEnvios() {
        List<Document> resultado = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> envio : neo4j.listarEnvios()) {
            resultado.add(documentoNeo4j(envio));
        }
        return resultado;
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        java.util.Map<String, Object> envio = neo4j.buscarEnvioPorId(id);
        return envio == null ? null : documentoNeo4j(envio);
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        return neo4j.buscarEnvioPorId(id) != null;
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {
        List<String> ids = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> envio : neo4j.listarEnvios()) {
            Object id = envio.get("id");
            if (id != null) ids.add(id.toString());
        }
        return ids;
    }


    // =========================================================
    // BUSCAR POR CLIENTE
    // =========================================================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {
        List<Document> resultado = new java.util.ArrayList<>();
        for (Document envio : listarEnvios()) {
            if (clienteId != null && clienteId.equals(envio.getString("cliente_id"))) {
                resultado.add(envio);
            }
        }
        return resultado;
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        List<Document> resultado = new java.util.ArrayList<>();
        for (Document envio : listarEnvios()) {
            if (estado != null && estado.equals(envio.getString("estado"))) resultado.add(envio);
        }
        return resultado;
    }


    // =========================================================
    // BUSCAR POR PAÍS
    // =========================================================

    public List<Document> buscarPorPais(
            String pais
    ) {

        List<Document> resultado = new java.util.ArrayList<>();
        for (Document envio : listarEnvios()) {
            Document origen = envio.get("origen", Document.class);
            Document destino = envio.get("destino", Document.class);
            if (origen != null && pais.equals(origen.getString("pais"))
                    || destino != null && pais.equals(destino.getString("pais"))) resultado.add(envio);
        }
        return resultado;
    }


    // =========================================================
    // BUSCAR DEMORADOS
    // =========================================================

    public List<Document> buscarDemorados() {
        return buscarPorEstado("DEMORADO");
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

        throw new UnsupportedOperationException("Los tramos de envíos se gestionan en Neo4j.");
    }

    private Document documentoNeo4j(java.util.Map<String, Object> envio) {
        Document origen = new Document("ciudad", envio.get("ciudadOrigen"))
                .append("pais", envio.get("paisOrigen"));
        Document destino = new Document("ciudad", envio.get("ciudadDestino"))
                .append("pais", envio.get("paisDestino"));
        return new Document("_id", envio.get("id"))
                .append("cliente_id", envio.get("clienteId"))
                .append("contenedores_ids", envio.get("contenedoresIds"))
                .append("fecha_creacion", envio.get("fechaCreacion"))
                .append("origen", origen)
                .append("destino", destino)
                .append("estado", envio.get("estado"))
                .append("prioridad", envio.get("prioridad"));
    }
}
