TARGET_ARCH = linux

CC     = g++
#CC     = test2.sh
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
SRCS_base = TMLTask.cpp TMLCommand.cpp TMLTransaction.cpp TMLChannel.cpp CPU.cpp CPURR.cpp CPUPB.cpp CPUPBL.cpp TMLWriteCommand.cpp TMLStateChannel.cpp TMLbrbwChannel.cpp TMLnbrnbwChannel.cpp TMLbrnbwChannel.cpp TMLEventChannel.cpp TMLEventBChannel.cpp TMLEventFChannel.cpp TMLEventFBChannel.cpp TMLReadCommand.cpp TMLExeciCommand.cpp TMLActionCommand.cpp TMLChoiceCommand.cpp TMLWaitCommand.cpp TMLSendCommand.cpp TMLSelectCommand.cpp TMLRequestCommand.cpp TMLNotifiedCommand.cpp Bus.cpp simkern.cpp definitions.cpp
SRCS_base_DIR = src_simulator
SRCS_generated = .

OBJS_simulator = $(SRCS_base:%.cpp=lib/%.o)
OBJS = $(SRCS:%.cpp=lib/%.o)

include Makefile.defs
