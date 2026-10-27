
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.service.PermisosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class VehiculosPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final VehiculosPanel view;
    private final PermisosService permisosService;
    private final String usuarioId;

    private List<Document> vehiculosActuales;

    private String vehiculoIdEditando;
    private boolean modoEdicion;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VehiculosPanelController(
            VehiculosPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        this.permisosService = new PermisosService();

        this.vehiculosActuales = new ArrayList<>();
        this.vehiculoIdEditando = null;
        this.modoEdicion = false;

        if (!permisosService.puedeAccederVista(
                usuarioId, "VEHICULOS")) {

            throw new SecurityException(
                    "No tiene permiso para acceder a Vehículos."
            );
        }

        configurarPermisos();
        configurarEventos();
        cargarVehiculos();
    }

    // =========================================================
    // CONFIGURAR PERMISOS
    // =========================================================

    private void configurarPermisos() {

        boolean admin = permisosService.esAdmin(usuarioId);

        // Solo Administrador puede agregar y eliminar.
        view.getBtnMostrarFormulario().setVisible(admin);
        view.getBtnEliminar().setVisible(admin);

        // Administrador y Operador pueden editar.
        view.getBtnEditar().setVisible(true);
        view.getBtnActualizar().setVisible(true);
    }

    private void exigirAdministrador() {
        permisosService.exigirAdmin(usuarioId);
    }

    private void exigirEdicionPermitida() {
        permisosService.exigirAdminUOperador(usuarioId);
    }

    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> cargarVehiculos()
        );

        view.getBtnMostrarFormulario().addActionListener(
                e -> prepararNuevoVehiculo()
        );

        view.getBtnEditar().addActionListener(
                e -> prepararEdicion()
        );

        view.getBtnEliminar().addActionListener(
                e -> eliminarVehiculo()
        );

        view.getBtnCancelar().addActionListener(
                e -> cancelar()
        );

        view.getBtnGuardar().addActionListener(
                e -> guardar()
        );
    }

    // =========================================================
    // CARGAR VEHÍCULOS
    // =========================================================

    private void cargarVehiculos() {

        try {

            List<Document> vehiculos =
                    mongo.listarVehiculos();

            vehiculosActuales =
                    new ArrayList<>(vehiculos);

            DefaultTableModel modelo =
                    (DefaultTableModel) view
                            .getTablaVehiculos()
                            .getModel();

            modelo.setRowCount(0);

            for (Document vehiculo : vehiculos) {

                modelo.addRow(new Object[]{
                        vehiculo.getString("tipo"),
                        vehiculo.getString("identificacion"),
                        vehiculo.getString("empresa_operadora"),
                        vehiculo.getString("estado")
                });
            }

            view.getLblEstado().setText(
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
    // OBTENER VEHÍCULO SELECCIONADO
    // =========================================================

    private Document obtenerVehiculoSeleccionado() {

        JTable tabla = view.getTablaVehiculos();

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return null;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(fila);

        if (filaModelo < 0
                || filaModelo >= vehiculosActuales.size()) {

            return null;
        }

        return vehiculosActuales.get(filaModelo);
    }

    // =========================================================
    // PREPARAR NUEVO VEHÍCULO
    // =========================================================

    private void prepararNuevoVehiculo() {

        exigirAdministrador();

        modoEdicion = false;
        vehiculoIdEditando = null;

        view.limpiarFormulario();
        view.modoAgregar();
        view.mostrarFormulario();
    }

    // =========================================================
    // PREPARAR EDICIÓN
    // =========================================================

    private void prepararEdicion() {

        exigirEdicionPermitida();

        Document vehiculo =
                obtenerVehiculoSeleccionado();

        if (vehiculo == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un vehículo de la tabla para editar.",
                    "Editar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        ObjectId id =
                vehiculo.getObjectId("_id");

        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El vehículo seleccionado no tiene un ID válido.",
                    "Editar vehículo",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        vehiculoIdEditando = id.toHexString();
        modoEdicion = true;

        // CARGAR DATOS DEL VEHÍCULO

        String tipo =
                vehiculo.getString("tipo");

        String identificacion =
                vehiculo.getString("identificacion");

        String empresaOperadora =
                vehiculo.getString("empresa_operadora");

        String estado =
                vehiculo.getString("estado");

        view.getTxtTipo().setText(
                tipo != null ? tipo : ""
        );

        view.getTxtIdentificacion().setText(
                identificacion != null
                        ? identificacion
                        : ""
        );

        view.getTxtEmpresaOperadora().setText(
                empresaOperadora != null
                        ? empresaOperadora
                        : ""
        );

        if (estado != null) {

            view.getComboEstado().setSelectedItem(
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

            exigirEdicionPermitida();
            modificarVehiculo();

        } else {

            exigirAdministrador();
            agregarVehiculo();
        }
    }

    // =========================================================
    // AGREGAR VEHÍCULO
    // =========================================================

    private void agregarVehiculo() {

        exigirAdministrador();

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

            mongo.crearVehiculo(
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
    // MODIFICAR VEHÍCULO
    // =========================================================

    private void modificarVehiculo() {

        exigirEdicionPermitida();

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
                (String) view.getComboEstado()
                        .getSelectedItem();

        try {

            mongo.modificarVehiculo(
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
    // ELIMINAR VEHÍCULO
    // =========================================================

    private void eliminarVehiculo() {

        exigirAdministrador();

        Document vehiculo =
                obtenerVehiculoSeleccionado();

        if (vehiculo == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un vehículo de la tabla para eliminar.",
                    "Eliminar vehículo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        ObjectId id =
                vehiculo.getObjectId("_id");

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
                vehiculo.getString("identificacion");

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

            // Validar nuevamente antes de eliminar.
            exigirAdministrador();

            mongo.eliminarVehiculo(
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
    // FINALIZAR OPERACIÓN
    // =========================================================

    private void finalizarOperacion() {

        modoEdicion = false;
        vehiculoIdEditando = null;

        view.limpiarFormulario();
        view.ocultarFormulario();

        cargarVehiculos();
    }

    // =========================================================
    // CANCELAR
    // =========================================================

    private void cancelar() {

        modoEdicion = false;
        vehiculoIdEditando = null;

        view.limpiarFormulario();
        view.ocultarFormulario();
    }

    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n"
                        + (e.getMessage() != null
                        ? e.getMessage()
                        : "Ocurrió un error."),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
