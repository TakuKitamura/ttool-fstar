CPUTOOLS=nios2-unknown-elf-
CPUCFLAGS= -ffixed-r26

ifeq ($(CONFIG_CPU_NIOS2_MUL), defined)
CPUCFLAGS+= -mhw-mul
endif

ifeq ($(CONFIG_CPU_NIOS2_MULX), defined)
CPUCFLAGS+= -mhw-mulx
endif

ifeq ($(CONFIG_CPU_NIOS2_DIV), defined)
CPUCFLAGS+= -mhw-div
endif

CPUCFLAGS+= -G0 

