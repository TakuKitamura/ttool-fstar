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
SRCS_base = app/TMLTask.cpp app/TMLCommand.cpp TMLTransaction.cpp app/TMLChannel.cpp arch/SingleCoreCPU.cpp app/TMLWriteCommand.cpp app/TMLWriteMultCommand.cpp app/TMLStateChannel.cpp app/TMLbrbwChannel.cpp app/TMLnbrnbwChannel.cpp app/TMLbrnbwChannel.cpp app/TMLEventChannel.cpp app/TMLEventBChannel.cpp app/TMLEventFChannel.cpp app/TMLEventFBChannel.cpp app/TMLReadCommand.cpp app/TMLExeciCommand.cpp app/TMLActionCommand.cpp app/TMLChoiceCommand.cpp app/TMLWaitCommand.cpp app/TMLSendCommand.cpp app/TMLSelectCommand.cpp app/TMLRequestCommand.cpp app/TMLNotifiedCommand.cpp app/TMLStopCommand.cpp arch/Bus.cpp definitions.cpp arch/Bridge.cpp arch/Memory.cpp MemPool.cpp Comment.cpp sim/Server.cpp sim/ServerLocal.cpp sim/Simulator.cpp sim/SimComponents.cpp sim/ServerIF.cpp evt/ListenersSimCmd.cpp arch/PrioScheduler.cpp arch/RRScheduler.cpp arch/WorkloadSource.cpp ebrdd/EBRDDCommand.cpp ebrdd/EBRDDActionCommand.cpp ebrdd/EBRDDChoiceCommand.cpp ebrdd/EBRDDStopCommand.cpp ebrdd/EBRDD.cpp ebrdd/ERC.cpp ebrdd/EventIF.cpp ebrdd/ERB.cpp ebrdd/ESO.cpp ebrdd/NotifyIF.cpp
SRCS_base_DIR = src_simulator
SRCS_generated = .

OBJS_simulator = $(SRCS_base:%.cpp=lib/%.o)
OBJS = $(SRCS:%.cpp=lib/%.o)

include Makefile.defs
