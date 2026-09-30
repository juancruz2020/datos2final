package org.example.interfaz.principal.header;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    private final String usuario;

    public HeaderPanel(String usuario) {

        this.usuario = usuario;

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

        JPanel usuarioPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        usuarioPanel.setOpaque(false);

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

        JButton btnCerrarSesion =
                new JButton(
                        "Salir"
                );

        btnCerrarSesion.setFocusPainted(false);

        usuarioPanel.add(
                lblUsuario
        );

        usuarioPanel.add(
                btnCerrarSesion
        );

        add(
                usuarioPanel,
                BorderLayout.EAST
        );
    }
}