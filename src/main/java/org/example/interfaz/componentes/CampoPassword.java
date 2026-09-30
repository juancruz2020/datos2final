package org.example.interfaz.componentes;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Dimensiones;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CampoPassword extends JPasswordField {

    public CampoPassword() {

        setFont(Fuentes.NORMAL);

        setForeground(Colores.TEXTO);

        setBackground(Colores.SUPERFICIE);

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
}