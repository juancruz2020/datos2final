
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.example.mongoDB.service.PermisosService;
import org.example.mongoDB.service.ProveedorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresPanelController {

    private final ProveedoresPanel view;
    private final String usuarioId;

    private final ProveedorService proveedorService;
    private final PermisosService permisosService;

    private final List<String> proveedoresIds = new ArrayList<>();

    private String proveedorEnEdicion;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public ProveedoresPanelController(
            ProveedoresPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        proveedorService = new ProveedorService();
        permisosService = new PermisosService();

        permisosService.exigirAdmin(usuarioId);

        configurarEventos();
        cargarProveedores();
    }

    // =========================================
    // EVENTOS
    // =========================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> cargarProveedores()
        );

        view.getBtnCancelar().addActionListener(
                e -> cancelarFormulario()
        );

        view.getBtnAgregar().addActionListener(
                e -> guardarCambios()
        );

        view.getBtnEditar().addActionListener(
                e -> editarProveedor()
        );

        view.getBtnEliminar().addActionListener(
                e -> eliminarProveedor()
        );
    }

    // =========================================
    // CARGAR PROVEEDORES
    // =========================================

    private void cargarProveedores() {

        try {

            permisosService.exigirAdmin(usuarioId);

            DefaultTableModel modelo =
                    (DefaultTableModel) view
                            .getTablaProveedores()
                            .getModel();

            modelo.setRowCount(0);
            proveedoresIds.clear();

            List<Document> proveedores =
                    proveedorService.listarProveedores();

            for (Document proveedor : proveedores) {

                ObjectId id = proveedor.getObjectId("_id");

                if (id == null) {
                    continue;
                }

                Document direccion =
                        proveedor.get("direccion", Document.class);

                modelo.addRow(new Object[]{
                        id.toHexString(),
                        proveedor.getString("razon_social"),
                        proveedor.getString("cuit"),
                        proveedor.getString("email"),
                        proveedor.getString("telefono"),
                        obtenerCampo(direccion, "ciudad"),
                        proveedor.getString("pais"),
                        proveedor.getString("estado")
                });

                proveedoresIds.add(id.toHexString());
            }

            view.getLblEstado().setText(
                    "Proveedores registrados: "
                            + proveedoresIds.size()
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los proveedores.",
                    e
            );
        }
    }

    // =========================================
    // GUARDAR CAMBIOS DEL PROVEEDOR
    // =========================================

    private void guardarCambios() {

        try {

            permisosService.exigirAdmin(usuarioId);

            // En este panel solamente se pueden
            // modificar proveedores existentes.

            if (proveedorEnEdicion == null) {
                throw new IllegalStateException(
                        "Primero seleccioná un proveedor para editar."
                );
            }

            String razonSocial =
                    view.getTxtRazonSocial().getText().trim();

            String cuit =
                    view.getTxtCuit().getText().trim();

            String email =
                    view.getTxtEmail().getText().trim();

            String telefono =
                    view.getTxtTelefono().getText().trim();

            String calle =
                    view.getTxtCalle().getText().trim();

            String numero =
                    view.getTxtNumero().getText().trim();

            String ciudad =
                    view.getTxtCiudad().getText().trim();

            String codigoPostal =
                    view.getTxtCodigoPostal().getText().trim();

            String pais =
                    view.getTxtPais().getText().trim();

            Document actual =
                    proveedorService.buscarPorId(proveedorEnEdicion);

            String estado = actual.getString("estado");

            if (estado == null || estado.isBlank()) {
                estado = "ACTIVO";
            }

            proveedorService.modificarProveedor(
                    proveedorEnEdicion,
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
                    "Proveedor modificado correctamente.",
                    "Proveedores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cancelarFormulario();
            cargarProveedores();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el proveedor.",
                    e
            );
        }
    }

    // =========================================
    // EDITAR PROVEEDOR
    // =========================================

    private void editarProveedor() {

        try {

            permisosService.exigirAdmin(usuarioId);

            String proveedorId = obtenerProveedorSeleccionado();

            if (proveedorId == null) {
                return;
            }

            Document proveedor =
                    proveedorService.buscarPorId(proveedorId);

            Document direccion =
                    proveedor.get("direccion", Document.class);

            view.getTxtRazonSocial().setText(
                    obtenerCampo(proveedor, "razon_social")
            );

            view.getTxtCuit().setText(
                    obtenerCampo(proveedor, "cuit")
            );

            view.getTxtEmail().setText(
                    obtenerCampo(proveedor, "email")
            );

            view.getTxtTelefono().setText(
                    obtenerCampo(proveedor, "telefono")
            );

            view.getTxtCalle().setText(
                    obtenerCampo(direccion, "calle")
            );

            view.getTxtNumero().setText(
                    obtenerCampo(direccion, "numero")
            );

            view.getTxtCiudad().setText(
                    obtenerCampo(direccion, "ciudad")
            );

            view.getTxtCodigoPostal().setText(
                    obtenerCampo(direccion, "codigo_postal")
            );

            view.getTxtPais().setText(
                    obtenerCampo(proveedor, "pais")
            );

            proveedorEnEdicion = proveedorId;

            view.getBtnAgregar().setText("Guardar cambios");
            view.mostrarFormulario();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo cargar el proveedor.",
                    e
            );
        }
    }

    // =========================================
    // ELIMINAR PROVEEDOR (BAJA LÓGICA)
    // =========================================

    private void eliminarProveedor() {

        try {

            permisosService.exigirAdmin(usuarioId);

            String proveedorId = obtenerProveedorSeleccionado();

            if (proveedorId == null) {
                return;
            }

            Document proveedor =
                    proveedorService.buscarPorId(proveedorId);

            if ("INACTIVO".equalsIgnoreCase(
                    proveedor.getString("estado")
            )) {

                JOptionPane.showMessageDialog(
                        view,
                        "Este proveedor ya está inactivo.",
                        "Proveedores",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(
                    view,
                    "¿Querés eliminar a "
                            + proveedor.getString("razon_social")
                            + "?\n\n"
                            + "El proveedor quedará INACTIVO.\n"
                            + "Se conservarán sus facturas, pagos "
                            + "y cuenta corriente.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            Document direccion =
                    proveedor.get("direccion", Document.class);

            proveedorService.modificarProveedor(
                    proveedorId,
                    obtenerCampo(proveedor, "razon_social"),
                    obtenerCampo(proveedor, "cuit"),
                    obtenerCampo(proveedor, "email"),
                    obtenerCampo(proveedor, "telefono"),
                    obtenerCampo(direccion, "calle"),
                    obtenerCampo(direccion, "numero"),
                    obtenerCampo(direccion, "ciudad"),
                    obtenerCampo(direccion, "codigo_postal"),
                    obtenerCampo(proveedor, "pais"),
                    "INACTIVO"
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Proveedor eliminado correctamente.\n"
                            + "Su información histórica se conserva.",
                    "Proveedores",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cancelarFormulario();
            cargarProveedores();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el proveedor.",
                    e
            );
        }
    }

    // =========================================
    // OBTENER PROVEEDOR SELECCIONADO
    // =========================================

    private String obtenerProveedorSeleccionado() {

        int filaVista =
                view.getTablaProveedores().getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un proveedor.",
                    "Proveedores",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        int filaModelo = view.getTablaProveedores()
                .convertRowIndexToModel(filaVista);

        if (filaModelo < 0
                || filaModelo >= proveedoresIds.size()) {

            return null;
        }

        return proveedoresIds.get(filaModelo);
    }

    // =========================================
    // CANCELAR FORMULARIO
    // =========================================

    private void cancelarFormulario() {

        proveedorEnEdicion = null;

        view.limpiarFormulario();
        view.getBtnAgregar().setText("Guardar cambios");
        view.ocultarFormulario();
    }

    // =========================================
    // OBTENER VALOR DE DOCUMENT
    // =========================================

    private String obtenerCampo(
            Document documento,
            String campo
    ) {

        if (documento == null) {
            return "";
        }

        Object valor = documento.get(campo);

        return valor == null ? "" : valor.toString();
    }

    // =========================================
    // MOSTRAR ERROR
    // =========================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
