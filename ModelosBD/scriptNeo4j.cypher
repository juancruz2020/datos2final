// Carga reproducible del grafo creado por GeneradorDatosPruebaNeo.generar().
// Los IDs CLI/CONT/VEH son los IDs iniciales de la fixture Java. En la carga
// coordinada de la aplicación, cliente, contenedor y vehículo se reemplazan
// por ObjectId de MongoDB; sus propiedades y relaciones permanecen.

// Maestros
UNWIND [
  {id:'CLI-001', razonSocial:'Logistica del Sur S.A.', cuit:'30-12345678-9'},
  {id:'CLI-002', razonSocial:'Comercio Internacional S.R.L.', cuit:'30-23456789-0'},
  {id:'CLI-003', razonSocial:'Importadora Global S.A.', cuit:'30-34567890-1'}
] AS row
MERGE (n:Cliente {id:row.id}) SET n.razonSocial=row.razonSocial, n.cuit=row.cuit;

UNWIND [
  {id:'VEH-001', identificacion:'AA123AA', patente:'AA123AA', tipo:'Camion'},
  {id:'VEH-002', identificacion:'AB456AB', patente:'AB456AB', tipo:'Camion'},
  {id:'VEH-003', identificacion:'AC789AC', patente:'AC789AC', tipo:'Camion'}
] AS row
MERGE (n:Vehiculo {id:row.id})
SET n.identificacion=row.identificacion, n.patente=row.patente, n.tipo=row.tipo;

UNWIND [
  {id:'CONT-001',tipo:'Refrigerado'}, {id:'CONT-002',tipo:'Seco'},
  {id:'CONT-003',tipo:'Open Top'}, {id:'CONT-004',tipo:'Seco'},
  {id:'CONT-005',tipo:'Seco'}, {id:'CONT-006',tipo:'Seco'},
  {id:'CONT-007',tipo:'Seco'}, {id:'CONT-008',tipo:'Seco'}
] AS row
MERGE (n:Contenedor {id:row.id}) SET n.codigo=row.id, n.tipo=row.tipo;

UNWIND [
  {id:'OP-001',nombre:'Carlos Rodriguez'}, {id:'OP-002',nombre:'Miguel Fernandez'}
] AS row
MERGE (n:Operador {id:row.id}) SET n.nombre=row.nombre;

UNWIND [
  {id:'PROV-001',nombre:'Transportes del Sur'},
  {id:'PROV-002',nombre:'Transportes Internacionales'}
] AS row
MERGE (n:Proveedor {id:row.id}) SET n.nombre=row.nombre;

UNWIND [
  {id:'SEN-001',tipo:'Temperatura'}, {id:'SEN-002',tipo:'GPS'},
  {id:'SEN-003',tipo:'Humedad'}
] AS row
MERGE (n:Sensor {id:row.id}) SET n.tipo=row.tipo;

UNWIND [
  {id:'UBI-001',clave:'buenos aires|argentina',ciudad:'Buenos Aires',pais:'Argentina'},
  {id:'UBI-002',clave:'montevideo|uruguay',ciudad:'Montevideo',pais:'Uruguay'},
  {id:'UBI-003',clave:'santiago|chile',ciudad:'Santiago',pais:'Chile'},
  {id:'UBI-004',clave:'sao paulo|brasil',ciudad:'Sao Paulo',pais:'Brasil'}
] AS row
MERGE (n:Ubicacion {clave:row.clave})
SET n.id=row.id, n.ciudad=row.ciudad, n.pais=row.pais;

