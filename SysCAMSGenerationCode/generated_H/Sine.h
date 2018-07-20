#ifndef SINE_H
#define SINE_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

class Sine : public sca_tdf::sca_module {

public:

	sca_tdf::sca_out<double> tdf_out;

	sca_tdf::sca_de::sca_out<int> out2_de;
	sca_tdf::sca_de::sca_out<int> out_de;

	explicit Sine(sc_core::sc_module_name nm)
	: tdf_out("tdf_out")
	, out2_de("out2_de")
	, out_de("out_de")
	{}

protected:
	void set_attributes() {
		set_timestep(6, sc_core::SC_US
		tdf_out.set_timestep(6, sc_core::SC_US);
		tdf_out.set_rate(1);
		out_de.set_timestep(6, sc_core::SC_US);
		out_de.set_rate(1);
		out_de.set_delay(0);
	}

	void processing() {
		double t = out_de.get_time().to_seconds();
		double x = sin(2.0 * M_PI * 5000000.0 * t);
		out_de.write( (int) x);
		tdf_out.write(x);
	}
     
};

#endif // SINE_H