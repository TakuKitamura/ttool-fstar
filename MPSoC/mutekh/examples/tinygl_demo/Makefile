
objs = vgafb.o
objs += $(BUILD_DEMO_NAME).o

ifeq ($(CONFIG_ARCH_SOCLIB),defined)
ifneq ($(BUILD_NO_FDT),1)
objs += $(BUILD_platform_name).o
endif
endif
