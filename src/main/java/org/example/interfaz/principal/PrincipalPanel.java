
package org.example.interfaz.principal;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.interfaz.principal.header.HeaderPanel;
import org.example.interfaz.principal.menu.MenuLateralPanel;
import org.example.interfaz.principal.vistas.*;
import org.example.interfaz.tema.Colores;

import javax.swing.*;
import java.awt.*;

public class PrincipalPanel extends JPanel {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final String usuarioId;

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

        this.usuarioId = usuarioId;


        cardLayout = new CardLayout();
        contenido = new JPanel(cardLayout);

        menuLateral = new MenuLateralPanel();
        header = new HeaderPanel(nombreUsuario);

        construir();
    }

    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(new BorderLayout());
        setBackground(Colores.FONDO);

        add(header, BorderLayout.NORTH);
        add(menuLateral, BorderLayout.WEST);

        construirContenido();

        add(contenido, BorderLayout.CENTER);
    }

    // =========================================================
    // VERIFICAR PERMISOS
    // =========================================================

    private boolean tienePermiso(String vista) {

        return mongo.puedeAccederVista(
                usuarioId,
                vista
        );
    }

    // =========================================================
    // CONSTRUIR VISTAS
    // =========================================================

    private void construirContenido() {

        // =====================================================
        // DASHBOARD
        // =====================================================

        if (tienePermiso("DASHBOARD")) {

            DashboardPanel dashboardPanel =
                    new DashboardPanel();

            contenido.add(
                    dashboardPanel,
                    "DASHBOARD"
            );

            new DashboardController(
                    dashboardPanel
            );
        }

        // =====================================================
        // CLIENTES
        // =====================================================

        if (tienePermiso("CLIENTES")) {

            ClientesPanel clientesPanel =
                    new ClientesPanel();

            contenido.add(
                    clientesPanel,
                    "CLIENTES"
            );

            new ClientesPanelController(
                    clientesPanel
            );
        }

        if (tienePermiso("PROVEEDOR")) {
            JPanel proveedorPanel = new JPanel();
            proveedorPanel.setBackground(Colores.FONDO);
            contenido.add(proveedorPanel, "PROVEEDOR");
        }

        // =====================================================
        // ENVIOS
        // =====================================================

        if (tienePermiso("ENVIOS")) {

            EnviosPanel enviosPanel =
                    new EnviosPanel();

            contenido.add(
                    enviosPanel,
                    "ENVIOS"
            );

            new EnviosPanelController(
                    enviosPanel,
                    usuarioId
            );
        }

        // =====================================================
        // EVENTOS LOGISTICOS
        // =====================================================

        if (tienePermiso("EVENTOS_LOGISTICOS")) {

            EventosLogisticosPanel eventosLogisticosPanel =
                    new EventosLogisticosPanel();

            contenido.add(
                    eventosLogisticosPanel,
                    "EVENTOS_LOGISTICOS"
            );

            new EventosLogisticosPanelController(
                    eventosLogisticosPanel,
                    usuarioId
            );
        }

        // =====================================================
        // CONTENEDORES
        // =====================================================

        if (tienePermiso("CONTENEDORES")) {

            ContenedoresPanel contenedoresPanel =
                    new ContenedoresPanel();

            contenido.add(
                    contenedoresPanel,
                    "CONTENEDORES"
            );

            new ContenedoresPanelController(contenedoresPanel, usuarioId);
        }

        // =====================================================
        // VEHICULOS
        // =====================================================

        if (tienePermiso("VEHICULOS")) {

            VehiculosPanel vehiculosPanel =
                    new VehiculosPanel();

            contenido.add(
                    vehiculosPanel,
                    "VEHICULOS"
            );

                new VehiculosPanelController(vehiculosPanel, usuarioId);
        }

        // =====================================================
        // SENSORES
        // =====================================================

        if (tienePermiso("SENSORES")) {

            SensoresPanel sensoresPanel =
                    new SensoresPanel();

            contenido.add(
                    sensoresPanel,
                    "SENSORES"
            );

            new SensoresPanelController(sensoresPanel, usuarioId);
        }

        // =====================================================
        // MONITOREO
        // =====================================================

        if (tienePermiso("MONITOREO")) {

            MonitoreoPanel monitoreoPanel =
                    new MonitoreoPanel();

            contenido.add(
                    monitoreoPanel,
                    "MONITOREO"
            );

            new MonitoreoPanelController(
                    monitoreoPanel
            );
        }

        // =====================================================
        // SEGUIMIENTO GPS
        // =====================================================

        if (tienePermiso("SEGUIMIENTO_GPS")) {

            SeguimientoGPSPanel seguimientoGPSPanel =
                    new SeguimientoGPSPanel();

            contenido.add(
                    seguimientoGPSPanel,
                    "SEGUIMIENTO_GPS"
            );

            new SeguimientoGPSPanelController(
                    seguimientoGPSPanel
            );
        }

        if (tienePermiso("SEGUIMIENTO_ENVIO")) {
            SeguimientoEnvioPanel panel = new SeguimientoEnvioPanel();
            contenido.add(panel, "SEGUIMIENTO_ENVIO");
            new SeguimientoEnvioPanelController(panel, usuarioId);
        }

        // =====================================================
        // TRAZABILIDAD
        // =====================================================

        if (tienePermiso("TRAZABILIDAD")) {

            TrazabilidadPanel trazabilidadPanel =
                    new TrazabilidadPanel();

            contenido.add(
                    trazabilidadPanel,
                    "TRAZABILIDAD"
            );

new TrazabilidadPanelController(trazabilidadPanel, usuarioId);
        }

        // =====================================================
        // COMUNICACIONES
        // =====================================================

        if (tienePermiso("COMUNICACIONES")) {

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
        }

        // =====================================================
        // FACTURACION
        // =====================================================

        if (tienePermiso("FACTURACION")) {

            FacturacionPanel facturacionPanel =
                    new FacturacionPanel();

            contenido.add(
                    facturacionPanel,
                    "FACTURACION"
            );

                new FacturacionPanelController(
                        facturacionPanel,
                        usuarioId
                );
        }

        if (tienePermiso("PAGOS")) {
            JPanel pagosPanel = new JPanel();
            pagosPanel.setBackground(Colores.FONDO);
            contenido.add(pagosPanel, "PAGOS");
        }

        // =====================================================
        // CUENTAS CORRIENTES
        // =====================================================

        if (tienePermiso("CUENTAS_CORRIENTES")) {

            CuentasCorrientesPanel cuentasCorrientesPanel =
                    new CuentasCorrientesPanel();

            contenido.add(
                    cuentasCorrientesPanel,
                    "CUENTAS_CORRIENTES"
            );

            new CuentasCorrientesPanelController(
                    cuentasCorrientesPanel
            );
        }

        // =====================================================
        // REPORTES
        // =====================================================

        if (tienePermiso("REPORTES")) {

            ReportesPanel reportesPanel =
                    new ReportesPanel();

            contenido.add(
                    reportesPanel,
                    "REPORTES"
            );

            new ReportesPanelController(
                    reportesPanel,
                    usuarioId
            );
        }

        // =====================================================
        // RIESGOS - INCIDENTES
        // =====================================================

        if (tienePermiso("RIESGOS")) {

            IncidentesPanel incidentesPanel =
                    new IncidentesPanel();

            contenido.add(
                    incidentesPanel,
                    "RIESGOS"
            );

                new IncidentesPanelController(
                        incidentesPanel,
                        usuarioId
                );
        }

        // =====================================================
        // ADMINISTRACION
        // =====================================================

        if (tienePermiso("ADMINISTRACION")) {

            AdministracionPanel administracionPanel =
                    new AdministracionPanel();

            contenido.add(
                    administracionPanel,
                    "ADMINISTRACION"
            );

            new AdministracionPanelController(
                    administracionPanel
            );
        }

        // =====================================================
        // VISTA INICIAL SEGUN PERMISOS
        // =====================================================

        if (tienePermiso("DASHBOARD")) {

            mostrarVista("DASHBOARD");

        } else if (tienePermiso("ENVIOS")) {

            mostrarVista("ENVIOS");

        } else {

            throw new SecurityException(
                    "El usuario no tiene una vista inicial permitida."
            );
        }
    }

    // =========================================================
    // NAVEGACION
    // =========================================================

    public void mostrarVista(String nombre) {

        if (!tienePermiso(nombre)) {

            throw new SecurityException(
                    "No tenés permisos para acceder a esta sección."
            );
        }

        cardLayout.show(
                contenido,
                nombre
        );

        header.setTitulo(
                obtenerTituloVista(nombre)
        );
    }

    // =========================================================
    // TITULOS DE LAS VISTAS
    // =========================================================

    private String obtenerTituloVista(String vista) {

        return switch (vista) {

            case "DASHBOARD" -> "Dashboard";

            case "CLIENTES" -> "Clientes";

            case "PROVEEDOR" -> "Proveedor";

            case "ENVIOS" -> "Envíos";

            case "EVENTOS_LOGISTICOS" -> "Eventos Logísticos";

            case "CONTENEDORES" -> "Contenedores";

            case "VEHICULOS" -> "Vehículos";

            case "SENSORES" -> "Sensores";

            case "MONITOREO" -> "Monitoreo";

            case "SEGUIMIENTO",
                 "SEGUIMIENTO_GPS" -> "Seguimiento GPS";

            case "SEGUIMIENTO_ENVIO" -> "Seguimiento por envío";

            case "TRAZABILIDAD" -> "Trazabilidad";

            case "COMUNICACIONES" -> "Comunicaciones";

            case "FACTURACION" -> "Facturación";

            case "PAGOS" -> "Pagos";

            case "CUENTAS_CORRIENTES" -> "Cuentas Corrientes";

            case "REPORTES" -> "Reportes";

            case "RIESGOS" -> "Riesgos";

            case "ADMINISTRACION" -> "Administración";

            default -> vista;
        };
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

}
