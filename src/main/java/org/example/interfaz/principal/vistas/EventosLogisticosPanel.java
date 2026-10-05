package org.example.interfaz.principal.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EventosLogisticosPanel extends JPanel {

    private final JComboBox<String> comboEnvio;
    private final JTextField txtTipoEvento;
    private final JTextField txtUbicacion;
    private final JTextArea txtDescripcion;

    private final JButton btnRegistrar;
    private final JButton btnEditar;
    private final JButton btnEliminar;
    private final JButton btnActualizar;

    private final JTable tablaEventos;
    private final DefaultTableModel modeloTabla;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EventosLogisticosPanel() {

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );


        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Eventos Logísticos"
                );

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        add(
                titulo,
                BorderLayout.NORTH
        );


        // =====================================================
        // PANEL CENTRAL
        // =====================================================

        JPanel panelCentral =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                        )
                );


        // =====================================================
        // FORMULARIO
        // =====================================================

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Registrar / modificar evento"
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        // -----------------------------------------------------
        // ENVÍO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Envío:"),
                gbc
        );


        comboEnvio =
                new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                comboEnvio,
                gbc
        );


        // -----------------------------------------------------
        // TIPO EVENTO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Tipo de evento:"),
                gbc
        );


        txtTipoEvento =
                new JTextField();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                txtTipoEvento,
                gbc
        );


        // -----------------------------------------------------
        // UBICACIÓN
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Ubicación:"),
                gbc
        );


        txtUbicacion =
                new JTextField();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                txtUbicacion,
                gbc
        );


        // -----------------------------------------------------
        // DESCRIPCIÓN
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        gbc.anchor =
                GridBagConstraints.NORTH;

        formulario.add(
                new JLabel("Descripción:"),
                gbc
        );


        txtDescripcion =
                new JTextArea(
                        3,
                        20
                );

        txtDescripcion.setLineWrap(
                true
        );

        txtDescripcion.setWrapStyleWord(
                true
        );


        JScrollPane scrollDescripcion =
                new JScrollPane(
                        txtDescripcion
                );


        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                scrollDescripcion,
                gbc
        );


        // -----------------------------------------------------
        // BOTÓN REGISTRAR / GUARDAR
        // -----------------------------------------------------

        btnRegistrar =
                new JButton(
                        "Registrar evento"
                );


        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0;

        formulario.add(
                btnRegistrar,
                gbc
        );


        panelCentral.add(
                formulario,
                BorderLayout.NORTH
        );


        // =====================================================
        // TABLA
        // =====================================================

        modeloTabla =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Envío",
                                "Fecha y hora",
                                "Tipo",
                                "Ubicación",
                                "Descripción"
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


        tablaEventos =
                new JTable(
                        modeloTabla
                );


        tablaEventos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        tablaEventos.setAutoResizeMode(
                JTable.AUTO_RESIZE_LAST_COLUMN
        );


        JScrollPane scrollTabla =
                new JScrollPane(
                        tablaEventos
                );


        panelCentral.add(
                scrollTabla,
                BorderLayout.CENTER
        );


        // =====================================================
        // BOTONES DE ACCIONES
        // =====================================================

        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        btnEditar =
                new JButton(
                        "Editar"
                );


        btnEliminar =
                new JButton(
                        "Eliminar"
                );


        btnActualizar =
                new JButton(
                        "Actualizar"
                );


        panelBotones.add(
                btnEditar
        );

        panelBotones.add(
                btnEliminar
        );

        panelBotones.add(
                btnActualizar
        );


        panelCentral.add(
                panelBotones,
                BorderLayout.SOUTH
        );


        add(
                panelCentral,
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public JComboBox<String> getComboEnvio() {

        return comboEnvio;
    }


    public JTextField getTxtTipoEvento() {

        return txtTipoEvento;
    }


    public JTextField getTxtUbicacion() {

        return txtUbicacion;
    }


    public JTextArea getTxtDescripcion() {

        return txtDescripcion;
    }


    public JButton getBtnRegistrar() {

        return btnRegistrar;
    }


    public JButton getBtnEditar() {

        return btnEditar;
    }


    public JButton getBtnEliminar() {

        return btnEliminar;
    }


    public JButton getBtnActualizar() {

        return btnActualizar;
    }


    public JTable getTablaEventos() {

        return tablaEventos;
    }


    public DefaultTableModel getModeloTabla() {

        return modeloTabla;
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        txtTipoEvento.setText("");

        txtUbicacion.setText("");

        txtDescripcion.setText("");


        if (comboEnvio.getItemCount() > 0) {

            comboEnvio.setSelectedIndex(
                    0
            );
        }
    }


    // =========================================================
    // MODO REGISTRAR
    // =========================================================

    public void modoRegistrar() {

        btnRegistrar.setText(
                "Registrar evento"
        );
    }


    // =========================================================
    // MODO EDITAR
    // =========================================================

    public void modoEditar() {

        btnRegistrar.setText(
                "Guardar cambios"
        );
    }
}