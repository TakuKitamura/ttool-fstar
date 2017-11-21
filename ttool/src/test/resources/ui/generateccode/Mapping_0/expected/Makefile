#In order to compile wpd, please define EMBB_INSTALL, either as an environment
# variable or as a Makefile variable by uncommenting and editing the following
# line:
 EMBB_INSTALL	= /home/libembb2
# where <someplace> is the full path of the directory in which you installed
# libembb. $(EMBB_INSTALL)/include shall contain embb/fep.h and
# $(EMBB_INSTALL)/lib shall contain libembb.so and libembbemu.so
CXX		= g++
LD		= g++
CXXFLAGS	= -c -g -Wall -Wno-unused-variable
ifndef EMBB_INSTALL
all:
	@echo "**************************************************************************************"; \
	echo "* Please define the EMBB_INSTALL environment variable and assign it the absolute path"; \
	echo "* of the libembb install directory. $(EMBB_INSTALL)/include shall contain embb/fep.h"; \
	echo "* and $(EMBB_INSTALL)/lib shall contain libembb.so and libembbemu.so"; \
	echo "**************************************************************************************"; \
	exit 1
else

EMBBINCLUDEDIR	= $(EMBB_INSTALL)/include
EMBBLIBDIR	= $(EMBB_INSTALL)/lib
OBJS	= $(patsubst %.c,%.o,$(wildcard *.c))
HRDS	= $(wildcard *.h)
EXECS	= waveform.x

print-%:
	@echo '$(OBJS)'

all: $(EXECS)

%.o: %.c

	$(CXX) $(CXXFLAGS) $(INCLUDES) -c $<
$(OBJS): INCLUDES += -I$(EMBBINCLUDEDIR)

$(EXECS): LDFLAGS += -L$(EMBBLIBDIR) -Wl,-rpath,$(EMBBLIBDIR)
$(EXECS): LIBS += -lembb -lembbemu
$(EXECS): $(OBJS)
	$(LD) $(LDFLAGS) -o $@ $^ $(LIBS)

clean:
	rm -rf $(OBJS)

ultraclean:
	rm -rf $(OBJS) $(EXECS)
endif