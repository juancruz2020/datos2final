
package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ClienteMongoDAO;
import org.example.mongoDB.dao.FacturaMongoDAO;
import org.example.mongoDB.model.Factura;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FacturaService {

    private final FacturaMongoDAO facturaDAO;
    private final ClienteMongoDAO clienteDAO;

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Argentina/Buenos_Aires");

    private static final Set<String> ESTADOS_MANUALES =
            Set.of("PENDIENTE", "PAGADA");

    public FacturaService() {
        this.facturaDAO = new FacturaMongoDAO();
        this.clienteDAO = new ClienteMongoDAO();
    }

    // ==========================================
    // CREAR FACTURA
    // ==========================================

    public void crearFactura(
            String clienteId,
            Date fechaEmision,
            Date fechaVencimiento,
            BigDecimal importeTotal,
            String estado
    ) {
        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        if (!clienteDAO.existePorId(clienteId)) {
            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe."
            );
        }

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

        validarVencimiento(fechaEmision, fechaVencimiento);
        validarImporte(importeTotal);

        String estadoValidado = validarEstadoManual(estado);

        String estadoFinal = calcularEstado(
                estadoValidado,
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
    }

    // ==========================================
    // COMPATIBILIDAD CON CÓDIGO ANTERIOR
    // ==========================================

    @Deprecated
    public void crearFactura(
            String clienteId,
            Date fechaEmision,
            BigDecimal importeTotal,
            String estado
    ) {
        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        if (!clienteDAO.existePorId(clienteId)) {
            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe."
            );
        }

        if (fechaEmision == null) {
            throw new IllegalArgumentException(
                    "La fecha de emisión es obligatoria."
            );
        }

        validarImporte(importeTotal);

        String estadoFinal = calcularEstado(
                validarEstadoManual(estado),
                null
        );

        Factura factura = new Factura(
                null,
                clienteId,
                fechaEmision,
                null,
                importeTotal,
                estadoFinal
        );

        facturaDAO.agregar(factura);
    }

    // ==========================================
    // MODIFICAR VENCIMIENTO Y ESTADO
    // ==========================================

    public void modificarVencimientoYEstado(
            String facturaId,
            Date fechaVencimiento,
            String estado
    ) {
        Document factura = buscarPorId(facturaId);

        Date fechaEmision = factura.getDate("fecha_emision");

        validarVencimiento(fechaEmision, fechaVencimiento);

        String estadoValidado = validarEstadoManual(estado);

        String estadoFinal = calcularEstado(
                estadoValidado,
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
        List<Document> facturas = facturaDAO.listarTodos();

        actualizarEstados(facturas);

        return facturas;
    }

    // ==========================================
    // BUSCAR POR ID
    // ==========================================

    public Document buscarPorId(String id) {
        validarObjectId(
                id,
                "El ID de la factura no es válido."
        );

        Document factura = facturaDAO.buscarPorId(id);

        if (factura == null) {
            throw new IllegalArgumentException(
                    "La factura no existe."
            );
        }

        actualizarEstado(factura);

        return factura;
    }

    // ==========================================
    // VERIFICAR EXISTENCIA
    // ==========================================

    public boolean existePorId(String id) {
        validarObjectId(
                id,
                "El ID de la factura no es válido."
        );

        return facturaDAO.existePorId(id);
    }

    // ==========================================
    // OBTENER TODOS LOS IDS
    // ==========================================

    public List<String> obtenerTodosLosIds() {
        return facturaDAO.obtenerTodosLosIds();
    }

    // ==========================================
    // BUSCAR POR CLIENTE
    // ==========================================

    public List<Document> buscarPorCliente(String clienteId) {
        validarObjectId(
                clienteId,
                "El ID del cliente no es válido."
        );

        List<Document> facturas =
                facturaDAO.buscarPorCliente(clienteId);

        actualizarEstados(facturas);

        return facturas;
    }

    // ==========================================
    // BUSCAR POR ESTADO
    // ==========================================

    public List<Document> buscarPorEstado(String estado) {
        String estadoBuscado = validarEstadoConsulta(estado);

        // Actualizar antes de filtrar para incluir las
        // facturas que vencieron desde la última consulta.
        List<Document> facturas = listarFacturas();

        return facturas.stream()
                .filter(factura ->
                        estadoBuscado.equals(
                                factura.getString("estado")
                        )
                )
                .toList();
    }

    // ==========================================
    // ACTUALIZAR ESTADOS AUTOMÁTICAMENTE
    // ==========================================

    private void actualizarEstados(List<Document> facturas) {
        for (Document factura : facturas) {
            actualizarEstado(factura);
        }
    }

    private void actualizarEstado(Document factura) {
        String estadoActual = factura.getString("estado");

        Date fechaVencimiento =
                factura.getDate("fecha_vencimiento");

        String estadoNuevo = calcularEstado(
                estadoActual,
                fechaVencimiento
        );

        if (!estadoNuevo.equals(estadoActual)) {

            ObjectId id = factura.getObjectId("_id");

            if (id != null) {
                facturaDAO.actualizarVencimientoYEstado(
                        id.toHexString(),
                        fechaVencimiento,
                        estadoNuevo
                );

                factura.put("estado", estadoNuevo);
            }
        }
    }

    // ==========================================
    // CALCULAR ESTADO DE LA FACTURA
    // ==========================================

    private String calcularEstado(
            String estadoActual,
            Date fechaVencimiento
    ) {
        // Una factura pagada nunca vuelve a pendiente
        // o vencida por el paso del tiempo.
        if ("PAGADA".equalsIgnoreCase(estadoActual)) {
            return "PAGADA";
        }

        // Compatibilidad con facturas antiguas
        // que todavía no tienen vencimiento.
        if (fechaVencimiento == null) {
            return "PENDIENTE";
        }

        LocalDate vencimiento = convertirFecha(
                fechaVencimiento
        );

        LocalDate hoy = LocalDate.now(ZONA_HORARIA);

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
        if (fechaVencimiento == null) {
            // Permitido al editar facturas antiguas.
            return;
        }

        if (fechaEmision == null) {
            throw new IllegalArgumentException(
                    "La factura no tiene fecha de emisión."
            );
        }

        LocalDate emision = convertirFecha(fechaEmision);

        LocalDate vencimiento =
                convertirFecha(fechaVencimiento);

        if (vencimiento.isBefore(emision)) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento no puede ser " +
                    "anterior a la fecha de emisión."
            );
        }
    }

    private LocalDate convertirFecha(Date fecha) {
        return Instant.ofEpochMilli(fecha.getTime())
                .atZone(ZONA_HORARIA)
                .toLocalDate();
    }

    // ==========================================
    // VALIDAR IMPORTE
    // ==========================================

    private void validarImporte(BigDecimal importeTotal) {
        if (importeTotal == null
                || importeTotal.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe total debe ser mayor a 0."
            );
        }
    }

    // ==========================================
    // VALIDAR ESTADO MANUAL
    // ==========================================

    private String validarEstadoManual(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        String valor = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!ESTADOS_MANUALES.contains(valor)) {
            throw new IllegalArgumentException(
                    "Solo podés seleccionar PENDIENTE o PAGADA. " +
                    "El estado VENCIDA se calcula automáticamente."
            );
        }

        return valor;
    }

    // ==========================================
    // VALIDAR ESTADO PARA CONSULTAS
    // ==========================================

    private String validarEstadoConsulta(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }

        String valor = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!Set.of("PENDIENTE", "PAGADA", "VENCIDA")
                .contains(valor)) {
            throw new IllegalArgumentException(
                    "El estado no es válido."
            );
        }

        return valor;
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
