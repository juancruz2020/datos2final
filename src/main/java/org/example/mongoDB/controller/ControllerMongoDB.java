package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.DatosPorDefecto.GeneradorDatosPruebaMongo;
import org.example.mongoDB.model.*;
import org.example.mongoDB.service.LogisticaMongoService;

import java.util.List;

public class ControllerMongoDB {

    private final LogisticaMongoService service;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ControllerMongoDB() {
        this.service = new LogisticaMongoService();
    }


    // =========================
    // CLIENTES
    // =========================

    public void agregarCliente(Cliente cliente) {
        service.agregarCliente(cliente);
    }

    public void modificarCliente(Cliente cliente) {
        service.modificarCliente(cliente);
    }

    public void eliminarCliente(String id) {
        service.eliminarCliente(id);
    }

    public Document buscarClientePorId(String id) {
        return service.buscarClientePorId(id);
    }

    public List<Document> listarClientes() {
        return service.listarClientes();
    }

    public List<String> obtenerIdsClientes() {
        return service.obtenerIdsClientes();
    }

    public boolean existeClientePorId(String id) {
        return service.existeClientePorId(id);
    }

    public Document buscarClientePorCuit(String cuit) {
        return service.buscarClientePorCuit(cuit);
    }


    // =========================
    // USUARIOS
    // =========================

    public void agregarUsuario(Usuario usuario) {
        service.agregarUsuario(usuario);
    }

    public void modificarUsuario(Usuario usuario) {
        service.modificarUsuario(usuario);
    }

    public void eliminarUsuario(String id) {
        service.eliminarUsuario(id);
    }

    public Document buscarUsuarioPorId(String id) {
        return service.buscarUsuarioPorId(id);
    }

    public List<Document> listarUsuarios() {
        return service.listarUsuarios();
    }

    public List<String> obtenerIdsUsuarios() {
        return service.obtenerIdsUsuarios();
    }

    public boolean existeUsuarioPorId(String id) {
        return service.existeUsuarioPorId(id);
    }

    public Document buscarUsuarioPorEmail(String email) {
        return service.buscarUsuarioPorEmail(email);
    }

    public List<Document> buscarUsuariosPorCliente(String clienteId) {
        return service.buscarUsuariosPorCliente(clienteId);
    }


    // =========================
    // BUSCAR USUARIOS POR ROL
    // =========================

    public List<Document> buscarUsuariosPorRol(String rolId) {
        return service.buscarUsuariosPorRol(rolId);
    }


    // =========================
    // CAMBIAR ROL DE USUARIO
    // =========================

    public void cambiarRolUsuario(String usuarioId, String rolId) {
        service.cambiarRolUsuario(usuarioId, rolId);
    }


    // =========================
    // BUSCAR ROL DE UN USUARIO
    // =========================

    public Document buscarRolDeUsuario(String usuarioId) {
        return service.buscarRolDeUsuario(usuarioId);
    }


    // =========================
    // ROLES
    // =========================

    public void agregarRol(Rol rol) {
        service.agregarRol(rol);
    }

    public void modificarRol(Rol rol) {
        service.modificarRol(rol);
    }

    public void eliminarRol(String id) {
        service.eliminarRol(id);
    }

    public Document buscarRolPorId(String id) {
        return service.buscarRolPorId(id);
    }

    public List<Document> listarRoles() {
        return service.listarRoles();
    }

    public List<String> obtenerIdsRoles() {
        return service.obtenerIdsRoles();
    }

    public boolean existeRolPorId(String id) {
        return service.existeRolPorId(id);
    }

    public Document buscarRolPorDescripcion(String descripcion) {
        return service.buscarRolPorDescripcion(descripcion);
    }


    // =========================
    // CONTENEDORES
    // =========================

    public void agregarContenedor(Contenedor contenedor) {
        service.agregarContenedor(contenedor);
    }

    public void modificarContenedor(Contenedor contenedor) {
        service.modificarContenedor(contenedor);
    }

    public void eliminarContenedor(String id) {
        service.eliminarContenedor(id);
    }

    public Document buscarContenedorPorId(String id) {
        return service.buscarContenedorPorId(id);
    }

    public List<Document> listarContenedores() {
        return service.listarContenedores();
    }

    public List<String> obtenerIdsContenedores() {
        return service.obtenerIdsContenedores();
    }

    public boolean existeContenedorPorId(String id) {
        return service.existeContenedorPorId(id);
    }

    public Document buscarContenedorPorCodigo(String codigo) {
        return service.buscarContenedorPorCodigo(codigo);
    }

    public List<Document> buscarContenedoresPorEstado(String estado) {
        return service.buscarContenedoresPorEstado(estado);
    }


    // =========================
    // SENSORES
    // =========================

