#ifndef GPIO2VCI_H
#define GPIO2VCI_H

#define sc_register sc_signal

#include <signal.h>
#include <stdlib.h>
#include <systemc.h>
//#include <communication/vci/caba/source/include/vci_target.h>
//#include <lib/base_module/include/base_module.h>
//#include <lib/mapping_table/include/mapping_table.h>
#include "caba_base_module.h"
#include "vci_target.h"
#include "mapping_table.h"

//#include "vci_param.h"

namespace soclib {
namespace caba {

template <typename vci_param>
class Gpio2Vci 
      : public soclib::caba::BaseModule {    

    sc_register< typename vci_param::data_t > r_rdata_ams, r_wdata_ams;
    sc_register<int> r_fsm_state, r_buf_eop;
    
    enum fsm_state_e {
      TARGET_IDLE = 0,
      TARGET_WRITE,
      TARGET_READ,
    };

    const soclib::common::Segment   m_segment;    

protected:
    SC_HAS_PROCESS(Gpio2Vci);

public:
    //Ports
    sc_in<bool> p_clk;
    sc_in<bool> p_resetn;
    soclib::caba::VciTarget<vci_param>  p_vci;
    sc_in< typename vci_param::data_t > p_rdata_ams;
    sc_out< typename vci_param::data_t > p_wdata_ams;
    
    Gpio2Vci( sc_module_name                insname,
        const soclib::common::IntTab       &index,
        const soclib::common::MappingTable &mt );

    ~Gpio2Vci();

private:
    void transition();
    void genMoore();

};


}
}

#endif // GPIO2VCI_H
