# TTool Makefile
# Tested under Linux *only*

TARGET_ARCH = linux

JAVA = java
JAVAC  = javac
JAR    = jar
JAVADOC = javadoc
TAR = tar
DEBUG  = -g
CLASSPATH = -classpath
CLASSPATH = -sourcepath
PACKAGE = automata compiler/tmlparser ddtranslator launcher myutil tpndescription sddescription sdtranslator translator tmltranslator tmltranslator/toautomata tmltranslator/tosystemc  tmltranslator/tomappingsystemc tmltranslator/tomappingsystemc2 tmltranslator/touppaal tmltranslator/toturtle translator/tojava translator/tosimujava translator/totpn translator/touppaal  ui ui/ad ui/cd ui/oscd ui/osad ui/dd ui/file ui/graph ui/iod ui/procsd ui/prosmdui/prosmd/util ui/tmlad ui/tmlcd ui/tmldd ui/tmlcomp ui/req ui/sd ui/tree ui/ucd ui/window tmltranslator tmltranslator/toturtle tmltranslator/tosystemc tmatrix uppaaldesc fr/inria/oasis/vercors/cttool/model
BUILDER = builder.jar
BUILD_INFO = build.txt
BUILD_TO_MODIFY = src/ui/DefaultText.java
TTOOL_BINARY = ttool.jar
LAUNCHER_BINARY = launcher.jar
TIFTRANSLATOR_BINARY = tiftranslator.jar
TMLTRANSLATOR_BINARY = tmltranslator.jar
TTOOL_JAR_TXT = ttool.txt
LAUNCHER_JAR_TXT = launcher.txt
TIFTRANSLATOR_JAR_TXT = tiftranslator.txt
TMLTRANSLATOR_JAR_TXT = tmltranslator.txt
TTOOL_CONFIG = config.xml
TTOOL_CONFIG_SRC = configcow13.xml
JTTOOL_JAR = jttool.jar
TTOOL_LOTOS_H =  spec
TTOOL_LOTOS_H_0 = spec_0.h 
TTOOL_LOTOS_H_1 = spec_1.h 
TTOOL_LOTOS_H_2 = spec_2.h 
TTOOL_LOTOS_H_3 = spec_3.h 
TTOOL_LOTOS_H_4 = spec_4.h 
TTOOL_LOTOS_H_5 = spec_5.h 
TTOOL_LOTOS_H_6 = spec_6.h 
TTOOL_LOTOS_H_7 = spec_7.h 
TTOOL_LOTOS_H_8 = spec_8.h 
TTOOL_LOTOS_H_9 = spec_9.h 

LOGO = starting_logo.gif
STD_LOGO = std_starting_logo.gif
ENTERPRISE_LOGO =  starting_logo_enterprise.gif

#Variable that points to TTool installation Path
TTOOL_PATH := $(shell /bin/pwd)
TTOOL_SRC = $(TTOOL_PATH)/src
TTOOL_BIN = $(TTOOL_PATH)/bin
TTOOL_DOC = $(TTOOL_PATH)/doc/html
TTOOL_WORD = $(TTOOL_PATH)/doc/word
TTOOL_STD_RELEASE = $(TTOOL_PATH)/release/
JTTOOL = $(TTOOL_PATH)/javacode
JTTOOL_DIR = jttool
TTOOL_TARGET = $(TTOOL_PATH)/TTool_install

RELEASE_STD_FILES_XML = manual-HW.xml DrinkMachineV7.xml WebV01.xml Protocol_example1.xml BasicExchange.xml SmartCardProtocol.xml ProtocolPatterns.xml
RELEASE_STD_FILES_LIB =  TClock1.lib TTimerv01.lib
RELEASE_STD_FILES_BIN = $(TTOOL_CONFIG) $(LAUNCHER_BINARY) $(TTOOL_BINARY) $(TIFTRANSLATOR_BINARY) $(TMLTRANSLATOR_BINARY) 
RELEASE_STD_FILES_LICENSES = LICENSE LICENSE_CECILL_ENG LICENSE_CECILL_FR

