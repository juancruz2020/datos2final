
package org.example.interfaz.principal.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportesPanel extends JPanel {

    private final JTextField txtUsuario;
    private final JComboBox<String> comboCliente;
    private final JComboBox<String> comboTipo;
    private final JComboBox<String> comboEstado;
    private final JComboBox<String> comboPais;
    private final JComboBox<String> comboFormato;

    private final JLabel lblCliente;
    private final JLabel lblEstado;
    private final JLabel lblPais;

    private final JButton btnGenerar;
    private final JButton btnActualizar;

    private final JTable tablaReportes;
    private final DefaultTableModel modeloTabla;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportesPanel() {

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Reportes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));

        add(titulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(
                new BorderLayout(15, 15)
        );

        JPanel formulario = new JPanel(
                new GridBagLayout()
        );

        formulario.setBorder(
                BorderFactory.createTitledBorder("Generar reporte")
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // =========================================================
        // USUARIO AUTENTICADO
        // =========================================================

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formulario.add(new JLabel("Usuario:"), gbc);

        txtUsuario = new JTextField();
        txtUsuario.setEditable(false);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(txtUsuario, gbc);

        // =========================================================
        // TIPO DE REPORTE
        // =========================================================

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Tipo de reporte:"),
                gbc
        );

        comboTipo = new JComboBox<>(
                new String[]{
                        "Envíos por cliente",
                        "Envíos por estado",
                        "Envíos por país",
                        "Envíos demorados",
                        "Incidentes abiertos",
                        "Promedio de incidentes por país",
                        "Promedio de incidentes por estado",
                        "Promedio de incidentes por cliente",
                        "Ranking de rutas con más incidentes",
                        "Estado de sensores",
                        "Tiempos mínimos y máximos por cliente",
                        "Tiempo promedio de entrega",
                        "Costos por tipo de transporte"
                }
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(comboTipo, gbc);

        // =========================================================
        // CLIENTE
        // =========================================================

        lblCliente = new JLabel("Cliente:");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formulario.add(lblCliente, gbc);

        comboCliente = new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(comboCliente, gbc);

        // =========================================================
        // ESTADO
        // =========================================================

        lblEstado = new JLabel("Estado:");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formulario.add(lblEstado, gbc);

        comboEstado = new JComboBox<>(
                new String[]{
                        "PENDIENTE",
                        "EN_TRANSITO",
                        "ENTREGADO",
                        "CANCELADO"
                }
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(comboEstado, gbc);

        // =========================================================
        // PAIS
        // =========================================================

        lblPais = new JLabel("País:");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;

        formulario.add(lblPais, gbc);

        comboPais = new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(comboPais, gbc);

        // =========================================================
        // FORMATO PDF FIJO, SIN FLECHITAS
        // =========================================================

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;

        formulario.add(new JLabel("Formato:"), gbc);

        comboFormato = new JComboBox<>(
                new String[]{"PDF"}
        );

        comboFormato.setEditable(true);

        JTextField campoFormato =
                (JTextField) comboFormato.getEditor()
                        .getEditorComponent();

        campoFormato.setEditable(false);
        campoFormato.setFocusable(false);

        comboFormato.setUI(
                new javax.swing.plaf.basic.BasicComboBoxUI() {
                    @Override
                    protected JButton createArrowButton() {
                        JButton boton = new JButton();
                        boton.setVisible(false);
                        boton.setPreferredSize(
                                new Dimension(0, 0)
                        );
                        return boton;
                    }
                }
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(comboFormato, gbc);

        // =========================================================
        // GENERAR
        // =========================================================

        btnGenerar = new JButton("Generar reporte");

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0;

        formulario.add(btnGenerar, gbc);

        panelCentral.add(formulario, BorderLayout.NORTH);

        // =========================================================
        // TABLA DE REPORTES
        // =========================================================

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Usuario",
                        "Tipo",
                        "Fecha de generación",
                        "Formato",
                        "Estado"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaReportes = new JTable(modeloTabla);

        tablaReportes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        panelCentral.add(
                new JScrollPane(tablaReportes),
                BorderLayout.CENTER
        );

        // =========================================================
        // ACTUALIZAR
        // =========================================================

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        btnActualizar = new JButton("Actualizar");

        panelBotones.add(btnActualizar);

        panelCentral.add(
                panelBotones,
                BorderLayout.SOUTH
        );

        add(panelCentral, BorderLayout.CENTER);

        comboTipo.addActionListener(
                e -> actualizarFiltros()
        );

        actualizarFiltros();
    }

    // =========================================================
    // MOSTRAR FILTROS SEGUN EL TIPO DE REPORTE
    // =========================================================

    public void actualizarFiltros() {

        String tipo =
                (String) comboTipo.getSelectedItem();

        boolean porCliente =
                "Envíos por cliente".equals(tipo);

        boolean porEstado =
                "Envíos por estado".equals(tipo);

        boolean porPais =
                "Envíos por país".equals(tipo);

        lblCliente.setVisible(porCliente);
        comboCliente.setVisible(porCliente);

        lblEstado.setVisible(porEstado);
        comboEstado.setVisible(porEstado);

        lblPais.setVisible(porPais);
        comboPais.setVisible(porPais);

        // Los siguientes reportes no necesitan
        // filtros adicionales:
        //
        // - Envíos demorados
        // - Incidentes abiertos
        // - Promedio de incidentes por país
        // - Promedio de incidentes por estado
        // - Promedio de incidentes por cliente
        // - Ranking de rutas con más incidentes
        // - Estado de sensores
        // - Tiempos mínimos y máximos por cliente
        // - Tiempo promedio de entrega
        // - Costos por tipo de transporte

        revalidate();
        repaint();
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public JTextField getTxtUsuario() {
        return txtUsuario;
    }

    public JComboBox<String> getComboCliente() {
        return comboCliente;
    }

    public JComboBox<String> getComboTipo() {
        return comboTipo;
    }

    public JComboBox<String> getComboEstado() {
        return comboEstado;
    }

    public JComboBox<String> getComboPais() {
        return comboPais;
    }


    public JButton getBtnGenerar() {
        return btnGenerar;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }


    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }


}
