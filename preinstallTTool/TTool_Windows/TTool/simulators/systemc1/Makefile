TARGET_ARCH = linux

CC     = g++
#CC = /packages/gcc_3.2.2/bin/g++32
OPT    = -O1
DEBUG  = -g
OTHER  = -Wall
TRY    = -Wno-deprecated
CFLAGS = $(OPT) $(OTHER) $(TRY)
# CFLAGS = $(DEBUG) $(OTHER)
OBJDIR = lib

MODULE = run
include Makefile.src
SRCS_base =  node_labsoc.cpp   channel_labsoc.cpp  event_labsoc.cpp interface_labsoc.cpp cpu_labsoc.cpp task_labsoc.cpp cpurr_labsoc.cpp cpurrpb_labsoc.cpp  mem_labsoc.cpp 
SRCS_base_DIR = src_simulator
SRCS_generated = .

OBJS_simulator = $(SRCS_base:%.cpp=lib/%.o)
OBJS = $(SRCS:%.cpp=lib/%.o)

include Makefile.defs
