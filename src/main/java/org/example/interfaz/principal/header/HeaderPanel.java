package org.example.interfaz.principal.header;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    private final String usuario;

    private final JButton btnCerrarSesion;

    private final JLabel lblTiempoSesion;

    public HeaderPanel(String usuario) {

        this.usuario = usuario;

        this.btnCerrarSesion =
                new JButton("Salir");

        this.lblTiempoSesion =
                new JLabel("Sesión: --:--");

        construir();
    }

    private void construir() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                Colores.SUPERFICIE
        );

        setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        Colores.BORDE
                )
        );

        setPreferredSize(
                new Dimension(
                        0,
                        65
                )
        );

        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "  Dashboard"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        add(
                titulo,
                BorderLayout.WEST
        );

        // =====================================================
        // USUARIO + TTL + SALIR
        // =====================================================

        JPanel usuarioPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                12,
                                15
                        )
                );

        usuarioPanel.setOpaque(false);

        // -----------------------------------------------------
        // USUARIO
        // -----------------------------------------------------

        JLabel lblUsuario =
                new JLabel(
                        usuario
                );

        lblUsuario.setFont(
                Fuentes.NORMAL
        );

        lblUsuario.setForeground(
                Colores.TEXTO
        );

        // -----------------------------------------------------
        // TTL
        // -----------------------------------------------------

        lblTiempoSesion.setFont(
                Fuentes.NORMAL
        );

        lblTiempoSesion.setForeground(
                Colores.TEXTO
        );

        // -----------------------------------------------------
        // BOTÓN SALIR
        // -----------------------------------------------------

        btnCerrarSesion.setFocusPainted(
                false
        );

        // -----------------------------------------------------
        // AGREGAR COMPONENTES
        // -----------------------------------------------------

        usuarioPanel.add(
                lblUsuario
        );

        usuarioPanel.add(
                lblTiempoSesion
        );

        usuarioPanel.add(
                btnCerrarSesion
        );

        add(
                usuarioPanel,
                BorderLayout.EAST
        );
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public JButton getBtnCerrarSesion() {

        return btnCerrarSesion;
    }

    public JLabel getLblTiempoSesion() {

        return lblTiempoSesion;
    }
}
