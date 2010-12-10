TARGET_ARCH = linux

CC     = g++
#CC     = test2.sh
#CC = /packages/gcc_3.2.2/bin/g++32
OPT    = -O1 -pthread
# Solaris
#LOPT   = -O1 -pthread -ldl -lnsl -lsocket
#Linux
LOPT   = -O1 -pthread -ldl

DEBUG  = -g
OTHER  = -Wall
TRY    = -Wno-deprecated
CFLAGS = $(OPT) $(OTHER) $(TRY)
LFLAGS = $(LOPT) $(OTHER) $(TRY)
# CFLAGS = $(DEBUG) $(OTHER)
OBJDIR = lib

MODULE = run
include Makefile.src
SRCS_base = app/AvActionCmd.cpp app/AvAsyncSignal.cpp app/AvAsyncSignalB.cpp app/AvChoiceCmd.cpp app/AvReceiveCmd.cpp app/AvSendCmd.cpp app/AvState.cpp app/AvSyncSignal.cpp app/AvTimerExpCmd.cpp app/AvTransition.cpp app/AvBlock.cpp app/AvNode.cpp ./definitions.cpp sim/Simulator.cpp sim/Server.cpp sim/SimComponents.cpp
SRCS_base_DIR = src_simulator
SRCS_generated = .

OBJS_simulator = $(SRCS_base:%.cpp=lib/%.o)
OBJS = $(SRCS:%.cpp=lib/%.o)

include Makefile.defs
