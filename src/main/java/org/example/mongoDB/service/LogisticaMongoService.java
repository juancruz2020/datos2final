package org.example.mongoDB.service;

import org.bson.Document;
import org.example.mongoDB.dao.*;
import org.example.mongoDB.model.*;

import java.util.List;

public class LogisticaMongoService {

    private final ClienteMongoDAO clienteDAO;
    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private final ContenedorMongoDAO contenedorDAO;
    private final SensorMongoDAO sensorDAO;
    private final EnvioMongoDAO envioDAO;
    private final EventoLogisticoMongoDAO eventoLogisticoDAO;
    private final IncidenteMongoDAO incidenteDAO;
    private final AlertaMongoDAO alertaDAO;
    private final ReporteMongoDAO reporteDAO;
    private final FacturaMongoDAO facturaDAO;
    private final PagoMongoDAO pagoDAO;
    private final CuentaCorrienteMongoDAO cuentaCorrienteDAO;


    // =========================
    // CONSTRUCTOR
    // =========================

    public LogisticaMongoService() {

        this.clienteDAO = new ClienteMongoDAO();
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
        this.contenedorDAO = new ContenedorMongoDAO();
        this.sensorDAO = new SensorMongoDAO();
        this.envioDAO = new EnvioMongoDAO();
        this.eventoLogisticoDAO = new EventoLogisticoMongoDAO();
        this.incidenteDAO = new IncidenteMongoDAO();
        this.alertaDAO = new AlertaMongoDAO();
        this.reporteDAO = new ReporteMongoDAO();
        this.facturaDAO = new FacturaMongoDAO();
        this.pagoDAO = new PagoMongoDAO();
        this.cuentaCorrienteDAO = new CuentaCorrienteMongoDAO();
    }


    // =========================
    // CLIENTES
    // =========================

    public void agregarCliente(Cliente cliente) {
        clienteDAO.agregar(cliente);
    }

    public void modificarCliente(Cliente cliente) {
        clienteDAO.modificar(cliente);
    }

    public void eliminarCliente(String id) {
        clienteDAO.eliminar(id);
    }

    public Document buscarClientePorId(String id) {
        return clienteDAO.buscarPorId(id);
    }

    public List<Document> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public Document buscarClientePorCuit(String cuit) {
        return clienteDAO.buscarPorCuit(cuit);
    }


    // =========================
    // USUARIOS
    // =========================

    public void agregarUsuario(Usuario usuario) {
        usuarioDAO.agregar(usuario);
    }

    public void modificarUsuario(Usuario usuario) {
        usuarioDAO.modificar(usuario);
    }

    public void eliminarUsuario(String id) {
        usuarioDAO.eliminar(id);
    }

    public Document buscarUsuarioPorId(String id) {
        return usuarioDAO.buscarPorId(id);
    }

