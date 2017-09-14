#include "my_hwa0.h"

namespace dsx { namespace caba {


#define tmpl(...) __VA_ARGS__ MyHWA0

tmpl(/**/)::~MyHWA0()
{
}

tmpl(/**/)::MyHWA0(sc_core::sc_module_name insname)
           :dsx::caba::FifoVirtualCoprocessorWrapper(insname, stringArray("output", NULL), intArray(1, 8), stringArray("input", NULL), intArray(1, 8))
{
}

tmpl(void *)::task_func() {
   srl_mwmr_t input = SRL_GET_MWMR(input);
   srl_mwmr_t output = SRL_GET_MWMR(output);

   uint32_t in0[8];
   uint32_t in1[8];
   uint32_t out[8];

   while (true) {
     // srl_mwmr_read(input0, &in0, 1); // Read 8 words from input0, i.e. 1 item since the fifo is 8-word wide
      //srl_mwmr_read(input1, &in1, 1); // Read 8 words from input1
 srl_mwmr_read(input, &in0, 1); //DG 4.9. corrige
 srl_mwmr_read(input, &in1, 1); //DG 4.9. corrige
      for (int32_t i = 0; i < 8; i++) {
         out[i] = in0[i] + in1[i];
      }
      srl_busy_cycles(2); // The computation takes 2 cycles
      srl_mwmr_write(output, &out, 1); // Write 8 words to output
   }
}

}}
