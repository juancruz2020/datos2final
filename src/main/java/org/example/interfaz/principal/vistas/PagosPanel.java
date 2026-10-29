
package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PagosPanel extends JPanel {

    private JTable tablaFacturas;

    private JButton btnActualizar;
    private JButton btnRegistrarFactura;
    private JButton btnRegistrarPago;
    private JButton btnVerPagos;

    private JLabel lblEstado;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public PagosPanel() {
        construir();
    }

    // =========================================
    // CONSTRUIR
    // =========================================

    private void construir() {

        setLayout(new BorderLayout(0, 20));
        setBackground(Colores.FONDO);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 20, 30
                )
        );

        // =========================================
        // TÍTULO
        // =========================================

        JLabel titulo = new JLabel("Pagos a proveedores");

        titulo.setFont(Fuentes.TITULO);
        titulo.setForeground(Colores.TEXTO);

        add(titulo, BorderLayout.NORTH);

        // =========================================
        // TABLA
        // =========================================

        add(
                construirPanelTabla(),
                BorderLayout.CENTER
        );

        // =========================================
        // ESTADO
        // =========================================

        lblEstado = new JLabel("Facturas registradas: 0");

        lblEstado.setFont(Fuentes.NORMAL);
        lblEstado.setForeground(Colores.TEXTO_SECUNDARIO);

        add(lblEstado, BorderLayout.SOUTH);
    }

    // =========================================
    // CONSTRUIR PANEL TABLA
    // =========================================

    private JPanel construirPanelTabla() {

        JPanel panel = new JPanel(
                new BorderLayout(0, 10)
        );

        panel.setBackground(Colores.SUPERFICIE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        // =========================================
        // CABECERA
        // =========================================

        JPanel cabecera = new JPanel(new BorderLayout());

        cabecera.setOpaque(false);

        JLabel titulo = new JLabel(
                "Facturas recibidas de proveedores"
        );

        titulo.setFont(Fuentes.LABEL);
        titulo.setForeground(Colores.TEXTO);

        btnActualizar = new JButton("Actualizar");

        cabecera.add(titulo, BorderLayout.WEST);
        cabecera.add(btnActualizar, BorderLayout.EAST);

        panel.add(cabecera, BorderLayout.NORTH);

        // =========================================
        // MODELO DE TABLA
        // =========================================

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                        "N° Factura",
                        "Proveedor",
                        "Concepto",
                        "Emisión",
                        "Vencimiento",
                        "Importe total",
                        "Estado"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        // =========================================
        // TABLA DE FACTURAS
        // =========================================

        tablaFacturas = new JTable(modelo);

        tablaFacturas.setRowHeight(30);
        tablaFacturas.setFont(Fuentes.NORMAL);

        tablaFacturas.getTableHeader().setFont(
                Fuentes.LABEL
        );

        tablaFacturas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaFacturas.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tablaFacturas);

        scroll.setPreferredSize(
                new Dimension(900, 350)
        );

        panel.add(scroll, BorderLayout.CENTER);

        // =========================================
        // BOTONES
        // =========================================

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        5
                )
        );

        acciones.setOpaque(false);

        btnRegistrarFactura = new JButton(
                "+ Registrar factura"
        );

        btnRegistrarPago = new JButton(
                "+ Registrar pago"
        );

        btnVerPagos = new JButton(
                "Ver pagos realizados"
        );

        acciones.add(btnRegistrarFactura);
        acciones.add(btnRegistrarPago);
        acciones.add(btnVerPagos);

        panel.add(acciones, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================
    // GETTERS
    // =========================================

    public JTable getTablaFacturas() {
        return tablaFacturas;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnRegistrarFactura() {
        return btnRegistrarFactura;
    }

    public JButton getBtnRegistrarPago() {
        return btnRegistrarPago;
    }

    public JButton getBtnVerPagos() {
        return btnVerPagos;
    }

    public JLabel getLblEstado() {
        return lblEstado;
    }
}
