/* -*- c++ -*-
  *
  * File : dspin_router.cpp
  * Copyright (c) UPMC, Lip6
  * Authors : Alain Greiner, Abbas Sheibanyrad, Ivan Miro, Zhen Zhang
  *
  * SOCLIB_LGPL_HEADER_BEGIN
  * 
  * This file is part of SoCLib, GNU LGPLv2.1.
  * 
  * SoCLib is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as published
  * by the Free Software Foundation; version 2.1 of the License.
  * 
  * SoCLib is distributed in the hope that it will be useful, but
  * WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  * Lesser General Public License for more details.
  * 
  * You should have received a copy of the GNU Lesser General Public
  * License along with SoCLib; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
  * 02110-1301 USA
  *
  * SOCLIB_LGPL_HEADER_END
  *
  */

#include "../include/dspin_router.h"

namespace soclib { namespace caba {

using namespace soclib::common;
using namespace soclib::caba;

#define tmpl(x) template<int flit_width> x DspinRouter<flit_width>

    ////////////////////////////////////////////////
    //              constructor
    ////////////////////////////////////////////////
    tmpl(/**/)::DspinRouter( sc_module_name name, 
                             const size_t   x,
                             const size_t   y,
                             const size_t   x_width,
                             const size_t   y_width,
                             const size_t   in_fifo_depth,
                             const size_t   out_fifo_depth )
	: soclib::caba::BaseModule(name),

      p_clk( "p_clk" ),
      p_resetn( "p_resetn" ),
      p_in( alloc_elems<DspinInput<flit_width> >("p_in", 5) ),
      p_out( alloc_elems<DspinOutput<flit_width> >("p_out", 5) ),

	  r_alloc_out( alloc_elems<sc_signal<bool> >("r_alloc_out", 5)),
	  r_index_out( soclib::common::alloc_elems<sc_signal<size_t> >("r_index_out", 5)),
	  r_fsm_in( alloc_elems<sc_signal<int> >("r_fsm_in", 5)),
	  r_index_in( alloc_elems<sc_signal<size_t> >("r_index_in", 5)),

      m_local_x( x ),
      m_local_y( y ),
      m_x_width( x_width ),
      m_x_shift( flit_width - x_width ),
      m_x_mask( (0x1 << x_width) - 1 ),
      m_y_width( y_width ),
      m_y_shift(flit_width - x_width - y_width ),
      m_y_mask( (0x1 << y_width) - 1 )
    {
        std::cout << "  - Building DspinRouter : " << name << std::endl;

	    SC_METHOD (transition);
	    dont_initialize();
	    sensitive << p_clk.pos();

   	    SC_METHOD (genMoore);
	    dont_initialize();
	    sensitive  << p_clk.neg();

	    r_fifo_in  = (GenericFifo<internal_flit_t>*)
	                 malloc(sizeof(GenericFifo<internal_flit_t>)*5);
	    r_fifo_out = (GenericFifo<internal_flit_t>*)
	                 malloc(sizeof(GenericFifo<internal_flit_t>)*5);

	    for( size_t i = 0 ; i < 5 ; i++ )
        {
		    std::ostringstream stri;
		    stri << "r_in_fifo_" << i;
	        new(&r_fifo_in[i])  
                GenericFifo<internal_flit_t >(stri.str(), in_fifo_depth);

		    std::ostringstream stro;
		    stro << "r_out_fifo_" << i;
	        new(&r_fifo_out[i]) 
                GenericFifo<internal_flit_t >(stro.str(), out_fifo_depth);
	    }
    } //  end constructor

    //////////////////////////////////////////////////
    tmpl(size_t)::xfirst_route( sc_uint<flit_width> data )
    {
        size_t xdest = (size_t)(data >> m_x_shift) & m_x_mask;
        size_t ydest = (size_t)(data >> m_y_shift) & m_y_mask;
        return (xdest < m_local_x ? DSPIN_WEST : 
               (xdest > m_local_x ? DSPIN_EAST : 
               (ydest < m_local_y ? DSPIN_SOUTH : 
               (ydest > m_local_y ? DSPIN_NORTH : DSPIN_LOCAL))));
    }

