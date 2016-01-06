all:
	if ! [ -d "bin" ]; then mkdir bin; fi
	javac -d bin/ src/*.java -cp lib/
run:
	java -cp bin/:lib/ daifugo.Daifugo 2>/dev/null
run-dev:
	java -cp bin/:lib/ daifugo.Daifugo
doc-gen:
	javadoc -private -d doc/ src/*.java
clean:
	rm -rf doc/
	rm -rf bin/
