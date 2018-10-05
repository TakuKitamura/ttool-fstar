PREFIX			= [$(COLOR)GRAPHMINIMIZE$(RESET)]    

MANIFEST		= $(GRAPHMINIMIZE_DIR)/manifest.txt

DEPEND			= $(patsubst %,$(TTOOL_BUILD)/%,$(shell cat $(MANIFEST) | grep Class-Path | cut -d: -f2))

LOCAL_SRC_DIR		= $(GRAPHMINIMIZE_DIR)/src/main/java
LOCAL_BUILD_DIR		= $(GRAPHMINIMIZE_DIR)/build/classes/main

LOCAL_JAVA	= $(shell cd $(LOCAL_SRC_DIR); find . -name "*.java")
LOCAL_CLASS	= $(patsubst %.java,%.class,$(LOCAL_JAVA))

CFLAGS		= $(GLOBAL_CFLAGS) -sourcepath $(TTOOL_SRC):$(LOCAL_SRC_DIR) -classpath $(TTOOL_CLASSPATH) -d $(LOCAL_BUILD_DIR)
JARFLAGS	= cmf
JARRSRCFLAGS	= uf

.PHONY: compile clean

$(GRAPHMINIMIZE_BINARY): compile $(DEPEND)
	@echo "$(PREFIX) Archiving"
	@mkdir -p $(TTOOL_BUILD)
	@$(JAR) $(JARFLAGS) $(MANIFEST) $@ -C $(LOCAL_BUILD_DIR) .
ifneq ($(strip $(LOCAL_RESOURCES_DIR)),)
	@$(JAR) $(JARRSRCFLAGS) $@ -C $(LOCAL_RESOURCES_DIR) .
endif

$(TTOOL_BUILD)/%.jar: $(TTOOL_LIBS)/%.jar
	@mkdir -p $(TTOOL_BUILD)
	@cp $< $@

compile: $(patsubst %,$(LOCAL_SRC_DIR)/%,$(LOCAL_JAVA)) $(patsubst %,$(TTOOL_SRC)/%,$(GLOBAL_JAVA))
	@echo "$(PREFIX) Compiling"
	@mkdir -p $(LOCAL_BUILD_DIR)
	@$(JAVAC) $(CFLAGS) $(patsubst %,$(LOCAL_SRC_DIR)/%,$(LOCAL_JAVA))

clean:
	@rm -rf $(LOCAL_BUILD_DIR)
