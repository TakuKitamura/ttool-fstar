/*
    This file is part of MutekH.

    MutekH is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MutekH is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MutekH; if not, write to the Free Software Foundation,
    Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

    Copyright (c) 2011 Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr>
*/

#ifndef _SOCLIB_FDACCESS_PRIVATE_H_
#define _SOCLIB_FDACCESS_PRIVATE_H_

#include <vfs/types.h>
#include <vfs/fs.h>
#include <vfs/ops.h>
#include <vfs/file.h>

struct soclib_fdaccess_devpv_s;

OBJECT_TYPE     (soclib_fdaccess_node, REFCOUNT, struct fs_node_s);
OBJECT_PROTOTYPE(soclib_fdaccess_node, static inline, soclib_fdaccess_node);

struct fs_node_s
{
  soclib_fdaccess_node_entry_t obj_entry;

  struct soclib_fdaccess_fs_s *fs;
  uint32_t                     host_fd;
  enum vfs_node_type_e         type;
  size_t                       size;
};

OBJECT_CONSTRUCTOR(soclib_fdaccess_node);
OBJECT_DESTRUCTOR(soclib_fdaccess_node);

struct soclib_fdaccess_fs_s
{
  struct vfs_fs_s		 fs; /* keep first field */
  struct fs_node_s		 *root;
  struct device_s                *dev;
};

OBJECT_FUNC   (soclib_fdaccess_node, REFCOUNT, static inline, soclib_fdaccess_node, obj_entry);

struct soclib_fdaccess_rq_s
{
#ifdef CONFIG_HEXO_IRQ
  CONTAINER_ENTRY_TYPE(CLIST)	queue_entry;
# ifndef CONFIG_MUTEK_SCHEDULER
  bool_t done;
# endif
#endif

#if defined(CONFIG_MUTEK_SCHEDULER) && defined(CONFIG_HEXO_IRQ)
  struct sched_context_s *ctx;
#endif

  uint32_t op;
  void *buffer;
  uint32_t size;
  int32_t fd;
  uint32_t how;
  uint32_t mode;
  int32_t retval;
  bool_t done;
};

#ifdef CONFIG_HEXO_IRQ
CONTAINER_TYPE(soclib_fdaccess_rq_queue, CLIST, struct soclib_fdaccess_rq_s, queue_entry);
CONTAINER_FUNC(soclib_fdaccess_rq_queue, CLIST, static inline, soclib_fdaccess_rq);
#endif

int32_t soclib_fdaccess_rq(struct device_s *dev,
                           struct soclib_fdaccess_rq_s *rq);

struct soclib_fdaccess_devpv_s
{
  struct soclib_fdaccess_fs_s         fs;
  soclib_fdaccess_rq_queue_root_t     queue;
};

enum {
    SOCLIB_FDACCESS_FD = 0,
    SOCLIB_FDACCESS_BUFFER = 4,
    SOCLIB_FDACCESS_SIZE = 8,
    SOCLIB_FDACCESS_HOW = 12,
    SOCLIB_FDACCESS_MODE = 16,
    SOCLIB_FDACCESS_OP = 20,
    SOCLIB_FDACCESS_RETVAL = 24,
    SOCLIB_FDACCESS_ERRNO = 28,
    SOCLIB_FDACCESS_IRQ_ENABLE = 32,
};

enum SoclibFdOp {
    SOCLIB_FDACCESS_NOOP,
    SOCLIB_FDACCESS_OPEN,
    SOCLIB_FDACCESS_CLOSE,
    SOCLIB_FDACCESS_READ,
    SOCLIB_FDACCESS_WRITE,
    SOCLIB_FDACCESS_LSEEK,

