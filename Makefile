
# TTool Makefile
# Tested under Linux *only*
# Meant to work with svn
export

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
#PACKAGE = avatartranslator avatartranslator/toexecutable avatartranslator/directsimulation avatartranslator/tocppsim avatartranslator/touppaal avatartranslator/toturtle avatartranslator/toproverif avatartranslator/totpn automata compiler/tmlparser vcd nc ddtranslator launcher myutil tpndescription sddescription sdtranslator tepe translator tmltranslator tmltranslator/tmlcp tmltranslator/toautomata tmltranslator/tosystemc  tmltranslator/tomappingsystemc tmltranslator/tomappingsystemc2 tmltranslator/touppaal tmltranslator/toturtle translator/tojava translator/tosimujava translator/totpn translator/touppaal  ui ui/avatarbd ui/avatardd ui/avatarsmd ui/avatarrd ui/avatarpd ui/avatarcd ui/avatarad ui/ad ui/cd ui/oscd ui/osad ui/dd ui/ebrdd ui/file ui/graph ui/iod ui/ncdd ui/procsd ui/prosmdui/prosmd/util ui/tmlad ui/tmlcd ui/tmldd ui/tmlcomp ui/req ui/sd ui/tree ui/ucd ui/window ui/avatarmethodology ui/sysmlsecmethodology tmltranslator tmltranslator/toturtle req/ebrdd tmltranslator/tosystemc tmatrix proverifspec uppaaldesc fr/inria/oasis/vercors/cttool/model remotesimulation tmltranslator/ctranslator attacktrees myutil/externalSearch
BUILDER = builder.jar
BUILD_INFO = build.txt
BUILD_TO_MODIFY = src/ui/DefaultText.java
TTOOL_BINARY = ttool.jar
LAUNCHER_BINARY = launcher.jar
TIFTRANSLATOR_BINARY = tiftranslator.jar
TMLTRANSLATOR_BINARY = tmltranslator.jar
JSOUP_BINARY = jsoup-1.8.1.jar
COMMON_CODEC_BINARY = commons-codec-1.10.jar
RUNDSE_BINARY = rundse.jar
REMOTESIMULATOR_BINARY = simulationcontrol.jar
RUNDSE_BINARY = rundse.jar
RUNDSE_JAR_TXT  = rundse.txt
TTOOL_JAR_TXT = ttool.txt
LAUNCHER_JAR_TXT = launcher.txt
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
TTOOL_CLASSPATH_BINARY = $(TTOOL_BIN)/JavaPlot.jar:$(TTOOL_BIN)/commons-codec-1.10.jar:$(TTOOL_BIN)/commons-io-2.4-javadoc.jar:$(TTOOL_BIN)/commons-io-2.4.jar:$(TTOOL_BIN)/derby.jar:$(TTOOL_BIN)/derbyclient-10.9.1.0.jar:$(TTOOL_BIN)/derbynet.jar:$(TTOOL_BIN)/dom4j-1.6.1.jar:$(TTOOL_BIN)/jaxen-1.1.6.jar:$(TTOOL_BIN)/jsoup-1.8.1.jar:$(TTOOL_BIN)/opencloud.jar:.
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
TTOOL_PREINSTALL = $(TTOOL_PATH)/preinstallTTool
TTOOL_PREINSTALL_LINUX = $(TTOOL_PREINSTALL)/TTool_Linux
PACKAGE = $(shell cd $(TTOOL_SRC); find . -type d)

TTOOL_CONFIG_SRC = $(TTOOL_DOC)/config_linux.xml  $(TTOOL_DOC)/config_macosx.xml  $(TTOOL_DOC)/config_windows.xml
TTOOL_EXE = $(TTOOL_DOC)/ttool_linux.exe  $(TTOOL_DOC)/ttool_macosx.exe  $(TTOOL_DOC)/ttool_windows.bat

RELEASE_STD_FILES_LINUX_EXE = ttool_unix
RELEASE_STD_FILES_WINDIWS_EXE = ttool_windows.bat

