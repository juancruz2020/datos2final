package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.VehiculoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class VehiculosPanelController {

    private final VehiculosPanel view;

    private final VehiculoController controller;

    private List<Document> vehiculosActuales;

    private String vehiculoIdEditando;

    private boolean modoEdicion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VehiculosPanelController(
            VehiculosPanel view
    ) {

        this.view = view;

        this.controller =
                new VehiculoController();

        this.vehiculosActuales =
                new ArrayList<>();

        this.vehiculoIdEditando =
                null;

        this.modoEdicion =
                false;

        configurarEventos();

        cargarVehiculos();
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
                        e -> cargarVehiculos()
                );


        // -----------------------------------------------------
        // MOSTRAR FORMULARIO PARA AGREGAR
        // -----------------------------------------------------

        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> prepararNuevoVehiculo()
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
                        e -> eliminarVehiculo()
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
    // CARGAR VEHICULOS
    // =========================================================

    private void cargarVehiculos() {

        try {

            List<Document> vehiculos =
                    controller.listarVehiculos();

            vehiculosActuales =
                    new ArrayList<>(
                            vehiculos
                    );

            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaVehiculos()
                                    .getModel();

            modelo.setRowCount(
                    0
            );

            for (Document vehiculo : vehiculos) {

                modelo.addRow(
                        new Object[]{
                                vehiculo.getString(
                                        "tipo"
                                ),

                                vehiculo.getString(
                                        "identificacion"
                                ),

                                vehiculo.getString(
                                        "empresa_operadora"
                                ),

                                vehiculo.getString(
                                        "estado"
                                )
                        }
                );
            }

            view.getLblEstado()
                    .setText(
                            "Vehículos registrados: "
                                    + vehiculos.size()
                    );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los vehículos.",
                    e
            );
        }
    }


    // =========================================================
    // PREPARAR NUEVO VEHICULO
    // =========================================================

    private void prepararNuevoVehiculo() {

        modoEdicion =
                false;

        vehiculoIdEditando =
                null;

        view.limpiarFormulario();

        view.modoAgregar();

        view.mostrarFormulario();
    }


    // =========================================================
    // PREPARAR EDICION
    // =========================================================

    private void prepararEdicion() {

        int fila =
                view.getTablaVehiculos()
                        .getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un vehículo de la tabla para editar.",
                    "Editar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (fila >= vehiculosActuales.size()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el vehículo seleccionado.",
                    "Editar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Document vehiculo =
                vehiculosActuales.get(
                        fila
                );

        ObjectId id =
                vehiculo.getObjectId(
                        "_id"
                );

        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El vehículo seleccionado no tiene un ID válido.",
                    "Editar vehículo",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        vehiculoIdEditando =
                id.toHexString();

        modoEdicion =
                true;


        // -----------------------------------------------------
        // CARGAR DATOS EN FORMULARIO
        // -----------------------------------------------------

        String tipo =
                vehiculo.getString(
                        "tipo"
                );

        String identificacion =
                vehiculo.getString(
                        "identificacion"
                );

        String empresaOperadora =
                vehiculo.getString(
                        "empresa_operadora"
                );

        String estado =
                vehiculo.getString(
                        "estado"
                );

        view.getTxtTipo()
                .setText(
                        tipo != null
                                ? tipo
                                : ""
                );

        view.getTxtIdentificacion()
                .setText(
                        identificacion != null
                                ? identificacion
                                : ""
                );

        view.getTxtEmpresaOperadora()
                .setText(
                        empresaOperadora != null
                                ? empresaOperadora
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

            modificarVehiculo();

        } else {

            agregarVehiculo();
        }
    }


    // =========================================================
    // AGREGAR VEHICULO
    // =========================================================

    private void agregarVehiculo() {

        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();

        String identificacion =
                view.getTxtIdentificacion()
                        .getText()
                        .trim();

        String empresaOperadora =
                view.getTxtEmpresaOperadora()
                        .getText()
                        .trim();

        try {

            controller.crearVehiculo(
                    tipo,
                    identificacion,
                    empresaOperadora
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Vehículo agregado correctamente.",
                    "Vehículos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            finalizarOperacion();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el vehículo.",
                    e
            );
        }
    }


    // =========================================================
    // MODIFICAR VEHICULO
    // =========================================================

    private void modificarVehiculo() {

        if (vehiculoIdEditando == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No hay ningún vehículo seleccionado para modificar.",
                    "Editar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();

        String identificacion =
                view.getTxtIdentificacion()
                        .getText()
                        .trim();

        String empresaOperadora =
                view.getTxtEmpresaOperadora()
                        .getText()
                        .trim();

        String estado =
                (String)
                        view.getComboEstado()
                                .getSelectedItem();

        try {

            controller.modificarVehiculo(
                    vehiculoIdEditando,
                    tipo,
                    identificacion,
                    empresaOperadora,
                    estado
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Vehículo modificado correctamente.",
                    "Vehículos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            finalizarOperacion();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el vehículo.",
                    e
            );
        }
    }


    // =========================================================
    // ELIMINAR VEHICULO
    // =========================================================

    private void eliminarVehiculo() {

        int fila =
                view.getTablaVehiculos()
                        .getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un vehículo de la tabla para eliminar.",
                    "Eliminar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (fila >= vehiculosActuales.size()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el vehículo seleccionado.",
                    "Eliminar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Document vehiculo =
                vehiculosActuales.get(
                        fila
                );

        ObjectId id =
                vehiculo.getObjectId(
                        "_id"
                );

        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El vehículo seleccionado no tiene un ID válido.",
                    "Eliminar vehículo",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        String identificacion =
                vehiculo.getString(
                        "identificacion"
                );

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Seguro que querés eliminar el vehículo"
                                + "\n"
                                + identificacion
                                + "?",
                        "Eliminar vehículo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }

        try {

            controller.eliminarVehiculo(
                    id.toHexString()
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Vehículo eliminado correctamente.",
                    "Vehículos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cancelar();

            cargarVehiculos();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el vehículo.",
                    e
            );
        }
    }


    // =========================================================
    // FINALIZAR OPERACION
    // =========================================================

    private void finalizarOperacion() {

        modoEdicion =
                false;

        vehiculoIdEditando =
                null;

        view.limpiarFormulario();

        view.ocultarFormulario();

        cargarVehiculos();
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    private void cancelar() {

        modoEdicion =
                false;

        vehiculoIdEditando =
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