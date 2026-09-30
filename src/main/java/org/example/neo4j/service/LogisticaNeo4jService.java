package org.example.neo4j.service;

import org.example.neo4j.dao.ClienteNeo4jDAO;
import org.example.neo4j.dao.ContenedorNeo4jDAO;
import org.example.neo4j.dao.EnvioNeo4jDAO;
import org.example.neo4j.dao.GrafoNeo4jDAO;
import org.example.neo4j.dao.OperadorNeo4jDAO;
import org.example.neo4j.dao.ProveedorNeo4jDAO;
import org.example.neo4j.dao.SensorNeo4jDAO;
import org.example.neo4j.dao.TramoNeo4jDAO;
import org.example.neo4j.dao.UbicacionNeo4jDAO;
import org.example.neo4j.dao.VehiculoNeo4jDAO;

import org.example.neo4j.model.Cliente;
import org.example.neo4j.model.Contenedor;
import org.example.neo4j.model.Envio;
import org.example.neo4j.model.Operador;
import org.example.neo4j.model.Proveedor;
import org.example.neo4j.model.Sensor;
import org.example.neo4j.model.Tramo;
import org.example.neo4j.model.Ubicacion;
import org.example.neo4j.model.Vehiculo;

import java.util.List;

public class LogisticaNeo4jService {

    private final ClienteNeo4jDAO clienteDAO;
    private final EnvioNeo4jDAO envioDAO;
    private final TramoNeo4jDAO tramoDAO;
    private final VehiculoNeo4jDAO vehiculoDAO;
    private final OperadorNeo4jDAO operadorDAO;
    private final ProveedorNeo4jDAO proveedorDAO;
    private final ContenedorNeo4jDAO contenedorDAO;
    private final UbicacionNeo4jDAO ubicacionDAO;
    private final SensorNeo4jDAO sensorDAO;

    private final GrafoNeo4jDAO grafoDAO;

    public LogisticaNeo4jService() {

        clienteDAO = new ClienteNeo4jDAO();
        envioDAO = new EnvioNeo4jDAO();
        tramoDAO = new TramoNeo4jDAO();

        vehiculoDAO = new VehiculoNeo4jDAO();
        operadorDAO = new OperadorNeo4jDAO();
        proveedorDAO = new ProveedorNeo4jDAO();
        contenedorDAO = new ContenedorNeo4jDAO();
        ubicacionDAO = new UbicacionNeo4jDAO();
        sensorDAO = new SensorNeo4jDAO();

        grafoDAO = new GrafoNeo4jDAO();
    }

    // =====================================================
    // CREACIÓN DE NODOS
    // =====================================================

    public void crearCliente(Cliente cliente) {
        clienteDAO.crearCliente(
                cliente.getId(),
                cliente.getRazonSocial(),
                cliente.getCuit()
        );
    }

    public void crearEnvio(Envio envio) {

        envioDAO.crearEnvio(
                envio.getId(),
                envio.getEstado(),
                envio.getPrioridad()
        );
    }

    public void crearTramo(Tramo tramo) {

        tramoDAO.crearTramo(
                tramo.getId(),
                tramo.getMedioTransporte(),
                tramo.getFechaSalida(),
                tramo.getFechaLlegadaEstimada()
        );
    }

    public void crearVehiculo(Vehiculo vehiculo) {
        vehiculoDAO.crear(vehiculo);
    }

    public void crearOperador(Operador operador) {
        operadorDAO.crear(operador);
    }

    public void crearProveedor(Proveedor proveedor) {
        proveedorDAO.crear(proveedor);
    }

    public void crearContenedor(Contenedor contenedor) {
        contenedorDAO.crear(contenedor);
    }

    public void crearUbicacion(Ubicacion ubicacion) {
        ubicacionDAO.crear(ubicacion);
    }

    public void crearSensor(Sensor sensor) {
        sensorDAO.crear(sensor);
    }

    // =====================================================
    // RELACIONES
    // =====================================================

