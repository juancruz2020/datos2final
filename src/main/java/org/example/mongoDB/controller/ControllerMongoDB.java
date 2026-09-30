package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.GeneradorDatosPrueba;
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


    // =========================
    // DATOS DE PRUEBA
    // =========================

    public void generarDatosPrueba() {
        GeneradorDatosPrueba.generar();
    }
}