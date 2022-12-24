# TODO: Si recibe un parametro, usarlo para generar los parametros que usa el java
python3 DXFParser/parser.py $1
javac -sourcepath ./src/ ./src/Main.java
java -cp ./src/ Main
python3 visualization/main.py
find src -type f -name "*.class" -delete