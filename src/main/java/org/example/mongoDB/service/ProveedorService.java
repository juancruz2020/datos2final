
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import com.mongodb.client.ClientSession;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.ProveedorMongoDAO;
import org.example.mongoDB.model.Proveedor;
import org.example.mongoDB.model.Direccion;

import java.util.List;

public class ProveedorService {

    private final ProveedorMongoDAO proveedorDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ProveedorService() {

        this.proveedorDAO =
                new ProveedorMongoDAO();
    }


    // =========================================================
    // CREAR PROVEEDOR
    // =========================================================

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
                proveedorDAO.buscarPorCuit(
                        cuit.trim()
                );

        if (existente != null) {

            throw new IllegalArgumentException(
                    "Ya existe un proveedor con ese CUIT."
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
        // PROVEEDOR
        // -----------------------------------------------------

        Proveedor proveedor =
                new Proveedor(
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
        // CREAR PROVEEDOR Y CUENTA CORRIENTE
        // EN LA MISMA TRANSACCIÓN
        // -----------------------------------------------------

        CuentaCorrienteService cuentaService =
                new CuentaCorrienteService();

        try (ClientSession session = MongoSingleton
                .getInstance()
                .startSession()) {

            session.startTransaction();

            try {

                // Guardar proveedor.
                proveedorDAO.agregar(session, proveedor);

                // Crear cuenta corriente con saldo inicial $0.
                cuentaService.crearCuentaProveedor(
                        session,
                        proveedor.getId()
                );

                // Confirmar ambas operaciones.
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


    // =========================================================
    // MODIFICAR PROVEEDOR
    // =========================================================

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

        // -----------------------------------------------------
        // VALIDAR ID
        // -----------------------------------------------------

        validarObjectId(
                proveedorId,
                "El ID del proveedor no es válido."
        );

        Document proveedorActual =
                proveedorDAO.buscarPorId(
                        proveedorId
                );

        if (proveedorActual == null) {

            throw new IllegalArgumentException(
                    "El proveedor no existe."
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

        if (estado == null || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        if (!"ACTIVO".equalsIgnoreCase(estado.trim())
                && !"INACTIVO".equalsIgnoreCase(estado.trim())) {

            throw new IllegalArgumentException(
                    "El estado debe ser ACTIVO o INACTIVO."
            );
        }

        // -----------------------------------------------------
        // VALIDAR CUIT DUPLICADO
        // -----------------------------------------------------

        Document proveedorConCuit =
                proveedorDAO.buscarPorCuit(
                        cuit.trim()
                );

        if (proveedorConCuit != null) {

            ObjectId idExistente =
                    proveedorConCuit.getObjectId(
                            "_id"
                    );

            if (idExistente != null
                    && !idExistente.toHexString()
                    .equals(proveedorId)) {

                throw new IllegalArgumentException(
                        "Ya existe otro proveedor con ese CUIT."
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
        // PROVEEDOR MODIFICADO
        // -----------------------------------------------------

        Proveedor proveedor =
                new Proveedor(
                        proveedorId,
                        razonSocial.trim(),
                        cuit.trim(),
                        email.trim(),
                        telefono.trim(),
                        direccion,
                        pais.trim(),
                        estado.trim().toUpperCase()
                );

        // -----------------------------------------------------
        // MODIFICAR
        // -----------------------------------------------------

        proveedorDAO.modificar(proveedor);

        AppLog.info(
                ProveedorService.class,
                "Proveedor modificado correctamente."
        );
    }


    // =========================================================
    // ELIMINAR PROVEEDOR
    // =========================================================

    public void eliminarProveedor(String proveedorId) {

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

        // Todavía no se habilita la eliminación definitiva.
        // Primero debemos integrar las cuentas corrientes
        // y los usuarios asociados al proveedor para evitar
        // dejar registros relacionados sin su proveedor.

        throw new IllegalStateException(
                "La eliminación de proveedores estará disponible "
                        + "cuando se integren sus usuarios y cuentas corrientes."
        );
    }


    // =========================================================
    // LISTAR PROVEEDORES
    // =========================================================

    public List<Document> listarProveedores() {

        return TablaConsola.mostrar(
                "ProveedorService.listarProveedores",
                proveedorDAO.listarTodos()
        );
    }


    // =========================================================
    // BUSCAR PROVEEDOR POR ID
    // =========================================================

    public Document buscarPorId(String proveedorId) {

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

        return TablaConsola.mostrar(
                "ProveedorService.buscarPorId",
                proveedor
        );
    }


    // =========================================================
    // BUSCAR PROVEEDOR POR CUIT
    // =========================================================

    public Document buscarPorCuit(String cuit) {

        if (cuit == null || cuit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El CUIT es obligatorio."
            );
        }

        return TablaConsola.mostrar(
                "ProveedorService.buscarPorCuit",
                proveedorDAO.buscarPorCuit(cuit.trim())
        );
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
