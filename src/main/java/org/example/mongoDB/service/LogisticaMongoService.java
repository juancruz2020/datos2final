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
}