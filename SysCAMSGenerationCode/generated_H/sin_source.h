#ifndef SIN_SOURCE_H
#define SIN_SOURCE_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SCA_TDF_MODULE(sin_source) {

	// TDF port declarations
	sca_tdf::sca_out<double> out;

	// Constructor
	SCA_CTOR(sin_source)
	: out("out")
	{}

	void set_attributes() {
		set_timestep(0, sc_core::SC_MS);
		out.set_timestep(1, sc_core::SC_US);
		out.set_rate(0);
		out.set_delay(0);
	}

	void processing() {  double t = out.get_time().to_seconds();  double x = 1.5 * sin(2.0 * M_PI * 50.0 * t);  out.write(x); }

};

#endif // SIN_SOURCE_H