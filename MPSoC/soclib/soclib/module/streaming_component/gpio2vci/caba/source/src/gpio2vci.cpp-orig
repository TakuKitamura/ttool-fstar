#include "../include/gpio2vci.h"
#include <iostream>

namespace soclib {
namespace caba {
  
#define tmpl(x) template<typename vci_param> x Gpio2Vci<vci_param>

tmpl(/**/)::Gpio2Vci( sc_module_name                     insname,
                     const soclib::common::IntTab       &index,
                     const soclib::common::MappingTable &mt )
        : soclib::caba::BaseModule(insname),
          m_segment(mt.getSegment(index)),
          p_clk("p_clk"), 
          p_resetn("p_resetn"),
          p_vci("p_vci"),
          p_rdata_ams("p_rdata_ams"),
          p_wdata_ams("p_wdata_ams") {
    std::cout << "  - Building Gpio2Vci " << insname << std::endl;      
    
    SC_METHOD(transition);
    sensitive << p_clk.pos();
    SC_METHOD (genMoore);
    sensitive << p_clk.neg();
}

tmpl(/**/)::~Gpio2Vci(){}

tmpl(void)::transition() {
    if(p_resetn == false) {
        r_fsm_state = TARGET_IDLE;
    }
    else {
        switch( r_fsm_state ) {
        case TARGET_IDLE:
            if( p_vci.cmdval.read() ) {
                r_buf_eop = p_vci.eop.read();
                if ( p_vci.cmd.read() == vci_param::CMD_WRITE ) { //CMD_WRITE ) {
                    r_wdata_ams = p_vci.wdata.read();
                    printf("IDLE CMD_WRITE: %d\n", (int)p_vci.wdata.read());
                    r_fsm_state = TARGET_WRITE;
                }
                else {  //VCI_CMD_READ
                    cout << "@" << sc_time_stamp() << ": ";
                    printf("Gpio2Vci_IDLE p_rdata_ams=%d\n",(int)p_rdata_ams.read());
                    r_rdata_ams = p_rdata_ams.read();
                    r_fsm_state = TARGET_READ;
                }
            }
            break;

        case TARGET_WRITE:
        case TARGET_READ:
            if( p_vci.rspack.read() ) {
                printf("READ-WRITE rspack \n");
                r_fsm_state = TARGET_IDLE;
            }
            break;
        }
    }
      
}

tmpl(void)::genMoore() {
    switch (r_fsm_state) {
    case TARGET_IDLE:
        p_vci.rspNop();
        break;
    case TARGET_WRITE:
        p_vci.rspWrite( r_buf_eop.read() );
        p_wdata_ams.write(r_wdata_ams);
        break;
    case TARGET_READ:
        p_vci.rspRead( r_buf_eop.read(), r_rdata_ams );
        break;
    }   
    // We only accept commands in Idle state
    p_vci.cmdack = (r_fsm_state == TARGET_IDLE);
}

}
}
