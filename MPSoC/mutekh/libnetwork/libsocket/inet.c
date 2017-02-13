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

#include <hexo/error.h>

#include <netinet/socket.h>
#include <network/socket.h>
#include <netinet/in.h>

error_t		inet_aton(const char *cp, struct in_addr *inp)
{
  char		*p;
  uint_fast8_t	i;
  uint_fast32_t	ip = 0;

  i = strto_uintl16(cp, &p, 10);
  if (*p != '.')
    return 0;

  ip = i << 24;

  p++;
  i = strto_uintl16(p, &p, 10);
  if (*p != '.')
    return 0;

  ip |= (i << 16);

  p++;
  i = strto_uintl16(p, &p, 10);
  if (*p != '.')
    return 0;

  ip |= (i << 8);

  p++;
  i = strto_uintl16(p, &p, 10);
  if (*p != 0)
    return 0;

  ip |= i;

  inp->s_addr = htonl(ip);

  return 1;
}
