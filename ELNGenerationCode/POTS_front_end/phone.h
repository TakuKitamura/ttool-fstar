#ifndef PHONE_H
#define PHONE_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(phone)
{
	sca_eln::sca_terminal ring;
	sca_eln::sca_terminal tip;

	sc_core::sc_in<bool> hook;
	sca_tdf::sca_in<double> voice;

	sca_eln::sca_c cr;
	sca_eln::sca_c cp;
	sca_eln::sca_r rr;
	sca_eln::sca_r rs;
	sca_eln::sca_r rp;
	sca_eln::sca_de_vsource sw1;
	sca_eln::sca_de_vsource sw2;
	sca_eln::sca_tdf_vsource mic;

	SC_CTOR(phone)
	: ring("ring")
	, tip("tip")
	, hook("hook")
	, voice("voice")
	, cr("cr", 1.0e-6, 0.0)
	, cp("cp", 115.0e-9, 0.0)
	, rr("rr", 1.0e3)
	, rs("rs", 220.0)
	, rp("rp", 820.0)
	, sw1("sw1", 1.0)
	, sw2("sw2", 1.0)
	, mic("mic", 1.0)
	, wring("wring")
	, w1("w1")
	, w_onhook("w_onhook")
	, w2("w2")
	, w_offhook("w_offhook")
	{
		cr.p(wring);
		cr.n(w_onhook);
		cp.p(w1);
		cp.n(w_offhook);
		rr.p(wring);
		rr.n(ring);
		rs.p(w1);
		rs.n(w2);
		rp.p(w_offhook);
		rp.n(w1);
		sw1.p(tip);
		sw1.n(w_onhook);
		sw1.inp(hook);
		sw2.p(tip);
		sw2.n(w_offhook);
		sw2.inp(hook);
		mic.p(w2);
		mic.n(ring);
		mic.inp(voice);
	}

private:
	sca_eln::sca_node wring;
	sca_eln::sca_node w1;
	sca_eln::sca_node w_onhook;
	sca_eln::sca_node w2;
	sca_eln::sca_node w_offhook;
};

#endif // PHONE_H