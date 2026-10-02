package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.EnvioController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrazabilidadPanelController {

    private final TrazabilidadPanel view;

    private final EnvioController envioController;

    private final Map<String, String> enviosPorDescripcion;

    private final SimpleDateFormat formatoFecha;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TrazabilidadPanelController(
            TrazabilidadPanel view
    ) {

        this.view =
                view;

        this.envioController =
                new EnvioController();

        this.enviosPorDescripcion =
                new HashMap<>();

        this.formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                );

        this.formatoFecha.setLenient(
                false
        );


        configurarEventos();

        cargarEnvios();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getCmbEnvio()
                .addActionListener(
                        e -> cargarTramosSeleccionados()
                );


        view.getBtnActualizar()
                .addActionListener(
                        e -> cargarEnvios()
                );


        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> mostrarFormulario()
                );


        view.getBtnCancelar()
                .addActionListener(
                        e -> cancelar()
                );


        view.getBtnGuardar()
                .addActionListener(
                        e -> guardarTramo()
                );
    }


    // =========================================================
    // CARGAR ENVÍOS
    // =========================================================

    private void cargarEnvios() {

        try {

            List<Document> envios =
                    envioController
                            .listarEnvios();


            Object seleccionadoAnterior =
                    view.getCmbEnvio()
                            .getSelectedItem();


            view.getCmbEnvio()
                    .removeAllItems();


            enviosPorDescripcion.clear();


            for (Document envio : envios) {

                ObjectId id =
                        envio.getObjectId(
                                "_id"
                        );


                if (id == null) {

                    continue;
                }


                Document origen =
                        envio.get(
                                "origen",
                                Document.class
                        );


                Document destino =
                        envio.get(
                                "destino",
                                Document.class
                        );


                String origenTexto =
                        obtenerUbicacion(
                                origen
                        );


                String destinoTexto =
                        obtenerUbicacion(
                                destino
                        );


                String descripcion =
                        id.toHexString()
                                + " | "
                                + origenTexto
                                + " → "
                                + destinoTexto;


                view.getCmbEnvio()
                        .addItem(
                                descripcion
                        );


                enviosPorDescripcion.put(
                        descripcion,
                        id.toHexString()
                );
            }


            // -------------------------------------------------
            // RESTAURAR SELECCIÓN
            // -------------------------------------------------

            if (seleccionadoAnterior != null
                    && enviosPorDescripcion.containsKey(
                    seleccionadoAnterior.toString()
            )) {

                view.getCmbEnvio()
                        .setSelectedItem(
                                seleccionadoAnterior
                        );
            }


            // -------------------------------------------------
            // SIN ENVÍOS
            // -------------------------------------------------

            if (view.getCmbEnvio()
                    .getItemCount() == 0) {

                limpiarTabla();

                view.getLblEstado()
                        .setText(
                                "No hay envíos registrados."
                        );

                return;
            }


            cargarTramosSeleccionados();


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los envíos.",
                    e
            );
        }
    }


    // =========================================================
    // CARGAR TRAMOS DEL ENVÍO SELECCIONADO
    // =========================================================

    private void cargarTramosSeleccionados() {

        Object seleccionado =
                view.getCmbEnvio()
                        .getSelectedItem();


        if (seleccionado == null) {

            limpiarTabla();

            return;
        }


        String envioId =
                enviosPorDescripcion.get(
                        seleccionado.toString()
                );


        if (envioId == null) {

            limpiarTabla();

            return;
        }


        try {

            Document envio =
                    envioController
                            .buscarPorId(
                                    envioId
                            );


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaTramos()
                                    .getModel();


            modelo.setRowCount(
                    0
            );


            List<Document> tramos =
                    envio.getList(
                            "tramos",
                            Document.class
                    );


            if (tramos == null) {

                view.getLblEstado()
                        .setText(
                                "El envío no tiene tramos registrados."
                        );

                return;
            }


            for (Document tramo : tramos) {

                Date fechaSalida =
                        tramo.getDate(
                                "fecha_salida"
                        );


                Date fechaLlegada =
                        tramo.getDate(
                                "fecha_llegada_estimada"
                        );


                modelo.addRow(
                        new Object[]{
                                tramo.getString(
                                        "medio_transporte"
                                ),

                                tramo.getString(
                                        "origen"
                                ),

                                tramo.getString(
                                        "destino"
                                ),

                                formatearFecha(
                                        fechaSalida
                                ),

                                formatearFecha(
                                        fechaLlegada
                                )
                        }
                );
            }


            if (tramos.isEmpty()) {

                view.getLblEstado()
                        .setText(
                                "El envío no tiene tramos registrados."
                        );

            } else {

                view.getLblEstado()
                        .setText(
                                "Tramos registrados: "
                                        + tramos.size()
                        );
            }


        } catch (Exception e) {

            mostrarError(
                    "No se pudo cargar la trazabilidad del envío.",
                    e
            );
        }
    }


    // =========================================================
    // MOSTRAR FORMULARIO
    // =========================================================

    private void mostrarFormulario() {

        if (view.getCmbEnvio()
                .getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Primero seleccioná un envío.",
                    "Trazabilidad",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        view.mostrarFormulario();
    }


    // =========================================================
    // GUARDAR TRAMO
    // =========================================================

    private void guardarTramo() {

        Object envioSeleccionado =
                view.getCmbEnvio()
                        .getSelectedItem();


        if (envioSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un envío.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String envioId =
                enviosPorDescripcion.get(
                        envioSeleccionado.toString()
                );


        if (envioId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del envío.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // MEDIO DE TRANSPORTE
        // =====================================================

        Object medioSeleccionado =
                view.getCmbMedioTransporte()
                        .getSelectedItem();


        if (medioSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un medio de transporte.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String medioTransporte =
                medioSeleccionado.toString();


        // =====================================================
        // ORIGEN / DESTINO
        // =====================================================

        String origen =
                view.getTxtOrigen()
                        .getText()
                        .trim();


        String destino =
                view.getTxtDestino()
                        .getText()
                        .trim();


        // =====================================================
        // FECHAS
        // =====================================================

        String fechaSalidaTexto =
                view.getTxtFechaSalida()
                        .getText()
                        .trim();


        String fechaLlegadaTexto =
                view.getTxtFechaLlegada()
                        .getText()
                        .trim();


        Date fechaSalida;

        Date fechaLlegada;


        try {

            fechaSalida =
                    convertirFecha(
                            fechaSalidaTexto,
                            "La fecha de salida debe tener formato dd/MM/yyyy."
                    );


            fechaLlegada =
                    convertirFecha(
                            fechaLlegadaTexto,
                            "La fecha de llegada debe tener formato dd/MM/yyyy."
                    );


        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // GUARDAR
        // =====================================================

        try {

            envioController.agregarTramo(
                    envioId,
                    medioTransporte,
                    origen,
                    destino,
                    fechaSalida,
                    fechaLlegada
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Tramo agregado correctamente.",
                    "Trazabilidad",
                    JOptionPane.INFORMATION_MESSAGE
            );


            view.limpiarFormulario();

            view.ocultarFormulario();


            cargarTramosSeleccionados();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el tramo.",
                    e
            );
        }
    }


    // =========================================================
    // CONVERTIR FECHA
    // =========================================================

    private Date convertirFecha(
            String texto,
            String mensajeError
    ) {

        if (texto == null
                || texto.isBlank()) {

            throw new IllegalArgumentException(
                    mensajeError
            );
        }


        try {

            return formatoFecha.parse(
                    texto
            );


        } catch (ParseException e) {

            throw new IllegalArgumentException(
                    mensajeError
            );
        }
    }


    // =========================================================
    // FORMATEAR FECHA
    // =========================================================

    private String formatearFecha(
            Date fecha
    ) {

        if (fecha == null) {

            return "";
        }


        return formatoFecha.format(
                fecha
        );
    }


    // =========================================================
    // OBTENER UBICACIÓN
    // =========================================================

    private String obtenerUbicacion(
            Document ubicacion
    ) {

        if (ubicacion == null) {

            return "";
        }


        String ciudad =
                ubicacion.getString(
                        "ciudad"
                );


        String pais =
                ubicacion.getString(
                        "pais"
                );


        if (ciudad == null) {

            ciudad = "";
        }


        if (pais == null) {

            pais = "";
        }


        if (ciudad.isBlank()) {

            return pais;
        }


        if (pais.isBlank()) {

            return ciudad;
        }


        return ciudad
                + ", "
                + pais;
    }


    // =========================================================
    // LIMPIAR TABLA
    // =========================================================

    private void limpiarTabla() {

        DefaultTableModel modelo =
                (DefaultTableModel)
                        view.getTablaTramos()
                                .getModel();


        modelo.setRowCount(
                0
        );
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