    public void relacionarClienteEnvio(
            String clienteId,
            String envioId
    ) {
        grafoDAO.clienteRealizaEnvio(
                clienteId,
                envioId
        );
    }

    public void relacionarEnvioTramo(
            String envioId,
            String tramoId
    ) {
        grafoDAO.envioTieneTramo(
                envioId,
                tramoId
        );
    }

    public void relacionarTramoVehiculo(
            String tramoId,
            String vehiculoId
    ) {
        grafoDAO.tramoUsaVehiculo(
                tramoId,
                vehiculoId
        );
    }

    public void relacionarTramoSalida(
            String tramoId,
            String ubicacionId
    ) {
        grafoDAO.tramoSaleDe(
                tramoId,
                ubicacionId
        );
    }

    public void relacionarTramoLlegada(
            String tramoId,
            String ubicacionId
    ) {
        grafoDAO.tramoLlegaA(
                tramoId,
                ubicacionId
        );
    }

    public void relacionarOperadorVehiculo(
            String operadorId,
            String vehiculoId
    ) {
        grafoDAO.operadorOperaVehiculo(
                operadorId,
                vehiculoId
        );
    }

    public void relacionarProveedorVehiculo(
            String proveedorId,
            String vehiculoId
    ) {
        grafoDAO.proveedorProveeVehiculo(
                proveedorId,
                vehiculoId
        );
    }

    public void relacionarEnvioContenedor(
            String envioId,
            String contenedorId
    ) {
        grafoDAO.envioTransportaContenedor(
                envioId,
                contenedorId
        );
    }

    public void relacionarContenedorSensor(
            String contenedorId,
            String sensorId
    ) {
        grafoDAO.contenedorTieneSensor(
                contenedorId,
                sensorId
        );
    }

    // =====================================================
    // CONSULTAS
    // =====================================================

    // 1. Obtiene la ruta de un envío
    public List<String> obtenerRutaEnvio(
            String envioId
    ) {
        return grafoDAO.obtenerRutaEnvio(
                envioId
        );
    }

    // 2. Obtiene los envíos de un cliente
    public List<String> obtenerEnviosDeCliente(
            String clienteId
    ) {
        return grafoDAO.obtenerEnviosDeCliente(
                clienteId
        );
    }

    // 3. Obtiene los vehículos de un operador
    public List<String> obtenerVehiculosDeOperador(
            String operadorId
    ) {
        return grafoDAO.obtenerVehiculosDeOperador(
                operadorId
        );
    }

    // 4. Obtiene los contenedores de un envío
    public List<String> obtenerContenedoresDeEnvio(
            String envioId
    ) {
        return grafoDAO.obtenerContenedoresDeEnvio(
                envioId
        );
    }

    // 5. Obtiene los envíos relacionados con una ubicación
    public List<String> obtenerEnviosPorUbicacion(
            String ubicacionId
    ) {
        return grafoDAO.obtenerEnviosPorUbicacion(
                ubicacionId
        );
    }

    // 6. Obtiene los operadores involucrados en un envío
    public List<String> obtenerOperadorDeEnvio(
            String envioId
    ) {
        return grafoDAO.obtenerOperadorDeEnvio(
                envioId
        );
    }

    // 7. Obtiene los proveedores involucrados en un envío
    public List<String> obtenerProveedorDeEnvio(
            String envioId
    ) {
        return grafoDAO.obtenerProveedorDeEnvio(
                envioId
        );
    }

    // 8. Obtiene el recorrido de un contenedor
    public List<String> obtenerRecorridoContenedor(
            String contenedorId
    ) {
        return grafoDAO.obtenerRecorridoContenedor(
                contenedorId
        );
    }

    // 9. Obtiene la red completa relacionada con un envío
    public List<String> obtenerRedDeEnvio(
            String envioId
    ) {
        return grafoDAO.obtenerRedDeEnvio(
                envioId
        );
    }
}