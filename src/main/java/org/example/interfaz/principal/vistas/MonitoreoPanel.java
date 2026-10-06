package org.example.interfaz.principal.vistas;

import org.example.interfaz.componentes.BotonPrincipal;
import org.example.interfaz.componentes.CampoFecha;
import org.example.interfaz.componentes.CampoFechaHora;
import org.example.interfaz.componentes.CampoTexto;
import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class MonitoreoPanel extends JPanel {

    // =========================================================
    // CONSULTAS
    // =========================================================

    private final JComboBox<String> comboConsulta;

    private final JPanel parametros;

    private final CardLayout parametrosLayout;

    private final JTable tabla;

    private final JLabel estado;


    // =========================================================
    // CAMPOS DE CONSULTA
    // =========================================================

    // -------------------------
    // HISTORIAL
    // -------------------------

    private final JComboBox<String> historialSensorId =
            new JComboBox<>();

    private final CampoFecha historialFecha =
            new CampoFecha();


    // -------------------------
    // HORARIOS
    // -------------------------

    private final JComboBox<String> horariosSensorId =
            new JComboBox<>();

    private final CampoFecha horariosFecha =
            new CampoFecha();

    private final CampoFechaHora horariosDesde =
            new CampoFechaHora();

    private final CampoFechaHora horariosHasta =
            new CampoFechaHora();


    // -------------------------
    // TEMPERATURAS
    // -------------------------

    private final JComboBox<String> temperaturasSensorId =
            new JComboBox<>();

    private final CampoFecha temperaturasFecha =
            new CampoFecha();


    // -------------------------
    // BATERÍA
    // -------------------------

    private final JComboBox<String> bateriaSensorId =
            new JComboBox<>();

    private final CampoFecha bateriaFecha =
            new CampoFecha();


    // -------------------------
    // GPS
    // -------------------------

    private final JComboBox<String> gpsSensorId =
            new JComboBox<>();

    private final CampoFecha gpsFecha =
            new CampoFecha();


    // -------------------------
    // REGIÓN
    // -------------------------

    private final JTextField region =
            new CampoTexto();

    private final CampoFecha regionDesde =
            new CampoFecha();

    private final CampoFecha regionHasta =
            new CampoFecha();


    // -------------------------
    // PAÍS
    // -------------------------

    private final JTextField pais =
            new CampoTexto();

    private final CampoFecha paisDesde =
            new CampoFecha();

    private final CampoFecha paisHasta =
            new CampoFecha();


    // =========================================================
    // INSERCIÓN DE LECTURAS
    // =========================================================

    private final JComboBox<String> iSensorId =
            new JComboBox<>();

    private final CampoFecha iFechaDia =
            new CampoFecha();

    private final CampoFechaHora iFechaHora =
            new CampoFechaHora();

    private final JComboBox<String> iContenedorId =
            new JComboBox<>();

    private final JTextField iTemperatura =
            new CampoTexto();

    private final JTextField iHumedad =
            new CampoTexto();

    private final JTextField iVibracion =
            new CampoTexto();

    private final JTextField iLatitud =
            new CampoTexto();

    private final JTextField iLongitud =
            new CampoTexto();

    private final JTextField iBateria =
            new CampoTexto();

    private final JTextField iPais =
            new CampoTexto();

    private final JTextField iRegion =
            new CampoTexto();


    // =========================================================
    // BOTONES
    // =========================================================

    private final JButton ejecutarConsulta =
            new BotonPrincipal(
                    "Ejecutar consulta"
            );

    private final JButton insertarLectura =
            new BotonPrincipal(
                    "Insertar lectura"
            );

    private final JButton crearTablas =
            new BotonPrincipal(
                    "Crear tablas"
            );

    private final JButton cargarPrueba =
            new BotonPrincipal(
                    "Cargar datos de prueba"
            );


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MonitoreoPanel() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                Colores.FONDO
        );

        setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );


        // =====================================================
        // COMBO CONSULTAS
        // =====================================================

        comboConsulta =
                new JComboBox<>(
                        new String[]{
                                "Historial de sensor",
                                "Lecturas entre horarios",
                                "Temperaturas",
                                "Batería",
                                "Posiciones GPS",
                                "Métricas por región",
                                "Métricas por país",
                                "Todas las lecturas",
                                "Todas las métricas por región",
                                "Todas las métricas por país"
                        }
                );

        comboConsulta.setPreferredSize(
                new Dimension(
                        350,
                        38
                )
        );


        // =====================================================
        // CARD LAYOUT
        // =====================================================

        parametrosLayout =
                new CardLayout();

        parametros =
                new JPanel(
                        parametrosLayout
                );

        parametros.setOpaque(false);


        // =====================================================
        // TABLA
        // =====================================================

        tabla =
                new JTable();


        // =====================================================
        // ESTADO
        // =====================================================

        estado =
                new JLabel(
                        "Listo para realizar una consulta."
                );


        construir();
    }


    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(false);


        JLabel titulo =
                new JLabel(
                        "Monitoreo IoT"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );


        JLabel subtitulo =
                new JLabel(
                        "Consultas, métricas y lecturas almacenadas en Cassandra"
                );

        subtitulo.setFont(
                Fuentes.SUBTITULO
        );

        subtitulo.setForeground(
                Colores.TEXTO_SECUNDARIO
        );


        JPanel textos =
                new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );


        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(5)
        );

        textos.add(subtitulo);


        cabecera.add(
                textos,
                BorderLayout.WEST
        );


        add(
                cabecera,
                BorderLayout.NORTH
        );


        JTabbedPane tabs =
                new JTabbedPane();


        tabs.addTab(
                "Consultas",
                construirConsultas()
        );


        tabs.addTab(
                "Insertar datos",
                construirInserciones()
        );


        tabs.addTab(
                "Administración",
                construirAdministracion()
        );


        add(
                tabs,
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // CONSULTAS
    // =========================================================

    private JPanel construirConsultas() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        panel.setOpaque(false);

        panel.setBorder(
                new EmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );


        JPanel filtros =
                tarjeta();

        filtros.setLayout(
                new BorderLayout(
                        0,
                        15
                )
        );


        JPanel selector =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        selector.setOpaque(false);


        JLabel tipo =
                label(
                        "Tipo de consulta"
                );


        selector.add(
                tipo,
                BorderLayout.WEST
        );


        selector.add(
                comboConsulta,
                BorderLayout.CENTER
        );


        ejecutarConsulta.setPreferredSize(
                new Dimension(
                        180,
                        38
                )
        );


        selector.add(
                ejecutarConsulta,
                BorderLayout.EAST
        );


        comboConsulta.addActionListener(
                e -> actualizarFormularioConsulta()
        );


        filtros.add(
                selector,
                BorderLayout.NORTH
        );


        filtros.add(
                parametros,
                BorderLayout.CENTER
        );


        crearFormulariosConsulta();


        tabla.setRowHeight(
                28
        );

        tabla.setFillsViewportHeight(
                true
        );


        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        Colores.BORDE
                )
        );


        estado.setFont(
                Fuentes.NORMAL
        );

        estado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );


        panel.add(
                filtros,
                BorderLayout.NORTH
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        panel.add(
                estado,
                BorderLayout.SOUTH
        );


        return panel;
    }


    // =========================================================
    // CREAR FORMULARIOS
    // =========================================================

    private void crearFormulariosConsulta() {

        parametros.add(
                formulario(
                        campo(
                                "Sensor ID",
                                historialSensorId
                        ),

                        campo(
                                "Fecha",
                                historialFecha
                        )
                ),
                "HISTORIAL"
        );


        parametros.add(
                formulario(
                        campo(
                                "Sensor ID",
                                horariosSensorId
                        ),

                        campo(
                                "Fecha",
                                horariosFecha
                        ),

                        campo(
                                "Desde",
                                horariosDesde
                        ),

                        campo(
                                "Hasta",
                                horariosHasta
                        )
                ),
                "HORARIOS"
        );


        parametros.add(
                formulario(
                        campo(
                                "Sensor ID",
                                temperaturasSensorId
                        ),

                        campo(
                                "Fecha",
                                temperaturasFecha
                        )
                ),
                "TEMPERATURAS"
        );


        parametros.add(
                formulario(
                        campo(
                                "Sensor ID",
                                bateriaSensorId
                        ),

                        campo(
                                "Fecha",
                                bateriaFecha
                        )
                ),
                "BATERIA"
        );


        parametros.add(
                formulario(
                        campo(
                                "Sensor ID",
                                gpsSensorId
                        ),

                        campo(
                                "Fecha",
                                gpsFecha
                        )
                ),
                "GPS"
        );


        parametros.add(
                formulario(
                        campo(
                                "Región",
                                region
                        ),

                        campo(
                                "Desde",
                                regionDesde
                        ),

                        campo(
                                "Hasta",
                                regionHasta
                        )
                ),
                "REGION"
        );


        parametros.add(
                formulario(
                        campo(
                                "País",
                                pais
                        ),

                        campo(
                                "Desde",
                                paisDesde
                        ),

                        campo(
                                "Hasta",
                                paisHasta
                        )
                ),
                "PAIS"
        );


        parametros.add(
                formulario(),
                "TODAS_LECTURAS"
        );


        parametros.add(
                formulario(),
                "TODAS_REGION"
        );


        parametros.add(
                formulario(),
                "TODAS_PAIS"
        );


        parametrosLayout.show(
                parametros,
                "HISTORIAL"
        );
    }


    // =========================================================
    // ACTUALIZAR CONSULTA
    // =========================================================

    private void actualizarFormularioConsulta() {

        String seleccion =
                (String)
                        comboConsulta.getSelectedItem();

        String vista;


        switch (seleccion) {

            case "Historial de sensor":
                vista = "HISTORIAL";
                break;

            case "Lecturas entre horarios":
                vista = "HORARIOS";
                break;

            case "Temperaturas":
                vista = "TEMPERATURAS";
                break;

            case "Batería":
                vista = "BATERIA";
                break;

            case "Posiciones GPS":
                vista = "GPS";
                break;

            case "Métricas por región":
                vista = "REGION";
                break;

            case "Métricas por país":
                vista = "PAIS";
                break;

            case "Todas las lecturas":
                vista = "TODAS_LECTURAS";
                break;

            case "Todas las métricas por región":
                vista = "TODAS_REGION";
                break;

            case "Todas las métricas por país":
                vista = "TODAS_PAIS";
                break;

            default:
                vista = "HISTORIAL";
        }


        parametrosLayout.show(
                parametros,
                vista
        );

        parametros.revalidate();
        parametros.repaint();
    }


    // =========================================================
    // FORMULARIO VERTICAL
    // =========================================================

    private JPanel formulario(
            JComponent... componentes
    ) {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setOpaque(false);

        panel.setBorder(
                new EmptyBorder(
                        5,
                        5,
                        5,
                        5
                )
        );


        for (
                JComponent componente :
                componentes
        ) {

            componente.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );


            panel.add(
                    componente
            );


            panel.add(
                    Box.createVerticalStrut(
                            12
                    )
            );
        }


        return panel;
    }


    // =========================================================
    // CAMPO
    // =========================================================

    private JPanel campo(
            String nombre,
            JComponent componente
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                6
                        )
                );

        panel.setOpaque(false);

        panel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        panel.setPreferredSize(
                new Dimension(
                        700,
                        70
                )
        );


        panel.setMinimumSize(
                new Dimension(
                        250,
                        70
                )
        );


        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        70
                )
        );


        JLabel label =
                label(
                        nombre
                );


        panel.add(
                label,
                BorderLayout.NORTH
        );


        JPanel contenedorCampo =
                new JPanel(
                        new BorderLayout()
                );

        contenedorCampo.setOpaque(true);

        contenedorCampo.setBackground(
                Color.WHITE
        );

        contenedorCampo.setBorder(
                new LineBorder(
                        new Color(
                                180,
                                180,
                                180
                        ),
                        1,
                        true
                )
        );


        contenedorCampo.setPreferredSize(
                new Dimension(
                        500,
                        40
                )
        );


        contenedorCampo.setMinimumSize(
                new Dimension(
                        200,
                        40
                )
        );


        contenedorCampo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );


        componente.setOpaque(true);

        componente.setBackground(
                Color.WHITE
        );

        componente.setForeground(
                Color.DARK_GRAY
        );

        componente.setFont(
                Fuentes.NORMAL
        );


        componente.setPreferredSize(
                new Dimension(
                        500,
                        38
                )
        );


        componente.setMinimumSize(
                new Dimension(
                        150,
                        38
                )
        );


        componente.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        if (componente instanceof JTextField) {

            JTextField textField =
                    (JTextField) componente;

            textField.setBorder(
                    new EmptyBorder(
                            5,
                            10,
                            5,
                            10
                    )
            );

            textField.setEditable(true);

            textField.setEnabled(true);
        }


        contenedorCampo.add(
                componente,
                BorderLayout.CENTER
        );


        panel.add(
                contenedorCampo,
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // INSERTAR
    // =========================================================

    private JPanel construirInserciones() {

        JPanel contenedor =
                new JPanel(
                        new BorderLayout()
                );

        contenedor.setOpaque(false);

        contenedor.setBorder(
                new EmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );


        JTabbedPane tabs =
                new JTabbedPane();


        tabs.addTab(
                "Lectura de sensor",
                scrollFormulario(
                        formulario(

                                campo(
                                        "Sensor ID",
                                        iSensorId
                                ),

                                campo(
                                        "Fecha día",
                                        iFechaDia
                                ),

                                campo(
                                        "Fecha y hora",
                                        iFechaHora
                                ),

                                campo(
                                        "Contenedor ID",
                                        iContenedorId
                                ),

                                campo(
                                        "Temperatura",
                                        iTemperatura
                                ),

                                campo(
                                        "Humedad",
                                        iHumedad
                                ),

                                campo(
                                        "Vibración",
                                        iVibracion
                                ),

                                campo(
                                        "Latitud",
                                        iLatitud
                                ),

                                campo(
                                        "Longitud",
                                        iLongitud
                                ),

                                campo(
                                        "Batería",
                                        iBateria
                                ),

                                campo(
                                        "País",
                                        iPais
                                ),

                                campo(
                                        "Región",
                                        iRegion
                                ),

                                insertarLectura
                        )
                )
        );


        contenedor.add(
                tabs,
                BorderLayout.CENTER
        );


        return contenedor;
    }


    // =========================================================
    // ADMINISTRACIÓN
    // =========================================================

    private JPanel construirAdministracion() {

        JPanel panel =
                new JPanel(
                        new GridBagLayout()
                );

        panel.setOpaque(false);


        JPanel tarjeta =
                tarjeta();


        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel titulo =
                label(
                        "Administración de Cassandra"
                );

        titulo.setFont(
                Fuentes.LABEL
        );


        JLabel descripcion =
                new JLabel(
                        "<html>Operaciones de preparación y datos de prueba del módulo de monitoreo.</html>"
                );

        descripcion.setFont(
                Fuentes.NORMAL
        );

        descripcion.setForeground(
                Colores.TEXTO_SECUNDARIO
        );


        tarjeta.add(
                titulo
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        10
                )
        );


        tarjeta.add(
                descripcion
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        25
                )
        );


        tarjeta.add(
                crearTablas
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        10
                )
        );


        tarjeta.add(
                cargarPrueba
        );


        panel.add(
                tarjeta
        );


        return panel;
    }


    // =========================================================
    // SCROLL FORMULARIO
    // =========================================================

    private JPanel scrollFormulario(
            JPanel formulario
    ) {

        JPanel contenedor =
                new JPanel(
                        new BorderLayout()
                );

        contenedor.setOpaque(false);


        JScrollPane scroll =
                new JScrollPane(
                        formulario
                );


        scroll.setBorder(
                null
        );


        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );


        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );


        contenedor.add(
                scroll,
                BorderLayout.CENTER
        );


        return contenedor;
    }


    // =========================================================
    // TARJETA
    // =========================================================

    private JPanel tarjeta() {

        JPanel panel =
                new JPanel();


        panel.setBackground(
                Colores.SUPERFICIE
        );


        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),

                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );


        return panel;
    }


    // =========================================================
    // LABEL
    // =========================================================

    private JLabel label(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );


        label.setFont(
                Fuentes.LABEL
        );


        label.setForeground(
                Colores.TEXTO
        );


        return label;
    }


    // =========================================================
    // CARGAR IDS DESDE MONGODB
    // =========================================================

    public void cargarIds(
            List<String> sensores,
            List<String> contenedores
    ) {

        cargarCombo(
                historialSensorId,
                sensores
        );

        cargarCombo(
                horariosSensorId,
                sensores
        );

        cargarCombo(
                temperaturasSensorId,
                sensores
        );

        cargarCombo(
                bateriaSensorId,
                sensores
        );

        cargarCombo(
                gpsSensorId,
                sensores
        );

        cargarCombo(
                iSensorId,
                sensores
        );

        cargarCombo(
                iContenedorId,
                contenedores
        );
    }


    private void cargarCombo(
            JComboBox<String> combo,
            List<String> ids
    ) {

        combo.removeAllItems();

        if (ids == null) {
            return;
        }

        for (String id : ids) {

            combo.addItem(id);
        }
    }


    // =========================================================
    // GETTERS CONSULTAS
    // =========================================================

    public JComboBox<String> getComboConsulta() {
        return comboConsulta;
    }


    public JPanel getParametros() {
        return parametros;
    }


    public JTable getTabla() {
        return tabla;
    }


    public JLabel getEstado() {
        return estado;
    }


    public String getSensorId() {

        String seleccion =
                (String)
                        comboConsulta.getSelectedItem();

        switch (seleccion) {

            case "Lecturas entre horarios":
                return (String)
                        horariosSensorId.getSelectedItem();

            case "Temperaturas":
                return (String)
                        temperaturasSensorId.getSelectedItem();

            case "Batería":
                return (String)
                        bateriaSensorId.getSelectedItem();

            case "Posiciones GPS":
                return (String)
                        gpsSensorId.getSelectedItem();

            case "Historial de sensor":
            default:
                return (String)
                        historialSensorId.getSelectedItem();
        }
    }


    public CampoFecha getFecha() {

        String seleccion =
                (String)
                        comboConsulta.getSelectedItem();

        switch (seleccion) {

            case "Lecturas entre horarios":
                return horariosFecha;

            case "Temperaturas":
                return temperaturasFecha;

            case "Batería":
                return bateriaFecha;

            case "Posiciones GPS":
                return gpsFecha;

            case "Historial de sensor":
            default:
                return historialFecha;
        }
    }


    public CampoFechaHora getDesde() {
        return horariosDesde;
    }


    public CampoFechaHora getHasta() {
        return horariosHasta;
    }


    public JTextField getRegion() {
        return region;
    }


    public JTextField getPais() {
        return pais;
    }


    public CampoFecha getRegionDesde() {
        return regionDesde;
    }


    public CampoFecha getRegionHasta() {
        return regionHasta;
    }


    public CampoFecha getPaisDesde() {
        return paisDesde;
    }


    public CampoFecha getPaisHasta() {
        return paisHasta;
    }


    // =========================================================
    // GETTERS INSERCIÓN
    // =========================================================

    public String getiSensorId() {

        return (String)
                iSensorId.getSelectedItem();
    }


    public CampoFecha getiFechaDia() {
        return iFechaDia;
    }


    public CampoFechaHora getiFechaHora() {
        return iFechaHora;
    }


    public String getiContenedorId() {

        return (String)
                iContenedorId.getSelectedItem();
    }


    public JTextField getiTemperatura() {
        return iTemperatura;
    }


    public JTextField getiHumedad() {
        return iHumedad;
    }


    public JTextField getiVibracion() {
        return iVibracion;
    }


    public JTextField getiLatitud() {
        return iLatitud;
    }


    public JTextField getiLongitud() {
        return iLongitud;
    }


    public JTextField getiBateria() {
        return iBateria;
    }


    public JTextField getiPais() {
        return iPais;
    }


    public JTextField getiRegion() {
        return iRegion;
    }


    // =========================================================
    // GETTERS BOTONES
    // =========================================================

    public JButton getEjecutarConsulta() {
        return ejecutarConsulta;
    }


    public JButton getInsertarLectura() {
        return insertarLectura;
    }


    public JButton getCrearTablas() {
        return crearTablas;
    }


    public JButton getCargarPrueba() {
        return cargarPrueba;
    }
}