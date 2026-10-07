package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.VehiculoMongoDAO;
import org.example.mongoDB.model.Vehiculo;

import java.util.List;

public class VehiculoService {

    private final VehiculoMongoDAO vehiculoDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VehiculoService() {
        this.vehiculoDAO = new VehiculoMongoDAO();
    }


    // =========================================================
    // CREAR VEHICULO
    // =========================================================

    public void crearVehiculo(
            String tipo,
            String identificacion,
            String empresaOperadora
    ) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException(
                    "La identificación es obligatoria."
            );
        }

        if (empresaOperadora == null || empresaOperadora.isBlank()) {
            throw new IllegalArgumentException(
                    "La empresa operadora es obligatoria."
            );
        }


        // -----------------------------------------------------
        // EVITAR IDENTIFICACIONES REPETIDAS
        // -----------------------------------------------------

        Document existente =
                vehiculoDAO.buscarPorIdentificacion(
                        identificacion.trim()
                );

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Ya existe un vehículo con esa identificación."
            );
        }


        // -----------------------------------------------------
        // CREAR VEHICULO
        // -----------------------------------------------------

        Vehiculo vehiculo =
                new Vehiculo(
                        null,
                        tipo.trim(),
                        identificacion.trim(),
                        empresaOperadora.trim(),
                        "ACTIVO"
                );

        vehiculoDAO.agregar(vehiculo);
    }


    // =========================================================
    // MODIFICAR VEHICULO
    // =========================================================

    public void modificarVehiculo(
            String id,
            String tipo,
            String identificacion,
            String empresaOperadora,
            String estado
    ) {

        validarObjectId(id);

        if (!vehiculoDAO.existePorId(id)) {
            throw new IllegalArgumentException(
                    "El vehículo no existe."
            );
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException(
                    "La identificación es obligatoria."
            );
        }

        if (empresaOperadora == null || empresaOperadora.isBlank()) {
            throw new IllegalArgumentException(
                    "La empresa operadora es obligatoria."
            );
        }

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        // -----------------------------------------------------
        // EVITAR IDENTIFICACION DUPLICADA
        // -----------------------------------------------------

        Document existente =
                vehiculoDAO.buscarPorIdentificacion(
                        identificacion.trim()
                );

        if (existente != null) {

            ObjectId idExistente =
                    existente.getObjectId("_id");

            if (idExistente != null
                    && !idExistente
                    .toHexString()
                    .equals(id)) {

                throw new IllegalArgumentException(
                        "Ya existe otro vehículo con esa identificación."
                );
            }
        }


        // -----------------------------------------------------
        // MODIFICAR VEHICULO
        // -----------------------------------------------------

        Vehiculo vehiculo =
                new Vehiculo(
                        id,
                        tipo.trim(),
                        identificacion.trim(),
                        empresaOperadora.trim(),
                        estado.trim()
                );

        vehiculoDAO.modificar(vehiculo);
    }


    // =========================================================
    // ELIMINAR VEHICULO
    // =========================================================

    public void eliminarVehiculo(String id) {

        validarObjectId(id);

        if (!vehiculoDAO.existePorId(id)) {
            throw new IllegalArgumentException(
                    "El vehículo no existe."
            );
        }

        vehiculoDAO.eliminar(id);
    }


    // =========================================================
    // LISTAR VEHICULOS
    // =========================================================

    public List<Document> listarVehiculos() {
        return vehiculoDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(String id) {

        validarObjectId(id);

        Document vehiculo =
                vehiculoDAO.buscarPorId(id);

        if (vehiculo == null) {
            throw new IllegalArgumentException(
                    "El vehículo no existe."
            );
        }

        return vehiculo;
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(String id) {

        validarObjectId(id);

        return vehiculoDAO.existePorId(id);
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {
        return vehiculoDAO.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR IDENTIFICACION
    // =========================================================

    public Document buscarPorIdentificacion(
            String identificacion
    ) {

        if (identificacion == null
                || identificacion.isBlank()) {

            throw new IllegalArgumentException(
                    "La identificación es obligatoria."
            );
        }

        return vehiculoDAO.buscarPorIdentificacion(
                identificacion.trim()
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        return vehiculoDAO.buscarPorEstado(
                estado.trim()
        );
    }


    // =========================================================
    // BUSCAR POR TIPO
    // =========================================================

    public List<Document> buscarPorTipo(
            String tipo
    ) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        return vehiculoDAO.buscarPorTipo(
                tipo.trim()
        );
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(String id) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    "El ID del vehículo no es válido."
            );
        }
    }
}