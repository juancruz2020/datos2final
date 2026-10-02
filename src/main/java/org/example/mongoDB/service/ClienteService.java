package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.model.Cliente;
import org.example.mongoDB.model.Direccion;

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

        // -----------------------------------------------------
        // VALIDACIONES
        // -----------------------------------------------------

        if (razonSocial == null
                || razonSocial.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La razón social es obligatoria."
            );
        }

        if (cuit == null
                || cuit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El CUIT es obligatorio."
            );
        }

        if (email == null
                || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El email es obligatorio."
            );
        }

        if (telefono == null
                || telefono.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El teléfono es obligatorio."
            );
        }

        if (calle == null
                || calle.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La calle es obligatoria."
            );
        }

        if (numero == null
                || numero.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El número es obligatorio."
            );
        }

        if (ciudad == null
                || ciudad.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La ciudad es obligatoria."
            );
        }

        if (codigoPostal == null
                || codigoPostal.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El código postal es obligatorio."
            );
        }

        if (pais == null
                || pais.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El país es obligatorio."
            );
        }


        // -----------------------------------------------------
        // CUIT DUPLICADO
        // -----------------------------------------------------

        Document existente =
                clienteDAO.buscarPorCuit(
                        cuit.trim()
                );

        if (existente != null) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente con ese CUIT."
            );
        }


        // -----------------------------------------------------
        // DIRECCIÓN
        // -----------------------------------------------------

        Direccion direccion =
                new Direccion(
                        calle.trim(),
                        numero.trim(),
                        ciudad.trim(),
                        codigoPostal.trim()
                );


        // -----------------------------------------------------
        // CLIENTE
        // -----------------------------------------------------

        Cliente cliente =
                new Cliente(
                        null,
                        razonSocial.trim(),
                        cuit.trim(),
                        email.trim(),
                        telefono.trim(),
                        direccion,
                        pais.trim(),
                        "ACTIVO"
                );


        // -----------------------------------------------------
        // GUARDAR
        // -----------------------------------------------------

        clienteDAO.agregar(
                cliente
        );
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