    public List<Document> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public Document buscarUsuarioPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }

    public List<Document> buscarUsuariosPorCliente(String clienteId) {
        return usuarioDAO.buscarPorCliente(clienteId);
    }


    // =========================
    // ROLES
    // =========================

    public void agregarRol(Rol rol) {
        rolDAO.agregar(rol);
    }

    public void modificarRol(Rol rol) {
        rolDAO.modificar(rol);
    }

    public void eliminarRol(String id) {
        rolDAO.eliminar(id);
    }

    public Document buscarRolPorId(String id) {
        return rolDAO.buscarPorId(id);
    }

    public List<Document> listarRoles() {
        return rolDAO.listarTodos();
    }

    public Document buscarRolPorDescripcion(String descripcion) {
        return rolDAO.buscarPorDescripcion(descripcion);
    }


    // =========================
    // CONTENEDORES
    // =========================

    public void agregarContenedor(Contenedor contenedor) {
        contenedorDAO.agregar(contenedor);
    }

    public void modificarContenedor(Contenedor contenedor) {
        contenedorDAO.modificar(contenedor);
    }

    public void eliminarContenedor(String id) {
        contenedorDAO.eliminar(id);
    }

    public Document buscarContenedorPorId(String id) {
        return contenedorDAO.buscarPorId(id);
    }

    public List<Document> listarContenedores() {
        return contenedorDAO.listarTodos();
    }

    public Document buscarContenedorPorCodigo(String codigo) {
        return contenedorDAO.buscarPorCodigo(codigo);
    }

    public List<Document> buscarContenedoresPorEstado(String estado) {
        return contenedorDAO.buscarPorEstado(estado);
    }


    // =========================
    // SENSORES
    // =========================

    public void agregarSensor(Sensor sensor) {
        sensorDAO.agregar(sensor);
    }

    public void modificarSensor(Sensor sensor) {
        sensorDAO.modificar(sensor);
    }

    public void eliminarSensor(String id) {
        sensorDAO.eliminar(id);
    }

    public Document buscarSensorPorId(String id) {
        return sensorDAO.buscarPorId(id);
    }

    public List<Document> listarSensores() {
        return sensorDAO.listarTodos();
    }

    public List<Document> buscarSensoresPorContenedor(String contenedorId) {
        return sensorDAO.buscarPorContenedor(contenedorId);
    }

    public List<Document> buscarSensoresPorEstado(String estado) {
        return sensorDAO.buscarPorEstado(estado);
    }


    // =========================
    // ENVIOS
    // =========================

    public void agregarEnvio(Envio envio) {
        envioDAO.agregar(envio);
    }

    public void modificarEnvio(Envio envio) {
        envioDAO.modificar(envio);
    }

    public void eliminarEnvio(String id) {
        envioDAO.eliminar(id);
    }

    public Document buscarEnvioPorId(String id) {
        return envioDAO.buscarPorId(id);
    }

    public List<Document> listarEnvios() {
        return envioDAO.listarTodos();
    }

    public List<Document> buscarEnviosPorCliente(String clienteId) {
        return envioDAO.buscarPorCliente(clienteId);
    }

    public List<Document> buscarEnviosPorEstado(String estado) {
        return envioDAO.buscarPorEstado(estado);
    }

    public List<Document> buscarEnviosPorPais(String pais) {
        return envioDAO.buscarPorPais(pais);
    }

    public List<Document> buscarEnviosDemorados() {
        return envioDAO.buscarDemorados();
    }


    // =========================
    // EVENTOS LOGISTICOS
    // =========================

    public void agregarEventoLogistico(EventoLogistico evento) {
        eventoLogisticoDAO.agregar(evento);
    }

    public void modificarEventoLogistico(EventoLogistico evento) {
        eventoLogisticoDAO.modificar(evento);
    }

    public void eliminarEventoLogistico(String id) {
        eventoLogisticoDAO.eliminar(id);
    }

    public Document buscarEventoLogisticoPorId(String id) {
        return eventoLogisticoDAO.buscarPorId(id);
    }

    public List<Document> listarEventosLogisticos() {
        return eventoLogisticoDAO.listarTodos();
    }

    public List<Document> buscarEventosLogisticosPorEnvio(String envioId) {
        return eventoLogisticoDAO.buscarPorEnvio(envioId);
    }

    public List<Document> buscarEventosLogisticosPorTipo(String tipoEvento) {
        return eventoLogisticoDAO.buscarPorTipo(tipoEvento);
    }


    // =========================
    // INCIDENTES
    // =========================

    public void agregarIncidente(Incidente incidente) {
        incidenteDAO.agregar(incidente);
    }

    public void modificarIncidente(Incidente incidente) {
        incidenteDAO.modificar(incidente);
    }

    public void eliminarIncidente(String id) {
        incidenteDAO.eliminar(id);
    }

    public Document buscarIncidentePorId(String id) {
        return incidenteDAO.buscarPorId(id);
    }

    public List<Document> listarIncidentes() {
        return incidenteDAO.listarTodos();
    }

    public List<Document> buscarIncidentesPorEnvio(String envioId) {
        return incidenteDAO.buscarPorEnvio(envioId);
    }

    public List<Document> buscarIncidentesAbiertos() {
        return incidenteDAO.buscarAbiertos();
    }


    // =========================
    // ALERTAS
    // =========================

    public void agregarAlerta(Alerta alerta) {
        alertaDAO.agregar(alerta);
    }

    public void modificarAlerta(Alerta alerta) {
        alertaDAO.modificar(alerta);
    }

    public void eliminarAlerta(String id) {
        alertaDAO.eliminar(id);
    }

    public Document buscarAlertaPorId(String id) {
        return alertaDAO.buscarPorId(id);
    }

    public List<Document> listarAlertas() {
        return alertaDAO.listarTodos();
    }

    public List<Document> buscarAlertasPorSensor(String sensorId) {
        return alertaDAO.buscarPorSensor(sensorId);
    }

    public List<Document> buscarAlertasPorEvento(String eventoId) {
        return alertaDAO.buscarPorEvento(eventoId);
    }

    public List<Document> buscarAlertasPorEstado(String estado) {
        return alertaDAO.buscarPorEstado(estado);
    }


    // =========================
    // REPORTES
    // =========================

    public void agregarReporte(Reporte reporte) {
        reporteDAO.agregar(reporte);
    }

    public void modificarReporte(Reporte reporte) {
        reporteDAO.modificar(reporte);
    }

    public void eliminarReporte(String id) {
        reporteDAO.eliminar(id);
    }

    public Document buscarReportePorId(String id) {
        return reporteDAO.buscarPorId(id);
    }

    public List<Document> listarReportes() {
        return reporteDAO.listarTodos();
    }

    public List<Document> buscarReportesPorUsuario(String usuarioId) {
        return reporteDAO.buscarPorUsuario(usuarioId);
    }

    public List<Document> buscarReportesPorTipo(String tipo) {
        return reporteDAO.buscarPorTipo(tipo);
    }

    public List<Document> buscarReportesPorEstado(String estado) {
        return reporteDAO.buscarPorEstado(estado);
    }


    // =========================
    // FACTURAS
    // =========================

    public void agregarFactura(Factura factura) {
        facturaDAO.agregar(factura);
    }

    public void modificarFactura(Factura factura) {
        facturaDAO.modificar(factura);
    }

    public void eliminarFactura(String id) {
        facturaDAO.eliminar(id);
    }

    public Document buscarFacturaPorId(String id) {
        return facturaDAO.buscarPorId(id);
    }

    public List<Document> listarFacturas() {
        return facturaDAO.listarTodos();
    }

    public List<Document> buscarFacturasPorCliente(String clienteId) {
        return facturaDAO.buscarPorCliente(clienteId);
    }

    public List<Document> buscarFacturasPorEstado(String estado) {
        return facturaDAO.buscarPorEstado(estado);
    }


    // =========================
    // PAGOS
    // =========================

    public void agregarPago(Pago pago) {
        pagoDAO.agregar(pago);
    }

    public void modificarPago(Pago pago) {
        pagoDAO.modificar(pago);
    }

    public void eliminarPago(String id) {
        pagoDAO.eliminar(id);
    }

    public Document buscarPagoPorId(String id) {
        return pagoDAO.buscarPorId(id);
    }

    public List<Document> listarPagos() {
        return pagoDAO.listarTodos();
    }

    public List<Document> buscarPagosPorFactura(String facturaId) {
        return pagoDAO.buscarPorFactura(facturaId);
    }

    public List<Document> buscarPagosPorMedioPago(String medioPago) {
        return pagoDAO.buscarPorMedioPago(medioPago);
    }


    // =========================
    // CUENTAS CORRIENTES
    // =========================

    public void agregarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        cuentaCorrienteDAO.agregar(cuentaCorriente);
    }

    public void modificarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        cuentaCorrienteDAO.modificar(cuentaCorriente);
    }

    public void eliminarCuentaCorriente(String id) {
        cuentaCorrienteDAO.eliminar(id);
    }

    public Document buscarCuentaCorrientePorId(String id) {
        return cuentaCorrienteDAO.buscarPorId(id);
    }

    public List<Document> listarCuentasCorrientes() {
        return cuentaCorrienteDAO.listarTodos();
    }

    public Document buscarCuentaCorrientePorCliente(String clienteId) {
        return cuentaCorrienteDAO.buscarPorCliente(clienteId);
    }
}