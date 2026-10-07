package org.example.interfaz.principal;

import org.example.interfaz.principal.header.HeaderPanel;
import org.example.interfaz.principal.menu.MenuLateralPanel;
import org.example.interfaz.principal.vistas.*;
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

        DashboardPanel dashboardPanel =
                new DashboardPanel();


        contenido.add(
                dashboardPanel,
                "DASHBOARD"
        );


        new DashboardController(
                dashboardPanel
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
        // EVENTOS LOGÍSTICOS
        // =====================================================

        EventosLogisticosPanel eventosLogisticosPanel =
                new EventosLogisticosPanel();


        contenido.add(
                eventosLogisticosPanel,
                "EVENTOS_LOGISTICOS"
        );


        new EventosLogisticosPanelController(
                eventosLogisticosPanel
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
        // VEHÍCULOS
        // =====================================================

        VehiculosPanel vehiculosPanel =
                new VehiculosPanel();


        contenido.add(
                vehiculosPanel,
                "VEHICULOS"
        );


        new VehiculosPanelController(
                vehiculosPanel
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

        ComunicacionesPanel comunicacionesPanel =
                new ComunicacionesPanel();


        contenido.add(
                comunicacionesPanel,
                "COMUNICACIONES"
        );


        new ComunicacionesPanelController(
                comunicacionesPanel,
                usuarioId
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

        ReportesPanel reportesPanel =
                new ReportesPanel();


        contenido.add(
                reportesPanel,
                "REPORTES"
        );


        new ReportesPanelController(
                reportesPanel
        );


        // =====================================================
        // RIESGOS - INCIDENTES
        // =====================================================

        IncidentesPanel incidentesPanel =
                new IncidentesPanel();


        contenido.add(
                incidentesPanel,
                "RIESGOS"
        );


        new IncidentesPanelController(
                incidentesPanel
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