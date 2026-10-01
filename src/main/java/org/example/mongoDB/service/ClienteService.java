package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;

import java.util.List;

public class ClienteService {

    private final ClienteMongoDAO clienteDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ClienteService() {

        this.clienteDAO =
                new ClienteMongoDAO();
    }


    // =========================================================
    // LISTAR CLIENTES
    // =========================================================

    public List<Document> listarClientes() {

        return clienteDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR CLIENTE POR ID
    // =========================================================

    public Document buscarPorId(
            String clienteId
    ) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        Document cliente =
                clienteDAO.buscarPorId(
                        clienteId
                );

        if (cliente == null) {

            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }

        return cliente;
    }


    // =========================================================
    // BUSCAR CLIENTE POR CUIT
    // =========================================================

    public Document buscarPorCuit(
            String cuit
    ) {

        if (cuit == null
                || cuit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El CUIT es obligatorio."
            );
        }

        return clienteDAO.buscarPorCuit(
                cuit.trim()
        );
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}