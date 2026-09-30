package org.example.neo4j.controller;

import org.example.neo4j.GeneradorDatosPrueba;
import org.example.neo4j.model.Cliente;
import org.example.neo4j.model.Contenedor;
import org.example.neo4j.model.Envio;
import org.example.neo4j.model.Operador;
import org.example.neo4j.model.Proveedor;
import org.example.neo4j.model.Sensor;
import org.example.neo4j.model.Tramo;
import org.example.neo4j.model.Ubicacion;
import org.example.neo4j.model.Vehiculo;
import org.example.neo4j.service.LogisticaNeo4jService;

import java.util.List;

public class ControllerNeo4j {

    private final LogisticaNeo4jService service;

    public ControllerNeo4j() {
        this.service = new LogisticaNeo4jService();
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

    public void crearTramo(Tramo tramo) {
        service.crearTramo(tramo);
    }

    public void crearVehiculo(Vehiculo vehiculo) {
        service.crearVehiculo(vehiculo);
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

    // Envío -> Tramo
    public void relacionarEnvioTramo(
            String envioId,
            String tramoId
    ) {
        service.relacionarEnvioTramo(
                envioId,
                tramoId
        );
    }

    // Tramo -> Vehículo
    public void relacionarTramoVehiculo(
            String tramoId,
            String vehiculoId
    ) {
        service.relacionarTramoVehiculo(
                tramoId,
                vehiculoId
        );
    }

    // Tramo -> Ubicación de salida
    public void relacionarTramoSalida(
            String tramoId,
            String ubicacionId
    ) {
        service.relacionarTramoSalida(
                tramoId,
                ubicacionId
        );
    }

    // Tramo -> Ubicación de llegada
    public void relacionarTramoLlegada(
            String tramoId,
            String ubicacionId
    ) {
        service.relacionarTramoLlegada(
                tramoId,
                ubicacionId
        );
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
        GeneradorDatosPrueba.generar();
    }
}