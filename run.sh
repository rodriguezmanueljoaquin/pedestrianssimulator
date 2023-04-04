python3 DXFParser/parser.py -dxf=$1 -params=$2 # receives DXF path and parameters path as argument
mvn install assembly:assembly
java -cp target/pedestrianssimulator-*-jar-with-dependencies.jar Main
mvn clean
python3 visualization/visualizator.py