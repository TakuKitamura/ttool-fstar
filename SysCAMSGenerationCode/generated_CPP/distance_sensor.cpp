#include <systemc-ams>
#include "analog_to_digital.h"

// Simulation entry point.
int sc_main(int argc, char *argv[]) {

	using namespace sc_core;
	using namespace sca_util;

	// Declare signal to interconnect.

	// Instantiate source and sink as well as bind their ports to the signal.
	analog_to_digital analog_to_digital_1("analog_to_digital_1");
	analog_to_digital_1.sensorIn(
	// Configure signal tracing.
	sca_trace_file* tfp = sca_create_tabular_trace_file("distance_sensor_tb");


	// Start simulation.
	sc_start(100.0, SC_MS);

	// Close trace file and stop simulation to enable clean-up by
	// asking SystemC to execute all end_of_simulation() callbacks.
	sca_close_tabular_trace_file(tfp);
	sc_stop();
	return 0;
}

