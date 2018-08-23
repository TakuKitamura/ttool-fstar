#include <systemc-ams>
#include "my_structural_eln_model.h"

int sc_main(int argc, char *argv[])
{
	sca_eln::sca_node n_out_b;
	sca_eln::sca_node n_in_a;

	my_structural_eln_model i_my_structural_eln_model("i_my_structural_eln_model");
	i_my_structural_eln_model.b(n_out_b);
	i_my_structural_eln_model.a(n_in_a);

	sca_util::sca_trace_file* tfp = sca_util::sca_create_tabular_trace_file("Cluster0_tb");
	sca_util::sca_trace(tfp, n_out_b, "n_out_b");
	sca_util::sca_trace(tfp, n_in_a, "n_in_a");

	sc_start(100.0, sc_core::SC_MS);

	sca_util::sca_close_tabular_trace_file(tfp);
	sc_core::sc_stop();
	return 0;
}

