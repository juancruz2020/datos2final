package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import java.awt.*;

public class VistaVaciaPanel extends JPanel {

    public VistaVaciaPanel(String nombre) {

        setLayout(
                new GridBagLayout()
        );

        setBackground(
                Colores.FONDO
        );

        JLabel label =
                new JLabel(
                        nombre
                );

        label.setFont(
                Fuentes.TITULO
        );

        label.setForeground(
                Colores.TEXTO
        );

        add(label);
    }
}