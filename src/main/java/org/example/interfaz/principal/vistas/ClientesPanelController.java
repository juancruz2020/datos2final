package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClientesPanelController {

    private final ClientesPanel view;

    private final ClienteController controller;

    private final List<String> clientesIds;

    private String clienteEditandoId;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ClientesPanelController(
            ClientesPanel view
    ) {

        this.view = view;

        this.controller =
                new ClienteController();

        this.clientesIds =
                new ArrayList<>();

        this.clienteEditandoId =
                null;

        configurarEventos();

        cargarClientes();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizar()
                .addActionListener(
                        e -> cargarClientes()
                );


        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> prepararNuevoCliente()
                );


        view.getBtnEditar()
                .addActionListener(
                        e -> editarClienteSeleccionado()
                );


        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarClienteSeleccionado()
                );


        view.getBtnCancelar()
                .addActionListener(
                        e -> cancelar()
                );


        view.getBtnAgregar()
                .addActionListener(
                        e -> guardarCliente()
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

            clientesIds.clear();


            for (Document cliente : clientes) {

                ObjectId id =
                        cliente.getObjectId(
                                "_id"
                        );


                if (id != null) {

                    clientesIds.add(
                            id.toHexString()
                    );

                } else {

                    clientesIds.add(
                            null
                    );
                }


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
    // PREPARAR NUEVO CLIENTE
    // =========================================================

    private void prepararNuevoCliente() {

        clienteEditandoId =
                null;

        view.limpiarFormulario();

        view.getBtnAgregar()
                .setText(
                        "Guardar cliente"
                );

        view.mostrarFormulario();
    }


    // =========================================================
    // EDITAR CLIENTE SELECCIONADO
    // =========================================================

    private void editarClienteSeleccionado() {

        int fila =
                view.getTablaClientes()
                        .getSelectedRow();


        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un cliente para editar.",
                    "Clientes",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            clienteEditandoId =
                    clientesIds.get(
                            fila
                    );


            Document cliente =
                    controller.buscarPorId(
                            clienteEditandoId
                    );


            Document direccion =
                    cliente.get(
                            "direccion",
                            Document.class
                    );


            view.getTxtRazonSocial()
                    .setText(
                            cliente.getString(
                                    "razon_social"
                            )
                    );


            view.getTxtCuit()
                    .setText(
                            cliente.getString(
                                    "cuit"
                            )
                    );


            view.getTxtEmail()
                    .setText(
                            cliente.getString(
                                    "email"
                            )
                    );


            view.getTxtTelefono()
                    .setText(
                            cliente.getString(
                                    "telefono"
                            )
                    );


            view.getTxtPais()
                    .setText(
                            cliente.getString(
                                    "pais"
                            )
                    );


            if (direccion != null) {

                view.getTxtCalle()
                        .setText(
                                direccion.getString(
                                        "calle"
                                )
                        );


                view.getTxtNumero()
                        .setText(
                                direccion.getString(
                                        "numero"
                                )
                        );


                view.getTxtCiudad()
                        .setText(
                                direccion.getString(
                                        "ciudad"
                                )
                        );


                view.getTxtCodigoPostal()
                        .setText(
                                direccion.getString(
                                        "codigo_postal"
                                )
                        );

            } else {

                view.getTxtCalle()
                        .setText("");

                view.getTxtNumero()
                        .setText("");

                view.getTxtCiudad()
                        .setText("");

                view.getTxtCodigoPostal()
                        .setText("");
            }


            view.getBtnAgregar()
                    .setText(
                            "Guardar cambios"
                    );


            view.mostrarFormulario();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo cargar el cliente.",
                    e
            );
        }
    }


    // =========================================================
    // GUARDAR CLIENTE
    // =========================================================

    private void guardarCliente() {

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

            if (clienteEditandoId == null) {

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

            } else {

                Document actual =
                        controller.buscarPorId(
                                clienteEditandoId
                        );


                String estado =
                        actual.getString(
                                "estado"
                        );


                if (estado == null
                        || estado.isBlank()) {

                    estado =
                            "ACTIVO";
                }


                controller.modificarCliente(
                        clienteEditandoId,
                        razonSocial,
                        cuit,
                        email,
                        telefono,
                        calle,
                        numero,
                        ciudad,
                        codigoPostal,
                        pais,
                        estado
                );


                JOptionPane.showMessageDialog(
                        view,
                        "Cliente modificado correctamente.",
                        "Clientes",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }


            clienteEditandoId =
                    null;


            view.limpiarFormulario();

            view.getBtnAgregar()
                    .setText(
                            "Guardar cliente"
                    );

            view.ocultarFormulario();

            cargarClientes();


        } catch (Exception e) {

            mostrarError(
                    clienteEditandoId == null
                            ? "No se pudo agregar el cliente."
                            : "No se pudo modificar el cliente.",
                    e
            );
        }
    }


    // =========================================================
    // ELIMINAR CLIENTE
    // =========================================================

    private void eliminarClienteSeleccionado() {

        int fila =
                view.getTablaClientes()
                        .getSelectedRow();


        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un cliente para eliminar.",
                    "Clientes",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String clienteId =
                clientesIds.get(
                        fila
                );


        String razonSocial =
                String.valueOf(
                        view.getTablaClientes()
                                .getValueAt(
                                        fila,
                                        0
                                )
                );


        int opcion =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Seguro que querés eliminar al cliente \""
                                + razonSocial
                                + "\"?",
                        "Eliminar cliente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (opcion != JOptionPane.YES_OPTION) {

            return;
        }


        try {

            controller.eliminarCliente(
                    clienteId
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Cliente eliminado correctamente.",
                    "Clientes",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clienteEditandoId =
                    null;

            view.limpiarFormulario();

            view.ocultarFormulario();

            cargarClientes();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el cliente.",
                    e
            );
        }
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    private void cancelar() {

        clienteEditandoId =
                null;

        view.limpiarFormulario();

        view.getBtnAgregar()
                .setText(
                        "Guardar cliente"
                );

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