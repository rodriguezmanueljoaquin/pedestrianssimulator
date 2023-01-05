python3 DXFParser/parser.py $1 # receives DXF path as argument
javac -sourcepath ./src/ ./src/Main.java
java -cp ./src/ Main
find src -type f -name "*.class" -delete
python3 visualization/main.py