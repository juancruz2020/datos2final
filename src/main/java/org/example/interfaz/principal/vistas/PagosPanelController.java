
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

        configurarEventos();
        cargarFacturas();
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

        try {

            List<Document> facturas =
                    facturaService.listarFacturas(usuarioId);

            for (Document factura : facturas) {

                ObjectId facturaId =
                        factura.getObjectId("_id");

                if (facturaId == null) {
                    continue;
                }

                modelo.addRow(new Object[]{
                        factura.getString("numero_factura"),
                        obtenerNombreProveedor(
                                factura.getObjectId("proveedor_id")
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
                        factura.getString("estado")
                });

                facturasIds.add(facturaId.toHexString());
            }

            view.getLblEstado().setText(
                    "Facturas registradas: " + facturasIds.size()
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las facturas.",
                    e
            );
        }
    }

    // =========================================
    // REGISTRAR FACTURA RECIBIDA
    // =========================================

        private void registrarFactura() {

        try {

                permisosService.exigirAdmin(usuarioId);

                List<Document> proveedores = mongo.listarProveedores();

                if (proveedores.isEmpty()) {

                JOptionPane.showMessageDialog(
                        view,
                        "Primero debés registrar un proveedor.",
                        "Pagos",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
                }

                JComboBox<String> cmbProveedor = new JComboBox<>();
                List<String> proveedoresIds = new ArrayList<>();

                for (Document proveedor : proveedores) {

                ObjectId id = proveedor.getObjectId("_id");

                if (id == null) {
                        continue;
                }

                cmbProveedor.addItem(
                        proveedor.getString("razon_social")
                );

                proveedoresIds.add(id.toHexString());
                }

                if (proveedoresIds.isEmpty()) {
                throw new IllegalStateException(
                        "No hay proveedores válidos."
                );
                }

                JTextField txtConcepto = new JTextField(20);
                JTextField txtImporte = new JTextField(20);

                JSpinner spFechaEmision = new JSpinner(
                        new SpinnerDateModel()
                );

                JSpinner spFechaVencimiento = new JSpinner(
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

                String concepto = txtConcepto.getText().trim();

                BigDecimal importe = convertirImporte(
                        txtImporte.getText()
                );

                int indice = cmbProveedor.getSelectedIndex();

                if (indice < 0) {
                throw new IllegalArgumentException(
                        "Seleccioná un proveedor."
                );
                }

                String proveedorId = proveedoresIds.get(indice);

                Date emision = (Date) spFechaEmision.getValue();
                Date vencimiento = (Date) spFechaVencimiento.getValue();

                FacturaProveedor factura = facturaService.registrarFactura(
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
                                + "Número: " + factura.getNumeroFactura()
                                + "\nSe generó la deuda en la cuenta corriente del proveedor.",
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

            String facturaId = obtenerFacturaSeleccionada();

            if (facturaId == null) {
                return;
            }

            Document factura = buscarFacturaSeleccionada(facturaId);

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

            JTextField txtImporte = new JTextField(20);

            JComboBox<String> cmbMetodo = new JComboBox<>(
                    new String[]{
                            "TRANSFERENCIA",
                            "EFECTIVO",
                            "TARJETA",
                            "CHEQUE"
                    }
            );

            JPanel formulario = new JPanel(
                    new GridLayout(0, 2, 10, 10)
            );

            formulario.add(new JLabel("Factura:"));
            formulario.add(new JLabel(
                    factura.getString("numero_factura")
            ));

            formulario.add(new JLabel("Importe a pagar:"));
            formulario.add(txtImporte);

            formulario.add(new JLabel("Método de pago:"));
            formulario.add(cmbMetodo);

            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Registrar pago a proveedor",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            BigDecimal importe = convertirImporte(
                    txtImporte.getText()
            );

            pagoService.registrarPago(
                    usuarioId,
                    facturaId,
                    importe,
                    (String) cmbMetodo.getSelectedItem()
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Pago registrado correctamente.\n"
                            + "Se actualizó la deuda del proveedor.",
                    "Pagos",
                    JOptionPane.INFORMATION_MESSAGE
            );

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

            String facturaId = obtenerFacturaSeleccionada();

            if (facturaId == null) {
                return;
            }

            List<Document> pagos =
                    pagoService.listarPorFactura(
                            usuarioId,
                            facturaId
                    );

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{
                            "Fecha",
                            "Importe",
                            "Método de pago"
                    },
                    0
            ) {

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

            for (Document pago : pagos) {

                modelo.addRow(new Object[]{
                        formatearFecha(pago.getDate("fecha")),
                        obtenerImporte(pago.get("importe")),
                        pago.getString("metodo_pago")
                });
            }

            JTable tabla = new JTable(modelo);
            tabla.setRowHeight(28);

            JScrollPane scroll = new JScrollPane(tabla);

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

        int filaVista = view.getTablaFacturas().getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una factura.",
                    "Pagos",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        int filaModelo = view.getTablaFacturas()
                .convertRowIndexToModel(filaVista);

        if (filaModelo < 0 || filaModelo >= facturasIds.size()) {
            return null;
        }

        return facturasIds.get(filaModelo);
    }

    // =========================================
    // BUSCAR FACTURA
    // =========================================

    private Document buscarFacturaSeleccionada(String facturaId) {

        for (Document factura :
                facturaService.listarFacturas(usuarioId)) {

            ObjectId id = factura.getObjectId("_id");

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

    private String obtenerNombreProveedor(ObjectId proveedorId) {

        if (proveedorId == null) {
            return "";
        }

        Document proveedor = mongo.buscarProveedorPorId(
                proveedorId.toHexString()
        );

        if (proveedor == null) {
            return proveedorId.toHexString();
        }

        String nombre = proveedor.getString("razon_social");

        return nombre == null || nombre.isBlank()
                ? proveedorId.toHexString()
                : nombre;
    }

    // =========================================
    // FORMATEAR Y VALIDAR
    // =========================================

    private BigDecimal convertirImporte(String texto) {

        try {

            BigDecimal importe = new BigDecimal(
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

    private void mostrarError(String mensaje, Exception e) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
