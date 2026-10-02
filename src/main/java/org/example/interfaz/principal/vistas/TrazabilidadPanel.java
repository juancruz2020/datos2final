package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrazabilidadPanel extends JPanel {

    private JComboBox<String> cmbEnvio;

    private JTable tablaTramos;

    private JButton btnActualizar;
    private JButton btnMostrarFormulario;

    private JLabel lblEstado;

    private JPanel panelFormulario;

    private JComboBox<String> cmbMedioTransporte;

    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JTextField txtFechaSalida;
    private JTextField txtFechaLlegada;

    private JButton btnGuardar;
    private JButton btnCancelar;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TrazabilidadPanel() {

        construir();
    }


    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(
                new BorderLayout(
                        0,
                        20
                )
        );

        setBackground(
                Colores.FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        20,
                        30
                )
        );


        // =====================================================
        // CABECERA
        // =====================================================

        JPanel cabecera =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        cabecera.setOpaque(
                false
        );


        JLabel titulo =
                new JLabel(
                        "Trazabilidad"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );


        JPanel selector =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        selector.setOpaque(
                false
        );


        JLabel lblEnvio =
                new JLabel(
                        "Envío:"
                );

        lblEnvio.setFont(
                Fuentes.LABEL
        );


        cmbEnvio =
                new JComboBox<>();

        cmbEnvio.setPreferredSize(
                new Dimension(
                        350,
                        30
                )
        );


        btnActualizar =
                new JButton(
                        "Actualizar"
                );


        selector.add(
                lblEnvio
        );

        selector.add(
                cmbEnvio
        );

        selector.add(
                btnActualizar
        );


        cabecera.add(
                titulo,
                BorderLayout.WEST
        );

        cabecera.add(
                selector,
                BorderLayout.EAST
        );


        add(
                cabecera,
                BorderLayout.NORTH
        );


        // =====================================================
        // CENTRO
        // =====================================================

        JPanel centro =
                new JPanel();

        centro.setLayout(
                new BoxLayout(
                        centro,
                        BoxLayout.Y_AXIS
                )
        );

        centro.setOpaque(
                false
        );


        centro.add(
                construirPanelTramos()
        );


        centro.add(
                Box.createVerticalStrut(
                        20
                )
        );


        panelFormulario =
                construirFormulario();

        panelFormulario.setVisible(
                false
        );


        centro.add(
                panelFormulario
        );


        add(
                centro,
                BorderLayout.CENTER
        );


        // =====================================================
        // ESTADO
        // =====================================================

        lblEstado =
                new JLabel(
                        "Seleccioná un envío para ver su trazabilidad."
                );

        lblEstado.setFont(
                Fuentes.NORMAL
        );

        lblEstado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );


        add(
                lblEstado,
                BorderLayout.SOUTH
        );
    }


    // =========================================================
    // PANEL DE TRAMOS
    // =========================================================

    private JPanel construirPanelTramos() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                10
                        )
                );


        panel.setBackground(
                Colores.SUPERFICIE
        );


        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );


        JLabel titulo =
                new JLabel(
                        "Tramos del envío"
                );

        titulo.setFont(
                Fuentes.LABEL
        );


        panel.add(
                titulo,
                BorderLayout.NORTH
        );


        // =====================================================
        // TABLA
        // =====================================================

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                                "Medio de transporte",
                                "Origen",
                                "Destino",
                                "Fecha salida",
                                "Llegada estimada"
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


        tablaTramos =
                new JTable(
                        modelo
                );


        tablaTramos.setRowHeight(
                30
        );

        tablaTramos.setFont(
                Fuentes.NORMAL
        );

        tablaTramos.getTableHeader()
                .setFont(
                        Fuentes.LABEL
                );

        tablaTramos.getTableHeader()
                .setReorderingAllowed(
                        false
                );


        JScrollPane scroll =
                new JScrollPane(
                        tablaTramos
                );


        scroll.setPreferredSize(
                new Dimension(
                        900,
                        350
                )
        );


        panel.add(
                scroll,
                BorderLayout.CENTER
        );


        // =====================================================
        // BOTÓN AGREGAR
        // =====================================================

        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        acciones.setOpaque(
                false
        );


        btnMostrarFormulario =
                new JButton(
                        "+ Agregar tramo"
                );


        acciones.add(
                btnMostrarFormulario
        );


        panel.add(
                acciones,
                BorderLayout.SOUTH
        );


        return panel;
    }


    // =========================================================
    // FORMULARIO
    // =========================================================

    private JPanel construirFormulario() {

        JPanel panel =
                new JPanel(
                        new GridBagLayout()
                );


        panel.setBackground(
                Colores.SUPERFICIE
        );


        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();


        gbc.insets =
                new Insets(
                        7,
                        5,
                        7,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        JLabel titulo =
                new JLabel(
                        "Nuevo tramo"
                );

        titulo.setFont(
                Fuentes.LABEL
        );


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;


        panel.add(
                titulo,
                gbc
        );


        gbc.gridwidth = 1;


        // =====================================================
        // MEDIO DE TRANSPORTE
        // =====================================================

        cmbMedioTransporte =
                new JComboBox<>(
                        new String[]{
                                "CAMION",
                                "BARCO",
                                "AVION",
                                "TREN"
                        }
                );


        agregarCampo(
                panel,
                gbc,
                1,
                "Medio de transporte:",
                cmbMedioTransporte
        );


        // =====================================================
        // ORIGEN
        // =====================================================

        txtOrigen =
                new JTextField();


        agregarCampo(
                panel,
                gbc,
                2,
                "Origen:",
                txtOrigen
        );


        // =====================================================
        // DESTINO
        // =====================================================

        txtDestino =
                new JTextField();


        agregarCampo(
                panel,
                gbc,
                3,
                "Destino:",
                txtDestino
        );


        // =====================================================
        // FECHA SALIDA
        // =====================================================

        txtFechaSalida =
                new JTextField();


        agregarCampo(
                panel,
                gbc,
                4,
                "Fecha salida (dd/MM/yyyy):",
                txtFechaSalida
        );


        // =====================================================
        // FECHA LLEGADA
        // =====================================================

        txtFechaLlegada =
                new JTextField();


        agregarCampo(
                panel,
                gbc,
                5,
                "Llegada estimada (dd/MM/yyyy):",
                txtFechaLlegada
        );


        // =====================================================
        // BOTONES
        // =====================================================

        JPanel botones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        botones.setOpaque(
                false
        );


        btnCancelar =
                new JButton(
                        "Cancelar"
                );


        btnGuardar =
                new JButton(
                        "Guardar tramo"
                );


        botones.add(
                btnCancelar
        );

        botones.add(
                btnGuardar
        );


        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1;


        panel.add(
                botones,
                gbc
        );


        return panel;
    }


    // =========================================================
    // AGREGAR CAMPO
    // =========================================================

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String texto,
            JComponent componente
    ) {

        JLabel label =
                new JLabel(
                        texto
                );

        label.setFont(
                Fuentes.NORMAL
        );


        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;


        panel.add(
                label,
                gbc
        );


        gbc.gridx = 1;
        gbc.weightx = 1;


        componente.setPreferredSize(
                new Dimension(
                        400,
                        30
                )
        );


        panel.add(
                componente,
                gbc
        );
    }


    // =========================================================
    // MOSTRAR FORMULARIO
    // =========================================================

    public void mostrarFormulario() {

        panelFormulario.setVisible(
                true
        );

        btnMostrarFormulario.setVisible(
                false
        );


        revalidate();

        repaint();
    }


    // =========================================================
    // OCULTAR FORMULARIO
    // =========================================================

    public void ocultarFormulario() {

        panelFormulario.setVisible(
                false
        );

        btnMostrarFormulario.setVisible(
                true
        );


        revalidate();

        repaint();
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        if (cmbMedioTransporte.getItemCount() > 0) {

            cmbMedioTransporte.setSelectedIndex(
                    0
            );
        }


        txtOrigen.setText("");

        txtDestino.setText("");

        txtFechaSalida.setText("");

        txtFechaLlegada.setText("");
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public JComboBox<String> getCmbEnvio() {

        return cmbEnvio;
    }


    public JTable getTablaTramos() {

        return tablaTramos;
    }


    public JButton getBtnActualizar() {

        return btnActualizar;
    }


    public JButton getBtnMostrarFormulario() {

        return btnMostrarFormulario;
    }


    public JComboBox<String> getCmbMedioTransporte() {

        return cmbMedioTransporte;
    }


    public JTextField getTxtOrigen() {

        return txtOrigen;
    }


    public JTextField getTxtDestino() {

        return txtDestino;
    }


    public JTextField getTxtFechaSalida() {

        return txtFechaSalida;
    }


    public JTextField getTxtFechaLlegada() {

        return txtFechaLlegada;
    }


    public JButton getBtnGuardar() {

        return btnGuardar;
    }


    public JButton getBtnCancelar() {

        return btnCancelar;
    }


    public JLabel getLblEstado() {

        return lblEstado;
    }
}