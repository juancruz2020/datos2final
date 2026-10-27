package org.example.neo4j.controller;

import org.example.mongoDB.controller.ControllerMongoDB;


import org.example.neo4j.model.Envio;

import org.example.neo4j.service.LogisticaNeo4jService;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
public class ControllerNeo4j {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final LogisticaNeo4jService service;

    public ControllerNeo4j() {
        this.service = new LogisticaNeo4jService();
    }

    // =====================================================
    // NODOS
    // =====================================================


    public void crearEnvio(Envio envio) {
        service.crearEnvio(envio);
    }


    /** Sincroniza el vehículo desde MongoDB y lo vincula directamente al envío en Neo4j. */
    public void vincularVehiculoMongoAEnvio(String envioId, String idVehiculoMongo) {
        Document documento = mongo.buscarVehiculoPorId(idVehiculoMongo);
        ObjectId objectId = documento.getObjectId("_id");
        if (objectId == null) {
            throw new IllegalStateException("El vehículo de MongoDB no tiene un _id ObjectId válido.");
        }
        service.vincularVehiculoAEnvio(
                envioId,
                objectId.toHexString(),
                documento.getString("identificacion"),
                documento.getString("tipo")
        );
    }


    // =====================================================
    // DATOS DE PRUEBA
    // =====================================================


    public List<Map<String, Object>> listarEnvios() {
        return service.listarEnvios();
    }

    public Map<String, Object> buscarEnvioPorId(String id) {
        return service.buscarEnvioPorId(id);
    }

    public void eliminarEnvio(String id) {
        service.eliminarEnvio(id);
    }

    public void modificarEnvio(Envio envio) {
        service.modificarEnvio(envio);
    }

}