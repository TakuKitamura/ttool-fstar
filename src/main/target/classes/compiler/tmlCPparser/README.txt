22/01/2015

To launch the parser use the script test.sh.
This script creates a local folder called "parser", then moves into the folder and builds the parser by using javacc and javatree.
It compiles the so-generated Java classed and launches the parser with input file spec.tmlcp.
The latter contains some sample Activity and Sequence Diagrams in text form that the parser is able to parse and to fill into a
TMLCP object as a data structure.

Due to the complexity of building the parser and filling the data structure there are some limitations on the grammar the parser
is able to parse. More precisely it is not able to parse parallelism between diagrams in branches of a choice operator. Only
sequences of diagrams can be used. The above limitation can be bypassed by a reference to an activity diagram where the
parallelism is then described.
As an example, the parser raises an error when parsing the following code snippet:

ACTIVITY myActivityDiagram

	MAIN
	<>; SequenceDiagram1; SequenceDiagram2;
		[ toto == 2 ] papa; figlio;	><
		[ toto == 5] mama; prova;	><
		[ toto > 2] { {tata} * {Zama} }; myActivityDiagram; ><
	><
	END

END myActivityDiagram

Because the third branch contains the parallelism operator. This limitation can be bypassed by the following code:

ACTIVITY myActivityDiagram

	MAIN
	<>; SequenceDiagram1; SequenceDiagram2;
		[ toto == 2 ] papa; figlio;	><
		[ toto == 5] mama; prova;	><
		[ toto > 2] workAround; myActivityDiagram; ><
	><
	END

END myActivityDiagram

ACTIVITY workAround

	MAIN
	<>; { {tata} * {Zama} };
	><
	END

END workAround

The parser has been tested on spec.tmlcp. The data structure it fills from the textual code of spec.tmlcp has been used as input
to the TML text generator integrated in TTool/DIPLODOCUS. The output was the same code as the one contained in spec.tmlcp.
