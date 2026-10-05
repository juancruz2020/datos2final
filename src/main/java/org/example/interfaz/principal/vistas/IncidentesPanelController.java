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

    private String incidenteIdEdicion;


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

        this.incidenteIdEdicion =
                null;


        configurarEventos();

        cargarEnvios();

        cargarIncidentes();

        vista.modoRegistrar();
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
                        e -> editarIncidenteSeleccionado()
                );


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        vista.getBtnEliminar()
                .addActionListener(
                        e -> eliminarIncidenteSeleccionado()
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

        if (incidenteIdEdicion == null) {

            registrarIncidente();

        } else {

            modificarIncidente();
        }
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


            cancelarEdicion();

            cargarIncidentes();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // EDITAR INCIDENTE SELECCIONADO
    // =========================================================

    private void editarIncidenteSeleccionado() {

        int fila =
                vista.getTablaIncidentes()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un incidente para editar.",
                    "Editar incidente",
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


            Document incidente =
                    incidenteController.buscarPorId(
                            id
                    );


            ObjectId envioId =
                    incidente.getObjectId(
                            "envio_id"
                    );


            if (envioId != null) {

                vista.getComboEnvio()
                        .setSelectedItem(
                                envioId.toHexString()
                        );
            }


            vista.getTxtTipo()
                    .setText(
                            incidente.getString(
                                    "tipo"
                            )
                    );


            vista.getTxtSeveridad()
                    .setText(
                            incidente.getString(
                                    "severidad"
                            )
                    );


            String estado =
                    incidente.getString(
                            "estado"
                    );


            if (estado != null) {

                vista.getComboEstado()
                        .setSelectedItem(
                                estado
                        );
            }


            vista.getTxtDescripcion()
                    .setText(
                            incidente.getString(
                                    "descripcion"
                            )
                    );


            incidenteIdEdicion =
                    id;


            vista.modoEditar();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // MODIFICAR INCIDENTE
    // =========================================================

    private void modificarIncidente() {

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


            String estado =
                    (String) vista
                            .getComboEstado()
                            .getSelectedItem();


            String descripcion =
                    vista
                            .getTxtDescripcion()
                            .getText();


            incidenteController.modificarIncidente(
                    incidenteIdEdicion,
                    envioId,
                    tipo,
                    severidad,
                    estado,
                    descripcion
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Incidente modificado correctamente.",
                    "Incidente modificado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cancelarEdicion();

            cargarIncidentes();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // ELIMINAR INCIDENTE SELECCIONADO
    // =========================================================

    private void eliminarIncidenteSeleccionado() {

        int fila =
                vista.getTablaIncidentes()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un incidente para eliminar.",
                    "Eliminar incidente",
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
                        "¿Seguro que querés eliminar el incidente seleccionado?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }


        try {

            incidenteController.eliminarIncidente(
                    id
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Incidente eliminado correctamente.",
                    "Incidente eliminado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            if (id.equals(incidenteIdEdicion)) {

                cancelarEdicion();
            }


            cargarIncidentes();


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

        incidenteIdEdicion =
                null;


        vista.limpiarFormulario();

        vista.modoRegistrar();

        vista.getTablaIncidentes()
                .clearSelection();
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizar() {

        try {

            cancelarEdicion();

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
                    .addItem(
                            id
                    );
        }
    }


    // =========================================================
    // CARGAR INCIDENTES
    // =========================================================

    private void cargarIncidentes() {

        vista.getModeloTabla()
                .setRowCount(
                        0
                );


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


            String fechaFormateada =
                    "";

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