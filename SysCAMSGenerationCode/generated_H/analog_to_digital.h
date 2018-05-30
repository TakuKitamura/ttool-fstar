#ifndef ANALOG_TO_DIGITAL_H
#define ANALOG_TO_DIGITAL_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SCA_TDF_MODULE(analog_to_digital) {

	// TDF port declarations
	sca_tdf::sca_in<double> sensorIn;
	// Converter port declarations
	sca_tdf::sca_de::sca_out<int> soclibOut;
	sca_tdf::sca_de::sca_in<int> soclibIn;

	// Constructor
	SCA_CTOR(analog_to_digital)
	: sensorIn("sensorIn")
	, soclibOut("soclibOut")
	, soclibIn("soclibIn")
	{}

	void set_attributes() {
		sensorIn.set_rate(1);
		sensorIn.set_delay(0);
		soclibOut.set_rate(1);
		soclibOut.set_delay(0);
		soclibIn.set_rate(1);
		soclibIn.set_delay(0);
	}

	void processing() {  }

};

#endif // ANALOG_TO_DIGITAL_H