package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ContenedorController;
import org.example.mongoDB.controller.SensorController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensoresPanelController {

    private final SensoresPanel view;

    private final SensorController sensorController;

    private final ContenedorController contenedorController;

    private final Map<String, String> contenedoresPorCodigo;


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


        configurarEventos();

        cargarContenedores();

        cargarSensores();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizar()
                .addActionListener(
                        e -> actualizarTodo()
                );


        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> {
                            cargarContenedores();
                            view.mostrarFormulario();
                        }
                );


        view.getBtnCancelar()
                .addActionListener(
                        e -> cancelar()
                );


        view.getBtnGuardar()
                .addActionListener(
                        e -> agregarSensor()
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


        String fechaTexto =
                view.getTxtFechaInstalacion()
                        .getText()
                        .trim();


        // =====================================================
        // FECHA
        // =====================================================

        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                );

        formatoFecha.setLenient(
                false
        );


        Date fechaInstalacion;


        try {

            fechaInstalacion =
                    formatoFecha.parse(
                            fechaTexto
                    );


        } catch (ParseException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "La fecha debe tener el formato dd/MM/yyyy.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // GUARDAR
        // =====================================================

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
    // CANCELAR
    // =========================================================

    private void cancelar() {

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