all:
	$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
	$(JAVAC) $(CLASSPATH) $(TTOOL_SRC) $(TTOOL_SRC)/*.java

ttooljar_std:
	rm -f $(TTOOL_BIN)/$(TTOOL_BINARY)
	cp $(TTOOL_SRC)/ui/images/$(STD_LOGO) $(TTOOL_SRC)/ui/images/$(LOGO) 
	cd $(TTOOL_SRC);  $(JAR) cmf $(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(TTOOL_BINARY) Main.class automata/*.class compiler/tmlparser/*.class  tmltranslator/*.class tmltranslator/toautomata/*.class tmatrix/*.class tmltranslator/toturtle/*.class tmltranslator/touppaal/*.class tmltranslator/tosystemc/*.class tmltranslator/tomappingsystemc/*.class tmltranslator/tomappingsystemc2/*.class  tpndescription/*.class ddtranslator/*.class launcher/*.class myutil/*.class sddescription/*.class sdtranslator/*.class translator/*.class translator/tojava/*.class  translator/tosimujava/*.class translator/touppaal/*.class translator/totpn/*.class ui/*.class ui/*/*.class ui/*/*/*.class jars/*.jar uppaaldesc/*.class ui/images/*.* ui/images/toolbarButtonGraphics/general/*.gif ui/images/toolbarButtonGraphics/media/*.gif $(TTOOL_BIN)/$(LAUNCHER_BINARY) RTLLauncher.class launcher/*.class fr/inria/oasis/vercors/cttool/model/*.class
	cp $(TTOOL_SRC)/$(TTOOL_CONFIG_SRC) $(TTOOL_BIN)/$(TTOOL_CONFIG)


launcher:
	rm -f $(TTOOL_BIN)/$(LAUNCHER_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(LAUNCHER_JAR_TXT) $(TTOOL_BIN)/$(LAUNCHER_BINARY)  RTLLauncher.class launcher/*.class

tiftranslator:
	rm -f $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(TIFTRANSLATOR_JAR_TXT) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY)  TIFTranslator.class translator/*.class translator/*/*.class myutil/*.class uppaaldesc/*.class ui/CheckingError.class compiler/tmlparser/*.class

tmltranslator:
	rm -f $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(TMLTRANSLATOR_JAR_TXT) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY)  TMLTranslator.class tmltranslator/*.class tmltranslator/*/*.class myutil/*.class translator/*.class uppaaldesc/*.class ui/CheckingError.class compiler/tmlparser/*.class

documentation:
	$(JAVADOC) $(CLASSPATH) $(TTOOL_SRC) -d $(TTOOL_DOC) $(TTOOL_SRC)/*.java $(TTOOL_SRC)/*/*.java $(TTOOL_SRC)/*/*/*.java

release: jttooljar launcher tiftranslator tmltranslator ttooljar_std stdrelease 
	echo release done

stdrelease:
	rm -rf $(TTOOL_TARGET)/src/*
	cp $(TTOOL_BIN)/$(JTTOOL_JAR) $(TTOOL_TARGET)/java
	cd $(TTOOL_BIN); cp $(RELEASE_STD_FILES_XML) $(TTOOL_TARGET)/modeling
	cd $(TTOOL_BIN); cp $(RELEASE_STD_FILES_LIB) $(TTOOL_TARGET)/lib
	cd $(TTOOL_BIN); cp $(RELEASE_STD_FILES_BIN) $(TTOOL_TARGET)/bin
	cd $(TTOOL_BIN); cp $(RELEASE_STD_FILES_LICENSES) $(TTOOL_TARGET)
	cd $(TTOOL_BIN); cp README $(TTOOL_TARGET)
	cp -R $(TTOOL_BIN)/README_modeling $(TTOOL_TARGET)/modeling
	cp -R $(TTOOL_BIN)/README_lotos $(TTOOL_TARGET)/lotos
	cp -R $(TTOOL_BIN)/README_bin $(TTOOL_TARGET)/bin
	cp -R $(TTOOL_BIN)/README_lib $(TTOOL_TARGET)/lib
	cp -R $(TTOOL_BIN)/README_java $(TTOOL_TARGET)/java
	cp -R $(TTOOL_BIN)/README_figure $(TTOOL_TARGET)/figure
	cp -R $(TTOOL_BIN)/README_doc $(TTOOL_TARGET)/doc
	cp -R $(TTOOL_BIN)/$(TTOOL_LOTOS_H).h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.t  $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.f $(TTOOL_TARGET)/bin
	cd $(TTOOL_TARGET);$(TAR) cfvz $(TTOOL_STD_RELEASE)/release.tgz *
	cp -R $(TTOOL_SRC)/* $(TTOOL_TARGET)/src
	find $(TTOOL_TARGET)/src -type f -not \( -name '*.java' -o -name '*.gif'  \) -a -exec rm {} \;
	cp -R $(TTOOL_BIN)/README_src $(TTOOL_TARGET)/src
	cd $(TTOOL_TARGET);$(TAR) cfvz $(TTOOL_STD_RELEASE)/releaseWithSrc.tgz *


jttooljar:
	cd $(JTTOOL);$(JAVAC) $(JTTOOL_DIR)/*.java;$(JAR) cmf $(TTOOL_SRC)/$(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(JTTOOL_JAR) $(JTTOOL_DIR)/*.class 

clean:
	rm -f $(TTOOL_SRC)/*.dot $(TTOOL_SRC)/*.dta $(TTOOL_SRC)/*.sim $(TTOOL_SRC)/*.lot
	rm -f $(TTOOL_SRC)/*.class $(TTOOL_SRC)/*.java~
	rm -f $(TTOOL_TARGET)/java/*
	rm -f $(TTOOL_TARGET)/modeling/*
	rm -f $(TTOOL_TARGET)/bin/*
	rm -f $(TTOOL_TARGET)/lotos/*
	rm -f $(TTOOL_TARGET)/doc/*
	rm -rf $(TTOOL_TARGET)/src/*
	rm -f $(TTOOL_TARGET)/lib/*
	@@for p in $(PACKAGE); do \
		echo rm -f $$p/*.class;\
		rm -f $(TTOOL_SRC)/$$p/*.class $(TTOOL_SRC)/$$p/*.java~; \
	done

