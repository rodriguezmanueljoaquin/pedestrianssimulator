# pedestrianssimulator

## Autores
* [Manuel Rodriguez](https://github.com/rodriguezmanueljoaquin) (manurodriguez@itba.edu.ar)
* [Gastón Donikian](https://github.com/GastonDonikian) (gdonikian@itba.edu.ar)

## Dependencias
Java 8 JRE y JDK, o posterior
pip install ezdxf

## Requisitos DXF
El programa funciona sobre un archivo en formato DXF (versiones aceptadas: R12, R2000, R2004, R2007, R2010, R2013 o R2018) que debe cumplir con las siguientes caracteristicas:

Estará compuesto por 5 layers:

1. WALLS: archivo que contiene las paredes (que deben ser rectas, no curvas) del plano, tanto las interiores como las exteriores (el espacio debe ser cerrado, es decir, no se deben considerar las puertas en este paso).
2. EXITS: indica las salidas del establecimiento, son indicadas como líneas.
3. AGENT GENERATORS: rectángulos en los cuales van a aparecer los peatones.
4. TARGETS: serán puntos con los cuales los agentes podrán interactuar un tiempo específico, sin una cola de espera. A modo de ejemplo encontramos productos en una góndola de supermercado, o carteles en un pasillo de una universidad.
5. SERVERS: serán rectángulos en los cuales los agentes se situarán para atender a un servicio, ejemplos pueden ser aulas para clases, o cajas registradoras de supermercados, o ascensores.

Tanto los TARGETS como los SERVERS deberán ser nombrados de cierta manera, pues esto definirá como deberán ser atendidos por los peatones. Un ejemplo sería, para aquellos targets que resultan ser productos de gondolas, su nombre debería seguir el formato: *PRODUCT_X* donde X sería reemplazado por el nombre del producto, por otro lado los rectángulos asociados a los servers de ascensores seguirian una nomenclatura del estilo: *ELEVATOR_X* donde X puede ser un número o palabra identificadora.

## Ejecución del programa integro
Desde el directorio pincipal, el comando de ejecución sera:
./run.sh DXF_PATH
Donde DXF_PATH es el path al archivo DXF sobre el cual se realizara

## Ejecución del programa por partes
Desde el directorio pincipal, los comandos de ejecución son:
### DXF Parser
```python3 DXFParser/main.py -dxf=DXF_PATH```
Donde DXF_PATH es el archivo de formato DXF donde se realiza la simulación, este debe cumplir con los [requisitos mencionados](#Requisitos DXF) para el correcto funcionamiento del programa, este es un parametro opcional que en caso de no ser indicado tomara el path al archivo de ejemplo: "DXFParser/DXFExamples/Plano2.dxf".

### Simulación
javac -sourcepath ./src/ ./src/Main.java
java -cp ./src/ Main
find src -type f -name "*.class" -delete

### Visualización
```python3 visualization/parser.py```
