package org.example.interfaz.componentes;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Dimensiones;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import java.awt.*;

public class BotonPrincipal extends JButton {

    public BotonPrincipal(String texto) {

        super(texto);

        setFont(Fuentes.BOTON);

        setForeground(Color.WHITE);

        setBackground(Colores.PRIMARIO);

        setFocusPainted(false);

        setBorderPainted(false);

        setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        setPreferredSize(
                new Dimension(
                        300,
                        Dimensiones.BOTON_ALTO
                )
        );
    }
}