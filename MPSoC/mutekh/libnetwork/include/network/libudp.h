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

#ifndef NETWORK_LIBUDP_H
#define NETWORK_LIBUDP_H

/**
   @file
   @module{Network library}
   @short UDP stack
 */

#ifndef CONFIG_NETWORK_UDP
# warning UDP support is not enabled in configuration file
#endif

#include <network/protos.h>
#include <network/packet.h>
#include <netinet/udp.h>
#include <network/route.h>

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/cont_hashlist.h>
#include <gpct/object_simple.h>

/*
 * Temporary ports boundaries.
 */

#define UDP_TEMP_PORT_BASE	46000
#define UDP_TEMP_PORT_RANGE	4000

/*
 * Types
 */

struct net_udp_desc_s;

/**
   @this holds an UDP address: an IP address and a port number.
 */
struct	net_udp_addr_s
{
  struct net_addr_s	address;
  uint_fast16_t		port;
};

/**
   UDP callback on packet receiving.

   @param desc UDP state
   @param remote Peer address
   @param data Received data
   @param size Data size
   @param pv User's private data
 */
#define UDP_CALLBACK(f)	void (f)(                                   \
    struct net_udp_desc_s	*desc,                                  \
    struct net_udp_addr_s	*remote,                                \
    const void		*data,                                          \
    size_t			size,                                           \
    void			*pv)

typedef UDP_CALLBACK(udp_callback_t);

/**
   UDP error callback.

   @param desc UDP state
   @param error Error type
   @param pv User's private data
 */
#define UDP_ERROR_CALLBACK(f)	void (f)(struct net_udp_desc_s	*desc,	\
					 net_error_id_t		error,	\
					 void			*pv)

typedef UDP_ERROR_CALLBACK(udp_error_callback_t);

/** Connection descriptors container. */
OBJECT_TYPE(udp_desc_obj, SIMPLE, struct net_udp_desc_s);

/**
   LibUDP internal state
 */
struct					net_udp_desc_s
{
  /** local address of the descriptor */
  net_port_t				port;
  struct net_addr_s			address;
  bool_t				connected;
  bool_t				bound;

  /** error callback */
  udp_error_callback_t			*callback_error;
  void					*pv_error;

  /** used for bound descriptors */
  udp_callback_t			*callback;
  void					*pv;
  /** used for a connected descriptor */
  struct net_udp_addr_s			remote;
  struct net_route_s			*route;

  /** options */
  bool_t				checksum;

  udp_desc_obj_entry_t			obj_entry;
  CONTAINER_ENTRY_TYPE(HASHLIST)	list_entry;
};

OBJECT_CONSTRUCTOR(udp_desc_obj);
OBJECT_DESTRUCTOR(udp_desc_obj);
OBJECT_FUNC(udp_desc_obj, SIMPLE, static inline, udp_desc_obj, obj_entry);

#define CONTAINER_LOCK_udp_desc	HEXO_SPIN
CONTAINER_TYPE(udp_desc, HASHLIST, struct net_udp_desc_s, list_entry, 64);
CONTAINER_KEY_TYPE(udp_desc, PTR, SCALAR, port);

/*
 * Prototypes
 */

/**
   @this creates a new UDP session targetting a given UDP address

   @param desc Returned descriptor pointer
   @param remote UDP destination address
 */
error_t	udp_connect(struct net_udp_desc_s	**desc,
                    struct net_udp_addr_s	*remote);

/**
   @this creates a new UDP listening port

   @param desc Returned descriptor pointer
   @param local Local UDP address
   @param callback Function to call when a packet arrives
   @param pv User data to pass back to callback
 */
error_t	udp_bind(struct net_udp_desc_s		**desc,
                 struct net_udp_addr_s		*local,
                 udp_callback_t			*callback,
                 void				*pv);

/**
   @this sends a payload to a given remote UDP address

   @param desc Existing UDP session
   @param remote Target address. If NULL, uses the connected address.
   @param data Payload
   @param size Payload size
 */
error_t	udp_send(struct net_udp_desc_s		*desc,
                 struct net_udp_addr_s		*remote,
                 const void			*data,
                 size_t				size);

/**
   @this terminates an UDP session

   @param desc Session to close
 */
void	udp_close(struct net_udp_desc_s		*desc);

void	libudp_signal(struct net_packet_s	*packet,
                      struct udphdr		*hdr);

/**
   @this is used to globally cleanup LibUDP
 */
void	libudp_destroy(void);


NET_SIGNAL_ERROR(libudp_signal_error);

#endif
