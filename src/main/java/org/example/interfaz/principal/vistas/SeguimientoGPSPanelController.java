package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.logging.AppLog;

import com.datastax.oss.driver.api.core.cql.Row;
import org.example.cassandra.monitoreo.controller.ControllerMonitoreo;

import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;

import org.jxmapviewer.cache.FileBasedLocalCache;

import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.geom.Point2D;

import java.io.File;

import java.time.Instant;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SeguimientoGPSPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final SeguimientoGPSPanel view;

    private final ControllerMonitoreo monitoreoController;
    private final Map<String, org.bson.Document> sensoresPorId = new HashMap<>();
    private final List<GPSWaypoint> puntosVisibles = new ArrayList<>();
    private boolean actualizandoCombo;
    private JPopupMenu detallePunto;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SeguimientoGPSPanelController(
            SeguimientoGPSPanel view
    ) {

        this.view = view;

        this.monitoreoController =
                new ControllerMonitoreo();

        configurarMapa();

        configurarEventos();

        cargarPosiciones();
    }


    // =========================================================
    // CONFIGURAR MAPA
    // =========================================================

    private void configurarMapa() {

        JXMapViewer mapa =
                view.getMapa();


        // =====================================================
        // OPENSTREETMAP
        // =====================================================

        OSMTileFactoryInfo info =
                new OSMTileFactoryInfo(
                        "OpenStreetMap",
                        "https://tile.openstreetmap.org"
                );


        DefaultTileFactory tileFactory =
                new DefaultTileFactory(info);


        // =====================================================
        // CACHE LOCAL
        // =====================================================

        File cacheDir =
                new File(
                        System.getProperty("user.home")
                                + File.separator
                                + ".jxmapviewer2"
                );


        tileFactory.setLocalCache(
                new FileBasedLocalCache(
                        cacheDir,
                        false
                )
        );


        mapa.setTileFactory(
                tileFactory
        );


        // =====================================================
        // AMÉRICA DEL SUR
        // =====================================================

        mapa.setZoom(10);

        mapa.setAddressLocation(
                new GeoPosition(
                        -15.0,
                        -60.0
                )
        );


        // =====================================================
        // MOVER MAPA CON EL MOUSE
        // =====================================================

        PanMouseInputListener pan =
                new PanMouseInputListener(mapa);


        mapa.addMouseListener(
                pan
        );


        mapa.addMouseMotionListener(
                pan
        );


        // =====================================================
        // ZOOM CON RUEDA
        // =====================================================

        mapa.addMouseWheelListener(
                new ZoomMouseWheelListenerCursor(mapa)
        );
    }


    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        /*
         * Botón actualizar.
         */
        view.getBtnActualizar()
                .addActionListener(
                        e -> cargarPosiciones()
                );


        /*
         * Cambio de sensor en el ComboBox.
         */
        view.getComboSensores()
                .addActionListener(
                        e -> {
                            if (!actualizandoCombo) cargarPosiciones();
                        }
                );
        view.getMapa().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) mostrarDetallePunto(e);
            }
        });
    }


    // =========================================================
    // CARGAR POSICIONES
    // =========================================================

    private void cargarPosiciones() {

        try {

            view.getLblEstado()
                    .setText(
                            "Cargando posiciones..."
                    );


            // =================================================
            // SENSOR SELECCIONADO
            // =================================================

            String sensorSeleccionado =
                    (String)
                            view.getComboSensores()
                                    .getSelectedItem();


            if (detallePunto != null) detallePunto.setVisible(false);
            List<Row> todasLasPosiciones = monitoreoController.obtenerTodasLasPosicionesGPS();
            cargarInformacionSensores();
            actualizarComboSensores(todasLasPosiciones);
            sensorSeleccionado = (String) view.getComboSensores().getSelectedItem();
            String sensorFiltrado = sensorSeleccionado;
            List<Row> posiciones = sensorFiltrado == null || "Todos los sensores".equals(sensorFiltrado)
                    ? todasLasPosiciones
                    : todasLasPosiciones.stream()
                    .filter(row -> sensorFiltrado.equals(row.getString("sensor_id")))
                    .toList();

            // =================================================
            // MOSTRAR EN MAPA
            // =================================================

            mostrarPosiciones(
                    posiciones
            );


            // =================================================
            // INFORMACIÓN
            // =================================================

            view.getLblCantidad()
                    .setText(
                            "Posiciones: "
                                    + posiciones.size()
                    );


            if (
                    sensorSeleccionado == null
                            || sensorSeleccionado.equals(
                            "Todos los sensores"
                    )
            ) {

                view.getLblEstado()
                        .setText(
                                "Mostrando todos los sensores."
                        );

            } else {

                view.getLblEstado()
                        .setText(
                                "Mostrando sensor: "
                                        + sensorSeleccionado
                                        + " · Hacé clic en un punto para ver su información."
                        );
            }


        } catch (Exception e) {

            AppLog.error(SeguimientoGPSPanelController.class,
                    "No se pudieron cargar posiciones GPS.", e);


            view.getLblEstado()
                    .setText(
                            "Error al cargar las posiciones."
                    );


            JOptionPane.showMessageDialog(
                    view,
                    "No se pudieron obtener las posiciones GPS:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // ACTUALIZAR COMBO DE SENSORES
    // =========================================================

    private void cargarInformacionSensores() {
        try {
            Map<String, org.bson.Document> sensores = new HashMap<>();
            for (org.bson.Document sensor : mongo.listarSensores()) {
                Object id = sensor.get("_id");
                if (id != null) sensores.put(id.toString(), sensor);
            }
            sensoresPorId.clear();
            sensoresPorId.putAll(sensores);
        } catch (Exception e) {
            AppLog.warning(SeguimientoGPSPanelController.class,
                    "No se pudo cargar la información de los sensores.");
        }
    }

    private void actualizarComboSensores(List<Row> posiciones) {
        String seleccionado = (String) view.getComboSensores().getSelectedItem();
        Set<String> sensores = new java.util.TreeSet<>(sensoresPorId.keySet());
        for (Row row : posiciones) {
            String sensorId = row.getString("sensor_id");
            if (sensorId != null && !sensorId.isBlank()) sensores.add(sensorId);
        }
        actualizandoCombo = true;
        try {
            JComboBox<String> combo = view.getComboSensores();
            combo.removeAllItems();
            combo.addItem("Todos los sensores");
            sensores.forEach(combo::addItem);
            combo.setSelectedItem(seleccionado != null && sensores.contains(seleccionado)
                    ? seleccionado : "Todos los sensores");
        } finally {
            actualizandoCombo = false;
        }
    }

    private void mostrarDetallePunto(java.awt.event.MouseEvent evento) {
        String seleccionado = (String) view.getComboSensores().getSelectedItem();
        if (seleccionado == null || "Todos los sensores".equals(seleccionado)) return;
        JXMapViewer mapa = view.getMapa();
        Rectangle viewport = mapa.getViewportBounds();
        List<GPSWaypoint> cercanos = new ArrayList<>();
        for (GPSWaypoint punto : puntosVisibles) {
            Point2D pixel = mapa.getTileFactory().geoToPixel(punto.getPosition(), mapa.getZoom());
            // El marcador tiene su punta en la coordenada y se dibuja hacia arriba.
            double x = pixel.getX() - viewport.x;
            double y = pixel.getY() - viewport.y;
            if (Math.abs(evento.getX() - x) <= 14
                    && evento.getY() >= y - 28 && evento.getY() <= y + 8) {
                cercanos.add(punto);
            }
        }
        if (cercanos.isEmpty()) return;
        if (detallePunto != null) detallePunto.setVisible(false);
        cercanos.sort(Comparator.comparing(p -> p.datos.getInstant("fecha_hora"),
                Comparator.nullsLast(Comparator.naturalOrder())));
        JTabbedPane lecturas = new JTabbedPane();
        for (int i = 0; i < cercanos.size(); i++) {
            GPSWaypoint punto = cercanos.get(i);
            javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
                    new String[]{"Dato", "Valor"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            org.bson.Document sensor = sensoresPorId.get(seleccionado);
            if (sensor != null) {
                sensor.forEach((campo, valor) -> modelo.addRow(
                        new Object[]{"Sensor · " + nombreCampo(campo), textoDetalle(valor)}));
            } else {
                modelo.addRow(new Object[]{"Sensor", seleccionado});
                modelo.addRow(new Object[]{"Información del sensor", "No disponible en MongoDB"});
            }
            for (var columna : punto.datos.getColumnDefinitions()) {
                String campo = columna.getName().asInternal();
                modelo.addRow(new Object[]{"Lectura · " + nombreCampo(campo),
                        textoDetalle(punto.datos.getObject(campo))});
            }
            JTable tabla = new JTable(modelo);
            tabla.setRowHeight(24);
            tabla.getColumnModel().getColumn(0).setPreferredWidth(190);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(290);
            lecturas.addTab(cercanos.size() == 1 ? "Información del punto" : "Lectura " + (i + 1),
                    new JScrollPane(tabla));
        }
        lecturas.setPreferredSize(new java.awt.Dimension(500, 360));
        detallePunto = new JPopupMenu();
        detallePunto.add(lecturas);
        detallePunto.show(mapa, evento.getX(), evento.getY());
    }

    private static String nombreCampo(String campo) {
        return switch (campo) {
            case "_id" -> "ID";
            case "sensor_id" -> "Sensor";
            case "contenedor_id" -> "Contenedor";
            case "fecha_dia" -> "Fecha";
            case "fecha_hora" -> "Fecha y hora";
            case "fecha_instalacion" -> "Fecha de instalación";
            case "temperatura" -> "Temperatura";
            case "humedad" -> "Humedad";
            case "vibracion" -> "Vibración";
            case "bateria" -> "Batería";
            case "pais" -> "País";
            default -> Character.toUpperCase(campo.charAt(0)) + campo.substring(1).replace('_', ' ');
        };
    }

    private static String textoDetalle(Object valor) {
        if (valor == null) return "Sin dato";
        if (valor instanceof Instant fecha) {
            return java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z")
                    .withZone(java.time.ZoneId.of("America/Argentina/Buenos_Aires")).format(fecha);
        }
        if (valor instanceof java.util.Date fecha) return textoDetalle(fecha.toInstant());
        return valor.toString();
    }

    // =========================================================
    // MOSTRAR POSICIONES
    // =========================================================

    private void mostrarPosiciones(
            List<Row> posiciones
    ) {

        JXMapViewer mapa =
                view.getMapa();


        // =====================================================
        // TODOS LOS PUNTOS
        // =====================================================

        Set<Waypoint> waypoints = new HashSet<>();
        puntosVisibles.clear();


        // =====================================================
        // AGRUPAR POSICIONES POR SENSOR
        // =====================================================

        Map<String, List<PosicionGPS>>
                posicionesPorSensor =
                new HashMap<>();


        // =====================================================
        // RECORRER POSICIONES
        // =====================================================

        for (Row row : posiciones) {

            try {

                // =============================================
                // SENSOR ID
                // =============================================

                String sensorId =
                        row.getString(
                                "sensor_id"
                        );


                if (sensorId == null) {
                    continue;
                }


                // =============================================
                // LATITUD
                // =============================================

                double latitud =
                        row.getBigDecimal(
                                "latitud"
                        ).doubleValue();


                // =============================================
                // LONGITUD
                // =============================================

                double longitud =
                        row.getBigDecimal(
                                "longitud"
                        ).doubleValue();


                // =============================================
                // VALIDAR COORDENADAS
                // =============================================

                if (
                        latitud < -90
                                || latitud > 90
                ) {
                    continue;
                }


                if (
                        longitud < -180
                                || longitud > 180
                ) {
                    continue;
                }


                // =============================================
                // FECHA Y HORA
                // posiciones_sensor no contiene fecha_dia;
                // Cassandra timestamp se obtiene como Instant.
                // =============================================

                Instant fechaHora = row.getInstant("fecha_hora");

                if (fechaHora == null) {
                    AppLog.debug(SeguimientoGPSPanelController.class,
                            "Se omitió una posición GPS sin fecha_hora.");
                    continue;
                }

                // Derivar la fecha del día desde fecha_hora en UTC.
                LocalDate fechaDia = LocalDate.ofInstant(
                        fechaHora,
                        java.time.ZoneOffset.UTC
                );


                // =============================================
                // CREAR OBJETO
                // =============================================

                PosicionGPS posicion =
                        new PosicionGPS(
                                sensorId,
                                latitud,
                                longitud,
                                fechaDia,
                                fechaHora
                        );


                // =============================================
                // AGREGAR PUNTO
                // =============================================

                GPSWaypoint punto = new GPSWaypoint(latitud, longitud, row);
                waypoints.add(punto);
                puntosVisibles.add(punto);


                // =============================================
                // AGRUPAR POR SENSOR
                // =============================================

                posicionesPorSensor
                        .computeIfAbsent(
                                sensorId,
                                k -> new ArrayList<>()
                        )
                        .add(posicion);


            } catch (Exception e) {

                AppLog.warning(SeguimientoGPSPanelController.class,
                        "Se omitió una posición GPS que no pudo procesarse.");
            }
        }


        // =====================================================
        // COLORES DISPONIBLES
        // =====================================================

        List<Color> colores =
                Arrays.asList(

                        new Color(
                                220,
                                38,
                                38
                        ),

                        new Color(
                                37,
                                99,
                                235
                        ),

                        new Color(
                                22,
                                163,
                                74
                        ),

                        new Color(
                                147,
                                51,
                                234
                        ),

                        new Color(
                                234,
                                88,
                                12
                        ),

                        new Color(
                                8,
                                145,
                                178
                        ),

                        new Color(
                                219,
                                39,
                                119
                        ),

                        new Color(
                                101,
                                163,
                                13
                        ),

                        new Color(
                                124,
                                58,
                                237
                        ),

                        new Color(
                                202,
                                138,
                                4
                        )
                );


        // =====================================================
        // LISTA DE PAINTERS
        // =====================================================

        List<Painter<JXMapViewer>> painters =
                new ArrayList<>();


        // =====================================================
        // CREAR UNA RUTA POR CADA SENSOR
        // =====================================================

        int indiceColor = 0;


        for (
                Map.Entry<String, List<PosicionGPS>> entry
                : posicionesPorSensor.entrySet()
        ) {


            List<PosicionGPS> ruta =
                    entry.getValue();


            // =============================================
            // ORDENAR POR FECHA Y HORA
            // =============================================

            ruta.sort(
                    Comparator.comparing(
                            PosicionGPS::getFechaHora,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    )
            );


            // =============================================
            // COLOR DEL SENSOR
            // =============================================

            Color color =
                    colores.get(
                            indiceColor
                                    % colores.size()
                    );


            indiceColor++;


            // =============================================
            // CREAR RUTA
            // =============================================

            if (ruta.size() >= 2) {

                List<GeoPosition> puntosRuta =
                        new ArrayList<>();


                for (
                        PosicionGPS posicion
                        : ruta
                ) {

                    puntosRuta.add(
                            new GeoPosition(
                                    posicion.getLatitud(),
                                    posicion.getLongitud()
                            )
                    );
                }


                painters.add(
                        new RoutePainter(
                                puntosRuta,
                                color
                        )
                );
            }
        }


        // =====================================================
        // PAINTER DE LOS PUNTOS
        // =====================================================

        WaypointPainter<Waypoint>
                waypointPainter =
                new WaypointPainter<>();


        waypointPainter.setWaypoints(
                waypoints
        );


        painters.add(
                waypointPainter
        );


        // =====================================================
        // COMBINAR RUTAS + PUNTOS
        // =====================================================

        CompoundPainter<JXMapViewer>
                compoundPainter =
                new CompoundPainter<>(
                        painters
                );


        mapa.setOverlayPainter(
                compoundPainter
        );


        mapa.repaint();
    }


    // =========================================================
    // PAINTER DE LAS RUTAS
    // =========================================================

    private static class RoutePainter
            implements Painter<JXMapViewer> {

        private final List<GeoPosition> track;

        private final Color color;


        public RoutePainter(
                List<GeoPosition> track,
                Color color
        ) {

            this.track =
                    new ArrayList<>(
                            track
                    );

            this.color =
                    color;
        }


        @Override
        public void paint(
                Graphics2D g,
                JXMapViewer mapa,
                int width,
                int height
        ) {

            Graphics2D graphics =
                    (Graphics2D) g.create();


            // =================================================
            // VIEWPORT
            // =================================================

            Rectangle rect =
                    mapa.getViewportBounds();


            graphics.translate(
                    -rect.x,
                    -rect.y
            );


            // =================================================
            // ANTIALIASING
            // =================================================

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            // =================================================
            // COLOR DE LA RUTA
            // =================================================

            graphics.setColor(
                    color
            );


            // =================================================
            // GROSOR
            // =================================================

            graphics.setStroke(
                    new BasicStroke(
                            3f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );


            // =================================================
            // DIBUJAR RECORRIDO
            // =================================================

            Point2D anterior =
                    null;


            for (
                    GeoPosition posicion
                    : track
            ) {

                Point2D actual =
                        mapa.getTileFactory()
                                .geoToPixel(
                                        posicion,
                                        mapa.getZoom()
                                );


                if (anterior != null) {

                    graphics.drawLine(
                            (int) anterior.getX(),
                            (int) anterior.getY(),
                            (int) actual.getX(),
                            (int) actual.getY()
                    );
                }


                anterior =
                        actual;
            }


            graphics.dispose();
        }
    }


    // =========================================================
    // MODELO DE POSICIÓN GPS
    // =========================================================

    private static class PosicionGPS {


        private final double latitud;

        private final double longitud;


        private final Instant fechaHora;


        public PosicionGPS(
                String sensorId,
                double latitud,
                double longitud,
                LocalDate fechaDia,
                Instant fechaHora
        ) {


            this.latitud =
                    latitud;

            this.longitud =
                    longitud;


            this.fechaHora =
                    fechaHora;
        }


        public double getLatitud() {

            return latitud;
        }


        public double getLongitud() {

            return longitud;
        }


        public Instant getFechaHora() {

            return fechaHora;
        }
    }


    // =========================================================
    // WAYPOINT GPS
    // =========================================================

    private static class GPSWaypoint
            implements Waypoint {

        private final GeoPosition posicion;
        private final Row datos;

        public GPSWaypoint(
                double latitud,
                double longitud,
                Row datos
        ) {

            this.datos = datos;
            this.posicion =
                    new GeoPosition(
                            latitud,
                            longitud
                    );
        }


        @Override
        public GeoPosition getPosition() {

            return posicion;
        }
    }
}
