#ifndef SINK_H
#define SINK_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

class Sink : public sca_tdf::sca_module {

public:

	sca_tdf::sca_in<double> in;

	explicit Sink(sc_core::sc_module_name nm)
	: in("in")
	{}

protected:
	void set_attributes() {
		in.set_rate(1);
	}

	void processing() {
		in.read();
	}
  
};

#endif // SINK_H