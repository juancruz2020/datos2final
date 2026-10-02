package org.example.interfaz.principal;

import org.example.interfaz.principal.header.HeaderPanel;
import org.example.interfaz.principal.menu.MenuLateralPanel;

import org.example.interfaz.principal.vistas.DashboardPanel;

import org.example.interfaz.principal.vistas.MonitoreoPanel;
import org.example.interfaz.principal.vistas.MonitoreoPanelController;

import org.example.interfaz.principal.vistas.VistaVaciaPanel;

import org.example.interfaz.principal.vistas.AdministracionPanel;
import org.example.interfaz.principal.vistas.AdministracionPanelController;

import org.example.interfaz.principal.vistas.ClientesPanel;
import org.example.interfaz.principal.vistas.ClientesPanelController;

import org.example.interfaz.principal.vistas.EnviosPanel;
import org.example.interfaz.principal.vistas.EnviosPanelController;

import org.example.interfaz.principal.vistas.ContenedoresPanel;
import org.example.interfaz.principal.vistas.ContenedoresPanelController;

import org.example.interfaz.principal.vistas.SensoresPanel;
import org.example.interfaz.principal.vistas.SensoresPanelController;

import org.example.interfaz.principal.vistas.FacturacionPanel;
import org.example.interfaz.principal.vistas.FacturacionPanelController;

import org.example.interfaz.principal.vistas.TrazabilidadPanel;
import org.example.interfaz.principal.vistas.TrazabilidadPanelController;

import org.example.interfaz.tema.Colores;

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

        // =====================================================
        // DASHBOARD
        // =====================================================

        contenido.add(
                new DashboardPanel(),
                "DASHBOARD"
        );


        // =====================================================
        // CLIENTES
        // =====================================================

        ClientesPanel clientesPanel =
                new ClientesPanel();


        contenido.add(
                clientesPanel,
                "CLIENTES"
        );


        new ClientesPanelController(
                clientesPanel
        );


        // =====================================================
        // ENVÍOS
        // =====================================================

        EnviosPanel enviosPanel =
                new EnviosPanel();


        contenido.add(
                enviosPanel,
                "ENVIOS"
        );


        new EnviosPanelController(
                enviosPanel
        );


        // =====================================================
        // CONTENEDORES
        // =====================================================

        ContenedoresPanel contenedoresPanel =
                new ContenedoresPanel();


        contenido.add(
                contenedoresPanel,
                "CONTENEDORES"
        );


        new ContenedoresPanelController(
                contenedoresPanel
        );


        // =====================================================
        // SENSORES
        // =====================================================

        SensoresPanel sensoresPanel =
                new SensoresPanel();


        contenido.add(
                sensoresPanel,
                "SENSORES"
        );


        new SensoresPanelController(
                sensoresPanel
        );


        // =====================================================
        // MONITOREO
        // =====================================================

        MonitoreoPanel monitoreoPanel =
                new MonitoreoPanel();


        contenido.add(
                monitoreoPanel,
                "MONITOREO"
        );


        new MonitoreoPanelController(
                monitoreoPanel
        );


        // =====================================================
        // TRAZABILIDAD
        // =====================================================

        TrazabilidadPanel trazabilidadPanel =
                new TrazabilidadPanel();


        contenido.add(
                trazabilidadPanel,
                "TRAZABILIDAD"
        );


        new TrazabilidadPanelController(
                trazabilidadPanel
        );


        // =====================================================
        // COMUNICACIONES
        // =====================================================

        contenido.add(
                new VistaVaciaPanel(
                        "Comunicaciones"
                ),
                "COMUNICACIONES"
        );


        // =====================================================
        // FACTURACIÓN
        // =====================================================

        FacturacionPanel facturacionPanel =
                new FacturacionPanel();


        contenido.add(
                facturacionPanel,
                "FACTURACION"
        );


        new FacturacionPanelController(
                facturacionPanel
        );


        // =====================================================
        // REPORTES
        // =====================================================

        contenido.add(
                new VistaVaciaPanel(
                        "Reportes"
                ),
                "REPORTES"
        );


        // =====================================================
        // RIESGOS
        // =====================================================

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