    public void agregarSensor(Sensor sensor) {
        service.agregarSensor(sensor);
    }

    public void modificarSensor(Sensor sensor) {
        service.modificarSensor(sensor);
    }

    public void eliminarSensor(String id) {
        service.eliminarSensor(id);
    }

    public Document buscarSensorPorId(String id) {
        return service.buscarSensorPorId(id);
    }

    public List<Document> listarSensores() {
        return service.listarSensores();
    }

    public List<String> obtenerIdsSensores() {
        return service.obtenerIdsSensores();
    }

    public boolean existeSensorPorId(String id) {
        return service.existeSensorPorId(id);
    }

    public List<Document> buscarSensoresPorContenedor(String contenedorId) {
        return service.buscarSensoresPorContenedor(contenedorId);
    }

    public List<Document> buscarSensoresPorEstado(String estado) {
        return service.buscarSensoresPorEstado(estado);
    }


    // =========================
    // ENVIOS
    // =========================

    public void agregarEnvio(Envio envio) {
        service.agregarEnvio(envio);
    }

    public void modificarEnvio(Envio envio) {
        service.modificarEnvio(envio);
    }

    public void eliminarEnvio(String id) {
        service.eliminarEnvio(id);
    }

    public Document buscarEnvioPorId(String id) {
        return service.buscarEnvioPorId(id);
    }

    public List<Document> listarEnvios() {
        return service.listarEnvios();
    }

    public List<String> obtenerIdsEnvios() {
        return service.obtenerIdsEnvios();
    }

    public boolean existeEnvioPorId(String id) {
        return service.existeEnvioPorId(id);
    }

    public List<Document> buscarEnviosPorCliente(String clienteId) {
        return service.buscarEnviosPorCliente(clienteId);
    }

    public List<Document> buscarEnviosPorEstado(String estado) {
        return service.buscarEnviosPorEstado(estado);
    }

    public List<Document> buscarEnviosPorPais(String pais) {
        return service.buscarEnviosPorPais(pais);
    }

    public List<Document> buscarEnviosDemorados() {
        return service.buscarEnviosDemorados();
    }


    // =========================
    // EVENTOS LOGISTICOS
    // =========================

    public void agregarEventoLogistico(EventoLogistico evento) {
        service.agregarEventoLogistico(evento);
    }

    public void modificarEventoLogistico(EventoLogistico evento) {
        service.modificarEventoLogistico(evento);
    }

    public void eliminarEventoLogistico(String id) {
        service.eliminarEventoLogistico(id);
    }

    public Document buscarEventoLogisticoPorId(String id) {
        return service.buscarEventoLogisticoPorId(id);
    }

    public List<Document> listarEventosLogisticos() {
        return service.listarEventosLogisticos();
    }

    public List<String> obtenerIdsEventosLogisticos() {
        return service.obtenerIdsEventosLogisticos();
    }

    public boolean existeEventoLogisticoPorId(String id) {
        return service.existeEventoLogisticoPorId(id);
    }

    public List<Document> buscarEventosLogisticosPorEnvio(String envioId) {
        return service.buscarEventosLogisticosPorEnvio(envioId);
    }

    public List<Document> buscarEventosLogisticosPorTipo(String tipoEvento) {
        return service.buscarEventosLogisticosPorTipo(tipoEvento);
    }


    // =========================
    // INCIDENTES
    // =========================

    public void agregarIncidente(Incidente incidente) {
        service.agregarIncidente(incidente);
    }

    public void modificarIncidente(Incidente incidente) {
        service.modificarIncidente(incidente);
    }

    public void eliminarIncidente(String id) {
        service.eliminarIncidente(id);
    }

    public Document buscarIncidentePorId(String id) {
        return service.buscarIncidentePorId(id);
    }

    public List<Document> listarIncidentes() {
        return service.listarIncidentes();
    }

    public List<String> obtenerIdsIncidentes() {
        return service.obtenerIdsIncidentes();
    }

    public boolean existeIncidentePorId(String id) {
        return service.existeIncidentePorId(id);
    }

    public List<Document> buscarIncidentesPorEnvio(String envioId) {
        return service.buscarIncidentesPorEnvio(envioId);
    }

    public List<Document> buscarIncidentesAbiertos() {
        return service.buscarIncidentesAbiertos();
    }


    // =========================
    // ALERTAS
    // =========================

    public void agregarAlerta(Alerta alerta) {
        service.agregarAlerta(alerta);
    }

    public void modificarAlerta(Alerta alerta) {
        service.modificarAlerta(alerta);
    }

    public void eliminarAlerta(String id) {
        service.eliminarAlerta(id);
    }

    public Document buscarAlertaPorId(String id) {
        return service.buscarAlertaPorId(id);
    }

