#ifndef BLOCK_DE_0_H
#define BLOCK_DE_0_H

#include <cmath>
#include <iostream>
#include <systemc>

template<int NBits>
class Block_DE_0 : public sca_core::sca_module {

public:
	typedef sc_dt::sc_int<NBits> output;

	struct parameters {
		int hi;
		parameters()
		: hi(4)
	};

	sca_core::sca_out<double> out;
	sca_core::sca_in<bool> clk;

	SC_HAS_PROCESS(Block_DE_0);
	explicit Block_DE_0(sc_core::sc_module_name nm, const parameters& p = parameters())
	: out("out")
	, clk("clk")
	, hi(p.hi)
	{
		SC_METHOD(attributeCode);
		sensitive << clk.pos();
	}

protected:
	void attributeCode() {
	}
   
private:
	const int hi;
};

#endif // BLOCK_DE_0_H
