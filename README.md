# pedestrianssimulator

## Autores
* [Manuel Rodriguez](https://github.com/rodriguezmanueljoaquin) (manurodriguez@itba.edu.ar)
* [Gastón Donikian](https://github.com/GastonDonikian) (gdonikian@itba.edu.ar)

## Dependencias
+ Java 8 JRE y JDK, o posterior
+ pip install ezdxf
+ Maven

## Requisitos DXF
...

## Requisitos JSON
...


## Ejecución del programa integro
Desde el directorio pincipal, el comando de ejecución sera:
./run.sh DXF_PATH JSON_PATH
Donde DXF_PATH es el archivo de formato DXF donde se realiza la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos DXF) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "DXFParser/DXFExamples/Plano prueba simulacion V03.dxf". \
Por otro lado, PARAMS_PATH es el archivo JSON donde se indican los distintos parametros que modifican la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos JSON) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "input/parameters.json". \

## Ejecución del programa por partes
Desde el directorio pincipal, los comandos de ejecución son:
### DXF Parser
```python3 DXFParser/parser.py -dxf=DXF_PATH -params=PARAMS_PATH```
Donde DXF_PATH es el archivo de formato DXF donde se realiza la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos DXF) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "DXFParser/DXFExamples/Plano prueba simulacion V03.dxf". \

### Simulación
```mvn install assembly:assembly```\
```java -cp target/pedestrianssimulator-*-jar-with-dependencies.jar Main```\
```mvn clean```

### Visualización
```python3 visualization/visualizator.py```
