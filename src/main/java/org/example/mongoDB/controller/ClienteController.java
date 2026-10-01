package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.ClienteService;

import java.util.List;

public class ClienteController {

    private final ClienteService clienteService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ClienteController() {

        this.clienteService =
                new ClienteService();
    }


    // =========================================================
    // LISTAR CLIENTES
    // =========================================================

    public List<Document> listarClientes() {

        return clienteService.listarClientes();
    }


    // =========================================================
    // BUSCAR CLIENTE POR ID
    // =========================================================

    public Document buscarPorId(
            String clienteId
    ) {

        return clienteService.buscarPorId(
                clienteId
        );
    }


    // =========================================================
    // BUSCAR CLIENTE POR CUIT
    // =========================================================

    public Document buscarPorCuit(
            String cuit
    ) {

        return clienteService.buscarPorCuit(
                cuit
        );
    }
}