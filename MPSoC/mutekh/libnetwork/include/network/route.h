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

#ifndef NETWORK_ROUTE_H
#define NETWORK_ROUTE_H

/**
   @file
   @module{Network library}
   @short Routing stack
 */

#include <hexo/types.h>
#include <hexo/error.h>

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/object_refcount.h>
#include <gpct/cont_dlist.h>

#include <network/packet.h>

struct net_if_s;

/*
 * Structure defining a route.
 */

OBJECT_TYPE(route_obj, REFCOUNT, struct net_route_s);

struct				net_route_s
{
  struct net_if_s		*interface;
  struct net_proto_s		*addressing;
  struct net_addr_s		target;
  bool_t			is_routed;
  struct net_addr_s		mask;
  struct net_addr_s		router;
  bool_t			invalidated;

  route_obj_entry_t		obj_entry;
  CONTAINER_ENTRY_TYPE(DLIST)	list_entry;
};

OBJECT_CONSTRUCTOR(route_obj);
OBJECT_DESTRUCTOR(route_obj);
OBJECT_FUNC(route_obj, REFCOUNT, static inline, route_obj, obj_entry);

/*
 * Route table container.
 */

#define CONTAINER_OBJ_route_table	route_obj
#define CONTAINER_LOCK_route_table	HEXO_SPIN
CONTAINER_TYPE(route_table, DLIST, struct net_route_s, list_entry);

/*
 * Prototypes
 */

error_t			route_add(struct net_route_s	*route);
struct net_route_s	*route_get(struct net_addr_s	*addr);
void			route_flush(struct net_if_s	*interface);
void			route_del(struct net_route_s	*route);
void			route_dump(void);

#endif
