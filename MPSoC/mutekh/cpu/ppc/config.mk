CPUTOOLS=powerpc-unknown-elf-
CPUCFLAGS=-mstrict-align -fsigned-char -G0

ifeq ($(CONFIG_COMPILE_SOFTFLOAT), defined)
CPUCFLAGS += -msoft-float
endif

ifeq ($(CONFIG_CPU_PPC_SOCLIB), defined)
CPUCFLAGS += -mno-dlmzb
endif

ifeq ($(CONFIG_COMPILE_DEBUG), defined)
CPUCFLAGS += -fno-dwarf2-cfi-asm
endif

