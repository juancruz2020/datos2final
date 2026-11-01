
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.mongoDB.controller.ControllerMongoDB;
import org.example.mongoDB.model.FacturaProveedor;
import org.example.mongoDB.service.FacturaProveedorService;
import org.example.mongoDB.service.PagoProveedorService;
import org.example.mongoDB.service.PermisosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PagosPanelController {

    private final PagosPanel view;
    private final String usuarioId;

    private final ControllerMongoDB mongo;
    private final FacturaProveedorService facturaService;
    private final PagoProveedorService pagoService;
    private final PermisosService permisosService;

    private final List<String> facturasIds = new ArrayList<>();

    // Guarda si el proveedor de cada factura está inactivo.
    private final List<Boolean> proveedoresInactivos =
            new ArrayList<>();

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public PagosPanelController(
            PagosPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        mongo = new ControllerMongoDB();
        facturaService = new FacturaProveedorService();
        pagoService = new PagoProveedorService();
        permisosService = new PermisosService();

        if (!permisosService.puedeAccederVista(usuarioId, "PAGOS")) {
            throw new SecurityException(
                    "No tenés permisos para acceder a Pagos."
            );
        }

        boolean admin = permisosService.esAdmin(usuarioId);

        view.getBtnRegistrarFactura().setVisible(admin);
        view.getBtnRegistrarPago().setVisible(admin);

        configurarColoresTabla();
        configurarEventos();
        cargarFacturas();
    }

    // =========================================
    // CONFIGURAR COLORES DE LA TABLA
    // =========================================

    private void configurarColoresTabla() {

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tabla,
                            Object valor,
                            boolean seleccionado,
                            boolean tieneFoco,
                            int fila,
                            int columna
                    ) {

                        Component componente =
                                super.getTableCellRendererComponent(
                                        tabla,
                                        valor,
                                        seleccionado,
                                        tieneFoco,
                                        fila,
                                        columna
                                );

                        int filaModelo =
                                tabla.convertRowIndexToModel(fila);

                        boolean inactivo =
                                filaModelo >= 0
                                && filaModelo < proveedoresInactivos.size()
                                && proveedoresInactivos.get(filaModelo);

                        if (seleccionado) {

                            componente.setBackground(
                                    tabla.getSelectionBackground()
                            );

                            componente.setForeground(
                                    tabla.getSelectionForeground()
                            );

                        } else if (inactivo) {

                            componente.setBackground(
                                    new Color(235, 235, 235)
                            );

                            componente.setForeground(
                                    new Color(130, 130, 130)
                            );

                        } else {

                            componente.setBackground(
                                    tabla.getBackground()
                            );

                            componente.setForeground(
                                    tabla.getForeground()
                            );
                        }

                        return componente;
                    }
                };

        view.getTablaFacturas().setDefaultRenderer(
                Object.class,
                renderer
        );
    }

    // =========================================
    // EVENTOS
    // =========================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> cargarFacturas()
        );

        view.getBtnRegistrarFactura().addActionListener(
                e -> registrarFactura()
        );

        view.getBtnRegistrarPago().addActionListener(
                e -> registrarPago()
        );

        view.getBtnVerPagos().addActionListener(
                e -> mostrarPagos()
        );
    }

    // =========================================
    // CARGAR FACTURAS
    // =========================================

    private void cargarFacturas() {

        DefaultTableModel modelo =
                (DefaultTableModel) view
                        .getTablaFacturas()
                        .getModel();

        modelo.setRowCount(0);
        facturasIds.clear();
        proveedoresInactivos.clear();

        try {

            List<Document> facturas =
                    facturaService.listarFacturas(usuarioId);

            for (Document factura : facturas) {

                ObjectId facturaId =
                        factura.getObjectId("_id");

                if (facturaId == null) {
                    continue;
                }

                // ======================================
                // VERIFICAR ESTADO DEL PROVEEDOR
                // ======================================

                ObjectId proveedorId =
                        factura.getObjectId("proveedor_id");

                boolean inactivo = false;

                if (proveedorId != null) {

                    Document proveedor =
                            mongo.buscarProveedorPorId(
                                    proveedorId.toHexString()
                            );

                    if (proveedor != null) {

                        inactivo =
                                "INACTIVO".equalsIgnoreCase(
                                        proveedor.getString("estado")
                                );
                    }
                }

                proveedoresInactivos.add(inactivo);

                // ======================================
                // CALCULAR SALDO PENDIENTE
                // ======================================

                BigDecimal saldoPendiente =
                        calcularSaldoPendiente(
                                factura,
                                facturaId.toHexString()
                        );

                // ======================================
                // AGREGAR FACTURA A LA TABLA
                // ======================================

                modelo.addRow(new Object[]{
                        factura.getString("numero_factura"),
                        obtenerNombreProveedor(
                                proveedorId
                        ),
                        factura.getString("concepto"),
                        formatearFecha(
                                factura.getDate("fecha_emision")
                        ),
                        formatearFecha(
                                factura.getDate("fecha_vencimiento")
                        ),
                        obtenerImporte(
                                factura.get("importe_total")
                        ),

                        // NUEVA COLUMNA
                        saldoPendiente
                                .setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                )
                                .toPlainString(),

                        factura.getString("estado")
                });

                facturasIds.add(
                        facturaId.toHexString()
                );
            }

            view.getLblEstado().setText(
                    "Facturas registradas: " + facturasIds.size()
            );

            view.getTablaFacturas().repaint();

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las facturas.",
                    e
            );
        }
    }

    // =========================================
    // NUEVO: CALCULAR SALDO PENDIENTE
    // =========================================

    private BigDecimal calcularSaldoPendiente(
            Document factura,
            String facturaId
    ) {

        BigDecimal importeTotal =
                convertirImporteMongo(
                        factura.get("importe_total")
                );

        // Consultamos todos los pagos realizados
        // para esta factura de proveedor.

        List<Document> pagos =
                pagoService.listarPorFactura(
                        usuarioId,
                        facturaId
                );

        BigDecimal totalPagado = BigDecimal.ZERO;

        for (Document pago : pagos) {

            BigDecimal importePago =
                    convertirImporteMongo(
                            pago.get("importe")
                    );

            totalPagado = totalPagado.add(importePago);
        }

        // Importe original menos pagos acumulados.

        BigDecimal saldoPendiente =
                importeTotal.subtract(totalPagado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalStateException(
                    "Los pagos registrados superan el importe "
                            + "total de la factura "
                            + factura.getString("numero_factura")
            );
        }

        return saldoPendiente;
    }

    // =========================================
    // NUEVO: CONVERTIR IMPORTE DE MONGODB
    // =========================================

    private BigDecimal convertirImporteMongo(Object valor) {

        if (valor instanceof Decimal128 decimal) {
            return decimal.bigDecimalValue();
        }

        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }

        if (valor instanceof Number numero) {
            return new BigDecimal(numero.toString());
        }

        throw new IllegalStateException(
                "Se encontró un importe inválido en MongoDB."
        );
    }

    // =========================================
    // REGISTRAR FACTURA RECIBIDA
    // =========================================

    private void registrarFactura() {

        try {

            permisosService.exigirAdmin(usuarioId);

            List<Document> proveedores =
                    mongo.listarProveedores();

            JComboBox<String> cmbProveedor =
                    new JComboBox<>();

            List<String> proveedoresIds =
                    new ArrayList<>();

            // ======================================
            // SOLO PROVEEDORES ACTIVOS
            // ======================================

            for (Document proveedor : proveedores) {

                String estadoProveedor =
                        proveedor.getString("estado");

                if (!"ACTIVO".equalsIgnoreCase(
                        estadoProveedor
                )) {
                    continue;
                }

                ObjectId id =
                        proveedor.getObjectId("_id");

                if (id == null) {
                    continue;
                }

                String razonSocial =
                        proveedor.getString("razon_social");

                if (razonSocial == null
                        || razonSocial.isBlank()) {
                    continue;
                }

                cmbProveedor.addItem(razonSocial);

                proveedoresIds.add(
                        id.toHexString()
                );
            }

            if (proveedoresIds.isEmpty()) {

                JOptionPane.showMessageDialog(
                        view,
                        "No hay proveedores activos disponibles.\n"
                                + "Registrá o reactivá un proveedor "
                                + "para generar una factura.",
                        "Pagos",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            JTextField txtConcepto =
                    new JTextField(20);

            JTextField txtImporte =
                    new JTextField(20);

            JSpinner spFechaEmision =
                    new JSpinner(
                            new SpinnerDateModel()
                    );

            JSpinner spFechaVencimiento =
                    new JSpinner(
                            new SpinnerDateModel()
                    );

            spFechaEmision.setEditor(
                    new JSpinner.DateEditor(
                            spFechaEmision,
                            "dd/MM/yyyy"
                    )
            );

            spFechaVencimiento.setEditor(
                    new JSpinner.DateEditor(
                            spFechaVencimiento,
                            "dd/MM/yyyy"
                    )
            );

            JPanel formulario = new JPanel(
                    new GridLayout(0, 2, 10, 10)
            );

            formulario.add(new JLabel("Proveedor:"));
            formulario.add(cmbProveedor);

            formulario.add(new JLabel("Concepto:"));
            formulario.add(txtConcepto);

            formulario.add(new JLabel("Fecha de emisión:"));
            formulario.add(spFechaEmision);

            formulario.add(new JLabel("Fecha de vencimiento:"));
            formulario.add(spFechaVencimiento);

            formulario.add(new JLabel("Importe total:"));
            formulario.add(txtImporte);

            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Registrar factura de proveedor",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            String concepto =
                    txtConcepto.getText().trim();

            BigDecimal importe =
                    convertirImporte(
                            txtImporte.getText()
                    );

            int indice =
                    cmbProveedor.getSelectedIndex();

            if (indice < 0) {

                throw new IllegalArgumentException(
                        "Seleccioná un proveedor."
                );
            }

            String proveedorId =
                    proveedoresIds.get(indice);

            Date emision =
                    (Date) spFechaEmision.getValue();

            Date vencimiento =
                    (Date) spFechaVencimiento.getValue();

            FacturaProveedor factura =
                    facturaService.registrarFactura(
                            usuarioId,
                            proveedorId,
                            concepto,
                            emision,
                            vencimiento,
                            importe
                    );

            JOptionPane.showMessageDialog(
                    view,
                    "Factura registrada correctamente.\n"
                            + "Número: "
                            + factura.getNumeroFactura()
                            + "\nSe generó la deuda en la cuenta "
                            + "corriente del proveedor.",
                    "Pagos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarFacturas();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar la factura.",
                    e
            );
        }
    }

    // =========================================
    // REGISTRAR PAGO
    // =========================================

    private void registrarPago() {

        try {

            permisosService.exigirAdmin(usuarioId);

            String facturaId =
                    obtenerFacturaSeleccionada();

            if (facturaId == null) {
                return;
            }

            Document factura =
                    buscarFacturaSeleccionada(facturaId);

            if (factura == null) {
                throw new IllegalStateException(
                        "No se encontró la factura."
                );
            }

            if ("PAGADA".equals(
                    factura.getString("estado")
            )) {

                throw new IllegalStateException(
                        "Esta factura ya está pagada."
                );
            }

            JTextField txtImporte =
                    new JTextField(20);

            // ======================================
            // ÚNICO MÉTODO DE PAGO: TRANSFERENCIA
            // ======================================

            JPanel formulario = new JPanel(
                    new GridLayout(0, 2, 10, 10)
            );

            formulario.add(
                    new JLabel("Factura:")
            );

            formulario.add(
                    new JLabel(
                            factura.getString("numero_factura")
                    )
            );

            formulario.add(
                    new JLabel("Importe a pagar:")
            );

            formulario.add(txtImporte);

            formulario.add(
                    new JLabel("Método de pago:")
            );

            formulario.add(
                    new JLabel("TRANSFERENCIA")
            );

            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Registrar pago a proveedor",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            BigDecimal importe =
                    convertirImporte(
                            txtImporte.getText()
                    );

            pagoService.registrarPago(
                    usuarioId,
                    facturaId,
                    importe,
                    "TRANSFERENCIA"
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Pago registrado correctamente.\n"
                            + "Se actualizó la deuda del proveedor.",
                    "Pagos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Recargar saldos y estados.

            cargarFacturas();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar el pago.",
                    e
            );
        }
    }

    // =========================================
    // MOSTRAR PAGOS DE LA FACTURA
    // =========================================

    private void mostrarPagos() {

        try {

            String facturaId =
                    obtenerFacturaSeleccionada();

            if (facturaId == null) {
                return;
            }

            List<Document> pagos =
                    pagoService.listarPorFactura(
                            usuarioId,
                            facturaId
                    );

            DefaultTableModel modelo =
                    new DefaultTableModel(
                            new Object[]{
                                    "Fecha",
                                    "Importe",
                                    "Método de pago"
                            },
                            0
                    ) {

                        @Override
                        public boolean isCellEditable(
                                int fila,
                                int columna
                        ) {
                            return false;
                        }
                    };

            for (Document pago : pagos) {

                modelo.addRow(new Object[]{
                        formatearFecha(
                                pago.getDate("fecha")
                        ),
                        obtenerImporte(
                                pago.get("importe")
                        ),
                        pago.getString("metodo_pago")
                });
            }

            JTable tabla =
                    new JTable(modelo);

            tabla.setRowHeight(28);

            JScrollPane scroll =
                    new JScrollPane(tabla);

            scroll.setPreferredSize(
                    new Dimension(650, 300)
            );

            JOptionPane.showMessageDialog(
                    view,
                    scroll,
                    "Pagos realizados",
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron consultar los pagos.",
                    e
            );
        }
    }

    // =========================================
    // OBTENER FACTURA SELECCIONADA
    // =========================================

    private String obtenerFacturaSeleccionada() {

        int filaVista =
                view.getTablaFacturas().getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una factura.",
                    "Pagos",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        int filaModelo =
                view.getTablaFacturas()
                        .convertRowIndexToModel(filaVista);

        if (filaModelo < 0
                || filaModelo >= facturasIds.size()) {
            return null;
        }

        return facturasIds.get(filaModelo);
    }

    // =========================================
    // BUSCAR FACTURA
    // =========================================

    private Document buscarFacturaSeleccionada(
            String facturaId
    ) {

        for (Document factura :
                facturaService.listarFacturas(usuarioId)) {

            ObjectId id =
                    factura.getObjectId("_id");

            if (id != null
                    && id.toHexString().equals(facturaId)) {

                return factura;
            }
        }

        return null;
    }

    // =========================================
    // NOMBRE DEL PROVEEDOR
    // =========================================

    private String obtenerNombreProveedor(
            ObjectId proveedorId
    ) {

        if (proveedorId == null) {
            return "";
        }

        Document proveedor =
                mongo.buscarProveedorPorId(
                        proveedorId.toHexString()
                );

        if (proveedor == null) {
            return proveedorId.toHexString();
        }

        String nombre =
                proveedor.getString("razon_social");

        return nombre == null || nombre.isBlank()
                ? proveedorId.toHexString()
                : nombre;
    }

    // =========================================
    // FORMATEAR Y VALIDAR
    // =========================================

    private BigDecimal convertirImporte(
            String texto
    ) {

        try {

            BigDecimal importe =
                    new BigDecimal(
                            texto.trim().replace(",", ".")
                    );

            if (importe.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "El importe debe ser mayor que cero."
                );
            }

            return importe;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Ingresá un importe válido."
            );
        }
    }

    private String obtenerImporte(Object valor) {

        if (valor == null) {
            return "0.00";
        }

        if (valor instanceof Decimal128) {
            return ((Decimal128) valor)
                    .bigDecimalValue()
                    .toPlainString();
        }

        return valor.toString();
    }

    private String formatearFecha(Date fecha) {

        if (fecha == null) {
            return "";
        }

        return new SimpleDateFormat(
                "dd/MM/yyyy"
        ).format(fecha);
    }

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
