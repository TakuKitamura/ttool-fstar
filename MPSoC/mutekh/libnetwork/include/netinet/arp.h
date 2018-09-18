/*
   Copyright (C) 1997, 1999, 2001 Free Software Foundation, Inc.
   This file is part of the GNU C Library.
   Contributed by Ulrich Drepper <drepper@cygnus.com>, 1997.

   The GNU C Library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   The GNU C Library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with the GNU C Library; if not, write to the Free
   Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
   02111-1307 USA.  */

/* Based on the 4.4BSD and Linux version of this file.  */

/**
   @file
   @module{Network library}
   @short Standard header for Address Resolution Protocol definitions
 */

#ifndef NETINET_ARP_H_
#define NETINET_ARP_H_

#include <hexo/types.h>
#include <netinet/ether.h>

/** ARP protocol opcodes. */
#define	ARPOP_REQUEST	1		//< ARP request
#define	ARPOP_REPLY	2		//< ARP reply
#define	ARPOP_RREQUEST	3		//< RARP request
#define	ARPOP_RREPLY	4		//< RARP reply
#define	ARPOP_InREQUEST	8		//< InARP request
#define	ARPOP_InREPLY	9		//< InARP reply
#define	ARPOP_NAK	10		//< (ATM)ARP NAK

/** This structure defines an ethernet arp header.  */
struct		arphdr
{
  uint16_t	ar_hrd;		//< Format of hardware address
  uint16_t	ar_pro;		//< Format of protocol address
  uint8_t	ar_hln;		//< Length of hardware address
  uint8_t	ar_pln;		//< Length of protocol address
  uint16_t	ar_op;		//< ARP opcode (command)
} __attribute__ ((packed));

/** ARP protocol HARDWARE identifiers. */
#define ARPHRD_NETROM	0		//< From KA9Q: NET/ROM pseudo
#define ARPHRD_ETHER 	1		//< Ethernet 10/100Mbps
#define	ARPHRD_EETHER	2		//< Experimental Ethernet
#define	ARPHRD_AX25	3		//< AX.25 Level 2
#define	ARPHRD_PRONET	4		//< PROnet token ring
#define	ARPHRD_CHAOS	5		//< Chaosnet
#define	ARPHRD_IEEE802	6		//< IEEE 802.2 Ethernet/TR/TB
#define	ARPHRD_ARCNET	7		//< ARCnet
#define	ARPHRD_APPLETLK	8		//< APPLEtalk
#define	ARPHRD_DLCI	15		//< Frame Relay DLCI
#define	ARPHRD_ATM	19		//< ATM
#define	ARPHRD_METRICOM	23		//< Metricom STRIP (new IANA id)
#define ARPHRD_IEEE1394	24		//< IEEE 1394 IPv4 - RFC 2734
#define ARPHRD_EUI64		27		//< EUI-64
#define ARPHRD_INFINIBAND	32		//< InfiniBand

/**
 * @this is the Ethernet Address Resolution Protocol structure
 *
 * See RFC 826 for protocol description.  Structure below is adapted
 * to resolving internet addresses.  Field names used correspond to
 * RFC 826.
 */
struct ether_arp
{
  struct arphdr	ea_hdr;		//< fixed-size header 
  uint8_t	arp_sha[ETH_ALEN];	//< sender hardware address 
  uint32_t	arp_spa;		//< sender protocol address 
  uint8_t	arp_tha[ETH_ALEN];	//< target hardware address 
  uint32_t	arp_tpa;		//< target protocol address 
} __attribute__ ((packed));

#endif

