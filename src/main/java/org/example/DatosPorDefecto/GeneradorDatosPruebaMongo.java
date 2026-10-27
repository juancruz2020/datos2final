package org.example.DatosPorDefecto;

import com.mongodb.client.ClientSession;
import org.example.mongoDB.dao.*;

import org.example.mongoDB.model.*;
import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;


import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

public class GeneradorDatosPruebaMongo {

    private final ClienteMongoDAO clienteDAO = new ClienteMongoDAO();

    private final RolMongoDAO rolDAO = new RolMongoDAO();

    private final UsuarioMongoDAO usuarioDAO = new UsuarioMongoDAO();

    private final ConversacionMongoDAO conversacionDAO = new ConversacionMongoDAO();

    private final MensajeMongoDAO mensajeDAO = new MensajeMongoDAO();

    private final ContenedorMongoDAO contenedorDAO = new ContenedorMongoDAO();

    private final VehiculoMongoDAO vehiculoDAO = new VehiculoMongoDAO();

    private final SensorMongoDAO sensorDAO = new SensorMongoDAO();

    private final EventoLogisticoMongoDAO eventoDAO = new EventoLogisticoMongoDAO();

    private final IncidenteMongoDAO incidenteDAO = new IncidenteMongoDAO();

    private final AlertaMongoDAO alertaDAO = new AlertaMongoDAO();


    private final FacturaMongoDAO facturaDAO = new FacturaMongoDAO();

    private final PagoMongoDAO pagoDAO = new PagoMongoDAO();


    private final CuentaCorrienteMongoDAO cuentaCorrienteDAO =

            new CuentaCorrienteMongoDAO();

    public void generarDatos() {

        AppLog.info(GeneradorDatosPruebaMongo.class,
                "Generando datos de prueba de MongoDB.");

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

                null,

                administrador.getId(),

                "Juan",

                "Perez",

                "juan@logisticadelsur.com",

                "clave1",

                "ACTIVO",

                new Date()

        );

        Usuario usuario2 = new Usuario(

                null,

                null,

                operador.getId(),

                "Maria",

                "Gomez",

                "maria@transportesandinos.com",

                "clave2",

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

                "clave3",

                "ACTIVO",

                new Date()

        );

        Usuario usuario4 = new Usuario(

                null,

                cliente4.getId(),

                clienteRol.getId(),

                "Sofia",

                "Martinez",

                "sofia@exportadorapampeana.com",

                "clave4",

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

                "clave5",

                "ACTIVO",

                new Date()

        );

        Usuario usuarioAdministrador = new Usuario(

                null,

                null,

                administrador.getId(),

                "1",

                "1",

                "1",

                "1",

                "ACTIVO",

                new Date()

        );

        // USUARIO DE PRUEBA CLIENTE: usuario 2, contraseña 2
        Usuario usuarioClientePrueba = new Usuario(
                null,
                cliente3.getId(),
                clienteRol.getId(),
                "2",
                "2",
                "2",
                "2",
                "ACTIVO",
                new Date()
        );

        // USUARIO DE PRUEBA OPERADOR: usuario 3, contraseña 3
        Usuario usuarioOperadorPrueba = new Usuario(
                null,
                null,
                operador.getId(),
                "3",
                "3",
                "3",
                "3",
                "ACTIVO",
                new Date()
        );

        // UNA CUENTA CLIENTE POR CADA EMPRESA
        // Juan y Maria conservan sus roles internos, sin cliente asociado.
        // Sofia utiliza el rol Cliente para Exportadora Pampeana.

        Usuario usuario6 = new Usuario(
                null,
                cliente1.getId(),
                clienteRol.getId(),
                "Carolina",
                "Ruiz",
                "carolina@logisticadelsur.com",
                "clave6",
                "ACTIVO",
                new Date()
        );

        Usuario usuario7 = new Usuario(
                null,
                cliente2.getId(),
                clienteRol.getId(),
                "Diego",
                "Torres",
                "diego@transportesandinos.com",
                "clave7",
                "ACTIVO",
                new Date()
        );

        usuarioDAO.agregar(usuario1);

        usuarioDAO.agregar(usuario2);

        usuarioDAO.agregar(usuario3);

