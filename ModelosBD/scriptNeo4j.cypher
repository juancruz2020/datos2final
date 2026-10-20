// Plantilla parametrizada de creación del grafo de un envío.
// Reproduce los campos y vínculos de un alta con ID nuevo de EnvioNeo4jDAO.guardarCompleto.
// No reproduce el reemplazo transaccional de relaciones de una modificación.
// No crea una base diferente: el driver usa su base predeterminada.
// No añade índices ni restricciones que no existan en el código.
// Antes de ejecutar, proporcionar parámetros reales obtenidos de la aplicación:
// id, fecha, fechaSalida, fechaEntrega, costoEnvio, estado, prioridad, cliente,
// contenedores (lista de IDs Mongo como String),
// claveOrigen, ciudadOrigen, paisOrigen, claveDestino, ciudadDestino, paisDestino.
// Para usar la carga de prueba sincronizada, ejecutar el generador Java en entorno de prueba.
MERGE (e:Envio {id: $id})
SET e.fechaCreacion = $fecha, e.fechaSalida = $fechaSalida,
    e.fechaEntrega = $fechaEntrega, e.costoEnvio = $costoEnvio,
    e.estado = $estado, e.prioridad = $prioridad
REMOVE e.clienteId, e.ciudadOrigen, e.paisOrigen, e.ciudadDestino, e.paisDestino
WITH e
MERGE (c:Cliente {id: $cliente})
MERGE (c)-[:REALIZA]->(e)
MERGE (o:Ubicacion {clave: $claveOrigen})
SET o.ciudad = $ciudadOrigen, o.pais = $paisOrigen
MERGE (e)-[:SALE_DE]->(o)
MERGE (d:Ubicacion {clave: $claveDestino})
SET d.ciudad = $ciudadDestino, d.pais = $paisDestino
MERGE (e)-[:LLEGA_A]->(d)
WITH e UNWIND $contenedores AS idContenedor
MERGE (co:Contenedor {id: idContenedor})
MERGE (e)-[:TRANSPORTA]->(co);

// Asignación del vehículo. Parámetros reales: vehiculoId, patente y tipoVehiculo.
MATCH (e:Envio {id: $id})
MERGE (v:Vehiculo {id: $vehiculoId})
SET v.patente = $patente, v.tipo = $tipoVehiculo
MERGE (e)-[:UTILIZA]->(v);

// Relaciones usadas por GrafoNeo4jDAO y el generador de prueba.
// Parámetros reales adicionales: operadorId, nombreOperador, proveedorId,
// nombreProveedor, contenedorId, sensorId y tipoSensor.
MATCH (v:Vehiculo {id: $vehiculoId})
MERGE (o:Operador {id: $operadorId})
SET o.nombre = $nombreOperador
MERGE (o)-[:OPERA]->(v)
MERGE (p:Proveedor {id: $proveedorId})
SET p.nombre = $nombreProveedor
MERGE (p)-[:PROVEE]->(v);

MATCH (co:Contenedor {id: $contenedorId})
MERGE (s:Sensor {id: $sensorId})
SET s.tipo = $tipoSensor
MERGE (co)-[:TIENE_SENSOR]->(s);

// Esta plantilla es para creación. Para modificación y control de disponibilidad
// utilizar el servicio Java; no valida ni reserva los contenedores en MongoDB.
// Consultas de inspección:
MATCH (e:Envio)
OPTIONAL MATCH (c:Cliente)-[:REALIZA]->(e)
OPTIONAL MATCH (e)-[:SALE_DE]->(o:Ubicacion)
OPTIONAL MATCH (e)-[:LLEGA_A]->(d:Ubicacion)
RETURN e, c, o, d;
