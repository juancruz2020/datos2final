package org.example.interfaz.principal.menu;

import javax.swing.*;
import java.awt.*;

public class MenuItem extends JPanel {

    private final String vista;

    private final JLabel lblIcono;
    private final JLabel lblTexto;

    public MenuItem(
            String icono,
            String texto,
            String vista
    ) {

        this.vista = vista;

        lblIcono = new JLabel(icono);
        lblTexto = new JLabel(texto);

        construir();
    }

    private void construir() {

        setLayout(
                new BorderLayout()
        );

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );

        setPreferredSize(
                new Dimension(
                        230,
                        48
                )
        );

        setBackground(
                new Color(15, 42, 68)
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        0,
                        10
                )
        );

        // ICONO

        lblIcono.setForeground(
                Color.WHITE
        );

        lblIcono.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        18
                )
        );

        // TEXTO

        lblTexto.setForeground(
                Color.WHITE
        );

        lblTexto.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        add(
                lblIcono,
                BorderLayout.WEST
        );

        add(
                lblTexto,
                BorderLayout.CENTER
        );

        setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    public String getVista() {
        return vista;
    }

    public void setExpandido(boolean expandido) {

        lblTexto.setVisible(
                expandido
        );

        if (expandido) {

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            15,
                            0,
                            10
                    )
            );

        } else {

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            25,
                            0,
                            10
                    )
            );
        }

        revalidate();
        repaint();
    }
}