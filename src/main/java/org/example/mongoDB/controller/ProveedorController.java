
package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.ProveedorService;

import java.util.List;

public class ProveedorController {

    private final ProveedorService proveedorService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ProveedorController() {
        this.proveedorService = new ProveedorService();
    }

    // =========================
    // CREAR PROVEEDOR
    // =========================

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

        proveedorService.crearProveedor(
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
    }

    // =========================
    // MODIFICAR PROVEEDOR
    // =========================

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

        proveedorService.modificarProveedor(
                proveedorId,
                razonSocial,
                cuit,
                email,
                telefono,
                calle,
                numero,
                ciudad,
                codigoPostal,
                pais,
                estado
        );
    }

    // =========================
    // ELIMINAR PROVEEDOR
    // =========================

    public void eliminarProveedor(String proveedorId) {
        proveedorService.eliminarProveedor(proveedorId);
    }

    // =========================
    // LISTAR PROVEEDORES
    // =========================

    public List<Document> listarProveedores() {
        return proveedorService.listarProveedores();
    }

    // =========================
    // BUSCAR PROVEEDOR POR ID
    // =========================

    public Document buscarPorId(String proveedorId) {
        return proveedorService.buscarPorId(proveedorId);
    }

    // =========================
    // BUSCAR PROVEEDOR POR CUIT
    // =========================

    public Document buscarPorCuit(String cuit) {
        return proveedorService.buscarPorCuit(cuit);
    }
}
