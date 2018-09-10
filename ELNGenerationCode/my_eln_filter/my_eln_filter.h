#ifndef MY_ELN_FILTER_H
#define MY_ELN_FILTER_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(my_eln_filter)
{
	sca_eln::sca_terminal a;
	sca_eln::sca_terminal b;

	sca_eln::sca_c c1;

	sca_eln::sca_r r1;

	SC_CTOR(my_eln_filter)
	: a("a")
	, b("b")
	, c1("c1", 1.0, 0.0)
	, r1("r1", 1.0)
	, gnd("gnd")
	{
		c1.p(gnd);
		c1.n(b);
		r1.p(b);
		r1.n(a);
	}

private:
	sca_eln::sca_node_ref gnd;
};

#endif // MY_ELN_FILTER_H