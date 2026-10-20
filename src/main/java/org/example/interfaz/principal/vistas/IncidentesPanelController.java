package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.EnvioController;
import org.example.mongoDB.controller.IncidenteController;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.mongoDB.dao.RolMongoDAO;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

public class IncidentesPanelController {

    private final IncidentesPanel vista;
    private final String usuarioId;
    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private String rolActual;
    private String clienteIdActual;
    private final Set<String> idsEnviosCliente = new HashSet<>();

    private final IncidenteController incidenteController;
    private final EnvioController envioController;

    private String incidenteIdEdicion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public IncidentesPanelController(
            IncidentesPanel vista,
            String usuarioId
    ) {

        this.vista =
                vista;

        this.usuarioId = usuarioId;
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();

        this.incidenteController =
                new IncidenteController();

        this.envioController =
                new EnvioController();

        this.incidenteIdEdicion =
                null;


        cargarUsuarioActual();

        configurarEventos();

        cargarEnvios();

        cargarIncidentes();

        vista.modoRegistrar();
        configurarPermisos();
    }


    // =========================================================
    // USUARIO AUTENTICADO Y PERMISOS
    // =========================================================

    private void cargarUsuarioActual() {

        if (usuarioId == null || !ObjectId.isValid(usuarioId)) {
            throw new SecurityException(
                    "No se pudo identificar al usuario autenticado."
            );
        }

        Document usuario = usuarioDAO.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new SecurityException("El usuario autenticado no existe.");
        }

        ObjectId rolId = usuario.getObjectId("rol_id");

        if (rolId == null) {
            throw new SecurityException("El usuario no tiene un rol asignado.");
        }

        Document rol = rolDAO.buscarPorId(rolId.toHexString());

        if (rol == null) {
            throw new SecurityException("No se encontró el rol del usuario.");
        }

        String descripcion = rol.getString("descripcion");
        rolActual = descripcion == null
                ? ""
                : descripcion.trim().toUpperCase(Locale.ROOT);

        if (!"ADMINISTRADOR".equals(rolActual)
                && !"OPERADOR".equals(rolActual)
                && !"CLIENTE".equals(rolActual)) {
            throw new SecurityException("Rol sin permisos definidos para Riesgos.");
        }

        ObjectId clienteId = usuario.getObjectId("cliente_id");
        clienteIdActual = clienteId == null ? null : clienteId.toHexString();

