
# TTool Makefile
# Tested under Linux *only*
# Meant to work with git

export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

TARGET_ARCH = linux

JAVA = java
JAVAC  = javac
JAVACC = /usr/bin/javacc.sh
JAR    = jar
JAVADOC = javadoc
TAR = tar
DEBUG  = -g
CLASSPATH = -classpath
SOURCEPATH = -sourcepath
#PACKAGE = avatartranslator avatartranslator/toexecutable avatartranslator/directsimulation avatartranslator/tocppsim avatartranslator/touppaal avatartranslator/toturtle avatartranslator/toproverif avatartranslator/totpn automata compiler/tmlparser vcd nc ddtranslator launcher myutil tpndescription sddescription sdtranslator tepe translator tmltranslator tmltranslator/tmlcp tmltranslator/toautomata tmltranslator/tosystemc  tmltranslator/tomappingsystemc tmltranslator/tomappingsystemc2 tmltranslator/touppaal tmltranslator/toturtle translator/tojava translator/tosimujava translator/totpn translator/touppaal  ui ui/avatarbd ui/avatardd ui/avatarsmd ui/avatarrd ui/avatarpd ui/avatarcd ui/avatarad ui/ad ui/cd ui/oscd ui/osad ui/dd ui/ebrdd ui/file ui/graph ui/iod ui/ncdd ui/procsd ui/prosmdui/prosmd/util ui/tmlad ui/tmlcd ui/tmldd ui/tmlcomp ui/req ui/sd ui/tree ui/ucd ui/window ui/avatarmethodology ui/sysmlsecmethodology tmltranslator tmltranslator/toturtle req/ebrdd tmltranslator/tosystemc tmatrix proverifspec uppaaldesc fr/inria/oasis/vercors/cttool/model remotesimulation tmltranslator/modelcompiler attacktrees myutil/externalSearch
BUILDER = builder.jar
BUILD_INFO = build.txt
BUILD_TO_MODIFY = src/ui/DefaultText.java
TTOOL_BINARY = ttool.jar
LAUNCHER_BINARY = launcher.jar
GRAPHSHOW_BINARY = graphshow.jar
GRAPHMINIMIZE_BINARY = graphminimize.jar
TIFTRANSLATOR_BINARY = tiftranslator.jar
TMLTRANSLATOR_BINARY = tmltranslator.jar
GSCORE_BINARY = gs-core-1.3.jar
GSUI_BINARY = gs-ui-1.3.jar
JSOUP_BINARY = jsoup-1.8.1.jar
COMMON_CODEC_BINARY = commons-codec-1.10.jar
RUNDSE_BINARY = rundse.jar
REMOTESIMULATOR_BINARY = simulationcontrol.jar
RUNDSE_BINARY = rundse.jar
RUNDSE_JAR_TXT  = rundse.txt
TTOOL_JAR_TXT = ttool.txt
LAUNCHER_JAR_TXT = launcher.txt
GRAPHSHOW_JAR_TXT = graphshow.txt
GRAPHMINIMIZE_JAR_TXT = graphminimize.txt
TIFTRANSLATOR_JAR_TXT = tiftranslator.txt
TMLTRANSLATOR_JAR_TXT = tmltranslator.txt
WEBCRAWLER_SERVER_JAR_TXT = webcrawler.txt
WEBCRAWLER_CLIENT_JAR_TXT = client.txt
WEBCRAWLER_SERVER_BINARY = webcrawler-server.jar
WEBCRAWLER_CLIENT_BINARY = webcrawler-client.jar
RUNDSE_JAR_TXT = rundse.txt
REMOTESIMULATOR_JAR_TXT = simulationcontrol.txt
TTOOL_CONFIG = config.xml
#TTOOL_CONFIG_SRC = config.xml
#TTOOL_CONFIG_SRC = $(TTOOL_DOC)/config_linux.xml  $(TTOOL_DOC)/config_macosx.xml  $(TTOOL_DOC)/config_windows.xml
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
TTOOL_WEBCRAWLER_SRC = $(TTOOL_PATH)/src/web/crawler
TTOOL_BIN = $(TTOOL_PATH)/bin
TTOOL_CLASSPATH_BINARY = $(TTOOL_BIN)/JavaPlot.jar:$(TTOOL_BIN)/commons-codec-1.10.jar:$(TTOOL_BIN)/commons-io-2.4-javadoc.jar:$(TTOOL_BIN)/commons-io-2.4.jar:$(TTOOL_BIN)/derby.jar:$(TTOOL_BIN)/derbyclient-10.9.1.0.jar:$(TTOOL_BIN)/derbynet.jar:$(TTOOL_BIN)/dom4j-1.6.1.jar:$(TTOOL_BIN)/jaxen-1.1.6.jar:$(TTOOL_BIN)/jsoup-1.8.1.jar:$(TTOOL_BIN)/opencloud.jar:$(TTOOL_BIN)/gs-core-1.3.jar:.
TTOOL_MODELING = $(TTOOL_PATH)/modeling
#TTOOL_MODELING = $(TTOOL_PATH)/figures
TTOOL_EXECUTABLECODE = $(TTOOL_PATH)/executablecode
TTOOL_MPSOC = $(TTOOL_PATH)/MPSoC
TTOOL_SIMULATORS = $(TTOOL_PATH)/simulators
TTOOL_FIGURES = $(TTOOL_PATH)/figures
TTOOL_DOC = $(TTOOL_PATH)/doc
TTOOL_DOC_SOCLIB = $(TTOOL_PATH)/doc/document_soclib
TTOOL_DOC_HTML = $(TTOOL_PATH)/doc/html
TTOOL_VCD = $(TTOOL_PATH)/vcd
TTOOL_WORD = $(TTOOL_PATH)/doc/word
TTOOL_STD_RELEASE = $(TTOOL_PATH)/release/
JTTOOL = $(TTOOL_PATH)/javacode
JTTOOL_DIR = jttool
TTOOL_TARGET = $(TTOOL_PATH)/TTool_install/TTool
TTOOL_TARGET_RELEASE = $(TTOOL_PATH)/TTool_install
TTOOL_PRIVATE = $(TTOOL_PATH)/../TTool-Private
TTOOL_PREINSTALL = $(TTOOL_PRIVATE)/preinstallTTool
TTOOL_WINDOWS = TTool_Windows
TTOOL_LINUX = TTool_Linux
TTOOL_MACOS = TTool_MacOS
TTOOL_PREINSTALL_LINUX = $(TTOOL_PREINSTALL)/$(TTOOL_LINUX)
TTOOL_PREINSTALL_WINDOWS = $(TTOOL_PREINSTALL)/$(TTOOL_WINDOWS)
TTOOL_PREINSTALL_MACOS = $(TTOOL_PREINSTALL)/$(TTOOL_MACOS)
PACKAGE = $(shell cd $(TTOOL_SRC); find . -type d)

