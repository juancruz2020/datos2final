package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ReporteController;
import org.example.mongoDB.dao.UsuarioMongoDAO;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportesPanelController {

    private final ReportesPanel vista;

    private final ReporteController reporteController;
    private final UsuarioMongoDAO usuarioDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportesPanelController(
            ReportesPanel vista
    ) {

        this.vista =
                vista;

        this.reporteController =
                new ReporteController();

        this.usuarioDAO =
                new UsuarioMongoDAO();


        configurarEventos();

        cargarUsuarios();

        cargarReportes();
    }


    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        vista.getBtnGenerar()
                .addActionListener(
                        e -> generarReporte()
                );


        vista.getBtnActualizar()
                .addActionListener(
                        e -> actualizar()
                );
    }


    // =========================================================
    // GENERAR REPORTE
    // =========================================================

    private void generarReporte() {

        try {

            String usuarioId =
                    (String) vista
                            .getComboUsuario()
                            .getSelectedItem();


            String tipo =
                    vista
                            .getTxtTipo()
                            .getText();


            String formato =
                    (String) vista
                            .getComboFormato()
                            .getSelectedItem();


            reporteController.crearReporte(
                    usuarioId,
                    tipo,
                    formato
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Reporte generado correctamente.",
                    "Reporte generado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            vista.limpiarFormulario();

            cargarReportes();

        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizar() {

        try {

            cargarUsuarios();

            cargarReportes();

        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // CARGAR USUARIOS
    // =========================================================

    private void cargarUsuarios() {

        vista.getComboUsuario()
                .removeAllItems();


        List<String> ids =
                usuarioDAO
                        .obtenerTodosLosIds();


        for (String id : ids) {

            vista.getComboUsuario()
                    .addItem(id);
        }
    }


    // =========================================================
    // CARGAR REPORTES
    // =========================================================

    private void cargarReportes() {

        vista.getModeloTabla()
                .setRowCount(0);


        List<Document> reportes =
                reporteController
                        .listarReportes();


        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm"
                );


        for (Document reporte : reportes) {

            ObjectId id =
                    reporte.getObjectId(
                            "_id"
                    );


            ObjectId usuarioId =
                    reporte.getObjectId(
                            "usuario_id"
                    );


            Date fecha =
                    reporte.getDate(
                            "fecha_generacion"
                    );


            String fechaFormateada = "";

            if (fecha != null) {

                fechaFormateada =
                        formatoFecha.format(
                                fecha
                        );
            }


            vista.getModeloTabla()
                    .addRow(
                            new Object[]{
                                    id != null
                                            ? id.toHexString()
                                            : "",

                                    usuarioId != null
                                            ? usuarioId.toHexString()
                                            : "",

                                    reporte.getString(
                                            "tipo"
                                    ),

                                    fechaFormateada,

                                    reporte.getString(
                                            "formato"
                                    ),

                                    reporte.getString(
                                            "estado"
                                    )
                            }
                    );
        }
    }


    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                vista,
                mensaje != null
                        ? mensaje
                        : "Ocurrió un error.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}