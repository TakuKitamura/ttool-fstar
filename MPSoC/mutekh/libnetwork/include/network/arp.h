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

#ifndef LIBNETWORK_ARP_H_
#define LIBNETWORK_ARP_H_

/**
   @file
   @module{Network library}
   @short ARP stack
 */

#include <hexo/types.h>
#include <netinet/ether.h>

#include <network/packet.h>
#include <network/protos.h>

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/object_simple.h>

#include <mutek/timer.h>

/*
 * Request timeout and retry count
 */

#define ARP_REQUEST_TIMEOUT	2000 //< 2 seconds 
#define ARP_ENTRY_TIMEOUT	120000 //< 2 minutes 
#define ARP_STALE_TIMEOUT	240000 //< 4 minutes 
#define ARP_MAX_RETRIES		3
#define RARP_TIMEOUT		30 //< 30 seconds 

/*
 * Flags
 */

#define ARP_TABLE_DEFAULT	0 //< Default flags, entry is valid
#define ARP_TABLE_IN_PROGRESS	1 //< Mark entry as being currently in resolution process
#define ARP_TABLE_NO_UPDATE	2 //< Do not update existing entry

NET_INITPROTO(arp_init);
NET_DESTROYPROTO(arp_destroy);
NET_PUSHPKT(arp_pushpkt);
NET_PREPAREPKT(arp_preparepkt);

/**
   @this inserts or updates a resolution inside an ARP table.

   @param arp The ARP table to add the entry in
   @param ip The resolved IP
   @param mac The MAC corresponding to IP
   @param flags Flags to associate to the entry, possible values are
   an ORing of ARP_TABLE_DEFAULT, ARP_TABLE_IN_PROGRESS and
   ARP_TABLE_NO_UPDATE.

   @returns the entry, or NULL if not found
 */
struct arp_entry_s	*arp_update_table(struct net_proto_s	*arp,
					  uint32_t		ip,
					  uint8_t		*mac,
					  uint_fast8_t		flags);

/**
   @this retrieves a MAC address for a given IP address.
   Makes an ARP request if needed.

   @param addressing
   @param arp The ARP instance
   @param packet
   @param ip The destination IP to get MAC for

   @returns a pointer to the MAC address, NULL if unresolved
 */
const uint8_t		*arp_get_mac(struct net_proto_s		*addressing,
				     struct net_proto_s		*arp,
				     struct net_packet_s	*packet,
				     uint_fast32_t		ip);


NET_INITPROTO(rarp_init);
NET_PUSHPKT(rarp_pushpkt);
NET_PREPAREPKT(rarp_preparepkt);

void	rarp_request(struct net_if_s	*interface,
		     struct net_proto_s	*rarp,
		     uint8_t		*mac);

/** ARP protocol descriptor */
extern const struct net_proto_desc_s	arp_protocol;

/** RARP protocol descriptor */
extern const struct net_proto_desc_s	rarp_protocol;

#endif
