package org.example.interfaz.principal.vistas;

import org.example.neo4j.controller.ControllerNeo4j;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.ContenedorController;
import org.example.mongoDB.controller.VehiculoController;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.neo4j.model.Envio;
import org.example.neo4j.model.Ubicacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnviosPanelController {

    private final EnviosPanel view;
    private final ControllerNeo4j neo4j;
    private final ClienteController clienteController;
    private final ContenedorController contenedorController;
    private final VehiculoController vehiculoController;

    private final Map<String, String> clientesPorNombre;
    private final Map<String, String> contenedoresPorCodigo;
    private final Map<String, String> vehiculosPorEtiqueta;

    private final List<String> idsEnvios;

    private String envioIdEnEdicion;

    public EnviosPanelController(EnviosPanel view) {
        this.view = view;
        this.neo4j = new ControllerNeo4j();
        this.clienteController = new ClienteController();
        this.contenedorController = new ContenedorController();
        this.vehiculoController = new VehiculoController();

        this.clientesPorNombre = new LinkedHashMap<>();
        this.contenedoresPorCodigo = new LinkedHashMap<>();
        this.vehiculosPorEtiqueta = new LinkedHashMap<>();
        this.idsEnvios = new ArrayList<>();
        this.envioIdEnEdicion = null;

        configurarEventos();
        actualizarTodo();
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    private void configurarEventos() {
        view.getBtnActualizar()
                .addActionListener(e -> actualizarTodo());

        view.getBtnMostrarFormulario()
                .addActionListener(e -> prepararNuevoEnvio());

        view.getBtnEditar()
                .addActionListener(e -> editarEnvioSeleccionado());

        view.getBtnEliminar()
                .addActionListener(e -> eliminarEnvioSeleccionado());

        view.getBtnCancelar()
                .addActionListener(e -> cancelar());

        view.getBtnGuardar()
                .addActionListener(e -> guardar());
    }

    private void actualizarTodo() {
        cargarClientes();
        cargarContenedores();
        cargarVehiculos();
        cargarEnvios();
    }

    // =====================================================
    // CLIENTES
    // =====================================================

    private void cargarClientes() {
        try {
            List<Document> clientes = clienteController.listarClientes();

            view.getCmbCliente().removeAllItems();
            clientesPorNombre.clear();

            for (Document cliente : clientes) {
                ObjectId objectId = cliente.getObjectId("_id");
                String razonSocial = cliente.getString("razon_social");

                if (objectId == null
                        || razonSocial == null
                        || razonSocial.isBlank()) {
                    continue;
                }

                String id = objectId.toHexString();
                String etiqueta = razonSocial;

                if (clientesPorNombre.containsKey(etiqueta)) {
                    etiqueta = razonSocial + " (" + id + ")";
                }

                view.getCmbCliente().addItem(etiqueta);
                clientesPorNombre.put(etiqueta, id);
            }

        } catch (Exception e) {
            mostrarError(
                    "No se pudieron cargar los clientes desde MongoDB.",
                    e
            );
        }
    }

    // =====================================================
    // CONTENEDORES
    // =====================================================

    private void cargarContenedores() {
        try {
            List<Document> contenedores =
                    contenedorController.listarContenedores();

            contenedoresPorCodigo.clear();

            for (Document contenedor : contenedores) {
                ObjectId objectId = contenedor.getObjectId("_id");
                String codigo =
                        contenedor.getString("codigo_internacional");

                if (objectId == null
                        || codigo == null
                        || codigo.isBlank()) {
                    continue;
                }

                String id = objectId.toHexString();
                String etiqueta = codigo;

                if (contenedoresPorCodigo.containsKey(etiqueta)) {
                    etiqueta = codigo + " (" + id + ")";
                }

                contenedoresPorCodigo.put(etiqueta, id);
            }

            view.setOpcionesContenedores(
                    new ArrayList<>(contenedoresPorCodigo.keySet())
            );

        } catch (Exception e) {
            mostrarError(
                    "No se pudieron cargar los contenedores desde MongoDB.",
                    e
            );
        }
    }

    // =====================================================
    // VEHÍCULOS
    // =====================================================

    private void cargarVehiculos() {
        try {
            List<Document> vehiculos =
                    vehiculoController.listarVehiculos();

            JComboBox<String> combo = view.getCmbVehiculo();

            combo.removeAllItems();
            vehiculosPorEtiqueta.clear();

            String sinVehiculo = "— Sin vehículo asignado —";

            combo.addItem(sinVehiculo);
            vehiculosPorEtiqueta.put(sinVehiculo, null);

            for (Document vehiculo : vehiculos) {
                ObjectId objectId = vehiculo.getObjectId("_id");

                String identificacion =
                        texto(vehiculo.get("identificacion"));

                String tipo = texto(vehiculo.get("tipo"));
                String estado = texto(vehiculo.get("estado"));

                if (objectId == null
                        || identificacion == null
                        || identificacion.isBlank()) {
                    continue;
                }

                // Se excluyen vehículos cuyo estado no sea ACTIVO.
                if (estado != null
                        && !estado.isBlank()
                        && !"ACTIVO".equalsIgnoreCase(estado)) {
                    continue;
                }

                String id = objectId.toHexString();

                String etiqueta =
                        (tipo == null || tipo.isBlank()
                                ? "Vehículo"
                                : tipo)
                                + " - " + identificacion;

                if (vehiculosPorEtiqueta.containsKey(etiqueta)) {
                    etiqueta += " (" + id + ")";
                }

                combo.addItem(etiqueta);
                vehiculosPorEtiqueta.put(etiqueta, id);
            }

        } catch (Exception e) {
            mostrarError(
                    "No se pudieron cargar los vehículos desde MongoDB.",
                    e
            );
        }
    }

    // =====================================================
    // LISTAR ENVÍOS
    // =====================================================

    private void cargarEnvios() {
        try {
            List<Map<String, Object>> envios = neo4j.listarEnvios();

            DefaultTableModel modelo =
                    (DefaultTableModel) view.getTablaEnvios().getModel();

            modelo.setRowCount(0);
            idsEnvios.clear();

            for (Map<String, Object> envio : envios) {
                String id = texto(envio.get("id"));

                if (id == null || id.isBlank()) {
                    continue;
                }

                String cliente = texto(envio.get("cliente"));

                if (cliente == null || cliente.isBlank()) {
                    cliente = nombreClientePorId(
                            texto(envio.get("clienteId"))
                    );
                }

                String contenedores = etiquetasContenedores(envio);

                String vehiculo = etiquetaVehiculo(
                        texto(envio.get("vehiculoId")),
                        texto(envio.get("vehiculo"))
                );

                String fecha =
                        formatearFecha(texto(envio.get("fechaCreacion")));

                String origen = combinarUbicacion(
                        texto(envio.get("ciudadOrigen")),
                        texto(envio.get("paisOrigen"))
                );

                String destino = combinarUbicacion(
                        texto(envio.get("ciudadDestino")),
                        texto(envio.get("paisDestino"))
                );

                modelo.addRow(new Object[]{
                        cliente,
                        contenedores,
                        vehiculo,
                        fecha,
                        origen,
                        destino,
                        texto(envio.get("estado")),
                        texto(envio.get("prioridad"))
                });

                idsEnvios.add(id);
            }

            view.getLblEstado().setText(
                    "Envíos registrados: " + idsEnvios.size()
            );

        } catch (Exception e) {
            mostrarError("No se pudieron cargar los envíos.", e);
        }
    }

    // =====================================================
    // NUEVO ENVÍO
    // =====================================================

    private void prepararNuevoEnvio() {
        envioIdEnEdicion = null;

        cargarClientes();
        cargarContenedores();
        cargarVehiculos();

        view.limpiarFormulario();
        view.prepararNuevoEnvio();
        view.mostrarFormulario();
    }

    // =====================================================
    // EDITAR ENVÍO
    // =====================================================

    private void editarEnvioSeleccionado() {
        int fila = view.getTablaEnvios().getSelectedRow();

        if (fila < 0) {
            advertir("Seleccioná un envío para editar.");
            return;
        }

        fila = view.getTablaEnvios().convertRowIndexToModel(fila);

        if (fila >= idsEnvios.size()) {
            advertir("No se pudo identificar el envío seleccionado.");
            return;
        }

        String id = idsEnvios.get(fila);

        try {
            Map<String, Object> envio = neo4j.buscarEnvioPorId(id);

            if (envio == null) {
                advertir("El envío seleccionado ya no existe.");
                cargarEnvios();
                return;
            }

            envioIdEnEdicion = id;

            cargarClientes();
            cargarContenedores();
            cargarVehiculos();

            seleccionarCliente(texto(envio.get("clienteId")));

            seleccionarContenedores(
                    listaTextos(envio.get("contenedoresIds"))
            );

            view.getTxtCiudadOrigen().setText(
                    valorTexto(envio.get("ciudadOrigen"))
            );

            view.getTxtPaisOrigen().setText(
                    valorTexto(envio.get("paisOrigen"))
            );

            view.getTxtCiudadDestino().setText(
                    valorTexto(envio.get("ciudadDestino"))
            );

            view.getTxtPaisDestino().setText(
                    valorTexto(envio.get("paisDestino"))
            );

            seleccionarCombo(
                    view.getCmbEstado(),
                    texto(envio.get("estado"))
            );

            seleccionarCombo(
                    view.getCmbPrioridad(),
                    texto(envio.get("prioridad"))
            );

            // El vehículo puede venir como ID de MongoDB en la consulta.
            seleccionarVehiculo(texto(envio.get("vehiculoId")));

            view.prepararEdicion();
            view.mostrarFormulario();

        } catch (Exception e) {
            mostrarError("No se pudo cargar el envío para editar.", e);
        }
    }

    private void seleccionarCliente(String clienteId) {
        if (clienteId == null) {
            return;
        }

        for (Map.Entry<String, String> entry
                : clientesPorNombre.entrySet()) {

            if (clienteId.equals(entry.getValue())) {
                view.getCmbCliente().setSelectedItem(entry.getKey());
                return;
            }
        }
    }

    private void seleccionarContenedores(List<String> ids) {
        List<String> etiquetasSeleccionadas = new ArrayList<>();

        for (Map.Entry<String, String> entry
                : contenedoresPorCodigo.entrySet()) {

            if (ids.contains(entry.getValue())) {
                etiquetasSeleccionadas.add(entry.getKey());
            }
        }

        view.seleccionarContenedores(etiquetasSeleccionadas);
    }

    private void seleccionarVehiculo(String vehiculoId) {
        JComboBox<String> combo = view.getCmbVehiculo();

        if (vehiculoId == null || vehiculoId.isBlank()) {
            combo.setSelectedIndex(0);
            return;
        }

        for (Map.Entry<String, String> entry
                : vehiculosPorEtiqueta.entrySet()) {

            if (vehiculoId.equals(entry.getValue())) {
                combo.setSelectedItem(entry.getKey());
                return;
            }
        }

        // Si el vehículo está inactivo o fue eliminado,
        // se deja seleccionada la opción sin vehículo.
        combo.setSelectedIndex(0);
    }

    private void seleccionarCombo(
            JComboBox<String> combo,
            String valor
    ) {
        if (valor != null) {
            combo.setSelectedItem(valor);
        }
    }

    // =====================================================
    // ELIMINAR ENVÍO
    // =====================================================

    private void eliminarEnvioSeleccionado() {
        int fila = view.getTablaEnvios().getSelectedRow();

        if (fila < 0) {
            advertir("Seleccioná un envío para eliminar.");
            return;
        }

        fila = view.getTablaEnvios().convertRowIndexToModel(fila);

        if (fila >= idsEnvios.size()) {
            advertir("No se pudo identificar el envío seleccionado.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                view,
                "¿Seguro que querés eliminar el envío seleccionado?",
                "Eliminar envío",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        String id = idsEnvios.get(fila);

        try {
            neo4j.eliminarEnvio(id);

            if (id.equals(envioIdEnEdicion)) {
                envioIdEnEdicion = null;
                view.limpiarFormulario();
                view.ocultarFormulario();
            }

            JOptionPane.showMessageDialog(
                    view,
                    "Envío eliminado correctamente.",
                    "Envíos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarEnvios();

        } catch (Exception e) {
            mostrarError("No se pudo eliminar el envío.", e);
        }
    }

    // =====================================================
    // GUARDAR
    // =====================================================

    private void guardar() {
        DatosFormulario datos = obtenerDatosFormulario();

        if (datos == null) {
            return;
        }

        Object estadoSeleccionado =
                view.getCmbEstado().getSelectedItem();

        if (estadoSeleccionado == null) {
            advertir("Seleccioná un estado.");
            return;
        }

        String id = envioIdEnEdicion != null
                ? envioIdEnEdicion
                : generarIdHexadecimal();

        String fechaCreacion;

        if (envioIdEnEdicion != null) {
            Map<String, Object> existente = neo4j.buscarEnvioPorId(id);

            fechaCreacion = existente != null
                    ? texto(existente.get("fechaCreacion"))
                    : Instant.now().toString();
        } else {
            fechaCreacion = Instant.now().toString();
        }

        Envio envio = new Envio(
                id,
                datos.clienteId,
                datos.contenedoresIds,
                fechaCreacion,
                new Ubicacion(
                        null,
                        datos.ciudadOrigen,
                        datos.paisOrigen
                ),
                new Ubicacion(
                        null,
                        datos.ciudadDestino,
                        datos.paisDestino
                ),
                estadoSeleccionado.toString(),
                datos.prioridad
        );

        try {
            if (envioIdEnEdicion == null) {
                neo4j.crearEnvio(envio);
            } else {
                neo4j.modificarEnvio(envio);
            }

            Object vehiculoSeleccionado =
                    view.getCmbVehiculo().getSelectedItem();

            String vehiculoIdMongo = vehiculoSeleccionado == null
                    ? null
                    : vehiculosPorEtiqueta.get(
                    vehiculoSeleccionado.toString()
            );

            // Este método debe eliminar la relación anterior si el ID
            // recibido es null. No se lo llama con null aquí porque
            // la firma y el comportamiento actual del DAO deben verificarse.
            if (vehiculoIdMongo != null
                    && !vehiculoIdMongo.isBlank()) {

                neo4j.vincularVehiculoMongoAEnvio(
                        id,
                        vehiculoIdMongo
                );
            }

            JOptionPane.showMessageDialog(
                    view,
                    envioIdEnEdicion == null
                            ? "Envío agregado correctamente."
                            : "Envío modificado correctamente.",
                    "Envíos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            envioIdEnEdicion = null;
            view.limpiarFormulario();
            view.ocultarFormulario();

            cargarEnvios();

        } catch (Exception e) {
            mostrarError("No se pudo guardar el envío.", e);
        }
    }

    // =====================================================
    // VALIDAR FORMULARIO
    // =====================================================

    private DatosFormulario obtenerDatosFormulario() {
        Object clienteSeleccionado =
                view.getCmbCliente().getSelectedItem();

        if (clienteSeleccionado == null) {
            advertir("Seleccioná un cliente.");
            return null;
        }

        String clienteId = clientesPorNombre.get(
                clienteSeleccionado.toString()
        );

        if (clienteId == null) {
            advertir("No se pudo obtener el ID del cliente.");
            return null;
        }

        List<String> seleccionados =
                view.getContenedoresSeleccionados();

        if (seleccionados == null || seleccionados.isEmpty()) {
            advertir("Seleccioná al menos un contenedor.");
            return null;
        }

        List<String> contenedoresIds = new ArrayList<>();

        for (String etiqueta : seleccionados) {
            String id = contenedoresPorCodigo.get(etiqueta);

            if (id != null) {
                contenedoresIds.add(id);
            }
        }

        if (contenedoresIds.isEmpty()) {
            advertir("No se pudieron obtener los IDs de los contenedores.");
            return null;
        }

        String ciudadOrigen =
                view.getTxtCiudadOrigen().getText().trim();

        String paisOrigen =
                view.getTxtPaisOrigen().getText().trim();

        String ciudadDestino =
                view.getTxtCiudadDestino().getText().trim();

        String paisDestino =
                view.getTxtPaisDestino().getText().trim();

        if (ciudadOrigen.isBlank()
                || paisOrigen.isBlank()
                || ciudadDestino.isBlank()
                || paisDestino.isBlank()) {

            advertir(
                    "Completá la ciudad y el país de origen y destino."
            );
            return null;
        }

        Object prioridadSeleccionada =
                view.getCmbPrioridad().getSelectedItem();

        if (prioridadSeleccionada == null) {
            advertir("Seleccioná una prioridad.");
            return null;
        }

        return new DatosFormulario(
                clienteId,
                contenedoresIds,
                ciudadOrigen,
                paisOrigen,
                ciudadDestino,
                paisDestino,
                prioridadSeleccionada.toString()
        );
    }

    // =====================================================
    // CANCELAR
    // =====================================================

    private void cancelar() {
        envioIdEnEdicion = null;
        view.limpiarFormulario();
        view.ocultarFormulario();
    }

    // =====================================================
    // UTILIDADES
    // =====================================================

    private String nombreClientePorId(String id) {
        if (id == null) {
            return "";
        }

        for (Map.Entry<String, String> entry
                : clientesPorNombre.entrySet()) {

            if (id.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return id;
    }

    private String unirLista(Object valor) {
        return String.join(", ", listaTextos(valor));
    }

    private String etiquetasContenedores(Map<String, Object> envio) {
        List<String> etiquetas = listaTextos(envio.get("contenedores"));
        if (!etiquetas.isEmpty()) {
            return unirLista(etiquetas);
        }

        List<String> ids = listaTextos(envio.get("contenedoresIds"));
        List<String> resultado = new ArrayList<>();
        for (String id : ids) {
            resultado.add(etiquetaPorId(contenedoresPorCodigo, id));
        }
        return String.join(", ", resultado);
    }

    private String etiquetaVehiculo(String id, String etiquetaNeo4j) {
        if (etiquetaNeo4j != null && !etiquetaNeo4j.isBlank()) {
            return etiquetaNeo4j;
        }
        if (id == null || id.isBlank()) {
            return "";
        }
        return etiquetaPorId(vehiculosPorEtiqueta, id);
    }

    private String etiquetaPorId(Map<String, String> etiquetasPorNombre, String id) {
        for (Map.Entry<String, String> entry : etiquetasPorNombre.entrySet()) {
            if (id.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return id;
    }

    private List<String> listaTextos(Object valor) {
        List<String> resultado = new ArrayList<>();

        if (valor instanceof List<?> lista) {
            for (Object elemento : lista) {
                if (elemento != null) {
                    resultado.add(elemento.toString());
                }
            }
        }

        return resultado;
    }

    private String formatearFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return "";
        }

        try {
            return LocalDate.parse(fecha.substring(0, 10))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ignored) {
            return fecha;
        }
    }

    private String combinarUbicacion(String ciudad, String pais) {
        ciudad = valorTexto(ciudad);
        pais = valorTexto(pais);

        if (ciudad.isBlank()) {
            return pais;
        }

        if (pais.isBlank()) {
            return ciudad;
        }

        return ciudad + ", " + pais;
    }

    private String texto(Object valor) {
        return valor == null ? null : valor.toString();
    }

    private String valorTexto(Object valor) {
        String resultado = texto(valor);
        return resultado == null ? "" : resultado;
    }

    private String generarIdHexadecimal() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);

        StringBuilder id = new StringBuilder(24);

        for (byte b : bytes) {
            id.append(String.format("%02x", b & 0xff));
        }

        return id.toString();
    }

    private void advertir(String mensaje) {
        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Envíos",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class DatosFormulario {
        private final String clienteId;
        private final List<String> contenedoresIds;
        private final String ciudadOrigen;
        private final String paisOrigen;
        private final String ciudadDestino;
        private final String paisDestino;
        private final String prioridad;

        private DatosFormulario(
                String clienteId,
                List<String> contenedoresIds,
                String ciudadOrigen,
                String paisOrigen,
                String ciudadDestino,
                String paisDestino,
                String prioridad
        ) {
            this.clienteId = clienteId;
            this.contenedoresIds = contenedoresIds;
            this.ciudadOrigen = ciudadOrigen;
            this.paisOrigen = paisOrigen;
            this.ciudadDestino = ciudadDestino;
            this.paisDestino = paisDestino;
            this.prioridad = prioridad;
        }
    }
}
