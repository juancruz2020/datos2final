package org.example.neo4j.service;

import org.example.consola.TablaConsola;

import org.example.logging.AppLog;

import org.example.neo4j.dao.ClienteNeo4jDAO;

import org.example.neo4j.dao.EnvioNeo4jDAO;
import org.example.neo4j.dao.GrafoNeo4jDAO;


import org.example.neo4j.dao.VehiculoNeo4jDAO;

import org.example.neo4j.model.Cliente;

import org.example.neo4j.model.Envio;

import org.example.neo4j.model.Vehiculo;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class LogisticaNeo4jService {

    private final ClienteNeo4jDAO clienteDAO;
    private final EnvioNeo4jDAO envioDAO;
    private final VehiculoNeo4jDAO vehiculoDAO;

    private final GrafoNeo4jDAO grafoDAO;
    private final DisponibilidadContenedores disponibilidad;

    public LogisticaNeo4jService() {
        clienteDAO = new ClienteNeo4jDAO();
        envioDAO = new EnvioNeo4jDAO();
        vehiculoDAO = new VehiculoNeo4jDAO();

        grafoDAO = new GrafoNeo4jDAO();
        var contenedores = new org.example.mongoDB.dao.ContenedorMongoDAO();
        disponibilidad = new DisponibilidadContenedores(new DisponibilidadContenedores.Repositorio() {
            public org.bson.Document reservar(String id, String envio, boolean propio) {
                return contenedores.reservarParaEnvio(id, envio, propio);
            }
            public void restaurar(org.bson.Document anterior, String envio) {
                contenedores.restaurarReserva(anterior, envio);
            }
            public void liberar(String id, String envio) {
                contenedores.liberarDeEnvio(id, envio);
            }
            public boolean ocupadoPorOtro(String id, String envio) {
                return envioDAO.contenedorOcupadoPorOtro(id, envio);
            }
        });
        envioDAO.normalizarEnviosExistentes();
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

        if (envio.getClienteId() == null || envio.getClienteId().isBlank()
                || envio.getContenedoresIds() == null || envio.getContenedoresIds().isEmpty()) {
            throw new IllegalArgumentException("El cliente y al menos un contenedor son obligatorios.");
        }
        validarFechasEnvio(envio);
        Map<String, Object> existente = envioDAO.buscarPorId(envio.getId());
        List<String> anteriores = existente == null ? List.of() : obtenerIdsContenedores(existente);
        disponibilidad.guardar(envio.getId(), anteriores, envio.getContenedoresIds(),
                envio.getEstado(), () -> envioDAO.guardarCompleto(envio));
        AppLog.info(LogisticaNeo4jService.class,
                "Envío y relaciones guardados; reservas de contenedores sincronizadas.");
    }

    private void validarFechasEnvio(Envio envio) {
        LocalDate salida = parsearFechaEnvio(envio.getFechaSalida(), "salida");
        LocalDate entrega = parsearFechaEnvio(envio.getFechaEntrega(), "entrega");
        if (salida != null && entrega != null && salida.isAfter(entrega)) {
            throw new IllegalArgumentException(
                    "La fecha de salida no puede ser posterior a la fecha de entrega."
            );
        }
    }

    private LocalDate parsearFechaEnvio(String fecha, String campo) {
        if (fecha == null || fecha.isBlank()) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha de " + campo + " debe tener formato AAAA-MM-DD.", e
            );
        }
    }

    private List<String> obtenerIdsContenedores(Map<String, Object> envio) {
        Object ids = envio.get("contenedoresIds");
        return ids instanceof List<?> lista
                ? lista.stream().map(Object::toString).toList() : List.of();
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
        return TablaConsola.mostrar("LogisticaNeo4jService.listarEnvios", envioDAO.listarTodos());
    }

    public Map<String, Object> buscarEnvioPorId(String id) {
        return TablaConsola.mostrar("LogisticaNeo4jService.buscarEnvioPorId", envioDAO.buscarPorId(id));
    }

    public void eliminarEnvio(String id) {
        Map<String, Object> existente = envioDAO.buscarPorId(id);
        if (existente == null) {
            return;
        }
        envioDAO.eliminarEnvio(id);
        disponibilidad.liberar(id, obtenerIdsContenedores(existente));
        AppLog.info(LogisticaNeo4jService.class,
                "Envío eliminado y contenedores liberados.");
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

    public List<String> obtenerRutaEnvio(String envioId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerRutaEnvio", grafoDAO.obtenerRutaEnvio(envioId)); }
    public List<String> obtenerEnviosDeCliente(String clienteId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerEnviosDeCliente", grafoDAO.obtenerEnviosDeCliente(clienteId)); }
    public List<String> obtenerVehiculosDeOperador(String operadorId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerVehiculosDeOperador", grafoDAO.obtenerVehiculosDeOperador(operadorId)); }
    public List<String> obtenerContenedoresDeEnvio(String envioId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerContenedoresDeEnvio", grafoDAO.obtenerContenedoresDeEnvio(envioId)); }
    public List<String> obtenerEnviosPorUbicacion(String ubicacionId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerEnviosPorUbicacion", grafoDAO.obtenerEnviosPorUbicacion(ubicacionId)); }
    public List<String> obtenerOperadorDeEnvio(String envioId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerOperadorDeEnvio", grafoDAO.obtenerOperadorDeEnvio(envioId)); }
    public List<String> obtenerProveedorDeEnvio(String envioId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerProveedorDeEnvio", grafoDAO.obtenerProveedorDeEnvio(envioId)); }
    public List<String> obtenerRecorridoContenedor(String contenedorId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerRecorridoContenedor", grafoDAO.obtenerRecorridoContenedor(contenedorId)); }
    public List<String> obtenerRedDeEnvio(String envioId) { return TablaConsola.mostrar("LogisticaNeo4jService.obtenerRedDeEnvio", grafoDAO.obtenerRedDeEnvio(envioId)); }

    public void modificarEnvio(Envio envio) {
        if (envio == null || envio.getId() == null) {
            throw new IllegalArgumentException("El envío y su ID son obligatorios.");
        }
        if (envioDAO.buscarPorId(envio.getId()) == null) {
            throw new IllegalArgumentException("No existe un envío con ID: " + envio.getId());
        }
        crearEnvio(envio);
    }


}
