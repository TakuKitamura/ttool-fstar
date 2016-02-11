UNAME := $(shell uname -s)
CC     = /usr/bin/gcc
#CC     = test2.sh
#CC = /packages/gcc_3.2.2/bin/g++32
OPT    = -O1 -pthread
# Solaris
#LOPT   = -O1 -pthread -ldl -lnsl -lsocket
#Linux
LOPT_UNIX   = -O1 -pthread -ldl -lrt
LOPT_MAC = -O1 -pthread -ldl

DEBUG  = -g
OTHER  = -Wall
TRY    = 
CFLAGS = $(OPT) $(OTHER) $(TRY)
LFLAGS_MAC = $(LOPT_MAC) $(OTHER) $(TRY)
LFLAGS_UNIX = $(LOPT_UNIX) $(OTHER) $(TRY)
# CFLAGS = $(DEBUG) $(OTHER)
OBJDIR = lib

MODULE = run
SRCS_generated_DIR = generated_src/
#SRCS_generated_DIR = generated_src_soclib/
include Makefile.src
#SRCS_base = src_soclib/request.c src_soclib/message.c src_soclib/myerrors.c src_soclib/debug.c src_soclib/syncchannel.c src_soclib/asyncchannel.c src_soclib/request_manager.c src_soclib/random.c src_soclib/mytimelib.c src_soclib/tracemanager.c
SRCS_base = src/request.c src/message.c src/myerrors.c src/debug.c src/syncchannel.c src/asyncchannel.c src/request_manager.c src/random.c src/mytimelib.c src/tracemanager.c
SRCS_base_DIR = .
SRCS_base_DIRSRC = src/ 
#SRCS_base_DIRSRC = src_soclib/ 
OBJS_executor = $(SRCS_base:%.c=lib/%.o)
OBJS = $(SRCS:%.c=lib/%.o)

include Makefile.defs

include Makefile.forsoclib
