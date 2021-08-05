buildTranspiler:
	jjtree FstarTranspiler.jjt  && javacc FstarTranspiler.jj  && javac *.java  && pushd ../../ && java fstar/transpiler/FstarTranspiler
buildGenerator:
	pushd fstar/generator && javac main.java && pushd ../../ && java fstar/generator/main ./AVATAR_executablecode ./out
