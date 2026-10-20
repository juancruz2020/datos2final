
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.EnvioController;
import org.example.mongoDB.controller.ReporteController;
import org.example.mongoDB.dao.RolMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.pdf.PDFOpener;
import org.example.pdf.reporte.ReportePDFService;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public class ReportesPanelController {

    private final ReportesPanel vista;
    private final String usuarioId;

    private final ReporteController reporteController;
    private final ClienteController clienteController;
    private final EnvioController envioController;
    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private final ReportePDFService pdfService;

    private final List<String> clientesIds = new ArrayList<>();
    private final Map<String, String> nombresUsuarios = new HashMap<>();

    private String rolActual;
    private String clienteIdActual;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportesPanelController(
            ReportesPanel vista,
            String usuarioId
    ) {
        this.vista = vista;
        this.usuarioId = usuarioId;

        this.reporteController = new ReporteController();
        this.clienteController = new ClienteController();
        this.envioController = new EnvioController();
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
        this.pdfService = new ReportePDFService();

        configurarEventos();

        try {
            cargarUsuarioActual();
            cargarClientes();
            cargarPaises();
            cargarReportes();

        } catch (Exception e) {
            vista.getBtnGenerar().setEnabled(false);
            vista.getBtnActualizar().setEnabled(false);
            mostrarError(e.getMessage());
        }
    }

    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {
        vista.getBtnGenerar().addActionListener(
                e -> generarReporte()
        );

        vista.getBtnActualizar().addActionListener(
                e -> actualizar()
        );
    }

    // =========================================================
    // USUARIO AUTENTICADO
    // =========================================================

    private void cargarUsuarioActual() {

        if (usuarioId == null || !ObjectId.isValid(usuarioId)) {
            throw new SecurityException(
                    "No se pudo identificar al usuario autenticado."
            );
        }

        Document usuario = usuarioDAO.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new SecurityException(
                    "El usuario autenticado no existe."
            );
        }

        ObjectId rolId = usuario.getObjectId("rol_id");

        if (rolId == null) {
            throw new SecurityException(
                    "El usuario no tiene un rol asignado."
            );
        }

        Document rol = rolDAO.buscarPorId(
                rolId.toHexString()
        );

        if (rol == null) {
            throw new SecurityException(
                    "No se encontró el rol del usuario."
            );
        }

        String descripcion = rol.getString("descripcion");

        rolActual = descripcion == null
                ? ""
                : descripcion.trim().toUpperCase(Locale.ROOT);

        if (!rolActual.equals("ADMINISTRADOR")
                && !rolActual.equals("CLIENTE")
                && !rolActual.equals("OPERADOR")) {

            throw new SecurityException(
                    "Rol sin permisos definidos para Reportes: "
                            + rolActual
            );
        }

        ObjectId clienteId = usuario.getObjectId("cliente_id");

        clienteIdActual = clienteId == null
                ? null
                : clienteId.toHexString();

        String nombre = texto(usuario.getString("nombre"));
        String apellido = texto(usuario.getString("apellido"));
        String email = texto(usuario.getString("email"));

        String nombreCompleto = (nombre + " " + apellido).trim();
        String etiqueta;

        if (!nombreCompleto.isEmpty() && !email.isEmpty()) {
            etiqueta = nombreCompleto + " (" + email + ")";
        } else if (!nombreCompleto.isEmpty()) {
            etiqueta = nombreCompleto;
        } else if (!email.isEmpty()) {
            etiqueta = email;
        } else {
            etiqueta = usuarioId;
        }

        vista.getTxtUsuario().setText(etiqueta);

        if (rolActual.equals("OPERADOR")) {
            vista.getBtnGenerar().setEnabled(false);
            vista.getBtnActualizar().setEnabled(false);

            throw new SecurityException(
                    "El rol OPERADOR no tiene acceso a Reportes."
            );
        }

        if (rolActual.equals("CLIENTE")
                && (clienteIdActual == null
                || clienteIdActual.isBlank())) {

            throw new SecurityException(
                    "Tu usuario no tiene un cliente asociado."
            );
        }

        // El cliente no puede generar reportes de sensores.
        if ("CLIENTE".equals(rolActual)) {
            vista.getComboTipo().removeItem("Estado de sensores");
        }
    }

    // =========================================================
    // CARGAR CLIENTES
    // =========================================================

    private void cargarClientes() {

        vista.getComboCliente().removeAllItems();
        clientesIds.clear();

        List<Document> clientes =
                clienteController.listarClientes();

        for (Document cliente : clientes) {

            ObjectId id = cliente.getObjectId("_id");

            if (id == null) {
                continue;
            }

            String idCliente = id.toHexString();

            if (rolActual.equals("CLIENTE")
                    && !idCliente.equals(clienteIdActual)) {
                continue;
            }

            String nombre = cliente.getString("razon_social");

            if (nombre == null || nombre.isBlank()) {
                nombre = idCliente;
            }

            clientesIds.add(idCliente);
            vista.getComboCliente().addItem(nombre);
        }

        if (rolActual.equals("CLIENTE")) {

            if (clientesIds.isEmpty()) {
                throw new SecurityException(
                        "No se encontró el cliente asociado a tu usuario."
                );
            }

            vista.getComboCliente().setSelectedIndex(0);
            vista.getComboCliente().setEnabled(false);

        } else {
            vista.getComboCliente().setEnabled(true);
        }
    }

    // =========================================================
    // CARGAR PAISES
    // =========================================================

    private void cargarPaises() {

        vista.getComboPais().removeAllItems();

        List<Document> envios;

        if (rolActual.equals("CLIENTE")) {
            envios = envioController.buscarPorCliente(
                    clienteIdActual
            );
        } else {
            envios = envioController.listarEnvios();
        }

        TreeSet<String> paises = new TreeSet<>();

        for (Document envio : envios) {

            Document origen = envio.get(
                    "origen",
                    Document.class
            );

            Document destino = envio.get(
                    "destino",
                    Document.class
            );

            if (origen != null) {
                String paisOrigen = origen.getString("pais");

                if (paisOrigen != null && !paisOrigen.isBlank()) {
                    paises.add(paisOrigen);
                }
            }

            if (destino != null) {
                String paisDestino = destino.getString("pais");

                if (paisDestino != null && !paisDestino.isBlank()) {
                    paises.add(paisDestino);
                }
            }
        }

        for (String pais : paises) {
            vista.getComboPais().addItem(pais);
        }
    }

    // =========================================================
    // GENERAR REPORTE
    // =========================================================

    private void generarReporte() {

        try {
            cargarUsuarioActual();

            if (rolActual.equals("OPERADOR")) {
                throw new SecurityException(
                        "No tenés permisos para generar reportes."
                );
            }

            String tipoSeleccionado =
                    (String) vista.getComboTipo().getSelectedItem();

            if (tipoSeleccionado == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un tipo de reporte."
                );
            }

            File archivo;
            String tipo;

            // =================================================
            // ENVIOS POR CLIENTE
            // =================================================

            if ("Envíos por cliente".equals(tipoSeleccionado)) {

                int indiceCliente =
                        vista.getComboCliente().getSelectedIndex();

                if (indiceCliente < 0
                        || indiceCliente >= clientesIds.size()) {
                    throw new IllegalArgumentException(
                            "Debe seleccionar un cliente."
                    );
                }

                String clienteId = clientesIds.get(indiceCliente);

                if (rolActual.equals("CLIENTE")
                        && !clienteId.equals(clienteIdActual)) {
                    throw new SecurityException(
                            "No podés generar reportes de otro cliente."
                    );
                }

                String nombreCliente =
                        (String) vista.getComboCliente().getSelectedItem();

                archivo = pdfService.generarEnviosPorCliente(
                        clienteId,
                        nombreCliente
                );

                tipo = "ENVIOS_POR_CLIENTE";

            // =================================================
            // ENVIOS POR ESTADO
            // =================================================

            } else if ("Envíos por estado".equals(tipoSeleccionado)) {

                String estado =
                        (String) vista.getComboEstado().getSelectedItem();

                if (estado == null || estado.isBlank()) {
                    throw new IllegalArgumentException(
                            "Debe seleccionar un estado."
                    );
                }

                if (rolActual.equals("ADMINISTRADOR")) {
                    archivo = pdfService.generarEnviosPorEstado(
                            estado
                    );
                } else {
                    String nombreCliente =
                            (String) vista.getComboCliente().getSelectedItem();

                    archivo = pdfService.generarEnviosPorEstado(
                            estado,
                            clienteIdActual,
                            nombreCliente
                    );
                }

                tipo = "ENVIOS_POR_ESTADO";

            // =================================================
            // ENVIOS POR PAIS
            // =================================================

            } else if ("Envíos por país".equals(tipoSeleccionado)) {

                String pais =
                        (String) vista.getComboPais().getSelectedItem();

                if (pais == null || pais.isBlank()) {
                    throw new IllegalArgumentException(
                            "Debe seleccionar un país."
                    );
                }

                if (rolActual.equals("ADMINISTRADOR")) {
                    archivo = pdfService.generarEnviosPorPais(pais);
                } else {
                    String nombreCliente =
                            (String) vista.getComboCliente().getSelectedItem();

                    archivo = pdfService.generarEnviosPorPais(
                            pais,
                            clienteIdActual,
                            nombreCliente
                    );
                }

                tipo = "ENVIOS_POR_PAIS";

            // =================================================
            // ENVIOS DEMORADOS
            // =================================================

            } else if ("Envíos demorados".equals(tipoSeleccionado)) {

                if (rolActual.equals("ADMINISTRADOR")) {
                    archivo = pdfService.generarEnviosDemorados();
                } else {
                    String nombreCliente =
                            (String) vista.getComboCliente().getSelectedItem();

                    archivo = pdfService.generarEnviosDemorados(
                            clienteIdActual,
                            nombreCliente
                    );
                }

                tipo = "ENVIOS_DEMORADOS";

            // =================================================
            // INCIDENTES ABIERTOS
            // =================================================

            } else if ("Incidentes abiertos".equals(tipoSeleccionado)) {

                if (rolActual.equals("ADMINISTRADOR")) {
                    archivo = pdfService.generarIncidentesAbiertos();
                } else {
                    String nombreCliente =
                            (String) vista.getComboCliente().getSelectedItem();

                    archivo = pdfService.generarIncidentesAbiertos(
                            clienteIdActual,
                            nombreCliente
                    );
                }

                tipo = "INCIDENTES_ABIERTOS";

            // =================================================
            // PROMEDIO DE INCIDENTES
            // =================================================

            } else if (
                    "Promedio de incidentes por país".equals(tipoSeleccionado)
                    || "Promedio de incidentes por estado".equals(tipoSeleccionado)
                    || "Promedio de incidentes por cliente".equals(tipoSeleccionado)
            ) {

                String agrupacion;

                if ("Promedio de incidentes por país".equals(tipoSeleccionado)) {
                    agrupacion = "PAIS";
                } else if ("Promedio de incidentes por estado".equals(tipoSeleccionado)) {
                    agrupacion = "ESTADO";
                } else {
                    agrupacion = "CLIENTE";
                }

                if (rolActual.equals("ADMINISTRADOR")) {

                    archivo = pdfService.generarPromedioIncidentes(
                            agrupacion
                    );

                } else {

                    String nombreCliente =
                            (String) vista.getComboCliente().getSelectedItem();

                    archivo = pdfService.generarPromedioIncidentes(
                            agrupacion,
                            clienteIdActual,
                            nombreCliente
                    );
                }

                tipo = "PROMEDIO_INCIDENTES_POR_" + agrupacion;

            // =================================================
            // RANKING DE RUTAS
            // =================================================

            } else if (
                    "Ranking de rutas con más incidentes".equals(tipoSeleccionado)
            ) {

                if (rolActual.equals("ADMINISTRADOR")) {
                    archivo = pdfService.generarRankingRutasIncidentes();
                } else {
                    String nombreCliente =
                            (String) vista.getComboCliente().getSelectedItem();

                    archivo = pdfService.generarRankingRutasIncidentes(
                            clienteIdActual,
                            nombreCliente
                    );
                }

                tipo = "RANKING_RUTAS_INCIDENTES";

            // =================================================
            // TIEMPOS MINIMOS Y MAXIMOS
            // =================================================

            } else if (
                    "Tiempos mínimos y máximos por cliente".equals(tipoSeleccionado)
            ) {

                String clienteId = rolActual.equals("CLIENTE")
                        ? clienteIdActual
                        : null;

                archivo = pdfService.generarTiemposPorCliente(
                        clienteId
                );

                tipo = "TIEMPOS_POR_CLIENTE";

            // =================================================
            // TIEMPO PROMEDIO DE ENTREGA
            // =================================================

            } else if (
                    "Tiempo promedio de entrega".equals(tipoSeleccionado)
            ) {

                String clienteId = rolActual.equals("CLIENTE")
                        ? clienteIdActual
                        : null;

                archivo = pdfService.generarTiempoPromedioEntrega(
                        clienteId
                );

                tipo = "TIEMPO_PROMEDIO_ENTREGA";

            // =================================================
            // COSTOS POR TIPO DE TRANSPORTE - NUEVO
            // =================================================

            } else if (
                    "Costos por tipo de transporte".equals(tipoSeleccionado)
            ) {

                String clienteId = rolActual.equals("CLIENTE")
                        ? clienteIdActual
                        : null;

                String nombreCliente = rolActual.equals("CLIENTE")
                        ? (String) vista.getComboCliente().getSelectedItem()
                        : null;

                archivo = pdfService.generarCostosPorTipoTransporte(
                        clienteId,
                        nombreCliente
                );

                tipo = "COSTOS_POR_TIPO_TRANSPORTE";

            // =================================================
            // ESTADO DE SENSORES - SOLO ADMINISTRADOR
            // =================================================

            } else if ("Estado de sensores".equals(tipoSeleccionado)) {

                if (!rolActual.equals("ADMINISTRADOR")) {
                    throw new SecurityException(
                            "No tenés permisos para generar el reporte de sensores."
                    );
                }

                archivo = pdfService.generarEstadoSensores();
                tipo = "ESTADO_SENSORES";

            } else {

                throw new IllegalArgumentException(
                        "Tipo de reporte no reconocido."
                );
            }

            // =================================================
            // VERIFICAR QUE SE GENERO EL PDF
            // =================================================

            if (archivo == null || !archivo.isFile()) {
                throw new IllegalStateException(
                        "No se pudo crear el archivo PDF."
                );
            }

            // =================================================
            // REGISTRAR REPORTE EN MONGODB
            // =================================================

            reporteController.crearReporte(
                    usuarioId,
                    tipo,
                    "PDF"
            );

            cargarReportes();

            JOptionPane.showMessageDialog(
                    vista,
                    "Reporte generado correctamente.\n\n"
                            + archivo.getAbsolutePath(),
                    "Reporte generado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // =================================================
            // ABRIR PDF
            // =================================================

            try {

                new PDFOpener().abrir(archivo);

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        vista,
                        "El PDF fue generado, pero no se pudo abrir.\n"
                                + archivo.getAbsolutePath(),
                        "PDF guardado",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    // =========================================================
    // HISTORIAL DE REPORTES
    // =========================================================

    private void cargarReportes() {

        vista.getModeloTabla().setRowCount(0);
        nombresUsuarios.clear();

        List<Document> reportes =
                reporteController.listarReportes();

        SimpleDateFormat formato =
                new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Document reporte : reportes) {

            ObjectId id = reporte.getObjectId("_id");
            ObjectId idUsuario = reporte.getObjectId("usuario_id");

            if (idUsuario == null) {
                continue;
            }

            String idUsuarioTexto = idUsuario.toHexString();

            if (rolActual.equals("CLIENTE")
                    && !usuarioId.equals(idUsuarioTexto)) {
                continue;
            }

            String nombreUsuario =
                    obtenerNombreUsuario(idUsuarioTexto);

            Date fecha = reporte.getDate("fecha_generacion");

            vista.getModeloTabla().addRow(
                    new Object[]{
                            id == null ? "" : id.toHexString(),
                            nombreUsuario,
                            reporte.getString("tipo"),
                            fecha == null ? "" : formato.format(fecha),
                            reporte.getString("formato"),
                            reporte.getString("estado")
                    }
            );
        }
    }

    // =========================================================
    // OBTENER NOMBRE DE USUARIO
    // =========================================================

    private String obtenerNombreUsuario(String id) {

        if (nombresUsuarios.containsKey(id)) {
            return nombresUsuarios.get(id);
        }

        Document usuario = usuarioDAO.buscarPorId(id);

        if (usuario == null) {
            return id;
        }

        String nombre = texto(usuario.getString("nombre"));
        String apellido = texto(usuario.getString("apellido"));

        String nombreCompleto = (nombre + " " + apellido).trim();

        if (nombreCompleto.isEmpty()) {
            nombreCompleto = texto(usuario.getString("email"));
        }

        if (nombreCompleto.isEmpty()) {
            nombreCompleto = id;
        }

        nombresUsuarios.put(id, nombreCompleto);

        return nombreCompleto;
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    private void actualizar() {

        try {
            cargarUsuarioActual();
            cargarClientes();
            cargarPaises();
            cargarReportes();

        } catch (Exception e) {
            vista.getBtnGenerar().setEnabled(false);
            mostrarError(e.getMessage());
        }
    }

    // =========================================================
    // METODOS AUXILIARES
    // =========================================================

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                vista,
                mensaje == null
                        ? "Ocurrió un error."
                        : mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
