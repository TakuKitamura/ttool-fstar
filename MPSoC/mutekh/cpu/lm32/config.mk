CPUTOOLS=lm32-unknown-elf-
CPUCFLAGS=-fsigned-char -ffixed-r25 -G0

ifeq ($(CONFIG_CPU_LM32_BARREL_SHIFT), defined)
CPUCFLAGS+= -mbarrel-shift-enabled
endif

ifeq ($(CONFIG_CPU_LM32_DIVIDE), defined)
CPUCFLAGS+= -mdivide-enabled
endif

ifeq ($(CONFIG_CPU_LM32_MULTIPLY), defined)
CPUCFLAGS+= -mmultiply-enabled
endif

ifeq ($(CONFIG_CPU_LM32_SIGN_EXTEND), defined)
CPUCFLAGS+= -msign-extend-enabled
endif

