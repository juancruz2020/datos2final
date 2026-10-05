package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.EnvioController;
import org.example.mongoDB.controller.EventoLogisticoController;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventosLogisticosPanelController {

    private final EventosLogisticosPanel vista;

    private final EventoLogisticoController eventoController;
    private final EnvioController envioController;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EventosLogisticosPanelController(
            EventosLogisticosPanel vista
    ) {

        this.vista = vista;

        this.eventoController =
                new EventoLogisticoController();

        this.envioController =
                new EnvioController();

        configurarEventos();

        cargarEnvios();

        cargarEventos();
    }


    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        vista.getBtnRegistrar()
                .addActionListener(
                        e -> registrarEvento()
                );

        vista.getBtnActualizar()
                .addActionListener(
                        e -> actualizar()
                );
    }


    // =========================================================
    // REGISTRAR EVENTO
    // =========================================================

    private void registrarEvento() {

        try {

            String envioId =
                    (String) vista
                            .getComboEnvio()
                            .getSelectedItem();

            String tipoEvento =
                    vista
                            .getTxtTipoEvento()
                            .getText();

            String ubicacion =
                    vista
                            .getTxtUbicacion()
                            .getText();

            String descripcion =
                    vista
                            .getTxtDescripcion()
                            .getText();


            eventoController.crearEvento(
                    envioId,
                    tipoEvento,
                    ubicacion,
                    descripcion
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Evento logístico registrado correctamente.",
                    "Evento registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            vista.limpiarFormulario();

            cargarEventos();

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

            cargarEventos();

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
    // CARGAR EVENTOS
    // =========================================================

    private void cargarEventos() {

        vista.getModeloTabla()
                .setRowCount(0);


        List<Document> eventos =
                eventoController
                        .listarEventos();


        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm"
                );


        for (Document evento : eventos) {

            ObjectId id =
                    evento.getObjectId(
                            "_id"
                    );

            ObjectId envioId =
                    evento.getObjectId(
                            "envio_id"
                    );

            Date fechaHora =
                    evento.getDate(
                            "fecha_hora"
                    );


            String fechaFormateada = "";

            if (fechaHora != null) {

                fechaFormateada =
                        formatoFecha.format(
                                fechaHora
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

                                    evento.getString(
                                            "tipo_evento"
                                    ),

                                    evento.getString(
                                            "ubicacion"
                                    ),

                                    evento.getString(
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