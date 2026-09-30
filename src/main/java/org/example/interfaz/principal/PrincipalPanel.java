package org.example.interfaz.principal;

import org.example.interfaz.principal.header.HeaderPanel;
import org.example.interfaz.principal.menu.MenuLateralPanel;
import org.example.interfaz.principal.vistas.DashboardPanel;
import org.example.interfaz.principal.vistas.VistaVaciaPanel;
import org.example.interfaz.tema.Colores;

import javax.swing.*;
import java.awt.*;

public class PrincipalPanel extends JPanel {

    private final String usuario;

    private final CardLayout cardLayout;
    private final JPanel contenido;

    private final MenuLateralPanel menuLateral;
    private final HeaderPanel header;

    public PrincipalPanel(String usuario) {

        this.usuario = usuario;

        cardLayout = new CardLayout();

        contenido = new JPanel(cardLayout);

        menuLateral = new MenuLateralPanel();
        header = new HeaderPanel(usuario);

        construir();
    }

    private void construir() {

        setLayout(new BorderLayout());

        setBackground(Colores.FONDO);

        // HEADER
        add(
                header,
                BorderLayout.NORTH
        );

        // MENÚ
        add(
                menuLateral,
                BorderLayout.WEST
        );

        // CONTENIDO
        construirContenido();

        add(
                contenido,
                BorderLayout.CENTER
        );
    }

    private void construirContenido() {

        contenido.add(
                new DashboardPanel(),
                "DASHBOARD"
        );

        contenido.add(
                new VistaVaciaPanel("Clientes"),
                "CLIENTES"
        );

        contenido.add(
                new VistaVaciaPanel("Envíos"),
                "ENVIOS"
        );

        contenido.add(
                new VistaVaciaPanel("Contenedores"),
                "CONTENEDORES"
        );

        contenido.add(
                new VistaVaciaPanel("Sensores"),
                "SENSORES"
        );

        contenido.add(
                new VistaVaciaPanel("Monitoreo IoT"),
                "MONITOREO"
        );

        contenido.add(
                new VistaVaciaPanel("Trazabilidad"),
                "TRAZABILIDAD"
        );

        contenido.add(
                new VistaVaciaPanel("Comunicaciones"),
                "COMUNICACIONES"
        );

        contenido.add(
                new VistaVaciaPanel("Facturación"),
                "FACTURACION"
        );

        contenido.add(
                new VistaVaciaPanel("Reportes"),
                "REPORTES"
        );

        contenido.add(
                new VistaVaciaPanel("Riesgos"),
                "RIESGOS"
        );

        contenido.add(
                new VistaVaciaPanel("Administración"),
                "ADMINISTRACION"
        );

        cardLayout.show(
                contenido,
                "DASHBOARD"
        );
    }

    public void mostrarVista(String nombre) {

        cardLayout.show(
                contenido,
                nombre
        );
    }

    public MenuLateralPanel getMenuLateral() {
        return menuLateral;
    }

    public HeaderPanel getHeader() {
        return header;
    }

    public String getUsuario() {
        return usuario;
    }
}