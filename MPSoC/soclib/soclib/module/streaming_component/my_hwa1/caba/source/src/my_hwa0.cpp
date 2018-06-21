
namespace dsx { namespace caba {


#define tmpl(...) __VA_ARGS__ MyHWA0

tmpl(/**/)::~MyHWA0()
{
}

    //intArray(1,1) there is one element (here an integer) of size 4 OCTETs/Quentin: attention cela a change c'est un MOT dans la version VM
tmpl(/**/)::MyHWA0(sc_core::sc_module_name insname)
           :dsx::caba::FifoVirtualCoprocessorWrapper(insname, stringArray("output", NULL), intArray(1,4), stringArray(NULL), NULL)
{
}

tmpl(void *)::task_func() {
  struct mwmr_s *output;
//  mwmr_t output = SRL_GET_MWMR(output);
   uint32_t data;
 
   while (true) {   
      for (int32_t i = 0; i < 8; i++) {
	out=i;
	mwmr_write(output, &data, 4; // Write integers 0 to 7 to output
      }
     
   }
}

}}

