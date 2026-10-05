package org.example.interfaz.componentes;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Dimensiones;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CampoPassword extends JPanel {

    private final JPasswordField campo;
    private final JButton botonMostrar;

    private boolean visible = false;

    public CampoPassword() {

        setLayout(new BorderLayout());

        setBackground(Colores.SUPERFICIE);

        // =============================================
        // CAMPO PASSWORD
        // =============================================

        campo = new JPasswordField();

        campo.setFont(Fuentes.NORMAL);
        campo.setForeground(Colores.TEXTO);
        campo.setBackground(Colores.SUPERFICIE);

        campo.setEchoChar('•');

        // El borde lo ponemos en el panel exterior
        campo.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        10,
                        0,
                        5
                )
        );


        // =============================================
        // BOTÓN OJO
        // =============================================

        botonMostrar = new JButton();

        botonMostrar.setIcon(
                new IconoOjo(false)
        );

        botonMostrar.setPreferredSize(
                new Dimension(
                        40,
                        Dimensiones.CAMPO_ALTO
                )
        );

        botonMostrar.setFocusPainted(false);
        botonMostrar.setBorderPainted(false);
        botonMostrar.setContentAreaFilled(false);
        botonMostrar.setOpaque(false);

        botonMostrar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );


        botonMostrar.addActionListener(
                e -> cambiarVisibilidad()
        );


        // =============================================
        // AGREGAR COMPONENTES
        // =============================================

        add(campo, BorderLayout.CENTER);

        add(botonMostrar, BorderLayout.EAST);


        // =============================================
        // BORDE EXTERIOR
        // =============================================

        setBorder(
                new LineBorder(
                        Colores.BORDE,
                        1,
                        true
                )
        );


        setPreferredSize(
                new Dimension(
                        300,
                        Dimensiones.CAMPO_ALTO
                )
        );
    }


    // =============================================
    // MOSTRAR / OCULTAR
    // =============================================

    private void cambiarVisibilidad() {

        visible = !visible;

        if (visible) {

            campo.setEchoChar((char) 0);

        } else {

            campo.setEchoChar('•');
        }

        botonMostrar.setIcon(
                new IconoOjo(visible)
        );

        botonMostrar.repaint();
    }


    // =============================================
    // GET PASSWORD
    // =============================================

    public char[] getPassword() {

        return campo.getPassword();
    }


    public String getText() {

        return new String(
                campo.getPassword()
        );
    }


    public void setText(String texto) {

        campo.setText(texto);
    }


    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        campo.setEnabled(enabled);
        botonMostrar.setEnabled(enabled);
    }


    // =============================================
    // ICONO OJO
    // =============================================

    private static class IconoOjo implements Icon {

        private final boolean tachado;

        public IconoOjo(boolean tachado) {

            this.tachado = tachado;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            // Color del icono
            g2.setColor(
                    Colores.TEXTO_SECUNDARIO
            );

            g2.setStroke(
                    new BasicStroke(
                            1.8f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );


            // =========================================
            // OJO
            // =========================================

            int ancho = 20;
            int alto = 12;

            int centroX = x + 10;
            int centroY = y + 10;


            // Parte exterior del ojo
            g2.drawArc(
                    x,
                    y + 4,
                    ancho,
                    alto,
                    0,
                    180
            );

            g2.drawArc(
                    x,
                    y + 4,
                    ancho,
                    alto,
                    180,
                    180
            );


            // Pupila
            g2.fillOval(
                    x + 7,
                    y + 7,
                    6,
                    6
            );


            // =========================================
            // TACHADO
            // =========================================

            if (tachado) {

                g2.drawLine(
                        x - 2,
                        y + 2,
                        x + 22,
                        y + 20
                );
            }


            g2.dispose();
        }


        @Override
        public int getIconWidth() {

            return 20;
        }


        @Override
        public int getIconHeight() {

            return 20;
        }
    }
}