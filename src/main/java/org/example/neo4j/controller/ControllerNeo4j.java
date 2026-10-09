package org.example.neo4j.controller;

import org.example.DatosPorDefecto.GeneradorDatosPruebaNeo;
import org.example.neo4j.model.Cliente;
import org.example.neo4j.model.Contenedor;
import org.example.neo4j.model.Envio;
import org.example.neo4j.model.Operador;
import org.example.neo4j.model.Proveedor;
import org.example.neo4j.model.Sensor;
import org.example.neo4j.model.Ubicacion;
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

    public void crearOperador(Operador operador) {
        service.crearOperador(operador);
    }

    public void crearProveedor(Proveedor proveedor) {
        service.crearProveedor(proveedor);
    }

    public void crearContenedor(Contenedor contenedor) {
        service.crearContenedor(contenedor);
    }

    public void crearUbicacion(Ubicacion ubicacion) {
        service.crearUbicacion(ubicacion);
    }

    public void crearSensor(Sensor sensor) {
        service.crearSensor(sensor);
    }

    // =====================================================
    // RELACIONES
    // =====================================================

    // Cliente -> Envío
    public void relacionarClienteEnvio(
            String clienteId,
            String envioId
    ) {
        service.relacionarClienteEnvio(
                clienteId,
                envioId
        );
    }

    public void relacionarEnvioVehiculo(String envioId, String vehiculoId) {
        service.relacionarEnvioVehiculo(envioId, vehiculoId);
    }

    public void relacionarEnvioOrigen(String envioId, String ciudad, String pais) {
        service.relacionarEnvioOrigen(envioId, ciudad, pais);
    }

    public void relacionarEnvioDestino(String envioId, String ciudad, String pais) {
        service.relacionarEnvioDestino(envioId, ciudad, pais);
    }

    // Operador -> Vehículo
    public void relacionarOperadorVehiculo(
            String operadorId,
            String vehiculoId
    ) {
        service.relacionarOperadorVehiculo(
                operadorId,
                vehiculoId
        );
    }

    // Proveedor -> Vehículo
    public void relacionarProveedorVehiculo(
            String proveedorId,
            String vehiculoId
    ) {
        service.relacionarProveedorVehiculo(
                proveedorId,
                vehiculoId
        );
    }

    // Envío -> Contenedor
    public void relacionarEnvioContenedor(
            String envioId,
            String contenedorId
    ) {
        service.relacionarEnvioContenedor(
                envioId,
                contenedorId
        );
    }

    // Contenedor -> Sensor
    public void relacionarContenedorSensor(
            String contenedorId,
            String sensorId
    ) {
        service.relacionarContenedorSensor(
                contenedorId,
                sensorId
        );
    }

    // =====================================================
    // CONSULTAS
    // =====================================================

    // 1. Ruta de un envío
    public List<String> obtenerRutaEnvio(
            String envioId
    ) {
        return service.obtenerRutaEnvio(
                envioId
        );
    }

    // 2. Envíos realizados por un cliente
    public List<String> obtenerEnviosDeCliente(
            String clienteId
    ) {
        return service.obtenerEnviosDeCliente(
                clienteId
        );
    }

    // 3. Vehículos operados por un operador
    public List<String> obtenerVehiculosDeOperador(
            String operadorId
    ) {
        return service.obtenerVehiculosDeOperador(
                operadorId
        );
    }

    // 4. Contenedores transportados por un envío
    public List<String> obtenerContenedoresDeEnvio(
            String envioId
    ) {
        return service.obtenerContenedoresDeEnvio(
                envioId
        );
    }

    // 5. Envíos que pasan por una ubicación
    public List<String> obtenerEnviosPorUbicacion(
            String ubicacionId
    ) {
        return service.obtenerEnviosPorUbicacion(
                ubicacionId
        );
    }

    // =====================================================
    // CONSULTAS AVANZADAS
    // =====================================================

    // 6. Operadores involucrados en un envío
    public List<String> obtenerOperadorDeEnvio(
            String envioId
    ) {
        return service.obtenerOperadorDeEnvio(
                envioId
        );
    }

    // 7. Proveedores involucrados en un envío
    public List<String> obtenerProveedorDeEnvio(
            String envioId
    ) {
        return service.obtenerProveedorDeEnvio(
                envioId
        );
    }

    // 8. Recorrido completo de un contenedor
    public List<String> obtenerRecorridoContenedor(
            String contenedorId
    ) {
        return service.obtenerRecorridoContenedor(
                contenedorId
        );
    }

    // 9. Red completa relacionada con un envío
    public List<String> obtenerRedDeEnvio(
            String envioId
    ) {
        return service.obtenerRedDeEnvio(
                envioId
        );
    }

    // =====================================================
    // DATOS DE PRUEBA
    // =====================================================

    public void generarDatosPrueba() {
        GeneradorDatosPruebaNeo.generar();
    }




// =====================================================
// CONSULTAS PARA LA INTERFAZ DE ENVÍOS
// =====================================================

    public List<Map<String, Object>> listarClientes() {
        return service.listarClientes();
    }

    public Map<String, Object> buscarClientePorId(String id) {
        return service.buscarClientePorId(id);
    }

    public List<Map<String, Object>> listarContenedores() {
        return service.listarContenedores();
    }

    public Map<String, Object> buscarContenedorPorId(String id) {
        return service.buscarContenedorPorId(id);
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