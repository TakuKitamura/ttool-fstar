
DIR_CFLAGS = -D_HEXO_INTTYPES_DEPRECATED

objs =						\
string.o					\
alloc.o						\
intconv.o					\
printf.o					\
scanf.o	        				\
rand.o						\
error.o						\
qsort.o						\
stdlib.o					\
unistd.o					\
formatter.o


ifeq ($(CONFIG_MUTEK_TIMER), defined)
objs += time.o
endif

ifeq ($(CONFIG_LIBC_ASSERT), defined)
objs += assert.o
endif

ifeq ($(CONFIG_LIBC_STREAM), defined)
objs += stdio.o
endif

ifeq ($(CONFIG_LIBC_FORMATTER_FLOAT), defined)
objs += formatter_float.o
endif

doc_headers = alloca.h assert.h ctype.h errno.h fcntl.h limits.h        \
	semaphore.h setjmp.h stdarg.h stdbool.h stddef.h stdint.h       \
	stdio.h stdlib.h string.h time.h unistd.h sys/stat.h            \
	libc/formatter.h
