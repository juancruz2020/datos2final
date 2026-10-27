package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.dao.ShowCollectionsDAO;
import org.example.mongoDB.model.Conversacion;
import org.example.mongoDB.model.Mensaje;
import org.example.mongoDB.model.Usuario;
import org.example.mongoDB.service.LogisticaMongoService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * Punto de acceso a los controllers de MongoDB desde el resto del proyecto.
 * Delega las operaciones sin cambiar sus validaciones ni sus resultados.
 * Los controllers internos se crean bajo demanda: construir esta fachada no abre conexiones
 * ni construye el adaptador de envíos, que a su vez utiliza el controller de Neo4j.
 */
public class ControllerMongoDB {

    private final Perezoso<ClienteController> clientes = new Perezoso<>(ClienteController::new);
    private final Perezoso<UsuarioController> usuarios = new Perezoso<>(UsuarioController::new);
    private final Perezoso<RolController> roles = new Perezoso<>(RolController::new);
    private final Perezoso<PermisosController> permisos = new Perezoso<>(PermisosController::new);
    private final Perezoso<ContenedorController> contenedores = new Perezoso<>(ContenedorController::new);
    private final Perezoso<VehiculoController> vehiculos = new Perezoso<>(VehiculoController::new);
    private final Perezoso<SensorController> sensores = new Perezoso<>(SensorController::new);
    private final Perezoso<EventoLogisticoController> eventos = new Perezoso<>(EventoLogisticoController::new);
    private final Perezoso<IncidenteController> incidentes = new Perezoso<>(IncidenteController::new);
    private final Perezoso<FacturaController> facturas = new Perezoso<>(FacturaController::new);
    private final Perezoso<ReporteController> reportes = new Perezoso<>(ReporteController::new);
    private final Perezoso<ConversacionController> conversaciones = new Perezoso<>(ConversacionController::new);
    private final Perezoso<MensajeController> mensajes = new Perezoso<>(MensajeController::new);
    private final Perezoso<EnvioController> envios = new Perezoso<>(EnvioController::new);
    private final Perezoso<LogisticaMongoService> consultasGenerales = new Perezoso<>(LogisticaMongoService::new);
    private final Perezoso<ShowCollectionsDAO> colecciones = new Perezoso<>(ShowCollectionsDAO::new);


    // =====================================================
    // CLIENTES
    // =====================================================

    // Consulta histórica: conserva el retorno null si el cliente no existe.
    public Document buscarClientePorId(String id) {
        return consultasGenerales.get().buscarClientePorId(id);
    }

    // consultarClientePorId delega en ClienteController y conserva sus validaciones.
    public void crearCliente(String razonSocial, String cuit, String email, String telefono, String calle, String numero, String ciudad, String codigoPostal, String pais) {
        clientes.get().crearCliente(razonSocial, cuit, email, telefono, calle, numero, ciudad, codigoPostal, pais);
    }

    public void modificarCliente(String clienteId, String razonSocial, String cuit, String email, String telefono, String calle, String numero, String ciudad, String codigoPostal, String pais, String estado) {
        clientes.get().modificarCliente(clienteId, razonSocial, cuit, email, telefono, calle, numero, ciudad, codigoPostal, pais, estado);
    }

    public void eliminarCliente(String clienteId) {
        clientes.get().eliminarCliente(clienteId);
    }

    public List<Document> listarClientes() {
        return clientes.get().listarClientes();
    }

    public Document consultarClientePorId(String clienteId) {
        return clientes.get().buscarPorId(clienteId);
    }

    public Document buscarClientePorCuit(String cuit) {
        return clientes.get().buscarPorCuit(cuit);
    }


    // =====================================================
    // USUARIOS
    // =====================================================

    public Usuario crearUsuario(String clienteId, String rolId, String nombre, String apellido, String email, String contraseña) {
        return usuarios.get().crearUsuario(clienteId, rolId, nombre, apellido, email, contraseña);
    }

    public void eliminarUsuario(String usuarioId) {
        usuarios.get().eliminarUsuario(usuarioId);
    }

    public Document buscarUsuarioPorEmail(String email) {
        return usuarios.get().buscarPorEmail(email);
    }

    public List<Document> listarUsuarios() {
        return usuarios.get().listarUsuarios();
    }

    public List<String> obtenerIdsUsuarios() {
        return consultasGenerales.get().obtenerIdsUsuarios();
    }


