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

    private final String usuario;

    private final CardLayout cardLayout;

    private final JPanel contenido;

    private final MenuLateralPanel menuLateral;

    private final HeaderPanel header;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrincipalPanel(
            String usuario
    ) {

        this.usuario =
                usuario;


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
                        usuario
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

        // -----------------------------------------------------
        // DASHBOARD
        // -----------------------------------------------------

        contenido.add(
                new DashboardPanel(),
                "DASHBOARD"
        );


        // -----------------------------------------------------
        // CLIENTES
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Clientes"
                ),
                "CLIENTES"
        );


        // -----------------------------------------------------
        // ENVÍOS
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Envíos"
                ),
                "ENVIOS"
        );


        // -----------------------------------------------------
        // CONTENEDORES
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Contenedores"
                ),
                "CONTENEDORES"
        );


        // -----------------------------------------------------
        // SENSORES
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Sensores"
                ),
                "SENSORES"
        );


        // -----------------------------------------------------
        // MONITOREO
        // -----------------------------------------------------

        MonitoreoPanel monitoreoPanel =
                new MonitoreoPanel();


        contenido.add(
                monitoreoPanel,
                "MONITOREO"
        );


        new MonitoreoPanelController(
                monitoreoPanel
        );


        // -----------------------------------------------------
        // TRAZABILIDAD
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Trazabilidad"
                ),
                "TRAZABILIDAD"
        );


        // -----------------------------------------------------
        // COMUNICACIONES
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Comunicaciones"
                ),
                "COMUNICACIONES"
        );


        // -----------------------------------------------------
        // FACTURACIÓN
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Facturación"
                ),
                "FACTURACION"
        );


        // -----------------------------------------------------
        // REPORTES
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Reportes"
                ),
                "REPORTES"
        );


        // -----------------------------------------------------
        // RIESGOS
        // -----------------------------------------------------

        contenido.add(
                new VistaVaciaPanel(
                        "Riesgos"
                ),
                "RIESGOS"
        );


// -----------------------------------------------------
// ADMINISTRACIÓN
// -----------------------------------------------------

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


    public String getUsuario() {

        return usuario;
    }
}