    public List<Document> listarAlertas() {
        return service.listarAlertas();
    }

    public List<String> obtenerIdsAlertas() {
        return service.obtenerIdsAlertas();
    }

    public boolean existeAlertaPorId(String id) {
        return service.existeAlertaPorId(id);
    }

    public List<Document> buscarAlertasPorSensor(String sensorId) {
        return service.buscarAlertasPorSensor(sensorId);
    }

    public List<Document> buscarAlertasPorEvento(String eventoId) {
        return service.buscarAlertasPorEvento(eventoId);
    }

    public List<Document> buscarAlertasPorEstado(String estado) {
        return service.buscarAlertasPorEstado(estado);
    }


    // =========================
    // REPORTES
    // =========================

    public void agregarReporte(Reporte reporte) {
        service.agregarReporte(reporte);
    }

    public void modificarReporte(Reporte reporte) {
        service.modificarReporte(reporte);
    }

    public void eliminarReporte(String id) {
        service.eliminarReporte(id);
    }

    public Document buscarReportePorId(String id) {
        return service.buscarReportePorId(id);
    }

    public List<Document> listarReportes() {
        return service.listarReportes();
    }

    public List<String> obtenerIdsReportes() {
        return service.obtenerIdsReportes();
    }

    public boolean existeReportePorId(String id) {
        return service.existeReportePorId(id);
    }

    public List<Document> buscarReportesPorUsuario(String usuarioId) {
        return service.buscarReportesPorUsuario(usuarioId);
    }

    public List<Document> buscarReportesPorTipo(String tipo) {
        return service.buscarReportesPorTipo(tipo);
    }

    public List<Document> buscarReportesPorEstado(String estado) {
        return service.buscarReportesPorEstado(estado);
    }


    // =========================
    // FACTURAS
    // =========================

    public void agregarFactura(Factura factura) {
        service.agregarFactura(factura);
    }

    public void modificarFactura(Factura factura) {
        service.modificarFactura(factura);
    }

    public void eliminarFactura(String id) {
        service.eliminarFactura(id);
    }

    public Document buscarFacturaPorId(String id) {
        return service.buscarFacturaPorId(id);
    }

    public List<Document> listarFacturas() {
        return service.listarFacturas();
    }

    public List<String> obtenerIdsFacturas() {
        return service.obtenerIdsFacturas();
    }

    public boolean existeFacturaPorId(String id) {
        return service.existeFacturaPorId(id);
    }

    public List<Document> buscarFacturasPorCliente(String clienteId) {
        return service.buscarFacturasPorCliente(clienteId);
    }

    public List<Document> buscarFacturasPorEstado(String estado) {
        return service.buscarFacturasPorEstado(estado);
    }


    // =========================
    // PAGOS
    // =========================

    public void agregarPago(Pago pago) {
        service.agregarPago(pago);
    }

    public void modificarPago(Pago pago) {
        service.modificarPago(pago);
    }

    public void eliminarPago(String id) {
        service.eliminarPago(id);
    }

    public Document buscarPagoPorId(String id) {
        return service.buscarPagoPorId(id);
    }

    public List<Document> listarPagos() {
        return service.listarPagos();
    }

    public List<String> obtenerIdsPagos() {
        return service.obtenerIdsPagos();
    }

    public boolean existePagoPorId(String id) {
        return service.existePagoPorId(id);
    }

    public List<Document> buscarPagosPorFactura(String facturaId) {
        return service.buscarPagosPorFactura(facturaId);
    }

    public List<Document> buscarPagosPorMedioPago(String medioPago) {
        return service.buscarPagosPorMedioPago(medioPago);
    }


    // =========================
    // CUENTAS CORRIENTES
    // =========================

    public void agregarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        service.agregarCuentaCorriente(cuentaCorriente);
    }

    public void modificarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        service.modificarCuentaCorriente(cuentaCorriente);
    }

    public void eliminarCuentaCorriente(String id) {
        service.eliminarCuentaCorriente(id);
    }

    public Document buscarCuentaCorrientePorId(String id) {
        return service.buscarCuentaCorrientePorId(id);
    }

    public List<Document> listarCuentasCorrientes() {
        return service.listarCuentasCorrientes();
    }

    public List<String> obtenerIdsCuentasCorrientes() {
        return service.obtenerIdsCuentasCorrientes();
    }

    public boolean existeCuentaCorrientePorId(String id) {
        return service.existeCuentaCorrientePorId(id);
    }

    public Document buscarCuentaCorrientePorCliente(String clienteId) {
        return service.buscarCuentaCorrientePorCliente(clienteId);
    }


    // =========================
    // DATOS DE PRUEBA
    // =========================

    public void generarDatosPrueba() {
        GeneradorDatosPruebaMongo.generar();
    }
}