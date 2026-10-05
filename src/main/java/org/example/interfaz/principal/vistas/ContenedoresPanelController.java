package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ContenedorController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ContenedoresPanelController {

    private final ContenedoresPanel view;

    private final ContenedorController controller;

    private List<Document> contenedoresActuales;

    private String contenedorIdEditando;

    private boolean modoEdicion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ContenedoresPanelController(
            ContenedoresPanel view
    ) {

        this.view = view;

        this.controller =
                new ContenedorController();

        this.contenedoresActuales =
                new ArrayList<>();

        this.contenedorIdEditando =
                null;

        this.modoEdicion =
                false;


        configurarEventos();

        cargarContenedores();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        // -----------------------------------------------------
        // ACTUALIZAR
        // -----------------------------------------------------

        view.getBtnActualizar()
                .addActionListener(
                        e -> cargarContenedores()
                );


        // -----------------------------------------------------
        // MOSTRAR FORMULARIO PARA AGREGAR
        // -----------------------------------------------------

        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> prepararNuevoContenedor()
                );


        // -----------------------------------------------------
        // EDITAR
        // -----------------------------------------------------

        view.getBtnEditar()
                .addActionListener(
                        e -> prepararEdicion()
                );


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarContenedor()
                );


        // -----------------------------------------------------
        // CANCELAR
        // -----------------------------------------------------

        view.getBtnCancelar()
                .addActionListener(
                        e -> cancelar()
                );


        // -----------------------------------------------------
        // GUARDAR
        // -----------------------------------------------------

        view.getBtnGuardar()
                .addActionListener(
                        e -> guardar()
                );
    }


    // =========================================================
    // CARGAR CONTENEDORES
    // =========================================================

    private void cargarContenedores() {

        try {

            List<Document> contenedores =
                    controller.listarContenedores();


            contenedoresActuales =
                    new ArrayList<>(
                            contenedores
                    );


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaContenedores()
                                    .getModel();


            modelo.setRowCount(
                    0
            );


            for (Document contenedor : contenedores) {

                modelo.addRow(
                        new Object[]{
                                contenedor.getString(
                                        "codigo_internacional"
                                ),

                                contenedor.getString(
                                        "tipo"
                                ),

                                contenedor.get(
                                        "capacidad"
                                ),

                                contenedor.getString(
                                        "estado"
                                )
                        }
                );
            }


            view.getLblEstado()
                    .setText(
                            "Contenedores registrados: "
                                    + contenedores.size()
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los contenedores.",
                    e
            );
        }
    }


    // =========================================================
    // PREPARAR NUEVO CONTENEDOR
    // =========================================================

    private void prepararNuevoContenedor() {

        modoEdicion =
                false;


        contenedorIdEditando =
                null;


        view.limpiarFormulario();

        view.modoAgregar();

        view.mostrarFormulario();
    }


    // =========================================================
    // PREPARAR EDICIÓN
    // =========================================================

    private void prepararEdicion() {

        int fila =
                view.getTablaContenedores()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor de la tabla para editar.",
                    "Editar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (fila >= contenedoresActuales.size()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el contenedor seleccionado.",
                    "Editar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        Document contenedor =
                contenedoresActuales.get(
                        fila
                );


        ObjectId id =
                contenedor.getObjectId(
                        "_id"
                );


        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El contenedor seleccionado no tiene un ID válido.",
                    "Editar contenedor",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        contenedorIdEditando =
                id.toHexString();


        modoEdicion =
                true;


        // -----------------------------------------------------
        // CARGAR DATOS EN FORMULARIO
        // -----------------------------------------------------

        String codigo =
                contenedor.getString(
                        "codigo_internacional"
                );


        String tipo =
                contenedor.getString(
                        "tipo"
                );


        Object capacidad =
                contenedor.get(
                        "capacidad"
                );


        String estado =
                contenedor.getString(
                        "estado"
                );


        view.getTxtCodigoInternacional()
                .setText(
                        codigo != null
                                ? codigo
                                : ""
                );


        view.getTxtTipo()
                .setText(
                        tipo != null
                                ? tipo
                                : ""
                );


        view.getTxtCapacidad()
                .setText(
                        capacidad != null
                                ? capacidad.toString()
                                : ""
                );


        if (estado != null) {

            view.getComboEstado()
                    .setSelectedItem(
                            estado
                    );
        }


        view.modoEditar();

        view.mostrarFormulario();
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        if (modoEdicion) {

            modificarContenedor();

        } else {

            agregarContenedor();
        }
    }


    // =========================================================
    // AGREGAR CONTENEDOR
    // =========================================================

    private void agregarContenedor() {

        String codigo =
                view.getTxtCodigoInternacional()
                        .getText()
                        .trim();


        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();


        Double capacidad =
                obtenerCapacidad();


        if (capacidad == null) {

            return;
        }


        try {

            controller.crearContenedor(
                    codigo,
                    tipo,
                    capacidad
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Contenedor agregado correctamente.",
                    "Contenedores",
                    JOptionPane.INFORMATION_MESSAGE
            );


            finalizarOperacion();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el contenedor.",
                    e
            );
        }
    }


    // =========================================================
    // MODIFICAR CONTENEDOR
    // =========================================================

    private void modificarContenedor() {

        if (contenedorIdEditando == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No hay ningún contenedor seleccionado para modificar.",
                    "Editar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String codigo =
                view.getTxtCodigoInternacional()
                        .getText()
                        .trim();


        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();


        Double capacidad =
                obtenerCapacidad();


        if (capacidad == null) {

            return;
        }


        String estado =
                (String)
                        view.getComboEstado()
                                .getSelectedItem();


        try {

            controller.modificarContenedor(
                    contenedorIdEditando,
                    codigo,
                    tipo,
                    capacidad,
                    estado
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Contenedor modificado correctamente.",
                    "Contenedores",
                    JOptionPane.INFORMATION_MESSAGE
            );


            finalizarOperacion();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el contenedor.",
                    e
            );
        }
    }


    // =========================================================
    // ELIMINAR CONTENEDOR
    // =========================================================

    private void eliminarContenedor() {

        int fila =
                view.getTablaContenedores()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor de la tabla para eliminar.",
                    "Eliminar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (fila >= contenedoresActuales.size()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el contenedor seleccionado.",
                    "Eliminar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        Document contenedor =
                contenedoresActuales.get(
                        fila
                );


        ObjectId id =
                contenedor.getObjectId(
                        "_id"
                );


        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El contenedor seleccionado no tiene un ID válido.",
                    "Eliminar contenedor",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        String codigo =
                contenedor.getString(
                        "codigo_internacional"
                );


        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Seguro que querés eliminar el contenedor"
                                + "\n"
                                + codigo
                                + "?",
                        "Eliminar contenedor",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }


        try {

            controller.eliminarContenedor(
                    id.toHexString()
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Contenedor eliminado correctamente.",
                    "Contenedores",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cancelar();

            cargarContenedores();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el contenedor.",
                    e
            );
        }
    }


    // =========================================================
    // OBTENER CAPACIDAD
    // =========================================================

    private Double obtenerCapacidad() {

        String capacidadTexto =
                view.getTxtCapacidad()
                        .getText()
                        .trim();


        try {

            return Double.parseDouble(
                    capacidadTexto
                            .replace(
                                    ",",
                                    "."
                            )
            );


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "La capacidad debe ser un número válido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );


            return null;
        }
    }


    // =========================================================
    // FINALIZAR OPERACIÓN
    // =========================================================

    private void finalizarOperacion() {

        modoEdicion =
                false;


        contenedorIdEditando =
                null;


        view.limpiarFormulario();

        view.ocultarFormulario();

        cargarContenedores();
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    private void cancelar() {

        modoEdicion =
                false;


        contenedorIdEditando =
                null;


        view.limpiarFormulario();

        view.ocultarFormulario();
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje
                        + "\n\n"
                        + (
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Ocurrió un error."
                ),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}