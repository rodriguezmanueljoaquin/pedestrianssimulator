javac -sourcepath ./src/ ./src/Main.java
java -cp ./src/ Main
python3 visualization/main.py
find src -type f -name "*.class" -delete