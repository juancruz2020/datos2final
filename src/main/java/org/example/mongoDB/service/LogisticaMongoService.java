package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.example.mongoDB.dao.*;
import java.util.List;

public class LogisticaMongoService {

    private final ClienteMongoDAO clienteDAO;
    private final UsuarioMongoDAO usuarioDAO;
    private final ContenedorMongoDAO contenedorDAO;
    private final SensorMongoDAO sensorDAO;


    // =========================
    // CONSTRUCTOR
    // =========================

    public LogisticaMongoService() {

        this.clienteDAO = new ClienteMongoDAO();
        this.usuarioDAO = new UsuarioMongoDAO();
        this.contenedorDAO = new ContenedorMongoDAO();
        this.sensorDAO = new SensorMongoDAO();
    }


    // =========================
    // CLIENTES
    // =========================


    public Document buscarClientePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarClientePorId", clienteDAO.buscarPorId(id));
    }


    // =========================
    // USUARIOS
    // =========================


    public List<String> obtenerIdsUsuarios() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsUsuarios", usuarioDAO.obtenerTodosLosIds());
    }


    // =========================
    // CONTENEDORES
    // =========================


    public List<String> obtenerIdsContenedores() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsContenedores", contenedorDAO.obtenerTodosLosIds());
    }


    // =========================
    // SENSORES
    // =========================


    public List<String> obtenerIdsSensores() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsSensores", sensorDAO.obtenerTodosLosIds());
    }


}
