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

        JPanel cabecera = new JPanel(new BorderLayout(10, 10));
        cabecera.setOpaque(false);

        JLabel titulo = new JLabel("Monitoreo IoT");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(Colores.TEXTO);

        JLabel subtitulo = new JLabel(
                "Consultas, métricas y lecturas almacenadas en Cassandra"
        );
        subtitulo.setFont(Fuentes.SUBTITULO);
        subtitulo.setForeground(Colores.TEXTO_SECUNDARIO);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        cabecera.add(textos, BorderLayout.WEST);
        add(cabecera, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(null);
        tabs.addTab("Consultas", construirConsultas());
        tabs.addTab("Insertar datos", construirInserciones());

        add(tabs, BorderLayout.CENTER);
    }



    // =========================================================
    // CONSULTAS
    // =========================================================

    private JPanel construirConsultas() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        formulario.setBorder(BorderFactory.createTitledBorder(
                "Parámetros de consulta"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel tipo = new JLabel("Tipo de consulta:");
        tipo.setFont(new Font("SansSerif", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formulario.add(tipo, gbc);

        comboConsulta.setPreferredSize(new Dimension(350, 36));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(comboConsulta, gbc);

        ejecutarConsulta.setPreferredSize(new Dimension(180, 36));
        gbc.gridx = 2;
        gbc.weightx = 0;
        formulario.add(ejecutarConsulta, gbc);

        comboConsulta.addActionListener(e -> actualizarFormularioConsulta());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formulario.add(parametros, gbc);

        crearFormulariosConsulta();

        /*
         * La zona de parámetros queda arriba y la consola de resultados
         * ocupa la mayor parte del espacio disponible.
         */
        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollFormulario.setPreferredSize(new Dimension(100, 210));
        scrollFormulario.setMinimumSize(new Dimension(100, 120));

        tabla.setRowHeight(32);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setShowGrid(true);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JPanel resultados = new JPanel(new BorderLayout());
        resultados.setOpaque(false);
        resultados.setMinimumSize(new Dimension(100, 150));
        resultados.add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        estado.setFont(Fuentes.NORMAL);
        estado.setForeground(Colores.TEXTO_SECUNDARIO);
        pie.add(estado, BorderLayout.WEST);
        resultados.add(pie, BorderLayout.SOUTH);

        JSplitPane divisor = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollFormulario,
                resultados
        );
        divisor.setBorder(null);
        divisor.setOpaque(false);
        divisor.setContinuousLayout(true);
        divisor.setResizeWeight(0.25);
        divisor.setDividerSize(12);
        divisor.setOneTouchExpandable(true);

        // Posición inicial del separador. Se puede arrastrar libremente
        // hacia arriba o hacia abajo, como el divisor de una ventana.
        SwingUtilities.invokeLater(() ->
                divisor.setDividerLocation(210)
        );

        panel.add(divisor, BorderLayout.CENTER);

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

    private JPanel formulario(JComponent... componentes) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int fila = 0;

        for (JComponent componente : componentes) {
            if (componente == null) {
                continue;
            }

            componente.setAlignmentX(Component.LEFT_ALIGNMENT);

            gbc.gridx = 0;
            gbc.gridy = fila++;
            gbc.gridwidth = 1;
            gbc.weightx = 1;

            panel.add(componente, gbc);
        }

        return panel;
    }



    // =========================================================
    // CAMPO
    // =========================================================

    private JPanel campo(String nombre, JComponent componente) {

        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 2, 2, 2));

        JLabel label = label(nombre);
        panel.add(label, BorderLayout.NORTH);

        componente.setOpaque(true);
        componente.setBackground(Color.WHITE);
        componente.setForeground(Color.DARK_GRAY);
        componente.setFont(new Font("SansSerif", Font.PLAIN, 14));

        componente.setPreferredSize(new Dimension(500, 36));
        componente.setMinimumSize(new Dimension(180, 36));

        if (componente instanceof JTextField textField) {
            textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        }

        JPanel contenedorCampo = new JPanel(new BorderLayout());
        contenedorCampo.setBackground(Color.WHITE);
        contenedorCampo.setBorder(new LineBorder(
                new Color(180, 180, 180), 1, true
        ));
        contenedorCampo.add(componente, BorderLayout.CENTER);

        panel.add(contenedorCampo, BorderLayout.CENTER);
        return panel;
    }



    // =========================================================
    // INSERTAR
    // =========================================================

    private JPanel construirInserciones() {

        JPanel contenedor = new JPanel(new BorderLayout(15, 15));
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        formulario.setBorder(BorderFactory.createTitledBorder(
                "Registrar lectura de sensor"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JComponent[] campos = {
                campo("Sensor ID", iSensorId),
                campo("Fecha día", iFechaDia),
                campo("Fecha y hora", iFechaHora),
                campo("Contenedor ID", iContenedorId),
                campo("Temperatura", iTemperatura),
                campo("Humedad", iHumedad),
                campo("Vibración", iVibracion),
                campo("Latitud", iLatitud),
                campo("Longitud", iLongitud),
                campo("Batería", iBateria),
                campo("País", iPais),
                campo("Región", iRegion)
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formulario.add(campos[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = (campos.length + 1) / 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        formulario.add(insertarLectura, gbc);

        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        contenedor.add(scroll, BorderLayout.CENTER);
        return contenedor;
    }



    // =========================================================
    // SCROLL FORMULARIO
    // =========================================================

    private JPanel scrollFormulario(JPanel formulario) {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);

        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        contenedor.add(scroll, BorderLayout.CENTER);
        return contenedor;
    }



    // =========================================================
    // TARJETA
    // =========================================================

    private JPanel tarjeta() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }



    // =========================================================
    // LABEL
    // =========================================================

    private JLabel label(String texto) {

        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Colores.TEXTO);
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