
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.logging.AppLog;

import com.datastax.oss.driver.api.core.cql.Row;
import org.bson.Document;
import org.example.cassandra.monitoreo.controller.ControllerMonitoreo;
import org.example.mongoDB.service.PermisosService;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class SeguimientoEnvioPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final SeguimientoEnvioPanel view;
    private final String usuarioId;

    private final ControllerMonitoreo monitoreoController;
    private final PermisosService permisosService =
            new PermisosService();

    private final Map<String, Document> sensoresPorId =
            new HashMap<>();

    private final List<GPSWaypoint> puntosVisibles =
            new ArrayList<>();

    private boolean actualizandoCombo;
    private JPopupMenu detallePunto;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SeguimientoEnvioPanelController(
            SeguimientoEnvioPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "SEGUIMIENTO_ENVIO"
        )) {
            throw new SecurityException(
                    "No tiene permiso para acceder al seguimiento."
            );
        }

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

        JXMapViewer mapa = view.getMapa();

        OSMTileFactoryInfo info =
                new OSMTileFactoryInfo(
                        "OpenStreetMap",
                        "https://tile.openstreetmap.org"
                );

        DefaultTileFactory tileFactory =
                new DefaultTileFactory(info);

        File cacheDir = new File(
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

        mapa.setTileFactory(tileFactory);
        mapa.setZoom(10);

        mapa.setAddressLocation(
                new GeoPosition(-15.0, -60.0)
        );

        PanMouseInputListener pan =
                new PanMouseInputListener(mapa);

        mapa.addMouseListener(pan);
        mapa.addMouseMotionListener(pan);

        mapa.addMouseWheelListener(
                new ZoomMouseWheelListenerCursor(mapa)
        );
    }

    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> cargarPosiciones()
        );

        view.getComboEnvios().addActionListener(
                e -> {
                    if (!actualizandoCombo) {
                        cargarPosiciones();
                    }
                }
        );

        view.getMapa().addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            mostrarDetallePunto(e);
                        }
                    }
                }
        );
    }

    // =========================================================
    // CARGAR POSICIONES
    // =========================================================

    private void cargarPosiciones() {

        try {

            view.getLblEstado().setText(
                    "Cargando posiciones..."
            );

            if (detallePunto != null) {
                detallePunto.setVisible(false);
            }

            // Actualizar la lista respetando el usuario.
            actualizarComboEnvios();

            String envioSeleccionado =
                    (String) view.getComboEnvios()
                            .getSelectedItem();

            List<Row> posiciones = new ArrayList<>();

            if (envioSeleccionado != null
                    && !"Seleccioná un envío".equals(
                            envioSeleccionado
                    )) {

                // Validar acceso antes de obtener GPS.
                verificarAccesoEnvio(envioSeleccionado);

                cargarInformacionSensores();

                Set<String> sensoresDelEnvio =
                        obtenerSensoresDelEnvio(
                                envioSeleccionado
                        );

                if (!sensoresDelEnvio.isEmpty()) {

                    List<Row> todasLasPosiciones =
                            monitoreoController
                                    .obtenerTodasLasPosicionesGPS();

                    for (Row row : todasLasPosiciones) {

                        String sensorId =
                                row.getString("sensor_id");

                        if (sensorId != null
                                && sensoresDelEnvio.contains(
                                        sensorId
                                )) {
                            posiciones.add(row);
                        }
                    }
                }
            }

            mostrarPosiciones(posiciones);

            view.getLblCantidad().setText(
                    "Posiciones: " + posiciones.size()
            );

            if (envioSeleccionado == null
                    || "Seleccioná un envío".equals(
                            envioSeleccionado
                    )) {

                view.getLblEstado().setText(
                        "Seleccioná un envío para ver las rutas de sus sensores."
                );

            } else {

                view.getLblEstado().setText(
                        "Mostrando envío: "
                                + envioSeleccionado
                                + " · Hacé clic en un punto para ver su información."
                );
            }

        } catch (Exception e) {

            AppLog.error(SeguimientoEnvioPanelController.class,
                    "No se pudieron cargar posiciones del envío.", e);

            mostrarPosiciones(
                    new ArrayList<>()
            );

            view.getLblCantidad().setText(
                    "Posiciones: 0"
            );

            view.getLblEstado().setText(
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
    // CARGAR INFORMACIÓN DE SENSORES
    // =========================================================

    private void cargarInformacionSensores() {

        try {

            Map<String, Document> sensores =
                    new HashMap<>();

            for (Document sensor :
                    mongo.listarSensores()) {

                Object id = sensor.get("_id");

                if (id != null) {
                    sensores.put(
                            id.toString(),
                            sensor
                    );
                }
            }

            sensoresPorId.clear();
            sensoresPorId.putAll(sensores);

        } catch (Exception e) {

            sensoresPorId.clear();

            AppLog.warning(SeguimientoEnvioPanelController.class,
                    "No se pudo cargar la información de los sensores.");
        }
    }

    // =========================================================
    // ACTUALIZAR COMBO DE ENVÍOS
    // =========================================================

    private void actualizarComboEnvios() {

        String seleccionado =
                (String) view.getComboEnvios()
                        .getSelectedItem();

        Set<String> envios =
                new TreeSet<>();

        List<Document> listaEnvios;

        // CLIENTE: solamente los envíos de su empresa.
        if (permisosService.esCliente(usuarioId)) {

            String clienteId =
                    permisosService.obtenerClienteId(
                            usuarioId
                    );

            if (clienteId == null) {
                throw new SecurityException(
                        "El usuario no tiene un cliente asociado."
                );
            }

            listaEnvios =
                    mongo.buscarEnviosPorCliente(
                            clienteId
                    );

        } else {

            // ADMINISTRADOR Y OPERADOR: todos.
            permisosService.exigirAdminUOperador(
                    usuarioId
            );

            listaEnvios =
                    mongo.listarEnvios();
        }

        for (Document envio : listaEnvios) {

            Object id = envio.get("_id");

            if (id != null) {
                envios.add(id.toString());
            }
        }

        actualizandoCombo = true;

        try {

            JComboBox<String> combo =
                    view.getComboEnvios();

            combo.removeAllItems();

            combo.addItem(
                    "Seleccioná un envío"
            );

            envios.forEach(combo::addItem);

            if (seleccionado != null
                    && envios.contains(seleccionado)) {

                combo.setSelectedItem(
                        seleccionado
                );

            } else {

                combo.setSelectedIndex(0);
            }

        } finally {

            actualizandoCombo = false;
        }
    }

    // =========================================================
    // VERIFICAR ACCESO A UN ENVÍO
    // =========================================================

    private Document verificarAccesoEnvio(
            String envioId
    ) {

        if (envioId == null || envioId.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un envío."
            );
        }

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "SEGUIMIENTO_ENVIO"
        )) {
            throw new SecurityException(
                    "No tiene permiso para acceder al seguimiento."
            );
        }

        Document envio =
                mongo.buscarEnvioPorId(envioId);

        if (envio == null) {
            throw new IllegalArgumentException(
                    "El envío no existe."
            );
        }

        if (permisosService.esCliente(usuarioId)) {

            String clienteId =
                    permisosService.obtenerClienteId(
                            usuarioId
                    );

            Object clienteEnvio =
                    envio.get("cliente_id");

            if (clienteId == null
                    || clienteEnvio == null
                    || !clienteId.equals(
                            clienteEnvio.toString()
                    )) {

                throw new SecurityException(
                        "No tiene permiso para consultar este envío."
                );
            }

        } else {

            permisosService.exigirAdminUOperador(
                    usuarioId
            );
        }

        return envio;
    }

    // =========================================================
    // OBTENER SENSORES DEL ENVÍO
    // =========================================================

    private Set<String> obtenerSensoresDelEnvio(
            String envioId
    ) {

        Set<String> sensores =
                new HashSet<>();

        if (envioId == null
                || "Seleccioná un envío".equals(envioId)) {

            return sensores;
        }

        Document envio =
                verificarAccesoEnvio(envioId);

        Object contenedoresValor =
                envio.get("contenedores_ids");

        if (!(contenedoresValor
                instanceof Iterable<?> contenedores)) {

            return sensores;
        }

        Set<String> contenedoresIds =
                new HashSet<>();

        for (Object id : contenedores) {

            if (id != null) {
                contenedoresIds.add(
                        id.toString()
                );
            }
        }

        for (Document sensor :
                sensoresPorId.values()) {

            Object contenedorId =
                    sensor.get("contenedor_id");

            Object id =
                    sensor.get("_id");

            if (contenedorId != null
                    && id != null
                    && contenedoresIds.contains(
                            contenedorId.toString()
                    )) {

                sensores.add(
                        id.toString()
                );
            }
        }

        return sensores;
    }

    // =========================================================
    // MOSTRAR DETALLE DE UN PUNTO GPS
    // =========================================================

    private void mostrarDetallePunto(
            java.awt.event.MouseEvent evento
    ) {

        String seleccionado =
                (String) view.getComboEnvios()
                        .getSelectedItem();

        if (seleccionado == null
                || "Seleccioná un envío".equals(
                        seleccionado
                )) {

            return;
        }

        // Comprobar nuevamente los permisos.
        try {

            verificarAccesoEnvio(seleccionado);

        } catch (SecurityException |
                 IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JXMapViewer mapa =
                view.getMapa();

        Rectangle viewport =
                mapa.getViewportBounds();

        List<GPSWaypoint> cercanos =
                new ArrayList<>();

        for (GPSWaypoint punto : puntosVisibles) {

            Point2D pixel =
                    mapa.getTileFactory()
                            .geoToPixel(
                                    punto.getPosition(),
                                    mapa.getZoom()
                            );

            double x =
                    pixel.getX() - viewport.x;

            double y =
                    pixel.getY() - viewport.y;

            if (Math.abs(
                    evento.getX() - x
            ) <= 14
                    && evento.getY() >= y - 28
                    && evento.getY() <= y + 8) {

                cercanos.add(punto);
            }
        }

        if (cercanos.isEmpty()) {
            return;
        }

        if (detallePunto != null) {
            detallePunto.setVisible(false);
        }

        cercanos.sort(
                Comparator.comparing(
                        p -> p.datos.getInstant("fecha_hora"),
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        JTabbedPane lecturas =
                new JTabbedPane();

        for (int i = 0; i < cercanos.size(); i++) {

            GPSWaypoint punto =
                    cercanos.get(i);

            DefaultTableModel modelo =
                    new DefaultTableModel(
                            new String[]{
                                    "Dato",
                                    "Valor"
                            },
                            0
                    ) {

                        @Override
                        public boolean isCellEditable(
                                int row,
                                int column
                        ) {
                            return false;
                        }
                    };

            Document sensor =
                    sensoresPorId.get(
                            punto.datos.getString("sensor_id")
                    );

            if (sensor != null) {

                sensor.forEach(
                        (campo, valor) ->
                                modelo.addRow(
                                        new Object[]{
                                                "Sensor · "
                                                        + nombreCampo(campo),
                                                textoDetalle(valor)
                                        }
                                )
                );

            } else {

                modelo.addRow(
                        new Object[]{
                                "Sensor",
                                seleccionado
                        }
                );

                modelo.addRow(
                        new Object[]{
                                "Información del sensor",
                                "No disponible en MongoDB"
                        }
                );
            }

            for (var columna :
                    punto.datos.getColumnDefinitions()) {

                String campo =
                        columna.getName().asInternal();

                modelo.addRow(
                        new Object[]{
                                "Lectura · "
                                        + nombreCampo(campo),
                                textoDetalle(
                                        punto.datos.getObject(campo)
                                )
                        }
                );
            }

            JTable tabla =
                    new JTable(modelo);

            tabla.setRowHeight(24);

            tabla.getColumnModel()
                    .getColumn(0)
                    .setPreferredWidth(190);

            tabla.getColumnModel()
                    .getColumn(1)
                    .setPreferredWidth(290);

            lecturas.addTab(
                    cercanos.size() == 1
                            ? "Información del punto"
                            : "Lectura " + (i + 1),
                    new JScrollPane(tabla)
            );
        }

        lecturas.setPreferredSize(
                new Dimension(500, 360)
        );

        detallePunto =
                new JPopupMenu();

        detallePunto.add(lecturas);

        detallePunto.show(
                mapa,
                evento.getX(),
                evento.getY()
        );
    }

    // =========================================================
    // NOMBRES DE CAMPOS
    // =========================================================

    private static String nombreCampo(
            String campo
    ) {

        if (campo == null || campo.isEmpty()) {
            return "Campo";
        }

        return switch (campo) {

            case "_id" -> "ID";
            case "sensor_id" -> "Sensor";
            case "contenedor_id" -> "Contenedor";
            case "fecha_dia" -> "Fecha";
            case "fecha_hora" -> "Fecha y hora";
            case "fecha_instalacion" ->
                    "Fecha de instalación";
            case "temperatura" -> "Temperatura";
            case "humedad" -> "Humedad";
            case "vibracion" -> "Vibración";
            case "bateria" -> "Batería";
            case "pais" -> "País";

            default ->
                    Character.toUpperCase(
                            campo.charAt(0)
                    )
                            + campo.substring(1)
                            .replace('_', ' ');
        };
    }

    // =========================================================
    // FORMATEAR VALORES
    // =========================================================

    private static String textoDetalle(
            Object valor
    ) {

        if (valor == null) {
            return "Sin dato";
        }

        if (valor instanceof Instant fecha) {

            return java.time.format.DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm:ss z")
                    .withZone(
                            java.time.ZoneId.of(
                                    "America/Argentina/Buenos_Aires"
                            )
                    )
                    .format(fecha);
        }

        if (valor instanceof java.util.Date fecha) {

            return textoDetalle(
                    fecha.toInstant()
            );
        }

        return valor.toString();
    }

    // =========================================================
    // MOSTRAR POSICIONES GPS
    // =========================================================

    private void mostrarPosiciones(
            List<Row> posiciones
    ) {

        JXMapViewer mapa =
                view.getMapa();

        Set<Waypoint> waypoints =
                new HashSet<>();

        puntosVisibles.clear();

        Map<String, List<PosicionGPS>> posicionesPorSensor =
                new HashMap<>();

        for (Row row : posiciones) {

            try {

                String sensorId =
                        row.getString("sensor_id");

                if (sensorId == null) {
                    continue;
                }

                java.math.BigDecimal latitudValor =
                        row.getBigDecimal("latitud");

                java.math.BigDecimal longitudValor =
                        row.getBigDecimal("longitud");

                if (latitudValor == null
                        || longitudValor == null) {

                    continue;
                }

                double latitud =
                        latitudValor.doubleValue();

                double longitud =
                        longitudValor.doubleValue();

                if (latitud < -90 || latitud > 90) {
                    continue;
                }

                if (longitud < -180 || longitud > 180) {
                    continue;
                }

                Instant fechaHora =
                        row.getInstant("fecha_hora");

                if (fechaHora == null) {

                    AppLog.debug(SeguimientoEnvioPanelController.class,
                            "Se omitió una posición GPS sin fecha_hora.");

                    continue;
                }

                LocalDate fechaDia =
                        LocalDate.ofInstant(
                                fechaHora,
                                java.time.ZoneOffset.UTC
                        );

                PosicionGPS posicion =
                        new PosicionGPS(
                                sensorId,
                                latitud,
                                longitud,
                                fechaDia,
                                fechaHora
                        );

                GPSWaypoint punto =
                        new GPSWaypoint(
                                latitud,
                                longitud,
                                row
                        );

                waypoints.add(punto);

                puntosVisibles.add(punto);

                posicionesPorSensor
                        .computeIfAbsent(
                                sensorId,
                                k -> new ArrayList<>()
                        )
                        .add(posicion);

            } catch (Exception e) {

                AppLog.warning(SeguimientoEnvioPanelController.class,
                        "Se omitió una posición GPS que no pudo procesarse.");
            }
        }

        // =====================================================
        // COLORES PARA CADA SENSOR
        // =====================================================

        List<Color> colores =
                Arrays.asList(
                        new Color(220, 38, 38),
                        new Color(37, 99, 235),
                        new Color(22, 163, 74),
                        new Color(147, 51, 234),
                        new Color(234, 88, 12),
                        new Color(8, 145, 178),
                        new Color(219, 39, 119),
                        new Color(101, 163, 13),
                        new Color(124, 58, 237),
                        new Color(202, 138, 4)
                );

        List<Painter<JXMapViewer>> painters =
                new ArrayList<>();

        int indiceColor = 0;

        // =====================================================
        // CREAR RUTA POR SENSOR
        // =====================================================

        for (Map.Entry<String, List<PosicionGPS>> entry :
                posicionesPorSensor.entrySet()) {

            List<PosicionGPS> ruta =
                    entry.getValue();

            ruta.sort(
                    Comparator.comparing(
                            PosicionGPS::getFechaHora,
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()
                            )
                    )
            );

            Color color =
                    colores.get(
                            indiceColor % colores.size()
                    );

            indiceColor++;

            if (ruta.size() >= 2) {

                List<GeoPosition> puntosRuta =
                        new ArrayList<>();

                for (PosicionGPS posicion : ruta) {

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
        // DIBUJAR MARCADORES
        // =====================================================

        WaypointPainter<Waypoint> waypointPainter =
                new WaypointPainter<>();

        waypointPainter.setWaypoints(
                waypoints
        );

        painters.add(waypointPainter);

        // =====================================================
        // COMBINAR RUTAS Y MARCADORES
        // =====================================================

        CompoundPainter<JXMapViewer> compoundPainter =
                new CompoundPainter<>(
                        painters
                );

        mapa.setOverlayPainter(
                compoundPainter
        );

        mapa.repaint();
    }

    // =========================================================
    // PAINTER DE RUTAS
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
                    new ArrayList<>(track);

            this.color = color;
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

            Rectangle rect =
                    mapa.getViewportBounds();

            graphics.translate(
                    -rect.x,
                    -rect.y
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics.setColor(color);

            graphics.setStroke(
                    new BasicStroke(
                            3f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            Point2D anterior = null;

            for (GeoPosition posicion : track) {

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

                anterior = actual;
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

            this.latitud = latitud;
            this.longitud = longitud;
            this.fechaHora = fechaHora;
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
