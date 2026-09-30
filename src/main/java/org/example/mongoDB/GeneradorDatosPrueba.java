package org.example.mongoDB;

import org.example.mongoDB.dao.*;
import org.example.mongoDB.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GeneradorDatosPrueba {

    private final ClienteMongoDAO clienteDAO = new ClienteMongoDAO();
    private final RolMongoDAO rolDAO = new RolMongoDAO();
    private final UsuarioMongoDAO usuarioDAO = new UsuarioMongoDAO();
    private final ContenedorMongoDAO contenedorDAO = new ContenedorMongoDAO();
    private final SensorMongoDAO sensorDAO = new SensorMongoDAO();
    private final EnvioMongoDAO envioDAO = new EnvioMongoDAO();
    private final EventoLogisticoMongoDAO eventoDAO = new EventoLogisticoMongoDAO();
    private final IncidenteMongoDAO incidenteDAO = new IncidenteMongoDAO();
    private final AlertaMongoDAO alertaDAO = new AlertaMongoDAO();
    private final ReporteMongoDAO reporteDAO = new ReporteMongoDAO();
    private final FacturaMongoDAO facturaDAO = new FacturaMongoDAO();
    private final PagoMongoDAO pagoDAO = new PagoMongoDAO();
    private final CuentaCorrienteMongoDAO cuentaCorrienteDAO =
            new CuentaCorrienteMongoDAO();


    public void generarDatos() {

        System.out.println("Generando datos de prueba de MongoDB...");

        // =====================================================
        // ROLES
        // =====================================================

        Rol administrador = new Rol(
                null,
                "Administrador",
                3
        );

        Rol operador = new Rol(
                null,
                "Operador",
                2
        );

        Rol clienteRol = new Rol(
                null,
                "Cliente",
                1
        );

        rolDAO.agregar(administrador);
        rolDAO.agregar(operador);
        rolDAO.agregar(clienteRol);


        // =====================================================
        // CLIENTES
        // =====================================================

        Cliente cliente1 = new Cliente(
                null,
                "Logistica del Sur",
                "30-71111111-1",
                "contacto@logisticadelsur.com",
                "1140001001",
                new Direccion(
                        "Av. Corrientes",
                        "1200",
                        "Buenos Aires",
                        "1043"
                ),
                "Argentina",
                "ACTIVO"
        );

        Cliente cliente2 = new Cliente(
                null,
                "Transportes Andinos",
                "30-72222222-2",
                "info@transportesandinos.com",
                "1140001002",
                new Direccion(
                        "San Martin",
                        "850",
                        "Mendoza",
                        "5500"
                ),
                "Argentina",
                "ACTIVO"
        );

        Cliente cliente3 = new Cliente(
                null,
                "Comercio Internacional SA",
                "30-73333333-3",
                "contacto@comerciointernacional.com",
                "1140001003",
                new Direccion(
                        "Cordoba",
                        "450",
                        "Rosario",
                        "2000"
                ),
                "Argentina",
                "ACTIVO"
        );

        Cliente cliente4 = new Cliente(
                null,
                "Exportadora Pampeana",
                "30-74444444-4",
                "info@exportadorapampeana.com",
                "1140001004",
                new Direccion(
                        "Belgrano",
                        "950",
                        "Cordoba",
                        "5000"
                ),
                "Argentina",
                "ACTIVO"
        );

        Cliente cliente5 = new Cliente(
                null,
                "Patagonia Cargo",
                "30-75555555-5",
                "contacto@patagoniacargo.com",
                "1140001005",
                new Direccion(
                        "Roca",
                        "600",
                        "Neuquen",
                        "8300"
                ),
                "Argentina",
                "ACTIVO"
        );

        clienteDAO.agregar(cliente1);
        clienteDAO.agregar(cliente2);
        clienteDAO.agregar(cliente3);
        clienteDAO.agregar(cliente4);
        clienteDAO.agregar(cliente5);


        // =====================================================
        // USUARIOS
        // =====================================================

        Usuario usuario1 = new Usuario(
                null,
                cliente1.getId(),
                administrador.getId(),
                "Juan",
                "Perez",
                "juan@logisticadelsur.com",
                "clave_encriptada_1",
                "ACTIVO",
                new Date()
        );

        Usuario usuario2 = new Usuario(
                null,
                cliente2.getId(),
                operador.getId(),
                "Maria",
                "Gomez",
                "maria@transportesandinos.com",
                "clave_encriptada_2",
                "ACTIVO",
                new Date()
        );

        Usuario usuario3 = new Usuario(
                null,
                cliente3.getId(),
                clienteRol.getId(),
                "Lucas",
                "Fernandez",
                "lucas@comerciointernacional.com",
                "clave_encriptada_3",
                "ACTIVO",
                new Date()
        );

        Usuario usuario4 = new Usuario(
                null,
                cliente4.getId(),
                operador.getId(),
                "Sofia",
                "Martinez",
                "sofia@exportadorapampeana.com",
                "clave_encriptada_4",
                "ACTIVO",
                new Date()
        );

        Usuario usuario5 = new Usuario(
                null,
                cliente5.getId(),
                clienteRol.getId(),
                "Martin",
                "Lopez",
                "martin@patagoniacargo.com",
                "clave_encriptada_5",
                "ACTIVO",
                new Date()
        );

        usuarioDAO.agregar(usuario1);
        usuarioDAO.agregar(usuario2);
        usuarioDAO.agregar(usuario3);
        usuarioDAO.agregar(usuario4);
        usuarioDAO.agregar(usuario5);


        // =====================================================
        // CONTENEDORES
        // =====================================================

        Contenedor contenedor1 =
                new Contenedor(null, "CONT-001", "SECO", 30000, "DISPONIBLE");

        Contenedor contenedor2 =
                new Contenedor(null, "CONT-002", "REFRIGERADO", 25000, "EN_USO");

        Contenedor contenedor3 =
                new Contenedor(null, "CONT-003", "SECO", 32000, "DISPONIBLE");

        Contenedor contenedor4 =
                new Contenedor(null, "CONT-004", "TANQUE", 28000, "EN_USO");

        Contenedor contenedor5 =
                new Contenedor(null, "CONT-005", "REFRIGERADO", 26000, "DISPONIBLE");

        contenedorDAO.agregar(contenedor1);
        contenedorDAO.agregar(contenedor2);
        contenedorDAO.agregar(contenedor3);
        contenedorDAO.agregar(contenedor4);
        contenedorDAO.agregar(contenedor5);


        // =====================================================
        // SENSORES
        // =====================================================

        Sensor sensor1 = new Sensor(
                null,
                contenedor1.getId(),
                "TEMPERATURA",
                "Bosch",
                new Date(),
                "ACTIVO"
        );

        Sensor sensor2 = new Sensor(
                null,
                contenedor2.getId(),
                "HUMEDAD",
                "Siemens",
                new Date(),
                "ACTIVO"
        );

        Sensor sensor3 = new Sensor(
                null,
                contenedor3.getId(),
                "GPS",
                "Garmin",
                new Date(),
                "ACTIVO"
        );

        Sensor sensor4 = new Sensor(
                null,
                contenedor4.getId(),
                "TEMPERATURA",
                "Bosch",
                new Date(),
                "ACTIVO"
        );

        Sensor sensor5 = new Sensor(
                null,
                contenedor5.getId(),
                "APERTURA",
                "Honeywell",
                new Date(),
                "ACTIVO"
        );

        sensorDAO.agregar(sensor1);
        sensorDAO.agregar(sensor2);
        sensorDAO.agregar(sensor3);
        sensorDAO.agregar(sensor4);
        sensorDAO.agregar(sensor5);


        // =====================================================
        // TRAMOS
        // Se embeben dentro de los envios.
        // NO tienen DAO propio.
        // =====================================================

        Tramo tramo1 = new Tramo(
                "CAMION",
                "Buenos Aires",
                "Mendoza",
                new Date(),
                new Date()
        );

        Tramo tramo2 = new Tramo(
                "CAMION",
                "Mendoza",
                "Santiago de Chile",
                new Date(),
                new Date()
        );

        Tramo tramo3 = new Tramo(
                "BARCO",
                "Buenos Aires",
                "Montevideo",
                new Date(),
                new Date()
        );

        Tramo tramo4 = new Tramo(
                "CAMION",
                "Rosario",
                "Cordoba",
                new Date(),
                new Date()
        );

        Tramo tramo5 = new Tramo(
                "CAMION",
                "Neuquen",
                "Buenos Aires",
                new Date(),
                new Date()
        );


        // =====================================================
        // ENVIOS
        // =====================================================

        Envio envio1 = new Envio(
                null,
                cliente1.getId(),
                Arrays.asList(contenedor1.getId()),
                new Date(),
                new Ubicacion("Buenos Aires", "Argentina"),
                new Ubicacion("Mendoza", "Argentina"),
                "EN_TRANSITO",
                "ALTA",
                Arrays.asList(tramo1)
        );

        Envio envio2 = new Envio(
                null,
                cliente2.getId(),
                Arrays.asList(contenedor2.getId()),
                new Date(),
                new Ubicacion("Mendoza", "Argentina"),
                new Ubicacion("Santiago", "Chile"),
                "DEMORADO",
                "ALTA",
                Arrays.asList(tramo2)
        );

        Envio envio3 = new Envio(
                null,
                cliente3.getId(),
                Arrays.asList(contenedor3.getId()),
                new Date(),
                new Ubicacion("Buenos Aires", "Argentina"),
                new Ubicacion("Montevideo", "Uruguay"),
                "ENTREGADO",
                "MEDIA",
                Arrays.asList(tramo3)
        );

        Envio envio4 = new Envio(
                null,
                cliente4.getId(),
                Arrays.asList(contenedor4.getId()),
                new Date(),
                new Ubicacion("Rosario", "Argentina"),
                new Ubicacion("Cordoba", "Argentina"),
                "EN_TRANSITO",
                "MEDIA",
                Arrays.asList(tramo4)
        );

        Envio envio5 = new Envio(
                null,
                cliente5.getId(),
                Arrays.asList(contenedor5.getId()),
                new Date(),
                new Ubicacion("Neuquen", "Argentina"),
                new Ubicacion("Buenos Aires", "Argentina"),
                "PENDIENTE",
                "BAJA",
                Arrays.asList(tramo5)
        );

        envioDAO.agregar(envio1);
        envioDAO.agregar(envio2);
        envioDAO.agregar(envio3);
        envioDAO.agregar(envio4);
        envioDAO.agregar(envio5);


        // =====================================================
        // EVENTOS LOGISTICOS
        // =====================================================

        EventoLogistico evento1 = new EventoLogistico(
                null,
                envio1.getId(),
                new Date(),
                "SALIDA",
                "Buenos Aires",
                "El envio salio del centro logistico"
        );

        EventoLogistico evento2 = new EventoLogistico(
                null,
                envio2.getId(),
                new Date(),
                "DEMORA",
                "Mendoza",
                "Demora en el transporte"
        );

        EventoLogistico evento3 = new EventoLogistico(
                null,
                envio3.getId(),
                new Date(),
                "ENTREGA",
                "Montevideo",
                "Mercaderia entregada"
        );

        EventoLogistico evento4 = new EventoLogistico(
                null,
                envio4.getId(),
                new Date(),
                "CONTROL",
                "Rosario",
                "Control de carga realizado"
        );

        EventoLogistico evento5 = new EventoLogistico(
                null,
                envio5.getId(),
                new Date(),
                "PREPARACION",
                "Neuquen",
                "Envio preparado para despacho"
        );

        eventoDAO.agregar(evento1);
        eventoDAO.agregar(evento2);
        eventoDAO.agregar(evento3);
        eventoDAO.agregar(evento4);
        eventoDAO.agregar(evento5);


        // =====================================================
        // INCIDENTES
        // =====================================================

        Incidente incidente1 = new Incidente(
                null,
                envio1.getId(),
                "DEMORA",
                new Date(),
                "MEDIA",
                "ABIERTO",
                "Demora por condiciones climaticas"
        );

        Incidente incidente2 = new Incidente(
                null,
                envio2.getId(),
                "RUTA",
                new Date(),
                "ALTA",
                "ABIERTO",
                "Ruta temporalmente bloqueada"
        );

        Incidente incidente3 = new Incidente(
                null,
                envio3.getId(),
                "DOCUMENTACION",
                new Date(),
                "BAJA",
                "CERRADO",
                "Documentacion verificada"
        );

        Incidente incidente4 = new Incidente(
                null,
                envio4.getId(),
                "MECANICO",
                new Date(),
                "MEDIA",
                "ABIERTO",
                "Revision del vehiculo"
        );

        Incidente incidente5 = new Incidente(
                null,
                envio5.getId(),
                "OPERATIVO",
                new Date(),
                "BAJA",
                "CERRADO",
                "Incidente operativo resuelto"
        );

        incidenteDAO.agregar(incidente1);
        incidenteDAO.agregar(incidente2);
        incidenteDAO.agregar(incidente3);
        incidenteDAO.agregar(incidente4);
        incidenteDAO.agregar(incidente5);


        // =====================================================
        // ALERTAS
        // =====================================================

        Alerta alerta1 = new Alerta(
                null,
                sensor1.getId(),
                evento1.getId(),
                "TEMPERATURA",
                new Date(),
                "ACTIVA"
        );

        Alerta alerta2 = new Alerta(
                null,
                sensor2.getId(),
                evento2.getId(),
                "HUMEDAD",
                new Date(),
                "ACTIVA"
        );

        Alerta alerta3 = new Alerta(
                null,
                sensor3.getId(),
                evento3.getId(),
                "UBICACION",
                new Date(),
                "RESUELTA"
        );

        Alerta alerta4 = new Alerta(
                null,
                sensor4.getId(),
                evento4.getId(),
                "TEMPERATURA",
                new Date(),
                "ACTIVA"
        );

        Alerta alerta5 = new Alerta(
                null,
                sensor5.getId(),
                evento5.getId(),
                "APERTURA",
                new Date(),
                "RESUELTA"
        );

        alertaDAO.agregar(alerta1);
        alertaDAO.agregar(alerta2);
        alertaDAO.agregar(alerta3);
        alertaDAO.agregar(alerta4);
        alertaDAO.agregar(alerta5);


        // =====================================================
        // REPORTES
        // =====================================================

        Reporte reporte1 = new Reporte(
                null,
                usuario1.getId(),
                "ENVIOS",
                new Date(),
                "PDF",
                "GENERADO"
        );

        Reporte reporte2 = new Reporte(
                null,
                usuario2.getId(),
                "INCIDENTES",
                new Date(),
                "PDF",
                "GENERADO"
        );

        Reporte reporte3 = new Reporte(
                null,
                usuario3.getId(),
                "FACTURACION",
                new Date(),
                "EXCEL",
                "GENERADO"
        );

        Reporte reporte4 = new Reporte(
                null,
                usuario4.getId(),
                "ALERTAS",
                new Date(),
                "PDF",
                "GENERADO"
        );

        Reporte reporte5 = new Reporte(
                null,
                usuario5.getId(),
                "LOGISTICA",
                new Date(),
                "EXCEL",
                "GENERADO"
        );

        reporteDAO.agregar(reporte1);
        reporteDAO.agregar(reporte2);
        reporteDAO.agregar(reporte3);
        reporteDAO.agregar(reporte4);
        reporteDAO.agregar(reporte5);


        // =====================================================
        // FACTURAS
        // =====================================================

        Factura factura1 = new Factura(
                null,
                cliente1.getId(),
                new Date(),
                new BigDecimal("150000.00"),
                "PENDIENTE"
        );

        Factura factura2 = new Factura(
                null,
                cliente2.getId(),
                new Date(),
                new BigDecimal("225000.00"),
                "PAGADA"
        );

        Factura factura3 = new Factura(
                null,
                cliente3.getId(),
                new Date(),
                new BigDecimal("180000.00"),
                "PAGADA"
        );

        Factura factura4 = new Factura(
                null,
                cliente4.getId(),
                new Date(),
                new BigDecimal("310000.00"),
                "PENDIENTE"
        );

        Factura factura5 = new Factura(
                null,
                cliente5.getId(),
                new Date(),
                new BigDecimal("95000.00"),
                "PAGADA"
        );

        facturaDAO.agregar(factura1);
        facturaDAO.agregar(factura2);
        facturaDAO.agregar(factura3);
        facturaDAO.agregar(factura4);
        facturaDAO.agregar(factura5);


        // =====================================================
        // PAGOS
        // =====================================================

        Pago pago1 = new Pago(
                null,
                factura1.getId(),
                new Date(),
                new BigDecimal("50000.00"),
                "TRANSFERENCIA"
        );

        Pago pago2 = new Pago(
                null,
                factura2.getId(),
                new Date(),
                new BigDecimal("225000.00"),
                "TRANSFERENCIA"
        );

        Pago pago3 = new Pago(
                null,
                factura3.getId(),
                new Date(),
                new BigDecimal("180000.00"),
                "TARJETA"
        );

        Pago pago4 = new Pago(
                null,
                factura4.getId(),
                new Date(),
                new BigDecimal("100000.00"),
                "TRANSFERENCIA"
        );

        Pago pago5 = new Pago(
                null,
                factura5.getId(),
                new Date(),
                new BigDecimal("95000.00"),
                "EFECTIVO"
        );

        pagoDAO.agregar(pago1);
        pagoDAO.agregar(pago2);
        pagoDAO.agregar(pago3);
        pagoDAO.agregar(pago4);
        pagoDAO.agregar(pago5);


        // =====================================================
        // CUENTAS CORRIENTES
        // Una por cliente
        // =====================================================

        CuentaCorriente cuenta1 = new CuentaCorriente(
                null,
                cliente1.getId(),
                new BigDecimal("100000.00"),
                new Date()
        );

        CuentaCorriente cuenta2 = new CuentaCorriente(
                null,
                cliente2.getId(),
                new BigDecimal("0.00"),
                new Date()
        );

        CuentaCorriente cuenta3 = new CuentaCorriente(
                null,
                cliente3.getId(),
                new BigDecimal("0.00"),
                new Date()
        );

        CuentaCorriente cuenta4 = new CuentaCorriente(
                null,
                cliente4.getId(),
                new BigDecimal("210000.00"),
                new Date()
        );

        CuentaCorriente cuenta5 = new CuentaCorriente(
                null,
                cliente5.getId(),
                new BigDecimal("0.00"),
                new Date()
        );

        cuentaCorrienteDAO.agregar(cuenta1);
        cuentaCorrienteDAO.agregar(cuenta2);
        cuentaCorrienteDAO.agregar(cuenta3);
        cuentaCorrienteDAO.agregar(cuenta4);
        cuentaCorrienteDAO.agregar(cuenta5);


        // =====================================================
        // MOSTRAR IDS GENERADOS
        // =====================================================

        System.out.println("------------------------------------");
        System.out.println("DATOS DE PRUEBA GENERADOS");
        System.out.println("------------------------------------");

        System.out.println("Cliente 1: " + cliente1.getId());
        System.out.println("Cliente 2: " + cliente2.getId());
        System.out.println("Cliente 3: " + cliente3.getId());
        System.out.println("Cliente 4: " + cliente4.getId());
        System.out.println("Cliente 5: " + cliente5.getId());

        System.out.println();

        System.out.println("Envio 1: " + envio1.getId());
        System.out.println("Envio 2: " + envio2.getId());
        System.out.println("Envio 3: " + envio3.getId());
        System.out.println("Envio 4: " + envio4.getId());
        System.out.println("Envio 5: " + envio5.getId());


        System.out.println("------------------------------------");
        System.out.println("Generacion finalizada correctamente.");
    }

    public static void main(String[] args) {

    GeneradorDatosPrueba generador = new GeneradorDatosPrueba();
    generador.generarDatos();
}

}