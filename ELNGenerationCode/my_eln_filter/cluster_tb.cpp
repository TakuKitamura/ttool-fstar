#include <systemc-ams>
#include "my_eln_filter.h"

int sc_main(int argc, char *argv[])
{
	sca_eln::sca_node n_b_out;
	sca_eln::sca_node n_in_a;

	my_eln_filter i_my_eln_filter("i_my_eln_filter");
	i_my_eln_filter.a(n_in_a);
	i_my_eln_filter.b(n_b_out);

	sca_util::sca_trace_file* tfp = sca_util::sca_create_tabular_trace_file("cluster_tb");
	sca_util::sca_trace(tfp, n_b_out, "n_b_out");
	sca_util::sca_trace(tfp, n_in_a, "n_in_a");

	sc_start(100.0, sc_core::SC_MS);

	sca_util::sca_close_tabular_trace_file(tfp);
	sc_core::sc_stop();
	return 0;
}

