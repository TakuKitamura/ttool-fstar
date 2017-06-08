PREFIX			= [JTTOOL]           

LOCAL_SRC_DIR		= $(JTTOOL_DIR)/src/main/java
LOCAL_BUILD_DIR		= $(JTTOOL_DIR)/build/classes/main

LOCAL_JAVA	= $(shell cd $(LOCAL_SRC_DIR); find . -name "*.java")
LOCAL_CLASS	= $(patsubst %.java,%.class,$(LOCAL_JAVA))

CFLAGS		= $(GLOBAL_CFLAGS) -sourcepath $(TTOOL_SRC):$(LOCAL_SRC_DIR) -classpath $(TTOOL_CLASSPATH) -d $(LOCAL_BUILD_DIR)
JARFLAGS	= cf
JARRSRCFLAGS	= uf

.PHONY: compile clean

$(JTTOOL_BINARY): compile
	@echo "$(PREFIX) Archiving"
	@mkdir -p $(TTOOL_BIN)
	@$(JAR) $(JARFLAGS) $@ -C $(LOCAL_BUILD_DIR) .
ifneq ($(strip $(LOCAL_RESOURCES_DIR)),)
	@$(JAR) $(JARRSRCFLAGS) $@ -C $(LOCAL_RESOURCES_DIR) .
endif

compile: $(patsubst %,$(LOCAL_SRC_DIR)/%,$(LOCAL_JAVA)) $(patsubst %,$(TTOOL_SRC)/%,$(GLOBAL_JAVA))
	@echo "$(PREFIX) Compiling"
	@mkdir -p $(LOCAL_BUILD_DIR)
	@$(JAVAC) $(CFLAGS) $(patsubst %,$(LOCAL_SRC_DIR)/%,$(LOCAL_JAVA))

clean:
	@rm -rf $(LOCAL_BUILD_DIR)
