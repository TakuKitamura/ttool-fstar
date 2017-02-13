CPUTOOLS=i686-unknown-elf-

ifeq ($(CONFIG_ARCH_EMU_DARWIN), defined)
CPUCFLAGS= -fno-stack-protector -m32
else
ifeq ($(CONFIG_ARCH_EMU_LINUX),defined)
CPUCFLAGS=-mno-tls-direct-seg-refs -m32
CPULDFLAGS=-melf_i386
endif
endif

CPUCFLAGS+= -malign-double

ifeq ($(CONFIG_COMPILE_SOFTFLOAT), defined)
CPUCFLAGS += -msoft-float
endif
