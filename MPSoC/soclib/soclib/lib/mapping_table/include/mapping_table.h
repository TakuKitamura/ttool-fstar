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
 * Copyright (c) UPMC, Lip6, Asim
 *         Nicolas Pouillon <nipo@ssji.net>, 2007
 *
 * Based on previous works by Francois Pecheux & Alain Greiner
 *
 * Maintainers: nipo
 */
#ifndef SOCLIB_MAPPING_TABLE_H
#define SOCLIB_MAPPING_TABLE_H

#include <inttypes.h>
#include <list>
#include <sstream>

#include "segment.h"
#include "address_decoding_table.h"
#include "address_masking_table.h"
#include "int_tab.h"

namespace soclib { namespace common {

//////////////////////////////////////
class MappingTable
//////////////////////////////////////
{
public:
    typedef uint64_t addr64_t;
    typedef uint32_t addr32_t;

private:
    std::list<soclib::common::Segment> m_segment_list;     // list of all segments
    size_t                             m_addr_width;       // address width 
    addr64_t                           m_addr_mask;
    IntTab                             m_level_addr_bits;  // number of bits per level (addr)
    IntTab                             m_level_id_bits;    // number of bits per level (srcid)
    addr64_t                           m_cacheability_mask;
    addr64_t                           m_rt_size;          // max segment size for 1 target
    bool                               m_used;             // no more modif when true
    size_t*                            m_srcid_array;      // array indexed by srcid,
                                                           // and containing local port index

public:
    MappingTable( const MappingTable& );
    const MappingTable &operator=( const MappingTable & );

    MappingTable( size_t           addr_width,
                  const IntTab     &level_addr_bits,
                  const IntTab     &level_id_bits,
                  const addr64_t   cacheability_mask );
    
    ~MappingTable();

    void add( const soclib::common::Segment &seg );

    void srcid_map( const IntTab &srcid, size_t portid );

    std::list<Segment> getSegmentList( const IntTab &index ) const;

    const std::list<Segment> &getAllSegmentList() const;

    soclib::common::Segment getSegment( const IntTab &index ) const;

    template<typename desired_addr_t>
    AddressDecodingTable<desired_addr_t, bool> 
    getCacheabilityTable() const;

    template<typename desired_addr_t>
    AddressDecodingTable<desired_addr_t, bool> 
    getLocalityTable( const IntTab &index ) const;

    template<typename desired_addr_t>
    AddressDecodingTable<desired_addr_t, int> 
    getRoutingTable( const IntTab &index, int default_index = 0 ) const;
    
    AddressDecodingTable<uint64_t, size_t>
    getPortidFromAddress( const size_t cluster_id, const size_t default_id = 0 ) const;

    AddressDecodingTable<uint64_t, size_t>
    getPortidFromSrcid( const size_t cluster_id ) const;

    AddressDecodingTable<uint32_t, bool> 
    getIdLocalityTable( const IntTab &index ) const;

    AddressMaskingTable<uint32_t> 
    getIdMaskingTable( const int level ) const;

    //////////////////////////////
    size_t getAddressWidth() const
    {
        return m_addr_width;
    }

    void print( std::ostream &o ) const;

    //////////////////////////////////////////////////////////////////////////
    friend std::ostream &operator << (std::ostream &o, const MappingTable &mt)
    {
        mt.print(o);
        return o;
    }

    ///////////////////////////////////////////////////////////
    inline unsigned int indexForId( const IntTab &index ) const
    {
    //DG 24.10.2016

     std::cout << " index : " << index << std::endl;
     std::cout << " m_level_id_bits : " << m_level_id_bits << std::endl;
     std::cout << " index*m_level_id_bits: " << index*m_level_id_bits << std::endl;
        return index*m_level_id_bits;
    }

    ///////////////////////////
    inline size_t level() const
    {
        return m_level_addr_bits.level();
    }

    //////////////////// simpler variants ///////////////////////////

    AddressDecodingTable<addr32_t, bool> 
    getCacheabilityTable() const
    {
        return getCacheabilityTable<addr32_t>();
    }

    AddressDecodingTable<addr32_t, bool> 
    getLocalityTable( const IntTab &index ) const
    {
        return getLocalityTable<addr32_t>( index );
    }

    AddressDecodingTable<addr32_t, int> 
    getRoutingTable( const IntTab &index, int default_index = 0 ) const
    {
        return getRoutingTable<addr32_t>( index, default_index );
    }

};

}}

#endif /* SOCLIB_MAPPING_TABLE_H */

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

