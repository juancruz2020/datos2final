package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.ContenedorController;
import org.example.mongoDB.controller.EnvioController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnviosPanelController {

    private final EnviosPanel view;

    private final EnvioController envioController;
    private final ClienteController clienteController;
    private final ContenedorController contenedorController;

    private final Map<String, String> clientesPorNombre;
    private final Map<String, String> contenedoresPorCodigo;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EnviosPanelController(
            EnviosPanel view
    ) {

        this.view =
                view;

        this.envioController =
                new EnvioController();

        this.clienteController =
                new ClienteController();

        this.contenedorController =
                new ContenedorController();

        this.clientesPorNombre =
                new HashMap<>();

        this.contenedoresPorCodigo =
                new HashMap<>();


        configurarEventos();

        cargarClientes();

        cargarContenedores();

        cargarEnvios();
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

                            cargarClientes();

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
                        e -> agregarEnvio()
                );
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizarTodo() {

        cargarClientes();

        cargarContenedores();

        cargarEnvios();
    }


    // =========================================================
    // CARGAR CLIENTES
    // =========================================================

    private void cargarClientes() {

        try {

            List<Document> clientes =
                    clienteController
                            .listarClientes();


            view.getCmbCliente()
                    .removeAllItems();


            clientesPorNombre.clear();


            for (Document cliente : clientes) {

                ObjectId id =
                        cliente.getObjectId(
                                "_id"
                        );


                String razonSocial =
                        cliente.getString(
                                "razon_social"
                        );


                if (id != null
                        && razonSocial != null) {

                    view.getCmbCliente()
                            .addItem(
                                    razonSocial
                            );


                    clientesPorNombre.put(
                            razonSocial,
                            id.toHexString()
                    );
                }
            }


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los clientes.",
                    e
            );
        }
    }


    // =========================================================
    // CARGAR CONTENEDORES
    // =========================================================

    private void cargarContenedores() {

        try {

            List<Document> contenedores =
                    contenedorController
                            .listarContenedores();


            DefaultListModel<String> modelo =
                    new DefaultListModel<>();


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


                if (id != null
                        && codigo != null) {

                    modelo.addElement(
                            codigo
                    );


                    contenedoresPorCodigo.put(
                            codigo,
                            id.toHexString()
                    );
                }
            }


            view.getListaContenedores()
                    .setModel(
                            modelo
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los contenedores.",
                    e
            );
        }
    }


    // =========================================================
    // CARGAR ENVÍOS
    // =========================================================

    private void cargarEnvios() {

        try {

            List<Document> envios =
                    envioController
                            .listarEnvios();


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaEnvios()
                                    .getModel();


            modelo.setRowCount(
                    0
            );


            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat(
                            "dd/MM/yyyy"
                    );


            for (Document envio : envios) {

                // -------------------------------------------------
                // CLIENTE
                // -------------------------------------------------

                ObjectId clienteId =
                        envio.getObjectId(
                                "cliente_id"
                        );


                String cliente =
                        obtenerNombreCliente(
                                clienteId
                        );


                // -------------------------------------------------
                // CONTENEDORES
                // -------------------------------------------------

                List<ObjectId> contenedoresIds =
                        obtenerListaObjectId(
                                envio,
                                "contenedores_ids"
                        );


                String contenedores =
                        obtenerCodigosContenedores(
                                contenedoresIds
                        );


                // -------------------------------------------------
                // FECHA
                // -------------------------------------------------

                Date fecha =
                        envio.getDate(
                                "fecha_creacion"
                        );


                String fechaTexto =
                        fecha != null
                                ? formatoFecha.format(fecha)
                                : "";


                // -------------------------------------------------
                // ORIGEN
                // -------------------------------------------------

                Document origen =
                        envio.get(
                                "origen",
                                Document.class
                        );


                String origenTexto =
                        obtenerUbicacionTexto(
                                origen
                        );


                // -------------------------------------------------
                // DESTINO
                // -------------------------------------------------

                Document destino =
                        envio.get(
                                "destino",
                                Document.class
                        );


                String destinoTexto =
                        obtenerUbicacionTexto(
                                destino
                        );


                // -------------------------------------------------
                // TABLA
                // -------------------------------------------------

                modelo.addRow(
                        new Object[]{
                                cliente,
                                contenedores,
                                fechaTexto,
                                origenTexto,
                                destinoTexto,
                                envio.getString(
                                        "estado"
                                ),
                                envio.getString(
                                        "prioridad"
                                )
                        }
                );
            }


            view.getLblEstado()
                    .setText(
                            "Envíos registrados: "
                                    + envios.size()
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los envíos.",
                    e
            );
        }
    }


    // =========================================================
    // OBTENER LISTA DE OBJECT ID
    // =========================================================

    private List<ObjectId> obtenerListaObjectId(
            Document documento,
            String campo
    ) {

        List<ObjectId> resultado =
                new ArrayList<>();


        Object valor =
                documento.get(
                        campo
                );


        if (!(valor instanceof List<?> lista)) {

            return resultado;
        }


        for (Object elemento : lista) {

            if (elemento instanceof ObjectId) {

                resultado.add(
                        (ObjectId) elemento
                );
            }
        }


        return resultado;
    }


    // =========================================================
    // OBTENER CLIENTE
    // =========================================================

    private String obtenerNombreCliente(
            ObjectId clienteId
    ) {

        if (clienteId == null) {

            return "";
        }


        try {

            Document cliente =
                    clienteController
                            .buscarPorId(
                                    clienteId.toHexString()
                            );


            if (cliente == null) {

                return clienteId.toHexString();
            }


            String razonSocial =
                    cliente.getString(
                            "razon_social"
                    );


            return razonSocial != null
                    ? razonSocial
                    : clienteId.toHexString();


        } catch (Exception e) {

            return clienteId.toHexString();
        }
    }


    // =========================================================
    // OBTENER CÓDIGOS DE CONTENEDORES
    // =========================================================

    private String obtenerCodigosContenedores(
            List<ObjectId> ids
    ) {

        List<String> codigos =
                new ArrayList<>();


        for (ObjectId id : ids) {

            try {

                Document contenedor =
                        contenedorController
                                .buscarPorId(
                                        id.toHexString()
                                );


                if (contenedor != null) {

                    String codigo =
                            contenedor.getString(
                                    "codigo_internacional"
                            );


                    if (codigo != null) {

                        codigos.add(
                                codigo
                        );

                        continue;
                    }
                }


                codigos.add(
                        id.toHexString()
                );


            } catch (Exception e) {

                codigos.add(
                        id.toHexString()
                );
            }
        }


        return String.join(
                ", ",
                codigos
        );
    }


    // =========================================================
    // UBICACIÓN
    // =========================================================

    private String obtenerUbicacionTexto(
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
    // AGREGAR ENVÍO
    // =========================================================

    private void agregarEnvio() {

        // =====================================================
        // CLIENTE
        // =====================================================

        Object clienteSeleccionado =
                view.getCmbCliente()
                        .getSelectedItem();


        if (clienteSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un cliente.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String clienteId =
                clientesPorNombre.get(
                        clienteSeleccionado.toString()
                );


        if (clienteId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del cliente.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // CONTENEDORES
        // =====================================================

        List<String> seleccionados =
                view.getListaContenedores()
                        .getSelectedValuesList();


        if (seleccionados.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná al menos un contenedor.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        List<String> contenedoresIds =
                new ArrayList<>();


        for (String codigo : seleccionados) {

            String id =
                    contenedoresPorCodigo.get(
                            codigo
                    );


            if (id != null) {

                contenedoresIds.add(
                        id
                );
            }
        }


        if (contenedoresIds.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudieron obtener los IDs de los contenedores.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // UBICACIONES
        // =====================================================

        String ciudadOrigen =
                view.getTxtCiudadOrigen()
                        .getText()
                        .trim();


        String paisOrigen =
                view.getTxtPaisOrigen()
                        .getText()
                        .trim();


        String ciudadDestino =
                view.getTxtCiudadDestino()
                        .getText()
                        .trim();


        String paisDestino =
                view.getTxtPaisDestino()
                        .getText()
                        .trim();


        // =====================================================
        // PRIORIDAD
        // =====================================================

        Object prioridadSeleccionada =
                view.getCmbPrioridad()
                        .getSelectedItem();


        if (prioridadSeleccionada == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una prioridad.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String prioridad =
                prioridadSeleccionada.toString();


        // =====================================================
        // GUARDAR
        // =====================================================

        try {

            envioController.crearEnvio(
                    clienteId,
                    contenedoresIds,
                    ciudadOrigen,
                    paisOrigen,
                    ciudadDestino,
                    paisDestino,
                    prioridad
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Envío agregado correctamente.",
                    "Envíos",
                    JOptionPane.INFORMATION_MESSAGE
            );


            view.limpiarFormulario();

            view.ocultarFormulario();

            cargarEnvios();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el envío.",
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