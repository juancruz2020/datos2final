
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.FacturaController;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.pdf.factura.FacturaPDFController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

public class FacturacionPanelController {

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Argentina/Buenos_Aires");

    private final FacturacionPanel view;
    private final String usuarioId;
    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private final FacturaController facturaController;
    private final ClienteController clienteController;
    private final FacturaPDFController facturaPDFController;

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
        this.facturaController = new FacturaController();
        this.clienteController = new ClienteController();
        this.facturaPDFController = new FacturaPDFController();

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

    private void configurarPermisos() {

        boolean puedeModificar =
                "ADMINISTRADOR".equals(rolActual)
                        || "OPERADOR".equals(rolActual);

        view.getBtnMostrarFormulario().setVisible(puedeModificar);
        view.getBtnMostrarFormulario().setEnabled(puedeModificar);

        view.getBtnGuardar().setEnabled(puedeModificar);

        view.getBtnModificarFactura().setVisible(puedeModificar);

        actualizarBotonModificar();

        if (!puedeModificar) {
            view.ocultarFormulario();
        }
    }

    private void validarPermisoAgregar() {

        cargarUsuarioActual();

        if (!"ADMINISTRADOR".equals(rolActual)
                && !"OPERADOR".equals(rolActual)) {

            throw new SecurityException(
                    "No tenés permisos para modificar facturas."
            );
        }
    }

    // ==========================================
    // ESTADOS DEL FORMULARIO
    // ==========================================

    private void configurarEstados() {

        JComboBox<String> combo = view.getCmbEstado();

        combo.removeAllItems();
        combo.addItem("PENDIENTE");
        combo.addItem("PAGADA");

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
                        validarPermisoAgregar();
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
                        actualizarBotonModificar();
                    }
                });

        view.getBtnModificarFactura().addActionListener(
                e -> modificarFactura()
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
                    clienteController.listarClientes();

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

                facturas = facturaController.buscarPorCliente(
                        clienteIdActual
                );

            } else {
                facturas = facturaController.listarFacturas();
            }

            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaFacturas().getModel();

            modelo.setRowCount(0);
            idsFacturas.clear();

            SimpleDateFormat formato =
                    crearFormatoFecha();

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

            actualizarBotonModificar();

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
            Document cliente = clienteController.buscarPorId(
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
    // MOSTRAR IMPORTE
    // ==========================================

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
    // FORMATO DE FECHAS
    // ==========================================

    private SimpleDateFormat crearFormatoFecha() {

        SimpleDateFormat formato =
                new SimpleDateFormat("dd/MM/yyyy");

        formato.setLenient(false);
        formato.setTimeZone(
                java.util.TimeZone.getTimeZone(ZONA_HORARIA)
        );

        return formato;
    }

    private Date convertirFecha(String texto, String campo) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "Ingresá la " + campo + "."
            );
        }

        try {
            return crearFormatoFecha().parse(texto.trim());

        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "La " + campo
                            + " debe tener el formato dd/MM/yyyy."
            );
        }
    }

    private void validarFechas(Date emision, Date vencimiento) {

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
            validarPermisoAgregar();

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

            String textoImporte =
                    view.getTxtImporteTotal()
                            .getText()
                            .trim()
                            .replace(",", ".");

            BigDecimal importe;

            try {
                importe = new BigDecimal(textoImporte);

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

            String estado = (String)
                    view.getCmbEstado().getSelectedItem();

            if (estado == null) {
                throw new IllegalArgumentException(
                        "Seleccioná un estado."
                );
            }

            // Guardar en MongoDB.
            facturaController.crearFactura(
                    clienteId,
                    fechaEmision,
                    fechaVencimiento,
                    importe,
                    estado
            );

            // Obtener el estado real calculado.
            String estadoFinal = calcularEstadoParaPDF(
                    estado,
                    fechaVencimiento
            );

            // Generar el PDF incluyendo el vencimiento.
            // Se mantiene separado del guardado en MongoDB.
            try {
                facturaPDFController.generarFactura(
                        clienteId,
                        fechaEmision,
                        fechaVencimiento,
                        importe,
                        estadoFinal
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
    // ESTADO QUE FIGURA EN EL PDF
    // ==========================================

    private String calcularEstadoParaPDF(
            String estadoManual,
            Date vencimiento
    ) {

        if ("PAGADA".equalsIgnoreCase(estadoManual)) {
            return "PAGADA";
        }

        LocalDate fechaVencimiento = vencimiento.toInstant()
                .atZone(ZONA_HORARIA)
                .toLocalDate();

        if (fechaVencimiento.isBefore(
                LocalDate.now(ZONA_HORARIA)
        )) {
            return "VENCIDA";
        }

        return "PENDIENTE";
    }

    // ==========================================
    // BOTÓN MODIFICAR
    // ==========================================

    private void actualizarBotonModificar() {

        boolean autorizado =
                "ADMINISTRADOR".equals(rolActual)
                        || "OPERADOR".equals(rolActual);

        JTable tabla = view.getTablaFacturas();

        int filaVista = tabla.getSelectedRow();

        view.getBtnModificarFactura().setEnabled(
                autorizado
                        && filaVista >= 0
                        && tabla.convertRowIndexToModel(filaVista)
                        < idsFacturas.size()
        );
    }

    // ==========================================
    // MODIFICAR FACTURA
    // ==========================================

    private void modificarFactura() {

        try {
            validarPermisoAgregar();

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
                        "La factura no tiene un ID válido."
                );
            }

            Document factura =
                    facturaController.buscarPorId(facturaId);

            Date fechaVencimientoActual =
                    factura.getDate("fecha_vencimiento");

            String estadoActual =
                    factura.getString("estado");

            JTextField campoVencimiento =
                    new JTextField(
                            fechaVencimientoActual == null
                                    ? ""
                                    : crearFormatoFecha().format(
                                            fechaVencimientoActual
                                    ),
                            12
                    );

            JComboBox<String> comboEstado =
                    new JComboBox<>(new String[]{
                            "PENDIENTE",
                            "PAGADA"
                    });

            // Si estaba VENCIDA, se muestra PENDIENTE
            // como opción manual. El servicio determinará
            // nuevamente si corresponde VENCIDA.
            comboEstado.setSelectedItem(
                    "PAGADA".equalsIgnoreCase(estadoActual)
                            ? "PAGADA"
                            : "PENDIENTE"
            );

            JPanel formulario = new JPanel(
                    new java.awt.GridLayout(2, 2, 8, 8)
            );

            formulario.add(
                    new JLabel("Vencimiento (dd/MM/yyyy):")
            );
            formulario.add(campoVencimiento);

            formulario.add(new JLabel("Estado:"));
            formulario.add(comboEstado);

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

            String estado = (String)
                    comboEstado.getSelectedItem();

            facturaController.modificarVencimientoYEstado(
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
    // ERRORES
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
