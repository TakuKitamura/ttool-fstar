all:
	jjtree SimpleCalculatorGrammar.jjt > /dev/null && javacc SimpleCalculatorGrammar.jj > /dev/null && javac *.java > /dev/null && cd .. && java transpiler/SimpleCalculatorParser