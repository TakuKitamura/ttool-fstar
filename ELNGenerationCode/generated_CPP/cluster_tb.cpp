#include <systemc-ams>
#include "eln_in_tdf.h"

int sc_main(int argc, char *argv[])
{
	sca_eln::sca_node out;
	sca_eln::sca_node in;

	eln_in_tdf i_eln_in_tdf("i_eln_in_tdf");
	i_eln_in_tdf.in(in);
	i_eln_in_tdf.out(out);

	sca_util::sca_trace_file* tfp = sca_util::sca_create_tabular_trace_file("cluster_tb");
	sca_util::sca_trace(tfp, out, "out");
	sca_util::sca_trace(tfp, in, "in");

	sc_start(100.0, sc_core::SC_MS);

	sca_util::sca_close_tabular_trace_file(tfp);
	sc_core::sc_stop();
	return 0;
}