    // =====================================================
    // ROLES
    // =====================================================

    public List<Document> listarRoles() {
        return roles.get().listarRoles();
    }

    public Document buscarRolPorDescripcion(String descripcion) {
        return roles.get().buscarPorDescripcion(descripcion);
    }


    // =====================================================
    // PERMISOS
    // =====================================================

    public boolean puedeAccederVista(String usuarioId, String vista) {
        return permisos.get().puedeAccederVista(usuarioId, vista);
    }


    // =====================================================
    // CONTENEDORES
    // =====================================================

    public void crearContenedor(String codigoInternacional, String tipo, double capacidad) {
        contenedores.get().crearContenedor(codigoInternacional, tipo, capacidad);
    }

    public void modificarContenedor(String id, String codigoInternacional, String tipo, double capacidad, String estado) {
        contenedores.get().modificarContenedor(id, codigoInternacional, tipo, capacidad, estado);
    }

    public void eliminarContenedor(String id) {
        contenedores.get().eliminarContenedor(id);
    }

    public List<Document> listarContenedores() {
        return contenedores.get().listarContenedores();
    }

    public Document buscarContenedorPorId(String id) {
        return contenedores.get().buscarPorId(id);
    }

    public List<String> obtenerIdsContenedores() {
        return consultasGenerales.get().obtenerIdsContenedores();
    }


    // =====================================================
    // VEHICULOS
    // =====================================================

    public void crearVehiculo(String tipo, String identificacion, String empresaOperadora) {
        vehiculos.get().crearVehiculo(tipo, identificacion, empresaOperadora);
    }

    public void modificarVehiculo(String id, String tipo, String identificacion, String empresaOperadora, String estado) {
        vehiculos.get().modificarVehiculo(id, tipo, identificacion, empresaOperadora, estado);
    }

    public void eliminarVehiculo(String id) {
        vehiculos.get().eliminarVehiculo(id);
    }

    public List<Document> listarVehiculos() {
        return vehiculos.get().listarVehiculos();
    }

    public Document buscarVehiculoPorId(String id) {
        return vehiculos.get().buscarPorId(id);
    }


    // =====================================================
    // SENSORES
    // =====================================================

    public void crearSensor(String contenedorId, String tipo, String fabricante, Date fechaInstalacion) {
        sensores.get().crearSensor(contenedorId, tipo, fabricante, fechaInstalacion);
    }

    public void modificarSensor(String id, String contenedorId, String tipo, String fabricante, Date fechaInstalacion, String estado) {
        sensores.get().modificarSensor(id, contenedorId, tipo, fabricante, fechaInstalacion, estado);
    }

    public void eliminarSensor(String id) {
        sensores.get().eliminarSensor(id);
    }

    public List<Document> listarSensores() {
        return sensores.get().listarSensores();
    }

    public List<String> obtenerIdsSensores() {
        return consultasGenerales.get().obtenerIdsSensores();
    }


    // =====================================================
    // EVENTOS LOGISTICOS
    // =====================================================

    public void crearEventoLogistico(String envioId, String tipoEvento, String ubicacion, String descripcion) {
        eventos.get().crearEvento(envioId, tipoEvento, ubicacion, descripcion);
    }

    public void modificarEventoLogistico(String id, String envioId, String tipoEvento, String ubicacion, String descripcion) {
        eventos.get().modificarEvento(id, envioId, tipoEvento, ubicacion, descripcion);
    }

    public void eliminarEventoLogistico(String id) {
        eventos.get().eliminarEvento(id);
    }

    public List<Document> listarEventosLogisticos() {
        return eventos.get().listarEventos();
    }

    public Document buscarEventoLogisticoPorId(String id) {
        return eventos.get().buscarPorId(id);
    }


    // =====================================================
    // INCIDENTES
    // =====================================================

    public void crearIncidente(String envioId, String tipo, String severidad, String descripcion) {
        incidentes.get().crearIncidente(envioId, tipo, severidad, descripcion);
    }

    public List<Document> listarIncidentes() {
        return incidentes.get().listarIncidentes();
    }

    public Document buscarIncidentePorId(String id) {
        return incidentes.get().buscarPorId(id);
    }

    public List<Document> buscarIncidentesPorEnvio(String envioId) {
        return incidentes.get().buscarPorEnvio(envioId);
    }

