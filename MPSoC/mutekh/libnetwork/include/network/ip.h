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

#ifndef NETWORK_IP_H_
#define NETWORK_IP_H_

/**
   @file
   @module{Network library}
   @short IP stack
 */

#include <network/packet.h>
#include <network/protos.h>
#include <network/route.h>

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/cont_hashlist.h>
#include <gpct/object_simple.h>

#include <mutek/timer.h>

#define IP_DELIVERY_DIRECT	0
#define IP_DELIVERY_INDIRECT	1

/** Reassembly timeout: 10 seconds */
#define IP_REASSEMBLY_TIMEOUT	10000

/** IP pseudo header (for upper layer checksum computation) */
struct ip_pseudoheader_s
{
  uint32_t	source;
  uint32_t	dest;
  uint8_t	zero;
  uint8_t	type;
  uint16_t	size;
} __attribute__((packed));

/*
 * Fragmentation structures.
 */

OBJECT_TYPE(fragment_obj, SIMPLE, struct ip_packet_s);

struct					ip_packet_s
{
  uint8_t				id[6];
  uint_fast16_t				size;
  uint_fast16_t				received;
  struct net_proto_s			*addressing;
  struct timer_event_s			timeout;
  packet_queue_root_t			packets;

  fragment_obj_entry_t			obj_entry;
  CONTAINER_ENTRY_TYPE(HASHLIST)	list_entry;
};

OBJECT_CONSTRUCTOR(fragment_obj);
OBJECT_DESTRUCTOR(fragment_obj);
OBJECT_FUNC(fragment_obj, SIMPLE, static inline, fragment_obj, obj_entry);

/*
 * Fragments list.
 */

#define CONTAINER_LOCK_ip_packet	HEXO_SPIN
CONTAINER_TYPE(ip_packet, HASHLIST, struct ip_packet_s, list_entry, 64);
CONTAINER_KEY_TYPE(ip_packet, PTR, BLOB, id, 6);

/*
 * IP private data.
 */

struct			net_pv_ip_s
{
  struct net_if_s	*interface;
  struct net_proto_s	*arp;
  struct net_proto_s	*icmp;
  uint_fast32_t		addr;
  uint_fast32_t		mask;
  ip_packet_root_t	fragments;
  uint_fast32_t		id_seq;
};

/*
 * IP functions
 */

NET_INITPROTO(ip_init);
NET_DESTROYPROTO(ip_destroy);
NET_PUSHPKT(ip_pushpkt);
NET_PREPAREPKT(ip_preparepkt);
NET_SENDPKT(ip_send);
NET_MATCHADDR(ip_matchaddr);
NET_PSEUDOHEADER_CHECKSUM(ip_pseudoheader_checksum);
void		ip_route(struct net_packet_s	*packet,
			 struct net_route_s	*route);
TIMER_CALLBACK(ip_fragment_timeout);

extern const struct net_proto_desc_s	ip_protocol;

#endif
