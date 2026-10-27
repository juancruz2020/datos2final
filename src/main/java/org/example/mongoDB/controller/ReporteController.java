package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.ReporteService;

import java.util.List;

class ReporteController {

    private final ReporteService reporteService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    ReporteController() {

        this.reporteService =
                new ReporteService();
    }


    // =========================================================
    // CREAR REPORTE
    // =========================================================

    public void crearReporte(
            String usuarioId,
            String tipo,
            String formato
    ) {

        reporteService.crearReporte(
                usuarioId,
                tipo,
                formato
        );
    }


    // =========================================================
    // LISTAR REPORTES
    // =========================================================

    public List<Document> listarReportes() {

        return reporteService
                .listarReportes();
    }


}