package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ReporteMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.logging.AppLog;
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
        AppLog.info(ReporteService.class, "Metadatos de reporte registrados.");
    }


    // =========================================================
    // LISTAR REPORTES
    // =========================================================

    public List<Document> listarReportes() {

        return TablaConsola.mostrar("ReporteService.listarReportes", reporteDAO.listarTodos());
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
