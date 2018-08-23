#ifndef PROTECTION_CIRCUIT_H
#define PROTECTION_CIRCUIT_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

SC_MODULE(protection_circuit)
{
	sca_eln::sca_terminal tip;
	sca_eln::sca_terminal ring;
	sca_eln::sca_terminal tip_slic;
	sca_eln::sca_terminal ring_slic;

	sca_eln::sca_c cprot1;
	sca_eln::sca_c cprot2;
	sca_eln::sca_r rprot1;
	sca_eln::sca_r rprot2;
	sca_eln::sca_r rprot3;
	sca_eln::sca_r rprot4;

	SC_CTOR(protection_circuit)
	: tip("tip")
	, ring("ring")
	, tip_slic("tip_slic")
	, ring_slic("ring_slic")
	, cprot1("cprot1", 18.0e-9, 0.0)
	, cprot2("cprot2", 18.0e-9, 0.0)
	, rprot1("rprot1", 20.0)
	, rprot2("rprot2", 20.0)
	, rprot3("rprot3", 20.0)
	, rprot4("rprot4", 20.0)
	, n_ring("n_ring")
	, n_tip("n_tip")
	, gnd("gnd")
	{
		cprot1.p(n_tip);
		cprot1.n(gnd);
		cprot2.p(n_ring);
		cprot2.n(gnd);
		rprot1.p(tip);
		rprot1.n(n_tip);
		rprot2.p(tip_slic);
		rprot2.n(n_tip);
		rprot3.p(ring);
		rprot3.n(n_ring);
		rprot4.p(ring_slic);
		rprot4.n(n_ring);
	}

private:
	sca_eln::sca_node n_ring;
	sca_eln::sca_node n_tip;
	sca_eln::sca_node_ref gnd;
};

#endif // PROTECTION_CIRCUIT_H