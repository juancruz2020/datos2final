package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdministracionPanel extends JPanel {

    // =========================================================
    // SESIONES
    // =========================================================

    private final JTable tablaSesiones;

    private final JButton btnActualizarSesiones;

    private final JButton btnCerrarSesion;

    // =========================================================
    // USUARIOS
    // =========================================================

    private final JTextField txtNombre;

    private final JTextField txtApellido;

    private final JTextField txtUsuario;

    private final JTextField txtEmail;

    private final JPasswordField txtPassword;

    private final JButton btnRegistrar;

    private final JButton btnEliminar;

    // =========================================================
    // ESTADO
    // =========================================================

    private final JLabel lblEstado;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdministracionPanel() {

        tablaSesiones =
                new JTable();

        btnActualizarSesiones =
                new JButton("Actualizar");

        btnCerrarSesion =
                new JButton("Cerrar sesión");

        txtNombre =
                new JTextField();

        txtApellido =
                new JTextField();

        txtUsuario =
                new JTextField();

        txtEmail =
                new JTextField();

        txtPassword =
                new JPasswordField();

        btnRegistrar =
                new JButton("Registrar usuario");

        btnEliminar =
                new JButton("Eliminar usuario");

        lblEstado =
                new JLabel(" ");

        construir();
    }

    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(
                new BorderLayout(
                        20,
                        20
                )
        );

        setBackground(
                Colores.FONDO
        );

        setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Administración"
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

        JPanel contenido =
                new JPanel();

        contenido.setLayout(
                new BorderLayout(
                        20,
                        20
                )
        );

        contenido.setOpaque(false);

        // =====================================================
        // SESIONES
        // =====================================================

        contenido.add(
                crearPanelSesiones(),
                BorderLayout.CENTER
        );

        // =====================================================
        // USUARIOS
        // =====================================================

        contenido.add(
                crearPanelUsuarios(),
                BorderLayout.SOUTH
        );

        add(
                contenido,
                BorderLayout.CENTER
        );

        // =====================================================
        // ESTADO
        // =====================================================

        lblEstado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        add(
                lblEstado,
                BorderLayout.SOUTH
        );
    }

    // =========================================================
    // PANEL SESIONES
    // =========================================================

    private JPanel crearPanelSesiones() {

        JPanel panel =
                crearPanelBase();

        panel.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        JLabel titulo =
                new JLabel(
                        "Sesiones activas"
                );

        titulo.setFont(
                Fuentes.SUBTITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        panel.add(
                titulo,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLA
        // =====================================================

        tablaSesiones.setModel(
                new javax.swing.table.DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                                "Usuario",
                                "Tiempo restante"
                        }
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                }
        );

        tablaSesiones.setRowHeight(
                32
        );

        tablaSesiones.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaSesiones.getTableHeader()
                .setReorderingAllowed(false);

        JScrollPane scroll =
                new JScrollPane(
                        tablaSesiones
                );

        panel.add(
                scroll,
                BorderLayout.CENTER
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

        botones.setOpaque(false);

        btnActualizarSesiones.setFocusPainted(
                false
        );

        btnCerrarSesion.setFocusPainted(
                false
        );

        botones.add(
                btnActualizarSesiones
        );

        botones.add(
                btnCerrarSesion
        );

        panel.add(
                botones,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // =========================================================
    // PANEL USUARIOS
    // =========================================================

    private JPanel crearPanelUsuarios() {

        JPanel panel =
                crearPanelBase();

        panel.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        JLabel titulo =
                new JLabel(
                        "Gestión de usuarios"
                );

        titulo.setFont(
                Fuentes.SUBTITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        panel.add(
                titulo,
                BorderLayout.NORTH
        );

        // =====================================================
        // FORMULARIO
        // =====================================================

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(false);

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

        gbc.weightx = 1;

        // -----------------------------------------------------
        // NOMBRE
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                0,
                "Nombre:",
                txtNombre
        );

        // -----------------------------------------------------
        // APELLIDO
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                1,
                "Apellido:",
                txtApellido
        );

        // -----------------------------------------------------
        // USUARIO
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                2,
                "Usuario:",
                txtUsuario
        );

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                3,
                "Email:",
                txtEmail
        );

        // -----------------------------------------------------
        // PASSWORD
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                4,
                "Contraseña:",
                txtPassword
        );

        panel.add(
                formulario,
                BorderLayout.CENTER
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

        botones.setOpaque(false);

        btnRegistrar.setFocusPainted(
                false
        );

        btnEliminar.setFocusPainted(
                false
        );

        botones.add(
                btnRegistrar
        );

        botones.add(
                btnEliminar
        );

        panel.add(
                botones,
                BorderLayout.SOUTH
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
            String etiqueta,
            JComponent campo
    ) {

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;

        JLabel label =
                new JLabel(
                        etiqueta
                );

        label.setForeground(
                Colores.TEXTO
        );

        panel.add(
                label,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(
                campo,
                gbc
        );
    }

    // =========================================================
    // PANEL BASE
    // =========================================================

    private JPanel crearPanelBase() {

        JPanel panel =
                new JPanel();

        panel.setBackground(
                Colores.SUPERFICIE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        new EmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        return panel;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public JTable getTablaSesiones() {

        return tablaSesiones;
    }

    public JButton getBtnActualizarSesiones() {

        return btnActualizarSesiones;
    }

    public JButton getBtnCerrarSesion() {

        return btnCerrarSesion;
    }

    public JTextField getTxtNombre() {

        return txtNombre;
    }

    public JTextField getTxtApellido() {

        return txtApellido;
    }

    public JTextField getTxtUsuario() {

        return txtUsuario;
    }

    public JTextField getTxtEmail() {

        return txtEmail;
    }

    public JPasswordField getTxtPassword() {

        return txtPassword;
    }

    public JButton getBtnRegistrar() {

        return btnRegistrar;
    }

    public JButton getBtnEliminar() {

        return btnEliminar;
    }

    public JLabel getLblEstado() {

        return lblEstado;
    }
}