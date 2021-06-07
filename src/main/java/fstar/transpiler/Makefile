all:
	jjtree FstarTranspiler.jjt > /dev/null && javacc FstarTranspiler.jj > /dev/null && javac *.java > /dev/null && cd ../../ && java fstar/transpiler/FstarTranspiler