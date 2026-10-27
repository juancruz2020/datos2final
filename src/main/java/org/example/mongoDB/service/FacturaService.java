
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.FacturaMongoDAO;
import org.example.mongoDB.model.Factura;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FacturaService {

    private final FacturaMongoDAO facturaDAO;
    private final ClienteMongoDAO clienteDAO;

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Argentina/Buenos_Aires");


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FacturaService() {
        this.facturaDAO = new FacturaMongoDAO();
        this.clienteDAO = new ClienteMongoDAO();
    }

    // ==========================================
    // CREAR FACTURA CON VENCIMIENTO
    // ==========================================

    public void crearFactura(
            String clienteId,
            Date fechaEmision,
            Date fechaVencimiento,
            BigDecimal importeTotal,
            String estado
    ) {

        validarCliente(clienteId);

        if (fechaEmision == null) {
            throw new IllegalArgumentException(
                    "La fecha de emisión es obligatoria."
            );
        }

        if (fechaVencimiento == null) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento es obligatoria."
            );
        }

        validarVencimiento(
                fechaEmision,
                fechaVencimiento
        );

        validarImporte(importeTotal);

        validarEstadoInicial(estado);

        String estadoFinal = calcularEstado(
                "PENDIENTE",
                fechaVencimiento
        );

        Factura factura = new Factura(
                null,
                clienteId,
                fechaEmision,
                fechaVencimiento,
                importeTotal,
                estadoFinal
        );

        facturaDAO.agregar(factura);
        AppLog.info(FacturaService.class, "Factura creada.");
    }


    // ==========================================
    // MODIFICAR FECHA DE VENCIMIENTO
    // ==========================================

    public void modificarVencimientoYEstado(
            String facturaId,
            Date fechaVencimiento,
            String estado
    ) {

        Document factura = buscarPorId(facturaId);

        Date fechaEmision =
                factura.getDate("fecha_emision");

        validarVencimiento(
                fechaEmision,
                fechaVencimiento
        );

        // El estado recibido NO puede marcar
        // una factura como pagada.
        //
        // Si ya estaba pagada, se conserva.
        // Si no, el estado se calcula por fecha.

        String estadoActual =
                factura.getString("estado");

        String estadoFinal = calcularEstado(
                estadoActual,
                fechaVencimiento
        );

        facturaDAO.actualizarVencimientoYEstado(
                facturaId,
                fechaVencimiento,
                estadoFinal
        );
    }

    // ==========================================
    // LISTAR FACTURAS
    // ==========================================

    public List<Document> listarFacturas() {

        List<Document> facturas =
                facturaDAO.listarTodos();

        actualizarEstados(facturas);

        return TablaConsola.mostrar("FacturaService.listarFacturas", facturas);
    }

    // ==========================================
    // BUSCAR FACTURA POR ID
    // ==========================================

    public Document buscarPorId(String id) {

        validarObjectId(
                id,
                "El ID de la factura no es válido."
        );

        Document factura =
                facturaDAO.buscarPorId(id);

        if (factura == null) {
            throw new IllegalArgumentException(
                    "La factura no existe."
            );
        }

        actualizarEstado(factura);

        return TablaConsola.mostrar("FacturaService.buscarPorId", factura);
    }


    // ==========================================
    // BUSCAR FACTURAS POR CLIENTE
    // ==========================================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        List<Document> facturas =
                facturaDAO.buscarPorCliente(clienteId);

        actualizarEstados(facturas);

        return TablaConsola.mostrar("FacturaService.buscarPorCliente", facturas);
    }


    // ==========================================
    // ACTUALIZAR ESTADOS AUTOMÁTICAMENTE
    // ==========================================

    private void actualizarEstados(
            List<Document> facturas
    ) {

        for (Document factura : facturas) {
            actualizarEstado(factura);
        }
    }

    private void actualizarEstado(Document factura) {

        String estadoActual =
                factura.getString("estado");

        Date fechaVencimiento =
                factura.getDate("fecha_vencimiento");

        String estadoNuevo = calcularEstado(
                estadoActual,
                fechaVencimiento
        );

        if (!estadoNuevo.equals(estadoActual)) {

            ObjectId id =
                    factura.getObjectId("_id");

            if (id != null) {

                facturaDAO.actualizarVencimientoYEstado(
                        id.toHexString(),
                        fechaVencimiento,
                        estadoNuevo
                );

                factura.put(
                        "estado",
                        estadoNuevo
                );
            }
        }
    }

    // ==========================================
    // CALCULAR ESTADO
    // ==========================================

    private String calcularEstado(
            String estadoActual,
            Date fechaVencimiento
    ) {

        // Las facturas pagadas siguen pagadas.
        if ("PAGADA".equalsIgnoreCase(
                estadoActual
        )) {
            return "PAGADA";
        }

        // Facturas antiguas sin vencimiento.
        if (fechaVencimiento == null) {
            return "PENDIENTE";
        }

        LocalDate vencimiento =
                convertirFecha(fechaVencimiento);

        LocalDate hoy =
                LocalDate.now(ZONA_HORARIA);

        if (vencimiento.isBefore(hoy)) {
            return "VENCIDA";
        }

        return "PENDIENTE";
    }

    // ==========================================
    // VALIDAR FECHA DE VENCIMIENTO
    // ==========================================

    private void validarVencimiento(
            Date fechaEmision,
            Date fechaVencimiento
    ) {

        // Compatibilidad con facturas anteriores.
        if (fechaVencimiento == null) {
            return;
        }

        if (fechaEmision == null) {
            throw new IllegalArgumentException(
                    "La factura no tiene fecha de emisión."
            );
        }

        LocalDate emision =
                convertirFecha(fechaEmision);

        LocalDate vencimiento =
                convertirFecha(fechaVencimiento);

        if (vencimiento.isBefore(emision)) {

            throw new IllegalArgumentException(
                    "La fecha de vencimiento no puede ser "
                            + "anterior a la fecha de emisión."
            );
        }
    }

    private LocalDate convertirFecha(Date fecha) {

        return Instant
                .ofEpochMilli(fecha.getTime())
                .atZone(ZONA_HORARIA)
                .toLocalDate();
    }

    // ==========================================
    // VALIDAR CLIENTE
    // ==========================================

    private void validarCliente(String clienteId) {

        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        if (!clienteDAO.existePorId(clienteId)) {

            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe."
            );
        }
    }

    // ==========================================
    // VALIDAR IMPORTE
    // ==========================================

    private void validarImporte(
            BigDecimal importeTotal
    ) {

        if (importeTotal == null
                || importeTotal.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "El importe total debe ser mayor a 0."
            );
        }
    }

    // ==========================================
    // VALIDAR ESTADO INICIAL
    // ==========================================

    private void validarEstadoInicial(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        String valor = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!"PENDIENTE".equals(valor)) {

            throw new IllegalArgumentException(
                    "Las facturas nuevas deben comenzar "
                            + "como PENDIENTE. "
                            + "El estado PAGADA se asigna "
                            + "cuando se completa el pago."
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
