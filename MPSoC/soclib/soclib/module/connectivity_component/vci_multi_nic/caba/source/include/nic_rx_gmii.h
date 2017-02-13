/* -*- c++ -*-
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
 * Copyright (c) UPMC, Lip6
 *         Alain Greiner <alain.greiner@lip6.fr> July 2008
 *         Clement Devigne <clement.devigne@etu.upmc.fr>
 *         Sylvain Leroy <sylvain.leroy@lip6.fr>
 *         Cassio Fraga <cassio.fraga@lip6.fr>
 *
 * Maintainers: alain
 */

/*************************************************************************
 * File         : gmii_rx.h
 * Date         : 01/06/2012
 * Authors      : Alain Greiner
 *************************************************************************
 * This object implements a packet receiver, acting as a PHY component,
 * and respecting the GMII protocol (one byte per cycle).
 * It reads packets in a file defined by the "path" constructor argument.
 * The inter-packet gap is another constructor argument.
 *************************************************************************
 * This object has 3 constructor parameters:
 * - string   name  : module name
 * - string   path  : file pathname
 * - uint32_t gap   : number of cycles between packets
 *************************************************************************/

#ifndef SOCLIB_CABA_GMII_RX_H
#define SOCLIB_CABA_GMII_RX_H

#include <inttypes.h>
#include <systemc>
#include <assert.h>

#include <string>
#include <cstring>
#include <iostream>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <fstream>


namespace soclib {
namespace caba {

using namespace sc_core;

////////////////
class NicRxGmii
{
    // structure constants
    const std::string   m_name;
    const uint32_t	    m_gap;
    std::ifstream       m_file;

    // registers
    bool                r_fsm_gap;      // inter_packet state when true
    uint32_t            r_counter;      // cycles counter (used for both gap and plen)
    uint8_t*	        r_buffer;       // local buffer containing one packet
    uint32_t            r_plen;         // packet length (in bytes)

    ///////////////////////////////////////////////////////////////////
    // This function is used to convert ascii to hexa.
    ///////////////////////////////////////////////////////////////////

    uint8_t atox (uint8_t el)
    {
        if((el >= 48) and (el <= 57))
            return (el - 48);
        else if ((el >= 65) and (el <= 70))
            return ((el - 65)+10);
        else if((el >= 97) and (el <= 102))
            return ((el-97)+10);
        else
            return -1;
    }

    ///////////////////////////////////////////////////////////////////
    // This function is used to read one packet from the input file
    // It updates the r_buffer and the r_plen variables.
    ///////////////////////////////////////////////////////////////////
    void read_one_packet()
    {
        uint32_t      data = 0;
        std::string   string;

        // We check that the file is open
        if (m_file)
            {
                // string contains one packet and r_plen contains number of byres
                m_file >> r_plen >> string;

                // check end of file and restart it
                if (m_file.eof())
                    {
                        m_file.clear();
                        m_file.seekg(0, std::ios::beg);
                        m_file >> r_plen >> string;
                    }

                // This version accepts packets containing preamble+SFD or only SFD
                // Preamble consumption
                /*!
                 * \attention 0xD5 as preamble is mandatory, at least
                 */
                size_t preamb_cpt = 0;  //counts bytes on the preamble

                while (atox(string[2*preamb_cpt])==0x5 and atox(string[2*preamb_cpt+1])==0x5)
                    {
                        preamb_cpt ++;
                    }
                // SFD consumption
                if (atox(string[2*preamb_cpt])==0xD and atox(string[2*preamb_cpt+1])==0x5)
                    {
                        preamb_cpt ++;
                    }
                else
                    {
                        // Packet without SFD
                        std::cout << "RX_GMII ERROR: Packets must contain SFD byte 0xD5"
                                  << std::endl;
                        exit(0);
                    }
                // Because r_plen from the file adds preamble, we remove it from the value
                r_plen = r_plen - preamb_cpt;

                // convert two hexadecimal characters into one uint8_t
                for (size_t n = 0; n < (r_plen << 1) ; n++)
                    {
                        data = (data << 4)| atox(string[n+2*preamb_cpt]);
                        if (n%2)
                            {
                                r_buffer[n>>1] = data;
                                data = 0;
                            }
                    }

                /*!
                 * \ attention Maybe something need to be done here to support not 4B aligned packets length
                 */

            }
    } // end read_one packet()

public:

    /////////////
    void reset()
    {
        r_fsm_gap   = true;
        r_counter   = m_gap;
        memset(r_buffer,0,2048);
    }

    ///////////////////////////////////////////////////////////////////
    // To reach the 1 Gbyte/s throughput, this method must be called
    // at all cycles of a 125MHz clock.
    // It is therefore written as a transition and contains a
    // two states FSM to introduce the inter-packet waiting cycles.
    ///////////////////////////////////////////////////////////////////
    void get( bool*     dv,         // data valid
              bool*     er,         // data error
              uint8_t*  dt  )       // data value
    {
        if ( r_fsm_gap )    // inter-packet state
            {
                *dv = false;
                *er = false;
                *dt = 0;

                r_counter = r_counter - 1;

                if (r_counter == 0 ) // end of gap
                    {
                        r_fsm_gap = false;
                        read_one_packet();
                    }
            }
        else    // running packet
            {
                *dv = true;
                *er = false;
                *dt = r_buffer[r_counter];

                r_counter = r_counter + 1;

                if ( r_counter == r_plen ) // end of packet
                    {
                        r_counter   = m_gap;
                        r_fsm_gap   = true;
                    }
            }
    } // end get()

    /*!
     * \brief This method returns true if the RX chain is to be frozen.
     */
    bool frz()
    {
        return false;
    }

    /*!
     * \brief constructor
     */
    NicRxGmii(const std::string  &name,
              const std::string  &path,
              uint32_t           gap)
        : m_name(name),
          m_gap(gap),
          m_file(path.c_str())
    {
        r_buffer        = new uint8_t[2048];

        if (m_file)
            std::cout << "input file = " << path << std::endl;
        else
            std::cout << "ERROR in RX_GMII : cannot open file " << path << std::endl;
    } // end constructor

    //////////////////
    // destructor)
    //////////////////
    ~NicRxGmii()
    {
        delete [] r_buffer;
        m_file.close();
    }

}; // end NicRxGmii

}}

#endif

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4



