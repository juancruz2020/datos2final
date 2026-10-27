
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.service.PermisosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensoresPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final SensoresPanel view;
    private final PermisosService permisosService;

    private final String usuarioId;

    private final Map<String, String> contenedoresPorCodigo;
    private final List<Document> sensoresCargados;

    private String sensorIdEditando;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SensoresPanelController(
            SensoresPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        this.permisosService = new PermisosService();

        this.contenedoresPorCodigo = new HashMap<>();
        this.sensoresCargados = new ArrayList<>();

        this.sensorIdEditando = null;

        if (!permisosService.puedeAccederVista(
                usuarioId, "SENSORES")) {

            throw new SecurityException(
                    "No tiene permiso para acceder a Sensores."
            );
        }

        configurarPermisos();
        configurarEventos();
        cargarContenedores();
        cargarSensores();
    }

    // =========================================================
    // PERMISOS
    // =========================================================

    private void configurarPermisos() {

        boolean admin = permisosService.esAdmin(usuarioId);

        // El operador puede editar, pero no agregar ni eliminar.
        view.getBtnMostrarFormulario().setVisible(admin);
        view.getBtnEliminar().setVisible(admin);

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
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> actualizarTodo()
        );

        view.getBtnMostrarFormulario().addActionListener(
                e -> prepararNuevoSensor()
        );

        view.getBtnEditar().addActionListener(
                e -> editarSensorSeleccionado()
        );

        view.getBtnEliminar().addActionListener(
                e -> eliminarSensorSeleccionado()
        );

        view.getBtnCancelar().addActionListener(
                e -> cancelar()
        );

        view.getBtnGuardar().addActionListener(
                e -> guardarSensor()
        );
    }

    // =========================================================
    // ACTUALIZAR TODO
    // =========================================================

    private void actualizarTodo() {

        cargarContenedores();
        cargarSensores();
    }

    // =========================================================
    // PREPARAR NUEVO SENSOR
    // =========================================================

    private void prepararNuevoSensor() {

        exigirAdministrador();

        sensorIdEditando = null;

        cargarContenedores();
        view.mostrarFormularioNuevo();
    }

    // =========================================================
    // CARGAR CONTENEDORES
    // =========================================================

    private void cargarContenedores() {

        try {

            List<Document> contenedores =
                    mongo.listarContenedores();

            view.getCmbContenedor().removeAllItems();
            contenedoresPorCodigo.clear();

            for (Document contenedor : contenedores) {

                ObjectId id =
                        contenedor.getObjectId("_id");

                String codigo =
                        contenedor.getString("codigo_internacional");

                if (id != null && codigo != null) {

                    view.getCmbContenedor().addItem(codigo);

                    contenedoresPorCodigo.put(
                            codigo,
                            id.toHexString()
                    );
                }
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los contenedores.",
                    e
            );
        }
    }

    // =========================================================
    // CARGAR SENSORES
    // =========================================================

    private void cargarSensores() {

        try {

            List<Document> sensores =
                    mongo.listarSensores();

            sensoresCargados.clear();
            sensoresCargados.addAll(sensores);

            DefaultTableModel modelo =
                    (DefaultTableModel) view
                            .getTablaSensores()
                            .getModel();

            modelo.setRowCount(0);

            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat("dd/MM/yyyy");

            for (Document sensor : sensores) {

                String codigoSensor =
                        sensor.getString("codigo");

                if (codigoSensor == null) {
                    codigoSensor = "";
                }

                ObjectId contenedorId =
                        sensor.getObjectId("contenedor_id");

                String codigoContenedor =
                        obtenerCodigoContenedor(contenedorId);

                Date fecha =
                        sensor.getDate("fecha_instalacion");

                String fechaTexto =
                        fecha != null
                                ? formatoFecha.format(fecha)
                                : "";

                modelo.addRow(new Object[]{
                        codigoSensor,
                        codigoContenedor,
                        sensor.getString("tipo"),
                        sensor.getString("fabricante"),
                        fechaTexto,
                        sensor.getString("estado")
                });
            }

            view.getLblEstado().setText(
                    "Sensores registrados: " + sensores.size()
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los sensores.",
                    e
            );
        }
    }

    // =========================================================
    // OBTENER CÓDIGO DEL CONTENEDOR
    // =========================================================

    private String obtenerCodigoContenedor(
            ObjectId contenedorId
    ) {

        if (contenedorId == null) {
            return "";
        }

        try {

            Document contenedor =
                    mongo.buscarContenedorPorId(
                            contenedorId.toHexString()
                    );

            if (contenedor == null) {
                return contenedorId.toHexString();
            }

            String codigo =
                    contenedor.getString("codigo_internacional");

            return codigo != null
                    ? codigo
                    : contenedorId.toHexString();

        } catch (Exception e) {

            return contenedorId.toHexString();
        }
    }

    // =========================================================
    // OBTENER SENSOR SELECCIONADO
    // =========================================================

    private Document obtenerSensorSeleccionado() {

        JTable tabla = view.getTablaSensores();

        int filaSeleccionada =
                tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            return null;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(
                        filaSeleccionada
                );

        if (filaModelo < 0
                || filaModelo >= sensoresCargados.size()) {

            return null;
        }

        return sensoresCargados.get(filaModelo);
    }

    // =========================================================
    // EDITAR SENSOR
    // =========================================================

    private void editarSensorSeleccionado() {

        exigirEdicionPermitida();

        Document sensor = obtenerSensorSeleccionado();

        if (sensor == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un sensor para editar.",
                    "Sensores",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        ObjectId sensorId =
                sensor.getObjectId("_id");

        if (sensorId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del sensor.",
                    "Sensores",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        sensorIdEditando =
                sensorId.toHexString();

        cargarContenedores();

        ObjectId contenedorId =
                sensor.getObjectId("contenedor_id");

        String codigoContenedor =
                obtenerCodigoContenedor(contenedorId);

        view.getCmbContenedor().setSelectedItem(
                codigoContenedor
        );

        String tipo =
                sensor.getString("tipo");

        view.getTxtTipo().setText(
                tipo != null ? tipo : ""
        );

        String fabricante =
                sensor.getString("fabricante");

        view.getTxtFabricante().setText(
                fabricante != null ? fabricante : ""
        );

        Date fecha =
                sensor.getDate("fecha_instalacion");

        if (fecha != null) {

            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat("dd/MM/yyyy");

            view.getTxtFechaInstalacion().setText(
                    formatoFecha.format(fecha)
            );

        } else {

            view.getTxtFechaInstalacion().setText("");
        }

        String estado =
                sensor.getString("estado");

        if (estado != null) {

            view.getCmbEstado().setSelectedItem(
                    estado
            );
        }

        view.mostrarFormularioEdicion();
    }

    // =========================================================
    // ELIMINAR SENSOR
    // =========================================================

    private void eliminarSensorSeleccionado() {

        // Solamente el Administrador puede eliminar.
        exigirAdministrador();

        Document sensor = obtenerSensorSeleccionado();

        if (sensor == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un sensor para eliminar.",
                    "Sensores",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        ObjectId sensorId =
                sensor.getObjectId("_id");

        if (sensorId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del sensor.",
                    "Sensores",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        String codigo =
                sensor.getString("codigo");

        String descripcion =
                codigo != null
                        ? codigo
                        : sensorId.toHexString();

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Seguro que querés eliminar el sensor "
                                + descripcion + "?",
                        "Eliminar sensor",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            // Validación antes de modificar MongoDB.
            exigirAdministrador();

            mongo.eliminarSensor(
                    sensorId.toHexString()
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Sensor eliminado correctamente.",
                    "Sensores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            sensorIdEditando = null;

            view.limpiarFormulario();
            view.ocultarFormulario();

            cargarSensores();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el sensor.",
                    e
            );
        }
    }

    // =========================================================
    // GUARDAR SENSOR
    // =========================================================

    private void guardarSensor() {

        if (sensorIdEditando == null) {

            // Crear requiere Administrador.
            exigirAdministrador();
            agregarSensor();

        } else {

            // Editar admite Administrador u Operador.
            exigirEdicionPermitida();
            modificarSensor();
        }
    }

    // =========================================================
    // OBTENER CONTENEDOR SELECCIONADO
    // =========================================================

    private String obtenerContenedorIdSeleccionado() {

        Object seleccionado =
                view.getCmbContenedor().getSelectedItem();

        if (seleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        String codigo =
                seleccionado.toString().trim();

        String contenedorId =
                contenedoresPorCodigo.get(codigo);

        if (contenedorId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del contenedor seleccionado.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        return contenedorId;
    }

    // =========================================================
    // AGREGAR SENSOR
    // =========================================================

    private void agregarSensor() {

        exigirAdministrador();

        String contenedorId =
                obtenerContenedorIdSeleccionado();

        if (contenedorId == null) {
            return;
        }

        String tipo =
                view.getTxtTipo().getText().trim();

        String fabricante =
                view.getTxtFabricante().getText().trim();

        Date fechaInstalacion =
                obtenerFechaFormulario();

        if (fechaInstalacion == null) {
            return;
        }

        try {

            mongo.crearSensor(
                    contenedorId,
                    tipo,
                    fabricante,
                    fechaInstalacion
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Sensor agregado correctamente.",
                    "Sensores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            sensorIdEditando = null;

            view.limpiarFormulario();
            view.ocultarFormulario();

            cargarSensores();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el sensor.",
                    e
            );
        }
    }

    // =========================================================
    // MODIFICAR SENSOR
    // =========================================================

    private void modificarSensor() {

        exigirEdicionPermitida();

        if (sensorIdEditando == null) {

            throw new IllegalStateException(
                    "No hay un sensor seleccionado para editar."
            );
        }

        String contenedorId =
                obtenerContenedorIdSeleccionado();

        if (contenedorId == null) {
            return;
        }

        String tipo =
                view.getTxtTipo().getText().trim();

        String fabricante =
                view.getTxtFabricante().getText().trim();

        Date fechaInstalacion =
                obtenerFechaFormulario();

        if (fechaInstalacion == null) {
            return;
        }

        Object estadoSeleccionado =
                view.getCmbEstado().getSelectedItem();

        String estado =
                estadoSeleccionado != null
                        ? estadoSeleccionado.toString()
                        : "ACTIVO";

        try {

            mongo.modificarSensor(
                    sensorIdEditando,
                    contenedorId,
                    tipo,
                    fabricante,
                    fechaInstalacion,
                    estado
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Sensor modificado correctamente.",
                    "Sensores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            sensorIdEditando = null;

            view.limpiarFormulario();
            view.ocultarFormulario();

            cargarSensores();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el sensor.",
                    e
            );
        }
    }

    // =========================================================
    // OBTENER FECHA
    // =========================================================

    private Date obtenerFechaFormulario() {

        String fechaTexto = view
                .getTxtFechaInstalacion()
                .getText()
                .trim();

        SimpleDateFormat formatoFecha =
                new SimpleDateFormat("dd/MM/yyyy");

        formatoFecha.setLenient(false);

        try {

            return formatoFecha.parse(fechaTexto);

        } catch (ParseException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "La fecha debe tener el formato dd/MM/yyyy.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }
    }

    // =========================================================
    // CANCELAR
    // =========================================================

    private void cancelar() {

        sensorIdEditando = null;

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
