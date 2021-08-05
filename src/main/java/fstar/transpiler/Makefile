buildTranspiler:
	jjtree FstarTranspiler.jjt  && javacc FstarTranspiler.jj  && javac *.java  && cd ../../ && java fstar/transpiler/FstarTranspiler
buildGenerator:
	cd fstar/generator && javac main.java && cd ../../ && java fstar/generator/main ./AVATAR_executablecode ./out
