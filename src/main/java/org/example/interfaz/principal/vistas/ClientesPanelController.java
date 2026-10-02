package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.example.mongoDB.controller.ClienteController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ClientesPanelController {

    private final ClientesPanel view;

    private final ClienteController controller;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ClientesPanelController(
            ClientesPanel view
    ) {

        this.view = view;

        this.controller =
                new ClienteController();

        configurarEventos();

        cargarClientes();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        // -----------------------------------------------------
        // ACTUALIZAR LISTADO
        // -----------------------------------------------------

        view.getBtnActualizar()
                .addActionListener(
                        e -> cargarClientes()
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
        // GUARDAR CLIENTE
        // -----------------------------------------------------

        view.getBtnAgregar()
                .addActionListener(
                        e -> agregarCliente()
                );
    }


    // =========================================================
    // CARGAR CLIENTES
    // =========================================================

    private void cargarClientes() {

        try {

            List<Document> clientes =
                    controller.listarClientes();


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaClientes()
                                    .getModel();


            modelo.setRowCount(0);


            for (Document cliente : clientes) {

                Document direccion =
                        cliente.get(
                                "direccion",
                                Document.class
                        );


                String ciudad = "";

                if (direccion != null) {

                    ciudad =
                            direccion.getString(
                                    "ciudad"
                            );
                }


                modelo.addRow(
                        new Object[]{
                                cliente.getString(
                                        "razon_social"
                                ),

                                cliente.getString(
                                        "cuit"
                                ),

                                cliente.getString(
                                        "email"
                                ),

                                cliente.getString(
                                        "telefono"
                                ),

                                ciudad,

                                cliente.getString(
                                        "pais"
                                ),

                                cliente.getString(
                                        "estado"
                                )
                        }
                );
            }


            view.getLblEstado()
                    .setText(
                            "Clientes registrados: "
                                    + clientes.size()
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los clientes.",
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
    // AGREGAR CLIENTE
    // =========================================================

    private void agregarCliente() {

        String razonSocial =
                view.getTxtRazonSocial()
                        .getText()
                        .trim();

        String cuit =
                view.getTxtCuit()
                        .getText()
                        .trim();

        String email =
                view.getTxtEmail()
                        .getText()
                        .trim();

        String telefono =
                view.getTxtTelefono()
                        .getText()
                        .trim();

        String calle =
                view.getTxtCalle()
                        .getText()
                        .trim();

        String numero =
                view.getTxtNumero()
                        .getText()
                        .trim();

        String ciudad =
                view.getTxtCiudad()
                        .getText()
                        .trim();

        String codigoPostal =
                view.getTxtCodigoPostal()
                        .getText()
                        .trim();

        String pais =
                view.getTxtPais()
                        .getText()
                        .trim();


        try {

            controller.crearCliente(
                    razonSocial,
                    cuit,
                    email,
                    telefono,
                    calle,
                    numero,
                    ciudad,
                    codigoPostal,
                    pais
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Cliente agregado correctamente.",
                    "Clientes",
                    JOptionPane.INFORMATION_MESSAGE
            );


            // -------------------------------------------------
            // LIMPIAR
            // -------------------------------------------------

            view.limpiarFormulario();


            // -------------------------------------------------
            // OCULTAR FORMULARIO
            // -------------------------------------------------

            view.ocultarFormulario();


            // -------------------------------------------------
            // ACTUALIZAR LISTADO
            // -------------------------------------------------

            cargarClientes();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el cliente.",
                    e
            );
        }
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