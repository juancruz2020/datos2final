package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.EnvioService;

import java.util.Date;
import java.util.List;

public class EnvioController {

    private final EnvioService envioService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EnvioController() {

        this.envioService =
                new EnvioService();
    }


    // =========================================================
    // CREAR ENVÍO
    // =========================================================

    public void crearEnvio(
            String clienteId,
            List<String> contenedoresIds,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String prioridad
    ) {

        envioService.crearEnvio(
                clienteId,
                contenedoresIds,
                ciudadOrigen,
                paisOrigen,
                ciudadDestino,
                paisDestino,
                prioridad
        );
    }


    // =========================================================
    // MODIFICAR ENVÍO
    // =========================================================

    public void modificarEnvio(
            String id,
            String clienteId,
            List<String> contenedoresIds,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String estado,
            String prioridad
    ) {

        envioService.modificarEnvio(
                id,
                clienteId,
                contenedoresIds,
                ciudadOrigen,
                paisOrigen,
                ciudadDestino,
                paisDestino,
                estado,
                prioridad
        );
    }


    // =========================================================
    // ELIMINAR ENVÍO
    // =========================================================

    public void eliminarEnvio(
            String id
    ) {

        envioService.eliminarEnvio(
                id
        );
    }


    // =========================================================
    // LISTAR ENVÍOS
    // =========================================================

    public List<Document> listarEnvios() {

        return envioService.listarEnvios();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return envioService.buscarPorId(
                id
        );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        return envioService.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return envioService.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR CLIENTE
    // =========================================================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {

        return envioService.buscarPorCliente(
                clienteId
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        return envioService.buscarPorEstado(
                estado
        );
    }


    // =========================================================
    // BUSCAR POR PAÍS
    // =========================================================

    public List<Document> buscarPorPais(
            String pais
    ) {

        return envioService.buscarPorPais(
                pais
        );
    }


    // =========================================================
    // BUSCAR DEMORADOS
    // =========================================================

    public List<Document> buscarDemorados() {

        return envioService.buscarDemorados();
    }


    // =========================================================
    // AGREGAR TRAMO
    // =========================================================

    public void agregarTramo(
            String envioId,
            String medioTransporte,
            String origen,
            String destino,
            Date fechaSalida,
            Date fechaLlegadaEstimada
    ) {

        envioService.agregarTramo(
                envioId,
                medioTransporte,
                origen,
                destino,
                fechaSalida,
                fechaLlegadaEstimada
        );
    }
}