export JAVAC  		= javac
export JAR    		= jar
JAVA			= java
JAVADOC			= javadoc
MAKE			= make -s
TAR			= tar
GZIP			= gzip
GRADLE_NO_TEST          = -x test 
GRADLE_OPTIONS          = --parallel
#GRADLE_OPTIONS          =
GRADLE			= $(shell which gradle)
GRADLE_VERSION_NEEDED	= 3.3
ERROR_MSG		= printf "$(COLOR)\nBuild with gradle failed. Falling back to regular javac command...\n$(RESET)"

NO_GUI_TESTS		= avatartranslator.* graph.* help.* launcher.* myutil.* tmltranslator.*

ifeq "$(GRADLE)" ""
    ERROR_MSG	= echo "Gradle was not found. Falling back to regular javac command...\n"
    GRADLE 	= false && echo >/dev/null
else
    GRADLE_VERSION 	:= $(shell $(GRADLE) --version | grep "^Gradle" | awk '{print $$2}')
    GRADLE_VERSION_MIN 	:= $(shell printf "%s\n%s\n" "$(GRADLE_VERSION_NEEDED)" "$(GRADLE_VERSION)"|sort -n 2>/dev/null|head -n1)
    ifneq "$(GRADLE_VERSION_NEEDED)" "$(GRADLE_VERSION_MIN)"
	ERROR_MSG	= echo "$(COLOR)Gradle $(GRADLE_VERSION) is too old. Needs at least $(GRADLE_VERSION_NEEDED). Falling back to regular javac command...\n$(RESET)"
	GRADLE = false && echo >/dev/null
    endif
endif

export COLOR		= $(shell tput setaf 1)
export RESET		= $(shell tput sgr0)
PREFIX			= [$(COLOR)BASE$(RESET)]             

export TTOOL_PATH 	:= $(shell /bin/pwd)

define HELP_message
Compilation targets:
--------------------
make all                Build TTool and the jar of companion software.
make ttool		Build TTool only


Usual targets:
--------------
make (help)             Print this help.
make documentation      Generate the documentation of java classes using javadoc.
make release            Prepare a new release for the website.
			It produces the release.tgz and releaseWithSrc.tgz files.
make test               Run tests on TTool.
make noguitest               Run non graphical tests on TTool.
make publish_jar        Build TTool and upload the resulting archive.
			!!! Must have the right ssh key installed for this !!!
make install		Install TTool, the jar of companion software and the runtime
			dependencies to $$DESTDIR/bin. By default, install to
			$(TTOOL_PATH)/bin.
make clean              Clean the repository from compilation artifacts.
make ultraclean         Clean the repository from binaries and compilation artifacts.

make ttooljavac		Build TTool only with javac
make ttoolnotest	Build TTool with gradle, but do not execute test. Performs the install
make allnotest		Builld all apps, but do not execute tests. Performs the install
make internalhelp		Generate the help of TTool in HTML format


Other targets:
--------------
make preinstall		Generate a preinstall version of TTool for Linux, Windows and
			MacOS and publish them on the website of TTool (hidden link)
			!!! Must have the right ssh key installed for this !!!
make git		Update the build number.


Please report bugs or suggestions of improvements to:
  ttool.telecom-paristech.fr/support.html
endef
export HELP_message

.PHONY: ttool clean launcher graphminimize graphshow ttool-cli tiftranslator rundse remotesimulator webcrawler documentation help ultraclean publish_jar preinstall test git

help:
	@echo "$$HELP_message"

FORCE:

