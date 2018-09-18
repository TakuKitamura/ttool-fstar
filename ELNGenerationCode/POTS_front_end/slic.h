#ifndef SLIC_H
#define SLIC_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(slic)
{
	sca_eln::sca_terminal tip;
	sca_eln::sca_terminal ring;

	sca_tdf::sca_out<double> i_trans;
	sca_tdf::sca_in<double> v2w;

	sca_eln::sca_r rtr;
	sca_eln::sca_vccs mirror2;
	sca_eln::sca_vccs mirror1;
	sca_eln::sca_tdf_vsink itr_meas;
	sca_eln::sca_tdf_vsource driver1;
	sca_eln::sca_tdf_vsource driver2;

	SC_CTOR(slic)
	: tip("tip")
	, ring("ring")
	, i_trans("i_trans")
	, v2w("v2w")
	, rtr("rtr", 1.0)
	, mirror2("mirror2", -0.5)
	, mirror1("mirror1", 0.5)
	, itr_meas("itr_meas", 1.0)
	, driver1("driver1", 0.5)
	, driver2("driver2", 0.5)
	, n_tri_i("n_tri_i")
	, n_ring_gnd("n_ring_gnd")
	, n_tip_gnd("n_tip_gnd")
	, gnd("gnd")
	{
		rtr.p(n_tri_i);
		rtr.n(gnd);
		mirror2.ncp(n_ring_gnd);
		mirror2.np(n_tri_i);
		mirror2.ncn(gnd);
		mirror2.nn(gnd);
		mirror1.ncp(n_tip_gnd);
		mirror1.np(n_tri_i);
		mirror1.ncn(gnd);
		mirror1.nn(gnd);
		itr_meas.p(n_tri_i);
		itr_meas.n(gnd);
		itr_meas.outp(i_trans);
		driver1.p(tip);
		driver1.n(n_tip_gnd);
		driver1.inp(v2w);
		driver2.p(ring);
		driver2.n(n_ring_gnd);
		driver2.inp(v2w);
	}

private:
	sca_eln::sca_node n_tri_i;
	sca_eln::sca_node n_ring_gnd;
	sca_eln::sca_node n_tip_gnd;
	sca_eln::sca_node_ref gnd;
};

#endif // SLIC_H