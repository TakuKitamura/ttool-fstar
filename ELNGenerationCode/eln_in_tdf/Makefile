# Compiler and linker flags
CXXFLAGS = -g -Wall -I. $(SYSTEMC_INCLUDE_DIRS)
LDFLAGS = $(SYSTEMC_LIBRARY_DIRS)

# List of all ecutables to be compiled
EXECUTABLES = cluster_tb

# .PHONY targets don't generate files
.PHONY:	all clean

# Default targets
all:	$(EXECUTABLES)

cluster_tb: cluster_tb.cpp eln_in_tdf.h
	$(CXX) $(CXXFLAGS) $(LDFLAGS) -o $@ $< -lsystemc-ams -lsystemc | c++filt

# Clean rule to delete temporary and generated files
clean:
	rm -rf *~ *.o *.dat *.vcd *.dSYM $(EXECUTABLES)
