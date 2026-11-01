"""Regenera los tres diagramas PNG y sus fuentes SVG. Requiere Pillow.

Ejecutar desde cualquier directorio: python3 ModelosBD/diagramas/generar_diagramas.py
Los diagramas describen el código Java; no requieren conexiones a las bases.
"""
from pathlib import Path
from html import escape
import math
from PIL import Image, ImageDraw, ImageFont

HERE = Path(__file__).resolve().parent
OUT = HERE.parent
INK, MUTED, LINE = '#18304e', '#50647a', '#70869a'
BLUE, TEAL, GOLD = '#235c9e', '#087d78', '#946419'
FONT_DIRS = [Path('/System/Library/Fonts/Supplemental'),
             Path('/usr/share/fonts/truetype/dejavu')]


def font(size, bold=False):
    for directory in FONT_DIRS:
        for name in (['Arial Bold.ttf', 'DejaVuSans-Bold.ttf'] if bold
                     else ['Arial.ttf', 'DejaVuSans.ttf']):
            if (directory / name).exists():
                return ImageFont.truetype(str(directory / name), size)
    raise RuntimeError('Instalar Arial o DejaVu Sans para regenerar las imágenes.')


class Diagram:
    def __init__(self, title, subtitle, height):
        self.w, self.h = 2400, height
        self.im = Image.new('RGB', (self.w, self.h), 'white')
        self.d = ImageDraw.Draw(self.im)
        self.svg = [f'<svg xmlns="http://www.w3.org/2000/svg" width="2400" height="{height}" viewBox="0 0 2400 {height}">',
                    '<rect width="100%" height="100%" fill="white"/>']
        self.boxes = []
        self.rect(80, 64, 12, 95, BLUE)
        self.text(120, 60, title, 52, True)
        self.text(120, 132, subtitle, 28, color=MUTED)
        self.text(2320, 190, 'DATOS2FINAL · 01/11/2026', 22, color=MUTED, anchor='right')

    def rect(self, x, y, w, h, fill, stroke=None, radius=0):
        self.d.rounded_rectangle((x, y, x+w, y+h), radius, fill=fill, outline=stroke, width=2)
        self.svg.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{radius}" fill="{fill}" stroke="{stroke or fill}" stroke-width="2"/>')

    def text(self, x, y, text, size=28, bold=False, color=INK, anchor='left'):
        f = font(size, bold)
        width = self.d.textlength(text, font=f)
        if anchor == 'center':
            x -= width / 2
        elif anchor == 'right':
            x -= width
        assert x >= 0 and x + width < self.w, (text, x, width)
        assert y >= 0 and y + size + 5 < self.h, (text, y)
        self.d.text((x, y), text, font=f, fill=color, anchor='lt')
        self.svg.append(f'<text x="{x}" y="{y}" dominant-baseline="text-before-edge" font-family="Arial, DejaVu Sans, sans-serif" font-size="{size}" font-weight="{700 if bold else 400}" fill="{color}">{escape(text)}</text>')
        return width

    def card(self, x, y, w, h, title, rows, color=BLUE, size=28):
        for a, b, c, d in self.boxes:
            assert x+w <= a or a+c <= x or y+h <= b or b+d <= y, ('Tarjetas superpuestas', title)
        self.boxes.append((x,y,w,h))
        self.rect(x, y, w, h, '#f7f9fc', '#b8c6d3', 16)
        self.rect(x+1, y+1, w-2, 68, color, radius=14)
        self.rect(x+1, y+40, w-2, 29, color)
        assert self.d.textlength(title, font=font(31, True)) < w-44, title
        self.text(x+22, y+19, title, 31, True, 'white')
        for i, row in enumerate(rows):
            assert self.d.textlength(row, font=font(size)) < w-44, row
            assert 94+i*43+size < h-12, (title, row, h)
            self.text(x+24, y+94+i*43, row, size)

    def arrow(self, points, color=LINE, dashed=False):
        # Las rutas se mantienen fuera del interior de las tarjetas.
        for (x1,y1),(x2,y2) in zip(points, points[1:]):
            assert x1 == x2 or y1 == y2, 'Usar rutas ortogonales'
            for x,y,w,h in self.boxes:
                if x1 == x2:
                    assert not (x < x1 < x+w and max(min(y1,y2),y) < min(max(y1,y2),y+h)), 'Flecha sobre tarjeta'
                else:
                    assert not (y < y1 < y+h and max(min(x1,x2),x) < min(max(x1,x2),x+w)), 'Flecha sobre tarjeta'
            if dashed:
                length = math.hypot(x2-x1,y2-y1)
                for start in range(0, int(length), 20):
                    end = min(start+11,length)
                    self.d.line((x1+(x2-x1)*start/length,y1+(y2-y1)*start/length,
                                 x1+(x2-x1)*end/length,y1+(y2-y1)*end/length),fill=color,width=4)
            else:
                self.d.line((x1,y1,x2,y2),fill=color,width=4)
        coords = ' '.join(f'{x},{y}' for x,y in points)
        self.svg.append(f'<polyline points="{coords}" fill="none" stroke="{color}" stroke-width="4" stroke-dasharray="{"11 9" if dashed else "none"}"/>')
        x,y=points[-1]; px,py=points[-2]
        angle=math.atan2(y-py,x-px)
        tri=[(x,y),(x-19*math.cos(angle)+9*math.sin(angle),y-19*math.sin(angle)-9*math.cos(angle)),
             (x-19*math.cos(angle)-9*math.sin(angle),y-19*math.sin(angle)+9*math.cos(angle))]
        self.d.polygon(tri,fill=color)
        self.svg.append(f'<polygon points="{" ".join(f"{a},{b}" for a,b in tri)}" fill="{color}"/>')

    def label(self, x, y, text, color=INK):
        width=self.d.textlength(text,font=font(27,True))
        self.rect(x-width/2-12,y-7,width+24,43,'white',radius=5)
        self.text(x,y,text,27,True,color,anchor='center')

    def save(self, name):
        self.im.save(OUT / f'{name}.png', optimize=True, dpi=(180,180))
        (HERE / f'{name}.svg').write_text('\n'.join(self.svg+['</svg>']), encoding='utf-8')
        preview=self.im.copy(); preview.thumbnail((1200,1000))
        preview.save(Path('/tmp') / f'{name}_preview.png')
        print(f'{name}: {self.w} × {self.h}; {len(self.boxes)} tarjetas verificadas')


