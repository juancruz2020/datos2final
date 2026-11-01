
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import com.mongodb.client.ClientSession;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;

import org.example.mongoDB.controller.ControllerMongoDB;
import org.example.mongoDB.dao.ClienteMongoDAO;

import org.example.mongoDB.model.Cliente;
import org.example.mongoDB.model.Direccion;

import java.util.List;
import java.util.Locale;

public class ClienteService {

    private final ClienteMongoDAO clienteDAO;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ClienteService() {
        this.clienteDAO = new ClienteMongoDAO();
    }

    // ==========================================
    // CREAR CLIENTE
    // ==========================================

    public void crearCliente(
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            String calle,
            String numero,
            String ciudad,
            String codigoPostal,
            String pais
    ) {

        ValidacionesRegistro.datosEmpresa(
                razonSocial,
                cuit,
                email,
                telefono,
                calle,
                numero,
                ciudad,
                codigoPostal,
                pais
        );

        String cuitNormalizado =
                ValidacionesRegistro.normalizarCuit(cuit);

        verificarCuitDuplicado(
                cuitNormalizado,
                null
        );

        Direccion direccion = new Direccion(
                calle.trim(),
                numero.trim(),
                ciudad.trim(),
                codigoPostal.trim().toUpperCase(Locale.ROOT)
        );

        Cliente cliente = new Cliente(
                null,
                razonSocial.trim(),
                cuitNormalizado,
                email.trim(),
                telefono.trim(),
                direccion,
                pais.trim(),
                "ACTIVO"
        );

        CuentaCorrienteService cuentaService =
                new CuentaCorrienteService();

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                clienteDAO.agregar(session, cliente);

                cuentaService.crearCuenta(
                        session,
                        cliente.getId()
                );

                session.commitTransaction();

                AppLog.info(
                        ClienteService.class,
                        "Cliente y cuenta corriente creados correctamente."
                );

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // ==========================================
    // MODIFICAR CLIENTE
    // ==========================================

    public void modificarCliente(
            String clienteId,
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            String calle,
            String numero,
            String ciudad,
            String codigoPostal,
            String pais,
            String estado
    ) {

        Document actual = obtenerCliente(clienteId);

        ValidacionesRegistro.datosEmpresa(
                razonSocial,
                cuit,
                email,
                telefono,
                calle,
                numero,
                ciudad,
                codigoPostal,
                pais
        );

        validarEstado(estado);

        // Si se intenta pasar de ACTIVO a INACTIVO,
        // verificar primero facturas y envíos.

        if ("INACTIVO".equalsIgnoreCase(estado.trim())
                && !"INACTIVO".equalsIgnoreCase(
                        actual.getString("estado"))) {

            validarBajaCliente(clienteId);
        }

        String cuitNormalizado =
                ValidacionesRegistro.normalizarCuit(cuit);

        verificarCuitDuplicado(
                cuitNormalizado,
                clienteId
        );

        Direccion direccion = new Direccion(
                calle.trim(),
                numero.trim(),
                ciudad.trim(),
                codigoPostal.trim().toUpperCase(Locale.ROOT)
        );

        Cliente cliente = new Cliente(
                clienteId,
                razonSocial.trim(),
                cuitNormalizado,
                email.trim(),
                telefono.trim(),
                direccion,
                pais.trim(),
                estado.trim().toUpperCase(Locale.ROOT)
        );

        clienteDAO.modificar(cliente);

        AppLog.info(
                ClienteService.class,
                "Cliente modificado correctamente."
        );
    }

    // ==========================================
    // ELIMINAR CLIENTE (BAJA LÓGICA)
    // ==========================================

    public void eliminarCliente(String clienteId) {

        cambiarEstadoCliente(
                clienteId,
                "INACTIVO"
        );

        AppLog.info(
                ClienteService.class,
                "Cliente desactivado correctamente."
        );
    }

    // ==========================================
    // REACTIVAR CLIENTE
    // ==========================================

    public void reactivarCliente(String clienteId) {

        cambiarEstadoCliente(
                clienteId,
                "ACTIVO"
        );

        AppLog.info(
                ClienteService.class,
                "Cliente reactivado correctamente."
        );
    }

    // ==========================================
    // CAMBIAR ESTADO DEL CLIENTE
    // ==========================================

    public void cambiarEstadoCliente(
            String clienteId,
            String estado
    ) {

        Document actual = obtenerCliente(clienteId);

        validarEstado(estado);

        // La baja solo se permite si no existen
        // facturas impagas ni envíos activos.

        if ("INACTIVO".equalsIgnoreCase(estado.trim())
                && !"INACTIVO".equalsIgnoreCase(
                        actual.getString("estado"))) {

            validarBajaCliente(clienteId);
        }

        Document direccion =
                actual.get("direccion", Document.class);

        if (direccion == null) {

            throw new IllegalStateException(
                    "El cliente no tiene una dirección válida."
            );
        }

        // Se conservan los datos históricos.
        // No se vuelve a validar el CUIT anterior.

        Cliente cliente = new Cliente(
                clienteId,
                actual.getString("razon_social"),
                actual.getString("cuit"),
                actual.getString("email"),
                actual.getString("telefono"),
                new Direccion(
                        direccion.getString("calle"),
                        direccion.getString("numero"),
                        direccion.getString("ciudad"),
                        direccion.getString("codigo_postal")
                ),
                actual.getString("pais"),
                estado.trim().toUpperCase(Locale.ROOT)
        );

        clienteDAO.modificar(cliente);
    }

    // ==========================================
    // VALIDAR BAJA LÓGICA DEL CLIENTE
    // ==========================================

    private void validarBajaCliente(String clienteId) {

        // Utilizamos las consultas existentes.
        // No modificamos Facturación ni Neo4j.

        ControllerMongoDB mongo = new ControllerMongoDB();

        List<Document> facturas =
                mongo.buscarFacturasPorCliente(clienteId);

        if (facturas == null) {
            throw new IllegalStateException(
                    "No se pudieron consultar las facturas del cliente."
            );
        }

        // Comprobar facturas impagas.

        for (Document factura : facturas) {

            String estadoFactura =
                    factura.getString("estado");

            // Solo una factura PAGADA permite continuar.
            // Incluye como bloqueo las facturas pendientes,
            // vencidas, parcialmente pagadas o sin estado.

            if (estadoFactura == null
                    || !"PAGADA".equalsIgnoreCase(
                            estadoFactura.trim())) {

                throw new IllegalArgumentException(
                        "No se puede desactivar el cliente "
                                + "porque tiene facturas sin pagar. "
                                + "Primero debe regularizar sus deudas."
                );
            }
        }

        // Comprobar envíos pendientes o en curso.

        List<Document> envios =
                mongo.buscarEnviosPorCliente(clienteId);

        if (envios == null) {
            throw new IllegalStateException(
                    "No se pudieron consultar los envíos del cliente."
            );
        }

        for (Document envio : envios) {

            String estadoEnvio =
                    envio.getString("estado");

            // Únicamente aceptamos envíos finalizados.
            // Un estado desconocido también bloquea la baja.

            boolean finalizado =
                    estadoEnvio != null
                            && (
                            "ENTREGADO".equalsIgnoreCase(
                                    estadoEnvio.trim())
                            || "CANCELADO".equalsIgnoreCase(
                                    estadoEnvio.trim())
                    );

            if (!finalizado) {

                throw new IllegalArgumentException(
                        "No se puede desactivar el cliente "
                                + "porque tiene envíos pendientes "
                                + "o en curso. "
                                + "Primero debe finalizar "
                                + "sus operaciones."
                );
            }
        }
    }

    // ==========================================
    // LISTAR CLIENTES
    // ==========================================

    public List<Document> listarClientes() {

        return TablaConsola.mostrar(
                "ClienteService.listarClientes",
                clienteDAO.listarTodos()
        );
    }

    // ==========================================
    // BUSCAR CLIENTE POR ID
    // ==========================================

    public Document buscarPorId(String clienteId) {

        Document cliente = obtenerCliente(clienteId);

        return TablaConsola.mostrar(
                "ClienteService.buscarPorId",
                cliente
        );
    }

    // ==========================================
    // BUSCAR CLIENTE POR CUIT
    // ==========================================

    public Document buscarPorCuit(String cuit) {

        // Permite buscar CUIT históricos ficticios.

        ValidacionesRegistro.obligatorio(cuit, "CUIT");

        String cuitNormalizado =
                limpiarGuionesCuit(cuit);

        Document encontrado = null;

        for (Document cliente : clienteDAO.listarTodos()) {

            String cuitGuardado =
                    cliente.getString("cuit");

            if (cuitGuardado == null) {
                continue;
            }

            String cuitExistente =
                    limpiarGuionesCuit(cuitGuardado);

            if (cuitExistente.equals(cuitNormalizado)) {

                encontrado = cliente;
                break;
            }
        }

        return TablaConsola.mostrar(
                "ClienteService.buscarPorCuit",
                encontrado
        );
    }

    // ==========================================
    // OBTENER CLIENTE EXISTENTE
    // ==========================================

    private Document obtenerCliente(String clienteId) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        Document cliente =
                clienteDAO.buscarPorId(clienteId);

        if (cliente == null) {

            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }

        return cliente;
    }

