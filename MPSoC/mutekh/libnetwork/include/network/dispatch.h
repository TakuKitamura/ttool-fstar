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

    Copyright Nicolas Pouillon <nipo@ssji.net> (c) 2009
*/

#ifndef LIBNETWORK_DISPATCH_H_
#define LIBNETWORK_DISPATCH_H_

/**
   @file
   @module{Network library}
   @short Incoming packets dispatching API
 */

#include <hexo/types.h>

/** @this is the dispatch thread internal state */
struct net_dispatch_s;

/** @hidden */
struct net_packet_s;

/** @hidden */
struct net_if_s;

/**
   @this creates a new dispatching thread attached to a given interface.

   @param interface Interface to attach the dispatching thread to

   @returns a dispatch thread structure, NULL on error
 */
struct net_dispatch_s *network_dispatch_create(struct net_if_s *interface);

/**
   @this kills a currently running dispatching thread. This waits for
   all the currently handled packets to leave the thread, then cleanly
   terminates the thread and return.

   @param dispatch Dispatch thread to kill
 */
void network_dispatch_kill(struct net_dispatch_s *dispatch);

/**
   @this pushes a packet to the dispatching thread. It will be handled
   by the associated interface stack as soon as possible.

   @param dispatch Dispatch thread to push the packet into
   @param packet Packet structure to push
 */
void network_dispatch_packet(struct net_dispatch_s *dispatch,
							 struct net_packet_s *packet);

/**
   @this pushes a packet to the dispatching thread. It will be handled
   by the associated interface stack as soon as possible.

   @param dispatch Dispatch thread to push the packet into
   @param packet Raw packet data to push
   @param size Raw packet data size
 */
void network_dispatch_data(struct net_dispatch_s *dispatch,
						   void *data,
						   uint_fast16_t size);

#endif
