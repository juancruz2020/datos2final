package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final JLabel lblSensores;
    private final JLabel lblContenedores;
    private final JLabel lblTemperatura;
    private final JLabel lblBateriaBaja;
    private final JLabel lblEnvios;
    private final JLabel lblIncidentesAbiertos;
    private final JLabel lblTemperaturasRiesgo;
    private final JButton btnActualizar;
    private final JTable tablaEstadosEnvios;
    private final JTable tablaSeveridadIncidentes;

    private final GraficoLineaPanel graficoTemperatura;
    private final GraficoLineaPanel graficoHumedad;
    private final GraficoBarrasPanel graficoVibracion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashboardPanel() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                Colores.FONDO
        );


        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Dashboard"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        titulo.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        15,
                        30
                )
        );

        btnActualizar = new JButton("Actualizar");
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.WEST);
        cabecera.add(btnActualizar, BorderLayout.EAST);
        remove(titulo);
        add(cabecera, BorderLayout.NORTH);


        // =====================================================
        // CONTENIDO
        // =====================================================

        JPanel contenido =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                        )
                );

        contenido.setOpaque(
                false
        );

        contenido.setBorder(
                new EmptyBorder(
                        0,
                        30,
                        30,
                        30
                )
        );


        // =====================================================
        // TARJETAS
        // =====================================================

        JPanel tarjetas =
                new JPanel(
                        new GridLayout(
                                2,
                                4,
                                15,
                                0
                        )
                );

        tarjetas.setOpaque(
                false
        );


        lblSensores =
                crearTarjeta(
                        tarjetas,
                        "Sensores",
                        "0"
                );


        lblContenedores =
                crearTarjeta(
                        tarjetas,
                        "Contenedores",
                        "0"
                );


        lblTemperatura =
                crearTarjeta(
                        tarjetas,
                        "Temperatura promedio",
                        "0 °C"
                );


        lblBateriaBaja =
                crearTarjeta(
                        tarjetas,
                        "Batería baja",
                        "0"
                );

        // =====================================================
        // GRÁFICOS
        // =====================================================

        JPanel graficos =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                        )
                );

        graficos.setOpaque(
                false
        );


        // =====================================================
        // GRÁFICOS SUPERIORES
        // =====================================================

        JPanel graficosSuperiores =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );

        graficosSuperiores.setOpaque(
                false
        );


        // -----------------------------------------------------
        // TEMPERATURA
        // -----------------------------------------------------

        graficoTemperatura =
                new GraficoLineaPanel(
                        "Temperatura promedio por día",
                        "°C"
                );


        graficosSuperiores.add(
                graficoTemperatura
        );


        // -----------------------------------------------------
        // HUMEDAD
        // -----------------------------------------------------

        graficoHumedad =
                new GraficoLineaPanel(
                        "Humedad promedio por día",
                        "%"
                );


        graficosSuperiores.add(
                graficoHumedad
        );


        graficos.add(
                graficosSuperiores,
                BorderLayout.CENTER
        );


        // =====================================================
        // VIBRACIÓN
        // =====================================================

        graficoVibracion =
                new GraficoBarrasPanel(
                        "Vibración promedio por región"
                );


        graficoVibracion.setPreferredSize(
                new Dimension(
                        0,
                        230
                )
        );


        graficos.add(
                graficoVibracion,
                BorderLayout.SOUTH
        );


        contenido.add(
                graficos,
                BorderLayout.CENTER
        );


        JPanel operativos = new JPanel(new BorderLayout(15, 15));
        operativos.setOpaque(false);
        operativos.setBorder(new EmptyBorder(20, 30, 30, 30));
        lblEnvios = crearTarjeta(tarjetas, "Envíos", "0");
        lblIncidentesAbiertos = crearTarjeta(tarjetas, "Incidentes abiertos", "0");
        lblTemperaturasRiesgo = crearTarjeta(tarjetas, "Temperaturas críticas", "0");
        operativos.add(tarjetas, BorderLayout.NORTH);
        JPanel tablasResumen = new JPanel(new GridLayout(1, 2, 15, 0));
        tablasResumen.setOpaque(false);
        tablaEstadosEnvios = crearTablaResumen("Envíos por estado", "Estado", "Cantidad");
        tablaSeveridadIncidentes = crearTablaResumen("Incidentes por severidad", "Severidad", "Cantidad");
        tablasResumen.add(panelTablaResumen("Distribución de envíos", tablaEstadosEnvios));
        tablasResumen.add(panelTablaResumen("Distribución de incidentes", tablaSeveridadIncidentes));
        operativos.add(tablasResumen, BorderLayout.CENTER);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Monitoreo", contenido);
        pestanas.addTab("Resumen operativo", operativos);
        add(pestanas, BorderLayout.CENTER);
    }

    private JTable crearTablaResumen(String nombre, String columna1, String columna2) {
        return new JTable(new DefaultTableModel(new Object[]{columna1, columna2}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
    }

    private JPanel panelTablaResumen(String titulo, JTable tabla) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(25, 50, 75));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 80, 105)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(Fuentes.LABEL);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }


    // =========================================================
    // CREAR TARJETA
    // =========================================================

    private JLabel crearTarjeta(
            JPanel contenedor,
            String titulo,
            String valorInicial
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout()
                );

        tarjeta.setBackground(
                new Color(
                        25,
                        50,
                        75
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        50,
                                        80,
                                        105
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );


        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setForeground(
                new Color(
                        180,
                        195,
                        210
                )
        );

        lblTitulo.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );


        JLabel lblValor =
                new JLabel(
                        valorInicial
                );

        lblValor.setForeground(
                Color.WHITE
        );

        lblValor.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        25
                )
        );


        tarjeta.add(
                lblTitulo,
                BorderLayout.NORTH
        );

        tarjeta.add(
                lblValor,
                BorderLayout.CENTER
        );


        contenedor.add(
                tarjeta
        );


        return lblValor;
    }


    // =========================================================
    // ACTUALIZAR TARJETAS
    // =========================================================

    public void actualizarSensores(
            int cantidad
    ) {

        lblSensores.setText(
                String.valueOf(
                        cantidad
                )
        );
    }


    public void actualizarContenedores(
            int cantidad
    ) {

        lblContenedores.setText(
                String.valueOf(
                        cantidad
                )
        );
    }


    public void actualizarTemperatura(
            double temperatura
    ) {

        lblTemperatura.setText(
                String.format(
                        "%.2f °C",
                        temperatura
                )
        );
    }


    public void actualizarBateriaBaja(
            int cantidad
    ) {

        lblBateriaBaja.setText(
                String.valueOf(
                        cantidad
                )
        );
    }

    public void actualizarEnvios(int cantidad) { lblEnvios.setText(String.valueOf(cantidad)); }
    public void actualizarIncidentesAbiertos(int cantidad) { lblIncidentesAbiertos.setText(String.valueOf(cantidad)); }
    public void actualizarTemperaturasRiesgo(int cantidad) { lblTemperaturasRiesgo.setText(String.valueOf(cantidad)); }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JTable getTablaEstadosEnvios() { return tablaEstadosEnvios; }
    public JTable getTablaSeveridadIncidentes() { return tablaSeveridadIncidentes; }


    // =========================================================
    // ACTUALIZAR GRÁFICOS
    // =========================================================

    public void actualizarTemperaturas(
            List<String> etiquetas,
            List<Double> valores
    ) {

        graficoTemperatura.setDatos(
                etiquetas,
                valores
        );
    }


    public void actualizarHumedades(
            List<String> etiquetas,
            List<Double> valores
    ) {

        graficoHumedad.setDatos(
                etiquetas,
                valores
        );
    }


    public void actualizarVibraciones(
            List<String> etiquetas,
            List<Double> valores
    ) {

        graficoVibracion.setDatos(
                etiquetas,
                valores
        );
    }


    // =========================================================
    // GRÁFICO DE LÍNEA
    // =========================================================

    private static class GraficoLineaPanel
            extends JPanel {

        private final String titulo;

        private final String unidad;

        private List<String> etiquetas =
                List.of();

        private List<Double> valores =
                List.of();


        public GraficoLineaPanel(
                String titulo,
                String unidad
        ) {

            this.titulo =
                    titulo;

            this.unidad =
                    unidad;

            setBackground(
                    new Color(
                            25,
                            50,
                            75
                    )
            );

            setBorder(
                    BorderFactory.createLineBorder(
                            new Color(
                                    50,
                                    80,
                                    105
                            )
                    )
            );
        }


        public void setDatos(
                List<String> etiquetas,
                List<Double> valores
        ) {

            this.etiquetas =
                    etiquetas != null
                            ? etiquetas
                            : List.of();

            this.valores =
                    valores != null
                            ? valores
                            : List.of();

            repaint();
        }


        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(
                    g
            );

            Graphics2D g2 =
                    (Graphics2D)
                            g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            int ancho =
                    getWidth();

            int alto =
                    getHeight();


            int izquierda = 55;
            int derecha = 25;
            int arriba = 45;
            int abajo = 40;


            // =================================================
            // TÍTULO
            // =================================================

            g2.setColor(
                    Color.WHITE
            );

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            15
                    )
            );

            g2.drawString(
                    titulo,
                    15,
                    25
            );


            if (valores.isEmpty()) {

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                13
                        )
                );

                g2.setColor(
                        new Color(
                                170,
                                185,
                                200
                        )
                );

                g2.drawString(
                        "No hay datos disponibles",
                        izquierda,
                        alto / 2
                );

                g2.dispose();

                return;
            }


            double minimo =
                    valores.stream()
                            .mapToDouble(
                                    Double::doubleValue
                            )
                            .min()
                            .orElse(0);


            double maximo =
                    valores.stream()
                            .mapToDouble(
                                    Double::doubleValue
                            )
                            .max()
                            .orElse(1);


            if (minimo == maximo) {

                minimo -= 1;
                maximo += 1;
            }


            int anchoGrafico =
                    ancho
                            - izquierda
                            - derecha;


            int altoGrafico =
                    alto
                            - arriba
                            - abajo;


            // =================================================
            // EJES
            // =================================================

            g2.setColor(
                    new Color(
                            100,
                            120,
                            140
                    )
            );


            g2.drawLine(
                    izquierda,
                    arriba,
                    izquierda,
                    alto - abajo
            );


            g2.drawLine(
                    izquierda,
                    alto - abajo,
                    ancho - derecha,
                    alto - abajo
            );


            // =================================================
            // GRILLA
            // =================================================

            for (int i = 0; i <= 4; i++) {

                double valor =
                        minimo
                                + (
                                (maximo - minimo)
                                        * i
                                        / 4.0
                        );


                int y =
                        (int)
                                (
                                        alto
                                                - abajo
                                                - (
                                                (valor - minimo)
                                                        / (maximo - minimo)
                                        )
                                                * altoGrafico
                                );


                g2.setColor(
                        new Color(
                                50,
                                75,
                                95
                        )
                );


                g2.drawLine(
                        izquierda,
                        y,
                        ancho - derecha,
                        y
                );


                g2.setColor(
                        new Color(
                                180,
                                195,
                                210
                        )
                );


                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                10
                        )
                );


                g2.drawString(
                        String.format(
                                "%.1f%s",
                                valor,
                                unidad
                        ),
                        5,
                        y + 4
                );
            }


            // =================================================
            // LÍNEA
            // =================================================

            g2.setColor(
                    new Color(
                            80,
                            180,
                            255
                    )
            );


            int divisor =
                    Math.max(
                            1,
                            valores.size() - 1
                    );


            for (
                    int i = 0;
                    i < valores.size() - 1;
                    i++
            ) {

                int x1 =
                        izquierda
                                + (
                                i
                                        * anchoGrafico
                                        / divisor
                        );


                int x2 =
                        izquierda
                                + (
                                (i + 1)
                                        * anchoGrafico
                                        / divisor
                        );


                int y1 =
                        (int)
                                (
                                        alto
                                                - abajo
                                                - (
                                                (valores.get(i) - minimo)
                                                        / (maximo - minimo)
                                        )
                                                * altoGrafico
                                );


                int y2 =
                        (int)
                                (
                                        alto
                                                - abajo
                                                - (
                                                (valores.get(i + 1) - minimo)
                                                        / (maximo - minimo)
                                        )
                                                * altoGrafico
                                );


                g2.drawLine(
                        x1,
                        y1,
                        x2,
                        y2
                );


                g2.fillOval(
                        x1 - 3,
                        y1 - 3,
                        6,
                        6
                );
            }


            // =================================================
            // ÚLTIMO PUNTO
            // =================================================

            int ultimo =
                    valores.size() - 1;


            int ultimoX =
                    izquierda
                            + (
                            ultimo
                                    * anchoGrafico
                                    / divisor
                    );


            int ultimoY =
                    (int)
                            (
                                    alto
                                            - abajo
                                            - (
                                            (valores.get(ultimo) - minimo)
                                                    / (maximo - minimo)
                                    )
                                            * altoGrafico
                            );


            g2.fillOval(
                    ultimoX - 3,
                    ultimoY - 3,
                    6,
                    6
            );


            // =================================================
            // ETIQUETAS
            // =================================================

            if (!etiquetas.isEmpty()) {

                g2.setColor(
                        new Color(
                                180,
                                195,
                                210
                        )
                );

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                10
                        )
                );


                g2.drawString(
                        etiquetas.get(0),
                        izquierda,
                        alto - 15
                );


                if (etiquetas.size() > 1) {

                    String ultima =
                            etiquetas.get(
                                    etiquetas.size() - 1
                            );


                    g2.drawString(
                            ultima,
                            ancho - derecha - 40,
                            alto - 15
                    );
                }
            }


            g2.dispose();
        }
    }


    // =========================================================
    // GRÁFICO DE BARRAS HORIZONTAL
    // =========================================================

    private static class GraficoBarrasPanel
            extends JPanel {

        private final String titulo;

        private List<String> etiquetas =
                List.of();

        private List<Double> valores =
                List.of();


        public GraficoBarrasPanel(
                String titulo
        ) {

            this.titulo =
                    titulo;

            setBackground(
                    new Color(
                            25,
                            50,
                            75
                    )
            );

            setBorder(
                    BorderFactory.createLineBorder(
                            new Color(
                                    50,
                                    80,
                                    105
                            )
                    )
            );
        }


        public void setDatos(
                List<String> etiquetas,
                List<Double> valores
        ) {

            this.etiquetas =
                    etiquetas != null
                            ? etiquetas
                            : List.of();

            this.valores =
                    valores != null
                            ? valores
                            : List.of();

            repaint();
        }


        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(
                    g
            );


            Graphics2D g2 =
                    (Graphics2D)
                            g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            int ancho =
                    getWidth();

            int alto =
                    getHeight();


            // =================================================
            // TÍTULO
            // =================================================

            g2.setColor(
                    Color.WHITE
            );

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            15
                    )
            );

            g2.drawString(
                    titulo,
                    15,
                    25
            );


            if (valores.isEmpty()) {

                g2.setColor(
                        new Color(
                                170,
                                185,
                                200
                        )
                );

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                13
                        )
                );

                g2.drawString(
                        "No hay datos disponibles",
                        30,
                        alto / 2
                );

                g2.dispose();

                return;
            }


            int izquierda = 150;
            int derecha = 45;
            int arriba = 50;
            int abajo = 20;


            double maximo =
                    valores.stream()
                            .mapToDouble(
                                    Double::doubleValue
                            )
                            .max()
                            .orElse(1);


            if (maximo <= 0) {

                maximo = 1;
            }


            int anchoGrafico =
                    ancho
                            - izquierda
                            - derecha;


            int altoGrafico =
                    alto
                            - arriba
                            - abajo;


            int cantidad =
                    valores.size();


            int espacio =
                    Math.max(
                            20,
                            altoGrafico
                                    / cantidad
                    );


            int alturaBarra =
                    Math.max(
                            8,
                            Math.min(
                                    25,
                                    espacio - 8
                            )
                    );


            // =================================================
            // BARRAS
            // =================================================

            for (
                    int i = 0;
                    i < cantidad;
                    i++
            ) {

                double valor =
                        valores.get(i);


                int anchoBarra =
                        (int)
                                (
                                        (valor / maximo)
                                                * anchoGrafico
                                );


                int y =
                        arriba
                                + (
                                i
                                        * espacio
                        )
                                + (
                                espacio
                                        - alturaBarra
                        )
                                / 2;


                // ---------------------------------------------
                // ETIQUETA
                // ---------------------------------------------

                String etiqueta =
                        i < etiquetas.size()
                                ? etiquetas.get(i)
                                : "";


                g2.setColor(
                        new Color(
                                210,
                                220,
                                230
                        )
                );


                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                11
                        )
                );


                // Si el nombre es demasiado largo,
                // lo recortamos.

                if (
                        etiqueta.length()
                                > 22
                ) {

                    etiqueta =
                            etiqueta.substring(
                                    0,
                                    22
                            )
                                    + "...";
                }


                g2.drawString(
                        etiqueta,
                        10,
                        y
                                + alturaBarra
                                - 5
                );


                // ---------------------------------------------
                // BARRA
                // ---------------------------------------------

                g2.setColor(
                        new Color(
                                80,
                                180,
                                255
                        )
                );


                g2.fillRoundRect(
                        izquierda,
                        y,
                        Math.max(
                                2,
                                anchoBarra
                        ),
                        alturaBarra,
                        6,
                        6
                );


                // ---------------------------------------------
                // VALOR
                // ---------------------------------------------

                g2.setColor(
                        Color.WHITE
                );


                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                10
                        )
                );


                g2.drawString(
                        String.format(
                                "%.2f",
                                valor
                        ),
                        izquierda
                                + anchoBarra
                                + 6,
                        y
                                + alturaBarra
                                - 5
                );
            }


            g2.dispose();
        }
    }
}
