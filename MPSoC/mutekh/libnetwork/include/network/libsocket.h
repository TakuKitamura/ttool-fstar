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

#ifndef NETWORK_LIBSOCKET_H
#define NETWORK_LIBSOCKET_H

/**
   @file
   @module{Network library}
   @short Low-level socket API
 */

#include <network/protos.h>
#include <network/packet.h>
#include <network/if.h>

#ifdef CONFIG_NETWORK_SOCKET_PACKET
void	pf_packet_signal(struct net_if_s	*interface,
			 struct net_packet_s	*packet,
			 net_proto_id_t		protocol);
#endif

#ifdef CONFIG_NETWORK_SOCKET_RAW
void	sock_raw_signal(struct net_if_s		*interface,
			struct net_proto_s	*addressing,
			struct net_packet_s	*packet,
			net_proto_id_t		protocol);
#endif

#endif

