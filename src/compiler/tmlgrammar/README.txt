# Edit the grammar ...
$ emacs TMLExprParser0.2.jjt

# Remove the old java / class files if necessary except SimpleNode.java
$ rm *.class *.java
$ cp save/SimpleNode.java .


# Compile it with jjtre and javacc
$ ~/bin/javacc-4.0/bin/jjtree TMLExprParser0.2.jjt
$ ~/bin/javacc-4.0/bin/javacc TMLExprParser0.2.jj

-> it creates Java files

# You may try the parser os follows:
$ javac *.java
$ java TMLExprParser



# Add header to those files
addheader header.txt '*.java'

# Put these files in the TTool sources
cp *.java ../tmlparser/

# Add them to the CVS, compile, etc.


