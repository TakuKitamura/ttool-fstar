#ifndef _HWA1_H
#define _HWA1_H

#include <systemc>

#include "fifo_virtual_copro_wrapper.h"

namespace dsx { namespace caba {

class MyHWA1
    : public dsx::caba::FifoVirtualCoprocessorWrapper
{

    public:
    ~MyHWA1();
    MyHWA1(sc_core::sc_module_name insname);


    private:
    void * task_func(); // Task code

};

}}
#endif

