
# TTool Makefile
# Tested under Linux *only*
# Meant to work with svn

TARGET_ARCH = linux

JAVA = java
JAVAC  = javac
JAR    = jar
JAVADOC = javadoc
TAR = tar
GZIP = gzip -9 -f
DEBUG  = -g
CLASSPATH = -classpath
CLASSPATH = -sourcepath
PACKAGE = avatartranslator avatartranslator/touppaal avatartranslator/toturtle automata compiler/tmlparser vcd nc ddtranslator launcher myutil tpndescription sddescription sdtranslator translator tmltranslator tmltranslator/toautomata tmltranslator/tosystemc  tmltranslator/tomappingsystemc tmltranslator/tomappingsystemc2 tmltranslator/touppaal tmltranslator/toturtle translator/tojava translator/tosimujava translator/totpn translator/touppaal  ui ui/avatarbd ui/avatarsmd ui/avatarrd ui/ad ui/cd ui/oscd ui/osad ui/dd ui/ebrdd ui/file ui/graph ui/iod ui/ncdd ui/procsd ui/prosmdui/prosmd/util ui/tmlad ui/tmlcd ui/tmldd ui/tmlcomp ui/req ui/sd ui/tree ui/ucd ui/window tmltranslator tmltranslator/toturtle req/ebrdd tmltranslator/tosystemc tmatrix uppaaldesc fr/inria/oasis/vercors/cttool/model remotesimulation
BUILDER = builder.jar
BUILD_INFO = build.txt
BUILD_TO_MODIFY = src/ui/DefaultText.java
TTOOL_BINARY = ttool.jar
LAUNCHER_BINARY = launcher.jar
TIFTRANSLATOR_BINARY = tiftranslator.jar
TMLTRANSLATOR_BINARY = tmltranslator.jar
REMOTESIMULATOR_BINARY = simulationcontrol.jar
TTOOL_JAR_TXT = ttool.txt
LAUNCHER_JAR_TXT = launcher.txt
TIFTRANSLATOR_JAR_TXT = tiftranslator.txt
TMLTRANSLATOR_JAR_TXT = tmltranslator.txt
REMOTESIMULATOR_JAR_TXT = simulationcontrol.txt
TTOOL_CONFIG = config.xml
TTOOL_CONFIG_SRC = config.xml
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
TTOOL_MODELING = $(TTOOL_PATH)/modeling
TTOOL_SIMULATORS = $(TTOOL_PATH)/simulators
TTOOL_DOC = $(TTOOL_PATH)/doc
TTOOL_DOC_HTML = $(TTOOL_PATH)/doc/html
TTOOL_VCD = $(TTOOL_PATH)/vcd
TTOOL_WORD = $(TTOOL_PATH)/doc/word
TTOOL_STD_RELEASE = $(TTOOL_PATH)/release/
JTTOOL = $(TTOOL_PATH)/javacode
JTTOOL_DIR = jttool
TTOOL_TARGET = $(TTOOL_PATH)/TTool_install/TTool
TTOOL_TARGET_RELEASE = $(TTOOL_PATH)/TTool_install

RELEASE_STD_FILES_XML = manual-HW.xml DrinkMachineV10.xml WebV01.xml Protocol_example1.xml BasicExchange.xml SmartCardProtocol.xml ProtocolPatterns.xml COCOME_V50.xml
RELEASE_STD_FILES_LIB =  TClock1.lib TTimerv01.lib
RELEASE_STD_FILES_BIN = $(TTOOL_CONFIG) $(LAUNCHER_BINARY) $(TTOOL_BINARY) $(TIFTRANSLATOR_BINARY) $(TMLTRANSLATOR_BINARY) $(REMOTESIMULATOR_BINARY) 
RELEASE_STD_FILES_LICENSES = LICENSE LICENSE_CECILL_ENG LICENSE_CECILL_FR

