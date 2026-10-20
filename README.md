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
