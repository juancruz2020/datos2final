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
    // CREAR CLIENTE
    // =========================================================

    public void crearCliente(
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            String calle,
            String numero,
            String ciudad,
            String codigoPostal,
            String pais
    ) {

        clienteService.crearCliente(
                razonSocial,
                cuit,
                email,
                telefono,
                calle,
                numero,
                ciudad,
                codigoPostal,
                pais
        );
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