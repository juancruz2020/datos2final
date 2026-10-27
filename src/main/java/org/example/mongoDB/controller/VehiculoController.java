package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.VehiculoService;

import java.util.List;

public class VehiculoController {

    private final VehiculoService vehiculoService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VehiculoController() {
        this.vehiculoService = new VehiculoService();
    }


    // =========================================================
    // CREAR VEHICULO
    // =========================================================

    public void crearVehiculo(
            String tipo,
            String identificacion,
            String empresaOperadora
    ) {

        vehiculoService.crearVehiculo(
                tipo,
                identificacion,
                empresaOperadora
        );
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

        vehiculoService.modificarVehiculo(
                id,
                tipo,
                identificacion,
                empresaOperadora,
                estado
        );
    }


    // =========================================================
    // ELIMINAR VEHICULO
    // =========================================================

    public void eliminarVehiculo(String id) {

        vehiculoService.eliminarVehiculo(id);
    }


    // =========================================================
    // LISTAR VEHICULOS
    // =========================================================

    public List<Document> listarVehiculos() {

        return vehiculoService.listarVehiculos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(String id) {

        return vehiculoService.buscarPorId(id);
    }


}