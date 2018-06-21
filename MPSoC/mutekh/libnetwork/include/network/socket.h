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

#ifndef NETWORK_SOCKET_H
#define NETWORK_SOCKET_H

/**
   @file
   @module{Network library}
   @short Low-level Socket API
 */

#ifndef CONFIG_NETWORK_SOCKET
# warning Socket support is not enabled in configuration file
#endif

#include <hexo/types.h>
#include <netinet/socket.h>
#include <network/packet.h>

struct socket_s;
struct socket_api_s;

/* a socket is a socket_s structure */
typedef struct socket_s *socket_t;

/*
 * Socket API prototypes.
 */

#define _SOCKET(f)	error_t (f)(socket_t		fd,	\
				    int_fast32_t	domain,	\
				    int_fast32_t	type,	\
				    int_fast32_t	protocol)

#define _BIND(f)	int_fast32_t (f)(socket_t		fd,	\
					 struct sockaddr	*addr,	\
					 socklen_t		len)

#define _GETSOCKNAME(f)	int_fast32_t (f)(socket_t		fd,	\
					 struct sockaddr	*addr,	\
					 socklen_t		*len)

#define _CONNECT(f)	int_fast32_t (f)(socket_t		fd,	\
					 struct sockaddr	*addr,	\
					 socklen_t		len)

#define _GETPEERNAME(f)	int_fast32_t (f)(socket_t		fd,	\
					 struct sockaddr	*addr,	\
					 socklen_t		*len)

#define _SEND(f)	ssize_t (f)(socket_t		fd,	\
				    const void		*buf,	\
				    size_t		n,	\
				    int_fast32_t	flags)

#define _RECV(f)	ssize_t (f)(socket_t		fd,	\
				    void		*buf,	\
				    size_t		n,	\
				    int_fast32_t	flags)

#define _SENDTO(f)	ssize_t (f)(socket_t		fd,		\
				    const void		*buf,		\
				    size_t		n,		\
				    int_fast32_t	flags,		\
				    struct sockaddr	*addr,		\
				    socklen_t		addr_len)


#define _RECVFROM(f)	ssize_t (f)(socket_t		fd,		\
				    void		*buf,		\
				    size_t		n,		\
				    int_fast32_t	flags,		\
				    struct sockaddr	*addr,		\
				    socklen_t		*addr_len)

#define _SENDMSG(f)	ssize_t (f)(socket_t		fd,		\
				    const struct msghdr	*message,	\
				    int_fast32_t	flags)

#define _RECVMSG(f)	ssize_t (f)(socket_t		fd,		\
				    struct msghdr	*message,	\
				    int_fast32_t	flags)

#define _GETSOCKOPT(f)	int_fast32_t (f)(socket_t	fd,		\
					 int_fast32_t	level,		\
					 int_fast32_t	optname,	\
					 void		*optval,	\
					 socklen_t	*optlen)

#define _SETSOCKOPT(f)	int_fast32_t (f)(socket_t	fd,		\
					 int_fast32_t	level,		\
					 int_fast32_t	optname,	\
					 const void	*optval,	\
					 socklen_t	optlen)

#define _LISTEN(f)	int_fast32_t (f)(socket_t	fd,	\
					 int_fast32_t	n)

#define _ACCEPT(f)	int_fast32_t (f)(socket_t		fd,		\
					 struct sockaddr	*addr,		\
					 socklen_t		*addr_len)

#define _SHUTDOWN(f)	int_fast32_t (f)(socket_t	fd,	\
					 int_fast32_t	how)

typedef _SOCKET(_socket_t);
typedef _BIND(_bind_t);
typedef _GETSOCKNAME(_getsockname_t);
typedef _CONNECT(_connect_t);
typedef _GETPEERNAME(_getpeername_t);
typedef _SEND(_send_t);
typedef _RECV(_recv_t);
typedef _SENDTO(_sendto_t);
typedef _RECVFROM(_recvfrom_t);
typedef _SENDMSG(_sendmsg_t);
typedef _RECVMSG(_recvmsg_t);
typedef _GETSOCKOPT(_getsockopt_t);
typedef _SETSOCKOPT(_setsockopt_t);
typedef _LISTEN(_listen_t);
typedef _ACCEPT(_accept_t);
typedef _SHUTDOWN(_shutdown_t);

/*
 * Socket dispatch structure.
 */

struct			socket_api_s
{
  _socket_t		*socket;
  _bind_t		*bind;
  _getsockname_t	*getsockname;
  _connect_t		*connect;
  _getpeername_t	*getpeername;
  _sendmsg_t		*sendmsg;
  _recvmsg_t		*recvmsg;
  _getsockopt_t		*getsockopt;
  _setsockopt_t		*setsockopt;
  _listen_t		*listen;
  _accept_t		*accept;
  _shutdown_t		*shutdown;
};

/*
 * A socket.
 */

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/cont_dlist.h>

struct				socket_s
{
  const struct socket_api_s	*f;
  int_fast32_t			type;
  int_fast32_t			shutdown;
  error_t			error;
  bool_t			broadcast;
  bool_t			keepalive;
  timer_delay_t			recv_timeout;
  timer_delay_t			send_timeout;
  timer_delay_t			linger;
  void				*pv;

  CONTAINER_ENTRY_TYPE(DLIST)	list_entry;
};

#define CONTAINER_LOCK_socket_table	HEXO_SPIN
CONTAINER_TYPE(socket_table, DLIST, struct socket_s, list_entry);
CONTAINER_FUNC(socket_table, DLIST, static inline, socket_table);

#include "socket_hexo.h"

#endif
