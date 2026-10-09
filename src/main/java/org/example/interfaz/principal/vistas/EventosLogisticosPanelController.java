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

    private String eventoIdEdicion;


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

        this.eventoIdEdicion = null;

        configurarEventos();

        cargarEnvios();

        cargarEventos();
    }


    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        // -----------------------------------------------------
        // REGISTRAR / GUARDAR CAMBIOS
        // -----------------------------------------------------

        vista.getBtnRegistrar()
                .addActionListener(
                        e -> guardar()
                );


        // -----------------------------------------------------
        // EDITAR
        // -----------------------------------------------------

        vista.getBtnEditar()
                .addActionListener(
                        e -> editarEventoSeleccionado()
                );


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        vista.getBtnEliminar()
                .addActionListener(
                        e -> eliminarEventoSeleccionado()
                );


        // -----------------------------------------------------
        // ACTUALIZAR
        // -----------------------------------------------------

        vista.getBtnActualizar()
                .addActionListener(
                        e -> actualizar()
                );
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        if (eventoIdEdicion == null) {

            registrarEvento();

        } else {

            modificarEvento();
        }
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


            cancelarEdicion();

            cargarEventos();

        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // EDITAR EVENTO SELECCIONADO
    // =========================================================

    private void editarEventoSeleccionado() {

        int fila =
                vista.getTablaEventos()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un evento para editar.",
                    "Editar evento",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            String id =
                    vista.getModeloTabla()
                            .getValueAt(
                                    fila,
                                    0
                            )
                            .toString();


            Document evento =
                    eventoController.buscarPorId(
                            id
                    );


            Object envioIdValor = evento.get("envio_id");
            String envioId = envioIdValor == null
                    ? null
                    : envioIdValor.toString();


            if (envioId != null) {

                vista.getComboEnvio()
                        .setSelectedItem(
                                envioId
                        );
            }


            vista.getTxtTipoEvento()
                    .setText(
                            evento.getString(
                                    "tipo_evento"
                            )
                    );


            vista.getTxtUbicacion()
                    .setText(
                            evento.getString(
                                    "ubicacion"
                            )
                    );


            vista.getTxtDescripcion()
                    .setText(
                            evento.getString(
                                    "descripcion"
                            )
                    );


            eventoIdEdicion =
                    id;


            vista.modoEditar();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // MODIFICAR EVENTO
    // =========================================================

    private void modificarEvento() {

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


            eventoController.modificarEvento(
                    eventoIdEdicion,
                    envioId,
                    tipoEvento,
                    ubicacion,
                    descripcion
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Evento logístico modificado correctamente.",
                    "Evento modificado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cancelarEdicion();

            cargarEventos();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // ELIMINAR EVENTO SELECCIONADO
    // =========================================================

    private void eliminarEventoSeleccionado() {

        int fila =
                vista.getTablaEventos()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un evento para eliminar.",
                    "Eliminar evento",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String id =
                vista.getModeloTabla()
                        .getValueAt(
                                fila,
                                0
                        )
                        .toString();


        int respuesta =
                JOptionPane.showConfirmDialog(
                        vista,
                        "¿Seguro que querés eliminar el evento seleccionado?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }


        try {

            eventoController.eliminarEvento(
                    id
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Evento logístico eliminado correctamente.",
                    "Evento eliminado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            if (id.equals(eventoIdEdicion)) {

                cancelarEdicion();
            }


            cargarEventos();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // CANCELAR EDICIÓN
    // =========================================================

    private void cancelarEdicion() {

        eventoIdEdicion = null;

        vista.limpiarFormulario();

        vista.modoRegistrar();

        vista.getTablaEventos()
                .clearSelection();
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizar() {

        try {

            cancelarEdicion();

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
                    .addItem(
                            id
                    );
        }
    }


    // =========================================================
    // CARGAR EVENTOS
    // =========================================================

    private void cargarEventos() {

        vista.getModeloTabla()
                .setRowCount(
                        0
                );


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

            Object envioIdValor = evento.get("envio_id");
            String envioId = envioIdValor == null ? null : envioIdValor.toString();

            Date fechaHora =
                    evento.getDate(
                            "fecha_hora"
                    );


            String fechaFormateada =
                    "";

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
                                            ? envioId
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
