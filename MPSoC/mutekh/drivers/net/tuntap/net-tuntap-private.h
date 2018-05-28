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

#ifndef DRIVERS_TUNTAP_PRIVATE_H
#define DRIVERS_TUNTAP_PRIVATE_H

#include <hexo/types.h>
#include <hexo/lock.h>

#include <pthread.h>

#include <network/packet.h>
#include <netinet/ether.h>
#include <network/protos.h>
#include <network/if.h>

#include <hexo/gpct_platform_hexo.h>
#include <gpct/cont_clist.h>

struct				net_tuntap_context_s
{
  struct net_dispatch_s *dispatch;
  lock_t			lock;
  bool_t			run;

  uint8_t			mac[ETH_ALEN];
  struct net_if_s		*interface;
  int				fd;
};

#endif