TTOOL_CONFIG_SRC = $(TTOOL_DOC)/config_linux.xml  $(TTOOL_DOC)/config_macosx.xml  $(TTOOL_DOC)/config_windows.xml
TTOOL_EXE = $(TTOOL_DOC)/ttool_linux.exe  $(TTOOL_DOC)/ttool_macosx.exe  $(TTOOL_DOC)/ttool_windows.bat

RELEASE_STD_FILES_LINUX_EXE = ttool_unix
RELEASE_STD_FILES_WINDIWS_EXE = ttool_windows.bat

RELEASE_STD_FILES_XML = TURTLE/manual-HW.xml TURTLE/WebV01.xml TURTLE/Protocol_example1.xml TURTLE/BasicExchange.xml DIPLODOCUS/SmartCardProtocol.xml TURTLE/ProtocolPatterns.xml CTTool/COCOME_V50.xml AVATAR/CoffeeMachine_Avatar.xml AVATAR/Network_Avatar.xml AVATAR/MicroWaveOven_SafetySecurity_fullMethodo.xml
RELEASE_STD_FILES_LIB =  TURTLE/TClock1.lib TURTLE/TTimerv01.lib
RELEASE_STD_FILES_BIN = $(LAUNCHER_BINARY) $(TTOOL_BINARY) $(TIFTRANSLATOR_BINARY) $(TMLTRANSLATOR_BINARY) $(REMOTESIMULATOR_BINARY) $(RUNDSE_BINARY) $(WEBCRAWLER_SERVER_BINARY) $(WEBCRAWLER_CLIENT_BINARY) $(GRAPHSHOW_BINARY) $(GRAPHMINIMIZE_BINARY)
RELEASE_STD_FILES_LICENSES = LICENSE LICENSE_CECILL_ENG LICENSE_CECILL_FR

