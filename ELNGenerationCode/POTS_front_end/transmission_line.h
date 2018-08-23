#ifndef TRANSMISSION_LINE_H
#define TRANSMISSION_LINE_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(transmission_line)
{
	sca_eln::sca_terminal a1;
	sca_eln::sca_terminal b1;
	sca_eln::sca_terminal a2;
	sca_eln::sca_terminal b2;

	sca_eln::sca_transmission_line tl0;

	SC_CTOR(transmission_line)
	: a1("a1")
	, b1("b1")
	, a2("a2")
	, b2("b2")
	, tl0("tl0", 50.0, sc_core::SC_ZERO_TIME, 0.0)
	{
		tl0.a1(a1);
		tl0.a2(a2);
		tl0.b1(b1);
		tl0.b2(b2);
	}

private:
};

#endif // TRANSMISSION_LINE_H