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

#ifndef NETWORK_PROTOS_H_
#define NETWORK_PROTOS_H_

/**
   @file
   @module{Network library}
   @short Internal protocol definitions
 */

#include <hexo/types.h>
#include <hexo/error.h>
#include <mutek/mem_alloc.h>

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/cont_hashlist.h>
#include <gpct/object_refcount.h>

struct net_if_s;
struct net_packet_s;
struct net_addr_s;
struct net_proto_s;

/**
   Error types for control protocols (ICMP for example).
 */

#define	ERROR_NET_UNREACHABLE		0
#define	ERROR_HOST_UNREACHABLE		1
#define	ERROR_PROTO_UNREACHABLE		2
#define	ERROR_PORT_UNREACHABLE		3
#define	ERROR_CANNOT_FRAGMENT		4
#define	ERROR_NET_DENIED		5
#define	ERROR_HOST_DENIED		6
#define	ERROR_CONGESTION		7
#define ERROR_TIMEOUT			8
#define ERROR_FRAGMENT_TIMEOUT		9
#define ERROR_BAD_HEADER		10
#define	ERROR_UNKNOWN			11

/**
   @this is the prototype for a push function.

   A push function passes a packet from lower-level parts of the
   kernel (network device "side") towards upper levels (application
   "side") of the network stack.

   @param interface The incoming interface
   @param packet The incoming packet
   @param protocol The protocol internal state
 */
#define NET_PUSHPKT(f)	void (f)(struct net_if_s	*interface,	\
				 struct net_packet_s	*packet,	\
				 struct	net_proto_s	*protocol)

/** pushpkt function type */
typedef NET_PUSHPKT(net_pushpkt_t);

/**
   @this is the prototype for a packet preparation function.

   A packet preparation fonction prepare packet headers for future
   sending of a packet of known size. It does not need to know about
   the actual data.

   @param interface The desired output interface
   @param packet the packet descriptor
   @param size of the payload
   @returns the address where the caller can write its payload of size
            @tt size
 */

#define NET_PREPAREPKT(f)	uint8_t *(f)(struct net_if_s	*interface,\
					 struct net_packet_s	*packet,   \
					 size_t			size,	   \
					 size_t			max_padding)

/** preparepkt function type */
typedef NET_PREPAREPKT(net_preparepkt_t);

/**
   @this initializes a protocol internal state
 */
#define NET_INITPROTO(f)	error_t (f)(struct net_if_s	*interface,	\
					    struct net_proto_s	*proto,		\
					    va_list		va)

/** protocol initializer type */
typedef NET_INITPROTO(net_initproto_t);

/**
   @this is used to clear a protocol and its private data

   @param proto Protocol state to destroy
 */
#define NET_DESTROYPROTO(f)	void (f)(struct net_proto_s	*proto)

/** protocol destructor type */
typedef NET_DESTROYPROTO(net_destroyproto_t);

typedef uint_fast16_t net_pkt_size_t;
typedef uint_fast16_t net_proto_id_t;
typedef uint_fast16_t net_error_id_t;
typedef uint_fast16_t net_port_t;

/**
   @this is used to send a packet. Packets must be previously be
   prepared with @see #NET_PREPAREPKT.

   @param interface Outgoing interface
   @param packet Packet descriptor buffer
   @param protocol Encapsulating protocol descriptor
   @param proto Encapsulated protocol id
 */
#define NET_SENDPKT(f)		void (f)(struct net_if_s	*interface,  \
					 struct net_packet_s	*packet,     \
					 struct net_proto_s	*protocol,   \
					 net_proto_id_t		proto)

/** send function type */
typedef NET_SENDPKT(net_sendpkt_t);

/**
   @this tells whether two addresses match for a given protocol and
   mask.

   @param protocol Protocol used for matching
   @param a First address to compare
   @param a Second address to compare
   @param mask Mask for comparaison, may be NULL for equality match

   @returns whether the two addresses match
 */
#define NET_MATCHADDR(f) uint_fast8_t (f)(struct net_proto_s	*protocol,\
					  struct net_addr_s	*a,	  \
					  struct net_addr_s	*b,	  \
					  struct net_addr_s	*mask)

/** match function type */
typedef NET_MATCHADDR(net_matchaddr_t);