        if ("CLIENTE".equals(rolActual)
                && (clienteIdActual == null || clienteIdActual.isBlank())) {
            throw new SecurityException("Tu usuario no tiene un cliente asociado.");
        }
    }

    private void configurarPermisos() {

        boolean puedeEditar = !"CLIENTE".equals(rolActual);

        vista.getBtnEditar().setVisible(puedeEditar);
        vista.getBtnEditar().setEnabled(puedeEditar);
        boolean puedeEliminar = "ADMINISTRADOR".equals(rolActual);
        vista.getBtnEliminar().setVisible(puedeEliminar);
        vista.getBtnEliminar().setEnabled(puedeEliminar);
    }

    private void validarPermisoEdicion() {

        cargarUsuarioActual();

        if ("CLIENTE".equals(rolActual)) {
            throw new SecurityException(
                    "El rol CLIENTE solo puede registrar y consultar incidentes propios."
            );
        }
    }

    private void validarPermisoEliminar() {

        cargarUsuarioActual();

        if (!"ADMINISTRADOR".equals(rolActual)) {
            throw new SecurityException(
                    "Solo el ADMINISTRADOR puede eliminar incidentes."
            );
        }
    }

    private void validarEnvioPropio(String envioId) {

        cargarUsuarioActual();

        if (envioId == null || envioId.isBlank()) {
            throw new IllegalArgumentException("Seleccioná un envío.");
        }

        if ("CLIENTE".equals(rolActual)) {
            boolean pertenece = false;

            // Comprobar nuevamente el vínculo desde el origen de datos.
            for (Document envio : envioController.buscarPorCliente(clienteIdActual)) {
                Object id = envio.get("_id");
                if (id != null && envioId.equals(id.toString())) {
                    pertenece = true;
                    break;
                }
            }

            if (!pertenece) {
                throw new SecurityException(
                        "No tenés permisos para registrar incidentes en ese envío."
                );
            }
        }
    }

    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        // -----------------------------------------------------
        // REGISTRAR / GUARDAR CAMBIOS
        // -----------------------------------------------------

        vista.getBtnRegistrar()
                .addActionListener(
                        e -> guardar()
                );


        // -----------------------------------------------------
        // EDITAR
        // -----------------------------------------------------

        vista.getBtnEditar()
                .addActionListener(
                        e -> editarIncidenteSeleccionado()
                );


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        vista.getBtnEliminar()
                .addActionListener(
                        e -> eliminarIncidenteSeleccionado()
                );


        // -----------------------------------------------------
        // ACTUALIZAR
        // -----------------------------------------------------

        vista.getBtnActualizar()
                .addActionListener(
                        e -> actualizar()
                );
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        if (incidenteIdEdicion != null) {
            try {
                validarPermisoEdicion();
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
                return;
            }
        }

        if (incidenteIdEdicion == null) {

            registrarIncidente();

        } else {

            modificarIncidente();
        }
    }


    // =========================================================
    // REGISTRAR INCIDENTE
    // =========================================================

    private void registrarIncidente() {

        try {

            String envioId =
                    (String) vista
                            .getComboEnvio()
                            .getSelectedItem();

            validarEnvioPropio(envioId);


            String tipo =
                    vista
                            .getTxtTipo()
                            .getText();


            String severidad =
                    vista
                            .getTxtSeveridad()
                            .getText();


            String descripcion =
                    vista
                            .getTxtDescripcion()
                            .getText();


            incidenteController.crearIncidente(
                    envioId,
                    tipo,
                    severidad,
                    descripcion
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Incidente registrado correctamente.",
                    "Incidente registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cancelarEdicion();

            cargarIncidentes();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // EDITAR INCIDENTE SELECCIONADO
    // =========================================================

    private void editarIncidenteSeleccionado() {

        try {
            validarPermisoEdicion();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
            return;
        }

        int fila =
                vista.getTablaIncidentes()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un incidente para editar.",
                    "Editar incidente",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            String id =
                    vista.getModeloTabla()
                            .getValueAt(
                                    fila,
                                    0
                            )
                            .toString();


            Document incidente =
                    incidenteController.buscarPorId(
                            id
                    );


            Object envioIdValor = incidente.get("envio_id");
            String envioId = envioIdValor == null
                    ? null
                    : envioIdValor.toString();


            if (envioId != null) {

                vista.getComboEnvio()
                        .setSelectedItem(
                                envioId
                        );
            }


            vista.getTxtTipo()
                    .setText(
                            incidente.getString(
                                    "tipo"
                            )
                    );


            vista.getTxtSeveridad()
                    .setText(
                            incidente.getString(
                                    "severidad"
                            )
                    );


            String estado =
                    incidente.getString(
                            "estado"
                    );


            if (estado != null) {

                vista.getComboEstado()
                        .setSelectedItem(
                                estado
                        );
            }


            vista.getTxtDescripcion()
                    .setText(
                            incidente.getString(
                                    "descripcion"
                            )
                    );


            incidenteIdEdicion =
                    id;


            vista.modoEditar();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // MODIFICAR INCIDENTE
    // =========================================================

    private void modificarIncidente() {

        try {

            validarPermisoEdicion();

            String envioId =
                    (String) vista
                            .getComboEnvio()
                            .getSelectedItem();


            String tipo =
                    vista
                            .getTxtTipo()
                            .getText();


            String severidad =
                    vista
                            .getTxtSeveridad()
                            .getText();


            String estado =
                    (String) vista
                            .getComboEstado()
                            .getSelectedItem();


            String descripcion =
                    vista
                            .getTxtDescripcion()
                            .getText();


            incidenteController.modificarIncidente(
                    incidenteIdEdicion,
                    envioId,
                    tipo,
                    severidad,
                    estado,
                    descripcion
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Incidente modificado correctamente.",
                    "Incidente modificado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cancelarEdicion();

            cargarIncidentes();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // ELIMINAR INCIDENTE SELECCIONADO
    // =========================================================

    private void eliminarIncidenteSeleccionado() {

        try {
            validarPermisoEliminar();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
            return;
        }

        int fila =
                vista.getTablaIncidentes()
                        .getSelectedRow();


        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un incidente para eliminar.",
                    "Eliminar incidente",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String id =
                vista.getModeloTabla()
                        .getValueAt(
                                fila,
                                0
                        )
                        .toString();


        int respuesta =
                JOptionPane.showConfirmDialog(
                        vista,
                        "¿Seguro que querés eliminar el incidente seleccionado?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }


        try {

            incidenteController.eliminarIncidente(
                    id
            );


            JOptionPane.showMessageDialog(
                    vista,
                    "Incidente eliminado correctamente.",
                    "Incidente eliminado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            if (id.equals(incidenteIdEdicion)) {

                cancelarEdicion();
            }


            cargarIncidentes();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // CANCELAR EDICIÓN
    // =========================================================

    private void cancelarEdicion() {

        incidenteIdEdicion =
                null;


        vista.limpiarFormulario();

        vista.modoRegistrar();

        vista.getTablaIncidentes()
                .clearSelection();
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizar() {

        try {

            cargarUsuarioActual();
            configurarPermisos();
            cancelarEdicion();

            cargarEnvios();

            cargarIncidentes();


        } catch (Exception ex) {

            mostrarError(
                    ex.getMessage()
            );
        }
    }


    // =========================================================
    // CARGAR ENVÍOS
    // =========================================================

    private void cargarEnvios() {

        vista.getComboEnvio()
                .removeAllItems();


        List<String> ids;

        idsEnviosCliente.clear();

        if ("CLIENTE".equals(rolActual)) {
            ids = new ArrayList<>();

            for (Document envio : envioController.buscarPorCliente(clienteIdActual)) {
                Object id = envio.get("_id");

                if (id != null) {
                    String idTexto = id.toString();
                    ids.add(idTexto);
                    idsEnviosCliente.add(idTexto);
                }
            }

        } else {
            ids = envioController.obtenerTodosLosIds();
        }


        for (String id : ids) {

            vista.getComboEnvio()
                    .addItem(
                            id
                    );
        }
    }


    // =========================================================
    // CARGAR INCIDENTES
    // =========================================================

    private void cargarIncidentes() {

        vista.getModeloTabla()
                .setRowCount(
                        0
                );


        List<Document> incidentes;

        if ("CLIENTE".equals(rolActual)) {
            incidentes = new ArrayList<>();

            for (String envioId : idsEnviosCliente) {
                incidentes.addAll(incidenteController.buscarPorEnvio(envioId));
            }

        } else {
            incidentes = incidenteController.listarIncidentes();
        }


        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm"
                );


        for (Document incidente : incidentes) {

            ObjectId id =
                    incidente.getObjectId(
                            "_id"
                    );


            Object envioIdValor = incidente.get("envio_id");
            String envioId = envioIdValor == null ? null : envioIdValor.toString();


            Date fecha =
                    incidente.getDate(
                            "fecha"
                    );


            String fechaFormateada =
                    "";

            if (fecha != null) {

                fechaFormateada =
                        formatoFecha.format(
                                fecha
                        );
            }


            vista.getModeloTabla()
                    .addRow(
                            new Object[]{
                                    id != null
                                            ? id.toHexString()
                                            : "",

                                    envioId != null
                                            ? envioId
                                            : "",

                                    fechaFormateada,

                                    incidente.getString(
                                            "tipo"
                                    ),

                                    incidente.getString(
                                            "severidad"
                                    ),

                                    incidente.getString(
                                            "estado"
                                    ),

                                    incidente.getString(
                                            "descripcion"
                                    )
                            }
                    );
        }
    }


    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                vista,
                mensaje != null
                        ? mensaje
                        : "Ocurrió un error.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
