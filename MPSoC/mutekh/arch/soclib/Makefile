
objs = 											\
arch_init.o										\
entry_points.o                                  \
mem_init.o \
power.o

ifneq ($(CONFIG_SOCLIB_EARLY_CONSOLE),undefined)
objs += early_console.o
endif

ifeq ($(CONFIG_HEXO_MMU), defined)
objs += mmu.o
endif

ifeq ($(LINKING),1)

meta = ldscript

endif