def neo4j():
    d=Diagram('Neo4j · Grafo logístico', 'Etiquetas, propiedades y relaciones dirigidas escritas por los DAO y el generador de prueba.', 1840)
    d.card(80,250,540,280,'Cliente',['id: String','razonSocial, cuit: String (*)'])
    d.card(900,250,650,480,'Envio',['id: String','fechaCreacion: String (ISO instant)','fechaSalida: String (AAAA-MM-DD)','fechaEntrega: String (AAAA-MM-DD)','estado, prioridad: String','costoEnvio: Double'])
    d.card(1840,250,480,370,'Ubicacion',['clave: String','ciudad, pais: String','clave = ciudad|pais','trim + minúsculas','id: String (solo prueba)'])
    d.card(80,960,540,280,'Vehiculo',['id: String','patente, tipo: String','identificacion: String (*)'])
    d.card(900,960,650,250,'Contenedor',['id: String','codigo, tipo: String (*)'])
    d.card(1840,960,480,250,'Sensor',['id, tipo: String (*)'],color=TEAL)
    d.card(80,1420,540,180,'Operador',['id, nombre: String (*)'],color=TEAL)
    d.card(900,1420,650,180,'Proveedor',['id, nombre: String (*)'],color=TEAL)
    d.arrow([(620,390),(900,390)]); d.label(760,350,'REALIZA')
    d.arrow([(1550,380),(1840,380)]); d.label(1695,340,'SALE_DE')
    d.arrow([(1550,540),(1840,540)]); d.label(1695,500,'LLEGA_A')
    d.arrow([(1050,730),(1050,830),(350,830),(350,960)]); d.label(700,795,'UTILIZA')
    d.arrow([(1350,730),(1350,960)]); d.label(1350,845,'TRANSPORTA')
    d.arrow([(1550,1080),(1840,1080)],TEAL); d.label(1695,1040,'TIENE_SENSOR',TEAL)
    d.arrow([(250,1420),(250,1240)],TEAL); d.label(250,1320,'OPERA',TEAL)
    d.arrow([(1225,1420),(1225,1360),(500,1360),(500,1240)],TEAL); d.label(860,1320,'PROVEE',TEAL)
    d.text(80,1650,'(*) Propiedades o nodos cargados por datos de prueba; MERGE por id puede crear nodos incompletos.',27)
    d.text(80,1696,'Cliente, Contenedor y Vehiculo comparten el ObjectId MongoDB como texto tras la sincronización.',27)
    d.text(80,1742,'Relaciones sin propiedades. Sin nodos Tramo ni restricciones físicas de cardinalidad o unicidad declaradas.',27)
    d.save('neo4jDiagrama')


