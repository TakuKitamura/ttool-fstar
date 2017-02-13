/*
    This file is part of MutekH.
    
    MutekH is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; version 2.1 of the
    License.
    
    MutekH is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public
    License along with MutekH; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
    02110-1301 USA.

    Copyright Matthieu Bucchianeri <matthieu.bucchianeri@epita.fr> (c) 2006
*/

#ifndef NETWORK_ICMP_H_
#define NETWORK_ICMP_H_

/**
   @file
   @module{Network library}
   @short ICMP stack
 */

#include <network/packet.h>
#include <network/protos.h>

/*
  @module{Network library}
  @short{ICMP functions}
 */

NET_PUSHPKT(icmp_pushpkt);
NET_PREPAREPKT(icmp_preparepkt);
NET_ERRORMSG(icmp_errormsg);

/** @this is the ICMP protocol descriptor */
extern const struct net_proto_desc_s	icmp_protocol;

#endif
