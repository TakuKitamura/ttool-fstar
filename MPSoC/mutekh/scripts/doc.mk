MKDOC=mkdoc

include $(MUTEK_SRC_DIR)/doc/header_list.mk

include $(MUTEK_SRC_DIR)/scripts/discover.mk

HG_REV:=$(shell cd $(MUTEK_SRC_DIR) && hg summary | grep ^parent | cut -d: -f3 | cut -d' ' -f1)

$(BUILD_DIR)/doc/config.h:
	test -d $(BUILD_DIR)/doc || mkdir -p $(BUILD_DIR)/doc
	perl $(MUTEK_SRC_DIR)/scripts/config.pl --src-path=$(MUTEK_SRC_DIR) --docheader=$@

doc: $(BUILD_DIR)/doc/config.h
	cd $(MUTEK_SRC_DIR) ; \
	$(MKDOC) $(MKDOCFLAGS) doc/gpct.mkdoclib \
	  --mkdoclib-create mutek-api \
	  --mkdoclib-url http://www.mutekh.org/www/mutekh_api/ \
	  --output-path $(BUILD_DIR)/doc \
	  --source-rev $(HG_REV) \
	  -I $(BUILD_DIR) doc/config.h \
	  -I . \
	  -I doc/include \
	  $(subst $(MUTEK_SRC_DIR)/,,$(MKDOC_ARGS)) \
	  $(CPU_HEADER) $(ARCH_HEADER)

.PHONY: doc