all:
	date
	svn update build.txt src/ui/DefaultText.java
	$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
	svn commit build.txt src/ui/DefaultText.java -m 'update on build version: builder.txt'
	$(JAVAC) $(CLASSPATH) $(TTOOL_SRC) $(TTOOL_SRC)/*.java

basic:
	$(JAVAC) $(CLASSPATH) $(TTOOL_SRC) $(TTOOL_SRC)/*.java	

ttooljar_std:
	rm -f $(TTOOL_BIN)/$(TTOOL_BINARY)
	cp $(TTOOL_SRC)/ui/images/$(STD_LOGO) $(TTOOL_SRC)/ui/images/$(LOGO) 
	cd $(TTOOL_SRC);  $(JAR) cmf $(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(TTOOL_BINARY) Main.class vcd/*.class avatartranslator/*.class avatartranslator/toturtle/*.java automata/*.class compiler/tmlparser/*.class nc/*.class  tmltranslator/*.class tmltranslator/toautomata/*.class tmatrix/*.class tmltranslator/toturtle/*.class tmltranslator/touppaal/*.class tmltranslator/tosystemc/*.class tmltranslator/tomappingsystemc/*.class tmltranslator/tomappingsystemc2/*.class  tpndescription/*.class ddtranslator/*.class launcher/*.class myutil/*.class sddescription/*.class sdtranslator/*.class translator/*.class translator/tojava/*.class  translator/tosimujava/*.class translator/touppaal/*.class translator/totpn/*.class req/ebrdd/*.java ui/*.class ui/*/*.class ui/*/*/*.class uppaaldesc/*.class ui/images/*.* ui/images/toolbarButtonGraphics/general/*.gif ui/images/toolbarButtonGraphics/media/*.gif $(TTOOL_BIN)/$(LAUNCHER_BINARY) RTLLauncher.class launcher/*.class fr/inria/oasis/vercors/cttool/model/*.class remotesimulation/*.class


launcher:
	rm -f $(TTOOL_BIN)/$(LAUNCHER_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(LAUNCHER_JAR_TXT) $(TTOOL_BIN)/$(LAUNCHER_BINARY)  RTLLauncher.class launcher/*.class

tiftranslator:
	rm -f $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(TIFTRANSLATOR_JAR_TXT) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY)  TIFTranslator.class translator/*.class translator/*/*.class myutil/*.class uppaaldesc/*.class ui/CheckingError.class compiler/tmlparser/*.class

tmltranslator:
	rm -f $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(TMLTRANSLATOR_JAR_TXT) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY)  TMLTranslator.class tmltranslator/*.class tmltranslator/*/*.class myutil/*.class translator/*.class uppaaldesc/*.class ui/CheckingError.class compiler/tmlparser/*.class

remotesimulator:
	rm -f $(TTOOL_BIN)/$(REMOTESIMULATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(REMOTESIMULATOR_JAR_TXT) $(TTOOL_BIN)/$(REMOTESIMULATOR_BINARY)  RemoteSimulationControl.class remotesimulation/*.class

documentation:
	$(JAVADOC) $(CLASSPATH) $(TTOOL_SRC) -d $(TTOOL_DOC_HTML) $(TTOOL_SRC)/*.java $(TTOOL_SRC)/*/*.java $(TTOOL_SRC)/*/*/*.java $(TTOOL_SRC)/fr/inria/oasis/vercors/cttool/model/*.java

release: jttooljar launcher tiftranslator tmltranslator remotesimulator ttooljar_std stdrelease 
	@echo release done

########## RELEASE
stdrelease:
	mkdir -p $(TTOOL_TARGET)
	rm -rf $(TTOOL_TARGET)/*
# java
	mkdir -p $(TTOOL_TARGET)/java
	cp $(TTOOL_BIN)/$(JTTOOL_JAR) $(TTOOL_TARGET)/java
	cp $(TTOOL_DOC)/README_java $(TTOOL_TARGET)/java
# modeling
	mkdir -p $(TTOOL_TARGET)/modeling
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_XML) $(TTOOL_TARGET)/modeling
	cp $(TTOOL_DOC)/README_modeling $(TTOOL_TARGET)/modeling
# lib
	mkdir -p $(TTOOL_TARGET)/lib
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_LIB) $(TTOOL_TARGET)/lib
	cp $(TTOOL_DOC)/README_lib $(TTOOL_TARGET)/lib
# simulators
	mkdir -p $(TTOOL_TARGET)/simulators/systemc1/src_simulator
	mkdir -p $(TTOOL_TARGET)/simulators/systemc1/lib
	cp  $(TTOOL_SIMULATORS)/systemc1/lib/README $(TTOOL_TARGET)/simulators/systemc1/lib/
	cp  $(TTOOL_SIMULATORS)/systemc1/Makefile $(TTOOL_TARGET)/simulators/systemc1
	cp  $(TTOOL_SIMULATORS)/systemc1/Makefile.defs $(TTOOL_TARGET)/simulators/systemc1
	cp  $(TTOOL_SIMULATORS)/systemc1/src_simulator/*.cpp $(TTOOL_TARGET)/simulators/systemc1/src_simulator
	cp  $(TTOOL_SIMULATORS)/systemc1/src_simulator/*.h $(TTOOL_TARGET)/simulators/systemc1/src_simulator
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/lib
	cp  $(TTOOL_SIMULATORS)/c++2/lib/README $(TTOOL_TARGET)/simulators/c++2/lib/
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile $(TTOOL_TARGET)/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile.defs $(TTOOL_TARGET)/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/schedstyle.css $(TTOOL_TARGET)/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
# Licenses
	cd $(TTOOL_DOC); cp $(RELEASE_STD_FILES_LICENSES) $(TTOOL_TARGET)
# Main readme
	cp $(TTOOL_DOC)/README $(TTOOL_TARGET)
# LOTOS
	mkdir -p $(TTOOL_TARGET)/lotos
	cp $(TTOOL_DOC)/README_lotos $(TTOOL_TARGET)/lotos
#NC
	mkdir -p $(TTOOL_TARGET)/nc
	cp $(TTOOL_DOC)/README_nc $(TTOOL_TARGET)/nc
#TML
	mkdir -p $(TTOOL_TARGET)/tmlcode
	cp $(TTOOL_DOC)/README_tml $(TTOOL_TARGET)/tmlcode
#UPPAAL
	mkdir -p $(TTOOL_TARGET)/uppaal
	cp $(TTOOL_DOC)/README_uppaal $(TTOOL_TARGET)/uppaal
# Figure
	mkdir -p $(TTOOL_TARGET)/figure
	cp $(TTOOL_DOC)/README_figure $(TTOOL_TARGET)/figure
# VCD
	mkdir -p $(TTOOL_TARGET)/vcd
	cp $(TTOOL_DOC)/README_vcd $(TTOOL_TARGET)/vcd
# Basic doc
	mkdir -p $(TTOOL_TARGET)/doc
	cp $(TTOOL_DOC)/README_doc $(TTOOL_TARGET)/doc
# Basic bin
	mkdir -p $(TTOOL_TARGET)/bin
	cp $(TTOOL_DOC)/README_bin $(TTOOL_TARGET)/bin
	cp $(TTOOL_BIN)/configuration.gcf $(TTOOL_TARGET)/bin
	cp -R $(TTOOL_BIN)/$(TTOOL_LOTOS_H).h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.t  $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.f $(TTOOL_TARGET)/bin
	cp $(TTOOL_BIN)/$(TTOOL_BINARY) $(TTOOL_BIN)/$(LAUNCHER_BINARY) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY) $(TTOOL_BIN)/$(TTOOL_CONFIG_SRC) $(TTOOL_TARGET)/bin
# Basic release
	cd $(TTOOL_TARGET_RELEASE);$(TAR) cfv $(TTOOL_STD_RELEASE)/release.tar *; $(GZIP) -9 $(TTOOL_STD_RELEASE)/release.tar; mv $(TTOOL_STD_RELEASE)/release.tar.gz $(TTOOL_STD_RELEASE)/release.tgz
# Advanced release
	$(JAVADOC) $(CLASSPATH) $(TTOOL_SRC) -d $(TTOOL_TARGET)/doc/srcdoc $(TTOOL_SRC)/*.java $(TTOOL_SRC)/*/*.java $(TTOOL_SRC)/*/*/*.java $(TTOOL_SRC)/fr/inria/oasis/vercors/cttool/model/*.java
	mkdir -p $(TTOOL_TARGET)/src
	cp -R $(TTOOL_SRC)/* $(TTOOL_TARGET)/src
	find $(TTOOL_TARGET)/src -type f -not \( -name '*.java' -o -name '*.gif' -o -name '*.jjt' -o -name '*.txt' \) -a -exec rm -f {} \;
	cp -R $(TTOOL_DOC)/README_src $(TTOOL_TARGET)/src
	cd $(TTOOL_TARGET_RELEASE);$(TAR) cfv $(TTOOL_STD_RELEASE)/releaseWithSrc.tar *; $(GZIP) $(TTOOL_STD_RELEASE)/releaseWithSrc.tar; mv $(TTOOL_STD_RELEASE)/releaseWithSrc.tar.gz $(TTOOL_STD_RELEASE)/releaseWithSrc.tgz


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

