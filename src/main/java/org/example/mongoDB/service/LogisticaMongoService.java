package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;
import org.example.conecciones.MongoSingleton;
import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.*;
import org.example.mongoDB.model.*;
import java.util.List;

public class LogisticaMongoService {

    private final ClienteMongoDAO clienteDAO;
    private final UsuarioMongoDAO usuarioDAO;
    private final RolMongoDAO rolDAO;
    private final ContenedorMongoDAO contenedorDAO;
    private final SensorMongoDAO sensorDAO;
    private final EventoLogisticoMongoDAO eventoLogisticoDAO;
    private final IncidenteMongoDAO incidenteDAO;
    private final AlertaMongoDAO alertaDAO;
    private final ReporteMongoDAO reporteDAO;
    private final FacturaMongoDAO facturaDAO;
    private final PagoMongoDAO pagoDAO;
    private final CuentaCorrienteMongoDAO cuentaCorrienteDAO;


    // =========================
    // CONSTRUCTOR
    // =========================

    public LogisticaMongoService() {

        this.clienteDAO = new ClienteMongoDAO();
        this.usuarioDAO = new UsuarioMongoDAO();
        this.rolDAO = new RolMongoDAO();
        this.contenedorDAO = new ContenedorMongoDAO();
        this.sensorDAO = new SensorMongoDAO();
        this.eventoLogisticoDAO = new EventoLogisticoMongoDAO();
        this.incidenteDAO = new IncidenteMongoDAO();
        this.alertaDAO = new AlertaMongoDAO();
        this.reporteDAO = new ReporteMongoDAO();
        this.facturaDAO = new FacturaMongoDAO();
        this.pagoDAO = new PagoMongoDAO();
        this.cuentaCorrienteDAO = new CuentaCorrienteMongoDAO();
    }


    // =========================
    // CLIENTES
    // =========================

    public void agregarCliente(Cliente cliente) {
        clienteDAO.agregar(cliente);
    }

    public void modificarCliente(Cliente cliente) {
        clienteDAO.modificar(cliente);
    }

    public void eliminarCliente(String id) {
        clienteDAO.eliminar(id);
    }

