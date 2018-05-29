CPUTOOLS=i686-unknown-elf-

CPUCFLAGS=-mno-tls-direct-seg-refs -m32 -malign-double
CPULDFLAGS= -melf_i386

ifeq ($(CONFIG_COMPILE_SOFTFLOAT), defined)
CPUCFLAGS += -msoft-float
endif
