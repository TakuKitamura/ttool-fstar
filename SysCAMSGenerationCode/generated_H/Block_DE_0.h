#ifndef BLOCK_DE_0_H
#define BLOCK_DE_0_H

#include <cmath>
#include <iostream>
#include <systemc>

class Block_DE_0 : public sca_core::sca_module {

public:

	sca_core::sca_in<bool> clk;
	sca_core::sca_out<double> out;

	SC_HAS_PROCESS(Block_DE_0);
	explicit Block_DE_0(sc_core::sc_module_name nm)
	: clk("clk")
	, out("out")
	{
		SC_METHOD(attributeCode);
		sensitive << clk.pos()
	}

	void attributeCode() {

	}
};

#endif // BLOCK_DE_0_H