RELEASE_STD_FILES_XML = manual-HW.xml DrinkMachineV10.xml WebV01.xml Protocol_example1.xml BasicExchange.xml SmartCardProtocol.xml ProtocolPatterns.xml COCOME_V50.xml CoffeeMachine_Avatar.xml Network_Avatar.xml MicroWaveOven_SafetySecurity_fullMethodo.xml
RELEASE_STD_FILES_LIB =  TClock1.lib TTimerv01.lib
RELEASE_STD_FILES_BIN = $(LAUNCHER_BINARY) $(TTOOL_BINARY) $(TIFTRANSLATOR_BINARY) $(TMLTRANSLATOR_BINARY) $(REMOTESIMULATOR_BINARY) $(RUNDSE_BINARY) $(WEBCRAWLER_SERVER_BINARY) $(WEBCRAWLER_CLIENT_BINARY)
RELEASE_STD_FILES_LICENSES = LICENSE LICENSE_CECILL_ENG LICENSE_CECILL_FR

TEST_DIR        = $(TTOOL_PATH)/tests
TEST_MK         = test.mk
TEST_DIRS       = $(shell find $(TEST_DIR)/* -type d)
TEST_MAKEFILES  = $(patsubst %,%/$(TEST_MK),$(TEST_DIRS))



define HELP_message
How to compile TTool:
---------------------
make all                        builds TTool and produces the jar files in bin/

Usual targets:
---------------
make (help)                     prints this help
make svn                        produces the .class files and commit a new build version in the svn
make basic                      generates the .class files
make documentation              generates the documentation of java classes using javadoc
make release                    to prepare a new release for the website. It produces the release.tgz files in releases/
make test                       tests on TTool. Currently, tests on AVATAR to ProVerif generation
make clean                      removes the .class .dot .dta .sim .lot .~ and clears the release and test directories
make publish_jar                places ttool.jar in perso.telecom-paristech.fr/docs/ttool.jar. Must have the right ssh key installed for this
make ultraclean                 runs clean and then removes the jar files in bin/

Other targets:
--------------
make basicsvnapvrille           produces the .class files and commit a new build version in the svn with username "apvrille"
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

svn: svnup jar

svnup:
	date
	svn update build.txt src/ui/DefaultText.java
	$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
	svn --username apvrille commit build.txt src/ui/DefaultText.java -m 'update on build version: builder.txt'

basicsvnapvrille: svupapvrille jar

svnupapvrille
	date
	svn --username apvrille update build.txt src/ui/DefaultText.java
	$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
	svn --username apvrille commit build.txt src/ui/DefaultText.java -m 'update on build version: builder.txt'

myrelease: basic launcher ttooljar 

basic:
	$(JAVAC) $(SOURCEPATH) $(TTOOL_SRC) $(CLASSPATH) $(TTOOL_CLASSPATH_BINARY) $(TTOOL_SRC)/*.java $(TTOOL_WEBCRAWLER_SRC)/*.java 

jar: launcher ttooljar tiftranslator tmltranslator rundse remotesimulator webcrawler

ttooljar:
	rm -f $(TTOOL_BIN)/$(TTOOL_BINARY)
	cp $(TTOOL_SRC)/ui/images/$(STD_LOGO) $(TTOOL_SRC)/ui/images/$(LOGO) 
	cd $(TTOOL_SRC);  $(JAR) cmf $(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(TTOOL_BINARY) Main.class vcd/*.class avatartranslator/*.class avatartranslator/toexecutable/*.class avatartranslator/directsimulation/*.class avatartranslator/touppaal/*.class avatartranslator/toproverif/*.class avatartranslator/totpn/* avatartranslator/*.class avatartranslator/toturtle/*.java automata/*.class compiler/tmlparser/*.class nc/*.class  tepe/*.class tmltranslator/*.class tmltranslator/toavatar/*.class tmltranslator/tmlcp/*.class tmltranslator/toautomata/*.class tmatrix/*.class tmltranslator/toturtle/*.class tmltranslator/touppaal/*.class tmltranslator/tosystemc/*.class tmltranslator/tomappingsystemc/*.class tmltranslator/tomappingsystemc2/*.class  tpndescription/*.class ddtranslator/*.class launcher/*.class myutil/*.class sddescription/*.class sdtranslator/*.class translator/*.class translator/tojava/*.class  translator/tosimujava/*.class translator/touppaal/*.class translator/totpn/*.class req/ebrdd/*.java ui/*.class ui/*/*.class ui/*/*/*.class proverifspec/*.class uppaaldesc/*.class ui/images/*.* ui/images/toolbarButtonGraphics/general/*.gif ui/images/toolbarButtonGraphics/navigation/*.gif  ui/images/toolbarButtonGraphics/media/*.gif $(TTOOL_BIN)/$(LAUNCHER_BINARY) RTLLauncher.class launcher/*.class fr/inria/oasis/vercors/cttool/model/*.class remotesimulation/*.class tmltranslator/ctranslator/*.class attacktrees/*.class myutil/externalSearch/*.class ddtranslatorSoclib/*.class ddtranslatorSoclib/toSoclib/*.class ddtranslatorSoclib/toTopCell/*.class #compiler/tmlCPparser/parser/*.class

launcher:
	rm -f $(TTOOL_BIN)/$(LAUNCHER_BINARY)
	cd $(TTOOL_SRC);$(JAR) cmf $(LAUNCHER_JAR_TXT) $(TTOOL_BIN)/$(LAUNCHER_BINARY)  RTLLauncher.class launcher/*.class myutil/*.class

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
	$(JAVADOC) $(CLASSPATH) $(TTOOL_SRC) -d $(TTOOL_DOC_HTML) $(TTOOL_SRC)/*.java $(TTOOL_SRC)/*/*.java $(TTOOL_SRC)/*/*/*.java $(TTOOL_SRC)/fr/inria/oasis/vercors/cttool/model/*.java

release: jttooljar launcher tiftranslator tmltranslator rundse remotesimulator ttooljar stdrelease 
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
	mkdir -p $(TTOOL_TARGET)/executablecode
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
	cp $(TTOOL_BIN)/$(TTOOL_BINARY) $(TTOOL_BIN)/$(LAUNCHER_BINARY) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY) $(TTOOL_BIN)/$(RUNDSE_BINARY) $(TTOOL_CONFIG_SRC) $(TTOOL_BIN)/$(JSOUP_BINARY) $(TTOOL_BIN)/$(COMMON_CODEC_BINARY)  $(TTOOL_TARGET)/bin
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

preinstall: jar preinstall_linux

preinstall_linux:
#jars
	cp $(TTOOL_BIN)/*.jar $(TTOOL_PREINSTALL_LINUX)/TTool/bin/
#models
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_XML) $(TTOOL_PREINSTALL_LINUX)/TTool/modeling/
	cp $(TTOOL_DOC)/README_modeling $(TTOOL_PREINSTALL_LINUX)/TTool/modeling/
# lib
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/lib
	cd $(TTOOL_MODELING); cp $(RELEASE_STD_FILES_LIB) $(TTOOL_PREINSTALL_LINUX)/TTool/lib
	cp $(TTOOL_DOC)/README_lib $(TTOOL_PREINSTALL_LINUX)/TTool/lib
# DIPLODOCUS simulators
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/app
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/arch
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/ebrdd
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/evt
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/sim
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/TEPE
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/lib
	cp  $(TTOOL_SIMULATORS)/c++2/lib/README $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/lib/
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/Makefile.defs $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/schedstyle.css $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/app
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/app
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/arch
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/arch
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/ebrdd
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/ebrdd
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/evt
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/evt
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/sim
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/sim
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.cpp $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/TEPE
	cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.h $(TTOOL_PREINSTALL_LINUX)/TTool/simulators/c++2/src_simulator/TEPE
# Licenses
	cd $(TTOOL_DOC); cp $(RELEASE_STD_FILES_LICENSES) $(TTOOL_PREINSTALL_LINUX)/TTool
# Main readme
	cp $(TTOOL_DOC)/README $(TTOOL_PREINSTALL_LINUX)/TTool
# LOTOS
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/lotos
	cp $(TTOOL_DOC)/README_lotos $(TTOOL_PREINSTALL_LINUX)/TTool/lotos
#NC
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/nc
	cp $(TTOOL_DOC)/README_nc $(TTOOL_TARGET)/nc
#TML
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/tmlcode
	cp $(TTOOL_DOC)/README_tml $(TTOOL_PREINSTALL_LINUX)/TTool/tmlcode
#UPPAAL
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/uppaal
	cp $(TTOOL_DOC)/README_uppaal $(TTOOL_PREINSTALL_LINUX)/TTool/uppaal
# Proverif
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/proverif
	cp $(TTOOL_DOC)/README_proverif $(TTOOL_PREINSTALL_LINUX)/TTool/proverif
# Figure
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/figure
	cp $(TTOOL_DOC)/README_figure $(TTOOL_PREINSTALL_LINUX)/TTool/figure
# VCD
	mkdir -p $(TTOOL_PREINSTALL_LINUX)/TTool/vcd
	cp $(TTOOL_DOC)/README_vcd $(TTOOL_PREINSTALL_LINUX)/TTool/vcd
# Basic doc
	mkdir -p $(TTOOL_TARGET)/doc
	cp $(TTOOL_DOC)/README_doc $(TTOOL_TARGET)/doc
# AVATAR executable code
	mkdir -p $(TTOOL_TARGET)/executablecode
	mkdir -p $(TTOOL_TARGET)/executablecode/src
	mkdir -p $(TTOOL_TARGET)/executablecode/generated_src
	cp $(TTOOL_EXECUTABLECODE)/Makefile $(TTOOL_TARGET)/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/Makefile.defs $(TTOOL_TARGET)/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/Makefile.forsoclib $(TTOOL_TARGET)/executablecode/
	cp $(TTOOL_EXECUTABLECODE)/src/*.c $(TTOOL_TARGET)/executablecode/src/
	cp $(TTOOL_EXECUTABLECODE)/src/*.h $(TTOOL_TARGET)/executablecode/src/
	cp $(TTOOL_EXECUTABLECODE)/generated_src/README $(TTOOL_TARGET)/executablecode/generated_src/

# Basic bin
	mkdir -p $(TTOOL_TARGET)/bin
	cp $(TTOOL_DOC)/README_bin $(TTOOL_TARGET)/bin
	cp $(TTOOL_BIN)/configuration.gcf $(TTOOL_TARGET)/bin
	cp -R $(TTOOL_BIN)/$(TTOOL_LOTOS_H).h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.h $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.t  $(TTOOL_BIN)/$(TTOOL_LOTOS_H)_?.f $(TTOOL_TARGET)/bin
	cp $(TTOOL_BIN)/$(TTOOL_BINARY) $(TTOOL_BIN)/$(LAUNCHER_BINARY) $(TTOOL_BIN)/$(TIFTRANSLATOR_BINARY) $(TTOOL_BIN)/$(TMLTRANSLATOR_BINARY) $(TTOOL_BIN)/$(RUNDSE_BINARY) $(TTOOL_BIN)/$(JSOUP_BINARY) $(TTOOL_BIN)/$(COMMON_CODEC_BINARY)  $(TTOOL_TARGET)/bin
	cp $(TTOOL_TARGET)/bin/config_linux.xml $(TTOOL_TARGET)/bin/config.xml


jttooljar:
	cd $(JTTOOL);$(JAVAC) $(JTTOOL_DIR)/*.java;$(JAR) cmf $(TTOOL_SRC)/$(TTOOL_JAR_TXT) $(TTOOL_BIN)/$(JTTOOL_JAR) $(JTTOOL_DIR)/*.class 

updatesimulator:
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/Doxyfile /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/

	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/app/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/app/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/app/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/app/

	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/arch/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/arch/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/arch/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/arch/

	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/ebrdd/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/ebrdd/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/ebrdd/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/ebrdd/

	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/evt/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/evt/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/evt/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/evt/

	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/sim/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/sim/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/sim/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/sim/

	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/TEPE/*.cpp /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/TEPE/
	cp /homes/apvrille/TTool/simulators/c++3/src_simulator/TEPE/*.h /homes/apvrille/TechTTool/SystemCCode/generated/src_simulator/TEPE/

	cp /homes/apvrille/TTool/simulators/c++3/Makefile /homes/apvrille/TechTTool/SystemCCode/generated/

	cd /homes/apvrille/TechTTool/SystemCCode/generated/; make ultraclean

test: $(TEST_MAKEFILES)
	$(foreach m,$(TEST_MAKEFILES),$(MAKE) -s -C $(dir $(m)) -f $(TEST_MK);)
	@echo "Everything went fine"

$(TEST_DIR)/%/$(TEST_MK): $(TEST_DIR)/$(TEST_MK)
	@cp $< $@



publishjar: ttooljar
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
