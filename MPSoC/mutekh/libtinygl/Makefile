
DIR_CFLAGS = -D_HEXO_INTTYPES_DEPRECATED

objs = api.o list.o vertex.o init.o matrix.o texture.o \
	   misc.o clear.o light.o clip.o select.o get.o error.o \
	   zbuffer.o zline.o zdither.o ztriangle.o \
	   zmath.o image_util.o msghandling.o \
	   arrays.o specbuf.o memory.o

ifeq ($(CONFIG_LIBTINYGL_VGAFB), defined)
objs += vgafb.o
endif

