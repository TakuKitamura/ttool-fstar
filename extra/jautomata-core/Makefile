all: clean
	mkdir build/
	javac -d build/ rationals/*.java rationals/*/*.java
	cd build;jar -cf jautomata-core.jar rationals/*.class rationals/*/*.class

clean:
	rm -rf build/
