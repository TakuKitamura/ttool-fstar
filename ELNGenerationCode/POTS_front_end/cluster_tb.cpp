#include <systemc-ams>
#include "phone.h"
#include "transmission_line.h"
#include "protection_circuit.h"
#include "slic.h"

int sc_main(int argc, char *argv[])
{
	sc_core::sc_signal<bool> s_hook;
	sca_tdf::sca_signal<double> s_voice;
	sca_eln::sca_node n_ring_b1;
	sca_eln::sca_node n_tip_a1;
	sca_tdf::sca_signal<double> s_v2w;
	sca_tdf::sca_signal<double> s_i_trans;
	sca_eln::sca_node n_tip_slic_tip;
	sca_eln::sca_node n_b2_ring;
	sca_eln::sca_node n_a2_tip;
	sca_eln::sca_node n_ring_slic_ring;

	phone i_phone("i_phone");
	i_phone.ring(n_ring_b1);
	i_phone.tip(n_tip_a1);
	i_phone.voice(s_voice);
	i_phone.hook(s_hook);

	transmission_line i_transmission_line("i_transmission_line");
	i_transmission_line.a1(n_tip_a1);
	i_transmission_line.b1(n_ring_b1);
	i_transmission_line.a2(n_a2_tip);
	i_transmission_line.b2(n_b2_ring);

	protection_circuit i_protection_circuit("i_protection_circuit");
	i_protection_circuit.tip(n_a2_tip);
	i_protection_circuit.ring(n_b2_ring);
	i_protection_circuit.tip_slic(n_tip_slic_tip);
	i_protection_circuit.ring_slic(n_ring_slic_ring);

	slic i_slic("i_slic");
	i_slic.tip(n_tip_slic_tip);
	i_slic.ring(n_ring_slic_ring);
	i_slic.i_trans(s_i_trans);
	i_slic.v2w(s_v2w);

	sca_util::sca_trace_file* tfp = sca_util::sca_create_tabular_trace_file("cluster_tb");
	sca_util::sca_trace(tfp, s_hook, "s_hook");
	sca_util::sca_trace(tfp, s_voice, "s_voice");
	sca_util::sca_trace(tfp, n_ring_b1, "n_ring_b1");
	sca_util::sca_trace(tfp, n_tip_a1, "n_tip_a1");
	sca_util::sca_trace(tfp, s_v2w, "s_v2w");
	sca_util::sca_trace(tfp, s_i_trans, "s_i_trans");
	sca_util::sca_trace(tfp, n_tip_slic_tip, "n_tip_slic_tip");
	sca_util::sca_trace(tfp, n_b2_ring, "n_b2_ring");
	sca_util::sca_trace(tfp, n_a2_tip, "n_a2_tip");
	sca_util::sca_trace(tfp, n_ring_slic_ring, "n_ring_slic_ring");

	sc_start(100.0, sc_core::SC_MS);

	sca_util::sca_close_tabular_trace_file(tfp);
	sc_core::sc_stop();
	return 0;
}

