
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.FacturaController;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import org.example.pdf.factura.FacturaPDFController;

public class FacturacionPanelController {

    private final FacturacionPanel view;

    private final String usuarioId;

    private final UsuarioMongoDAO usuarioDAO;

    private final RolMongoDAO rolDAO;

    private String rolActual;

    private String clienteIdActual;

    private final FacturaController facturaController;

    private final ClienteController clienteController;

    private final Map<String, String> clientesPorNombre;

    private final FacturaPDFController facturaPDFController;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public FacturacionPanelController(
            FacturacionPanel view,
            String usuarioId
    ) {

        this.view =
                view;

        this.usuarioId = usuarioId;

        this.usuarioDAO = new UsuarioMongoDAO();

        this.rolDAO = new RolMongoDAO();

        this.facturaController =
                new FacturaController();

        this.clienteController =
                new ClienteController();

        this.clientesPorNombre =
                new HashMap<>();

        this.facturaPDFController =
                new FacturaPDFController();


        cargarUsuarioActual();

        configurarEventos();

        configurarPermisos();

        cargarClientes();

        cargarFacturas();
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
            throw new SecurityException(
                    "El usuario autenticado no existe."
            );
        }

        ObjectId rolId = usuario.getObjectId("rol_id");

        if (rolId == null) {
            throw new SecurityException(
                    "El usuario no tiene un rol asignado."
            );
        }

        Document rol = rolDAO.buscarPorId(rolId.toHexString());

        if (rol == null) {
            throw new SecurityException(
                    "No se encontró el rol del usuario."
            );
        }

        String descripcion = rol.getString("descripcion");

        rolActual = descripcion == null
                ? ""
                : descripcion.trim().toUpperCase(Locale.ROOT);

        if (!"ADMINISTRADOR".equals(rolActual)
                && !"OPERADOR".equals(rolActual)
                && !"CLIENTE".equals(rolActual)) {
            throw new SecurityException(
                    "Rol sin permisos definidos para Facturación: " + rolActual
            );
        }

        ObjectId clienteId = usuario.getObjectId("cliente_id");

        clienteIdActual = clienteId == null
                ? null
                : clienteId.toHexString();

