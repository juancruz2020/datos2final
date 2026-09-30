package org.example.interfaz.principal;

import org.example.interfaz.tema.Colores;

import javax.swing.*;
import java.awt.*;

public class PrincipalFrame extends JFrame {

    private final PrincipalPanel panel;

    public PrincipalFrame(String usuario) {

        panel = new PrincipalPanel(usuario);

        configurarVentana();

        setContentPane(panel);

        new PrincipalController(panel);
    }

    private void configurarVentana() {

        setTitle("Logística Inteligente");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(1400, 850);

        setMinimumSize(
                new Dimension(1100, 700)
        );

        setLocationRelativeTo(null);

        setBackground(Colores.FONDO);
    }
}