/**
   @this computes the pseudo header checksum.

   @param addressing Used encapsulating protocol
   @param packet Packet descriptor
   @param proto Protocol id of encapsulated protocol
   @param size Size of encapsulated payload
 */

#define NET_PSEUDOHEADER_CHECKSUM(f)					\
  uint16_t	(f)(struct net_proto_s	*addressing,			\
		    struct net_packet_s	*packet,			\
		    net_proto_id_t	proto,				\
		    uint_fast16_t	size)

typedef NET_PSEUDOHEADER_CHECKSUM(net_pseudoheader_checksum_t);

/**
   @this reports error.

   @param erroneous Erroneous packet to signal error on
   @param error Error type
 */
#define NET_ERRORMSG(f)	void	(f)(struct net_packet_s	*erroneous,	\
				    net_error_id_t	error, ...)

typedef NET_ERRORMSG(net_errormsg_t);

/**
   @this signals error to an higher level.

   @param error Error type
 */
#define NET_SIGNAL_ERROR(f)	void	(f)(net_error_id_t	error,		\
					    struct net_addr_s	*address,	\
					    net_port_t		port)

typedef NET_SIGNAL_ERROR(net_signal_error_t);

/**
   @this defines the interface of an addressing protocol.
 */
struct				net_addressing_interface_s
{
  net_sendpkt_t			*sendpkt;
  net_matchaddr_t		*matchaddr;
  net_pseudoheader_checksum_t	*pseudoheader_checksum;
  net_errormsg_t		*errormsg;
};

/**
   @this defines the interface of a control protocol.
 */
struct				net_control_interface_s
{
  net_errormsg_t		*errormsg;
};

/**
   @this is a protocol definition.
 */
struct					net_proto_desc_s
{
  const char				*name;	//< the name of the protocol 
  net_proto_id_t			id;	//< protocol identifier 
  net_pushpkt_t				*pushpkt; //< push packet function 
  net_preparepkt_t			*preparepkt; //< prepare packet func 
  net_initproto_t			*initproto; //< init pv data 
  net_destroyproto_t			*destroyproto; //< clear pv data 
  union
  {
    const struct net_addressing_interface_s	*addressing;
    const struct net_control_interface_s	*control;
  } f;
  size_t				pv_size;
};

OBJECT_TYPE(net_proto_obj, REFCOUNT, struct net_proto_s);

/**
   @this is a protocol state.
 */
struct					net_proto_s
{
  const struct net_proto_desc_s		*desc;	//< protocol descriptor 
  net_proto_id_t			id;	//< protocol identifier 
  struct net_proto_pv_s			*pv;	//< private data 
  bool_t				initialized;

  net_proto_obj_entry_t			obj_entry;
  CONTAINER_ENTRY_TYPE(HASHLIST)	list_entry;
};

OBJECT_CONSTRUCTOR(net_proto_obj);
OBJECT_DESTRUCTOR(net_proto_obj);
OBJECT_FUNC(net_proto_obj, REFCOUNT, static inline, net_proto_obj, obj_entry);

/*
 * Container type for protocols list.
 */

#define CONTAINER_OBJ_net_protos	net_proto_obj
CONTAINER_TYPE(net_protos, HASHLIST, struct net_proto_s, list_entry, 8);
CONTAINER_KEY_TYPE(net_protos, PTR, SCALAR, id);

/*
 * Container functions.
 */

CONTAINER_FUNC(net_protos, HASHLIST, static inline, net_protos, id);
CONTAINER_KEY_FUNC(net_protos, HASHLIST, static inline, net_protos, id);

/*
 * Foreach
 */

#define NET_FOREACH_PROTO(Protocols,Id,Code)							\
  {												\
    struct net_proto_s	*item;									\
												\
    for (item = net_protos_lookup((Protocols), (Id));						\
	 item != NULL;										\
	 )											\
      {												\
	struct net_proto_s	*__item;							\
												\
	Code;											\
												\
	__item = item;										\
	item = net_protos_lookup_next((Protocols), item, (Id));					\
	net_proto_obj_refdrop(__item);								\
      }												\
  }

#define NET_FOREACH_PROTO_BREAK									\
  {												\
    net_proto_obj_refdrop(item);								\
    break;											\
  }

#endif

