package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.EnvioController;
import org.example.mongoDB.controller.IncidenteController;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncidentesPanelController {

    private final IncidentesPanel vista;

    private final IncidenteController incidenteController;
    private final EnvioController envioController;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public IncidentesPanelController(
            IncidentesPanel vista
    ) {

        this.vista =
                vista;

        this.incidenteController =
                new IncidenteController();

        this.envioController =
                new EnvioController();


        configurarEventos();

        cargarEnvios();

        cargarIncidentes();
    }


    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        vista.getBtnRegistrar()
                .addActionListener(
                        e -> registrarIncidente()
                );


        vista.getBtnActualizar()
                .addActionListener(
                        e -> actualizar()
                );
    }


    // =========================================================
    // REGISTRAR INCIDENTE
    // =========================================================

    private void registrarIncidente() {

        try {

            String envioId =
                    (String) vista
                            .getComboEnvio()
                            .getSelectedItem();


            String tipo =
                    vista
                            .getTxtTipo()
                            .getText();


            String severidad =
                    vista
                            .getTxtSeveridad()
                            .getText();


            String descripcion =
                    vista
                            .getTxtDescripcion()
                            .getText();


            incidenteController.crearIncidente(
                    envioId,
                    tipo,
                    severidad,
                    descripcion
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Incidente registrado correctamente.",
                    "Incidente registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            vista.limpiarFormulario();

            cargarIncidentes();

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

            cargarEnvios();

            cargarIncidentes();

        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // CARGAR ENVÍOS
    // =========================================================

    private void cargarEnvios() {

        vista.getComboEnvio()
                .removeAllItems();


        List<String> ids =
                envioController
                        .obtenerTodosLosIds();


        for (String id : ids) {

            vista.getComboEnvio()
                    .addItem(id);
        }
    }


    // =========================================================
    // CARGAR INCIDENTES
    // =========================================================

    private void cargarIncidentes() {

        vista.getModeloTabla()
                .setRowCount(0);


        List<Document> incidentes =
                incidenteController
                        .listarIncidentes();


        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm"
                );


        for (Document incidente : incidentes) {

            ObjectId id =
                    incidente.getObjectId(
                            "_id"
                    );


            ObjectId envioId =
                    incidente.getObjectId(
                            "envio_id"
                    );


            Date fecha =
                    incidente.getDate(
                            "fecha"
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

                                    envioId != null
                                            ? envioId.toHexString()
                                            : "",

                                    fechaFormateada,

                                    incidente.getString(
                                            "tipo"
                                    ),

                                    incidente.getString(
                                            "severidad"
                                    ),

                                    incidente.getString(
                                            "estado"
                                    ),

                                    incidente.getString(
                                            "descripcion"
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