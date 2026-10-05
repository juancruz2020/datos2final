package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ReporteMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.mongoDB.model.Reporte;

import java.util.Date;
import java.util.List;

public class ReporteService {

    private final ReporteMongoDAO reporteDAO;
    private final UsuarioMongoDAO usuarioDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReporteService() {

        this.reporteDAO =
                new ReporteMongoDAO();

        this.usuarioDAO =
                new UsuarioMongoDAO();
    }


    // =========================================================
    // CREAR REPORTE
    // =========================================================

    public void crearReporte(
            String usuarioId,
            String tipo,
            String formato
    ) {

        // -----------------------------------------------------
        // VALIDAR USUARIO
        // -----------------------------------------------------

        validarObjectId(
                usuarioId,
                "El ID del usuario no es válido."
        );


        if (!usuarioDAO.existePorId(usuarioId)) {

            throw new IllegalArgumentException(
                    "El usuario seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de reporte es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR FORMATO
        // -----------------------------------------------------

        if (formato == null
                || formato.isBlank()) {

            throw new IllegalArgumentException(
                    "El formato es obligatorio."
            );
        }


        // -----------------------------------------------------
        // CREAR REPORTE
        // -----------------------------------------------------

        Reporte reporte =
                new Reporte(
                        null,
                        usuarioId,
                        tipo.trim(),
                        new Date(),
                        formato.trim(),
                        "GENERADO"
                );


        reporteDAO.agregar(
                reporte
        );
    }


    // =========================================================
    // LISTAR REPORTES
    // =========================================================

    public List<Document> listarReportes() {

        return reporteDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del reporte no es válido."
        );


        Document reporte =
                reporteDAO.buscarPorId(
                        id
                );


        if (reporte == null) {

            throw new IllegalArgumentException(
                    "El reporte no existe."
            );
        }


        return reporte;
    }


    // =========================================================
    // BUSCAR POR USUARIO
    // =========================================================

    public List<Document> buscarPorUsuario(
            String usuarioId
    ) {

        validarObjectId(
                usuarioId,
                "El ID del usuario no es válido."
        );


        return reporteDAO.buscarPorUsuario(
                usuarioId
        );
    }


    // =========================================================
    // BUSCAR POR TIPO
    // =========================================================

    public List<Document> buscarPorTipo(
            String tipo
    ) {

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de reporte es obligatorio."
            );
        }


        return reporteDAO.buscarPorTipo(
                tipo.trim()
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


        return reporteDAO.buscarPorEstado(
                estado.trim()
        );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del reporte no es válido."
        );


        return reporteDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return reporteDAO.obtenerTodosLosIds();
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