package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import com.mongodb.client.ClientSession;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.dao.FacturaMongoDAO;
import org.example.mongoDB.model.Cliente;
import org.example.mongoDB.model.Direccion;
import org.example.neo4j.controller.ControllerNeo4j;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.CierreMensualCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.PagoMongoDAO;
import org.example.mongoDB.dao.UsuarioMongoDAO;
import org.example.mongoDB.dao.ConversacionMongoDAO;
import org.example.mongoDB.dao.MensajeMongoDAO;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public class ClienteService {

    private final ClienteMongoDAO clienteDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ClienteService() {

        this.clienteDAO =
                new ClienteMongoDAO();
    }


    // =========================================================
    // CREAR CLIENTE
    // =========================================================

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

        validarDatos(
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


        // -----------------------------------------------------
        // CUIT DUPLICADO
        // -----------------------------------------------------

        Document existente =
                clienteDAO.buscarPorCuit(
                        cuit.trim()
                );

        if (existente != null) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente con ese CUIT."
            );
        }


        // -----------------------------------------------------
        // DIRECCIÓN
        // -----------------------------------------------------

        Direccion direccion =
                new Direccion(
                        calle.trim(),
                        numero.trim(),
                        ciudad.trim(),
                        codigoPostal.trim()
                );


        // -----------------------------------------------------
        // CLIENTE
        // -----------------------------------------------------

        Cliente cliente =
                new Cliente(
                        null,
                        razonSocial.trim(),
                        cuit.trim(),
                        email.trim(),
                        telefono.trim(),
                        direccion,
                        pais.trim(),
                        "ACTIVO"
                );


        // -----------------------------------------------------
        // GUARDAR
        // -----------------------------------------------------

        CuentaCorrienteService cuentaService =
                new CuentaCorrienteService();

        try (ClientSession session = MongoSingleton
                .getInstance()
                .startSession()) {
            session.startTransaction();
            try {
                clienteDAO.agregar(session, cliente);
                cuentaService.crearCuenta(session, cliente.getId());
                session.commitTransaction();
                AppLog.info(ClienteService.class,
                        "Alta de cliente y cuenta corriente confirmada.");
            } catch (RuntimeException e) {
                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                    AppLog.warning(ClienteService.class,
                            "Alta de cliente revertida por un error.");
                }
                throw e;
            }
        }
    }


    // =========================================================
    // MODIFICAR CLIENTE
    // =========================================================

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

        // -----------------------------------------------------
        // VALIDAR ID
        // -----------------------------------------------------

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        Document clienteActual =
                clienteDAO.buscarPorId(
                        clienteId
                );


        if (clienteActual == null) {

            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DATOS
        // -----------------------------------------------------

        validarDatos(
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


        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR CUIT DUPLICADO
        // -----------------------------------------------------

        Document clienteConCuit =
                clienteDAO.buscarPorCuit(
                        cuit.trim()
                );


        if (clienteConCuit != null) {

            ObjectId idExistente =
                    clienteConCuit.getObjectId(
                            "_id"
                    );


            if (idExistente != null
                    && !idExistente.toHexString()
                    .equals(clienteId)) {

                throw new IllegalArgumentException(
                        "Ya existe otro cliente con ese CUIT."
                );
            }
        }


        // -----------------------------------------------------
        // DIRECCIÓN
        // -----------------------------------------------------

        Direccion direccion =
                new Direccion(
                        calle.trim(),
                        numero.trim(),
                        ciudad.trim(),
                        codigoPostal.trim()
                );


        // -----------------------------------------------------
        // CLIENTE MODIFICADO
        // -----------------------------------------------------

        Cliente cliente =
                new Cliente(
                        clienteId,
                        razonSocial.trim(),
                        cuit.trim(),
                        email.trim(),
                        telefono.trim(),
                        direccion,
                        pais.trim(),
                        estado.trim()
                );


        // -----------------------------------------------------
        // MODIFICAR
        // -----------------------------------------------------

        clienteDAO.modificar(
                cliente
        );
    }


    // =========================================================
    // ELIMINAR CLIENTE
    // =========================================================


public void eliminarCliente(String clienteId) {

    validarObjectId(clienteId, "El ID del cliente no es válido.");

    Document cliente = clienteDAO.buscarPorId(clienteId);

    if (cliente == null) {
        throw new IllegalArgumentException("El cliente no existe.");
    }

    FacturaMongoDAO facturaDAO = new FacturaMongoDAO();
    List<Document> facturas = facturaDAO.buscarPorCliente(clienteId);

    // No eliminar si tiene facturas sin pagar.
    for (Document factura : facturas) {
        if (!"PAGADA".equalsIgnoreCase(factura.getString("estado"))) {
            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque tiene facturas sin pagar."
            );
        }
    }

    // No eliminar si tiene envíos activos.
    ControllerNeo4j neo4j = new ControllerNeo4j();

    for (Map<String, Object> envio : neo4j.listarEnvios()) {

        if (!clienteId.equals(envio.get("clienteId"))) {
            continue;
        }

        String estado = (String) envio.get("estado");

        if (estado == null
                || (!"ENTREGADO".equalsIgnoreCase(estado)
                && !"FINALIZADO".equalsIgnoreCase(estado)
                && !"CANCELADO".equalsIgnoreCase(estado))) {

            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque tiene envíos sin finalizar."
            );
        }
    }

    CuentaCorrienteMongoDAO cuentaDAO = new CuentaCorrienteMongoDAO();
    Document cuenta = cuentaDAO.buscarPorCliente(clienteId);

    // Evitar eliminar dinero disponible.
    if (cuenta != null) {
        BigDecimal saldo = cuenta.get(
                "saldo", org.bson.types.Decimal128.class
        ).bigDecimalValue();

        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque su cuenta corriente "
                    + "tiene un saldo distinto de cero."
            );
        }
    }

    UsuarioMongoDAO usuarioDAO = new UsuarioMongoDAO();
    ConversacionMongoDAO conversacionDAO = new ConversacionMongoDAO();
    MensajeMongoDAO mensajeDAO = new MensajeMongoDAO();
    PagoMongoDAO pagoDAO = new PagoMongoDAO();

    MovimientoCuentaCorrienteMongoDAO movimientoDAO =
            new MovimientoCuentaCorrienteMongoDAO();

    CierreMensualCuentaCorrienteMongoDAO cierreDAO =
            new CierreMensualCuentaCorrienteMongoDAO();

    CuentaCorrienteService cuentaService = new CuentaCorrienteService();

    try (ClientSession session =
                 MongoSingleton.getInstance().startSession()) {

        session.startTransaction();

        try {

            // Eliminar las comunicaciones de los usuarios del cliente.
            for (Document usuario : usuarioDAO.listarTodos()) {

                ObjectId asociado = usuario.getObjectId("cliente_id");

                if (asociado == null
                        || !asociado.toHexString().equals(clienteId)) {
                    continue;
                }

                String usuarioId = usuario.getObjectId("_id").toHexString();

                for (Document conversacion :
                        conversacionDAO.buscarPorUsuario(usuarioId)) {

                    String conversacionId =
                            conversacion.getObjectId("_id").toHexString();

                    String tipo = conversacion.getString("tipo");

                    if ("PRIVADA".equalsIgnoreCase(tipo)) {

                        mensajeDAO.eliminarPorConversacion(
                                session, conversacionId
                        );

                        conversacionDAO.eliminar(
                                session, conversacionId
                        );

                    } else if ("GRUPAL".equalsIgnoreCase(tipo)) {

                        List<String> participantes =
                                conversacion.getList(
                                        "participantes_ids", String.class
                                );

                        if (participantes != null
                                && participantes.size() <= 1) {

                            mensajeDAO.eliminarPorConversacion(
                                    session, conversacionId
                            );

                            conversacionDAO.eliminar(
                                    session, conversacionId
                            );

                        } else {

                            conversacionDAO.quitarParticipante(
                                    session, conversacionId, usuarioId
                            );
                        }
                    }
                }

                mensajeDAO.eliminarPorRemitente(session, usuarioId);
            }

            usuarioDAO.eliminarPorCliente(session, clienteId);

            // Eliminar pagos antes que las facturas.
            for (Document factura : facturas) {
                pagoDAO.eliminarPorFactura(
                        session,
                        factura.getObjectId("_id").toHexString()
                );
            }

            facturaDAO.eliminarPorCliente(session, clienteId);

            // Eliminar todos los registros de cuenta corriente.
            if (cuenta != null) {

                String cuentaId =
                        cuenta.getObjectId("_id").toHexString();

                movimientoDAO.eliminarPorCuenta(session, cuentaId);
                cierreDAO.eliminarPorCuenta(session, cuentaId);
                cuentaService.eliminarAperturas(session, cuentaId);
                cuentaDAO.eliminar(session, cuentaId);
            }

            // Eliminar el cliente al final.
            clienteDAO.eliminar(session, clienteId);

            session.commitTransaction();

            AppLog.info(
                    ClienteService.class,
                    "Cliente y datos asociados eliminados."
            );

        } catch (RuntimeException e) {

            if (session.hasActiveTransaction()) {
                session.abortTransaction();
            }

            throw e;
        }
    }
}



    // =========================================================
    // LISTAR CLIENTES
    // =========================================================

    public List<Document> listarClientes() {

        return TablaConsola.mostrar("ClienteService.listarClientes", clienteDAO.listarTodos());
    }


    // =========================================================
    // BUSCAR CLIENTE POR ID
    // =========================================================

    public Document buscarPorId(
            String clienteId
    ) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );


        Document cliente =
                clienteDAO.buscarPorId(
                        clienteId
                );


        if (cliente == null) {

            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }


        return TablaConsola.mostrar("ClienteService.buscarPorId", cliente);
    }


    // =========================================================
    // BUSCAR CLIENTE POR CUIT
    // =========================================================

    public Document buscarPorCuit(
            String cuit
    ) {

        if (cuit == null
                || cuit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El CUIT es obligatorio."
            );
        }


        return TablaConsola.mostrar("ClienteService.buscarPorCuit", clienteDAO.buscarPorCuit(
                cuit.trim()
        ));
    }


    // =========================================================
    // VALIDAR DATOS
    // =========================================================

    private void validarDatos(
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

        if (razonSocial == null
                || razonSocial.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La razón social es obligatoria."
            );
        }


        if (cuit == null
                || cuit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El CUIT es obligatorio."
            );
        }


        if (email == null
                || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El email es obligatorio."
            );
        }


        if (telefono == null
                || telefono.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El teléfono es obligatorio."
            );
        }


        if (calle == null
                || calle.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La calle es obligatoria."
            );
        }


        if (numero == null
                || numero.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El número es obligatorio."
            );
        }


        if (ciudad == null
                || ciudad.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La ciudad es obligatoria."
            );
        }


        if (codigoPostal == null
                || codigoPostal.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El código postal es obligatorio."
            );
        }


        if (pais == null
                || pais.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El país es obligatorio."
            );
        }
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}
