
namespace dsx { namespace caba {


#define tmpl(...) __VA_ARGS__ MyHWA

tmpl(/**/)::~MyHWA()
{
}

    //intArray(1,1) there is one element (here an integer) of size 4 OCTETs/Quentin: attention cela a change c'est un MOT dans la version VM

tmpl(/**/)::MyHWA(sc_core::sc_module_name insname)
           :dsx::caba::FifoVirtualCoprocessorWrapper(insname, stringArray(NULL), NULL, stringArray("input", NULL), intArray(1,4))
{
}

tmpl(void *)::task_func() {
  struct mwmr_s *input;
   uint32_t data;
 
   while (true) {   
      for (int32_t i = 0; i < 8; i++) {
	mwmr_read(input, &data, 1); // read 8 integers wich have been modified by the intermediate software task
      }
     
   }
}

}}