    public List<Document> buscarIncidentesAbiertos() {
        return incidentes.get().buscarAbiertos();
    }

    public void modificarIncidente(String id, String envioId, String tipo, String severidad, String estado, String descripcion) {
        incidentes.get().modificarIncidente(id, envioId, tipo, severidad, estado, descripcion);
    }

    public void eliminarIncidente(String id) {
        incidentes.get().eliminarIncidente(id);
    }


    // =====================================================
    // FACTURAS
    // =====================================================

    public void crearFactura(String clienteId, Date fechaEmision, Date fechaVencimiento, BigDecimal importeTotal, String estado) {
        facturas.get().crearFactura(clienteId, fechaEmision, fechaVencimiento, importeTotal, estado);
    }

    public void modificarVencimientoYEstadoFactura(String facturaId, Date fechaVencimiento, String estado) {
        facturas.get().modificarVencimientoYEstado(facturaId, fechaVencimiento, estado);
    }

    public List<Document> listarFacturas() {
        return facturas.get().listarFacturas();
    }

    public Document buscarFacturaPorId(String id) {
        return facturas.get().buscarPorId(id);
    }

    public List<Document> buscarFacturasPorCliente(String clienteId) {
        return facturas.get().buscarPorCliente(clienteId);
    }


    // =====================================================
    // REPORTES
    // =====================================================

    public void crearReporte(String usuarioId, String tipo, String formato) {
        reportes.get().crearReporte(usuarioId, tipo, formato);
    }

    public List<Document> listarReportes() {
        return reportes.get().listarReportes();
    }


    // =====================================================
    // CONVERSACIONES
    // =====================================================

    public Conversacion crearConversacionPrivada(String nombre, String usuario1Id, String usuario2Id) {
        return conversaciones.get().crearConversacionPrivada(nombre, usuario1Id, usuario2Id);
    }

    public Conversacion crearConversacionGrupal(String nombre, List<String> participantesIds) {
        return conversaciones.get().crearConversacionGrupal(nombre, participantesIds);
    }

    public List<Document> obtenerConversacionesUsuario(String usuarioId) {
        return conversaciones.get().obtenerConversacionesUsuario(usuarioId);
    }


    // =====================================================
    // MENSAJES
    // =====================================================

    public Mensaje enviarMensaje(String conversacionId, String remitenteId, String contenido) {
        return mensajes.get().enviarMensaje(conversacionId, remitenteId, contenido);
    }

    public List<Document> obtenerHistorialConversacion(String conversacionId) {
        return mensajes.get().obtenerHistorial(conversacionId);
    }

    public void modificarMensaje(String mensajeId, String usuarioActualId, String nuevoContenido) {
        mensajes.get().modificarMensaje(mensajeId, usuarioActualId, nuevoContenido);
    }

    public void eliminarMensaje(String mensajeId, String usuarioActualId) {
        mensajes.get().eliminarMensaje(mensajeId, usuarioActualId);
    }


    // =====================================================
    // ENVIOS: ADAPTADOR DE CONSULTAS DE NEO4J
    // =====================================================

    public List<Document> listarEnvios() {
        return envios.get().listarEnvios();
    }

    public Document buscarEnvioPorId(String id) {
        return envios.get().buscarPorId(id);
    }

    public List<String> obtenerIdsEnvios() {
        return envios.get().obtenerTodosLosIds();
    }

    public List<Document> buscarEnviosPorCliente(String clienteId) {
        return envios.get().buscarPorCliente(clienteId);
    }

    public List<Document> buscarEnviosPorEstado(String estado) {
        return envios.get().buscarPorEstado(estado);
    }

    public List<Document> buscarEnviosPorPais(String pais) {
        return envios.get().buscarPorPais(pais);
    }

    public List<Document> buscarEnviosDemorados() {
        return envios.get().buscarDemorados();
    }


    // =====================================================
    // COLECCIONES
    // =====================================================

    public List<String> mostrarCollections() {
        return colecciones.get().mostrarCollections();
    }

    /** Una instancia por dependencia y fachada; evita la construcción circular Mongo / Neo4j. */
    private static final class Perezoso<T> {
        private final Supplier<T> crear;
        private T instancia;

        private Perezoso(Supplier<T> crear) {
            this.crear = crear;
        }

        private synchronized T get() {
            if (instancia == null) {
                instancia = crear.get();
            }
            return instancia;
        }
    }
}
