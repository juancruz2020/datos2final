
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.EnvioController;
import org.example.mongoDB.controller.EventoLogisticoController;
import org.example.mongoDB.service.PermisosService;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventosLogisticosPanelController {

    private final EventosLogisticosPanel vista;

    private final EventoLogisticoController eventoController;
    private final EnvioController envioController;
    private final PermisosService permisosService;

    private final String usuarioId;
    private final String rolActual;
    private final String clienteIdActual;

    private final Set<String> idsEnviosPermitidos;

    private String eventoIdEdicion;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EventosLogisticosPanelController(
            EventosLogisticosPanel vista,
            String usuarioId
    ) {

        this.vista = vista;
        this.usuarioId = usuarioId;

        this.eventoController =
                new EventoLogisticoController();

        this.envioController =
                new EnvioController();

        this.permisosService =
                new PermisosService();

        this.idsEnviosPermitidos =
                new HashSet<>();

        this.eventoIdEdicion = null;

        this.rolActual =
                permisosService.obtenerRolUsuario(usuarioId);

        if (!esAdministrador()
                && !esOperador()
                && !esCliente()) {

            throw new SecurityException(
                    "No tenés permisos para acceder a Eventos Logísticos."
            );
        }

        if (esCliente()) {

            this.clienteIdActual =
                    permisosService.obtenerClienteId(usuarioId);

            if (clienteIdActual == null
                    || clienteIdActual.isBlank()) {

                throw new SecurityException(
                        "El usuario CLIENTE no tiene una empresa asociada."
                );
            }

        } else {

            this.clienteIdActual = null;
        }

        configurarEventos();
        configurarPermisos();

        cargarEnvios();
        cargarEventos();
    }

    // =========================================================
    // ROLES Y PERMISOS
    // =========================================================

    private boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(rolActual);
    }

    private boolean esOperador() {
        return "OPERADOR".equalsIgnoreCase(rolActual);
    }

    private boolean esCliente() {
        return "CLIENTE".equalsIgnoreCase(rolActual);
    }

    private boolean puedeModificar() {
        return esAdministrador() || esOperador();
    }

    private void exigirPermisoModificacion() {

        if (!puedeModificar()) {

            throw new SecurityException(
                    "Tu usuario solamente puede consultar eventos logísticos."
            );
        }
    }

    // =========================================================
    // CONFIGURAR PERMISOS
    // =========================================================

    private void configurarPermisos() {

        boolean permitido = puedeModificar();

        // Ocultar todo el formulario para CLIENTE.
        vista.mostrarFormulario(permitido);

        vista.getBtnRegistrar().setVisible(permitido);
        vista.getBtnEditar().setVisible(permitido);
        vista.getBtnEliminar().setVisible(permitido);

        vista.getBtnRegistrar().setEnabled(permitido);
        vista.getBtnEditar().setEnabled(permitido);
        vista.getBtnEliminar().setEnabled(permitido);

        if (!permitido) {

            vista.getComboEnvio().setEnabled(false);
            vista.getTxtTipoEvento().setEditable(false);
            vista.getTxtUbicacion().setEditable(false);
            vista.getTxtDescripcion().setEditable(false);
        }

        vista.revalidate();
        vista.repaint();
    }

    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        vista.getBtnRegistrar()
                .addActionListener(e -> guardar());

        vista.getBtnEditar()
                .addActionListener(e -> editarEventoSeleccionado());

        vista.getBtnEliminar()
                .addActionListener(e -> eliminarEventoSeleccionado());

        vista.getBtnActualizar()
                .addActionListener(e -> actualizar());
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        exigirPermisoModificacion();

        if (eventoIdEdicion == null) {

            registrarEvento();

        } else {

            modificarEvento();
        }
    }

    // =========================================================
    // REGISTRAR EVENTO
    // =========================================================

    private void registrarEvento() {

        exigirPermisoModificacion();

        try {

            String envioId =
                    (String) vista.getComboEnvio()
                            .getSelectedItem();

            String tipoEvento =
                    vista.getTxtTipoEvento().getText();

            String ubicacion =
                    vista.getTxtUbicacion().getText();

            String descripcion =
                    vista.getTxtDescripcion().getText();

            eventoController.crearEvento(
                    envioId,
                    tipoEvento,
                    ubicacion,
                    descripcion
            );

            JOptionPane.showMessageDialog(
                    vista,
                    "Evento logístico registrado correctamente.",
                    "Evento registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cancelarEdicion();
            cargarEventos();

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // EDITAR EVENTO SELECCIONADO
    // =========================================================

    private void editarEventoSeleccionado() {

        exigirPermisoModificacion();

        int fila = vista.getTablaEventos().getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un evento para editar.",
                    "Editar evento",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        fila = vista.getTablaEventos()
                .convertRowIndexToModel(fila);

        try {

            String id =
                    vista.getModeloTabla()
                            .getValueAt(fila, 0)
                            .toString();

            Document evento =
                    eventoController.buscarPorId(id);

            if (evento == null) {

                mostrarError("El evento seleccionado no existe.");
                return;
            }

            Object envioIdValor = evento.get("envio_id");

            String envioId = envioIdValor == null
                    ? null
                    : envioIdValor.toString();

            if (envioId != null) {

                vista.getComboEnvio()
                        .setSelectedItem(envioId);
            }

            vista.getTxtTipoEvento().setText(
                    evento.getString("tipo_evento")
            );

            vista.getTxtUbicacion().setText(
                    evento.getString("ubicacion")
            );

            vista.getTxtDescripcion().setText(
                    evento.getString("descripcion")
            );

            eventoIdEdicion = id;

            vista.modoEditar();

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // MODIFICAR EVENTO
    // =========================================================

    private void modificarEvento() {

        exigirPermisoModificacion();

        try {

            String envioId =
                    (String) vista.getComboEnvio()
                            .getSelectedItem();

            String tipoEvento =
                    vista.getTxtTipoEvento().getText();

            String ubicacion =
                    vista.getTxtUbicacion().getText();

            String descripcion =
                    vista.getTxtDescripcion().getText();

            eventoController.modificarEvento(
                    eventoIdEdicion,
                    envioId,
                    tipoEvento,
                    ubicacion,
                    descripcion
            );

            JOptionPane.showMessageDialog(
                    vista,
                    "Evento logístico modificado correctamente.",
                    "Evento modificado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cancelarEdicion();
            cargarEventos();

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // ELIMINAR EVENTO SELECCIONADO
    // =========================================================

    private void eliminarEventoSeleccionado() {

        exigirPermisoModificacion();

        int fila = vista.getTablaEventos().getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccioná un evento para eliminar.",
                    "Eliminar evento",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        fila = vista.getTablaEventos()
                .convertRowIndexToModel(fila);

        String id =
                vista.getModeloTabla()
                        .getValueAt(fila, 0)
                        .toString();

        int respuesta =
                JOptionPane.showConfirmDialog(
                        vista,
                        "¿Seguro que querés eliminar el evento seleccionado?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            eventoController.eliminarEvento(id);

            JOptionPane.showMessageDialog(
                    vista,
                    "Evento logístico eliminado correctamente.",
                    "Evento eliminado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (id.equals(eventoIdEdicion)) {
                cancelarEdicion();
            }

            cargarEventos();

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // CANCELAR EDICIÓN
    // =========================================================

    private void cancelarEdicion() {

        eventoIdEdicion = null;

        vista.limpiarFormulario();

        vista.modoRegistrar();

        vista.getTablaEventos().clearSelection();
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizar() {

        try {

            cancelarEdicion();

            cargarEnvios();
            cargarEventos();

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // CARGAR ENVÍOS
    // =========================================================

    private void cargarEnvios() {

        vista.getComboEnvio().removeAllItems();

        idsEnviosPermitidos.clear();

        if (esCliente()) {

            List<Document> envios =
                    envioController.buscarPorCliente(clienteIdActual);

            for (Document envio : envios) {

                Object valorId = envio.get("_id");

                if (valorId == null) {
                    continue;
                }

                String id = valorId.toString();

                idsEnviosPermitidos.add(id);
                vista.getComboEnvio().addItem(id);
            }

        } else {

            List<String> ids =
                    envioController.obtenerTodosLosIds();

            for (String id : ids) {

                idsEnviosPermitidos.add(id);
                vista.getComboEnvio().addItem(id);
            }
        }
    }

    // =========================================================
    // CARGAR EVENTOS
    // =========================================================

    private void cargarEventos() {

        vista.getModeloTabla().setRowCount(0);

        List<Document> eventos =
                eventoController.listarEventos();

        SimpleDateFormat formatoFecha =
                new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Document evento : eventos) {

            ObjectId id =
                    evento.getObjectId("_id");

            Object envioIdValor =
                    evento.get("envio_id");

            String envioId = envioIdValor == null
                    ? null
                    : envioIdValor.toString();

            // CLIENTE: solamente eventos de sus envíos.
            if (esCliente()) {

                if (envioId == null
                        || !idsEnviosPermitidos.contains(envioId)) {

                    continue;
                }
            }

            Date fechaHora =
                    evento.getDate("fecha_hora");

            String fechaFormateada = "";

            if (fechaHora != null) {

                fechaFormateada =
                        formatoFecha.format(fechaHora);
            }

            vista.getModeloTabla().addRow(
                    new Object[]{
                            id != null
                                    ? id.toHexString()
                                    : "",
                            envioId != null
                                    ? envioId
                                    : "",
                            fechaFormateada,
                            evento.getString("tipo_evento"),
                            evento.getString("ubicacion"),
                            evento.getString("descripcion")
                    }
            );
        }
    }

    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(String mensaje) {

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
