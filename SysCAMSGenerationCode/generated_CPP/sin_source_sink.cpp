#include <systemc-ams>
#include "sink.h"
#include "sin_source.h"

// Simulation entry point.
int sc_main(int argc, char *argv[]) {

	using namespace sc_core;
	using namespace sca_util;

	// Declare signal to interconnect.
	sca_tdf::sca_signal<double> sig_1("sig_1");

	// Instantiate source and sink as well as bind their ports to the signal.
	sink sink_1("sink_1");
	sink_1.in(sig_1);

	sin_source sin_source_2("sin_source_2");
	sin_source_2.out(sig_1);

	// Configure signal tracing.
	sca_trace_file* tfp = sca_create_tabular_trace_file("sin_source_sink_tb");
	sca_trace(tfp, sig_1, "sig_1");

	// Start simulation.
	sc_start(100.0, SC_MS);

	// Close trace file and stop simulation to enable clean-up by
	// asking SystemC to execute all end_of_simulation() callbacks.
	sca_close_tabular_trace_file(tfp);
	sc_stop();
	return 0;
}

