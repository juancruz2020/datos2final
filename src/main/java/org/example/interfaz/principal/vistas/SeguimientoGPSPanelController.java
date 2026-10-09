package org.example.interfaz.principal.vistas;

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

    private final SeguimientoGPSPanel view;

    private final ControllerMonitoreo monitoreoController;


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
                        e -> cargarPosiciones()
                );
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


            List<Row> posiciones;


            // =================================================
            // TODOS LOS SENSORES
            // =================================================

            if (
                    sensorSeleccionado == null
                            || sensorSeleccionado.equals(
                            "Todos los sensores"
                    )
            ) {

                posiciones =
                        monitoreoController
                                .obtenerTodasLasPosicionesGPS();
            }


            // =================================================
            // UN SENSOR
            // =================================================

            else {

                posiciones =
                        monitoreoController
                                .obtenerTodasLasPosicionesGPS();

                posiciones =
                        posiciones.stream()
                                .filter(row ->
                                        sensorSeleccionado.equals(
                                                row.getString("sensor_id")
                                        )
                                )
                                .toList();
            }

            // =================================================
            // ACTUALIZAR COMBO
            // =================================================

            actualizarComboSensores(
                    posiciones
            );


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
                        );
            }


        } catch (Exception e) {

            e.printStackTrace();


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

    private void actualizarComboSensores(
            List<Row> posiciones
    ) {

        /*
         * Guardamos el sensor que estaba seleccionado.
         */
        String seleccionado =
                (String)
                        view.getComboSensores()
                                .getSelectedItem();


        /*
         * Obtener IDs únicos.
         */
        Set<String> sensores =
                new HashSet<>();


        for (Row row : posiciones) {

            try {

                String sensorId =
                        row.getString(
                                "sensor_id"
                        );


                if (
                        sensorId != null
                                && !sensorId.isBlank()
                ) {

                    sensores.add(
                            sensorId
                    );
                }

            } catch (Exception e) {

                System.err.println(
                        "No se pudo obtener sensor_id: "
                                + e.getMessage()
                );
            }
        }


        /*
         * Evitamos que el ActionListener
         * dispare nuevamente mientras
         * modificamos el combo.
         */
        view.getComboSensores()
                .removeActionListener(
                        view.getComboSensores()
                                .getActionListeners()[0]
                );


        /*
         * Limpiar combo.
         */
        view.getComboSensores()
                .removeAllItems();


        /*
         * Primera opción.
         */
        view.getComboSensores()
                .addItem(
                        "Todos los sensores"
                );


        /*
         * Agregar sensores.
         */
        sensores.stream()
                .sorted()
                .forEach(
                        sensorId ->
                                view.getComboSensores()
                                        .addItem(
                                                sensorId
                                        )
                );


        /*
         * Intentar mantener la selección.
         */
        if (
                seleccionado != null
                        && sensores.contains(
                        seleccionado
                )
        ) {

            view.getComboSensores()
                    .setSelectedItem(
                            seleccionado
                    );

        } else {

            view.getComboSensores()
                    .setSelectedItem(
                            "Todos los sensores"
                    );
        }


        /*
         * Volver a registrar el evento.
         */
        view.getComboSensores()
                .addActionListener(
                        e -> cargarPosiciones()
                );
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

        Set<Waypoint> waypoints =
                new HashSet<>();


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
                // FECHA DEL DÍA
                // =============================================

                LocalDate fechaDia =
                        row.getLocalDate(
                                "fecha_dia"
                        );


                // =============================================
                // FECHA Y HORA
                // Cassandra timestamp -> Instant
                // =============================================

                Instant fechaHora =
                        row.getInstant(
                                "fecha_hora"
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

                waypoints.add(
                        new GPSWaypoint(
                                latitud,
                                longitud
                        )
                );


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

                System.err.println(
                        "No se pudo procesar una posición GPS: "
                                + e.getMessage()
                );
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

            String sensorId =
                    entry.getKey();


            List<PosicionGPS> ruta =
                    entry.getValue();


            // =============================================
            // ORDENAR POR FECHA Y HORA
            // =============================================

            ruta.sort(
                    Comparator.comparing(
                            PosicionGPS::getFechaHora
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

        private final String sensorId;

        private final double latitud;

        private final double longitud;

        private final LocalDate fechaDia;

        private final Instant fechaHora;


        public PosicionGPS(
                String sensorId,
                double latitud,
                double longitud,
                LocalDate fechaDia,
                Instant fechaHora
        ) {

            this.sensorId =
                    sensorId;

            this.latitud =
                    latitud;

            this.longitud =
                    longitud;

            this.fechaDia =
                    fechaDia;

            this.fechaHora =
                    fechaHora;
        }


        public String getSensorId() {

            return sensorId;
        }


        public double getLatitud() {

            return latitud;
        }


        public double getLongitud() {

            return longitud;
        }


        public LocalDate getFechaDia() {

            return fechaDia;
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


        public GPSWaypoint(
                double latitud,
                double longitud
        ) {

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