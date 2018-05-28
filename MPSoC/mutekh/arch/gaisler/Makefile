
objs = arch_init.o

ifneq ($(CONFIG_GAISLER_EARLY_CONSOLE),undefined)
objs += early_console.o
endif

ifeq ($(LINKING),1)
meta = ldscript
endif

