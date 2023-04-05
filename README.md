# pedestrianssimulator

# Autores
* [Manuel Rodriguez](https://github.com/rodriguezmanueljoaquin) (manurodriguez@itba.edu.ar)
* [Gastón Donikian](https://github.com/GastonDonikian) (gdonikian@itba.edu.ar)

# Dependencias
+ Java 8 JRE y JDK, o posterior
+ Python 3
+ ezdxf (python package)
+ Maven

# Requisitos DXF
Existen 6 posibles funcionalidades para las figuras que permiten indicar cómo será la simulación resultante. La funcionalidad que cumplan las figuras será determinada a partir del nombre del LAYER en el que se encuentren, las distintas posibilidades son:

1. **WALLS**
    
    Todas las formas pertenecientes al LAYER de nombre “***WALLS”*** son las paredes u obstáculos del plano que representan un obstáculo para los peatones. Las mismas deben ser líneas sólidas rectas, no curvas, del tipo LINE o POLYLINE.
    
2. **EXITS**
    
    Dentro del LAYER ***“EXITS”*** se encontraran bloques, que pueden ser repetidos o no y solo deberán contener una línea, que indicarán las salidas del establecimiento sobre la línea de la puerta y de ancho exactamente igual al espacio de la misma que es determinada por las paredes adyacentes, las figuras deben ser de tipo LINE.
    
    El nombre de los bloques permitirá luego definir que peatones utilizaran dichas salidas.
    
3. **GENERATORS**
    
    Dentro del LAYER “***GENERATORS***”  se encontraran bloques, que pueden ser repetidos o no y solo deberán contener un rectángulo, que determinan las áreas donde van a aparecer los peatones. Deben ser rectángulos (de tipo POLYLINE, formados con la función de rectángulos cerrados). 
    
    El nombre de los bloques permitirá luego definir para los distintos grupos distintos parámetros, como pueden ser la cantidad de agentes a generar y su frecuencia.
    
4. **TARGETS**
    
    Dentro del LAYER “***TARGETS***” se encontraran bloques, que pueden ser repetidos o no y solo deberán contener una figura, que determinan entidades con los cuales los agentes podrán interactuar un tiempo específico, sin una cola de espera. La figura puede ser círculo (CIRCLE) o rectángulo (POLYLINE, formados con la función de rectángulos cerrados).  El área de dicha forma determinará el rango en el cual un agente puede realizar la interacción con el objetivo en cuestión. 
    
    El nombre de los bloques permitirá luego definir para los distintos distintos tiempos de atención que requerirá cada objetivo
    
    A modo de ejemplo encontramos productos en una góndola de supermercado (que serían repeticiones de un bloque de nombre *PRODUCT*), o carteles (que serían repeticiones de un bloque de nombre *LETTER*) en un pasillo de una universidad.
    
5. **SERVERS**
    
    Dentro del LAYER “***SERVERS***” se encontraran bloques, que pueden ser repetidos o no, que determinan áreas en las cuales los agentes se situarán para atender a un servicio.
    
    Estos bloques deben tener un rectángulo (de tipo POLYLINE, formados con la función de rectángulos cerrados) definiendo el área mencionada, y podrá tener, o no, un conjunto de líneas unidas (de tipo POLYLINE o LINE) que defina la cola de espera para dicho servicio, en caso de existir. 
    
    Ejemplos son aulas para clases estudiantiles (podrían en layers del estilo ****SERVERS_CLASS1****, ****SERVER_CLASS-MATH) y**** cajas registradoras de supermercados (******SERVERS_CASHIER1******).

# Requisitos JSON

Se especificarán a través de un archivo JSON los siguientes parámetros:

```json
{
    "max_time": INTEGER,
    "evacuate_at": INTEGER,

    "agents_generators": [ ... ],

    "targets": [ ... ],

    "servers": [ ... ]
}
```

Primero, encontramos los valores generales de la simulación. Entre ellos ellos están:

- *max_time*: Duración de la simulación.
- *evacuate_at*: Segundo en el cual se inicia la evacuación de todos los agentes del establecimiento. Este parámetro es opcional.

Luego encontramos 3 secciones relacionadas con las capas encontradas en el DXF que indica el plano a recrear. 

Como muchas de las variables que se encuentran en estas secciones funcionan de forma aleatoria, se les debe definir una distribución. Esta definición de la distribución se realiza de la misma forma para cada una de las variables, por lo que a continuación la describiremos para que luego pueda ser referenciada.

DISTRIBUTION_OBJECT

```json
{
    "type": "STRING",
    "min": DOUBLE,
    "max": DOUBLE,
    "std": DOUBLE,
    "mean": DOUBLE
}
```

Es un objeto JSON en el cual se indicarán características de la generación de los números aleatorios, las cuales son:

- **type**: Distribución aleatoria que sigue la generación. Dependiendo el tipo de distribución se deberan determinar algunos de los otros parámetros. Las posibilidades son:
    - `“UNIFORM”` Se debera indicar los valores de
        - ***min***
        - ***max***
    - `“GAUSSIAN”` Se debera indicar los valores de
        - *std*
        - *mean*
    - `“EXPONENTIAL"` Se debera indicar el valor de
        - *mean*

En cada sección se dará, a partir de su identificador, distintas propiedades que permitirán modificar la ejecución de la simulación. A continuación analizaremos cada una en detalle.

## agents_generators

```json
{
	...,

	"agents_generators": [
        {
            "group_name": "STRING",
            "behaviour_scheme": "STRING",
            "agents": {
                "min_radius_distribution": DISTRIBUTION_OBJECT,
                "max_radius_distribution": DISTRIBUTION_OBJECT,
                "max_velocity": DOUBLE
            },
            "active_time": DOUBLE,
            "inactive_time": DOUBLE,
            "generation": {
                "frequency": DOUBLE,
                "quantity_distribution": DISTRIBUTION_OBJECT
            }
        }, 
        ...
    ],

	...
}
```

A cada grupo de generadores, los cuales son identificados por la variable *group_name*, se les debe asignar:

- *behaviour_scheme* : Indica el comportamiento general de los agentes, como transicionan entre los estados de caminar, acercarse a un objetivo, etc.. Puede tomar los valores: `"MARKET-CLIENT"`, `"SREC-STUDENT"`.
- *agents*: Es un objeto JSON en el cual se indicarán características físicas de los agentes generados, las cuales son:
    - *min_radius_mean*: Radio minimo promedio, con el se define el radio que los agentes generados tendrán al momento de colisionar.
    - *max_radius_mean*: Radio promedio máximo que se utiliza para definir el radio que los agentes generados tendrán. Sigue el formato del DISTRIBUTION_OBJECT explicado previamente.
    - *radius_std*: Desviación estandar aplicada sobre el radio minimo y maximo al momento de definir los radios de un agente. Sigue el formato del DISTRIBUTION_OBJECT explicado previamente.
    - *max_velocity*: Velocidad máxima que los agentes generados tendrán.
- *active_time*: Tiempo durante el generarán agentes.
- *inactive_time*: Tiempo durante el cual no generarán agentes, luego de un periodo de actividad.
- *generation*: Es un objeto JSON en el cual se indicarán las características sobre la generación:
    - *frequency*: frecuencia de generación dentro del active time.
    - *quantity_distribution*: distribución aleatoria que sigue la selección de la cantidad de agentes a generar en cada ejecución. Sigue el formato del DISTRIBUTION_OBJECT explicado previamente.

En caso de que el área del generador no sea lo suficientemente grande para generar el *max_agents* indicado, este se reducirá hasta el máximo que permita el área.

## targets

```json
{
    ...,

    "targets": [
        {
            "group_name": "STRING",
            "attending_time_distribution": DISTRIBUTION_OBJECT
        },
        ...
    ],

    ...
}
```

A cada grupo de targets, los cuales son identificados por la variable *group_name*, se les debe asignar:

- *attending_time_distribution*: Distribución aleatoria que sigue el tiempo de atención que dedica el agente. Sigue el formato del DISTRIBUTION_OBJECT explicado previamente.

## servers

```json
{
    ...,

    "servers": [
        {
            "group_name": "STRING",
            "attending_time_distribution": DISTRIBUTION_OBJECT,
            "max_capacity": INTEGER,
            "start_time": DOUBLE
        },
        ...
    ]
}
```

A cada servidor, el cual es identificado por la variable *group_name*, se le debe asignar:

- *attending_time_distribution*: Distribución aleatoria que sigue el tiempo de atención que dedica el agente. Sigue el formato del DISTRIBUTION_OBJECT explicado previamente.
- **max_capacity**: Máxima cantidad de agentes que es capaz de atender en simultáneo.
- start_time: Tiempo que tardará el servidor en arrancar su servicio.

Notamos que en caso de que el servidor:

- Tenga fila de espera (servidor dinámico): El servidor a partir de *start_time* atenderá agentes indefinidamente.
- No tenga fila de espera (servidor estático): El servidor a partir de *start_time* atenderá agentes hasta *start_time + attending_time*, y luego no atenderá a nadie más. Un ejemplo sería una clase de matemáticas que arranca a las 14horas y tiene una duración de 1 hora.

# Ejecución del programa integro
Desde el directorio pincipal, el comando de ejecución sera:

```bash
./run.sh DXF_PATH JSON_PATH
``` 


Donde DXF_PATH es el archivo de formato DXF donde se realiza la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos-DXF) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "DXFParser/DXFExamples/Plano prueba simulacion V03.dxf". 

Por otro lado, PARAMS_PATH es el archivo JSON donde se indican los distintos parametros que modifican la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos-JSON) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "data/parameters.json". 

Existen ejecutadores para simulaciones preestablecidas, estos son:
- `./run_test.sh` : Ejecuta la simulación sobre un plano reducido de 3 ambientes.
- `./run_srec.sh` : Ejecuta la simulación sobre el plano de la [sede de rectorado del ITBA](https://goo.gl/maps/hNh8fU9UCq97gFEZA).


# Ejecución del programa por partes
Desde el directorio pincipal, los comandos de ejecución son:
## DXF Parser
```bash
python3 DXFParser/parser.py -dxf=DXF_PATH -params=PARAMS_PATH
```
Donde DXF_PATH es el archivo de formato DXF donde se realiza la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos DXF) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "DXFParser/DXFExamples/Plano prueba simulacion V03.dxf". \

## Simulación
```bash
mvn install assembly:assembly
java -cp target/pedestrianssimulator-*-jar-with-dependencies.jar Main
mvn clean
```

## Visualización
```bash
python3 visualization/visualizator.py
```