def chebotko():
    d=Diagram('Cassandra · Modelo físico orientado a consultas', 'Keyspace logistica · K = partición · C↓ = clustering DESC · columnas sin marca = datos.', 1910)
    d.card(80,250,1080,650,'lecturas_sensor',[
        'K    sensor_id: text','K    fecha_dia: date','C↓  fecha_hora: timestamp','contenedor_id: text','temperatura, humedad, vibracion: decimal',
        'latitud, longitud, bateria: decimal','pais, region: text','Q1: historial por sensor y día; intervalo horario',
        'Q2: temperatura y batería por sensor y día'])
    d.card(1240,250,1080,650,'posiciones_sensor',[
        'K    sensor_id: text','C↓  fecha_hora: timestamp','contenedor_id: text','latitud, longitud: decimal','pais, region: text',
        'Q3: GPS por sensor en [inicio UTC, día siguiente)','La partición abarca todos los días del sensor.','La tabla no incluye fecha_dia.'])
    d.card(80,960,1080,430,'metricas_iot_region_dia',[
        'K    region: text','C↓  fecha_dia: date','temperatura_min, temperatura_max: decimal','humedad_promedio: decimal','cantidad_lecturas: bigint',
        'Q4: métricas por región y rango de días'])
    d.card(1240,960,1080,430,'metricas_iot_pais_dia',[
        'K    pais: text','C↓  fecha_dia: date','humedad_promedio: decimal','temperatura_promedio: decimal','cantidad_lecturas: bigint',
        'Q5: métricas por país y rango de días'])
    d.card(80,1450,720,300,'alertas',[
        'K    id: text','nombre, descripcion: text','Q6: listar catálogo de tipos de alerta',
        'Catálogo independiente del monitoreo.'],color=TEAL)
    d.text(870,1460,'ESCRITURA DEL MONITOREO',30,True,color=BLUE)
    d.text(870,1520,'Un batch LOGGED agrupa lectura, posición y las dos métricas.',27)
    d.text(870,1565,'Los agregados se calculan con lecturas previas al batch.',27)
    d.text(870,1610,'Sin aislamiento para ese cálculo: pueden perderse actualizaciones',27)
    d.text(870,1655,'concurrentes o contarse dos veces las lecturas reinsertadas.',27)
    d.text(80,1805,'Sin claves foráneas, índices secundarios ni TTL en el DDL. Las vistas también ejecutan SELECT sin filtro.',27)
    d.text(80,1850,'Fuentes: CrearTablasMonitoreo, CrearTablaAlertas, ConsultasCassandraMonitoreo y AlertasCassandraDAO.',25,color=MUTED)
    d.save('ChebotkoCassandra')


def conceptual():
    d=Diagram('Monitoreo · Entidades y referencias entre motores', 'Vista conceptual: maestros MongoDB, referencias por identificador y proyecciones físicas en Cassandra.', 1830)
    d.card(80,250,900,330,'contenedores · MongoDB',[
        '_id: ObjectId','codigo_internacional, tipo, estado: String','capacidad: Double','envio_asignado: String (opcional)'],color=TEAL)
    d.card(1420,250,900,330,'sensores · MongoDB',[
        '_id: ObjectId · codigo: String','contenedor_id: ObjectId','tipo, fabricante, estado: String','fecha_instalacion: Date'],color=TEAL)
    d.arrow([(1420,405),(980,405)],TEAL,True); d.label(1200,360,'contenedor_id → _id',TEAL)
    d.text(1200,450,'Referencia lógica',25,color=MUTED,anchor='center')
    d.card(560,820,1280,360,'Lectura IoT · registro conceptual',[
        'sensor_id, contenedor_id: texto de los ObjectId MongoDB','fecha_dia: date · fecha_hora: timestamp',
        'temperatura, humedad, vibracion, bateria: decimal','latitud, longitud: decimal · pais, region: text',
        'Una lectura produce dos registros y actualiza dos agregados.'])
    d.arrow([(530,580),(530,690),(850,690),(850,820)],TEAL,True)
    d.label(760,650,'_id → contenedor_id',TEAL)
    d.arrow([(1870,580),(1870,690),(1550,690),(1550,820)],TEAL,True)
    d.label(1630,650,'_id → sensor_id',TEAL)
    d.card(80,1350,1040,270,'lecturas_sensor · Cassandra',[
        'PK: ((sensor_id, fecha_dia), fecha_hora)','Registro completo de monitoreo.','Historial por sensor, día y hora.'])
    d.card(1280,1350,1040,270,'posiciones_sensor · Cassandra',[
        'PK: ((sensor_id), fecha_hora)','Proyección GPS: coordenadas, país y región.','Conserva sensor_id y contenedor_id.'])
    d.arrow([(850,1180),(850,1260),(600,1260),(600,1350)]); d.label(620,1220,'Registro completo')
    d.arrow([(1550,1180),(1550,1260),(1800,1260),(1800,1350)]); d.label(1790,1220,'Proyección GPS')
    d.text(80,1670,'Las flechas discontinuas indican referencias lógicas; no hay claves foráneas entre motores.',27)
    d.text(80,1715,'El servicio exige IDs no vacíos; no comprueba su existencia ni la asociación sensor–contenedor en MongoDB.',27)
    d.text(80,1760,'País y región son dimensiones de métricas, sin tablas maestras propias en Cassandra. Ver ChebotkoCassandra.',27)
    d.save('EntidadRelacionCassandra')


if __name__ == '__main__':
    neo4j()
    chebotko()
    conceptual()
