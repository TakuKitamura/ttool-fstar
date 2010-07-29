TARGET_ARCH = linux

CC     = gcc
#CC     = test2.sh
#CC = /packages/gcc_3.2.2/bin/g++32
OPT    = -O1 -pthread
# Solaris
#LOPT   = -O1 -pthread -ldl -lnsl -lsocket
#Linux
LOPT   = -O1 -pthread -ldl -lrt

DEBUG  = -g
OTHER  = -Wall
TRY    = 
CFLAGS = $(OPT) $(OTHER) $(TRY)
LFLAGS = $(LOPT) $(OTHER) $(TRY)
# CFLAGS = $(DEBUG) $(OTHER)
OBJDIR = lib

MODULE = run
SRCS_generated_DIR = generated_src/
include Makefile.src
SRCS_base = src/transactions.c src/syncchannel.c src/timers.c src/myerrors.c src/debug.c src/storeevents.c
SRCS_base_DIR = .
SRCS_base_DIRSRC = src/ 
OBJS_executor = $(SRCS_base:%.c=lib/%.o)
OBJS = $(SRCS:%.c=lib/%.o)

include Makefile.defs
