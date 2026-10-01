package org.example.interfaz.principal;

import org.example.interfaz.principal.header.HeaderPanel;
import org.example.interfaz.principal.menu.MenuLateralPanel;
import org.example.interfaz.principal.vistas.DashboardPanel;
import org.example.interfaz.principal.vistas.MonitoreoPanel;
import org.example.interfaz.principal.vistas.MonitoreoPanelController;
import org.example.interfaz.principal.vistas.VistaVaciaPanel;
import org.example.interfaz.tema.Colores;
import org.example.interfaz.principal.vistas.AdministracionPanel;
import org.example.interfaz.principal.vistas.AdministracionPanelController;

import javax.swing.*;
import java.awt.*;

public class PrincipalPanel extends JPanel {

    private final String usuarioId;

    private final String nombreUsuario;

    private final CardLayout cardLayout;

    private final JPanel contenido;

    private final MenuLateralPanel menuLateral;

    private final HeaderPanel header;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrincipalPanel(
            String usuarioId,
            String nombreUsuario
    ) {

        this.usuarioId =
                usuarioId;

        this.nombreUsuario =
                nombreUsuario;


        cardLayout =
                new CardLayout();


        contenido =
                new JPanel(
                        cardLayout
                );


        menuLateral =
                new MenuLateralPanel();


        header =
                new HeaderPanel(
                        nombreUsuario
                );


        construir();
    }


    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(
                new BorderLayout()
        );


        setBackground(
                Colores.FONDO
        );


        // =====================================================
        // HEADER
        // =====================================================

        add(
                header,
                BorderLayout.NORTH
        );


        // =====================================================
        // MENÚ
        // =====================================================

        add(
                menuLateral,
                BorderLayout.WEST
        );


        // =====================================================
        // CONTENIDO
        // =====================================================

        construirContenido();


        add(
                contenido,
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // VISTAS
    // =========================================================

    private void construirContenido() {

        contenido.add(
                new DashboardPanel(),
                "DASHBOARD"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Clientes"
                ),
                "CLIENTES"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Envíos"
                ),
                "ENVIOS"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Contenedores"
                ),
                "CONTENEDORES"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Sensores"
                ),
                "SENSORES"
        );


        MonitoreoPanel monitoreoPanel =
                new MonitoreoPanel();


        contenido.add(
                monitoreoPanel,
                "MONITOREO"
        );


        new MonitoreoPanelController(
                monitoreoPanel
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Trazabilidad"
                ),
                "TRAZABILIDAD"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Comunicaciones"
                ),
                "COMUNICACIONES"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Facturación"
                ),
                "FACTURACION"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Reportes"
                ),
                "REPORTES"
        );


        contenido.add(
                new VistaVaciaPanel(
                        "Riesgos"
                ),
                "RIESGOS"
        );


        // =====================================================
        // ADMINISTRACIÓN
        // =====================================================

        AdministracionPanel administracionPanel =
                new AdministracionPanel();


        contenido.add(
                administracionPanel,
                "ADMINISTRACION"
        );


        new AdministracionPanelController(
                administracionPanel
        );


        // =====================================================
        // VISTA INICIAL
        // =====================================================

        cardLayout.show(
                contenido,
                "DASHBOARD"
        );
    }


    // =========================================================
    // NAVEGACIÓN
    // =========================================================

    public void mostrarVista(
            String nombre
    ) {

        cardLayout.show(
                contenido,
                nombre
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public MenuLateralPanel getMenuLateral() {

        return menuLateral;
    }


    public HeaderPanel getHeader() {

        return header;
    }


    public String getUsuarioId() {

        return usuarioId;
    }


    public String getNombreUsuario() {

        return nombreUsuario;
    }
}