# ======================================== 
# ========== SUB-PROJECTS BUILD ========== 
# ======================================== 
export TTOOL_SRC 		= $(TTOOL_PATH)/src/main/java
export GLOBAL_JAVA		= $(shell cd $(TTOOL_SRC); find . -name "[^.]*.java")
export TTOOL_RESOURCES		= $(TTOOL_PATH)/src/main/resources
export TTOOL_WEBCRAWLER_SRC 	= $(TTOOL_PATH)/src/main/java/web/crawler
export TTOOL_BUILD		= $(TTOOL_PATH)/build
export TTOOL_LIBS		= $(TTOOL_PATH)/libs
export TTOOL_LIBRARIES		= $(wildcard $(TTOOL_LIBS)/*.jar)
export TTOOL_CLASSPATH		= $(subst $(eval) ,:,$(TTOOL_LIBRARIES))

export GLOBAL_CFLAGS		= -encoding "UTF8" -Xlint:unchecked -Xlint:deprecation -Xlint:cast -Xlint:divzero -Xlint:empty -Xlint:finally -Xlint:fallthrough

export TTOOL_DIR		= $(TTOOL_PATH)/ttool
export TTOOL_BINARY 		= $(TTOOL_BUILD)/ttool.jar

export LAUNCHER_DIR		= $(TTOOL_PATH)/launcher
export LAUNCHER_BINARY 		= $(TTOOL_BUILD)/launcher.jar

export TTOOLCLI_DIR	= $(TTOOL_PATH)/ttool-cli
export TTOOLCLI_BINARY 	= $(TTOOL_BUILD)/ttool-cli.jar

export GRAPHMINIMIZE_DIR	= $(TTOOL_PATH)/graphminimize
export GRAPHMINIMIZE_BINARY 	= $(TTOOL_BUILD)/graphminimize.jar

export GRAPHSHOW_DIR		= $(TTOOL_PATH)/graphshow
export GRAPHSHOW_BINARY 	= $(TTOOL_BUILD)/graphshow.jar

export TIFTRANSLATOR_DIR	= $(TTOOL_PATH)/tiftranslator
export TIFTRANSLATOR_BINARY 	= $(TTOOL_BUILD)/tiftranslator.jar

export TMLTRANSLATOR_DIR	= $(TTOOL_PATH)/tmltranslator
export TMLTRANSLATOR_BINARY 	= $(TTOOL_BUILD)/tmltranslator.jar

export RUNDSE_DIR		= $(TTOOL_PATH)/rundse
export RUNDSE_BINARY 		= $(TTOOL_BUILD)/rundse.jar

export REMOTESIMULATOR_DIR	= $(TTOOL_PATH)/simulationcontrol
export REMOTESIMULATOR_BINARY 	= $(TTOOL_BUILD)/simulationcontrol.jar

export WEBCRAWLER_CLIENT_DIR	= $(TTOOL_PATH)/webcrawler/client
export WEBCRAWLER_CLIENT_BINARY	= $(TTOOL_BUILD)/webcrawler-client.jar

export WEBCRAWLER_SERVER_DIR	= $(TTOOL_PATH)/webcrawler/server
export WEBCRAWLER_SERVER_BINARY	= $(TTOOL_BUILD)/webcrawler-server.jar

export JTTOOL_DIR		= $(TTOOL_PATH)/jttool
export JTTOOL_BINARY		= $(TTOOL_BUILD)/jttool.jar

export TTOOL_HELP_DIR	= $(TTOOL_PATH)/src/main/resources/help
MD_FILES=$(wildcard src/main/resources/help/*.md)	
MD2HTML=$(MD_FILES:.md=.html)

all: ttool launcher ttool-cli graphminimize graphshow tiftranslator rundse remotesimulator webcrawler install

allnotest: GRADLE_OPTIONS += $(GRADLE_NO_TEST)
allnotest: ttool launcher ttool-cli graphminimize graphshow tiftranslator rundse remotesimulator webcrawler install

ttoolnotest: GRADLE_OPTIONS += $(GRADLE_NO_TEST)
ttoolnotest: ttool install

ttool: $(TTOOL_BINARY)
ttoolnotest: 

$(TTOOL_BINARY): FORCE
	@($(GRADLE) :ttool:build $(GRADLE_OPTIONS)) || ($(ERROR_MSG) $(GRADLE_VERSION) $(GRADLE_VERSION_NEEDED)&& $(MAKE) -C $(TTOOL_DIR) -e $@)


ttooljavac: 
	$(MAKE) -C $(TTOOL_DIR)
	$(MAKE) -C $(TTOOLCLI_DIR)

launcher: $(LAUNCHER_BINARY)

$(LAUNCHER_BINARY): FORCE
	@($(GRADLE) :launcher:build) || ($(ERROR_MSG) && $(MAKE) -C $(LAUNCHER_DIR) -e $@)

ttool-cli: $(TTOOLCLI_BINARY)

$(TTOOLCLI_BINARY): FORCE
	@($(GRADLE) :ttool-cli:build $(GRADLE_OPTIONS)) || ($(ERROR_MSG) && $(MAKE) -C $(TTOOLCLI_DIR) -e $@)

graphminimize: $(GRAPHMINIMIZE_BINARY)

$(GRAPHMINIMIZE_BINARY): FORCE
	@($(GRADLE) :graphminimize:build) || ($(ERROR_MSG) && $(MAKE) -C $(GRAPHMINIMIZE_DIR) -e $@)

graphshow: $(GRAPHSHOW_BINARY)

$(GRAPHSHOW_BINARY): FORCE
	@($(GRADLE) :graphshow:build) || ($(ERROR_MSG) && $(MAKE) -C $(GRAPHSHOW_DIR) -e $@)

tiftranslator: $(TIFTRANSLATOR_BINARY)

$(TIFTRANSLATOR_BINARY): FORCE
	@($(GRADLE) :tiftranslator:build) || ($(ERROR_MSG) && $(MAKE) -C $(TIFTRANSLATOR_DIR) -e $@)

tmltranslator: $(TMLTRANSLATOR_BINARY)

$(TMLTRANSLATOR_BINARY): FORCE
	@($(GRADLE) :tmltranslator:build) || ($(ERROR_MSG) && $(MAKE) -C $(TMLTRANSLATOR_DIR) -e $@)

rundse: $(RUNDSE_BINARY)

$(RUNDSE_BINARY): FORCE
	@($(GRADLE) :rundse:build) || ($(ERROR_MSG) && $(MAKE) -C $(RUNDSE_DIR) -e $@)

remotesimulator: $(REMOTESIMULATOR_BINARY)

$(REMOTESIMULATOR_BINARY): FORCE
	@($(GRADLE) :simulationcontrol:build) || ($(ERROR_MSG) && $(MAKE) -C $(REMOTESIMULATOR_DIR) -e $@)

webcrawler: $(WEBCRAWLER_CLIENT_BINARY) $(WEBCRAWLER_SERVER_BINARY)

$(WEBCRAWLER_CLIENT_BINARY): FORCE
	@($(GRADLE) :webcrawler-client:build) || ($(ERROR_MSG) && $(MAKE) -C $(WEBCRAWLER_CLIENT_DIR) -e $@)

$(WEBCRAWLER_SERVER_BINARY): FORCE
	@($(GRADLE) :webcrawler-server:build) || ($(ERROR_MSG) && $(MAKE) -C $(WEBCRAWLER_SERVER_DIR) -e $@)

#$(JTTOOL_BINARY): FORCE
#	@$(MAKE) -C $(JTTOOL_DIR) -e $@


internalhelp: html

html: $(MD2HTML)

%.html: %.md
	pandoc $< -f markdown -t html -s -o  $@ --lua-filter=doc/ttoolfilter.lua --metadata pagetitle="TTool help"

# ======================================== 
# ==========    DOCUMENTATION   ========== 
# ======================================== 
TTOOL_DOC			= $(TTOOL_PATH)/doc
export TTOOL_DOC_HTML 		= $(TTOOL_DOC)/html

DOCFLAGS			= -encoding "UTF8" -quiet -J-Xmx256m -classpath $(TTOOL_CLASSPATH) -d $(TTOOL_DOC_HTML)

documentation: $(patsubst %,$(TTOOL_SRC)/%,$(GLOBAL_JAVA))
	@echo "$(PREFIX) Generating Javadoc"
	@$(JAVADOC) $(DOCFLAGS) $^

# ======================================== 
# ==========      RELEASES      ========== 
# ======================================== 
TTOOL_PRIVATE 			?= $(TTOOL_PATH)/../TTool-Private
TTOOL_PRIVATE_RELEASES 			?= $(TTOOL_PRIVATE)/website/ttool/releases


PROD_USERNAME			= apvrille
PROD_ADDRESS			= ssh.enst.fr
PROD_PATH			= public_html/docs

TTOOL_DOC_SYSMLSEC_DIR		 	= $(TTOOL_DOC)/SysMLSec
TTOOL_DOC_AVATARCODEGENERATION_DIR 	= $(TTOOL_DOC)/codegeneration
TTOOL_DOC_SOCLIB_USERGUIDE_DIR 		= $(TTOOL_DOC)/documents_soclib/USER_GUIDE
TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR 	= $(TTOOL_DOC)/documents_soclib/INSTALLATION_GUIDE

TTOOL_MODELING			= $(TTOOL_PATH)/modeling
TTOOL_SIMULATORS 		= $(TTOOL_PATH)/simulators
TTOOL_FIGURES 			= $(TTOOL_PATH)/figures
TTOOL_EXECUTABLECODE 		= $(TTOOL_PATH)/executablecode
TTOOL_MPSOC 			= $(TTOOL_PATH)/MPSoC
TTOOL_STD_RELEASE 		= $(TTOOL_PATH)/release
TTOOL_TARGET_RELEASE 		= $(TTOOL_PATH)/TTool_install
TTOOL_TARGET 			= $(TTOOL_TARGET_RELEASE)/TTool
TTOOL_TARGET_WINDOWS		= $(TTOOL_TARGET_RELEASE)/Windows
TTOOL_TARGET_MACOS		= $(TTOOL_TARGET_RELEASE)/MacOS
TTOOL_TARGET_LINUX		= $(TTOOL_TARGET_RELEASE)/Linux

BASERELEASE			= $(TTOOL_STD_RELEASE)/baseRelease.tar
STDRELEASE			= $(TTOOL_STD_RELEASE)/release.tgz
ADVANCED_RELEASE		= $(TTOOL_STD_RELEASE)/releaseWithSrc.tgz
TTOOL_PREINSTALL_LINUX 		= $(TTOOL_STD_RELEASE)/ttoollinux.tgz
TTOOL_PREINSTALL_WINDOWS 	= $(TTOOL_STD_RELEASE)/ttoolwindows.tgz
TTOOL_PREINSTALL_MACOS 		= $(TTOOL_STD_RELEASE)/ttoolmacos.tgz

BUILDER			= $(TTOOL_PATH)/builder.jar
BUILD_INFO		= build.txt
BUILD_TO_MODIFY		= $(TTOOL_SRC)/ui/util/DefaultText.java

TTOOL_LOTOS_H		= $(patsubst $(TTOOL_DIR)/runtime/%,$(TTOOL_BUILD)/%,$(wildcard $(TTOOL_DIR)/runtime/spec*))

RELEASE_STD_FILES_XML 	= $(patsubst %,$(TTOOL_MODELING)/%,\
			  TURTLE/manual-HW.xml \
			  TURTLE/WebV01.xml \
			  TURTLE/Protocol_example1.xml \
			  TURTLE/BasicExchange.xml \
			  DIPLODOCUS/SmartCardProtocol.xml \
			  TURTLE/ProtocolPatterns.xml \
			  AVATAR/CoffeeMachine_Avatar.xml \
			  AVATAR/Network_Avatar.xml \
			  AVATAR/MicroWaveOven_SafetySecurity_fullMethodo.xml)
RELEASE_STD_FILES_LIB 	= $(patsubst %,$(TTOOL_MODELING)/%,\
			  TURTLE/TClock1.lib \
                          DIPLODOCUS/CPlibrary/ConfigPollingDMA_SD.lib\
                          DIPLODOCUS/CPlibrary/ConfigureDMA_SD.lib\
                          DIPLODOCUS/CPlibrary/DMACycle_SD.lib\
                          DIPLODOCUS/CPlibrary/DMAPollingCycleP_AD.lib\
                          DIPLODOCUS/CPlibrary/DMATransfer_NoPolling_MainCP.lib\
                          DIPLODOCUS/CPlibrary/DMATransfer_Polling_MainCP.lib\
                          DIPLODOCUS/CPlibrary/DMATransferCycleP_AD.lib\
                          DIPLODOCUS/CPlibrary/DMATransferCycleP_SD.lib\
                          DIPLODOCUS/CPlibrary/EnableFlag_SD.lib\
                          DIPLODOCUS/CPlibrary/LoadCPU_LoadConfigureSD.lib\
                          DIPLODOCUS/CPlibrary/LoadCPU_LoadTransferCycleSD.lib\
                          DIPLODOCUS/CPlibrary/LoadCPU_mainCP.lib\
                          DIPLODOCUS/CPlibrary/PollingCycle_SD.lib\
                          DIPLODOCUS/CPlibrary/StoreCPU_mainCP.lib\
                          DIPLODOCUS/CPlibrary/StoreCPU_StoreConfigureSD.lib\
                          DIPLODOCUS/CPlibrary/StoreCPU_StoreTransferCycleSD.lib\
                          DIPLODOCUS/CPlibrary/TerminateDMA_SD.lib\
                          DIPLODOCUS/StoreCPU_StoreConfigureCycleSD.lib\
			  TURTLE/TTimerv01.lib) 
RELEASE_STD_FILES_LICENSES 	= $(patsubst %,$(TTOOL_DOC)/%,\
			     	  LICENSE \
				  LICENSE_CECILL_ENG \
				  LICENSE_CECILL_FR)
TTOOL_EXE 		= $(patsubst %,$(TTOOL_DOC)/%,\
			  ttool_linux.exe \
			  ttool_macosx.exe \
			  ttool_windows.bat)
TTOOL_CONFIG_SRC 	= $(patsubst %,$(TTOOL_DOC)/%,\
			  config_linux.xml \
			  config_macosx.xml \
			  config_windows.xml)

release: $(STDRELEASE)
#$(ADVANCED_RELEASE)

$(TTOOL_STD_RELEASE)/%.tgz: $(TTOOL_STD_RELEASE)/%.tar
	@$(GZIP) -c $< > $@

$(STDRELEASE:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating standard release"
	@cp $< $@
# LOTOS
	@mkdir -p $(TTOOL_TARGET)/lotos
	@cp $(TTOOL_DOC)/README_lotos $(TTOOL_TARGET)/lotos
#NC
	@mkdir -p $(TTOOL_TARGET)/nc
	@cp $(TTOOL_DOC)/README_nc $(TTOOL_TARGET)/nc
# Figures
	@cp $(TTOOL_FIGURES)/Makefile $(TTOOL_TARGET)/figures
	@cp $(TTOOL_FIGURES)/mli.mk $(TTOOL_TARGET)/figures
# JTTool
#	@mkdir -p $(TTOOL_TARGET)/java
#	@cp $(JTTOOL_BINARY) $(TTOOL_TARGET)/java
#	@cp $(TTOOL_DOC)/README_java $(TTOOL_TARGET)/java
# Basic bin
	@cp $(TTOOL_EXE) $(TTOOL_TARGET)/
	@cp $(TTOOL_CONFIG_SRC) $(TTOOL_TARGET)/bin
	@cp $(TTOOL_LOTOS_H) $(TTOOL_TARGET)/bin
	@$(TAR) uf $@ -C $(TTOOL_TARGET_RELEASE) TTool/lotos TTool/nc TTool/bin TTool/figures TTool/doc/prototyping_with_soclib_installation_guide.pdf TTool/doc/prototyping_with_soclib_user_guide.pdf  $(patsubst $(TTOOL_DOC)/%,TTool/%,$(TTOOL_EXE))

$(ADVANCED_RELEASE:.tgz=.tar): $(STDRELEASE:.tgz=.tar) documentation
	@echo "$(PREFIX) Generating advanced release"
	@cp $< $@
	@cp -r $(TTOOL_DOC_HTML) $(TTOOL_TARGET)/doc/srcdoc
	@mkdir -p $(TTOOL_TARGET)/src
	@cp -R $(TTOOL_SRC)/* $(TTOOL_TARGET)/src
	@cp -r $(TTOOL_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(LAUNCHER_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(TTOOLCLI_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(GRAPHMINIMIZE_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(GRAPHSHOW_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(TIFTRANSLATOR_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(TMLTRANSLATOR_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(RUNDSE_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(REMOTESIMULATOR_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(WEBCRAWLER_CLIENT_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(WEBCRAWLER_SERVER_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@find $(TTOOL_TARGET)/src -type f -not \( -name '*.java' -o -name '*.gif' -o -name '*.jjt' -o -name '*.txt' \) -a -exec rm -f {} \;
	@cp $(TTOOL_DOC)/README_src $(TTOOL_TARGET)/src
	@$(TAR) uf $@ -C $(TTOOL_TARGET_RELEASE) TTool/doc/srcdoc TTool/src

$(TTOOL_PREINSTALL_WINDOWS:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating preinstall for Windows"
	@cp $< $@
	@mkdir -p $(TTOOL_TARGET_WINDOWS)/TTool/bin
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/proverif_windows.tar.gz -C $(TTOOL_TARGET_WINDOWS)
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/uppaal.tar.gz -C $(TTOOL_TARGET_WINDOWS)
	@cp $(TTOOL_DOC)/config_windows.xml $(TTOOL_TARGET_WINDOWS)/TTool/bin/
	@sed 's#chdir .*#chdir TTool/bin#' $(TTOOL_DOC)/ttool_windows.bat > $(TTOOL_TARGET_WINDOWS)/ttool.bat
	@cp $(TTOOL_PATH)/build/*.jar $(TTOOL_TARGET_WINDOWS)/TTool/bin/
	@$(TAR) uf $@ -C $(TTOOL_TARGET_WINDOWS) proverif uppaal TTool/bin/config_windows.xml ttool.bat

$(TTOOL_PREINSTALL_MACOS:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating preinstall for MacOS"
	@cp $< $@
	@mkdir -p $(TTOOL_TARGET_MACOS)/TTool/bin
	@$(TAR) xzf $(TTOOL_PRIVATE)/stocks/proverif_macos.tar.gz -C $(TTOOL_TARGET_MACOS)
	@mv $(TTOOL_TARGET_MACOS)/proverif* $(TTOOL_TARGET_MACOS)/proverif
	@$(TAR) xzf $(TTOOL_PRIVATE)/stocks/uppaal_macos.tar.gz -C $(TTOOL_TARGET_MACOS)
	@mv $(TTOOL_TARGET_MACOS)/uppaal* $(TTOOL_TARGET_MACOS)/uppaal
	@cp $(TTOOL_DOC)/config_macosx.xml $(TTOOL_TARGET_MACOS)/TTool/bin/config_macosx.xml
	@sed 's#cd [^;]*#cd TTool/bin#' $(TTOOL_DOC)/ttool_macosx.exe > $(TTOOL_TARGET_MACOS)/ttool.exe
	@chmod u+x $(TTOOL_TARGET_MACOS)/ttool.exe
	@cp $(TTOOL_PATH)/build/*.jar $(TTOOL_TARGET_MACOS)/TTool/bin/
	@$(TAR) uf $@ -C $(TTOOL_TARGET_MACOS) proverif uppaal TTool/bin/config_macosx.xml ttool.exe

$(TTOOL_PREINSTALL_LINUX:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating preinstall for Linux"
	@cp $< $@
	@mkdir -p $(TTOOL_TARGET_LINUX)/TTool/bin
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/proverif_linux.tar.gz -C $(TTOOL_TARGET_LINUX)
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/uppaal.tar.gz -C $(TTOOL_TARGET_LINUX)
	@cp $(TTOOL_DOC)/config_linux.xml $(TTOOL_TARGET_LINUX)/TTool/bin/config_linux.xml
	@sed 's#cd [^;]*#cd TTool/bin#' $(TTOOL_DOC)/ttool_linux.exe > $(TTOOL_TARGET_LINUX)/ttool.exe
	@chmod u+x $(TTOOL_TARGET_LINUX)/ttool.exe
	@cp $(TTOOL_PATH)/build/*.jar $(TTOOL_TARGET_LINUX)/TTool/bin/
	@$(TAR) uf $@ -C $(TTOOL_TARGET_LINUX) proverif uppaal TTool/bin/config_linux.xml ttool.exe

#$(BASERELEASE:.tgz=.tar): $(JTTOOL_BINARY) $(TTOOL_BINARY) $(LAUNCHER_BINARY $(TIFTRANSLATOR_BINARYT) $(TMLTRANSLATOR_BINARY) $(RUNDSE_BINARY) FORCE
$(BASERELEASE:.tgz=.tar): allnotest
	@echo "$(PREFIX) Preparing base release"
	@rm -rf $(TTOOL_TARGET_RELEASE)
	@mkdir -p $(TTOOL_TARGET)
# modeling
	@mkdir -p $(TTOOL_TARGET)/modeling
	@cp $(RELEASE_STD_FILES_XML) $(TTOOL_TARGET)/modeling
	@cp $(TTOOL_DOC)/README_modeling $(TTOOL_TARGET)/modeling
# lib
	@mkdir -p $(TTOOL_TARGET)/lib
	@cp $(RELEASE_STD_FILES_LIB) $(TTOOL_TARGET)/lib
	@cp $(TTOOL_DOC)/README_lib $(TTOOL_TARGET)/lib
# DIPLODOCUS simulators
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/lib
	@cp  $(TTOOL_SIMULATORS)/c++2/lib/README $(TTOOL_TARGET)/simulators/c++2/lib/
	@cp  $(TTOOL_SIMULATORS)/c++2/Makefile $(TTOOL_TARGET)/simulators/c++2
	@cp  $(TTOOL_SIMULATORS)/c++2/Makefile.defs $(TTOOL_TARGET)/simulators/c++2
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.js $(TTOOL_TARGET)/simulators/c++2/src_simulator
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
# Licenses
	@cp $(RELEASE_STD_FILES_LICENSES) $(TTOOL_TARGET)
# Main readme
	@cp $(TTOOL_DOC)/README $(TTOOL_TARGET)
#TML
	@mkdir -p $(TTOOL_TARGET)/tmlcode
	@cp $(TTOOL_DOC)/README_tml $(TTOOL_TARGET)/tmlcode
#UPPAAL
	@mkdir -p $(TTOOL_TARGET)/uppaal
	@cp $(TTOOL_DOC)/README_uppaal $(TTOOL_TARGET)/uppaal
# Proverif
	@mkdir -p $(TTOOL_TARGET)/proverif
	@cp $(TTOOL_DOC)/README_proverif $(TTOOL_TARGET)/proverif
# Graphs
	@mkdir -p $(TTOOL_TARGET)/graphs
	@cp $(TTOOL_DOC)/README_graph $(TTOOL_TARGET)/graphs/
# Figure
	@mkdir -p $(TTOOL_TARGET)/figures
	@cp $(TTOOL_DOC)/README_figure $(TTOOL_TARGET)/figures
# VCD
	@mkdir -p $(TTOOL_TARGET)/vcd
	@cp $(TTOOL_DOC)/README_vcd $(TTOOL_TARGET)/vcd
# Basic doc
	@mkdir -p $(TTOOL_TARGET)/doc
	@cp $(TTOOL_DOC)/README_doc $(TTOOL_TARGET)/doc
# AVATAR executable code
	@mkdir -p $(TTOOL_TARGET)/executablecode
	@mkdir -p $(TTOOL_TARGET)/executablecode/src
	@mkdir -p $(TTOOL_TARGET)/executablecode/generated_src
	@mkdir -p $(TTOOL_TARGET)/executablecode/example
	@cp $(TTOOL_EXECUTABLECODE)/LICENSE* $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/Makefile $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/Makefile.defs $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/Makefile.forsoclib $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/src/*.c $(TTOOL_TARGET)/executablecode/src/
	@cp $(TTOOL_EXECUTABLECODE)/src/*.h $(TTOOL_TARGET)/executablecode/src/
	@cp $(TTOOL_EXECUTABLECODE)/generated_src/README $(TTOOL_TARGET)/executablecode/generated_src/
	@cp $(TTOOL_EXECUTABLECODE)/example/*.java $(TTOOL_TARGET)/executablecode/example/
	@cp $(TTOOL_EXECUTABLECODE)/example/README $(TTOOL_TARGET)/executablecode/example/
# MPSOC
#	@mkdir -p $(TTOOL_TARGET)/MPSoC
#	@mkdir -p $(TTOOL_TARGET)/MPSoC/generated_topcell
#	@mkdir -p $(TTOOL_TARGET)/MPSoC/generated_src
#	@mkdir -p $(TTOOL_TARGET)/MPSoC/src
#	@cp $(TTOOL_MPSOC)/Makefile $(TTOOL_TARGET)/MPSoC/
#	@cp $(TTOOL_MPSOC)/Makefile.defs $(TTOOL_TARGET)/MPSoC/
#	@cp $(TTOOL_MPSOC)/Makefile.forsoclib $(TTOOL_TARGET)/MPSoC/
#	@cp $(TTOOL_MPSOC)/src/*.c $(TTOOL_TARGET)/MPSoC/src/
#	@cp $(TTOOL_MPSOC)/src/*.h $(TTOOL_TARGET)/MPSoC/src/
#	@cp $(TTOOL_MPSOC)/generated_src/README $(TTOOL_TARGET)/MPSoC/generated_src/
#	@cp $(TTOOL_MPSOC)/generated_topcell/nbproc $(TTOOL_TARGET)/MPSoC/generated_topcell/
#	@cp $(TTOOL_MPSOC)/generated_topcell/config_noproc $(TTOOL_TARGET)/MPSoC/generated_topcell/
#DOC
	@$(MAKE) -C $(TTOOL_DOC_SYSMLSEC_DIR) sysmlsec_documentation
	@cp $(TTOOL_DOC_SYSMLSEC_DIR)/build/sysmlsec_documentation.pdf  $(TTOOL_TARGET)/doc/sysmlsec_documentation.pdf
	@cp $(TTOOL_DOC_SYSMLSEC_DIR)/build/sysmlsec_documentation.pdf  $(TTOOL_PRIVATE)/website/ttool/docs/
	@$(MAKE) -C $(TTOOL_DOC_AVATARCODEGENERATION_DIR) codegeneration_documentation
	@cp $(TTOOL_DOC_AVATARCODEGENERATION_DIR)/build/codegeneration_documentation.pdf  $(TTOOL_TARGET)/doc/avatarcodegeneration_documentation.pdf
	@cp $(TTOOL_DOC_AVATARCODEGENERATION_DIR)/build/codegeneration_documentation.pdf  $(TTOOL_PRIVATE)/website/ttool/docs/
	@$(MAKE) -C $(TTOOL_DOC_SOCLIB_USERGUIDE_DIR) user_guide
	@cp $(TTOOL_DOC_SOCLIB_USERGUIDE_DIR)/build/user_guide.pdf  $(TTOOL_TARGET)/doc/prototyping_with_soclib_user_guide.pdf
	@$(MAKE) -C $(TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR) installation_guide
	@cp $(TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR)/build/installation_guide.pdf  $(TTOOL_TARGET)/doc/prototyping_with_soclib_installation_guide.pdf
# Basic bin
	@mkdir -p $(TTOOL_TARGET)/bin
	@cp $(TTOOL_DOC)/README_bin $(TTOOL_TARGET)/bin
	@cp $(TTOOL_BUILD)/*.jar $(TTOOL_TARGET)/bin
	@mkdir -p $(TTOOL_STD_RELEASE)
	@$(TAR) cf $(BASERELEASE) -C $(TTOOL_TARGET_RELEASE) .

publish_jar: $(TTOOL_BINARY)
	@echo "$(PREFIX) Publishing standard and advanced releases"
#	scp $< $(PROD_USERNAME)@$(PROD_ADDRESS):$(PROD_PATH)/
#	ssh $(PROD_USERNAME)@$(PROD_ADDRESS) "chmod a+r $(PROD_PATH)/$(notdir $<)"
	cp $< $(TTOOL_PRIVATE_RELEASES)/

preinstall: $(TTOOL_PREINSTALL_WINDOWS) $(TTOOL_PREINSTALL_LINUX) $(TTOOL_PREINSTALL_MACOS)
	@echo "$(PREFIX) Publishing preinstall versions"
#	scp $^ $(PROD_USERNAME)@$(PROD_ADDRESS):$(PROD_PATH)/
	cp $^ $(TTOOL_PRIVATE_RELEASES)/
	cd $(TTOOL_PRIVATE)/website&&make ttool

git:
	@echo "$(PREFIX) Updating build number"
	@date
	git pull
	@$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
	git commit -m 'update on build version: $(BUILD_INFO)' $(BUILD_INFO) $(BUILD_TO_MODIFY)
	git push

# ======================================== 
# ==========      INSTALL       ========== 
# ======================================== 

DESTDIR ?= $(TTOOL_PATH)

install:
#ttool launcher graphminimize graphshow tiftranslator tmltranslator rundse remotesimulator webcrawler
	mkdir -p $(DESTDIR)/bin
	@cp $(TTOOL_BUILD)/*.jar $(TTOOL_BUILD)/*.xml $(TTOOL_BUILD)/*.h $(TTOOL_BUILD)/*.f  $(TTOOL_BUILD)/*.t $(TTOOL_BUILD)/*.exe  $(DESTDIR)/bin

# ======================================== 
# ==========       TESTS        ========== 
# ======================================== 
test:
	@$(GRADLE) test

noguitest:
	@@for p in $(NO_GUI_TESTS); do \
		echo "\n-----> TESTS FOR:" $$p ;\
		$(GRADLE) test --tests $$p ;\
	done

# ======================================== 
# ==========       CLEAN        ========== 
# ======================================== 
clean:
	@$(MAKE) -C $(TTOOL_DIR) -e clean
	@$(MAKE) -C $(LAUNCHER_DIR) -e clean
	@$(MAKE) -C $(TTOOLCLI_DIR) -e clean
	@$(MAKE) -C $(GRAPHMINIMIZE_DIR) -e clean
	@$(MAKE) -C $(GRAPHSHOW_DIR) -e clean
	@$(MAKE) -C $(TIFTRANSLATOR_DIR) -e clean
	@$(MAKE) -C $(RUNDSE_DIR) -e clean
	@$(MAKE) -C $(REMOTESIMULATOR_DIR) -e clean
	@$(MAKE) -C $(WEBCRAWLER_CLIENT_DIR) -e clean
	@$(MAKE) -C $(WEBCRAWLER_SERVER_DIR) -e clean
	@$(MAKE) -C $(JTTOOL_DIR) -e clean
	@$(MAKE) -C $(TTOOL_DOC_SYSMLSEC_DIR) clean	
	@$(MAKE) -C $(TTOOL_DOC_AVATARCODEGENERATION_DIR) clean
	@$(MAKE) -C $(TTOOL_DOC_SOCLIB_USERGUIDE_DIR) clean
	@$(MAKE) -C $(TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR) clean
	@rm -rf $(TTOOL_TARGET_RELEASE)
	@rm -f $(TTOOL_STD_RELEASE)/*.tar
	@rm -rf $(TTOOL_BUILD)
	@rm -rf $(TTOOL_DIR)/build/

ultraclean: clean
	@rm -rf $(TTOOL_DOC_HTML)
	@rm -rf $(TTOOL_STD_RELEASE)
