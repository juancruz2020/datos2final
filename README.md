# datos2final

## Acceso a las bases de datos

Los comandos asumen que Docker está en ejecución y que los contenedores usan
los nombres indicados. Ejecutalos desde PowerShell o una terminal compatible.

### MongoDB

```powershell
docker exec -it datos2-mongodb mongosh datos2
```

En `mongosh`, `show collections` lista las colecciones de la base `datos2`.

### Cassandra

```powershell
docker exec -it datos2-cassandra cqlsh
```

Dentro de `cqlsh`, seleccioná el keyspace de la aplicación:

```sql
USE datos2;
```

### Redis

```powershell
docker exec -it datos2-redis redis-cli -a TuPasswordRedis123
```

Usá `PING` para comprobar la conexión. La contraseña corresponde a la
configuración de `ModelosBD/docker-compose.yml`.

### Neo4j

Consola Cypher (solicita la contraseña de `neo4j`):

MATCH (n)
OPTIONAL MATCH (n)-[r]-(m)
RETURN n, r, m;

```powershell
docker exec -it datos2-neo4j cypher-shell -u neo4j -p tu_password
```

Interfaz gráfica Neo4j Browser: [http://localhost:7474](http://localhost:7474)

Iniciá sesión con el usuario `neo4j` y la contraseña `tu_password` de
`ModelosBD/docker-compose.yml`.
La dirección Bolt para clientes es `bolt://localhost:7687`.

## Iniciar los contenedores

```powershell
docker compose -f ModelosBD/docker-compose.yml up -d
```

MongoDB está configurado como replica set (`rs0`) porque las transacciones
multidocumento de la aplicación lo requieren. En una instalación nueva,
inicializalo una vez después de iniciar el contenedor:

```powershell
docker exec datos2-mongodb mongosh --eval "rs.initiate({_id: 'rs0', members: [{_id: 0, host: 'localhost:27017'}]})"
```

La URI de `.env` debe incluir `?replicaSet=rs0`; podés partir de
`.env.example`. Si `rs.initiate` informa que el replica set ya está iniciado,
no hace falta repetirlo.

## Transacciones MongoDB

Se usan transacciones solo donde una operación de negocio escribe documentos
relacionados que deben confirmarse o revertirse juntos:

- **Crear un cliente desde `ClienteService`:** genera el código, guarda el
  cliente, crea su cuenta corriente y registra la apertura inicial. Así no
  queda un cliente a medio alta si falla la cuenta o su apertura. La creación
  aislada de una cuenta también usa la misma transacción cuenta más apertura.
- **Registrar un pago:** inserta el pago, registra el débito en la cuenta
  corriente, actualiza el saldo y marca la factura como pagada. Si falla
  cualquiera de esos pasos, se revierten todos para evitar cobros duplicados o
  saldos y estados inconsistentes.
- **Registrar un movimiento de cuenta corriente:** guarda el movimiento, la
  apertura del período cuando corresponda y el nuevo saldo como una sola
  operación.
- **Cerrar un período mensual:** calcula el cierre a partir de la apertura,
  los cierres anteriores y los movimientos, y persiste el resultado dentro de
  la transacción para no guardar un cierre parcial.

En Neo4j, guardar un envío y todas sus relaciones, y normalizar envíos
existentes, ya agrupa sus varias sentencias con `executeWrite`. Las mutaciones
restantes de Neo4j son una sola sentencia Cypher, que Neo4j confirma como una
transacción. En Redis cada cambio de sesión es un solo comando (`SETEX`, `DEL`
o `EXPIRE`), también atómico.

En Cassandra, registrar una lectura escribe el dato original, su proyección de
posiciones y las métricas de país y región. Esas cuatro escrituras ahora se
envían en un batch `LOGGED` para evitar que un fallo deje solo algunas de las
filas persistidas. Cassandra no ofrece aislamiento de transacción SQL para
este flujo: las métricas se calculan con lecturas previas y dos inserciones
concurrentes pueden competir al actualizar el agregado.

Las operaciones MongoDB de una sola escritura no se envuelven de nuevo en
transacciones porque no ganarían consistencia adicional. Tampoco hay una
transacción distribuida entre MongoDB, Neo4j, Cassandra y Redis. En particular,
la reserva de contenedores coordinada entre MongoDB y Neo4j usa compensación:
si falla el guardado del envío, se restaura la reserva MongoDB; no se puede
confirmar ambos motores en una única transacción.