        usuarioDAO.agregar(usuario4);

        usuarioDAO.agregar(usuario5);

        usuarioDAO.agregar(usuarioAdministrador);
        usuarioDAO.agregar(usuario6);
        usuarioDAO.agregar(usuario7);
        usuarioDAO.agregar(usuarioClientePrueba);
        usuarioDAO.agregar(usuarioOperadorPrueba);

        // =====================================================
        // COMUNICACION: CHATS PRIVADOS Y GRUPALES
        // IDs reales de los usuarios recién insertados, incluidas las cuentas 1, 2 y 3.
        // =====================================================

        Date fechaConversaciones = new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000);

        Conversacion privadoCoordinacion = new Conversacion(
                null, "Juan Perez - Maria Gomez", "PRIVADA",
                List.of(usuario1.getId(), usuario2.getId()), fechaConversaciones
        );
        Conversacion privadoLucas = new Conversacion(
                null, "Maria Gomez - Lucas Fernandez", "PRIVADA",
                List.of(usuario2.getId(), usuario3.getId()), fechaConversaciones
        );
        Conversacion privadoSofia = new Conversacion(
                null, "Maria Gomez - Sofia Martinez", "PRIVADA",
                List.of(usuario2.getId(), usuario4.getId()), fechaConversaciones
        );
        Conversacion privadoMartin = new Conversacion(
                null, "Juan Perez - Martin Lopez", "PRIVADA",
                List.of(usuario1.getId(), usuario5.getId()), fechaConversaciones
        );
        Conversacion privadoTransportes = new Conversacion(
                null, "Carolina Ruiz - Diego Torres", "PRIVADA",
                List.of(usuario6.getId(), usuario7.getId()), fechaConversaciones
        );
        Conversacion privadoClientePrueba = new Conversacion(
                null, "1 1 - 2 2", "PRIVADA",
                List.of(usuarioAdministrador.getId(), usuarioClientePrueba.getId()), fechaConversaciones
        );
        Conversacion privadoOperadorPrueba = new Conversacion(
                null, "Maria Gomez - 3 3", "PRIVADA",
                List.of(usuario2.getId(), usuarioOperadorPrueba.getId()), fechaConversaciones
        );
        Conversacion grupoOperaciones = new Conversacion(
                null, "Coordinacion de operaciones", "GRUPAL",
                List.of(usuario1.getId(), usuario2.getId(), usuarioAdministrador.getId(),
                        usuarioOperadorPrueba.getId()), fechaConversaciones
        );
        Conversacion grupoClientes = new Conversacion(
                null, "Atencion a clientes", "GRUPAL",
                List.of(usuario2.getId(), usuario3.getId(), usuario4.getId(), usuario5.getId(),
                        usuarioClientePrueba.getId()), fechaConversaciones
        );
        Conversacion grupoTransportes = new Conversacion(
                null, "Coordinacion de transportes", "GRUPAL",
                List.of(usuario1.getId(), usuario6.getId(), usuario7.getId()), fechaConversaciones
        );

        conversacionDAO.agregar(privadoCoordinacion);
        conversacionDAO.agregar(privadoLucas);
        conversacionDAO.agregar(privadoSofia);
        conversacionDAO.agregar(privadoMartin);
        conversacionDAO.agregar(privadoTransportes);
        conversacionDAO.agregar(privadoClientePrueba);
        conversacionDAO.agregar(privadoOperadorPrueba);
        conversacionDAO.agregar(grupoOperaciones);
        conversacionDAO.agregar(grupoClientes);
        conversacionDAO.agregar(grupoTransportes);

        // Cada remitente pertenece al chat y los mensajes son posteriores a su creación.
        Date primerMensaje = new Date(fechaConversaciones.getTime() + 60_000);
        Date segundoMensaje = new Date(fechaConversaciones.getTime() + 120_000);
        Date tercerMensaje = new Date(fechaConversaciones.getTime() + 180_000);

        mensajeDAO.agregar(new Mensaje(null, privadoCoordinacion.getId(), usuario1.getId(),
                "Maria, revisemos las novedades de operaciones.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoCoordinacion.getId(), usuario2.getId(),
                "Estoy reuniendo las consultas de los clientes.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoCoordinacion.getId(), usuario1.getId(),
                "Gracias, coordinamos las prioridades por el grupo.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, privadoLucas.getId(), usuario3.getId(),
                "Hola Maria, necesito consultar la documentacion de Comercio Internacional SA.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoLucas.getId(), usuario2.getId(),
                "Hola Lucas, indicame que documentacion necesitas revisar.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoLucas.getId(), usuario3.getId(),
                "Quisiera confirmar los requisitos para el proximo despacho.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, privadoSofia.getId(), usuario4.getId(),
                "Hola Maria, estamos preparando una carga de Exportadora Pampeana.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoSofia.getId(), usuario2.getId(),
                "Hola Sofia, pasame el horario previsto para coordinar la recepcion.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoSofia.getId(), usuario4.getId(),
                "La carga estara lista a las 10, te confirmo antes de salir.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, privadoMartin.getId(), usuario5.getId(),
                "Juan, necesitamos coordinar una consulta de Patagonia Cargo.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoMartin.getId(), usuario1.getId(),
                "Hola Martin, contame que necesitan y lo revisamos.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoMartin.getId(), usuario5.getId(),
                "Queremos confirmar la disponibilidad para una nueva carga refrigerada.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, privadoTransportes.getId(), usuario6.getId(),
                "Diego, podemos revisar los horarios de recepcion entre ambas empresas?", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoTransportes.getId(), usuario7.getId(),
                "Si Carolina, Transportes Andinos recibe consultas hasta las 18.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoTransportes.getId(), usuario6.getId(),
                "Gracias, comparto el dato con Logistica del Sur.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, privadoClientePrueba.getId(), usuarioClientePrueba.getId(),
                "Hola, quisiera consultar como coordinar un despacho de Comercio Internacional SA.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoClientePrueba.getId(), usuarioAdministrador.getId(),
                "Indicanos la carga y la fecha prevista para revisar la solicitud.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoClientePrueba.getId(), usuarioClientePrueba.getId(),
                "Gracias, voy a reunir esos datos para enviarlos.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, privadoOperadorPrueba.getId(), usuario2.getId(),
                "Podemos repasar las consultas pendientes del turno?", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoOperadorPrueba.getId(), usuarioOperadorPrueba.getId(),
                "Si, estoy disponible para revisar las prioridades.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, privadoOperadorPrueba.getId(), usuario2.getId(),
                "Empecemos por las solicitudes de documentacion.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, grupoOperaciones.getId(), usuarioAdministrador.getId(),
                "Usaremos este grupo para coordinar al equipo interno.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, grupoOperaciones.getId(), usuario1.getId(),
                "Voy a revisar las prioridades con Maria.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, grupoOperaciones.getId(), usuarioOperadorPrueba.getId(),
                "Quedo disponible para atender las consultas del turno.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, grupoClientes.getId(), usuario2.getId(),
                "Bienvenidos, por este grupo coordinaremos consultas generales de los clientes.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, grupoClientes.getId(), usuario3.getId(),
                "Gracias Maria, estamos preparando la documentacion del proximo despacho.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, grupoClientes.getId(), usuarioClientePrueba.getId(),
                "Confirmo que puedo ver los mensajes del grupo.", tercerMensaje));

        mensajeDAO.agregar(new Mensaje(null, grupoTransportes.getId(), usuario1.getId(),
                "Coordinemos aqui las consultas entre Logistica del Sur y Transportes Andinos.", primerMensaje));
        mensajeDAO.agregar(new Mensaje(null, grupoTransportes.getId(), usuario6.getId(),
                "De acuerdo, voy a reunir los horarios de Logistica del Sur.", segundoMensaje));
        mensajeDAO.agregar(new Mensaje(null, grupoTransportes.getId(), usuario7.getId(),
                "Por Transportes Andinos voy a confirmar los horarios disponibles.", tercerMensaje));

        // =====================================================

        // CONTENEDORES

        // =====================================================

        Contenedor contenedor1 =

                new Contenedor(null, "CONT-001", "SECO", 30000, "EN_USO");

        Contenedor contenedor2 =

                new Contenedor(null, "CONT-002", "REFRIGERADO", 25000, "EN_USO");

        Contenedor contenedor3 =

                new Contenedor(null, "CONT-003", "SECO", 32000, "DISPONIBLE");

        Contenedor contenedor4 =

                new Contenedor(null, "CONT-004", "TANQUE", 28000, "DISPONIBLE");

        Contenedor contenedor5 =

                new Contenedor(null, "CONT-005", "REFRIGERADO", 26000, "EN_USO");

        contenedorDAO.agregar(contenedor1);

        contenedorDAO.agregar(contenedor2);

        contenedorDAO.agregar(contenedor3);

        contenedorDAO.agregar(contenedor4);

        contenedorDAO.agregar(contenedor5);

        // Un contenedor diferente por cada envío de prueba (ENV-001 a ENV-008).

        contenedorDAO.agregar(new Contenedor(null, "CONT-006", "SECO", 30000, "DISPONIBLE"));

        contenedorDAO.agregar(new Contenedor(null, "CONT-007", "SECO", 30000, "DISPONIBLE"));

        contenedorDAO.agregar(new Contenedor(null, "CONT-008", "REFRIGERADO", 26000, "EN_USO"));

        // =====================================================

        // VEHICULOS

        // =====================================================

        Vehiculo vehiculo1 =

                new Vehiculo(

                        null,

                        "CAMION",

                        "AA123BB",

                        "Logistica del Sur",

                        "ACTIVO"

                );

        Vehiculo vehiculo2 =

                new Vehiculo(

                        null,

                        "CAMION",

                        "AB456CD",

                        "Transportes Andinos",

                        "ACTIVO"

                );

        Vehiculo vehiculo3 =

                new Vehiculo(

                        null,

                        "FURGON",

                        "AC789EF",

                        "Comercio Internacional SA",

                        "ACTIVO"

                );

        Vehiculo vehiculo4 =

                new Vehiculo(

                        null,

                        "CAMION",

                        "AD321GH",

                        "Exportadora Pampeana",

                        "INACTIVO"

                );

        Vehiculo vehiculo5 =

                new Vehiculo(

                        null,

                        "CAMION",

                        "AE654IJ",

                        "Patagonia Cargo",

                        "ACTIVO"

                );

        vehiculoDAO.agregar(vehiculo1);

        vehiculoDAO.agregar(vehiculo2);

        vehiculoDAO.agregar(vehiculo3);

        vehiculoDAO.agregar(vehiculo4);

        vehiculoDAO.agregar(vehiculo5);

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

        // EVENTOS LOGISTICOS

        // =====================================================

        EventoLogistico evento1 = new EventoLogistico(

                null,

                "ENV-001",

                new Date(),

                "SALIDA",

                "Buenos Aires",

                "El envio salio del centro logistico"

        );

        EventoLogistico evento2 = new EventoLogistico(

                null,

                "ENV-002",

                new Date(),

                "DEMORA",

                "Mendoza",

                "Demora en el transporte"

        );

        EventoLogistico evento3 = new EventoLogistico(

                null,

                "ENV-003",

                new Date(),

                "ENTREGA",

                "Montevideo",

                "Mercaderia entregada"

        );

        EventoLogistico evento4 = new EventoLogistico(

                null,

                "ENV-004",

                new Date(),

                "CONTROL",

                "Rosario",

                "Control de carga realizado"

        );

        EventoLogistico evento5 = new EventoLogistico(

                null,

                "ENV-005",

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

                "ENV-001",

                "DEMORA",

                new Date(),

                "MEDIA",

                "ABIERTO",

                "Demora por condiciones climaticas"

        );

        Incidente incidente2 = new Incidente(

                null,

                "ENV-002",

                "RUTA",

                new Date(),

                "ALTA",

                "ABIERTO",

                "Ruta temporalmente bloqueada"

        );

        Incidente incidente3 = new Incidente(

                null,

                "ENV-003",

                "DOCUMENTACION",

                new Date(),

                "BAJA",

                "CERRADO",

                "Documentacion verificada"

        );

        Incidente incidente4 = new Incidente(

                null,

                "ENV-004",

                "MECANICO",

                new Date(),

                "MEDIA",

                "ABIERTO",

                "Revision del vehiculo"

        );

        Incidente incidente5 = new Incidente(

                null,

                "ENV-005",

                "OPERATIVO",

                new Date(),

                "BAJA",

                "CERRADO",

                "Incidente operativo resuelto"

        );

        Incidente incidente6 = new Incidente(

                null,

                "ENV-006",

                "ADUANA",

                new Date(),

                "ALTA",

                "ABIERTO",

                "Retención preventiva en la ruta"

        );

        Incidente incidente7 = new Incidente(

                null,

                "ENV-007",

                "CLIMATICO",

                new Date(),

                "MEDIA",

                "ABIERTO",

                "Demora por condiciones climáticas"

        );

        incidenteDAO.agregar(incidente1);

        incidenteDAO.agregar(incidente2);

        incidenteDAO.agregar(incidente3);

        incidenteDAO.agregar(incidente4);

        incidenteDAO.agregar(incidente5);

        incidenteDAO.agregar(incidente6);

        incidenteDAO.agregar(incidente7);

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
        // FACTURAS
        // =====================================================

        // Se utilizan fechas relativas al día de ejecución, en horario argentino.
        // La fecha de vencimiento nunca es anterior a la de emisión.
        java.time.ZoneId zona = java.time.ZoneId.of("America/Argentina/Buenos_Aires");
        java.time.LocalDate hoy = java.time.LocalDate.now(zona);

        // 1. Pendiente: vence dentro de 10 días. Pago parcial de $50.000.
        Factura factura1 = new Factura(
                null,
                cliente1.getId(),
                Date.from(hoy.atStartOfDay(zona).toInstant()),
                Date.from(hoy.plusDays(10).atStartOfDay(zona).toInstant()),
                new BigDecimal("150000.00"),
                "PENDIENTE"
        );

        // 2. Pagada: venció hace 10 días, pero está cancelada.
        Factura factura2 = new Factura(
                null,
                cliente2.getId(),
                Date.from(hoy.minusDays(40).atStartOfDay(zona).toInstant()),
                Date.from(hoy.minusDays(10).atStartOfDay(zona).toInstant()),
                new BigDecimal("225000.00"),
                "PAGADA"
        );

        // 3. Pagada: su vencimiento todavía no llegó.
        Factura factura3 = new Factura(
                null,
                cliente3.getId(),
                Date.from(hoy.minusDays(5).atStartOfDay(zona).toInstant()),
                Date.from(hoy.plusDays(15).atStartOfDay(zona).toInstant()),
                new BigDecimal("180000.00"),
                "PAGADA"
        );

        // 4. Pendiente al crearla: el servicio la mostrará VENCIDA.
        // Tiene un pago parcial de $100.000, no está totalmente pagada.
        Factura factura4 = new Factura(
                null,
                cliente4.getId(),
                Date.from(hoy.minusDays(30).atStartOfDay(zona).toInstant()),
                Date.from(hoy.minusDays(5).atStartOfDay(zona).toInstant()),
                new BigDecimal("310000.00"),
                "PENDIENTE"
        );

        // 5. Pagada: vence hoy. Se conserva PAGADA porque tiene pago total.
        Factura factura5 = new Factura(
                null,
                cliente5.getId(),
                Date.from(hoy.minusDays(15).atStartOfDay(zona).toInstant()),
                Date.from(hoy.atStartOfDay(zona).toInstant()),
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

        // Una sesión no inicia una transacción por sí sola.
        // withTransaction confirma los cinco pagos juntos o revierte sus inserciones.
        try (ClientSession session = obtenerSesion()) {
            session.withTransaction(() -> {
                pagoDAO.agregar(session, pago1);
                pagoDAO.agregar(session, pago2);
                pagoDAO.agregar(session, pago3);
                pagoDAO.agregar(session, pago4);
                pagoDAO.agregar(session, pago5);
                return null;
            });
        }

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

        AppLog.info(GeneradorDatosPruebaMongo.class,
                "Datos de prueba MongoDB generados correctamente.");

    }

    // =====================================================

    // GENERAR DATOS DE PRUEBA

    // =====================================================


    /** Crea una sesión desde el cliente compartido. Quien la utiliza debe cerrarla. */
    public static ClientSession obtenerSesion() {
        return MongoSingleton.getInstance().startSession();
    }
}
