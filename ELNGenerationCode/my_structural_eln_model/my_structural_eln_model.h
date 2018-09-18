#ifndef MY_STRUCTURAL_ELN_MODEL_H
#define MY_STRUCTURAL_ELN_MODEL_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(my_structural_eln_model)
{
	sca_eln::sca_terminal b;
	sca_eln::sca_terminal a;

	sca_eln::sca_c c1;

	sca_eln::sca_r r1;
	sca_eln::sca_r r2;

	SC_CTOR(my_structural_eln_model)
	: b("b")
	, a("a")
	, c1("c1", 100.0e-6, 0.0)
	, r1("r1", 10.0e3)
	, r2("r2", 100.0)
	, net1("net1")
	, gnd("gnd")
	{
		c1.p(net1);
		c1.n(gnd);
		r1.p(a);
		r1.n(b);
		r2.p(a);
		r2.n(net1);
	}

private:
	sca_eln::sca_node net1;
	sca_eln::sca_node_ref gnd;
};

#endif // MY_STRUCTURAL_ELN_MODEL_H