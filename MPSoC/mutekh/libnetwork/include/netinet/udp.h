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

#ifndef NETINET_UDP_H_
#define NETINET_UDP_H_

/**
   @file
   @module{Network library}
   @short Standard header for UDP definitions
 */

#ifndef CONFIG_NETWORK_UDP
# warning UDP support is not enabled in configuration file
#endif

#include <hexo/types.h>

/* The UDP packet header */
struct		udphdr
{
  uint16_t	source;
  uint16_t	dest;
  uint16_t	len;
  uint16_t	check;
} __attribute__ ((packed));

#endif

