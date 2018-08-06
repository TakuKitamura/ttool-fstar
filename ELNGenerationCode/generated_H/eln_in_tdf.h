#ifndef ELN_IN_TDF_H
#define ELN_IN_TDF_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(eln_in_tdf)
{
	sca_tdf::sca_in<double> in;
	sca_tdf::sca_out<double> out;

	sca_eln::sca_c c;
	sca_eln::sca_r r;
	sca_eln::sca_tdf_vsink vout;
	sca_eln::sca_tdf_vsource vin;

	SC_CTOR(eln_in_tdf)
	: in("in")
	, out("out")
	, c("c", 1.0, 0.0)
	, r("r", 1.0)
	, vout("vout", 1.0)
	, vin("vin", 1.0)
	, n2("n2")
	, n1("n1")
	, gnd("gnd")
	{
		c.p(n2);
		c.n(gnd);
		r.p(n1);
		r.n(n2);
		vout.p(n2);
		vout.n(gnd);
		vout.outp(out);
		vin.p(n1);
		vin.n(gnd);
		vin.inp(in);
	}

private:
	sca_eln::sca_node n2;
	sca_eln::sca_node n1;
	sca_eln::sca_node_ref gnd;
};

#endif // ELN_IN_TDF_H