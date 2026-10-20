# Modelos y documentación técnica

La aplicación utiliza MongoDB, Neo4j, Cassandra y Redis. El modelo documentado corresponde al código del proyecto revisado el 20 de octubre de 2026, sobre main b8c08a3354ba32873f3065691b215481e094e4ac y los cambios locales existentes.

## Informe técnico

[Documentación técnica en Word](DocumentacionTecnica.docx) describe la arquitectura, la justificación de los cuatro paradigmas, los modelos físicos, las validaciones, las consultas, las reservas de contenedores, la sincronización, los datos de prueba y la cobertura de requisitos del trabajo práctico. Las referencias E del informe identifican archivos, métodos y líneas del código.

Las funciones implementadas, parciales y pendientes se distinguen en el informe. Los reportes operativos y analíticos generan PDF en `reportes/` y después registran sus metadatos en MongoDB. Las facturas se cargan manualmente y tienen PDF; el cierre mensual de cuentas corrientes se solicita desde la interfaz, sin scheduler.

## Archivos por motor

| Motor | Modelo | Script o comandos | Diagrama |
| --- | --- | --- | --- |
| MongoDB | [modeloMongoDB.txt](modeloMongoDB.txt) | [scriptMongoDB.txt](scriptMongoDB.txt) | Campos y referencias en el modelo y el Word |
| Neo4j | [modeloNeo4j.txt](modeloNeo4j.txt) | [scriptNeo4j.cypher](scriptNeo4j.cypher) | [neo4jDiagrama.png](neo4jDiagrama.png) |
| Cassandra | [modeloCassandra.txt](modeloCassandra.txt) | [Scriptcassandra.txt](Scriptcassandra.txt) | [ChebotkoCassandra.png](ChebotkoCassandra.png) y [EntidadRelacionCassandra.png](EntidadRelacionCassandra.png) |
| Redis | [modeloRedis.txt](modeloRedis.txt) | [scriptRedis.txt](scriptRedis.txt) | Claves y TTL en el modelo y el Word |

El script MongoDB crea las 19 colecciones referenciadas por Java y los tres índices únicos de aperturas, cierres y movimientos, sin datos de prueba. El DDL Cassandra reproduce las cuatro tablas creadas por Java, con IDs `text`. El script Neo4j es una plantilla de creación parametrizada; requiere valores reales y no sustituye el servicio que valida y reserva contenedores en MongoDB. Los comandos Redis requieren reemplazar el marcador por un ID de usuario real.

Los datos de prueba coordinados se generan desde `DatosPruebaTotal` y los generadores Java. Los scripts de esta carpeta no están montados como inicialización automática en Docker Compose. Crear colecciones manualmente antes de llamar a `cargarDatos()` cambia su condición de carga, que comprueba si ya existe cualquier colección.

## Ejecución del entorno

Desde la raíz del proyecto:

```sh
docker compose -f ModelosBD/docker-compose.yml up -d
mvn -q -DskipTests package
```

Configurar `.env` con las variables leídas por los cuatro Singleton. MongoDB usa la base `datos2` desde los DAO y Cassandra usa el keyspace `logistica`; esos nombres están fijados en Java. Las variables de conexión se enumeran en el informe.

**Comportamiento del Main local:** la llamada a `EliminarDatos.eliminarDatosDePrueba()` está comentada. Ejecuta `DatosPruebaTotal.cargarDatos()` y abre el login. Si existen colecciones MongoDB, la carga sincroniza referencias del grafo y normaliza envíos; si no existen, genera los datos iniciales. Por lo tanto, el inicio puede escribir en las bases aunque no ejecute el borrado.

El compose declara MongoDB con `--replSet rs0`. Los archivos revisados no incluyen una inicialización automática con `rs.initiate`; esa declaración no acredita el estado del replica set en ejecución. Los servicios de movimientos y cierre requieren transacciones MongoDB.

## Reglas actuales que afectan los modelos

- Los envíos se guardan en Neo4j. `EnvioController` bajo el paquete MongoDB solo adapta consultas del grafo a objetos `Document`; no guarda una colección de envíos.
- El nodo `Envio` guarda `id`, `fechaCreacion`, `fechaSalida`, `fechaEntrega`, `estado`, `prioridad` y `costoEnvio`. Cliente y ubicaciones se obtienen mediante relaciones.
- No hay nodos `Tramo` ni una tabla o colección de tramos implementada. El recorrido actual vincula origen y destino directamente al envío.
- El contenedor termina `EN_USO` solo para un envío `EN_TRANSITO` o `DEMORADO`. Los otros estados lo dejan `DISPONIBLE`; la reserva temporal durante el guardado valida su disponibilidad.
- Los ocho envíos de prueba usan un contenedor distinto por envío. La interfaz y el modelo siguen permitiendo una lista de contenedores.
- Las referencias entre motores son lógicas y no tienen una transacción distribuida. MongoDB declara índices únicos para aperturas, cierres y movimientos; las demás comprobaciones Java no equivalen a claves foráneas o índices físicos.
- Los sensores, contenedores y dimensiones de país y región no son tablas adicionales de Cassandra. Sus referencias y métricas se explican en los diagramas.

## Comprobaciones realizadas

La revisión del 20/10/2026 es estática: código, modelos, consultas, scripts y configuración existente. Se comprobó la coincidencia de HEAD, main y el remoto con b8c08a3 antes de editar. No se compiló, no se ejecutaron pruebas de la aplicación ni scripts de bases y no se consultaron bases en ejecución. La documentación describe flujos implementados; no certifica su ejecución con datos reales.
