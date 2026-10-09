package org.example.neo4j.service;

import org.example.neo4j.dao.ClienteNeo4jDAO;

import org.example.neo4j.dao.EnvioNeo4jDAO;
import org.example.neo4j.dao.GrafoNeo4jDAO;


import org.example.neo4j.dao.VehiculoNeo4jDAO;

import org.example.neo4j.model.Cliente;

import org.example.neo4j.model.Envio;

import org.example.neo4j.model.Vehiculo;

import java.util.List;
import java.util.Map;

public class LogisticaNeo4jService {

    private final ClienteNeo4jDAO clienteDAO;
    private final EnvioNeo4jDAO envioDAO;
    private final VehiculoNeo4jDAO vehiculoDAO;

    private final GrafoNeo4jDAO grafoDAO;

    public LogisticaNeo4jService() {
        clienteDAO = new ClienteNeo4jDAO();
        envioDAO = new EnvioNeo4jDAO();
        vehiculoDAO = new VehiculoNeo4jDAO();

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
        if (envio == null || envio.getId() == null || envio.getId().isBlank()
                || envio.getOrigen() == null || envio.getDestino() == null) {
            throw new IllegalArgumentException(
                    "El envío, su ID, origen y destino son obligatorios."
            );
        }

        envioDAO.crearEnvio(
                envio.getId(),
                envio.getClienteId(),
                envio.getFechaCreacion(),
                envio.getOrigen().getCiudad(),
                envio.getOrigen().getPais(),
                envio.getDestino().getCiudad(),
                envio.getDestino().getPais(),
                envio.getEstado(),
                envio.getPrioridad()
        );

        if (envio.getClienteId() != null) {
            relacionarClienteEnvio(
                    envio.getClienteId(),
                    envio.getId()
            );
        }

        // El envío se conecta directamente con sus ubicaciones; no hay nodos Tramo.
        if (envio.getOrigen() != null) {
            relacionarEnvioOrigen(envio.getId(),
                    envio.getOrigen().getCiudad(), envio.getOrigen().getPais());
        }
        if (envio.getDestino() != null) {
            relacionarEnvioDestino(envio.getId(),
                    envio.getDestino().getCiudad(), envio.getDestino().getPais());
        }

        if (envio.getContenedoresIds() != null) {
            for (String contenedorId : envio.getContenedoresIds()) {
                if (contenedorId == null || contenedorId.isBlank()) {
                    continue;
                }
                relacionarEnvioContenedor(
                        envio.getId(),
                        contenedorId
                );
            }
        }
    }

    public void crearVehiculo(Vehiculo vehiculo) {
        vehiculoDAO.crear(vehiculo);
    }

    /** Crea o actualiza la representación del vehículo en Neo4j usando el ID de MongoDB. */
    public void sincronizarVehiculoMongo(String idMongo, String identificacion, String tipo) {
        if (idMongo == null || idMongo.isBlank()) {
            throw new IllegalArgumentException("El ID de MongoDB del vehículo es obligatorio.");
        }
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación del vehículo es obligatoria.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo del vehículo es obligatorio.");
        }
        crearVehiculo(new Vehiculo(idMongo, identificacion.trim(), tipo.trim()));
    }

    /** Asegura el nodo Vehiculo y crea la relación directa Envio-[:UTILIZA]->Vehiculo. */
    public void vincularVehiculoAEnvio(String envioId, String vehiculoIdMongo, String identificacion, String tipo) {
        if (envioId == null || envioId.isBlank()) {
            throw new IllegalArgumentException("El ID del envío es obligatorio.");
        }
        sincronizarVehiculoMongo(vehiculoIdMongo, identificacion, tipo);
        relacionarEnvioVehiculo(envioId, vehiculoIdMongo);
    }





    // =====================================================
    // CONSULTAS PARA LA INTERFAZ
    // =====================================================


    public List<Map<String, Object>> listarEnvios() {
        return envioDAO.listarTodos();
    }

    public Map<String, Object> buscarEnvioPorId(String id) {
        return envioDAO.buscarPorId(id);
    }

    public void eliminarEnvio(String id) {
        envioDAO.eliminarEnvio(id);
    }

    // =====================================================
    // RELACIONES
    // =====================================================

    public void relacionarClienteEnvio(
            String clienteId,
            String envioId
    ) {
        grafoDAO.clienteRealizaEnvio(clienteId, envioId);
    }

    public void relacionarEnvioVehiculo(String envioId, String vehiculoId) {
        grafoDAO.envioUsaVehiculo(envioId, vehiculoId);
    }

    public void relacionarEnvioOrigen(String envioId, String ciudad, String pais) {
        grafoDAO.envioSaleDe(envioId, ciudad, pais);
    }

    public void relacionarEnvioDestino(String envioId, String ciudad, String pais) {
        grafoDAO.envioLlegaA(envioId, ciudad, pais);
    }

    public void relacionarOperadorVehiculo(String operadorId, String vehiculoId) {
        grafoDAO.operadorOperaVehiculo(operadorId, vehiculoId);
    }
    public void relacionarProveedorVehiculo(String proveedorId, String vehiculoId) {
        grafoDAO.proveedorProveeVehiculo(proveedorId, vehiculoId);
    }


    public void relacionarEnvioContenedor(
            String envioId,
            String contenedorId
    ) {
        grafoDAO.envioTransportaContenedor(envioId, contenedorId);
    }

    public void relacionarContenedorSensor(String contenedorId, String sensorId) {
        grafoDAO.contenedorTieneSensor(contenedorId, sensorId);
    }

    public List<String> obtenerRutaEnvio(String envioId) { return grafoDAO.obtenerRutaEnvio(envioId); }
    public List<String> obtenerEnviosDeCliente(String clienteId) { return grafoDAO.obtenerEnviosDeCliente(clienteId); }
    public List<String> obtenerVehiculosDeOperador(String operadorId) { return grafoDAO.obtenerVehiculosDeOperador(operadorId); }
    public List<String> obtenerContenedoresDeEnvio(String envioId) { return grafoDAO.obtenerContenedoresDeEnvio(envioId); }
    public List<String> obtenerEnviosPorUbicacion(String ubicacionId) { return grafoDAO.obtenerEnviosPorUbicacion(ubicacionId); }
    public List<String> obtenerOperadorDeEnvio(String envioId) { return grafoDAO.obtenerOperadorDeEnvio(envioId); }
    public List<String> obtenerProveedorDeEnvio(String envioId) { return grafoDAO.obtenerProveedorDeEnvio(envioId); }
    public List<String> obtenerRecorridoContenedor(String contenedorId) { return grafoDAO.obtenerRecorridoContenedor(contenedorId); }
    public List<String> obtenerRedDeEnvio(String envioId) { return grafoDAO.obtenerRedDeEnvio(envioId); }

    public void modificarEnvio(Envio envio) {
        if (envio == null || envio.getId() == null) {
            throw new IllegalArgumentException("El envío y su ID son obligatorios.");
        }
        if (envioDAO.buscarPorId(envio.getId()) == null) {
            throw new IllegalArgumentException("No existe un envío con ID: " + envio.getId());
        }
        envioDAO.eliminarRelacionesContenedores(envio.getId());
        envioDAO.eliminarRelacionCliente(envio.getId());
        envioDAO.eliminarRelacionesVehiculo(envio.getId());
        envioDAO.eliminarRelacionesUbicacion(envio.getId());
        crearEnvio(envio);
        envioDAO.eliminarNodosHuerfanos();
    }


}
