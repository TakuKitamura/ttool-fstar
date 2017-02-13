
objs =											\
arch_init.o										\
mem_init.o \
hw_init.o \
power.o

ifeq ($(CONFIG_ARCH_IBMPC_DMA), defined)
objs += dma-8237.o
endif

ifeq ($(CONFIG_MUTEK_EARLY_CONSOLE), defined)
objs += early_console.o
endif

meta = ldscript
