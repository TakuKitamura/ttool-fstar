CPUCFLAGS=-fsigned-char -mflat
CPUTOOLS=sparc-unknown-elf-

ifeq ($(CONFIG_COMPILE_SOFTFLOAT), defined)
CPUCFLAGS += -msoft-float
endif

ifneq ($(CONFIG_CPU_SPARC_APP_REGS), defined)
CPUCFLAGS += -mno-app-regs
endif