    /////////////////////////
    tmpl(void)::print_trace()
    {
        const char* port_name[] = {"NORTH","SOUTH","EAST ","WEST ","LOCAL"};

        std::cout << "DSPIN_ROUTER " << name() << std::hex; 
        for ( size_t out=0 ; out<5 ; out++)  // loop on output ports
        {
            if ( r_alloc_out[out].read() )
            {
                int in = r_index_out[out];
                std::cout << " / " << port_name[in] << " -> " << port_name[out] ;
            }   
        }
        std::cout << std::endl;
    }

    ////////////////////////
    tmpl(void)::transition()
    {
        // Long wires connecting input and output ports
        size_t              req_in[5];         // input ports  -> output ports
        size_t              get_out[5];        // output ports -> input ports
        bool                put_in[5];         // input ports  -> output ports
        internal_flit_t     flit_in[5];        // input ports  -> output ports

        // control signals for the input fifos
	    bool                fifo_in_write[5];
	    bool                fifo_in_read[5];	
	    internal_flit_t     fifo_in_wdata[5];

        // control signals for the output fifos
	    bool                fifo_out_write[5];
	    bool                fifo_out_read[5];
	    internal_flit_t     fifo_out_wdata[5];

	    // Reset 
	    if ( p_resetn == false ) 
        {
	        for(size_t i = 0 ; i < 5 ; i++) 
            {
		        r_alloc_out[i] = false;
		        r_index_out[i] = 0;
		        r_index_in[i]  = 0;
		        r_fsm_in[i]    = INFSM_IDLE;
		        r_fifo_in[i].init();
		        r_fifo_out[i].init();
	        }
            return;
        }

	    // fifos signals default values
	    for(size_t i = 0 ; i < 5 ; i++) 
        {
		    fifo_in_read[i]        = false;
		    fifo_in_write[i]       = p_in[i].write.read();
		    fifo_in_wdata[i].data  = p_in[i].data.read();
		    fifo_in_wdata[i].eop   = p_in[i].eop.read();
         
		    fifo_out_read[i]       = p_out[i].read.read();
		    fifo_out_write[i]      = false;
	    }

        // loop on the output ports:
        // compute get_out[j] depending on the output port state
        // and combining fifo_out[j].wok and r_alloc_out[j]
        for ( size_t j = 0 ; j < 5 ; j++ )
        {
		    if( r_alloc_out[j].read() and (r_fifo_out[j].wok()) ) 
            {
                get_out[j] = r_index_out[j].read();
            }
            else
            {                       
                get_out[j] = 0xFFFFFFFF;  
            }
        }

        // loop on the input ports :
        // The port state is defined by r_fsm_in[i], r_index_in[i] 
        // The req_in[i] computation implements the X-FIRST algorithm.
        // Both put_in[i] and req_in[i] depend on the input port state.

        for ( size_t i = 0 ; i < 5 ; i++ )
        {
            switch ( r_fsm_in[i].read() )
            {
                case INFSM_IDLE:    // no output port allocated
                {
                    put_in[i] = false;
                    if ( r_fifo_in[i].rok() ) // packet available in input fifo
                    {
                        req_in[i]       = xfirst_route( r_fifo_in[i].read().data );
                        r_index_in[i]   = req_in[i];
                        r_fsm_in[i]     = INFSM_REQ;
                    }
                    else
                    {
                        req_in[i] = 0xFFFFFFFF;  // no request
                    }
                    break;
                }
                case INFSM_REQ:   // waiting output port allocation
                {
                    flit_in[i] = r_fifo_in[i].read();
                    put_in[i]  = r_fifo_in[i].rok();
                    req_in[i]  = r_index_in[i];
                    if ( get_out[r_index_in[i].read()] == i ) // first flit transfered
                    {
                        if ( r_fifo_in[i].read().eop ) r_fsm_in[i] = INFSM_IDLE;
                        else                           r_fsm_in[i] = INFSM_ALLOC;
                    }
                    break;
                }
                case INFSM_ALLOC:  // output port allocated
                {
                    flit_in[i] = r_fifo_in[i].read();
                    put_in[i] = r_fifo_in[i].rok();
                    req_in[i] = 0xFFFFFFFF;                 // no request
                    if ( r_fifo_in[i].read().eop and r_fifo_in[i].rok() and 
                         (get_out[r_index_in[i].read()] == i) ) // last flit transfered 
                    {
                        r_fsm_in[i] = INFSM_IDLE;
                    }
                    break;
                }
            } // end switch
        } // end for input ports
                                   
        // loop on the output ports :
	    // The r_alloc_out[j] and r_index_out[j] computation
        // implements the round-robin allocation policy.
        // These two registers implement a 10 states FSM.
	    for( size_t j = 0 ; j < 5 ; j++ ) 
        {
		    if( not r_alloc_out[j].read() )  // not allocated: possible new allocation
            {
		        for( size_t k = r_index_out[j].read() + 1 ; 
                     k < (r_index_out[j] + 6) ; k++) 
                { 
			        size_t i = k % 5;

			        if( req_in[i] == j ) 
                    {
			            r_alloc_out[j] = true;
			            r_index_out[j] = i;
                        break;
                    }
		        } // end loop on input ports
		    } 
            else                            // allocated: possible desallocation
            {
		        if ( flit_in[r_index_out[j]].eop and
                     r_fifo_out[j].wok() and 
                     put_in[r_index_out[j]] ) 
                {
			        r_alloc_out[j] = false;
                }
		    }
		} // end loop on output ports

        // loop on input ports :
	    // fifo_in_read[i] computation (get data from fifo_in[i]
        // (computed here because it depends on get_out[])
	    for( size_t i = 0 ; i < 5 ; i++ ) 
        {
		    if ( r_fsm_in[i].read() != INFSM_IDLE ) 
            {
                fifo_in_read[i] = (get_out[r_index_in[i].read()] == i);
            }
            else
            {
                fifo_in_read[i] = false;
            }
	    }  // end loop on input ports

        // loop on the output ports :
        // The fifo_out_write[j] and fifo_out_wdata[j] computation
        // implements the output port mux.
	    for( size_t j = 0 ; j < 5 ; j++ ) 
        {
		    if( r_alloc_out[j] )  // output port allocated
            {
		        fifo_out_write[j] = put_in[r_index_out[j]];
		        fifo_out_wdata[j]  = flit_in[r_index_out[j]];
            }
        }  // end loop on the output ports

	    //  FIFOS update
	    for(size_t i = 0 ; i < 5 ; i++) 
        {
		    r_fifo_in[i].update(fifo_in_read[i],
                                fifo_in_write[i],
                                fifo_in_wdata[i]);
		    r_fifo_out[i].update(fifo_out_read[i],
                                 fifo_out_write[i],
                                 fifo_out_wdata[i]);
	    }
    } // end transition

    ////////////////////////////////
    //      genMoore
    ////////////////////////////////
    tmpl(void)::genMoore()
    {
        for(size_t i = 0 ; i < 5 ; i++) 
        { 
            // input ports : READ signals
	        p_in[i].read = r_fifo_in[i].wok();
      
            // output ports : DATA & WRITE signals
	        p_out[i].data  = r_fifo_out[i].read().data; 
	        p_out[i].eop   = r_fifo_out[i].read().eop; 
	        p_out[i].write = r_fifo_out[i].rok();
        }
    } // end genMoore

}} // end namespace

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4
