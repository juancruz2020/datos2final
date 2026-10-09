package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EnviosPanel extends JPanel {
    private JTable tablaEnvios;
    private JButton btnActualizar, btnMostrarFormulario, btnEditar, btnEliminar;
    private JLabel lblEstado;
    private JPanel panelFormulario;
    private JLabel lblTituloFormulario;
    private JComboBox<String> cmbCliente;
    private JComboBox<String> cmbVehiculo;
    private JButton btnSelectorContenedores;
    private JPopupMenu popupContenedores;
    private final Set<String> contenedoresSeleccionados = new LinkedHashSet<>();
    private final List<String> opcionesContenedores = new ArrayList<>();
    private JTextField txtCiudadOrigen, txtPaisOrigen, txtCiudadDestino, txtPaisDestino;
    private JComboBox<String> cmbEstado, cmbPrioridad;
    private JButton btnGuardar, btnCancelar;
    private JPanel centro;
    private JScrollPane scrollFormulario;

    public EnviosPanel() { construir(); }



    private void construir() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Colores.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));

        // =========================
        // TÍTULO
        // =========================
        JLabel titulo = new JLabel("Envíos");
        titulo.setFont(Fuentes.TITULO);
        titulo.setForeground(Colores.TEXTO);

        add(titulo, BorderLayout.NORTH);

        // =========================
        // PANEL CENTRAL
        // =========================
        centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        // La tabla ocupa todo el espacio disponible.
        centro.add(construirPanelTabla(), BorderLayout.CENTER);

        // =========================
        // FORMULARIO
        // =========================
        panelFormulario = construirFormulario();

        scrollFormulario = new JScrollPane(panelFormulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(14);
        scrollFormulario.setPreferredSize(new Dimension(800, 300));

        // No agregamos el formulario hasta que se solicite.
        add(centro, BorderLayout.CENTER);

        // =========================
        // ESTADO
        // =========================
        lblEstado = new JLabel("Envíos registrados: 0");
        lblEstado.setFont(Fuentes.NORMAL);
        lblEstado.setForeground(Colores.TEXTO_SECUNDARIO);

        add(lblEstado, BorderLayout.SOUTH);
    }



    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colores.BORDE),
                BorderFactory.createEmptyBorder(12, 12, 10, 12)
        ));

        // =========================
        // CABECERA
        // =========================
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JLabel subtitulo = new JLabel("Envíos registrados");
        subtitulo.setFont(Fuentes.LABEL);

        btnActualizar = new JButton("Actualizar");

        cabecera.add(subtitulo, BorderLayout.WEST);
        cabecera.add(btnActualizar, BorderLayout.EAST);

        panel.add(cabecera, BorderLayout.NORTH);

        // =========================
        // TABLA
        // =========================
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                        "Identificador",
                        "Cliente",
                        "Contenedores",
                        "Vehículo",
                        "Fecha",
                        "Origen",
                        "Destino",
                        "Estado",
                        "Prioridad"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEnvios = new JTable(modelo);
        tablaEnvios.setRowHeight(28);
        tablaEnvios.setFont(Fuentes.NORMAL);
        tablaEnvios.getTableHeader().setFont(Fuentes.LABEL);
        tablaEnvios.getTableHeader().setReorderingAllowed(false);
        tablaEnvios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Evita que Swing comprima todas las columnas para meterlas
        // en el ancho disponible.
        tablaEnvios.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Ancho de cada columna.
        tablaEnvios.getColumnModel().getColumn(0).setPreferredWidth(110); // Identificador
        tablaEnvios.getColumnModel().getColumn(1).setPreferredWidth(180); // Cliente
        tablaEnvios.getColumnModel().getColumn(2).setPreferredWidth(220); // Contenedores
        tablaEnvios.getColumnModel().getColumn(3).setPreferredWidth(170); // Vehículo
        tablaEnvios.getColumnModel().getColumn(4).setPreferredWidth(100); // Fecha
        tablaEnvios.getColumnModel().getColumn(5).setPreferredWidth(180); // Origen
        tablaEnvios.getColumnModel().getColumn(6).setPreferredWidth(180); // Destino
        tablaEnvios.getColumnModel().getColumn(7).setPreferredWidth(120); // Estado
        tablaEnvios.getColumnModel().getColumn(8).setPreferredWidth(100); // Prioridad

        JScrollPane scroll = new JScrollPane(tablaEnvios);
        scroll.setPreferredSize(new Dimension(900, 250));
        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        panel.add(scroll, BorderLayout.CENTER);

        // =========================
        // BOTONES DE ACCIÓN
        // =========================
        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        acciones.setOpaque(false);

        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnMostrarFormulario = new JButton("+ Agregar envío");

        acciones.add(btnEditar);
        acciones.add(btnEliminar);
        acciones.add(btnMostrarFormulario);

        panel.add(acciones, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Colores.SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colores.BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 7, 5, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        lblTituloFormulario = new JLabel("Nuevo envío");
        lblTituloFormulario.setFont(Fuentes.LABEL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1;
        panel.add(lblTituloFormulario, gbc);
        gbc.gridwidth = 1;

        cmbCliente = new JComboBox<>();
        agregarCampo(panel, gbc, 1, "Cliente", cmbCliente);

        btnSelectorContenedores = new JButton("Seleccionar contenedores...");
        btnSelectorContenedores.setHorizontalAlignment(SwingConstants.LEFT);
        popupContenedores = new JPopupMenu();
        btnSelectorContenedores.addActionListener(e -> {
            reconstruirPopupContenedores();
            popupContenedores.show(btnSelectorContenedores, 0, btnSelectorContenedores.getHeight());
        });
        agregarCampo(panel, gbc, 2, "Contenedores", btnSelectorContenedores);

        cmbVehiculo = new JComboBox<>();
        agregarCampo(panel, gbc, 3, "Vehículo", cmbVehiculo);

        txtCiudadOrigen = new JTextField(20);
        txtPaisOrigen = new JTextField(20);
        txtCiudadDestino = new JTextField(20);
        txtPaisDestino = new JTextField(20);
        agregarCampo(panel, gbc, 4, "Ciudad de origen", txtCiudadOrigen);
        agregarCampo(panel, gbc, 5, "País de origen", txtPaisOrigen);
        agregarCampo(panel, gbc, 6, "Ciudad de destino", txtCiudadDestino);
        agregarCampo(panel, gbc, 7, "País de destino", txtPaisDestino);

        cmbEstado = new JComboBox<>(new String[]{"PENDIENTE", "EN_TRANSITO", "DEMORADO", "ENTREGADO", "CANCELADO"});
        agregarCampo(panel, gbc, 8, "Estado", cmbEstado);
        cmbPrioridad = new JComboBox<>(new String[]{"BAJA", "MEDIA", "ALTA"});
        agregarCampo(panel, gbc, 9, "Prioridad", cmbPrioridad);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        btnCancelar = new JButton("Cancelar");
        btnGuardar = new JButton("Guardar envío");
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.weightx = 1;
        panel.add(botones, gbc);
        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String texto, JComponent componente) {
        JLabel label = new JLabel(texto + ":");
        label.setFont(Fuentes.NORMAL);
        gbc.gridy = fila; gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        componente.setPreferredSize(new Dimension(400, 32));
        componente.setMinimumSize(new Dimension(150, 30));
        panel.add(componente, gbc);
    }

    private void reconstruirPopupContenedores() {
        popupContenedores.removeAll();
        if (opcionesContenedores.isEmpty()) {
            JMenuItem vacio = new JMenuItem("No hay contenedores disponibles");
            vacio.setEnabled(false);
            popupContenedores.add(vacio);
            return;
        }
        for (String codigo : opcionesContenedores) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(codigo, contenedoresSeleccionados.contains(codigo));
            item.addActionListener(e -> {
                if (item.isSelected()) contenedoresSeleccionados.add(codigo);
                else contenedoresSeleccionados.remove(codigo);
                actualizarTextoSelectorContenedores();
            });
            popupContenedores.add(item);
        }
    }

    private void actualizarTextoSelectorContenedores() {
        if (contenedoresSeleccionados.isEmpty()) {
            btnSelectorContenedores.setText("Seleccionar contenedores...");
        } else if (contenedoresSeleccionados.size() <= 2) {
            btnSelectorContenedores.setText(String.join(", ", contenedoresSeleccionados));
        } else {
            btnSelectorContenedores.setText(contenedoresSeleccionados.size() + " contenedores seleccionados");
        }
    }

    public void setOpcionesContenedores(List<String> opciones) {
        opcionesContenedores.clear();
        opcionesContenedores.addAll(opciones);
        contenedoresSeleccionados.retainAll(opcionesContenedores);
        actualizarTextoSelectorContenedores();
    }

    public List<String> getContenedoresSeleccionados() {
        return new ArrayList<>(contenedoresSeleccionados);
    }

    public void seleccionarContenedores(List<String> etiquetas) {
        contenedoresSeleccionados.clear();
        for (String etiqueta : etiquetas) {
            if (opcionesContenedores.contains(etiqueta)) contenedoresSeleccionados.add(etiqueta);
        }
        actualizarTextoSelectorContenedores();
    }

    public void limpiarContenedoresSeleccionados() {
        contenedoresSeleccionados.clear();
        actualizarTextoSelectorContenedores();
    }


    public void mostrarFormulario() {
        if (scrollFormulario.getParent() != centro) {
            centro.add(scrollFormulario, BorderLayout.SOUTH);
        }

        panelFormulario.setVisible(true);
        btnMostrarFormulario.setVisible(false);

        centro.revalidate();
        centro.repaint();
    }

    public void ocultarFormulario() {
        centro.remove(scrollFormulario);

        panelFormulario.setVisible(false);
        btnMostrarFormulario.setVisible(true);

        centro.revalidate();
        centro.repaint();
    }


    public void prepararNuevoEnvio() {
        lblTituloFormulario.setText("Nuevo envío");
        btnGuardar.setText("Guardar envío");
        cmbEstado.setSelectedItem("PENDIENTE");
        cmbEstado.setEnabled(false);
    }

    public void prepararEdicion() {
        lblTituloFormulario.setText("Editar envío");
        btnGuardar.setText("Guardar cambios");
        cmbEstado.setEnabled(true);
    }

    public void limpiarFormulario() {
        if (cmbCliente.getItemCount() > 0) cmbCliente.setSelectedIndex(0);
        if (cmbVehiculo != null && cmbVehiculo.getItemCount() > 0) cmbVehiculo.setSelectedIndex(0);
        limpiarContenedoresSeleccionados();
        txtCiudadOrigen.setText("");
        txtPaisOrigen.setText("");
        txtCiudadDestino.setText("");
        txtPaisDestino.setText("");
        if (cmbEstado.getItemCount() > 0) cmbEstado.setSelectedItem("PENDIENTE");
        if (cmbPrioridad.getItemCount() > 0) cmbPrioridad.setSelectedIndex(0);
    }

    public JTable getTablaEnvios() { return tablaEnvios; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnMostrarFormulario() { return btnMostrarFormulario; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JComboBox<String> getCmbCliente() { return cmbCliente; }
    public JComboBox<String> getCmbVehiculo() { return cmbVehiculo; }
    public JTextField getTxtCiudadOrigen() { return txtCiudadOrigen; }
    public JTextField getTxtPaisOrigen() { return txtPaisOrigen; }
    public JTextField getTxtCiudadDestino() { return txtCiudadDestino; }
    public JTextField getTxtPaisDestino() { return txtPaisDestino; }
    public JComboBox<String> getCmbEstado() { return cmbEstado; }
    public JComboBox<String> getCmbPrioridad() { return cmbPrioridad; }
    public JLabel getLblEstado() { return lblEstado; }
}
