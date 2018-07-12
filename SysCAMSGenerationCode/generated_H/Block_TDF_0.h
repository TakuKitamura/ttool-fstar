#ifndef BLOCK_TDF_0_H
#define BLOCK_TDF_0_H

#include <cmath>
#include <iostream>
#include <systemc-ams>

class Block_TDF_0 : public sca_tdf::sca_module {

public:

	sca_tdf::sca_in<int> port2;

	sca_tdf::sca_de::sca_out<int> port1;

	explicit Block_TDF_0(sc_core::sc_module_name nm)
	: port2("port2")
	, port1("port1")
	{}

protected:
	void processing() {
	}
   
};

#endif // BLOCK_TDF_0_H