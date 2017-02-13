/* -*- c++ -*-
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
 */

#include <cassert>
#include <sstream>
#include "mapping_table.h"

namespace soclib { namespace common {

///////////////////////////
MappingTable::MappingTable(
    size_t addr_width,
    const IntTab &level_addr_bits,
    const IntTab &level_id_bits,
    const addr64_t cacheability_mask )
        : m_segment_list(),
          m_addr_width(addr_width),
          m_addr_mask((addr_width == 64) ? ((addr64_t)-1) : (((addr64_t)1<<addr_width)-1)),
          m_level_addr_bits(level_addr_bits),
          m_level_id_bits(level_id_bits),
          m_cacheability_mask(cacheability_mask),
          m_used(false)
{
    m_rt_size = 1ULL << (addr_width - m_level_addr_bits.sum());
    addr64_t cm_rt_size = 1 << AddressMaskingTable<addr64_t>(m_cacheability_mask).getDrop();
    m_rt_size = std::min<addr64_t>(cm_rt_size, m_rt_size);

    m_srcid_array = new size_t[1<<m_level_id_bits.sum()];
}


MappingTable::~MappingTable() {
    delete [] m_srcid_array;
}

/////////////////////////////////////////////////////
MappingTable::MappingTable( const MappingTable &ref )
        : m_segment_list(ref.m_segment_list),
          m_addr_width(ref.m_addr_width),
          m_addr_mask(ref.m_addr_mask),
          m_level_addr_bits(ref.m_level_addr_bits),
          m_level_id_bits(ref.m_level_id_bits),
          m_cacheability_mask(ref.m_cacheability_mask),
          m_rt_size(ref.m_rt_size),
          m_used(ref.m_used)
{
}

//////////////////////////////////////////////////////////////////////
const MappingTable &MappingTable::operator=( const MappingTable &ref )
{
    m_segment_list = ref.m_segment_list;
    m_addr_width = ref.m_addr_width;
    m_addr_mask = ref.m_addr_mask;
    m_level_addr_bits = ref.m_level_addr_bits;
    m_level_id_bits = ref.m_level_id_bits;
    m_cacheability_mask = ref.m_cacheability_mask;
    m_rt_size = ref.m_rt_size;
    m_used = ref.m_used;
    return *this;
}

/////////////////////////////////////////////////////
void MappingTable::srcid_map( const IntTab   &srcid,
                              size_t         portid )
{
    m_srcid_array[indexForId(srcid)] = portid;
}
 
/////////////////////////////////////////////
void MappingTable::add( const Segment &_seg )
{
    Segment seg = _seg.masked(m_addr_mask);
    std::list<Segment>::iterator i;

    assert(m_used == false && 
    "You must not add segments inside a mapping table once it is used.");

    if ( seg.index().level() != m_level_addr_bits.level() ) 
    {
        std::ostringstream o;
        o << seg << " is not the same level as the mapping table.";
        throw soclib::exception::ValueError(o.str());
    }

    for ( i = m_segment_list.begin();
          i != m_segment_list.end();
          i++ ) 
    {
        Segment &s = *i;
        if ( s.isOverlapping(seg) ) {
            std::ostringstream o;
            o << seg << " bumps in " << s;
            throw soclib::exception::Collision(o.str());
        }

        for ( addr64_t address = s.baseAddress() & ~(m_rt_size-1);
              (address < s.baseAddress()+s.size()) &&
                  (address >= (s.baseAddress() & ~(m_rt_size-1)));
              address += m_rt_size ) 
        {
            for ( addr64_t segaddress = seg.baseAddress() & ~(m_rt_size-1);
                  (segaddress < seg.baseAddress()+seg.size()) &&
                      (segaddress >= (seg.baseAddress() & ~(m_rt_size-1)));
                  segaddress += m_rt_size ) 
            {
                if ( (m_cacheability_mask & address) == (m_cacheability_mask & segaddress)
                    && s.cacheable() != seg.cacheable() ) 
                {
                    std::ostringstream oss;
                    oss << "Segment " << s
                        << " has a different cacheability attribute with same MSBs than " 
                        << seg << std::endl;
                    throw soclib::exception::RunTimeError(oss.str());
                }
            }
        }
    }
    m_segment_list.push_back(seg);
}

////////////////////////////////////////////////////////////////////////////
// This function returns a list of all segments defined in a mapping table.
////////////////////////////////////////////////////////////////////////////
const std::list<Segment> & MappingTable::getAllSegmentList() const
{
    const_cast<MappingTable*>(this)->m_used = true;
    return m_segment_list;
}

////////////////////////////////////////////////////////////////////////////
// This function returns a list containing all segments allocated
// to a target identified by its target index.
////////////////////////////////////////////////////////////////////////////
std::list<Segment> MappingTable::getSegmentList( const IntTab &index ) const
{
    std::list<Segment> ret;
    std::list<Segment>::const_iterator i;
    
    const_cast<MappingTable*>(this)->m_used = true;

    for ( i = m_segment_list.begin();
          i != m_segment_list.end();
          i++ ) 
    {
        if ( i->index() == index )
            ret.push_back(*i);
    }
    return ret;
}

///////////////////////////////////////////////////////////////////////////
// This function checks that only one segment is allocated to a target
// identified by its target index, ant returns this segment.
///////////////////////////////////////////////////////////////////////////
Segment MappingTable::getSegment( const IntTab &index ) const
{
    std::list<Segment> list = getSegmentList(index);

    const_cast<MappingTable*>(this)->m_used = true;

    assert(list.size() == 1);
    return list.front();
}

///////////////////////////////////////////////////////////////////////////
// This function returns an ADT that can be used to get
// the local port_id for a target identified by a VCI address
// in a clusterized architecture (two levels interconnect).
// Only the local bits in the address are decoded.
// This ADT can depend on the cluster_id.
///////////////////////////////////////////////////////////////////////////
AddressDecodingTable<uint64_t,size_t> 
MappingTable::getPortidFromAddress( const size_t cluster_id, 
                                    const size_t default_tgt_id ) const
{
    // checking mapping table (two levels interconnect)
    if ( m_level_addr_bits.level() != 2 )
    {
        std::cout << "ERROR in Mapping Table : the getPortidfromAddress() function"
                  << " requires a two levels interconnect" << std::endl;
        std::cout << *this << std::endl;
        exit(0);
    }

    size_t global_bits  = m_level_addr_bits[0];   // number of address global bits
    size_t local_bits   = m_level_addr_bits[1];   // number of address local bits

    // ADT to be returned
    AddressDecodingTable<uint64_t,size_t> 
    adt( local_bits, m_addr_width - global_bits - local_bits );
	adt.reset( default_tgt_id );

    // temporary ADT for checking
    AddressDecodingTable<uint64_t,bool>
    done( local_bits, m_addr_width - global_bits - local_bits );
	done.reset( false );

    const_cast<MappingTable*>(this)->m_used = true;

    // loop on all segments
    std::list<Segment>::const_iterator seg;
    for ( seg = m_segment_list.begin();
          seg != m_segment_list.end();
          seg++ ) 
    {
        // skip segment if cluster_id does not match
        if ( (size_t)seg->index()[0] != cluster_id )    continue; 

        uint64_t base = seg->baseAddress() & ~(m_rt_size-1);

        // loop on all possible values for the address local bits
        for ( uint64_t addr = base ;
              (addr < base + seg->size()) and (addr >= base);
              addr += m_rt_size ) 
        {
            size_t port_id = seg->index()[1];

            if ( done[addr] and (adt[addr] != port_id) ) 
            {
                std::cout << "ERROR in Mapping Table : segment " << *seg
                          << " allocated to a different target than another segment"
                          << " with the same routing bits" << std::endl;
                std::cout << *this << std::endl;
            }
            adt.set( addr, port_id );
            done.set( addr, true );
        }
	}

    return adt;
}

///////////////////////////////////////////////////////////////////////////
// This function returns an ADT that can be used to get
// the local port_id for an initiator identified by the VCI srcid
// in a clusterized architecture (two levels interconnect).
// Only the local bits in the srcid are decoded.
// This ADT can depend on the cluster_id.
///////////////////////////////////////////////////////////////////////////
AddressDecodingTable<uint64_t, size_t> 
MappingTable::getPortidFromSrcid( const size_t cluster_id ) const
{
    // checking mapping table (two levels interconnect)
    if ( m_level_addr_bits.level() != 2 )
    {
        std::cout << "ERROR in Mapping Table : the getPortidfromSrcid() function"
                  << " requires a two levels interconnect" << std::endl;
        std::cout << *this << std::endl;
        exit(0);
    }

    size_t local_id_bits  = m_level_id_bits[1];   // number of local bits in SRCID 
    size_t local_id_max   = 1<<local_id_bits;     // adt size

    const_cast<MappingTable*>(this)->m_used = true;

    // ADT to be returned
    AddressDecodingTable<uint64_t, size_t> adt(local_id_bits, 0);

    for ( size_t loc = 0 ; loc < local_id_max ; loc++ )
    {
        uint64_t srcid = (cluster_id<<local_id_bits) + loc;
        adt.set( srcid , m_srcid_array[srcid] );
    }

    return adt;
}

////////////////////////////////////////////////////////////
// This function returns an ADT that can be used to get
// the cacheability attribute from a physical address
////////////////////////////////////////////////////////////
template<typename desired_addr_t>
AddressDecodingTable<desired_addr_t, bool> 
MappingTable::getCacheabilityTable() const
{
    // ADT to be returned
    AddressDecodingTable<desired_addr_t, bool> adt(m_cacheability_mask);
	adt.reset(false);

    // temporary ADT for checking
    AddressDecodingTable<desired_addr_t, bool> done(m_cacheability_mask);
	done.reset(false);

    const_cast<MappingTable*>(this)->m_used = true;

    std::list<Segment>::const_iterator i;
    for ( i = m_segment_list.begin();
          i != m_segment_list.end();
          i++ ) 
    {
        for ( desired_addr_t addr = i->baseAddress() & ~(m_rt_size-1);
              (addr < i->baseAddress()+i->size()) &&
                  (addr >= (i->baseAddress() & ~(m_rt_size-1)));
              addr += m_rt_size ) 
        {
            if ( done[addr] && adt[addr] != i->cacheable() ) 
            {
                std::ostringstream oss;
                oss << "Incoherent Mapping Table:" << std::endl
                    << "Segment " << *i 
                    << " has different cacheability than other segment with same mask"
                    << std::endl
                    << "Mapping table:" << std::endl
                    << *this;
                throw soclib::exception::RunTimeError(oss.str());
            }
            adt.set( addr, i->cacheable() );
            done.set( addr, true );
        }
    }
    return adt;
}

////////////////////////////////////////////////////////////
/// This function returns an ADT that can be used to get
// the local condition from a physical address
////////////////////////////////////////////////////////////
template<typename desired_addr_t> 
AddressDecodingTable<desired_addr_t, bool>
MappingTable::getLocalityTable( const IntTab &index ) const
{
	size_t nbits = m_level_addr_bits.sum(index.level());

    // ADT to be returned
    AddressDecodingTable<desired_addr_t, bool> adt(nbits, m_addr_width - nbits);
	adt.reset(true);

    // temporary ADT for checking
    AddressDecodingTable<desired_addr_t, bool> done(nbits, m_addr_width - nbits);
	done.reset(false);

    const_cast<MappingTable*>(this)->m_used = true;

    std::list<Segment>::const_iterator i;
    for ( i = m_segment_list.begin();
          i != m_segment_list.end();
          i++ ) 
    {
        for ( desired_addr_t addr = i->baseAddress() & ~(m_rt_size-1);
              (addr < i->baseAddress()+i->size()) &&
                  (addr >= (i->baseAddress() & ~(m_rt_size-1)));
              addr += m_rt_size ) 
        {
            bool val = (i->index().idMatches(index) );

            if ( done[addr] && adt[addr] != val ) 
            {
                std::ostringstream oss;
                oss << "Incoherent Mapping Table:" << std::endl
                    << "Segment " << *i 
                    << " targets different component than other segments with same MSBs" 
                    << std::endl
                    << "Mapping table:" << std::endl
                    << *this;
                throw soclib::exception::RunTimeError(oss.str());
            }
            adt.set( addr, val );
            done.set( addr, true );
        }
	}
    return adt;
}

///////////////////////////////////////////////////////////
// This function returns an ADT that can be used to get
// the target port index from a physical address
///////////////////////////////////////////////////////////
template<typename desired_addr_t>
AddressDecodingTable<desired_addr_t, int>
MappingTable::getRoutingTable( const IntTab &index, int default_index ) const
{
#ifdef SOCLIB_MODULE_DEBUG
    std::cout << __FUNCTION__ << std::endl;
#endif
	size_t before = m_level_addr_bits.sum(index.level());
	size_t at     = m_level_addr_bits[index.level()];

    //  ADT to be returned 
    AddressDecodingTable<desired_addr_t, int> adt(at, m_addr_width - at - before);
	adt.reset(default_index);

    // temporary ADT for checking
    AddressDecodingTable<desired_addr_t, bool> done(at, m_addr_width - at - before);
	done.reset(false);

    const_cast<MappingTable*>(this)->m_used = true;

    std::list<Segment>::const_iterator i;
    for ( i = m_segment_list.begin();
          i != m_segment_list.end();
          i++ ) 
    {
#ifdef SOCLIB_MODULE_DEBUG
        std::cout << *i
                  << ", m_rt_size=" << m_rt_size
                  << ", m_rt_mask=" << ~(m_rt_size-1)
                  << std::endl;
#endif
        if ( ! i->index().idMatches(index) ) 
        {
#ifdef SOCLIB_MODULE_DEBUG
			std::cout << i->index() << " does not match " << index << std::endl;
#endif
			continue;
		}

        #ifdef SOCLIB_MODULE_DEBUG
        std::cout
            << ' ' << (i->baseAddress() & ~(m_rt_size-1))
            << ' ' << (i->baseAddress() + i->size())
            << ' ' << (((i->baseAddress() & ~(m_rt_size-1)) < i->baseAddress()+i->size()))
            << ' ' << (((i->baseAddress() & ~(m_rt_size-1)) >= (i->baseAddress() & ~(m_rt_size-1))))
            << std::endl;
        #endif

        for ( desired_addr_t addr = i->baseAddress() & ~(m_rt_size-1);
              (addr < i->baseAddress()+i->size()) &&
                  (addr >= (i->baseAddress() & ~(m_rt_size-1)));
              addr += m_rt_size ) 
        {
            int val = i->index()[index.level()];

            #ifdef SOCLIB_MODULE_DEBUG
			std::cout << addr << " -> " << val << std::endl;
            #endif

            if ( done[addr] && adt[addr] != val ) 
            {
                std::ostringstream oss;
                oss << "Incoherent Mapping Table: for " << index << std::endl
                    << "Segment " << *i << " targets different target (or cluster) than other segments with same routing bits" << std::endl
                    << "Mapping table:" << std::endl
                    << *this;
                  throw soclib::exception::RunTimeError(oss.str());
            }
            adt.set( addr, val );
            done.set( addr, true );
        }
#ifdef SOCLIB_MODULE_DEBUG
        std::cout << std::endl;
#endif
	}
    return adt;
}

//////////////////////////////////////////////////
void MappingTable::print( std::ostream &o ) const
{
    std::list<Segment>::const_iterator i;

    o << "Mapping table: ad:" << m_level_addr_bits
      << " id:" << m_level_id_bits
      << " cacheability mask: " << std::hex << std::showbase << m_cacheability_mask
      << std::endl;
    for ( i = m_segment_list.begin();
          i != m_segment_list.end();
          i++ ) {
        o << " " << (*i) << std::endl;
    }
}

/////////////////////////////////////////////////////////
AddressMaskingTable<uint32_t> 
MappingTable::getIdMaskingTable( const int level ) const
{
    int use = m_level_id_bits[level];
    int drop = 0;
    const_cast<MappingTable*>(this)->m_used = true;

    for ( size_t i=level+1; i<m_level_id_bits.level(); ++i )
        drop += m_level_id_bits[i];
    return AddressMaskingTable<uint32_t>( use, drop );
}

/////////////////////////////////////
AddressDecodingTable<uint32_t, bool> 
MappingTable::getIdLocalityTable( const IntTab &index ) const
{
    size_t 	nbits = m_level_id_bits.sum(index.level());
    size_t 	id_width = m_level_id_bits.sum();
    IntTab	complete_index(index, 0);
    uint32_t 	match = (uint32_t)indexForId(complete_index);
    const_cast<MappingTable*>(this)->m_used = true;

    AddressDecodingTable<uint32_t, bool> adt(nbits, id_width-nbits);
    adt.reset(false);
    adt.set(match, true);
    return adt;
}

template
AddressDecodingTable<uint64_t, bool>
MappingTable::getCacheabilityTable<uint64_t>() const;

template
AddressDecodingTable<uint64_t, bool>
MappingTable::getLocalityTable<uint64_t>( const IntTab &index ) const;

template
AddressDecodingTable<uint64_t, int>
MappingTable::getRoutingTable<uint64_t>( const IntTab &index, int default_index ) const;
    
template
AddressDecodingTable<uint32_t, bool>
MappingTable::getCacheabilityTable<uint32_t>() const;

template
AddressDecodingTable<uint32_t, bool>
MappingTable::getLocalityTable<uint32_t>( const IntTab &index ) const;

template
AddressDecodingTable<uint32_t, int>
MappingTable::getRoutingTable<uint32_t>( const IntTab &index, int default_index ) const;
    
}}

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

