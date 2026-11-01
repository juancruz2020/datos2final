
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import com.mongodb.client.ClientSession;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;

import org.example.mongoDB.dao.ProveedorMongoDAO;
import org.example.mongoDB.dao.FacturaProveedorMongoDAO;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;

import org.example.mongoDB.model.Proveedor;
import org.example.mongoDB.model.Direccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class ProveedorService {

    private final ProveedorMongoDAO proveedorDAO;
    private final FacturaProveedorMongoDAO facturaDAO;
    private final CuentaCorrienteMongoDAO cuentaDAO;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ProveedorService() {
        this.proveedorDAO = new ProveedorMongoDAO();
        this.facturaDAO = new FacturaProveedorMongoDAO();
        this.cuentaDAO = new CuentaCorrienteMongoDAO();
    }

    // ==========================================
    // CREAR PROVEEDOR
    // ==========================================

    public void crearProveedor(
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

        Proveedor proveedor = new Proveedor(
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

                proveedorDAO.agregar(session, proveedor);

                cuentaService.crearCuentaProveedor(
                        session,
                        proveedor.getId()
                );

                session.commitTransaction();

                AppLog.info(
                        ProveedorService.class,
                        "Proveedor y cuenta corriente creados correctamente."
                );

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();

                    AppLog.warning(
                            ProveedorService.class,
                            "Alta de proveedor revertida por un error."
                    );
                }

                throw e;
            }
        }
    }

    // ==========================================
    // MODIFICAR PROVEEDOR
    // ==========================================

    public void modificarProveedor(
            String proveedorId,
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

        Document actual = obtenerProveedor(proveedorId);

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

        // Si se intenta desactivar un proveedor activo,
        // comprobar primero sus obligaciones pendientes.

        if ("INACTIVO".equalsIgnoreCase(estado.trim())
                && !"INACTIVO".equalsIgnoreCase(
                        actual.getString("estado"))) {

            validarBajaProveedor(proveedorId);
        }

        String cuitNormalizado =
                ValidacionesRegistro.normalizarCuit(cuit);

        verificarCuitDuplicado(
                cuitNormalizado,
                proveedorId
        );

        Direccion direccion = new Direccion(
                calle.trim(),
                numero.trim(),
                ciudad.trim(),
                codigoPostal.trim().toUpperCase(Locale.ROOT)
        );

        Proveedor proveedor = new Proveedor(
                proveedorId,
                razonSocial.trim(),
                cuitNormalizado,
                email.trim(),
                telefono.trim(),
                direccion,
                pais.trim(),
                estado.trim().toUpperCase(Locale.ROOT)
        );

        proveedorDAO.modificar(proveedor);

        AppLog.info(
                ProveedorService.class,
                "Proveedor modificado correctamente."
        );
    }

    // ==========================================
    // ELIMINAR PROVEEDOR (BAJA LÓGICA)
    // ==========================================

    public void eliminarProveedor(String proveedorId) {

        cambiarEstadoProveedor(
                proveedorId,
                "INACTIVO"
        );

        AppLog.info(
                ProveedorService.class,
                "Proveedor desactivado correctamente."
        );
    }

    // ==========================================
    // REACTIVAR PROVEEDOR
    // ==========================================

    public void reactivarProveedor(String proveedorId) {

        cambiarEstadoProveedor(
                proveedorId,
                "ACTIVO"
        );

        AppLog.info(
                ProveedorService.class,
                "Proveedor reactivado correctamente."
        );
    }

    // ==========================================
    // CAMBIAR ESTADO DEL PROVEEDOR
    // ==========================================

    public void cambiarEstadoProveedor(
            String proveedorId,
            String estado
    ) {

        validarEstado(estado);

        Document actual = obtenerProveedor(proveedorId);

        // Solo validar las deudas cuando se
        // pasa de ACTIVO a INACTIVO.

        if ("INACTIVO".equalsIgnoreCase(estado.trim())
                && !"INACTIVO".equalsIgnoreCase(
                        actual.getString("estado"))) {

            validarBajaProveedor(proveedorId);
        }

        Document direccion =
                actual.get("direccion", Document.class);

        if (direccion == null) {

            throw new IllegalStateException(
                    "El proveedor no tiene una dirección válida."
            );
        }

        // Se conservan los datos históricos.
        // No volvemos a validar los CUIT anteriores.

        Proveedor proveedor = new Proveedor(
                proveedorId,
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

        proveedorDAO.modificar(proveedor);
    }

    // ==========================================
    // VALIDAR BAJA LÓGICA DEL PROVEEDOR
    // ==========================================

    private void validarBajaProveedor(String proveedorId) {

        // 1. Consultar las facturas del proveedor.

        List<Document> facturas =
                facturaDAO.buscarPorProveedor(proveedorId);

        if (facturas == null) {

            throw new IllegalStateException(
                    "No se pudieron consultar las facturas del proveedor."
            );
        }

        // Una factura solo se considera saldada
        // cuando su estado es PAGADA.

        for (Document factura : facturas) {

            String estadoFactura =
                    factura.getString("estado");

            if (estadoFactura == null
                    || !"PAGADA".equalsIgnoreCase(
                            estadoFactura.trim())) {

                throw new IllegalArgumentException(
                        "No se puede desactivar el proveedor "
                                + "porque tiene facturas pendientes de pago. "
                                + "Primero debe regularizar sus deudas."
                );
            }
        }

        // 2. Consultar la cuenta corriente.

        Document cuenta =
                cuentaDAO.buscarPorProveedor(proveedorId);

        if (cuenta == null) {

            throw new IllegalStateException(
                    "No se puede desactivar el proveedor "
                            + "porque no se encontró su cuenta corriente."
            );
        }

        // 3. Obtener el saldo actual.

        Decimal128 saldoDecimal =
                cuenta.get("saldo", Decimal128.class);

        if (saldoDecimal == null) {

            throw new IllegalStateException(
                    "No se puede desactivar el proveedor "
                            + "porque su cuenta corriente "
                            + "no tiene un saldo válido."
            );
        }

        BigDecimal saldo =
                saldoDecimal.bigDecimalValue();

        // Según FacturaProveedorService:
        // Las facturas restan dinero al saldo.
        // Por lo tanto, un saldo negativo indica deuda.

        if (saldo.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "No se puede desactivar el proveedor "
                            + "porque tiene un saldo pendiente "
                            + "en su cuenta corriente: $"
                            + saldo.abs().toPlainString()
                            + ". Primero debe regularizarlo."
            );
        }

        // Si llegó hasta acá, no se encontraron
        // facturas impagas ni deuda en cuenta corriente.
    }

    // ==========================================
    // LISTAR PROVEEDORES
    // ==========================================

    public List<Document> listarProveedores() {

        return TablaConsola.mostrar(
                "ProveedorService.listarProveedores",
                proveedorDAO.listarTodos()
        );
    }

    // ==========================================
    // BUSCAR PROVEEDOR POR ID
    // ==========================================

    public Document buscarPorId(String proveedorId) {

        Document proveedor = obtenerProveedor(proveedorId);

        return TablaConsola.mostrar(
                "ProveedorService.buscarPorId",
                proveedor
        );
    }

    // ==========================================
    // BUSCAR PROVEEDOR POR CUIT
    // ==========================================

    public Document buscarPorCuit(String cuit) {

        // Permitir buscar CUIT históricos ficticios.
        // No validar el dígito verificador en búsquedas.

        ValidacionesRegistro.obligatorio(cuit, "CUIT");

        String cuitNormalizado =
                limpiarGuionesCuit(cuit);

        Document encontrado = null;

        for (Document proveedor : proveedorDAO.listarTodos()) {

            String cuitGuardado =
                    proveedor.getString("cuit");

            if (cuitGuardado == null) {
                continue;
            }

            String cuitExistente =
                    limpiarGuionesCuit(cuitGuardado);

            if (cuitExistente.equals(cuitNormalizado)) {

                encontrado = proveedor;
                break;
            }
        }

        return TablaConsola.mostrar(
                "ProveedorService.buscarPorCuit",
                encontrado
        );
    }

    // ==========================================
    // OBTENER PROVEEDOR EXISTENTE
    // ==========================================

    private Document obtenerProveedor(String proveedorId) {

        validarObjectId(
                proveedorId,
                "El ID del proveedor no es válido."
        );

        Document proveedor =
                proveedorDAO.buscarPorId(proveedorId);

        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "El proveedor no existe."
            );
        }

        return proveedor;
    }

    // ==========================================
    // VERIFICAR CUIT DUPLICADO
    // ==========================================

    private void verificarCuitDuplicado(
            String cuitNormalizado,
            String proveedorId
    ) {

        for (Document proveedor : proveedorDAO.listarTodos()) {

            String cuitGuardado =
                    proveedor.getString("cuit");

            if (cuitGuardado == null) {
                continue;
            }

            String cuitExistente =
                    limpiarGuionesCuit(cuitGuardado);

            if (!cuitExistente.equals(cuitNormalizado)) {
                continue;
            }

            ObjectId id =
                    proveedor.getObjectId("_id");

            if (proveedorId != null
                    && id != null
                    && id.toHexString().equals(proveedorId)) {

                continue;
            }

            throw new IllegalArgumentException(
                    "Ya existe otro proveedor con ese CUIT."
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