        if ("CLIENTE".equals(rolActual)
                && (clienteIdActual == null || clienteIdActual.isBlank())) {
            throw new SecurityException(
                    "Tu usuario no tiene un cliente asociado."
            );
        }
    }

    private void configurarPermisos() {

        boolean puedeAgregar = !"CLIENTE".equals(rolActual);

        view.getBtnMostrarFormulario().setVisible(puedeAgregar);
        view.getBtnMostrarFormulario().setEnabled(puedeAgregar);
        view.getBtnGuardar().setEnabled(puedeAgregar);

        if (!puedeAgregar) {
            view.ocultarFormulario();
        }
    }

    private void validarPermisoAgregar() {

        cargarUsuarioActual();

        if ("CLIENTE".equals(rolActual)) {
            throw new SecurityException(
                    "El rol CLIENTE solo puede consultar sus propias facturas."
            );
        }
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
                            try {
                                validarPermisoAgregar();
                                cargarClientes();
                                view.mostrarFormulario();
                            } catch (Exception ex) {
                                mostrarError(
                                        "No tenés permisos para agregar facturas.",
                                        ex
                                );
                            }
                        }
                );


        view.getBtnCancelar()
                .addActionListener(
                        e -> cancelar()
                );


        view.getBtnGuardar()
                .addActionListener(
                        e -> agregarFactura()
                );
    }


    // =========================================================
    // ACTUALIZAR TODO
    // =========================================================

    private void actualizarTodo() {

        try {
            cargarUsuarioActual();
            configurarPermisos();
        } catch (Exception e) {
            view.getBtnMostrarFormulario().setEnabled(false);
            view.getBtnGuardar().setEnabled(false);
            mostrarError("No se pudo validar la sesión.", e);
            return;
        }

        cargarClientes();

        cargarFacturas();
    }


    // =========================================================
    // CARGAR CLIENTES
    // =========================================================

    private void cargarClientes() {

        if ("CLIENTE".equals(rolActual)) {
            view.getCmbCliente().removeAllItems();
            clientesPorNombre.clear();
            return;
        }

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


                if (
                        id != null
                                && razonSocial != null
                ) {

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
    // CARGAR FACTURAS
    // =========================================================

    private void cargarFacturas() {

        try {

            List<Document> facturas;

            if ("CLIENTE".equals(rolActual)) {
                facturas = facturaController.buscarPorCliente(
                        clienteIdActual
                );
            } else {
                facturas = facturaController.listarFacturas();
            }


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaFacturas()
                                    .getModel();


            modelo.setRowCount(
                    0
            );


            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat(
                            "dd/MM/yyyy"
                    );


            for (Document factura : facturas) {

                // -------------------------------------------------
                // CLIENTE
                // -------------------------------------------------

                ObjectId clienteId =
                        factura.getObjectId(
                                "cliente_id"
                        );


                String cliente =
                        obtenerNombreCliente(
                                clienteId
                        );


                // -------------------------------------------------
                // FECHA
                // -------------------------------------------------

                Date fecha =
                        factura.getDate(
                                "fecha_emision"
                        );


                String fechaTexto =
                        fecha != null
                                ? formatoFecha.format(fecha)
                                : "";


                // -------------------------------------------------
                // IMPORTE
                // -------------------------------------------------

                Object importeMongo =
                        factura.get(
                                "importe_total"
                        );


                String importeTexto =
                        obtenerImporteTexto(
                                importeMongo
                        );


                // -------------------------------------------------
                // TABLA
                // -------------------------------------------------

                modelo.addRow(
                        new Object[]{
                                cliente,
                                fechaTexto,
                                importeTexto,
                                factura.getString(
                                        "estado"
                                )
                        }
                );
            }


            view.getLblEstado()
                    .setText(
                            "Facturas registradas: "
                                    + facturas.size()
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las facturas.",
                    e
            );
        }
    }


    // =========================================================
    // OBTENER NOMBRE DEL CLIENTE
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
                                    clienteId
                                            .toHexString()
                            );


            if (cliente == null) {

                return clienteId
                        .toHexString();
            }


            String razonSocial =
                    cliente.getString(
                            "razon_social"
                    );


            return razonSocial != null
                    ? razonSocial
                    : clienteId.toHexString();


        } catch (Exception e) {

            return clienteId
                    .toHexString();
        }
    }


    // =========================================================
    // OBTENER IMPORTE
    // =========================================================

    private String obtenerImporteTexto(
            Object importe
    ) {

        if (importe == null) {

            return "";
        }


        if (importe instanceof Decimal128) {

            Decimal128 decimal =
                    (Decimal128) importe;


            return decimal
                    .bigDecimalValue()
                    .toPlainString();
        }


        if (importe instanceof Number) {

            return importe.toString();
        }


        return importe.toString();
    }


    // =========================================================
    // AGREGAR FACTURA
    // =========================================================

    private void agregarFactura() {

        try {
            validarPermisoAgregar();
        } catch (Exception e) {
            mostrarError("No tenés permisos para agregar facturas.", e);
            return;
        }

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


        String nombreCliente =
                clienteSeleccionado
                        .toString();


        String clienteId =
                clientesPorNombre.get(
                        nombreCliente
                );


        if (clienteId == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo obtener el ID del cliente seleccionado.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // FECHA
        // =====================================================

        String fechaTexto =
                view.getTxtFechaEmision()
                        .getText()
                        .trim();


        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                );

        formatoFecha.setLenient(
                false
        );


        Date fechaEmision;


        try {

            fechaEmision =
                    formatoFecha.parse(
                            fechaTexto
                    );


        } catch (ParseException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "La fecha debe tener el formato dd/MM/yyyy.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // IMPORTE
        // =====================================================

        String importeTexto =
                view.getTxtImporteTotal()
                        .getText()
                        .trim()
                        .replace(",", ".");


        BigDecimal importeTotal;


        try {

            importeTotal =
                    new BigDecimal(
                            importeTexto
                    );


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    view,
                    "El importe debe ser un número válido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // ESTADO
        // =====================================================

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
                estadoSeleccionado
                        .toString();


// =====================================================
// GUARDAR
// =====================================================

        try {

            facturaController.crearFactura(
                    clienteId,
                    fechaEmision,
                    importeTotal,
                    estado
            );

            // Generar y abrir el PDF
            facturaPDFController.generarFactura(
                    clienteId,
                    fechaEmision,
                    importeTotal,
                    estado
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Factura agregada correctamente.",
                    "Facturación",
                    JOptionPane.INFORMATION_MESSAGE
            );

            view.limpiarFormulario();

            view.ocultarFormulario();

            cargarFacturas();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar la factura.",
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
