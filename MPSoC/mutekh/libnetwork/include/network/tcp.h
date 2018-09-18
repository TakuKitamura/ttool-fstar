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

#ifndef NETWORK_TCP_H_
#define NETWORK_TCP_H_

/**
   @file
   @module{Network library}
   @short TCP stack
 */

#include <network/packet.h>
#include <network/protos.h>
#include <network/if.h>
#include <network/libtcp.h>

#include <hexo/gpct_platform_hexo.h>
#include <gpct/cont_hashlist.h>

/**
 * Window default size
 */
#define TCP_DFL_WINDOW	4096

/**
  Total length of headers for a TCP packet.
  IP + TCP headers
 */
#define TCP_HEADERS_LEN	40

/*
 * Connection state
 */

#define TCP_STATE_ERROR		0
#define TCP_STATE_SYN_SENT	1
#define TCP_STATE_SYN_RCVD	2
#define TCP_STATE_LISTEN	3
#define TCP_STATE_FIN_WAIT1	4
#define TCP_STATE_FIN_WAIT2	5
#define TCP_STATE_CLOSING	6
#define TCP_STATE_TIME_WAIT	7
#define TCP_STATE_CLOSE_WAIT	8
#define TCP_STATE_LAST_ACK	9
#define TCP_STATE_ESTABLISHED	10
#define TCP_STATE_CLOSED	11

/*
 * Control operations
 */

#define TCP_SYN		0
#define TCP_SYN_ACK	1
#define TCP_ACK		2
#define TCP_FIN		3

/*
 * Prototypes
 */

NET_PUSHPKT(tcp_pushpkt);

/**
   @this prepares headers of a packet up to the TCP header

   @param interface Outgoing interface
   @param addressing Lower protocol
   @param packet Packet descriptor
   @param size TCP payload size

   @returns the TCP payload address where caller can write @tt size
            bytes.
 */
uint8_t	*tcp_preparepkt(struct net_if_s		*interface,
                        struct net_proto_s	*addressing,
                        struct net_packet_s	*packet,
                        size_t			size,
                        size_t			max_padding);

/**
   @this sends a control packet for an existing TCP session

   @param session Valid TCP session descriptor
   @param operation Operation to perform (syn, ack, fin)
 */
void	tcp_send_controlpkt(struct net_tcp_session_s	*session,
                            uint_fast8_t		operation);

/**
   @this sends a prepared and populated packet to the network.

   @param session Valid TCP session descriptor
   @param data Data to send
   @param size Size of @tt data
 */
void	tcp_send_datapkt(struct net_tcp_session_s	*session,
			 void				*data,
			 size_t				size,
			 uint_fast8_t			flags);

extern const struct net_proto_desc_s	tcp_protocol;

#endif
