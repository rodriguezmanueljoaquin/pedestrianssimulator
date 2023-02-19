python3 DXFParser/old_parser.py $1 # receives DXF path as argument
mvn install assembly:assembly
java -cp target/pedestrianssimulator-*-jar-with-dependencies.jar Main
mvn clean
python3 visualization/visualizator.py