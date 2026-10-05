package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.ReporteService;

import java.util.List;

public class ReporteController {

    private final ReporteService reporteService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReporteController() {

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


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return reporteService
                .buscarPorId(
                        id
                );
    }


    // =========================================================
    // BUSCAR POR USUARIO
    // =========================================================

    public List<Document> buscarPorUsuario(
            String usuarioId
    ) {

        return reporteService
                .buscarPorUsuario(
                        usuarioId
                );
    }


    // =========================================================
    // BUSCAR POR TIPO
    // =========================================================

    public List<Document> buscarPorTipo(
            String tipo
    ) {

        return reporteService
                .buscarPorTipo(
                        tipo
                );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        return reporteService
                .buscarPorEstado(
                        estado
                );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        return reporteService
                .existePorId(
                        id
                );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return reporteService
                .obtenerTodosLosIds();
    }
}