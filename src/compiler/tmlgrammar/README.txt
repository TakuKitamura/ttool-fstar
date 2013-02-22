# Writing the grammar
---------------------
# Edit the grammar ...
$ emacs TMLExprParser0.2.jjt


# Generating the parser
-----------------------
# Remove the old java / class files if necessary except SimpleNode.java
$ rm *.class *.java
$ cp save/SimpleNode.java .

# Compile it with jjtre and javacc (we assume javacc is installed in ~/bin/javacc-4.0/)
$ ~/bin/javacc-4.0/bin/jjtree TMLExprParser0.2.jjt
$ ~/bin/javacc-4.0/bin/javacc TMLExprParser0.2.jj

-> it creates Java files of the parser

# Trying the parser
-------------------
# You may try the parser as follows. From the current directory, do:
$ javac *.java
$ java TMLExprParser

You may then enter an expression, and obtain the result of its parsing.


# To integrate the generated parser to TTool
--------------------------------------------
# Add header to those files
addheader header.txt '*.java'

# Put these files in the TTool sources
cp *.java ../tmlparser/

# Add them to the CVS, compile, etc.


