/**************************************************************************
 * File : soclib_reordering_buffer.h
 * Date : 11/02/2005
 * author : Daniela Genius
 * This program is released under the GNU public license
 * Copyright : UPMC - LIP6
 *
***************************************************************************/

#ifndef SOCLIB_REORDERING_BUFFER_H
#define SOCLIB_REORDERING_BUFFER_H

#include <systemc>
#include "alloc_elems.h"

namespace soclib {
  namespace caba {   
using namespace sc_core;
//using soclib::common::alloc_elems;
template<typename data_t, typename DATETYPE> 
class ReorderingBuffer
{
 
  const size_t m_size;
  const uint32_t m_delay;
  const uint32_t m_advance;
  sc_core::sc_signal<data_t>  *DATA[2];
 
  sc_core::sc_signal<int>	 PTR;
  sc_core::sc_signal<int>	 PTW;
  sc_core::sc_signal<int>	 STATE;
  sc_core::sc_signal<bool> *COUNTER;
 public:  
 ReorderingBuffer(size_t size, uint32_t advance,uint32_t delay)
  :m_size(size),
   m_advance(advance),
   m_delay(delay),
   PTR("ptr"),
   PTW("ptw"),
   STATE("state")
  {
    
     using soclib::common::alloc_elems;
 DATA[0] = alloc_elems<sc_core::sc_signal<data_t> >
 ("address", size);                                   
 DATA[1] = alloc_elems<sc_core::sc_signal<data_t> >
   ("data", size);
 COUNTER = alloc_elems<sc_core::sc_signal<bool> >
 ("counter", size);
  }

  ~ReorderingBuffer()
  {
     using soclib::common::dealloc_elems;
    dealloc_elems<sc_core::sc_signal<data_t> >(DATA[0],m_size);
    dealloc_elems<sc_core::sc_signal<data_t> >(DATA[1], m_size);
    dealloc_elems<sc_core::sc_signal<bool> >(COUNTER, m_size);
    }

 

	
///////////////////////
//  method init()
///////////////////////
    void init( uint32_t m_delay, uint32_t m_advance)
     {
        PTR = -m_delay % (m_delay+m_advance);
       	PTW =0;
	STATE = 0;
     }// end init()

///////////////////////
// method simple_put()
///////////////////////
      void simple_put(DATETYPE m, data_t addr,data_t info )
      {
        PTW=m%m_size;
        if (COUNTER[m%m_size]==0) { 
	 	STATE = STATE + 1; 
		DATA[1][m%m_size] = info;
		DATA[0][m%m_size] = addr;
	       	COUNTER[m%m_size]=1;
                             }
 
       } // end simple_put()

///////////////////////
// method set_free()
///////////////////////
      void set_free()
        {
	if (STATE != 0) {
		STATE = STATE - 1;
	        COUNTER[PTR]=0; //remettre le compteur de collisions à zéro 
                //PTR = (PTR + 1)  % (delay+advance);
           	}
        } // end

///////////////////////
// method set_PTR()
///////////////////////
      void set_ptr(int delay, int advance)
        {
          PTR = (PTR + 1)  % (delay+advance);
           	
        } // end


////////////////////////
//  method rok()
///////////////////////
bool rok()
{
  if(STATE != 0)  return(true);
	else	  return(false);
}// end rok()

////////////////////////
//  method wok()  
///////////////////////
bool wok()
{return(true);//on peut toujours écrire!!
}// end wok()



////////////////////////
//  method read_info()  
///////////////////////
data_t read_addr()
{     return(DATA[0][PTR]);
 
   
} // end read()

////////////////////////
//  method read_addr()  
///////////////////////
data_t read_info()
{      return(DATA[1][PTR]);

   
} // end read()
//////////////////////////
// get- ptr
/////////////////////
int get_ptr()
{
  return PTR;


}
//////////////////////////
// get- ptw
/////////////////////
int get_ptw()
{
  return PTW;


}



/////////////////////////
//get_delay
////////////////////
 uint32_t get_delay()
 {
   return m_delay;
}
/////////////////////////
//get_advance
////////////////////
 uint32_t get_advance()
 {
   return m_advance;
}


/////////////////////////
// lecture valide
/////////////////////
bool valide()
{
  if(COUNTER[PTR]== 1) return true;
  else return false;


} 


};//end of class
 }

}

#endif