// Envíos, cliente, vehículo, contenedor y ubicaciones de la fixture Java.
UNWIND [
 {id:'ENV-001',cliente:'CLI-001',contenedor:'CONT-001',vehiculo:'VEH-001',fecha:'2026-09-01T08:00:00Z',salida:'2026-09-01',entrega:'2026-09-05',origen:'Buenos Aires|Argentina',destino:'Montevideo|Uruguay',estado:'EN_TRANSITO',prioridad:'ALTA',costo:185000.0},
 {id:'ENV-002',cliente:'CLI-002',contenedor:'CONT-002',vehiculo:'VEH-002',fecha:'2026-09-02T08:00:00Z',salida:'2026-09-02',entrega:'2026-09-07',origen:'Buenos Aires|Argentina',destino:'Santiago|Chile',estado:'DEMORADO',prioridad:'ALTA',costo:230000.0},
 {id:'ENV-003',cliente:'CLI-003',contenedor:'CONT-003',vehiculo:'VEH-003',fecha:'2026-09-03T08:00:00Z',salida:'2026-09-03',entrega:'2026-09-08',origen:'Sao Paulo|Brasil',destino:'Buenos Aires|Argentina',estado:'ENTREGADO',prioridad:'MEDIA',costo:275000.0},
 {id:'ENV-004',cliente:'CLI-001',contenedor:'CONT-004',vehiculo:'VEH-001',fecha:'2026-09-04T08:00:00Z',salida:'2026-09-04',entrega:'2026-09-09',origen:'Montevideo|Uruguay',destino:'Sao Paulo|Brasil',estado:'PENDIENTE',prioridad:'BAJA',costo:160000.0},
 {id:'ENV-005',cliente:'CLI-002',contenedor:'CONT-005',vehiculo:'VEH-002',fecha:'2026-09-05T08:00:00Z',salida:'2026-09-05',entrega:'2026-09-10',origen:'Santiago|Chile',destino:'Buenos Aires|Argentina',estado:'EN_TRANSITO',prioridad:'MEDIA',costo:210000.0},
 {id:'ENV-006',cliente:'CLI-003',contenedor:'CONT-006',vehiculo:'VEH-003',fecha:'2026-09-06T08:00:00Z',salida:'2026-09-06',entrega:'2026-09-11',origen:'Buenos Aires|Argentina',destino:'Sao Paulo|Brasil',estado:'PENDIENTE',prioridad:'ALTA',costo:255000.0},
 {id:'ENV-007',cliente:'CLI-001',contenedor:'CONT-007',vehiculo:'VEH-001',fecha:'2026-09-07T08:00:00Z',salida:'2026-09-07',entrega:'2026-09-12',origen:'Sao Paulo|Brasil',destino:'Montevideo|Uruguay',estado:'CANCELADO',prioridad:'BAJA',costo:145000.0},
 {id:'ENV-008',cliente:'CLI-002',contenedor:'CONT-008',vehiculo:'VEH-002',fecha:'2026-09-08T08:00:00Z',salida:'2026-09-08',entrega:'2026-09-13',origen:'Montevideo|Uruguay',destino:'Santiago|Chile',estado:'EN_TRANSITO',prioridad:'ALTA',costo:220000.0}
] AS row
MERGE (e:Envio {id:row.id})
SET e.fechaCreacion=row.fecha, e.fechaSalida=row.salida, e.fechaEntrega=row.entrega,
    e.estado=row.estado, e.prioridad=row.prioridad, e.costoEnvio=row.costo
WITH e,row
MATCH (c:Cliente {id:row.cliente}), (co:Contenedor {id:row.contenedor}),
      (v:Vehiculo {id:row.vehiculo}), (o:Ubicacion {clave:toLower(row.origen)}),
      (d:Ubicacion {clave:toLower(row.destino)})
MERGE (c)-[:REALIZA]->(e)
MERGE (e)-[:TRANSPORTA]->(co)
MERGE (e)-[:UTILIZA]->(v)
MERGE (e)-[:SALE_DE]->(o)
MERGE (e)-[:LLEGA_A]->(d);

// Relaciones complementarias incluidas en la fixture.
UNWIND [
 {operador:'OP-001',vehiculo:'VEH-001'}, {operador:'OP-002',vehiculo:'VEH-002'}
] AS row
MATCH (op:Operador {id:row.operador}), (v:Vehiculo {id:row.vehiculo})
MERGE (op)-[:OPERA]->(v);

UNWIND [
 {proveedor:'PROV-001',vehiculo:'VEH-001'},
 {proveedor:'PROV-002',vehiculo:'VEH-002'}
] AS row
MATCH (pr:Proveedor {id:row.proveedor}), (v:Vehiculo {id:row.vehiculo})
MERGE (pr)-[:PROVEE]->(v);

UNWIND [
 {contenedor:'CONT-001',sensor:'SEN-001'},
 {contenedor:'CONT-002',sensor:'SEN-002'},
 {contenedor:'CONT-003',sensor:'SEN-003'}
] AS row
MATCH (co:Contenedor {id:row.contenedor}), (s:Sensor {id:row.sensor})
MERGE (co)-[:TIENE_SENSOR]->(s);

// Inspección de los ocho envíos cargados.
MATCH (e:Envio)
OPTIONAL MATCH (c:Cliente)-[:REALIZA]->(e)
OPTIONAL MATCH (e)-[:SALE_DE]->(o:Ubicacion)
OPTIONAL MATCH (e)-[:LLEGA_A]->(d:Ubicacion)
RETURN e.id AS envio, c.id AS cliente, o.ciudad AS origen, d.ciudad AS destino
ORDER BY envio;
