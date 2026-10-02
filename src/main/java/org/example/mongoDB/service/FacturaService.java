package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.dao.FacturaMongoDAO;
import org.example.mongoDB.model.Factura;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FacturaService {

    private final FacturaMongoDAO facturaDAO;
    private final ClienteMongoDAO clienteDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public FacturaService() {

        this.facturaDAO =
                new FacturaMongoDAO();

        this.clienteDAO =
                new ClienteMongoDAO();
    }


    // =========================================================
    // CREAR FACTURA
    // =========================================================

    public void crearFactura(
            String clienteId,
            Date fechaEmision,
            BigDecimal importeTotal,
            String estado
    ) {

        // -----------------------------------------------------
        // VALIDAR CLIENTE
        // -----------------------------------------------------

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        if (!clienteDAO.existePorId(clienteId)) {

            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR FECHA
        // -----------------------------------------------------

        if (fechaEmision == null) {

            throw new IllegalArgumentException(
                    "La fecha de emisión es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR IMPORTE
        // -----------------------------------------------------

        if (importeTotal == null
                || importeTotal.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe total debe ser mayor a 0."
            );
        }


        // -----------------------------------------------------
        // VALIDAR ESTADO
        // -----------------------------------------------------

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        // -----------------------------------------------------
        // CREAR
        // -----------------------------------------------------

        Factura factura =
                new Factura(
                        null,
                        clienteId,
                        fechaEmision,
                        importeTotal,
                        estado.trim()
                );


        facturaDAO.agregar(
                factura
        );
    }


    // =========================================================
    // LISTAR FACTURAS
    // =========================================================

    public List<Document> listarFacturas() {

        return facturaDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID de la factura no es válido."
        );


        Document factura =
                facturaDAO.buscarPorId(
                        id
                );


        if (factura == null) {

            throw new IllegalArgumentException(
                    "La factura no existe."
            );
        }


        return factura;
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID de la factura no es válido."
        );


        return facturaDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return facturaDAO.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR CLIENTE
    // =========================================================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        return facturaDAO.buscarPorCliente(
                clienteId
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        return facturaDAO.buscarPorEstado(
                estado.trim()
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