package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ContenedorController;
import org.example.mongoDB.controller.SensorController;

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

    private final SensoresPanel view;

    private final SensorController sensorController;

    private final ContenedorController contenedorController;

    private final Map<String, String> contenedoresPorCodigo;

    private final List<Document> sensoresCargados;

    private String sensorIdEditando;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SensoresPanelController(
            SensoresPanel view
    ) {

        this.view = view;

        this.sensorController =
                new SensorController();

        this.contenedorController =
                new ContenedorController();

        this.contenedoresPorCodigo =
                new HashMap<>();

        this.sensoresCargados =
                new ArrayList<>();

        this.sensorIdEditando =
                null;


        configurarEventos();

        cargarContenedores();

        cargarSensores();
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
                        e -> actualizarTodo()
                );


        // -----------------------------------------------------
        // NUEVO SENSOR
        // -----------------------------------------------------

        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> prepararNuevoSensor()
                );


        // -----------------------------------------------------
        // EDITAR SENSOR
        // -----------------------------------------------------

        view.getBtnEditar()
                .addActionListener(
                        e -> editarSensorSeleccionado()
                );


        // -----------------------------------------------------
        // ELIMINAR SENSOR
        // -----------------------------------------------------

        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarSensorSeleccionado()
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
                    contenedorController
                            .listarContenedores();


            view.getCmbContenedor()
                    .removeAllItems();


            contenedoresPorCodigo.clear();


            for (Document contenedor : contenedores) {

                ObjectId id =
                        contenedor.getObjectId(
                                "_id"
                        );


                String codigo =
                        contenedor.getString(
                                "codigo_internacional"
                        );


                if (
                        id != null
                                && codigo != null
                ) {

                    view.getCmbContenedor()
                            .addItem(
                                    codigo
                            );


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
                    sensorController
                            .listarSensores();


            sensoresCargados.clear();

            sensoresCargados.addAll(
                    sensores
            );


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaSensores()
                                    .getModel();


            modelo.setRowCount(
                    0
            );


            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat(
                            "dd/MM/yyyy"
                    );


            for (Document sensor : sensores) {

                ObjectId contenedorId =
                        sensor.getObjectId(
                                "contenedor_id"
                        );


                String codigoContenedor =
                        obtenerCodigoContenedor(
                                contenedorId
                        );


                Date fecha =
                        sensor.getDate(
                                "fecha_instalacion"
                        );


                String fechaTexto =
                        fecha != null
                                ? formatoFecha.format(fecha)
                                : "";


                modelo.addRow(
                        new Object[]{
                                codigoContenedor,

                                sensor.getString(
                                        "tipo"
                                ),

                                sensor.getString(
                                        "fabricante"
                                ),

                                fechaTexto,

                                sensor.getString(
                                        "estado"
                                )
                        }
                );
            }


            view.getLblEstado()
                    .setText(
                            "Sensores registrados: "
                                    + sensores.size()
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
                    contenedorController
                            .buscarPorId(
                                    contenedorId
                                            .toHexString()
                            );


            if (contenedor == null) {

                return contenedorId
                        .toHexString();
            }


            String codigo =
                    contenedor.getString(
                            "codigo_internacional"
                    );


            return codigo != null
                    ? codigo
                    : contenedorId.toHexString();

        } catch (Exception e) {

            return contenedorId
                    .toHexString();
        }
    }


    // =========================================================
    // EDITAR SENSOR SELECCIONADO
    // =========================================================

    private void editarSensorSeleccionado() {

        int fila =
                view.getTablaSensores()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un sensor para editar.",
                    "Sensores",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (
                fila < 0
                        || fila >= sensoresCargados.size()
        ) {

            return;
        }


        Document sensor =
                sensoresCargados.get(
                        fila
                );


        ObjectId sensorId =
                sensor.getObjectId(
                        "_id"
                );


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


        // -----------------------------------------------------
        // CONTENEDOR
        // -----------------------------------------------------

        cargarContenedores();


        ObjectId contenedorId =
                sensor.getObjectId(
                        "contenedor_id"
                );


        String codigoContenedor =
                obtenerCodigoContenedor(
                        contenedorId
                );


        view.getCmbContenedor()
                .setSelectedItem(
                        codigoContenedor
                );


        // -----------------------------------------------------
        // TIPO
        // -----------------------------------------------------

        String tipo =
                sensor.getString(
                        "tipo"
                );


        view.getTxtTipo()
                .setText(
                        tipo != null
                                ? tipo
                                : ""
                );


        // -----------------------------------------------------
        // FABRICANTE
        // -----------------------------------------------------

        String fabricante =
                sensor.getString(
                        "fabricante"
                );


        view.getTxtFabricante()
                .setText(
                        fabricante != null
                                ? fabricante
                                : ""
                );


        // -----------------------------------------------------
        // FECHA
        // -----------------------------------------------------

        Date fecha =
                sensor.getDate(
                        "fecha_instalacion"
                );


        if (fecha != null) {

            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat(
                            "dd/MM/yyyy"
                    );


            view.getTxtFechaInstalacion()
                    .setText(
                            formatoFecha.format(
                                    fecha
                            )
                    );

        } else {

            view.getTxtFechaInstalacion()
                    .setText("");
        }


        // -----------------------------------------------------
        // ESTADO
        // -----------------------------------------------------

        String estado =
                sensor.getString(
                        "estado"
                );


        if (estado != null) {

            view.getCmbEstado()
                    .setSelectedItem(
                            estado
                    );
        }


        view.mostrarFormularioEdicion();
    }


    // =========================================================
    // ELIMINAR SENSOR SELECCIONADO
    // =========================================================

    private void eliminarSensorSeleccionado() {

        int fila =
                view.getTablaSensores()
                        .getSelectedRow();


        // -----------------------------------------------------
        // VALIDAR SELECCIÓN
        // -----------------------------------------------------

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un sensor para eliminar.",
                    "Sensores",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (
                fila < 0
                        || fila >= sensoresCargados.size()
        ) {

            return;
        }


        // -----------------------------------------------------
        // OBTENER SENSOR
        // -----------------------------------------------------

        Document sensor =
                sensoresCargados.get(
                        fila
                );


        ObjectId sensorId =
                sensor.getObjectId(
                        "_id"
                );


        if (sensorId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del sensor.",
                    "Sensores",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        // -----------------------------------------------------
        // CONFIRMAR ELIMINACIÓN
        // -----------------------------------------------------

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Está seguro de eliminar este sensor?",
                        "Eliminar sensor",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        try {

            sensorController.eliminarSensor(
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

            agregarSensor();

        } else {

            modificarSensor();
        }
    }


    // =========================================================
    // AGREGAR SENSOR
    // =========================================================

    private void agregarSensor() {

        Object contenedorSeleccionado =
                view.getCmbContenedor()
                        .getSelectedItem();


        if (contenedorSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String codigoContenedor =
                contenedorSeleccionado
                        .toString()
                        .trim();


        String contenedorId =
                contenedoresPorCodigo.get(
                        codigoContenedor
                );


        if (contenedorId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del contenedor seleccionado.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();


        String fabricante =
                view.getTxtFabricante()
                        .getText()
                        .trim();


        Date fechaInstalacion =
                obtenerFechaFormulario();


        if (fechaInstalacion == null) {

            return;
        }


        try {

            sensorController.crearSensor(
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

        Object contenedorSeleccionado =
                view.getCmbContenedor()
                        .getSelectedItem();


        if (contenedorSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String codigoContenedor =
                contenedorSeleccionado
                        .toString()
                        .trim();


        String contenedorId =
                contenedoresPorCodigo.get(
                        codigoContenedor
                );


        if (contenedorId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del contenedor seleccionado.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();


        String fabricante =
                view.getTxtFabricante()
                        .getText()
                        .trim();


        Date fechaInstalacion =
                obtenerFechaFormulario();


        if (fechaInstalacion == null) {

            return;
        }


        Object estadoSeleccionado =
                view.getCmbEstado()
                        .getSelectedItem();


        String estado =
                estadoSeleccionado != null
                        ? estadoSeleccionado.toString()
                        : "ACTIVO";


        try {

            sensorController.modificarSensor(
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
    // OBTENER FECHA DEL FORMULARIO
    // =========================================================

    private Date obtenerFechaFormulario() {

        String fechaTexto =
                view.getTxtFechaInstalacion()
                        .getText()
                        .trim();


        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                );


        formatoFecha.setLenient(
                false
        );


        try {

            return formatoFecha.parse(
                    fechaTexto
            );

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
                        + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}