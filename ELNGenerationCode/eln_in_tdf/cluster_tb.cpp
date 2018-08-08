#include <systemc-ams>
#include "eln_in_tdf.h"

int sc_main(int argc, char *argv[])
{
	sca_tdf::sca_signal s_out;
	sca_tdf::sca_signal s_in;

	eln_in_tdf i_eln_in_tdf("i_eln_in_tdf");
	i_eln_in_tdf.in(s_in);
	i_eln_in_tdf.out(s_out);

	sca_util::sca_trace_file* tfp = sca_util::sca_create_tabular_trace_file("cluster_tb");
	sca_util::sca_trace(tfp, s_out, "s_out");
	sca_util::sca_trace(tfp, s_in, "s_in");

	sc_start(100.0, sc_core::SC_MS);

	sca_util::sca_close_tabular_trace_file(tfp);
	sc_core::sc_stop();
	return 0;
}

