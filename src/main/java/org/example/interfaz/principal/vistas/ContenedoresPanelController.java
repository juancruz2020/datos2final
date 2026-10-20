
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ContenedorController;
import org.example.mongoDB.service.PermisosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ContenedoresPanelController {

    private final ContenedoresPanel view;
    private final ContenedorController controller;
    private final PermisosService permisosService;
    private final String usuarioId;

    private List<Document> contenedoresActuales;

    private String contenedorIdEditando;
    private boolean modoEdicion;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ContenedoresPanelController(
            ContenedoresPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        this.controller = new ContenedorController();
        this.permisosService = new PermisosService();

        this.contenedoresActuales = new ArrayList<>();
        this.contenedorIdEditando = null;
        this.modoEdicion = false;

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "CONTENEDORES"
        )) {
            throw new SecurityException(
                    "No tiene permiso para acceder a Contenedores."
            );
        }

        configurarPermisos();
        configurarEventos();
        cargarContenedores();
    }

    // =========================================================
    // CONFIGURAR PERMISOS
    // =========================================================

    private void configurarPermisos() {

        boolean admin = permisosService.esAdmin(usuarioId);

        // Administrador: puede agregar y eliminar.
        // Operador: solamente puede editar.

        view.getBtnMostrarFormulario().setVisible(admin);
        view.getBtnEliminar().setVisible(admin);

        view.getBtnEditar().setVisible(true);
        view.getBtnActualizar().setVisible(true);
    }

    private void exigirAdministrador() {

        permisosService.exigirAdmin(usuarioId);
    }

    private void exigirEdicionPermitida() {

        permisosService.exigirAdminUOperador(usuarioId);
    }

    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        // ACTUALIZAR
        view.getBtnActualizar().addActionListener(
                e -> cargarContenedores()
        );

        // AGREGAR
        view.getBtnMostrarFormulario().addActionListener(
                e -> prepararNuevoContenedor()
        );

        // EDITAR
        view.getBtnEditar().addActionListener(
                e -> prepararEdicion()
        );

        // ELIMINAR
        view.getBtnEliminar().addActionListener(
                e -> eliminarContenedor()
        );

        // CANCELAR
        view.getBtnCancelar().addActionListener(
                e -> cancelar()
        );

        // GUARDAR
        view.getBtnGuardar().addActionListener(
                e -> guardar()
        );
    }

    // =========================================================
    // CARGAR CONTENEDORES
    // =========================================================

    private void cargarContenedores() {

        try {

            List<Document> contenedores =
                    controller.listarContenedores();

            contenedoresActuales =
                    new ArrayList<>(contenedores);

            DefaultTableModel modelo =
                    (DefaultTableModel) view
                            .getTablaContenedores()
                            .getModel();

            modelo.setRowCount(0);

            for (Document contenedor : contenedores) {

                modelo.addRow(new Object[]{
                        contenedor.getString(
                                "codigo_internacional"
                        ),
                        contenedor.getString("tipo"),
                        contenedor.get("capacidad"),
                        contenedor.getString("estado")
                });
            }

            view.getLblEstado().setText(
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
    // PREPARAR NUEVO CONTENEDOR
    // =========================================================

    private void prepararNuevoContenedor() {

        // Solo Administrador.
        exigirAdministrador();

        modoEdicion = false;
        contenedorIdEditando = null;

        view.limpiarFormulario();
        view.modoAgregar();
        view.mostrarFormulario();
    }

    // =========================================================
    // OBTENER CONTENEDOR SELECCIONADO
    // =========================================================

    private Document obtenerContenedorSeleccionado() {

        JTable tabla = view.getTablaContenedores();

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return null;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(fila);

        if (filaModelo < 0
                || filaModelo >= contenedoresActuales.size()) {
            return null;
        }

        return contenedoresActuales.get(filaModelo);
    }

    // =========================================================
    // PREPARAR EDICIÓN
    // =========================================================

    private void prepararEdicion() {

        // Administrador y Operador.
        exigirEdicionPermitida();

        Document contenedor =
                obtenerContenedorSeleccionado();

        if (contenedor == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor de la tabla para editar.",
                    "Editar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        ObjectId id =
                contenedor.getObjectId("_id");

        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El contenedor seleccionado no tiene un ID válido.",
                    "Editar contenedor",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        contenedorIdEditando = id.toHexString();
        modoEdicion = true;

        // CARGAR DATOS EN EL FORMULARIO

        String codigo =
                contenedor.getString("codigo_internacional");

        String tipo =
                contenedor.getString("tipo");

        Object capacidad =
                contenedor.get("capacidad");

        String estado =
                contenedor.getString("estado");

        view.getTxtCodigoInternacional().setText(
                codigo != null ? codigo : ""
        );

        view.getTxtTipo().setText(
                tipo != null ? tipo : ""
        );

        view.getTxtCapacidad().setText(
                capacidad != null
                        ? capacidad.toString()
                        : ""
        );

        if (estado != null) {

            view.getComboEstado().setSelectedItem(
                    estado
            );
        }

        view.modoEditar();
        view.mostrarFormulario();
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        if (modoEdicion) {

            // Administrador y Operador pueden modificar.
            exigirEdicionPermitida();
            modificarContenedor();

        } else {

            // Solo Administrador puede crear.
            exigirAdministrador();
            agregarContenedor();
        }
    }

    // =========================================================
    // AGREGAR CONTENEDOR
    // =========================================================

    private void agregarContenedor() {

        exigirAdministrador();

        // El código se genera automáticamente en el DAO.
        String codigo = null;

        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();

        Double capacidad = obtenerCapacidad();

        if (capacidad == null) {
            return;
        }

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

            finalizarOperacion();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar el contenedor.",
                    e
            );
        }
    }

    // =========================================================
    // MODIFICAR CONTENEDOR
    // =========================================================

    private void modificarContenedor() {

        exigirEdicionPermitida();

        if (contenedorIdEditando == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No hay ningún contenedor seleccionado para modificar.",
                    "Editar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // El código es de solo lectura.
        String codigo =
                view.getTxtCodigoInternacional()
                        .getText()
                        .trim();

        String tipo =
                view.getTxtTipo()
                        .getText()
                        .trim();

        Double capacidad =
                obtenerCapacidad();

        if (capacidad == null) {
            return;
        }

        String estado =
                (String) view.getComboEstado()
                        .getSelectedItem();

        try {

            controller.modificarContenedor(
                    contenedorIdEditando,
                    codigo,
                    tipo,
                    capacidad,
                    estado
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Contenedor modificado correctamente.",
                    "Contenedores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            finalizarOperacion();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el contenedor.",
                    e
            );
        }
    }

    // =========================================================
    // ELIMINAR CONTENEDOR
    // =========================================================

    private void eliminarContenedor() {

        // Solo Administrador.
        exigirAdministrador();

        Document contenedor =
                obtenerContenedorSeleccionado();

        if (contenedor == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un contenedor de la tabla para eliminar.",
                    "Eliminar contenedor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        ObjectId id =
                contenedor.getObjectId("_id");

        if (id == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "El contenedor seleccionado no tiene un ID válido.",
                    "Eliminar contenedor",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        String codigo =
                contenedor.getString("codigo_internacional");

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Seguro que querés eliminar el contenedor"
                                + "\n"
                                + codigo
                                + "?",
                        "Eliminar contenedor",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            // Validar nuevamente antes de eliminar.
            exigirAdministrador();

            controller.eliminarContenedor(
                    id.toHexString()
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Contenedor eliminado correctamente.",
                    "Contenedores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cancelar();
            cargarContenedores();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el contenedor.",
                    e
            );
        }
    }

    // =========================================================
    // OBTENER CAPACIDAD
    // =========================================================

    private Double obtenerCapacidad() {

        String capacidadTexto =
                view.getTxtCapacidad()
                        .getText()
                        .trim();

        try {

            return Double.parseDouble(
                    capacidadTexto.replace(",", ".")
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "La capacidad debe ser un número válido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }
    }

    // =========================================================
    // FINALIZAR OPERACIÓN
    // =========================================================

    private void finalizarOperacion() {

        modoEdicion = false;
        contenedorIdEditando = null;

        view.limpiarFormulario();
        view.ocultarFormulario();

        cargarContenedores();
    }

    // =========================================================
    // CANCELAR
    // =========================================================

    private void cancelar() {

        modoEdicion = false;
        contenedorIdEditando = null;

        view.limpiarFormulario();
        view.ocultarFormulario();
    }

    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n"
                        + (e.getMessage() != null
                        ? e.getMessage()
                        : "Ocurrió un error."),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
