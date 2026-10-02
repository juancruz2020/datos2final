package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.example.mongoDB.controller.ContenedorController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ContenedoresPanelController {

    private final ContenedoresPanel view;

    private final ContenedorController controller;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ContenedoresPanelController(
            ContenedoresPanel view
    ) {

        this.view = view;

        this.controller =
                new ContenedorController();

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
        // MOSTRAR FORMULARIO
        // -----------------------------------------------------

        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> view.mostrarFormulario()
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
                        e -> agregarContenedor()
                );
    }


    // =========================================================
    // CARGAR CONTENEDORES
    // =========================================================

    private void cargarContenedores() {

        try {

            List<Document> contenedores =
                    controller.listarContenedores();


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaContenedores()
                                    .getModel();


            modelo.setRowCount(0);


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


        String capacidadTexto =
                view.getTxtCapacidad()
                        .getText()
                        .trim();


        // =====================================================
        // CONVERTIR CAPACIDAD
        // =====================================================

        double capacidad;


        try {

            capacidad =
                    Double.parseDouble(
                            capacidadTexto
                                    .replace(",", ".")
                    );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "La capacidad debe ser un número válido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // GUARDAR
        // =====================================================

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


            view.limpiarFormulario();

            view.ocultarFormulario();

            cargarContenedores();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el contenedor.",
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