    // ==========================================
    // VERIFICAR CUIT DUPLICADO
    // ==========================================

    private void verificarCuitDuplicado(
            String cuitNormalizado,
            String clienteId
    ) {

        for (Document cliente : clienteDAO.listarTodos()) {

            String cuitGuardado =
                    cliente.getString("cuit");

            if (cuitGuardado == null) {
                continue;
            }

            String cuitExistente =
                    limpiarGuionesCuit(cuitGuardado);

            if (!cuitExistente.equals(cuitNormalizado)) {
                continue;
            }

            ObjectId id =
                    cliente.getObjectId("_id");

            if (clienteId != null
                    && id != null
                    && id.toHexString().equals(clienteId)) {

                continue;
            }

            throw new IllegalArgumentException(
                    "Ya existe otro cliente con ese CUIT."
            );
        }
    }

    // ==========================================
    // LIMPIAR GUIONES DE CUIT
    // ==========================================

    private String limpiarGuionesCuit(String cuit) {

        if (cuit == null) {
            return "";
        }

        return cuit.trim()
                .replace("-", "")
                .replace(" ", "");
    }

    // ==========================================
    // VALIDAR ESTADO
    // ==========================================

    private void validarEstado(String estado) {

        if (estado == null
                || (!"ACTIVO".equalsIgnoreCase(estado.trim())
                && !"INACTIVO".equalsIgnoreCase(estado.trim()))) {

            throw new IllegalArgumentException(
                    "El estado debe ser ACTIVO o INACTIVO."
            );
        }
    }

    // ==========================================
    // VALIDAR OBJECT ID
    // ==========================================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(mensaje);
        }
    }
}
