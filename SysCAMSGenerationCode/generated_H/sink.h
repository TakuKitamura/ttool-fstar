#ifndef SINK_H
#define SINK_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SCA_TDF_MODULE(sink) {

	// TDF port declarations
	sca_tdf::sca_in<double> in;

	// Constructor
	SCA_CTOR(sink)
	: in("in")
	{}

	void set_attributes() {
		set_timestep(0, sc_core::SC_MS);
		in.set_timestep(0, sc_core::SC_US);
		in.set_rate(0);
		in.set_delay(0);
	}

	void processing() {  using namespace std;  cout << this->name() << " @ " << this->get_time() << ": "     << in.read() << endl; } 

};

#endif // SINK_H