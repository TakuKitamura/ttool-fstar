/*
 * Copyright (c) 1982, 1986, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)tcp.h	8.1 (Berkeley) 6/10/93
 */

#ifndef NETINET_TCP_H_
#define NETINET_TCP_H_

/**
   @file
   @module{Network library}
   @short Standard header for TCP definitions
 */

#ifndef CONFIG_NETWORK_TCP
# warning TCP support is not enabled in configuration file
#endif

#include <hexo/types.h>
#include <hexo/endian.h>

/*
 * TCP header flags.
 */

#define TH_FIN	0x01
#define TH_SYN	0x02
#define TH_RST	0x04
#define TH_PUSH	0x08
#define TH_ACK	0x10
#define TH_URG	0x20

/**
  @this is a TCP header.

  Per RFC 793, September, 1981.
 */
struct		tcphdr
{
  uint16_t	th_sport;		//< source port 
  uint16_t	th_dport;		//< destination port 
  uint32_t	th_seq;		//< sequence number 
  uint32_t	th_ack;		//< acknowledgement number 
  ENDIAN_BITFIELD(uint8_t th_off:4,		//< data offset 
                  uint8_t th_x2:4);		//< (unused) 
  uint8_t	th_flags;
  uint16_t	th_win;		//< window 
  uint16_t	th_sum;		//< checksum 
  uint16_t	th_urp;		//< urgent pointer 
} __attribute__((packed));

/**
 * Default maximum segment size for TCP.
 * With an IP MSS of 576, this is 536,
 * but 512 is probably more convenient.
 * This should be defined as MIN(512, IP_MSS - sizeof (struct tcpiphdr)).
 */
#define TCP_MSS			512

#define TCP_MAXWIN		65535	//< largest value for (unscaled) window 

#define TCP_MAX_WINSHIFT	14	//< maximum window shift 

#define TCP_MSL			30000	//< ms, see RFC 

#endif

