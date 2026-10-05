package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnviosPanel extends JPanel {

    // =========================================================
    // TABLA
    // =========================================================

    private JTable tablaEnvios;

    private JButton btnActualizar;
    private JButton btnMostrarFormulario;
    private JButton btnEditar;
    private JButton btnEliminar;

    private JLabel lblEstado;


    // =========================================================
    // FORMULARIO
    // =========================================================

    private JPanel panelFormulario;

    private JLabel lblTituloFormulario;

    private JComboBox<String> cmbCliente;

    private JList<String> listaContenedores;

    private JTextField txtCiudadOrigen;
    private JTextField txtPaisOrigen;

    private JTextField txtCiudadDestino;
    private JTextField txtPaisDestino;

    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbPrioridad;

    private JButton btnGuardar;
    private JButton btnCancelar;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EnviosPanel() {

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
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Envíos"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        add(
                titulo,
                BorderLayout.NORTH
        );


        // =====================================================
        // CONTENIDO
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
                construirPanelTabla()
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
                        "Envíos registrados: 0"
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
    // PANEL TABLA
    // =========================================================

    private JPanel construirPanelTabla() {

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


        // =====================================================
        // CABECERA
        // =====================================================

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(
                false
        );

        JLabel subtitulo =
                new JLabel(
                        "Envíos registrados"
                );

        subtitulo.setFont(
                Fuentes.LABEL
        );

        btnActualizar =
                new JButton(
                        "Actualizar"
                );

        cabecera.add(
                subtitulo,
                BorderLayout.WEST
        );

        cabecera.add(
                btnActualizar,
                BorderLayout.EAST
        );

        panel.add(
                cabecera,
                BorderLayout.NORTH
        );


        // =====================================================
        // TABLA
        // =====================================================

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                                "Cliente",
                                "Contenedores",
                                "Fecha",
                                "Origen",
                                "Destino",
                                "Estado",
                                "Prioridad"
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

        tablaEnvios =
                new JTable(
                        modelo
                );

        tablaEnvios.setRowHeight(
                30
        );

        tablaEnvios.setFont(
                Fuentes.NORMAL
        );

        tablaEnvios.getTableHeader()
                .setFont(
                        Fuentes.LABEL
                );

        tablaEnvios.getTableHeader()
                .setReorderingAllowed(
                        false
                );

        tablaEnvios.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(
                        tablaEnvios
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
        // ACCIONES
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

        btnEditar =
                new JButton(
                        "Editar"
                );

        btnEliminar =
                new JButton(
                        "Eliminar"
                );

        btnMostrarFormulario =
                new JButton(
                        "+ Agregar envío"
                );

        acciones.add(
                btnEditar
        );

        acciones.add(
                btnEliminar
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


        // =====================================================
        // TÍTULO
        // =====================================================

        lblTituloFormulario =
                new JLabel(
                        "Nuevo envío"
                );

        lblTituloFormulario.setFont(
                Fuentes.LABEL
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        panel.add(
                lblTituloFormulario,
                gbc
        );

        gbc.gridwidth = 1;


        // =====================================================
        // CLIENTE
        // =====================================================

        cmbCliente =
                new JComboBox<>();

        agregarCampo(
                panel,
                gbc,
                1,
                "Cliente:",
                cmbCliente
        );


        // =====================================================
        // CONTENEDORES
        // =====================================================

        listaContenedores =
                new JList<>();

        listaContenedores.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        );

        listaContenedores.setVisibleRowCount(
                4
        );

        JScrollPane scrollContenedores =
                new JScrollPane(
                        listaContenedores
                );

        agregarCampo(
                panel,
                gbc,
                2,
                "Contenedores:",
                scrollContenedores
        );


        // =====================================================
        // ORIGEN
        // =====================================================

        txtCiudadOrigen =
                new JTextField();

        agregarCampo(
                panel,
                gbc,
                3,
                "Ciudad origen:",
                txtCiudadOrigen
        );

        txtPaisOrigen =
                new JTextField();

        agregarCampo(
                panel,
                gbc,
                4,
                "País origen:",
                txtPaisOrigen
        );


        // =====================================================
        // DESTINO
        // =====================================================

        txtCiudadDestino =
                new JTextField();

        agregarCampo(
                panel,
                gbc,
                5,
                "Ciudad destino:",
                txtCiudadDestino
        );

        txtPaisDestino =
                new JTextField();

        agregarCampo(
                panel,
                gbc,
                6,
                "País destino:",
                txtPaisDestino
        );


        // =====================================================
        // ESTADO
        // =====================================================

        cmbEstado =
                new JComboBox<>(
                        new String[]{
                                "PENDIENTE",
                                "EN_TRANSITO",
                                "DEMORADO",
                                "ENTREGADO",
                                "CANCELADO"
                        }
                );

        agregarCampo(
                panel,
                gbc,
                7,
                "Estado:",
                cmbEstado
        );


        // =====================================================
        // PRIORIDAD
        // =====================================================

        cmbPrioridad =
                new JComboBox<>(
                        new String[]{
                                "BAJA",
                                "MEDIA",
                                "ALTA"
                        }
                );

        agregarCampo(
                panel,
                gbc,
                8,
                "Prioridad:",
                cmbPrioridad
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
                        "Guardar envío"
                );

        botones.add(
                btnCancelar
        );

        botones.add(
                btnGuardar
        );

        gbc.gridx = 0;
        gbc.gridy = 9;
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
                        componente instanceof JScrollPane
                                ? 80
                                : 30
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
    // MODO NUEVO
    // =========================================================

    public void prepararNuevoEnvio() {

        lblTituloFormulario.setText(
                "Nuevo envío"
        );

        btnGuardar.setText(
                "Guardar envío"
        );

        cmbEstado.setSelectedItem(
                "PENDIENTE"
        );

        cmbEstado.setEnabled(
                false
        );
    }


    // =========================================================
    // MODO EDICIÓN
    // =========================================================

    public void prepararEdicion() {

        lblTituloFormulario.setText(
                "Editar envío"
        );

        btnGuardar.setText(
                "Guardar cambios"
        );

        cmbEstado.setEnabled(
                true
        );
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        if (cmbCliente.getItemCount() > 0) {

            cmbCliente.setSelectedIndex(
                    0
            );
        }

        listaContenedores.clearSelection();

        txtCiudadOrigen.setText("");

        txtPaisOrigen.setText("");

        txtCiudadDestino.setText("");

        txtPaisDestino.setText("");

        if (cmbEstado.getItemCount() > 0) {

            cmbEstado.setSelectedItem(
                    "PENDIENTE"
            );
        }

        if (cmbPrioridad.getItemCount() > 0) {

            cmbPrioridad.setSelectedIndex(
                    0
            );
        }
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public JTable getTablaEnvios() {

        return tablaEnvios;
    }


    public JButton getBtnActualizar() {

        return btnActualizar;
    }


    public JButton getBtnMostrarFormulario() {

        return btnMostrarFormulario;
    }


    public JButton getBtnEditar() {

        return btnEditar;
    }


    public JButton getBtnEliminar() {

        return btnEliminar;
    }


    public JButton getBtnGuardar() {

        return btnGuardar;
    }


    public JButton getBtnCancelar() {

        return btnCancelar;
    }


    public JComboBox<String> getCmbCliente() {

        return cmbCliente;
    }


    public JList<String> getListaContenedores() {

        return listaContenedores;
    }


    public JTextField getTxtCiudadOrigen() {

        return txtCiudadOrigen;
    }


    public JTextField getTxtPaisOrigen() {

        return txtPaisOrigen;
    }


    public JTextField getTxtCiudadDestino() {

        return txtCiudadDestino;
    }


    public JTextField getTxtPaisDestino() {

        return txtPaisDestino;
    }


    public JComboBox<String> getCmbEstado() {

        return cmbEstado;
    }


    public JComboBox<String> getCmbPrioridad() {

        return cmbPrioridad;
    }


    public JLabel getLblEstado() {

        return lblEstado;
    }
}