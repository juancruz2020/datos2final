package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.FacturaService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FacturaController {

    private final FacturaService facturaService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public FacturaController() {

        this.facturaService =
                new FacturaService();
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

        facturaService.crearFactura(
                clienteId,
                fechaEmision,
                importeTotal,
                estado
        );
    }


    // =========================================================
    // LISTAR FACTURAS
    // =========================================================

    public List<Document> listarFacturas() {

        return facturaService.listarFacturas();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return facturaService.buscarPorId(
                id
        );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        return facturaService.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return facturaService.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR CLIENTE
    // =========================================================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {

        return facturaService.buscarPorCliente(
                clienteId
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        return facturaService.buscarPorEstado(
                estado
        );
    }
}