TEST_DIR        = $(TTOOL_PATH)/tests
TEST_MK         = test.mk
TEST_DIRS       = $(shell find $(TEST_DIR)/* -type d)
TEST_MAKEFILES  = $(patsubst %,%/$(TEST_MK),$(TEST_DIRS))

define functionEcho
    echo $1
endef

all:
    $(call generate_file,John Doe,101)


define HELP_message
How to compile TTool:
---------------------
make all                        builds TTool and produces the jar files in bin/
make ttool			builds TTool (but do not produce the jar of companion software)

Usual targets:
---------------
make (help)                     prints this help
make git                        produces the .class files and commit a new build version in the git
make basic                      generates the .class files
make documentation              generates the documentation of java classes using javadoc
make release                    to prepare a new release for the website. It produces the release.tgz files in releases/
make test                       tests on TTool. Currently, tests on AVATAR to ProVerif generation
make clean                      removes the .class .dot .dta .sim .lot .~ and clears the release and test directories
make publish_jar                places ttool.jar in perso.telecom-paristech.fr/docs/ttool.jar. Must have the right ssh key installed for this
make ultraclean                 runs clean and then removes the jar files in bin/

Other targets:
--------------
make jar                        generates the .jar files in bin/
make publish_jar                places ttool.jar in perso.telecom-paristech.fr/docs/ttool.jar. Must have the right ssh key installed for this
make preinstall			generates a preinstall version of TTool for Linux


Please report bugs or suggestions of improvements to:
  Ludovic Apvrille <ludovic.apvrille@telecom-paristech.fr>
endef
export HELP_message


# Targets
help:
	@echo "$$HELP_message"


all: basic jar

ttool: basic jarttool

git: gitpull jar

gitpull:
	date
	git pull
	$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
	git commit -m 'update on build version: builder.txt' build.txt src/ui/DefaultText.java
	git push


myrelease: basic launcher ttooljar 

basic:
	$(JAVAC) $(SOURCEPATH) $(TTOOL_SRC) $(CLASSPATH) $(TTOOL_CLASSPATH_BINARY) $(TTOOL_SRC)/*.java $(TTOOL_WEBCRAWLER_SRC)/*.java

jarttool:  launcher ttooljar

jar: launcher ttooljar tiftranslator tmltranslator rundse remotesimulator webcrawler graphshow graphminimize

ttooljar:
	rm -f $(TTOOL_BIN)/$(TTOOL_BINARY)
	cp $(TTOOL_SRC)/ui/images/$(STD_LOGO) $(TTOOL_SRC)/ui/images/$(LOGO) 
	cd $(TTOOL_SRC);  $(JAR) cmf $(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(TTOOL_BINARY) Main.class vcd/*.class avatartranslator/*.class avatartranslator/toexecutable/*.class avatartranslator/directsimulation/*.class avatartranslator/modelchecker/*.class  avatartranslator/touppaal/*.class avatartranslator/toproverif/*.class avatartranslator/totpn/* avatartranslator/*.class avatartranslator/toturtle/*.java automata/*.class compiler/tmlparser/*.class nc/*.class  tepe/*.class tmltranslator/*.class tmltranslator/toavatar/*.class tmltranslator/tmlcp/*.class tmltranslator/toautomata/*.class tmatrix/*.class tmltranslator/toturtle/*.class tmltranslator/touppaal/*.class tmltranslator/tosystemc/*.class tmltranslator/tomappingsystemc/*.class tmltranslator/tomappingsystemc2/*.class  tpndescription/*.class ddtranslator/*.class launcher/*.class myutil/*.class sddescription/*.class sdtranslator/*.class translator/*.class translator/tojava/*.class  translator/tosimujava/*.class translator/touppaal/*.class translator/totpn/*.class req/ebrdd/*.java ui/*.class ui/*/*.class ui/*/*/*.class proverifspec/*.class uppaaldesc/*.class ui/images/*.* ui/images/toolbarButtonGraphics/general/*.gif ui/images/toolbarButtonGraphics/navigation/*.gif  ui/images/toolbarButtonGraphics/media/*.gif $(TTOOL_BIN)/$(LAUNCHER_BINARY) RTLLauncher.class launcher/*.class fr/inria/oasis/vercors/cttool/model/*.class remotesimulation/*.class tmltranslator/modelcompiler/*.class attacktrees/*.class myutil/externalSearch/*.class ddtranslatorSoclib/*.class ddtranslatorSoclib/toSoclib/*.class ddtranslatorSoclib/toTopCell/*.class dseengine/*.class #compiler/tmlCPparser/parser/*.class

launcher:
	rm -f $(TTOOL_BIN)/$(LAUNCHER_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(LAUNCHER_JAR_TXT) $(TTOOL_BIN)/$(LAUNCHER_BINARY)  RTLLauncher.class launcher/*.class myutil/*.class

graphminimize:
	rm -f $(TTOOL_BIN)/$(GRAPHMINIMIZE_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(GRAPHMINIMIZE_JAR_TXT) $(TTOOL_BIN)/$(GRAPHMINIMIZE_BINARY)  GraphMinimize.class myutil/*.class ui/graph/*.class

graphshow:
	rm -f $(TTOOL_BIN)/$(GRAPHSHOW_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(GRAPHSHOW_JAR_TXT) $(TTOOL_BIN)/$(GRAPHSHOW_BINARY)  GraphShow.class myutil/*.class ui/graph/*.class ui/IconManager.class ui/file/PNGFilter.class

tiftranslator:
	rm -f $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(TIFTRANSLATOR_JAR_TXT) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY)  TIFTranslator.class translator/*.class translator/*/*.class myutil/*.class uppaaldesc/*.class ui/CheckingError.class compiler/tmlparser/*.class

tmltranslator:
	rm -f $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(TMLTRANSLATOR_JAR_TXT) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY)  TMLTranslator.class tmltranslator/*.class tmltranslator/*/*.class myutil/*.class translator/*.class uppaaldesc/*.class ui/CheckingError.class compiler/tmlparser/*.class

rundse:
	rm -f $(TTOOL_BIN)/$(RUNDSE_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(RUNDSE_JAR_TXT) $(TTOOL_BIN)/$(RUNDSE_BINARY)  RunDSE.class dseengine/*.class tmltranslator/*.class myutil/*.class


remotesimulator:
	rm -f $(TTOOL_BIN)/$(REMOTESIMULATOR_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(REMOTESIMULATOR_JAR_TXT) $(TTOOL_BIN)/$(REMOTESIMULATOR_BINARY)  RemoteSimulationControl.class remotesimulation/*.class

webcrawler:
	rm -f $(TTOOL_BIN)/$(WEBCRAWLER_SERVER_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(WEBCRAWLER_SERVER_JAR_TXT) $(TTOOL_BIN)/$(WEBCRAWLER_SERVER_BINARY)  web/crawler/*.class myutil/*.class myutil/*/*.class
	rm -f $(TTOOL_BIN)/$(WEBCRAWLER_CLIENT_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(WEBCRAWLER_CLIENT_JAR_TXT) $(TTOOL_BIN)/$(WEBCRAWLER_CLIENT_BINARY)  web/crawler/*.class myutil/*.class myutil/*/*.class

documentation:
	$(JAVADOC) $(CLASSPATH) $(TTOOL_SRC):-$(TTOOL_CLASSPATH_BINARY) -d $(TTOOL_DOC_HTML) $(TTOOL_SRC)/*.java $(TTOOL_SRC)/*/*.java $(TTOOL_SRC)/*/*/*.java $(TTOOL_SRC)/fr/inria/oasis/vercors/cttool/model/*.java
#	cd $(TTOOL_PATH)/doc/document_soclib&&make all

release: jttooljar launcher tiftranslator tmltranslator rundse remotesimulator ttooljar documentation stdrelease 
	@echo release done

########## RELEASE
stdrelease:
	echo "stdrelease"
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
# DIPLODOCUS simulators
	#mkdir -p $(TTOOL_TARGET)/simulators/systemc1/src_simulator
	#mkdir -p $(TTOOL_TARGET)/simulators/systemc1/lib
	#cp  $(TTOOL_SIMULATORS)/systemc1/lib/README $(TTOOL_TARGET)/simulators/systemc1/lib/
	#cp  $(TTOOL_SIMULATORS)/systemc1/Makefile $(TTOOL_TARGET)/simulators/systemc1
	#cp  $(TTOOL_SIMULATORS)/systemc1/Makefile.defs $(TTOOL_TARGET)/simulators/systemc1
	#cp  $(TTOOL_SIMULATORS)/systemc1/src_simulator/*.cpp $(TTOOL_TARGET)/simulators/systemc1/src_simulator
	#cp  $(TTOOL_SIMULATORS)/systemc1/src_simulator/*.h $(TTOOL_TARGET)/simulators/systemc1/src_simulator
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
	mkdir -p $(TTOOL_TARGET)/simulators/c++2/lib
	cp  $(TTOOL_SIMULATORS)/c++2/lib/README $(TTOOL_TARGET)/simulators/c++2/lib/
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile $(TTOOL_TARGET)/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile.defs $(TTOOL_TARGET)/simulators/c++2
#	cp  $(TTOOL_SIMULATORS)/c++2/schedstyle.css $(TTOOL_TARGET)/simulators/c++2
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
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
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
# Proverif
	mkdir -p $(TTOOL_TARGET)/proverif
	cp $(TTOOL_DOC)/README_proverif $(TTOOL_TARGET)/proverif
# Graphs
	mkdir -p $(TTOOL_TARGET)/graphs
	cp $(TTOOL_DOC)/README_graph $(TTOOL_TARGET)/graphs/
# Figure
	mkdir -p $(TTOOL_TARGET)/figures
	cp $(TTOOL_DOC)/README_figure $(TTOOL_TARGET)/figures
	cp $(TTOOL_FIGURES)/Makefile $(TTOOL_TARGET)/figures
	cp $(TTOOL_FIGURES)/mli.mk $(TTOOL_TARGET)/figures
# VCD
	mkdir -p $(TTOOL_TARGET)/vcd
	cp $(TTOOL_DOC)/README_vcd $(TTOOL_TARGET)/vcd
# Basic doc
	mkdir -p $(TTOOL_TARGET)/doc
	cp $(TTOOL_DOC)/README_doc $(TTOOL_TARGET)/doc
	cp $(TTOOL_DOC_SOCLIB)/doc_ttool_soclib.pdf  $(TTOOL_TARGET)/doc/
# AVATAR executable code
	mkdir -p $(TTOOL_TARGET)/TToolexecutablecode
	mkdir -p $(TTOOL_TARGET)/executablecode/src
	mkdir -p $(TTOOL_TARGET)/executablecode/generated_src
	cp $(TTOOL_EXECUTABLECODE)/Makefile $(TTOOL_TARGET)/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/Makefile.defs $(TTOOL_TARGET)/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/Makefile.forsoclib $(TTOOL_TARGET)/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/src/*.c $(TTOOL_TARGET)/executablecode/src/
	cp $(TTOOL_EXECUTABLECODE)/src/*.h $(TTOOL_TARGET)/executablecode/src/
	cp $(TTOOL_EXECUTABLECODE)/generated_src/README $(TTOOL_TARGET)/executablecode/generated_src/
# MPSOC
	mkdir -p $(TTOOL_TARGET)/MPSoC
	mkdir -p $(TTOOL_TARGET)/MPSoC/generated_topcell
	mkdir -p $(TTOOL_TARGET)/MPSoC/generated_src
	mkdir -p $(TTOOL_TARGET)/MPSoC/src
	cp $(TTOOL_MPSOC)/Makefile $(TTOOL_TARGET)/MPSoC/
	cp $(TTOOL_MPSOC)/Makefile.defs $(TTOOL_TARGET)/MPSoC/
	cp $(TTOOL_MPSOC)/Makefile.forsoclib $(TTOOL_TARGET)/MPSoC/
	cp $(TTOOL_MPSOC)/src/*.c $(TTOOL_TARGET)/MPSoC/src/
	cp $(TTOOL_MPSOC)/src/*.h $(TTOOL_TARGET)/MPSoC/src/
	cp $(TTOOL_MPSOC)/generated_src/README $(TTOOL_TARGET)/MPSoC/generated_src/
	cp $(TTOOL_MPSOC)/generated_topcell/nbproc $(TTOOL_TARGET)/MPSoC/generated_topcell/
	cp $(TTOOL_MPSOC)/generated_topcell/config_noproc $(TTOOL_TARGET)/MPSoC/generated_topcell/
# Basic bin
	cp $(TTOOL_EXE) $(TTOOL_TARGET)/
	mkdir -p $(TTOOL_TARGET)/bin
	cp $(TTOOL_DOC)/README_bin $(TTOOL_TARGET)/bin
	cp $(TTOOL_BIN)/configuration.gcf $(TTOOL_TARGET)/bin
	cp -R $(TTOOL_BIN)/$(TTOOL_LOTOS_H).h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.t  $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.f $(TTOOL_TARGET)/bin
	cp $(TTOOL_BIN)/$(TTOOL_BINARY) $(TTOOL_BIN)/$(LAUNCHER_BINARY) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY) $(TTOOL_BIN)/$(RUNDSE_BINARY) $(TTOOL_CONFIG_SRC) $(TTOOL_BIN)/$(JSOUP_BINARY) $(TTOOL_BIN)/$(COMMON_CODEC_BINARY) $(TTOOL_BIN)/$(GSCORE_BINARY) $(TTOOL_BIN)/$(GSUI_BINARY)   $(TTOOL_TARGET)/bin
	cp $(TTOOL_TARGET)/bin/config_linux.xml $(TTOOL_TARGET)/bin/config.xml
# Basic release
	echo "Basic release"
	cd $(TTOOL_TARGET_RELEASE)&&$(TAR) cvzf $(TTOOL_STD_RELEASE)/release.tgz * 
# Advanced release
	echo "Advanced release"
	$(JAVADOC) -J-Xmx256m $(CLASSPATH) $(TTOOL_SRC) -d $(TTOOL_TARGET)/doc/srcdoc $(TTOOL_SRC)/*.java $(TTOOL_SRC)/*/*.java $(TTOOL_SRC)/*/*/*.java $(TTOOL_SRC)/fr/inria/oasis/vercors/cttool/model/*.java
	mkdir -p $(TTOOL_TARGET)/src
	cp -R $(TTOOL_SRC)/* $(TTOOL_TARGET)/src
	find $(TTOOL_TARGET)/src -type f -not \( -name '*.java' -o -name '*.gif' -o -name '*.jjt' -o -name '*.txt' \) -a -exec rm -f {} \;
	cp -R $(TTOOL_DOC)/README_src $(TTOOL_TARGET)/src
	cd $(TTOOL_TARGET_RELEASE);$(TAR) cvzf $(TTOOL_STD_RELEASE)/releaseWithSrc.tgz *

preinstall: jar remove_preinstall preinstall_linux preinstall_windows preinstall_macos

remove_preinstall:
	rm -rf $(TTOOL_PREINSTALL)

define functionCommonPreinstall
#Main directories
	mkdir -p $(TTOOL_PREINSTALL)
	mkdir -p $(1)
	mkdir -p $(1)/TTool/
#jars
	mkdir -p $(1)/TTool/bin
	cp $(TTOOL_BIN)/*.jar $(1)/TTool/bin/
#models
	mkdir -p $(1)/TTool/modeling/
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_XML) $(1)/TTool/modeling/
	cp $(TTOOL_DOC)/README_modeling $(1)/TTool/modeling/
#models
	mkdir -p $(1)/TTool/modeling/
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_XML) $(1)/TTool/modeling/
	cp $(TTOOL_DOC)/README_modeling $(1)/TTool/modeling/
# lib
	mkdir -p $(1)/TTool/lib
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_LIB) $(1)/TTool/lib
	cp $(TTOOL_DOC)/README_lib $(1)/TTool/lib
# DIPLODOCUS simulators
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator/app
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator/arch
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator/ebrdd
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator/evt
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator/sim
	mkdir -p $(1)/TTool/simulators/c++2/src_simulator/TEPE
	mkdir -p $(1)/TTool/simulators/c++2/lib
	cp  $(TTOOL_SIMULATORS)/c++2/lib/README $(1)/TTool/simulators/c++2/lib/
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile $(1)/TTool/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile.defs $(1)/TTool/simulators/c++2
#	cp  $(TTOOL_SIMULATORS)/c++2/schedstyle.css $(1)/TTool/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.cpp $(1)/TTool/simulators/c++2/src_simulator
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.h $(1)/TTool/simulators/c++2/src_simulator
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.cpp $(1)/TTool/simulators/c++2/src_simulator/app
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.h $(1)/TTool/simulators/c++2/src_simulator/app
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.cpp $(1)/TTool/simulators/c++2/src_simulator/arch
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.h $(1)/TTool/simulators/c++2/src_simulator/arch
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.cpp $(1)/TTool/simulators/c++2/src_simulator/ebrdd
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.h $(1)/TTool/simulators/c++2/src_simulator/ebrdd
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.cpp $(1)/TTool/simulators/c++2/src_simulator/evt
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.h $(1)/TTool/simulators/c++2/src_simulator/evt
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.cpp $(1)/TTool/simulators/c++2/src_simulator/sim
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.h $(1)/TTool/simulators/c++2/src_simulator/sim
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.cpp $(1)/TTool/simulators/c++2/src_simulator/TEPE
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.h $(1)/TTool/simulators/c++2/src_simulator/TEPE
# Licenses
	cd $(TTOOL_DOC); cp $(RELEASE_STD_FILES_LICENSES) $(1)/TTool
# Main readme
	cp $(TTOOL_DOC)/README $(1)/TTool
#TML
	mkdir -p $(1)/TTool/tmlcode
	cp $(TTOOL_DOC)/README_tml $(1)/TTool/tmlcode
#UPPAAL

	mkdir -p $(1)/TTool/uppaal
	cp $(TTOOL_DOC)/README_uppaal $(1)/TTool/uppaal
# Proverif
	mkdir -p $(1)/TTool/proverif
	cp $(TTOOL_DOC)/README_proverif $(1)/TTool/proverif
#Graphs
	mkdir -p $(1)/TTool/graphs
	cp $(TTOOL_DOC)/README_graph $(1)/TTool/graphs
# Figure
	mkdir -p $(1)/TTool/figures
	cp $(TTOOL_DOC)/README_figure $(1)/TTool/figures
# VCD
	mkdir -p $(1)/TTool/vcd
	cp $(TTOOL_DOC)/README_vcd $(1)/TTool/vcd
# Basic doc
	mkdir -p $(TTOOL_TARGET)/doc
	cp $(TTOOL_DOC)/README_doc $(TTOOL_TARGET)/doc
# AVATAR executable code
	mkdir -p $(1)/TTool/executablecode
	mkdir -p $(1)/TTool/executablecode/src
	mkdir -p $(1)/TTool/executablecode/generated_src
	cp $(TTOOL_EXECUTABLECODE)/Makefile $(1)/TTool/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/Makefile.defs $(1)/TTool/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/Makefile.forsoclib $(1)/TTool/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/src/*.c $(1)/TTool/executablecode/src/
	cp $(TTOOL_EXECUTABLECODE)/src/*.h $(1)/TTool/executablecode/src/
	cp $(TTOOL_EXECUTABLECODE)/generated_src/README $(1)/TTool/executablecode/generated_src/
# MPSOC
	mkdir -p $(1)/TTool/MPSoC
	mkdir -p $(1)/TTool/MPSoC/generated_topcell
	mkdir -p $(1)/TTool/MPSoC/generated_src
	mkdir -p $(1)/TTool/MPSoC/src
	cp $(TTOOL_MPSOC)/Makefile $(1)/TTool/MPSoC/
	cp $(TTOOL_MPSOC)/Makefile.defs $(1)/TTool/MPSoC/
	cp $(TTOOL_MPSOC)/Makefile.forsoclib $(1)/TTool/MPSoC/
	cp $(TTOOL_MPSOC)/src/*.c $(1)/TTool/MPSoC/src/
	cp $(TTOOL_MPSOC)/src/*.h $(1)/TTool/MPSoC/src/
	cp $(TTOOL_MPSOC)/generated_src/README $(1)/TTool/MPSoC/generated_src/
	cp $(TTOOL_MPSOC)/generated_topcell/nbproc $(1)/TTool/MPSoC/generated_topcell/
	cp $(TTOOL_MPSOC)/generated_topcell/config_noproc $(1)/TTool/MPSoC/generated_topcell/
# Basic bin
	mkdir -p $(TTOOL_TARGET)/bin
	cp $(TTOOL_DOC)/README_bin $(1)/TTool/bin
	cp $(TTOOL_BIN)/configuration.gcf $(1)/TTool/bin
	cp $(TTOOL_BIN)/$(TTOOL_BINARY) $(TTOOL_BIN)/$(LAUNCHER_BINARY) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY) $(TTOOL_BIN)/$(RUNDSE_BINARY) $(TTOOL_BIN)/$(JSOUP_BINARY) $(TTOOL_BIN)/$(COMMON_CODEC_BINARY)  $(TTOOL_BIN)/$(GSCORE_BINARY) $(TTOOL_BIN)/$(GSUI_BINARY)  $(1)/TTool/bin

endef


preinstall_windows:
	$(call functionCommonPreinstall,$(TTOOL_PREINSTALL_WINDOWS)/)
#Proverif	
	cp $(TTOOL_PRIVATE)/stocks/proverif_windows.tar.gz $(TTOOL_PREINSTALL_WINDOWS)/
	cd $(TTOOL_PREINSTALL_WINDOWS)/ && gunzip -f proverif_windows.tar.gz && tar -xof proverif_windows.tar && rm proverif_windows.tar
#UPPAAL
	cp $(TTOOL_PRIVATE)/stocks/uppaal.tar.gz $(TTOOL_PREINSTALL_WINDOWS)/
	cd $(TTOOL_PREINSTALL_WINDOWS)/ && gunzip -f uppaal.tar.gz && tar -xof uppaal.tar && rm uppaal.tar
#bin
	cp $(TTOOL_DOC)/config_windows.xml $(TTOOL_PREINSTALL_WINDOWS)/TTool/bin/config.xml
	cp $(TTOOL_DOC)/ttool_windows.bat $(TTOOL_PREINSTALL_WINDOWS)/ttool.bat
# Make the tgz file
	cd $(TTOOL_PREINSTALL_WINDOWS)/.. && tar -czvf ttoolwindows.tgz $(TTOOL_WINDOWS)/*
#Publish it
	scp $(TTOOL_PREINSTALL_LINUX)/../ttoolwindows.tgz apvrille@ssh.enst.fr:public_html/docs/


preinstall_macos:
	$(call functionCommonPreinstall,$(TTOOL_PREINSTALL_MACOS)/)
#Proverif	
	cp $(TTOOL_PRIVATE)/stocks/proverif_macos.tar.gz $(TTOOL_PREINSTALL_MACOS)/
	cd $(TTOOL_PREINSTALL_MACOS)/ && gunzip -f proverif_macos.tar.gz && tar -xof proverif_macos.tar && rm proverif_macos.tar
#UPPAAL
	cp $(TTOOL_PRIVATE)/stocks/uppaal_macos.tar.gz $(TTOOL_PREINSTALL_MACOS)/
	cd $(TTOOL_PREINSTALL_MACOS)/ && gunzip -f uppaal_macos.tar.gz && tar -xof uppaal_macos.tar && rm uppaal_macos.tar
	mv $(TTOOL_PREINSTALL_MACOS)/uppaal* $(TTOOL_PREINSTALL_MACOS)/uppaal
#bin
	cp $(TTOOL_DOC)/config_macosx.xml $(TTOOL_PREINSTALL_MACOS)/TTool/bin/config.xml
	cp $(TTOOL_DOC)/ttool4preinstalllinux.exe $(TTOOL_PREINSTALL_MACOS)/ttool.exe
# Make the tgz file
	cd $(TTOOL_PREINSTALL_MACOS)/.. && tar -czvf ttoolmacos.tgz $(TTOOL_MACOS)/*

preinstall_linux:
# Common part
	$(call functionCommonPreinstall,$(TTOOL_PREINSTALL_LINUX)/)
#Proverif	
	cp $(TTOOL_PRIVATE)/stocks/proverif_linux.tar.gz $(TTOOL_PREINSTALL_LINUX)/
	cd $(TTOOL_PREINSTALL_LINUX)/ && gunzip -f proverif_linux.tar.gz && tar -xof proverif_linux.tar && rm proverif_linux.tar
#UPPAAL
	cp $(TTOOL_PRIVATE)/stocks/uppaal.tar.gz $(TTOOL_PREINSTALL_LINUX)/
	cd $(TTOOL_PREINSTALL_LINUX)/ && gunzip -f uppaal.tar.gz && tar -xof uppaal.tar && rm uppaal.tar
# Configuration and executable
	cp $(TTOOL_DOC)/config_windows.xml $(TTOOL_PREINSTALL_LINUX)/TTool/bin/config.xml
	cp $(TTOOL_DOC)/ttool4preinstalllinux.exe $(TTOOL_PREINSTALL_LINUX)/ttool.exe
# Make the tgz file
	cd $(TTOOL_PREINSTALL_LINUX)/.. && tar -czvf ttoollinux.tgz $(TTOOL_LINUX)/*
#Publish it
	scp $(TTOOL_PREINSTALL_LINUX)/../ttoollinux.tgz apvrille@ssh.enst.fr:public_html/docs/

jttooljar:
	cd $(JTTOOL);$(JAVAC) $(JTTOOL_DIR)/*.java;$(JAR) cmf $(TTOOL_SRC)/$(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(JTTOOL_JAR) $(JTTOOL_DIR)/*.class 


test: basic $(TEST_DIRS)
	@echo "Everything went fine"

$(TEST_DIR)/%/$(TEST_MK): $(TEST_DIR)/$(TEST_MK)
	@cp $< $@

$(TEST_DIRS): %: %/$(TEST_MK) force
	$(MAKE) -s -C $@ -f $(TEST_MK)

.PHONY: force
force:;

publish_jar: ttooljar
	scp bin/ttool.jar apvrille@ssh.enst.fr:public_html/docs/
	ssh apvrille@ssh.enst.fr "chmod a+r public_html/docs/ttool.jar"

clean:
	rm -f $(TTOOL_SRC)/*.dot $(TTOOL_SRC)/*.dta $(TTOOL_SRC)/*.sim $(TTOOL_SRC)/*.lot
	rm -f $(TTOOL_SRC)/*.class $(TTOOL_SRC)/*.java~
	rm -f $(TTOOL_TARGET)/java/*
	rm -f $(TTOOL_TARGET)/modeling/*
	rm -f $(TTOOL_TARGET)/bin/*
	rm -f $(TTOOL_TARGET)/lotos/*
	rm -rf $(TTOOL_TARGET)/doc/*
	rm -rf $(TTOOL_TARGET)/src/*
	rm -f $(TTOOL_TARGET)/lib/*
	@@for p in $(PACKAGE); do \
		echo rm -f $$p/*.class;\
		rm -f $(TTOOL_SRC)/$$p/*.class $(TTOOL_SRC)/$$p/*.java~; \
	done
	rm -f $(TEST_DIR)/*.class
	@@for t in $(TEST_DIRS); do \
		if [ -w $$t/$(TEST_MK) ]; \
		then \
			$(MAKE) -s -C $$t -f $(TEST_MK) clean; \
			echo rm -f $$t/*.class; \
			rm -f $$t/$(TEST_MK); \
		fi \
	done


ultraclean: clean
	@@for p in $(RELEASE_STD_FILES_BIN); do \
		echo rm -f $$p;\
		rm -f $(TTOOL_BIN)/$$p; \
	done
