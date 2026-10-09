package org.example.neo4j.controller;

import org.example.DatosPorDefecto.GeneradorDatosPruebaNeo;
import org.example.neo4j.model.Cliente;

import org.example.neo4j.model.Envio;

import org.example.neo4j.model.Vehiculo;
import org.example.neo4j.service.LogisticaNeo4jService;
import org.example.mongoDB.controller.VehiculoController;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
public class ControllerNeo4j {

    private final LogisticaNeo4jService service;
    private final VehiculoController vehiculoMongoController;

    public ControllerNeo4j() {
        this.service = new LogisticaNeo4jService();
        this.vehiculoMongoController = new VehiculoController();
    }

    // =====================================================
    // NODOS
    // =====================================================

    public void crearCliente(Cliente cliente) {
        service.crearCliente(cliente);
    }

    public void crearEnvio(Envio envio) {
        service.crearEnvio(envio);
    }

    public void crearVehiculo(Vehiculo vehiculo) {
        service.crearVehiculo(vehiculo);
    }

    /** Copia la referencia del vehículo de MongoDB a Neo4j conservando el mismo ID. */
    public void sincronizarVehiculoDesdeMongo(String idVehiculoMongo) {
        Document documento = vehiculoMongoController.buscarPorId(idVehiculoMongo);
        ObjectId objectId = documento.getObjectId("_id");
        if (objectId == null) {
            throw new IllegalStateException("El vehículo de MongoDB no tiene un _id ObjectId válido.");
        }
        String id = objectId.toHexString();
        String identificacion = documento.getString("identificacion");
        String tipo = documento.getString("tipo");
        service.sincronizarVehiculoMongo(id, identificacion, tipo);
    }

    /** Sincroniza el vehículo desde MongoDB y lo vincula directamente al envío en Neo4j. */
    public void vincularVehiculoMongoAEnvio(String envioId, String idVehiculoMongo) {
        Document documento = vehiculoMongoController.buscarPorId(idVehiculoMongo);
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

    public void generarDatosPrueba() {
        GeneradorDatosPruebaNeo.generar();
    }






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