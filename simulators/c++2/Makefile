TARGET_ARCH = linux

CC     = g++
#CC     = test2.sh
#CC = /packages/gcc_3.2.2/bin/g++32
OPT    = -O3 -pthread
# Solaris
#LOPT   = -O1 -pthread -ldl -lnsl -lsocket
#Linux
LOPT   = -O3 -ldl
LTHREAD = -pthread

DEBUG  = -g
PROFILE = -pg
OTHER  = -Wall
SOLARIS_SPECIFIC = -lsocket -lnsl
TRY    = -Wno-deprecated
#make  = $(OPT) $(OTHER) $(TRY) 
LFLAGS = $(LOPT) $(OTHER) $(TRY)
LFLAGS_SOLARIS = $(LFLAGS) $(SOLARIS_SPECIFIC)
CFLAGS = $(OTHER)
OBJDIR = lib
OS := $(shell uname)

MODULE = run
include Makefile.src
SRCS_base = app/TMLTask.cpp app/TMLCommand.cpp TMLTransaction.cpp app/TMLChannel.cpp arch/SingleCoreCPU.cpp app/TMLWriteCommand.cpp app/TMLWriteMultCommand.cpp app/TMLStateChannel.cpp app/TMLbrbwChannel.cpp app/TMLnbrnbwChannel.cpp app/TMLbrnbwChannel.cpp app/TMLReadCommand.cpp app/TMLExeciCommand.cpp app/TMLExeciRangeCommand.cpp app/TMLActionCommand.cpp app/TMLChoiceCommand.cpp app/TMLRandomChoiceCommand.cpp app/TMLWaitCommand.cpp app/TMLSendCommand.cpp app/TMLSelectCommand.cpp app/TMLRequestCommand.cpp app/TMLNotifiedCommand.cpp app/TMLRandomCommand.cpp app/TMLStopCommand.cpp arch/Bus.cpp definitions.cpp arch/Bridge.cpp arch/Memory.cpp Comment.cpp sim/Server.cpp sim/ServerLocal.cpp sim/Simulator.cpp sim/SimComponents.cpp sim/ServerIF.cpp evt/ListenersSimCmd.cpp arch/PrioScheduler.cpp arch/RRScheduler.cpp arch/RRPrioScheduler.cpp arch/WorkloadSource.cpp TEPE/AliasConstraint.cpp TEPE/EqConstraint.cpp TEPE/FSMConstraint.cpp TEPE/PropertyConstraint.cpp TEPE/PropertyStateConstraint.cpp TEPE/PropLabConstraint.cpp TEPE/PropRelConstraint.cpp TEPE/SignalConstraint.cpp TEPE/ThreeSigConstraint.cpp TEPE/TimeMMConstraint.cpp TEPE/TimeTConstraint.cpp TEPE/TwoSigConstraint.cpp

SRCS_base_DIR = src_simulator
SRCS_generated = .

OBJS_simulator = $(SRCS_base:%.cpp=lib/%.o)
OBJS = $(SRCS:%.cpp=lib/%.o)

include Makefile.defs