    SOCLIB_FDACCESS_NODE_LOOKUP,
    SOCLIB_FDACCESS_NODE_CREATE,
    SOCLIB_FDACCESS_NODE_FREE,
    SOCLIB_FDACCESS_NODE_LINK,
    SOCLIB_FDACCESS_NODE_UNLINK,
    SOCLIB_FDACCESS_NODE_READ,
    SOCLIB_FDACCESS_NODE_READDIR,
    SOCLIB_FDACCESS_NODE_WRITE,
    SOCLIB_FDACCESS_NODE_STAT,
};

#define SOCLIB_FDACCESS_OS_PROT(x) SOCLIB_FDACCESS_##x

enum {
    SOCLIB_FDACCESS_OS_PROT(O_RDONLY)   = 0000001,
    SOCLIB_FDACCESS_OS_PROT(O_WRONLY)   = 0000002,
    SOCLIB_FDACCESS_OS_PROT(O_RDWR)     = 0000004,
    SOCLIB_FDACCESS_OS_PROT(O_CREAT)    = 0000010,
    SOCLIB_FDACCESS_OS_PROT(O_EXCL)     = 0000020,
    SOCLIB_FDACCESS_OS_PROT(O_NOCTTY)   = 0000040,
    SOCLIB_FDACCESS_OS_PROT(O_TRUNC)    = 0000100,
    SOCLIB_FDACCESS_OS_PROT(O_APPEND)   = 0000200,
    SOCLIB_FDACCESS_OS_PROT(O_NONBLOCK) = 0000400,
    SOCLIB_FDACCESS_OS_PROT(O_SYNC)     = 0001000,
    SOCLIB_FDACCESS_OS_PROT(O_DIRECT)   = 0004000,
    SOCLIB_FDACCESS_OS_PROT(O_LARGEFILE)= 0010000,
    SOCLIB_FDACCESS_OS_PROT(O_DIRECTORY)= 0020000,
    SOCLIB_FDACCESS_OS_PROT(O_NOFOLLOW) = 0040000,
    SOCLIB_FDACCESS_OS_PROT(O_NOATIME)  = 0100000,
    SOCLIB_FDACCESS_OS_PROT(O_NDELAY)   = SOCLIB_FDACCESS_OS_PROT(O_NONBLOCK),

    SOCLIB_FDACCESS_OS_PROT(S_IFMT   ) =  0170000, //   bit mask for the file type bit fields                                                                                                                                                                                                                                                                                    
    SOCLIB_FDACCESS_OS_PROT(S_IFSOCK ) =  0140000, //   socket                                                                                                                                                                                                                                                                                                                   
    SOCLIB_FDACCESS_OS_PROT(S_IFLNK  ) =  0120000, //   symbolic link                                                                                                                                                                                                                                                                                                            
    SOCLIB_FDACCESS_OS_PROT(S_IFREG  ) =  0100000, //   regular file                                                                                                                                                                                                                                                                                                             
    SOCLIB_FDACCESS_OS_PROT(S_IFBLK  ) =  0060000, //   block device                                                                                                                                                                                                                                                                                                             
    SOCLIB_FDACCESS_OS_PROT(S_IFDIR  ) =  0040000, //   directory                                                                                                                                                                                                                                                                                                                
    SOCLIB_FDACCESS_OS_PROT(S_IFCHR  ) =  0020000, //   character device                                                                                                                                                                                                                                                                                                         
    SOCLIB_FDACCESS_OS_PROT(S_IFIFO  ) =  0010000, //   FIFO                                                                                                                                                                                                                                                                                                                     
    SOCLIB_FDACCESS_OS_PROT(S_ISUID  ) =  0004000, //   set UID bit                                                                                                                                                                                                                                                                                                              
    SOCLIB_FDACCESS_OS_PROT(S_ISGID  ) =  0002000, //   set-group-ID bit (see below)                                                                                                                                                                                                                                                                                             
    SOCLIB_FDACCESS_OS_PROT(S_ISVTX  ) =  0001000, //   sticky bit (see below)                                                                                                                                                                                                                                                                                                   
};


#endif

