
package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ContenedorMongoDAO;
import org.example.logging.AppLog;
import org.example.mongoDB.model.Contenedor;

import java.util.List;

public class ContenedorService {

    private final ContenedorMongoDAO contenedorDAO;

    public ContenedorService() {
        this.contenedorDAO = new ContenedorMongoDAO();
    }

    // =========================================================
    // CREAR CONTENEDOR
    // =========================================================

    public void crearContenedor(
            String codigoInternacional,
            String tipo,
            double capacidad
    ) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (capacidad <= 0 || !Double.isFinite(capacidad)) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un número mayor a 0."
            );
        }

        // El código se genera automáticamente en el DAO.
        Contenedor contenedor = new Contenedor(
                null,
                null,
                tipo.trim(),
                capacidad,
                "DISPONIBLE"
        );

        contenedorDAO.agregar(contenedor);
        AppLog.info(ContenedorService.class, "Contenedor creado.");
    }

    // =========================================================
    // MODIFICAR CONTENEDOR
    // =========================================================

    public void modificarContenedor(
            String id,
            String codigoInternacional,
            String tipo,
            double capacidad,
            String estado
    ) {

        validarObjectId(id);

        Document actual = contenedorDAO.buscarPorId(id);

        if (actual == null) {
            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (capacidad <= 0 || !Double.isFinite(capacidad)) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un número mayor a 0."
            );
        }

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        // Recuperamos el código original de MongoDB.
        // No permitimos modificarlo desde el formulario.
        String codigoOriginal = actual.getString(
                "codigo_internacional"
        );

        Contenedor contenedor = new Contenedor(
                id,
                codigoOriginal,
                tipo.trim(),
                capacidad,
                estado.trim()
        );

        contenedorDAO.modificar(contenedor);
    }

    // =========================================================
    // ELIMINAR CONTENEDOR
    // =========================================================

    public void eliminarContenedor(String id) {

        validarObjectId(id);

        if (!contenedorDAO.existePorId(id)) {
            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }

        contenedorDAO.eliminar(id);
    }

    // =========================================================
    // LISTAR CONTENEDORES
    // =========================================================

    public List<Document> listarContenedores() {
        return contenedorDAO.listarTodos();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(String id) {

        validarObjectId(id);

        Document contenedor = contenedorDAO.buscarPorId(id);

        if (contenedor == null) {
            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }

        return contenedor;
    }

    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(String id) {

        validarObjectId(id);

        return contenedorDAO.existePorId(id);
    }

    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {
        return contenedorDAO.obtenerTodosLosIds();
    }

    // =========================================================
    // BUSCAR POR CÓDIGO
    // =========================================================

    public Document buscarPorCodigo(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código internacional es obligatorio."
            );
        }

        return contenedorDAO.buscarPorCodigo(codigo.trim());
    }

    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        return contenedorDAO.buscarPorEstado(estado.trim());
    }

    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(String id) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    "El ID del contenedor no es válido."
            );
        }
    }
}
