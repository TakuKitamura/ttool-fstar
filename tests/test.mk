CLASS=$(patsubst %.java,%.class,$(wildcard *.java))
test: $(CLASS)
	$(foreach var,$(CLASS),$(JAVA) $(CLASSPATH) "$(TTOOL_SRC):." $(patsubst %.class,%,$(var));)
%.class: %.java
	$(JAVAC) $(CLASSPATH) $(TTOOL_BIN)/$(JSOUP_BINARY):$(TTOOL_BIN)/$(COMMON_CODEC_BINARY):$(TTOOL_SRC) $<
clean:
	rm -f *.class
