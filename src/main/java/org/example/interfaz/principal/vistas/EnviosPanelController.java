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

    // Guarda los IDs en el mismo orden que las filas de la tabla
    private final List<String> idsEnvios;

    // null = alta / distinto de null = edición
    private String envioIdEnEdicion;


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

        this.idsEnvios =
                new ArrayList<>();

        this.envioIdEnEdicion =
                null;

        configurarEventos();

        cargarClientes();

        cargarContenedores();

        cargarEnvios();
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
        // NUEVO ENVÍO
        // -----------------------------------------------------

        view.getBtnMostrarFormulario()
                .addActionListener(
                        e -> prepararNuevoEnvio()
                );


        // -----------------------------------------------------
        // EDITAR
        // -----------------------------------------------------

        view.getBtnEditar()
                .addActionListener(
                        e -> editarEnvioSeleccionado()
                );


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarEnvioSeleccionado()
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
    // ACTUALIZAR
    // =========================================================

    private void actualizarTodo() {

        cargarClientes();

        cargarContenedores();

        cargarEnvios();
    }


    // =========================================================
    // PREPARAR NUEVO ENVÍO
    // =========================================================

    private void prepararNuevoEnvio() {

        envioIdEnEdicion =
                null;

        cargarClientes();

        cargarContenedores();

        view.limpiarFormulario();

        view.prepararNuevoEnvio();

        view.mostrarFormulario();
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

            idsEnvios.clear();

            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat(
                            "dd/MM/yyyy"
                    );

            for (Document envio : envios) {

                // -------------------------------------------------
                // ID
                // -------------------------------------------------

                ObjectId envioId =
                        envio.getObjectId(
                                "_id"
                        );

                if (envioId == null) {

                    continue;
                }


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

                idsEnvios.add(
                        envioId.toHexString()
                );
            }

            view.getLblEstado()
                    .setText(
                            "Envíos registrados: "
                                    + idsEnvios.size()
                    );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los envíos.",
                    e
            );
        }
    }


    // =========================================================
    // EDITAR ENVÍO SELECCIONADO
    // =========================================================

    private void editarEnvioSeleccionado() {

        int fila =
                view.getTablaEnvios()
                        .getSelectedRow();

        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un envío para editar.",
                    "Envíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (fila >= idsEnvios.size()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el envío seleccionado.",
                    "Envíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String envioId =
                idsEnvios.get(
                        fila
                );

        try {

            Document envio =
                    envioController.buscarPorId(
                            envioId
                    );

            envioIdEnEdicion =
                    envioId;

            // Volvemos a cargar para asegurarnos de tener
            // clientes y contenedores actualizados.
            cargarClientes();

            cargarContenedores();

            cargarDatosEnFormulario(
                    envio
            );

            view.prepararEdicion();

            view.mostrarFormulario();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo cargar el envío para editar.",
                    e
            );
        }
    }


    // =========================================================
    // CARGAR DATOS EN FORMULARIO
    // =========================================================

    private void cargarDatosEnFormulario(
            Document envio
    ) {

        // -----------------------------------------------------
        // CLIENTE
        // -----------------------------------------------------

        ObjectId clienteId =
                envio.getObjectId(
                        "cliente_id"
                );

        seleccionarClientePorId(
                clienteId
        );


        // -----------------------------------------------------
        // CONTENEDORES
        // -----------------------------------------------------

        List<ObjectId> contenedoresIds =
                obtenerListaObjectId(
                        envio,
                        "contenedores_ids"
                );

        seleccionarContenedoresPorId(
                contenedoresIds
        );


        // -----------------------------------------------------
        // ORIGEN
        // -----------------------------------------------------

        Document origen =
                envio.get(
                        "origen",
                        Document.class
                );

        if (origen != null) {

            String ciudad =
                    origen.getString(
                            "ciudad"
                    );

            String pais =
                    origen.getString(
                            "pais"
                    );

            view.getTxtCiudadOrigen()
                    .setText(
                            ciudad != null
                                    ? ciudad
                                    : ""
                    );

            view.getTxtPaisOrigen()
                    .setText(
                            pais != null
                                    ? pais
                                    : ""
                    );

        } else {

            view.getTxtCiudadOrigen()
                    .setText("");

            view.getTxtPaisOrigen()
                    .setText("");
        }


        // -----------------------------------------------------
        // DESTINO
        // -----------------------------------------------------

        Document destino =
                envio.get(
                        "destino",
                        Document.class
                );

        if (destino != null) {

            String ciudad =
                    destino.getString(
                            "ciudad"
                    );

            String pais =
                    destino.getString(
                            "pais"
                    );

            view.getTxtCiudadDestino()
                    .setText(
                            ciudad != null
                                    ? ciudad
                                    : ""
                    );

            view.getTxtPaisDestino()
                    .setText(
                            pais != null
                                    ? pais
                                    : ""
                    );

        } else {

            view.getTxtCiudadDestino()
                    .setText("");

            view.getTxtPaisDestino()
                    .setText("");
        }


        // -----------------------------------------------------
        // ESTADO
        // -----------------------------------------------------

        String estado =
                envio.getString(
                        "estado"
                );

        if (estado != null) {

            view.getCmbEstado()
                    .setSelectedItem(
                            estado
                    );
        }


        // -----------------------------------------------------
        // PRIORIDAD
        // -----------------------------------------------------

        String prioridad =
                envio.getString(
                        "prioridad"
                );

        if (prioridad != null) {

            view.getCmbPrioridad()
                    .setSelectedItem(
                            prioridad
                    );
        }
    }


    // =========================================================
    // SELECCIONAR CLIENTE POR ID
    // =========================================================

    private void seleccionarClientePorId(
            ObjectId clienteId
    ) {

        if (clienteId == null) {

            return;
        }

        String idBuscado =
                clienteId.toHexString();

        for (Map.Entry<String, String> entry
                : clientesPorNombre.entrySet()) {

            if (idBuscado.equals(
                    entry.getValue()
            )) {

                view.getCmbCliente()
                        .setSelectedItem(
                                entry.getKey()
                        );

                return;
            }
        }
    }


    // =========================================================
    // SELECCIONAR CONTENEDORES POR ID
    // =========================================================

    private void seleccionarContenedoresPorId(
            List<ObjectId> ids
    ) {

        view.getListaContenedores()
                .clearSelection();

        if (ids == null
                || ids.isEmpty()) {

            return;
        }

        List<Integer> indices =
                new ArrayList<>();

        ListModel<String> modelo =
                view.getListaContenedores()
                        .getModel();

        for (int i = 0;
             i < modelo.getSize();
             i++) {

            String codigo =
                    modelo.getElementAt(
                            i
                    );

            String idCodigo =
                    contenedoresPorCodigo.get(
                            codigo
                    );

            if (idCodigo == null) {

                continue;
            }

            for (ObjectId id : ids) {

                if (id != null
                        && idCodigo.equals(
                        id.toHexString()
                )) {

                    indices.add(
                            i
                    );

                    break;
                }
            }
        }

        int[] indicesArray =
                new int[
                        indices.size()
                        ];

        for (int i = 0;
             i < indices.size();
             i++) {

            indicesArray[i] =
                    indices.get(i);
        }

        view.getListaContenedores()
                .setSelectedIndices(
                        indicesArray
                );
    }


    // =========================================================
    // ELIMINAR ENVÍO SELECCIONADO
    // =========================================================

    private void eliminarEnvioSeleccionado() {

        int fila =
                view.getTablaEnvios()
                        .getSelectedRow();

        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un envío para eliminar.",
                    "Envíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (fila >= idsEnvios.size()) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el envío seleccionado.",
                    "Envíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Seguro que querés eliminar el envío seleccionado?",
                        "Eliminar envío",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta
                != JOptionPane.YES_OPTION) {

            return;
        }

        String envioId =
                idsEnvios.get(
                        fila
                );

        try {

            envioController.eliminarEnvio(
                    envioId
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Envío eliminado correctamente.",
                    "Envíos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (envioId.equals(
                    envioIdEnEdicion
            )) {

                envioIdEnEdicion =
                        null;

                view.limpiarFormulario();

                view.ocultarFormulario();
            }

            cargarEnvios();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el envío.",
                    e
            );
        }
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        if (envioIdEnEdicion == null) {

            agregarEnvio();

        } else {

            modificarEnvio();
        }
    }


    // =========================================================
    // AGREGAR ENVÍO
    // =========================================================

    private void agregarEnvio() {

        DatosFormulario datos =
                obtenerDatosFormulario();

        if (datos == null) {

            return;
        }

        try {

            envioController.crearEnvio(
                    datos.clienteId,
                    datos.contenedoresIds,
                    datos.ciudadOrigen,
                    datos.paisOrigen,
                    datos.ciudadDestino,
                    datos.paisDestino,
                    datos.prioridad
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Envío agregado correctamente.",
                    "Envíos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            envioIdEnEdicion =
                    null;

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
    // MODIFICAR ENVÍO
    // =========================================================

    private void modificarEnvio() {

        if (envioIdEnEdicion == null) {

            return;
        }

        DatosFormulario datos =
                obtenerDatosFormulario();

        if (datos == null) {

            return;
        }

        Object estadoSeleccionado =
                view.getCmbEstado()
                        .getSelectedItem();

        if (estadoSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un estado.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String estado =
                estadoSeleccionado.toString();

        try {

            envioController.modificarEnvio(
                    envioIdEnEdicion,
                    datos.clienteId,
                    datos.contenedoresIds,
                    datos.ciudadOrigen,
                    datos.paisOrigen,
                    datos.ciudadDestino,
                    datos.paisDestino,
                    estado,
                    datos.prioridad
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Envío modificado correctamente.",
                    "Envíos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            envioIdEnEdicion =
                    null;

            view.limpiarFormulario();

            view.ocultarFormulario();

            cargarEnvios();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el envío.",
                    e
            );
        }
    }


    // =========================================================
    // OBTENER DATOS DEL FORMULARIO
    // =========================================================

    private DatosFormulario obtenerDatosFormulario() {

        // -----------------------------------------------------
        // CLIENTE
        // -----------------------------------------------------

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

            return null;
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

            return null;
        }


        // -----------------------------------------------------
        // CONTENEDORES
        // -----------------------------------------------------

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

            return null;
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

            return null;
        }


        // -----------------------------------------------------
        // UBICACIONES
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // PRIORIDAD
        // -----------------------------------------------------

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

            return null;
        }

        String prioridad =
                prioridadSeleccionada.toString();


        return new DatosFormulario(
                clienteId,
                contenedoresIds,
                ciudadOrigen,
                paisOrigen,
                ciudadDestino,
                paisDestino,
                prioridad
        );
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
    // CANCELAR
    // =========================================================

    private void cancelar() {

        envioIdEnEdicion =
                null;

        view.limpiarFormulario();

        view.ocultarFormulario();
    }


    // =========================================================
    // DATOS DEL FORMULARIO
    // =========================================================

    private static class DatosFormulario {

        private final String clienteId;

        private final List<String> contenedoresIds;

        private final String ciudadOrigen;
        private final String paisOrigen;

        private final String ciudadDestino;
        private final String paisDestino;

        private final String prioridad;


        private DatosFormulario(
                String clienteId,
                List<String> contenedoresIds,
                String ciudadOrigen,
                String paisOrigen,
                String ciudadDestino,
                String paisDestino,
                String prioridad
        ) {

            this.clienteId =
                    clienteId;

            this.contenedoresIds =
                    contenedoresIds;

            this.ciudadOrigen =
                    ciudadOrigen;

            this.paisOrigen =
                    paisOrigen;

            this.ciudadDestino =
                    ciudadDestino;

            this.paisDestino =
                    paisDestino;

            this.prioridad =
                    prioridad;
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