    public Document buscarClientePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarClientePorId", clienteDAO.buscarPorId(id));
    }

    public List<Document> listarClientes() {
        return TablaConsola.mostrar("LogisticaMongoService.listarClientes", clienteDAO.listarTodos());
    }

    public List<String> obtenerIdsClientes() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsClientes", clienteDAO.obtenerTodosLosIds());
    }

    public boolean existeClientePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeClientePorId", clienteDAO.existePorId(id));
    }

    public Document buscarClientePorCuit(String cuit) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarClientePorCuit", clienteDAO.buscarPorCuit(cuit));
    }


    // =========================
    // USUARIOS
    // =========================

    public void agregarUsuario(Usuario usuario) {
        usuarioDAO.agregar(usuario);
    }

    public void modificarUsuario(Usuario usuario) {
        usuarioDAO.modificar(usuario);
    }

    public void eliminarUsuario(String id) {
        usuarioDAO.eliminar(id);
    }

    public Document buscarUsuarioPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarUsuarioPorId", usuarioDAO.buscarPorId(id));
    }

    public List<Document> listarUsuarios() {
        return TablaConsola.mostrar("LogisticaMongoService.listarUsuarios", usuarioDAO.listarTodos());
    }

    public List<String> obtenerIdsUsuarios() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsUsuarios", usuarioDAO.obtenerTodosLosIds());
    }

    public boolean existeUsuarioPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeUsuarioPorId", usuarioDAO.existePorId(id));
    }

    public Document buscarUsuarioPorEmail(String email) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarUsuarioPorEmail", usuarioDAO.buscarPorEmail(email));
    }

    public List<Document> buscarUsuariosPorCliente(String clienteId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarUsuariosPorCliente", usuarioDAO.buscarPorCliente(clienteId));
    }


    // =========================
    // BUSCAR USUARIOS POR ROL
    // =========================

    public List<Document> buscarUsuariosPorRol(String rolId) {

        if (!ObjectId.isValid(rolId)) {
            throw new IllegalArgumentException("El ID del rol no es válido");
        }

        if (!rolDAO.existePorId(rolId)) {
            throw new IllegalArgumentException("El rol no existe");
        }

        return TablaConsola.mostrar("LogisticaMongoService.buscarUsuariosPorRol", usuarioDAO.buscarPorRol(rolId));
    }


    // =========================
    // CAMBIAR ROL DE USUARIO
    // =========================

    public void cambiarRolUsuario(String usuarioId, String rolId) {

        if (!ObjectId.isValid(usuarioId)) {
            throw new IllegalArgumentException(
                    "El ID del usuario no es válido"
            );
        }

        if (!ObjectId.isValid(rolId)) {
            throw new IllegalArgumentException(
                    "El ID del rol no es válido"
            );
        }

        if (!usuarioDAO.existePorId(usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario no existe"
            );
        }

        if (!rolDAO.existePorId(rolId)) {
            throw new IllegalArgumentException(
                    "El rol no existe"
            );
        }

        usuarioDAO.cambiarRol(usuarioId, rolId);
    }


    // =========================
    // BUSCAR ROL DE UN USUARIO
    // =========================

    public Document buscarRolDeUsuario(String usuarioId) {

        if (!ObjectId.isValid(usuarioId)) {
            throw new IllegalArgumentException(
                    "El ID del usuario no es válido"
            );
        }

        Document usuario = usuarioDAO.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe"
            );
        }

        ObjectId rolId = usuario.getObjectId("rol_id");

        if (rolId == null) {
            return TablaConsola.mostrar("LogisticaMongoService.buscarRolDeUsuario", null);
        }

        return TablaConsola.mostrar("LogisticaMongoService.buscarRolDeUsuario", rolDAO.buscarPorId(
                rolId.toHexString()
        ));
    }


    // =========================
    // ROLES
    // =========================

    public void agregarRol(Rol rol) {
        rolDAO.agregar(rol);
    }

    public void modificarRol(Rol rol) {
        rolDAO.modificar(rol);
    }

    public void eliminarRol(String id) {
        rolDAO.eliminar(id);
    }

    public Document buscarRolPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarRolPorId", rolDAO.buscarPorId(id));
    }

    public List<Document> listarRoles() {
        return TablaConsola.mostrar("LogisticaMongoService.listarRoles", rolDAO.listarTodos());
    }

    public List<String> obtenerIdsRoles() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsRoles", rolDAO.obtenerTodosLosIds());
    }

    public boolean existeRolPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeRolPorId", rolDAO.existePorId(id));
    }

    public Document buscarRolPorDescripcion(String descripcion) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarRolPorDescripcion", rolDAO.buscarPorDescripcion(descripcion));
    }


    // =========================
    // CONTENEDORES
    // =========================

    public void agregarContenedor(Contenedor contenedor) {
        contenedorDAO.agregar(contenedor);
    }

    public void modificarContenedor(Contenedor contenedor) {
        contenedorDAO.modificar(contenedor);
    }

    public void eliminarContenedor(String id) {
        contenedorDAO.eliminar(id);
    }

    public Document buscarContenedorPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarContenedorPorId", contenedorDAO.buscarPorId(id));
    }

    public List<Document> listarContenedores() {
        return TablaConsola.mostrar("LogisticaMongoService.listarContenedores", contenedorDAO.listarTodos());
    }

    public List<String> obtenerIdsContenedores() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsContenedores", contenedorDAO.obtenerTodosLosIds());
    }

    public boolean existeContenedorPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeContenedorPorId", contenedorDAO.existePorId(id));
    }

    public Document buscarContenedorPorCodigo(String codigo) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarContenedorPorCodigo", contenedorDAO.buscarPorCodigo(codigo));
    }

    public List<Document> buscarContenedoresPorEstado(String estado) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarContenedoresPorEstado", contenedorDAO.buscarPorEstado(estado));
    }


    // =========================
    // SENSORES
    // =========================

    public void agregarSensor(Sensor sensor) {
        sensorDAO.agregar(sensor);
    }

    public void modificarSensor(Sensor sensor) {
        sensorDAO.modificar(sensor);
    }

    public void eliminarSensor(String id) {
        sensorDAO.eliminar(id);
    }

    public Document buscarSensorPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarSensorPorId", sensorDAO.buscarPorId(id));
    }

    public List<Document> listarSensores() {
        return TablaConsola.mostrar("LogisticaMongoService.listarSensores", sensorDAO.listarTodos());
    }

    public List<String> obtenerIdsSensores() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsSensores", sensorDAO.obtenerTodosLosIds());
    }

    public boolean existeSensorPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeSensorPorId", sensorDAO.existePorId(id));
    }

    public List<Document> buscarSensoresPorContenedor(String contenedorId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarSensoresPorContenedor", sensorDAO.buscarPorContenedor(contenedorId));
    }

    public List<Document> buscarSensoresPorEstado(String estado) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarSensoresPorEstado", sensorDAO.buscarPorEstado(estado));
    }


    // =========================
    // EVENTOS LOGISTICOS
    // =========================

    public void agregarEventoLogistico(EventoLogistico evento) {
        eventoLogisticoDAO.agregar(evento);
    }

    public void modificarEventoLogistico(EventoLogistico evento) {
        eventoLogisticoDAO.modificar(evento);
    }

    public void eliminarEventoLogistico(String id) {
        eventoLogisticoDAO.eliminar(id);
    }

    public Document buscarEventoLogisticoPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarEventoLogisticoPorId", eventoLogisticoDAO.buscarPorId(id));
    }

    public List<Document> listarEventosLogisticos() {
        return TablaConsola.mostrar("LogisticaMongoService.listarEventosLogisticos", eventoLogisticoDAO.listarTodos());
    }

    public List<String> obtenerIdsEventosLogisticos() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsEventosLogisticos", eventoLogisticoDAO.obtenerTodosLosIds());
    }

    public boolean existeEventoLogisticoPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeEventoLogisticoPorId", eventoLogisticoDAO.existePorId(id));
    }

    public List<Document> buscarEventosLogisticosPorEnvio(String envioId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarEventosLogisticosPorEnvio", eventoLogisticoDAO.buscarPorEnvio(envioId));
    }

    public List<Document> buscarEventosLogisticosPorTipo(String tipoEvento) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarEventosLogisticosPorTipo", eventoLogisticoDAO.buscarPorTipo(tipoEvento));
    }


    // =========================
    // INCIDENTES
    // =========================

    public void agregarIncidente(Incidente incidente) {
        incidenteDAO.agregar(incidente);
    }

    public void modificarIncidente(Incidente incidente) {
        incidenteDAO.modificar(incidente);
    }

    public void eliminarIncidente(String id) {
        incidenteDAO.eliminar(id);
    }

    public Document buscarIncidentePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarIncidentePorId", incidenteDAO.buscarPorId(id));
    }

    public List<Document> listarIncidentes() {
        return TablaConsola.mostrar("LogisticaMongoService.listarIncidentes", incidenteDAO.listarTodos());
    }

    public List<String> obtenerIdsIncidentes() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsIncidentes", incidenteDAO.obtenerTodosLosIds());
    }

    public boolean existeIncidentePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeIncidentePorId", incidenteDAO.existePorId(id));
    }

    public List<Document> buscarIncidentesPorEnvio(String envioId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarIncidentesPorEnvio", incidenteDAO.buscarPorEnvio(envioId));
    }

    public List<Document> buscarIncidentesAbiertos() {
        return TablaConsola.mostrar("LogisticaMongoService.buscarIncidentesAbiertos", incidenteDAO.buscarAbiertos());
    }


    // =========================
    // ALERTAS
    // =========================

    public void agregarAlerta(Alerta alerta) {
        alertaDAO.agregar(alerta);
    }

    public void modificarAlerta(Alerta alerta) {
        alertaDAO.modificar(alerta);
    }

    public void eliminarAlerta(String id) {
        alertaDAO.eliminar(id);
    }

    public Document buscarAlertaPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarAlertaPorId", alertaDAO.buscarPorId(id));
    }

    public List<Document> listarAlertas() {
        return TablaConsola.mostrar("LogisticaMongoService.listarAlertas", alertaDAO.listarTodos());
    }

    public List<String> obtenerIdsAlertas() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsAlertas", alertaDAO.obtenerTodosLosIds());
    }

    public boolean existeAlertaPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeAlertaPorId", alertaDAO.existePorId(id));
    }

    public List<Document> buscarAlertasPorSensor(String sensorId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarAlertasPorSensor", alertaDAO.buscarPorSensor(sensorId));
    }

    public List<Document> buscarAlertasPorEvento(String eventoId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarAlertasPorEvento", alertaDAO.buscarPorEvento(eventoId));
    }

    public List<Document> buscarAlertasPorEstado(String estado) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarAlertasPorEstado", alertaDAO.buscarPorEstado(estado));
    }


    // =========================
    // REPORTES
    // =========================

    public void agregarReporte(Reporte reporte) {
        reporteDAO.agregar(reporte);
    }

    public void modificarReporte(Reporte reporte) {
        reporteDAO.modificar(reporte);
    }

    public void eliminarReporte(String id) {
        reporteDAO.eliminar(id);
    }

    public Document buscarReportePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarReportePorId", reporteDAO.buscarPorId(id));
    }

    public List<Document> listarReportes() {
        return TablaConsola.mostrar("LogisticaMongoService.listarReportes", reporteDAO.listarTodos());
    }

    public List<String> obtenerIdsReportes() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsReportes", reporteDAO.obtenerTodosLosIds());
    }

    public boolean existeReportePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeReportePorId", reporteDAO.existePorId(id));
    }

    public List<Document> buscarReportesPorUsuario(String usuarioId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarReportesPorUsuario", reporteDAO.buscarPorUsuario(usuarioId));
    }

    public List<Document> buscarReportesPorTipo(String tipo) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarReportesPorTipo", reporteDAO.buscarPorTipo(tipo));
    }

    public List<Document> buscarReportesPorEstado(String estado) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarReportesPorEstado", reporteDAO.buscarPorEstado(estado));
    }


    // =========================
    // FACTURAS
    // =========================

    public void agregarFactura(Factura factura) {
        facturaDAO.agregar(factura);
    }

    public void modificarFactura(Factura factura) {
        facturaDAO.modificar(factura);
    }

    public void eliminarFactura(String id) {
        facturaDAO.eliminar(id);
    }

    public Document buscarFacturaPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarFacturaPorId", facturaDAO.buscarPorId(id));
    }

    public List<Document> listarFacturas() {
        return TablaConsola.mostrar("LogisticaMongoService.listarFacturas", facturaDAO.listarTodos());
    }

    public List<String> obtenerIdsFacturas() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsFacturas", facturaDAO.obtenerTodosLosIds());
    }

    public boolean existeFacturaPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeFacturaPorId", facturaDAO.existePorId(id));
    }

    public List<Document> buscarFacturasPorCliente(String clienteId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarFacturasPorCliente", facturaDAO.buscarPorCliente(clienteId));
    }

    public List<Document> buscarFacturasPorEstado(String estado) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarFacturasPorEstado", facturaDAO.buscarPorEstado(estado));
    }


    // =========================
    // PAGOS
    // =========================

    public void agregarPago(Pago pago) {
        try (ClientSession session = obtenerSesion()) {
            session.withTransaction(() -> {
                pagoDAO.agregar(session, pago);
                return null;
            });
        }
    }

    public void modificarPago(Pago pago) {
        pagoDAO.modificar(pago);
    }

    public void eliminarPago(String id) {
        pagoDAO.eliminar(id);
    }

    public Document buscarPagoPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarPagoPorId", pagoDAO.buscarPorId(id));
    }

    public List<Document> listarPagos() {
        return TablaConsola.mostrar("LogisticaMongoService.listarPagos", pagoDAO.listarTodos());
    }

    public List<String> obtenerIdsPagos() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsPagos", pagoDAO.obtenerTodosLosIds());
    }

    public boolean existePagoPorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existePagoPorId", pagoDAO.existePorId(id));
    }

    public List<Document> buscarPagosPorFactura(String facturaId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarPagosPorFactura", pagoDAO.buscarPorFactura(facturaId));
    }

    public List<Document> buscarPagosPorMedioPago(String medioPago) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarPagosPorMedioPago", pagoDAO.buscarPorMedioPago(medioPago));
    }


    // =========================
    // CUENTAS CORRIENTES
    // =========================

    public void agregarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        cuentaCorrienteDAO.agregar(cuentaCorriente);
    }

    public void modificarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        cuentaCorrienteDAO.modificar(cuentaCorriente);
    }

    public void eliminarCuentaCorriente(String id) {
        cuentaCorrienteDAO.eliminar(id);
    }

    public Document buscarCuentaCorrientePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarCuentaCorrientePorId", cuentaCorrienteDAO.buscarPorId(id));
    }

    public List<Document> listarCuentasCorrientes() {
        return TablaConsola.mostrar("LogisticaMongoService.listarCuentasCorrientes", cuentaCorrienteDAO.listarTodos());
    }

    public List<String> obtenerIdsCuentasCorrientes() {
        return TablaConsola.mostrar("LogisticaMongoService.obtenerIdsCuentasCorrientes", cuentaCorrienteDAO.obtenerTodosLosIds());
    }

    public boolean existeCuentaCorrientePorId(String id) {
        return TablaConsola.mostrar("LogisticaMongoService.existeCuentaCorrientePorId", cuentaCorrienteDAO.existePorId(id));
    }

    public Document buscarCuentaCorrientePorCliente(String clienteId) {
        return TablaConsola.mostrar("LogisticaMongoService.buscarCuentaCorrientePorCliente", cuentaCorrienteDAO.buscarPorCliente(clienteId));
    }

    /** Crea una sesión desde el cliente compartido. Quien la utiliza debe cerrarla. */
    public static ClientSession obtenerSesion() {
        return MongoSingleton.getInstance().startSession();
    }
}
