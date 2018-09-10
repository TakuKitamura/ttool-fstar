#ifndef _HWA_H
#define _HWA_H

#include <systemc>

#include "fifo_virtual_copro_wrapper.h"

namespace dsx { namespace caba {

class MyHWA
    : public dsx::caba::FifoVirtualCoprocessorWrapper
{

    public:
    ~MyHWA();
    MyHWA(sc_core::sc_module_name insname);


    private:
    void * task_func(); // Task code

};

}}
#endif

