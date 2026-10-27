
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.mongoDB.service.PagoFacturaService;
import org.example.pdf.factura.FacturaPDFController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FacturacionPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Argentina/Buenos_Aires");

    private final FacturacionPanel view;
    private final String usuarioId;

    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;

    private final FacturaPDFController facturaPDFController;
    private final PagoFacturaService pagoFacturaService;

    private final Map<String, String> clientesPorNombre =
            new HashMap<>();

    private final List<String> idsFacturas =
            new ArrayList<>();

    private String rolActual;
    private String clienteIdActual;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FacturacionPanelController(
            FacturacionPanel view,
            String usuarioId
    ) {
        this.view = view;
        this.usuarioId = usuarioId;

        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();

        this.facturaPDFController = new FacturaPDFController();
        this.pagoFacturaService = new PagoFacturaService();

        cargarUsuarioActual();
        configurarEstados();
        configurarEventos();
        configurarPermisos();
        cargarClientes();
        cargarFacturas();
    }

    // ==========================================
    // USUARIO Y PERMISOS
    // ==========================================

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

        Document rol = rolDAO.buscarPorId(
                rolId.toHexString()
        );

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
                    "Rol sin permisos definidos para Facturación: "
                            + rolActual
            );
        }

        ObjectId clienteId = usuario.getObjectId("cliente_id");

        clienteIdActual = clienteId == null
                ? null
                : clienteId.toHexString();

        if ("CLIENTE".equals(rolActual)
                && (clienteIdActual == null
                || clienteIdActual.isBlank())) {

            throw new SecurityException(
                    "Tu usuario no tiene un cliente asociado."
            );
        }
    }

    private boolean puedeGestionar() {
        return "ADMINISTRADOR".equals(rolActual)
                || "OPERADOR".equals(rolActual);
    }

    private void configurarPermisos() {

        boolean autorizado = puedeGestionar();

        view.getBtnMostrarFormulario().setVisible(autorizado);
        view.getBtnMostrarFormulario().setEnabled(autorizado);

        view.getBtnGuardar().setEnabled(autorizado);

        view.getBtnModificarFactura().setVisible(autorizado);
        view.getBtnPagarFactura().setVisible(autorizado);

        actualizarBotones();

        if (!autorizado) {
            view.ocultarFormulario();
        }
    }

    private void validarPermisoGestionar() {

        cargarUsuarioActual();

        if (!puedeGestionar()) {
            throw new SecurityException(
                    "No tenés permisos para gestionar facturas."
            );
        }
    }

    // ==========================================
    // ESTADO INICIAL
    // ==========================================

    private void configurarEstados() {

        JComboBox<String> combo = view.getCmbEstado();

        combo.removeAllItems();
        combo.addItem("PENDIENTE");
        combo.setSelectedItem("PENDIENTE");
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> actualizarTodo()
        );

        view.getBtnMostrarFormulario().addActionListener(
                e -> {
                    try {
                        validarPermisoGestionar();
                        cargarClientes();
                        configurarEstados();
                        view.mostrarFormulario();

                    } catch (Exception ex) {
                        mostrarError(
                                "No se pudo abrir el formulario.",
                                ex
                        );
                    }
                }
        );

        view.getBtnCancelar().addActionListener(
                e -> cancelar()
        );

        view.getBtnGuardar().addActionListener(
                e -> agregarFactura()
        );

        view.getTablaFacturas()
                .getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        actualizarBotones();
                    }
                });

        view.getBtnModificarFactura().addActionListener(
                e -> modificarFactura()
        );

        view.getBtnPagarFactura().addActionListener(
                e -> pagarFactura()
        );
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    private void actualizarTodo() {

        try {
            cargarUsuarioActual();
            configurarPermisos();
            cargarClientes();
            cargarFacturas();

        } catch (Exception e) {
            mostrarError(
                    "No se pudo actualizar la facturación.",
                    e
            );
        }
    }

    // ==========================================
    // CARGAR CLIENTES
    // ==========================================

    private void cargarClientes() {

        if ("CLIENTE".equals(rolActual)) {
            view.getCmbCliente().removeAllItems();
            clientesPorNombre.clear();
            return;
        }

        try {
            List<Document> clientes =
                    mongo.listarClientes();

            view.getCmbCliente().removeAllItems();
            clientesPorNombre.clear();

            for (Document cliente : clientes) {

                ObjectId id = cliente.getObjectId("_id");
                String razonSocial =
                        cliente.getString("razon_social");

                if (id != null && razonSocial != null) {

                    view.getCmbCliente().addItem(razonSocial);

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

    // ==========================================
    // CARGAR FACTURAS
    // ==========================================

    private void cargarFacturas() {

        try {
            List<Document> facturas;

            if ("CLIENTE".equals(rolActual)) {
                facturas = mongo.buscarFacturasPorCliente(
                        clienteIdActual
                );
            } else {
                facturas = mongo.listarFacturas();
            }

            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaFacturas().getModel();

            modelo.setRowCount(0);
            idsFacturas.clear();

            SimpleDateFormat formato = crearFormatoFecha();

            for (Document factura : facturas) {

                ObjectId clienteId =
                        factura.getObjectId("cliente_id");

                String nombreCliente =
                        obtenerNombreCliente(clienteId);

                Date fechaEmision =
                        factura.getDate("fecha_emision");

                Date fechaVencimiento =
                        factura.getDate("fecha_vencimiento");

                String emisionTexto = fechaEmision == null
                        ? ""
                        : formato.format(fechaEmision);

                String vencimientoTexto =
                        fechaVencimiento == null
                                ? "Sin cargar"
                                : formato.format(fechaVencimiento);

                String importeTexto =
                        obtenerImporteTexto(
                                factura.get("importe_total")
                        );

                ObjectId id = factura.getObjectId("_id");

                idsFacturas.add(
                        id == null ? "" : id.toHexString()
                );

                modelo.addRow(new Object[]{
                        nombreCliente,
                        emisionTexto,
                        vencimientoTexto,
                        importeTexto,
                        factura.getString("estado")
                });
            }

            view.getLblEstado().setText(
                    "Facturas registradas: " + facturas.size()
            );

            actualizarBotones();

        } catch (Exception e) {
            mostrarError(
                    "No se pudieron cargar las facturas.",
                    e
            );
        }
    }

    // ==========================================
    // NOMBRE DEL CLIENTE
    // ==========================================

    private String obtenerNombreCliente(ObjectId clienteId) {

        if (clienteId == null) {
            return "";
        }

        try {
            Document cliente = mongo.consultarClientePorId(
                    clienteId.toHexString()
            );

            if (cliente == null) {
                return clienteId.toHexString();
            }

            String razonSocial =
                    cliente.getString("razon_social");

            return razonSocial == null
                    ? clienteId.toHexString()
                    : razonSocial;

        } catch (Exception e) {
            return clienteId.toHexString();
        }
    }

    // ==========================================
    // CONVERTIR IMPORTE
    // ==========================================

    private BigDecimal convertirImporte(Object valor) {

        if (valor instanceof Decimal128 decimal) {
            return decimal.bigDecimalValue();
        }

        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }

        if (valor instanceof Number numero) {
            return new BigDecimal(numero.toString());
        }

        throw new IllegalArgumentException(
                "La factura tiene un importe inválido."
        );
    }

    private String obtenerImporteTexto(Object importe) {

        if (importe == null) {
            return "";
        }

        if (importe instanceof Decimal128 decimal) {
            return decimal.bigDecimalValue().toPlainString();
        }

        return importe.toString();
    }

    // ==========================================
    // FECHAS
    // ==========================================

    private SimpleDateFormat crearFormatoFecha() {

        SimpleDateFormat formato =
                new SimpleDateFormat("dd/MM/yyyy");

        formato.setLenient(false);

        formato.setTimeZone(
                TimeZone.getTimeZone(ZONA_HORARIA)
        );

        return formato;
    }

    private Date convertirFecha(
            String texto,
            String nombreCampo
    ) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "Ingresá la " + nombreCampo + "."
            );
        }

        try {
            return crearFormatoFecha().parse(texto.trim());

        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "La " + nombreCampo
                            + " debe tener el formato dd/MM/yyyy."
            );
        }
    }

    private void validarFechas(
            Date emision,
            Date vencimiento
    ) {

        LocalDate fechaEmision = emision.toInstant()
                .atZone(ZONA_HORARIA)
                .toLocalDate();

        LocalDate fechaVencimiento = vencimiento.toInstant()
                .atZone(ZONA_HORARIA)
                .toLocalDate();

        if (fechaVencimiento.isBefore(fechaEmision)) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento no puede ser "
                            + "anterior a la fecha de emisión."
            );
        }
    }

    // ==========================================
    // AGREGAR FACTURA
    // ==========================================

    private void agregarFactura() {

        try {
            validarPermisoGestionar();

            Object clienteSeleccionado =
                    view.getCmbCliente().getSelectedItem();

            if (clienteSeleccionado == null) {
                throw new IllegalArgumentException(
                        "Seleccioná un cliente."
                );
            }

            String clienteId = clientesPorNombre.get(
                    clienteSeleccionado.toString()
            );

            if (clienteId == null) {
                throw new IllegalArgumentException(
                        "No se pudo identificar al cliente."
                );
            }

            Date fechaEmision = convertirFecha(
                    view.getTxtFechaEmision().getText(),
                    "fecha de emisión"
            );

            Date fechaVencimiento = convertirFecha(
                    view.getTxtFechaVencimiento().getText(),
                    "fecha de vencimiento"
            );

            validarFechas(fechaEmision, fechaVencimiento);

            String importeTexto =
                    view.getTxtImporteTotal()
                            .getText()
                            .trim()
                            .replace(",", ".");

            BigDecimal importe;

            try {
                importe = new BigDecimal(importeTexto);

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Ingresá un importe válido."
                );
            }

            if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El importe debe ser mayor a cero."
                );
            }

            // Una factura nueva comienza sin pagar.
            mongo.crearFactura(
                    clienteId,
                    fechaEmision,
                    fechaVencimiento,
                    importe,
                    "PENDIENTE"
            );

            String estadoPDF = "PENDIENTE";

            LocalDate diaVencimiento =
                    fechaVencimiento.toInstant()
                            .atZone(ZONA_HORARIA)
                            .toLocalDate();

            if (diaVencimiento.isBefore(
                    LocalDate.now(ZONA_HORARIA))) {
                estadoPDF = "VENCIDA";
            }

            try {
                facturaPDFController.generarFactura(
                        clienteId,
                        fechaEmision,
                        fechaVencimiento,
                        importe,
                        estadoPDF
                );

            } catch (Exception errorPDF) {

                cargarFacturas();

                JOptionPane.showMessageDialog(
                        view,
                        "La factura se guardó en MongoDB, "
                                + "pero no se pudo generar el PDF.\n\n"
                                + errorPDF.getMessage(),
                        "Factura guardada",
                        JOptionPane.WARNING_MESSAGE
                );

                view.limpiarFormulario();
                configurarEstados();
                view.ocultarFormulario();
                return;
            }

            JOptionPane.showMessageDialog(
                    view,
                    "Factura agregada correctamente.",
                    "Facturación",
                    JOptionPane.INFORMATION_MESSAGE
            );

            view.limpiarFormulario();
            configurarEstados();
            view.ocultarFormulario();
            cargarFacturas();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo agregar la factura.",
                    e
            );
        }
    }

    // ==========================================
    // ACTUALIZAR BOTONES
    // ==========================================

    private void actualizarBotones() {

        boolean autorizado = puedeGestionar();

        JTable tabla = view.getTablaFacturas();

        int filaVista = tabla.getSelectedRow();

        int filaModelo = filaVista < 0
                ? -1
                : tabla.convertRowIndexToModel(filaVista);

        boolean seleccionValida =
                filaModelo >= 0
                        && filaModelo < idsFacturas.size()
                        && !idsFacturas.get(filaModelo).isBlank();

        view.getBtnModificarFactura().setEnabled(
                autorizado && seleccionValida
        );

        view.getBtnPagarFactura().setEnabled(
                autorizado && seleccionValida
        );
    }

    // ==========================================
    // PAGAR FACTURA COMPLETA
    // ==========================================

    private void pagarFactura() {

        try {
            validarPermisoGestionar();

            JTable tabla = view.getTablaFacturas();

            int filaVista = tabla.getSelectedRow();

            if (filaVista < 0) {
                throw new IllegalArgumentException(
                        "Seleccioná una factura."
                );
            }

            int filaModelo =
                    tabla.convertRowIndexToModel(filaVista);

            if (filaModelo < 0
                    || filaModelo >= idsFacturas.size()) {

                throw new IllegalArgumentException(
                        "No se pudo identificar la factura."
                );
            }

            String facturaId = idsFacturas.get(filaModelo);

            if (facturaId == null || facturaId.isBlank()) {
                throw new IllegalArgumentException(
                        "La factura no tiene un ID válido."
                );
            }

            Document factura =
                    mongo.buscarFacturaPorId(facturaId);

            if ("PAGADA".equalsIgnoreCase(
                    factura.getString("estado"))) {

                throw new IllegalArgumentException(
                        "Esta factura ya está pagada."
                );
            }

            // ======================================
            // IMPORTE FIJO DE LA FACTURA
            // ======================================

            BigDecimal importeTotal = convertirImporte(
                    factura.get("importe_total")
            );

            if (importeTotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El importe de la factura no es válido."
                );
            }

            // ======================================
            // SELECCIONAR MEDIO DE PAGO
            // ======================================

            JComboBox<String> cmbMedioPago =
                    new JComboBox<>(new String[]{
                            "TRANSFERENCIA",
                            "EFECTIVO",
                            "TARJETA"
                    });

            JPanel formulario = new JPanel(
                    new GridLayout(2, 2, 8, 8)
            );

            formulario.add(
                    new JLabel("Importe total a pagar:")
            );

            formulario.add(
                    new JLabel(
                            "$ " + importeTotal.toPlainString()
                    )
            );

            formulario.add(
                    new JLabel("Medio de pago:")
            );

            formulario.add(cmbMedioPago);

            int respuesta = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Confirmar pago completo",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta != JOptionPane.OK_OPTION) {
                return;
            }

            String medioPago =
                    (String) cmbMedioPago.getSelectedItem();

            // ======================================
            // REGISTRAR Y DESCONTAR
            // ======================================

            pagoFacturaService.registrarPago(
                    facturaId,
                    importeTotal,
                    medioPago
            );

            cargarFacturas();

            JOptionPane.showMessageDialog(
                    view,
                    "Factura pagada correctamente.\n"
                            + "Importe descontado: $"
                            + importeTotal.toPlainString(),
                    "Pago realizado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException
                 | IllegalStateException e) {

            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "No se pudo registrar el pago",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar el pago.",
                    e
            );
        }
    }

    // ==========================================
    // MODIFICAR FACTURA
    // ==========================================

    private void modificarFactura() {

        try {
            validarPermisoGestionar();

            JTable tabla = view.getTablaFacturas();

            int filaVista = tabla.getSelectedRow();

            if (filaVista < 0) {
                throw new IllegalArgumentException(
                        "Seleccioná una factura."
                );
            }

            int filaModelo =
                    tabla.convertRowIndexToModel(filaVista);

            if (filaModelo < 0
                    || filaModelo >= idsFacturas.size()) {

                throw new IllegalArgumentException(
                        "No se pudo identificar la factura."
                );
            }

            String facturaId = idsFacturas.get(filaModelo);

            if (facturaId.isBlank()) {
                throw new IllegalArgumentException(
                        "Factura sin ID."
                );
            }

            Document factura =
                    mongo.buscarFacturaPorId(facturaId);

            Date vencimientoActual =
                    factura.getDate("fecha_vencimiento");

            JTextField campoVencimiento =
                    new JTextField(
                            vencimientoActual == null
                                    ? ""
                                    : crearFormatoFecha().format(
                                            vencimientoActual
                                    ),
                            12
                    );

            JPanel formulario = new JPanel(
                    new GridLayout(1, 2, 8, 8)
            );

            formulario.add(
                    new JLabel("Vencimiento (dd/MM/yyyy):")
            );

            formulario.add(campoVencimiento);

            int respuesta = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Modificar factura",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (respuesta != JOptionPane.OK_OPTION) {
                return;
            }

            Date fechaVencimiento = convertirFecha(
                    campoVencimiento.getText(),
                    "fecha de vencimiento"
            );

            Date fechaEmision =
                    factura.getDate("fecha_emision");

            if (fechaEmision == null) {
                throw new IllegalArgumentException(
                        "La factura no tiene fecha de emisión."
                );
            }

            validarFechas(fechaEmision, fechaVencimiento);

            // El estado de pago no se modifica acá.
            // Se conserva si ya figura PAGADA.
            // En las demás facturas se recalcula
            // PENDIENTE o VENCIDA por la fecha.
            String estado =
                    "PAGADA".equalsIgnoreCase(
                            factura.getString("estado")
                    )
                            ? "PAGADA"
                            : "PENDIENTE";

            mongo.modificarVencimientoYEstadoFactura(
                    facturaId,
                    fechaVencimiento,
                    estado
            );

            cargarFacturas();

            JOptionPane.showMessageDialog(
                    view,
                    "Factura actualizada correctamente.",
                    "Facturación",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar la factura.",
                    e
            );
        }
    }

    // ==========================================
    // CANCELAR
    // ==========================================

    private void cancelar() {

        view.limpiarFormulario();
        configurarEstados();
        view.ocultarFormulario();
    }

    // ==========================================
    // MOSTRAR ERROR
    // ==========================================

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
