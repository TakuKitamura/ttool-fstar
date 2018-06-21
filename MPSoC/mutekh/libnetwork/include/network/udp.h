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

#ifndef NETWORK_UDP_H_
#define NETWORK_UDP_H_

/**
   @file
   @module{Network library}
   @short UDP stack
 */

#include <hexo/types.h>
#include <network/packet.h>
#include <network/protos.h>

/*
 * UDP functions
 */

NET_PUSHPKT(udp_pushpkt);

/**
   @this prepares headers of a packet up to the UDP header

   @param interface Outgoing interface
   @param addressing Lower protocol
   @param packet Packet descriptor
   @param size UDP payload size

   @returns the UDP payload address where caller can write @tt size
            bytes.
 */
uint8_t	*udp_preparepkt(struct net_if_s		*interface,
                        struct net_proto_s	*addressing,
                        struct net_packet_s	*packet,
                        size_t			size,
                        size_t			max_padding);

/**
   @this sends a prepared and populated packet to the network.

   @param interface Outgoing interface
   @param addressing Lower protocol
   @param packet Packet descriptor
   @param source_port Source port
   @param dest_port Destination port
   @param compute_checksum Whether to add the optional UDP payload
          checksum in header, or to put 0
 */
void	udp_sendpkt(struct net_if_s	*interface,
                    struct net_proto_s	*addressing,
                    struct net_packet_s	*packet,
                    uint_fast16_t	source_port,
                    uint_fast16_t	dest_port,
                    bool_t		compute_checksum);

/*
 * UDP protocol descriptor.
 */

extern const struct net_proto_desc_s	udp_protocol;

#endif
