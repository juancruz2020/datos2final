
package org.example.interfaz.principal.vistas;

import org.example.neo4j.controller.ControllerNeo4j;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.ContenedorController;
import org.example.mongoDB.controller.VehiculoController;
import org.example.mongoDB.service.PermisosService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.neo4j.model.Envio;
import org.example.neo4j.model.Ubicacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private final PermisosService permisosService;

    private final Map<String, String> clientesPorNombre;
    private final Map<String, String> contenedoresPorCodigo;
    private final Map<String, String> vehiculosPorEtiqueta;

    private final List<String> idsEnvios;

    private final String rolActual;
    private final String clienteIdActual;

    private String envioIdEnEdicion;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public EnviosPanelController(
            EnviosPanel view,
            String usuarioId
    ) {

        this.view = view;

        this.neo4j = new ControllerNeo4j();
        this.clienteController = new ClienteController();
        this.contenedorController = new ContenedorController();
        this.vehiculoController = new VehiculoController();
        this.permisosService = new PermisosService();

        this.clientesPorNombre = new LinkedHashMap<>();
        this.contenedoresPorCodigo = new LinkedHashMap<>();
        this.vehiculosPorEtiqueta = new LinkedHashMap<>();
        this.idsEnvios = new ArrayList<>();

        this.envioIdEnEdicion = null;

        this.rolActual = permisosService.obtenerRolUsuario(usuarioId);

        if (!esAdministrador()
                && !esOperador()
                && !esCliente()) {

            throw new SecurityException(
                    "El usuario no tiene permisos para acceder a Envíos."
            );
        }

        if (esCliente()) {

            this.clienteIdActual =
                    permisosService.obtenerClienteId(usuarioId);

            if (clienteIdActual == null
                    || clienteIdActual.isBlank()) {

                throw new SecurityException(
                        "El usuario CLIENTE no tiene una empresa asociada."
                );
            }

        } else {

            this.clienteIdActual = null;
        }

        configurarEventos();
        configurarPermisos();
        actualizarTodo();
    }

    // =====================================================
    // ROLES Y PERMISOS
    // =====================================================

    private boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(rolActual);
    }

    private boolean esOperador() {
        return "OPERADOR".equalsIgnoreCase(rolActual);
    }

    private boolean esCliente() {
        return "CLIENTE".equalsIgnoreCase(rolActual);
    }

    private boolean puedeModificarEnvios() {
        return esAdministrador() || esOperador();
    }

    private void exigirPermisoModificacion() {

        if (!puedeModificarEnvios()) {

            throw new SecurityException(
                    "Tu usuario solamente puede consultar sus propios envíos."
            );
        }
    }

    private void configurarPermisos() {

        boolean puedeModificar = puedeModificarEnvios();

        view.getBtnMostrarFormulario().setVisible(puedeModificar);
        view.getBtnEditar().setVisible(puedeModificar);
        view.getBtnEliminar().setVisible(puedeModificar);

        view.getBtnMostrarFormulario().setEnabled(puedeModificar);
        view.getBtnEditar().setEnabled(puedeModificar);
        view.getBtnEliminar().setEnabled(puedeModificar);

        view.getBtnGuardar().setEnabled(puedeModificar);

        if (!puedeModificar) {
            view.ocultarFormulario();
        }

        view.revalidate();
        view.repaint();
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

        if (esCliente()) {

            cargarClientes();
            cargarEnvios();

        } else {

            cargarClientes();
            cargarContenedores();
            cargarVehiculos();
            cargarEnvios();
        }
    }

    // =====================================================
    // CLIENTES
    // =====================================================

    private void cargarClientes() {

        try {

            List<Document> clientes =
                    clienteController.listarClientes();

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

                // El CLIENTE solamente carga su empresa.
                if (esCliente()
                        && !id.equals(clienteIdActual)) {
                    continue;
                }

                String etiqueta = razonSocial;

                if (clientesPorNombre.containsKey(etiqueta)) {
                    etiqueta = razonSocial + " (" + id + ")";
                }

                view.getCmbCliente().addItem(etiqueta);
                clientesPorNombre.put(etiqueta, id);
            }

            if (esCliente()) {
                view.getCmbCliente().setEnabled(false);
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

        if (!puedeModificarEnvios()) {
            return;
        }

        try {

            Map<String, Object> envio =
                    envioIdEnEdicion == null
                            ? null
                            : neo4j.buscarEnvioPorId(envioIdEnEdicion);

            cargarContenedores(
                    envio == null
                            ? List.of()
                            : listaTextos(envio.get("contenedoresIds"))
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los contenedores del envío.",
                    e
            );
        }
    }

    private void cargarContenedores(
            List<String> asignadosAlEnvio
    ) {

        if (!puedeModificarEnvios()) {
            return;
        }

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
                String estado = texto(contenedor.get("estado"));
                String propietario =
                        texto(contenedor.get("envio_asignado"));

                boolean propioEnUso = "EN_USO".equals(estado)
                        && envioIdEnEdicion != null
                        && (envioIdEnEdicion.equals(propietario)
                        || (propietario == null && asignadosAlEnvio.contains(id)));

                if (!"DISPONIBLE".equals(estado)
                        && !propioEnUso) {
                    continue;
                }

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
        cargarVehiculos(null);
    }

    private void cargarVehiculos(String vehiculoAsignadoId) {

        if (!puedeModificarEnvios()) {
            return;
        }

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

                String id = objectId.toHexString();
                boolean asignadoAEsteEnvio = vehiculoAsignadoId != null
                        && vehiculoAsignadoId.equals(id);

                if (estado != null
                        && !estado.isBlank()
                        && !"ACTIVO".equalsIgnoreCase(estado)
                        && !asignadoAEsteEnvio) {
                    continue;
                }

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

            List<Map<String, Object>> envios =
                    neo4j.listarEnvios();

            DefaultTableModel modelo =
                    (DefaultTableModel) view.getTablaEnvios().getModel();

            modelo.setRowCount(0);
            idsEnvios.clear();

            for (Map<String, Object> envio : envios) {

                String id = texto(envio.get("id"));

                if (id == null || id.isBlank()) {
                    continue;
                }

                String clienteId =
                        texto(envio.get("clienteId"));

                // El CLIENTE solamente ve los envíos
                // asociados a su empresa.
                if (esCliente()) {

                    if (clienteId == null
                            || !clienteIdActual.equals(clienteId)) {
                        continue;
                    }
                }

                String cliente = texto(envio.get("cliente"));

                if (cliente == null || cliente.isBlank()) {

                    cliente = nombreClientePorId(clienteId);
                }

                String contenedores = etiquetasContenedores(envio);

                String vehiculo = etiquetaVehiculo(
                        texto(envio.get("vehiculoId")),
                        texto(envio.get("vehiculo"))
                );

                String fecha = formatearFecha(
                        texto(envio.get("fechaCreacion"))
                );

                String origen = combinarUbicacion(
                        texto(envio.get("ciudadOrigen")),
                        texto(envio.get("paisOrigen"))
                );

                String destino = combinarUbicacion(
                        texto(envio.get("ciudadDestino")),
                        texto(envio.get("paisDestino"))
                );

                modelo.addRow(new Object[]{
                        id,
                        cliente,
                        contenedores,
                        vehiculo,
                        fecha,
                        formatearFecha(texto(envio.get("fechaSalida"))),
                        formatearFecha(texto(envio.get("fechaEntrega"))),
                        origen,
                        destino,
                        texto(envio.get("estado")),
                        texto(envio.get("prioridad")),
                        envio.get("costoEnvio") == null ? "" : envio.get("costoEnvio")
                });

                idsEnvios.add(id);
            }

            view.getLblEstado().setText(
                    "Envíos registrados: " + idsEnvios.size()
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los envíos.",
                    e
            );
        }
    }

    // =====================================================
    // NUEVO ENVÍO
    // =====================================================

    private void prepararNuevoEnvio() {

        exigirPermisoModificacion();

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

        exigirPermisoModificacion();

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

            Map<String, Object> envio =
                    neo4j.buscarEnvioPorId(id);

            if (envio == null) {
                advertir("El envío seleccionado ya no existe.");
                cargarEnvios();
                return;
            }

            envioIdEnEdicion = id;

            cargarClientes();
            cargarContenedores(
                    listaTextos(envio.get("contenedoresIds"))
            );
            cargarVehiculos(texto(envio.get("vehiculoId")));

            seleccionarCliente(
                    texto(envio.get("clienteId")),
                    texto(envio.get("cliente"))
            );

            seleccionarContenedores(
                    listaTextos(envio.get("contenedoresIds")),
                    listaTextos(envio.get("contenedores"))
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
            view.getTxtFechaSalida().setText(
                    valorTexto(envio.get("fechaSalida"))
            );
            view.getTxtFechaEntrega().setText(
                    valorTexto(envio.get("fechaEntrega"))
            );
            view.getTxtCostoEnvio().setText(valorTexto(envio.get("costoEnvio")));

            seleccionarCombo(
                    view.getCmbEstado(),
                    texto(envio.get("estado"))
            );

            seleccionarCombo(
                    view.getCmbPrioridad(),
                    texto(envio.get("prioridad"))
            );

            seleccionarVehiculo(
                    texto(envio.get("vehiculoId")),
                    texto(envio.get("vehiculo"))
            );

            view.prepararEdicion();
            view.mostrarFormulario();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo cargar el envío para editar.",
                    e
            );
        }
    }

    private void seleccionarCliente(String clienteId, String clienteNombre) {
        for (Map.Entry<String, String> entry
                : clientesPorNombre.entrySet()) {

            if (clienteId != null && clienteId.equals(entry.getValue())) {
                view.getCmbCliente().setSelectedItem(
                        entry.getKey()
                );
                return;
            }
        }

        // Compatibilidad con envíos de prueba que preceden a los IDs de MongoDB.
        String nombreNormalizado = normalizar(clienteNombre);
        if (!nombreNormalizado.isBlank()) {
            for (String etiqueta : clientesPorNombre.keySet()) {
                String opcionNormalizada = normalizar(etiqueta);
                if (nombreNormalizado.equals(opcionNormalizada)
                        || nombreNormalizado.startsWith(opcionNormalizada)
                        || opcionNormalizada.startsWith(nombreNormalizado)) {
                    view.getCmbCliente().setSelectedItem(etiqueta);
                    return;
                }
            }
        }
    }

    private void seleccionarContenedores(List<String> ids, List<String> codigos) {

        List<String> etiquetasSeleccionadas =
                new ArrayList<>();

        for (Map.Entry<String, String> entry
                : contenedoresPorCodigo.entrySet()) {

            if (ids.contains(entry.getValue())) {
                etiquetasSeleccionadas.add(entry.getKey());
            }
        }

        // Los fixtures anteriores guardaban el código CONT-### como ID.
        for (String codigo : codigos) {
            for (String etiqueta : contenedoresPorCodigo.keySet()) {
                if (normalizar(codigo).equals(normalizar(etiqueta))
                        && !etiquetasSeleccionadas.contains(etiqueta)) {
                    etiquetasSeleccionadas.add(etiqueta);
                }
            }
        }

        view.seleccionarContenedores(etiquetasSeleccionadas);
    }

    private void seleccionarVehiculo(String vehiculoId, String identificacion) {

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

        String identificacionNormalizada = normalizar(identificacion);
        if (!identificacionNormalizada.isBlank()) {
            for (String etiqueta : vehiculosPorEtiqueta.keySet()) {
                if (normalizar(etiqueta).endsWith(identificacionNormalizada)) {
                    combo.setSelectedItem(etiqueta);
                    return;
                }
            }
        }

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

        exigirPermisoModificacion();

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

            mostrarError(
                    "No se pudo eliminar el envío.",
                    e
            );
        }
    }

    // =====================================================
    // GUARDAR
    // =====================================================

    private void guardar() {

        exigirPermisoModificacion();

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
                : generarIdentificador();

        String fechaCreacion;

        if (envioIdEnEdicion != null) {

            Map<String, Object> existente =
                    neo4j.buscarEnvioPorId(id);

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
                datos.fechaSalida,
                datos.fechaEntrega,
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
        envio.setCostoEnvio(datos.costoEnvio);

        try {

            if (envioIdEnEdicion == null) {
                neo4j.crearEnvio(envio);
            } else {
                neo4j.modificarEnvio(envio);
            }

            Object vehiculoSeleccionado =
                    view.getCmbVehiculo().getSelectedItem();

            String vehiculoIdMongo =
                    vehiculoSeleccionado == null
                            ? null
                            : vehiculosPorEtiqueta.get(
                            vehiculoSeleccionado.toString()
                    );

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

            mostrarError(
                    "No se pudo guardar el envío.",
                    e
            );
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
            advertir(
                    "No se pudieron obtener los IDs de los contenedores."
            );
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

        String fechaSalida = view.getTxtFechaSalida().getText().trim();
        String fechaEntrega = view.getTxtFechaEntrega().getText().trim();
        if (!fechaValida(fechaSalida)) {
            advertir("La fecha de salida debe tener formato AAAA-MM-DD.");
            return null;
        }
        if (!fechaValida(fechaEntrega)) {
            advertir("La fecha de entrega debe tener formato AAAA-MM-DD.");
            return null;
        }
        if (!fechaSalida.isBlank() && !fechaEntrega.isBlank()
                && LocalDate.parse(fechaSalida).isAfter(LocalDate.parse(fechaEntrega))) {
            advertir("La fecha de salida no puede ser posterior a la fecha de entrega.");
            return null;
        }

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

        String costoTexto = view.getTxtCostoEnvio().getText().trim();
        double costoEnvio;
        try {
            costoEnvio = Double.parseDouble(costoTexto.replace(',', '.'));
        } catch (NumberFormatException e) {
            advertir("Ingresá un costo de envío válido.");
            return null;
        }

        return new DatosFormulario(
                clienteId,
                contenedoresIds,
                ciudadOrigen,
                paisOrigen,
                ciudadDestino,
                paisDestino,
                fechaSalida,
                fechaEntrega,
                prioridadSeleccionada.toString(),
                costoEnvio
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

    private String etiquetasContenedores(
            Map<String, Object> envio
    ) {

        List<String> etiquetas =
                listaTextos(envio.get("contenedores"));

        if (!etiquetas.isEmpty()) {
            return unirLista(etiquetas);
        }

        List<String> ids =
                listaTextos(envio.get("contenedoresIds"));

        List<String> resultado = new ArrayList<>();

        for (String id : ids) {

            resultado.add(
                    etiquetaPorId(contenedoresPorCodigo, id)
            );
        }

        return String.join(", ", resultado);
    }

    private String etiquetaVehiculo(
            String id,
            String etiquetaNeo4j
    ) {

        if (etiquetaNeo4j != null
                && !etiquetaNeo4j.isBlank()) {

            return etiquetaNeo4j;
        }

        if (id == null || id.isBlank()) {
            return "";
        }

        return etiquetaPorId(vehiculosPorEtiqueta, id);
    }

    private String etiquetaPorId(
            Map<String, String> etiquetasPorNombre,
            String id
    ) {

        for (Map.Entry<String, String> entry
                : etiquetasPorNombre.entrySet()) {

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
                    .format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    );

        } catch (Exception ignored) {

            return fecha;
        }
    }

    private boolean fechaValida(String fecha) {
        if (fecha == null || fecha.isBlank()) return true;
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase()
                .replaceAll("\\s+", "").replaceAll("[^a-z0-9]", "")
                .replaceAll("(srl|sas|sac|ltda|sa)$", "");
    }

    private String combinarUbicacion(
            String ciudad,
            String pais
    ) {

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

    private String generarIdentificador() {

        int mayor = 0;

        for (Map<String, Object> envio : neo4j.listarEnvios()) {

            String id = texto(envio.get("id"));

            if (id == null || !id.startsWith("ENV-")) {
                continue;
            }

            try {

                mayor = Math.max(
                        mayor,
                        Integer.parseInt(id.substring(4))
                );

            } catch (NumberFormatException ignored) {
                // Ignorar IDs antiguos.
            }
        }

        return String.format("ENV-%03d", mayor + 1);
    }

    private void advertir(String mensaje) {

        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Envíos",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // =====================================================
    // DATOS DEL FORMULARIO
    // =====================================================

    private static class DatosFormulario {

        private final String clienteId;
        private final List<String> contenedoresIds;
        private final String ciudadOrigen;
        private final String paisOrigen;
        private final String ciudadDestino;
        private final String paisDestino;
        private final String fechaSalida;
        private final String fechaEntrega;
        private final String prioridad;
        private final double costoEnvio;

        private DatosFormulario(
                String clienteId,
                List<String> contenedoresIds,
                String ciudadOrigen,
                String paisOrigen,
                String ciudadDestino,
                String paisDestino,
                String fechaSalida,
                String fechaEntrega,
                String prioridad,
                double costoEnvio
        ) {

            this.clienteId = clienteId;
            this.contenedoresIds = contenedoresIds;
            this.ciudadOrigen = ciudadOrigen;
            this.paisOrigen = paisOrigen;
            this.ciudadDestino = ciudadDestino;
            this.paisDestino = paisDestino;
            this.fechaSalida = fechaSalida;
            this.fechaEntrega = fechaEntrega;
            this.prioridad = prioridad;
            this.costoEnvio = costoEnvio;
        }
    }
}
