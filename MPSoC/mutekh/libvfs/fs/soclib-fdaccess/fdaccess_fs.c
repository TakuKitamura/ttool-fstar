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

#include <hexo/types.h>
#include <hexo/lock.h>

#include <vfs/fs.h>
#include <vfs/ops.h>
#include <vfs/file.h>

#include <mutek/printk.h>

#include "soclib-fdaccess.h"
#include "soclib-fdaccess-private.h"

extern struct device_s *soclib_fdaccess_device; /* FIXME use device api to find device from fs part */

VFS_FILE_READ(soclib_fdaccess_file_read)
{
  struct fs_node_s *node = file->node;
  struct soclib_fdaccess_fs_s *pfs = node->fs;

  struct soclib_fdaccess_rq_s rq = {
    .op = SOCLIB_FDACCESS_NODE_READ,
    .buffer = buffer,
    .size = size,
    .mode = file->offset,
    .fd = node->host_fd,
  };

  ssize_t r = soclib_fdaccess_rq(pfs->dev, &rq);

  if (r > 0)
    file->offset += r;

  return r;
}

VFS_FILE_WRITE(soclib_fdaccess_file_write)
{
  struct fs_node_s *node = file->node;
  struct soclib_fdaccess_fs_s *pfs = node->fs;

  struct soclib_fdaccess_rq_s rq = {
    .op = SOCLIB_FDACCESS_NODE_WRITE,
    .buffer = (void*)buffer,
    .size = size,
    .mode = file->offset,
    .fd = node->host_fd,
  };

  ssize_t r = soclib_fdaccess_rq(pfs->dev, &rq);

  if (r > 0) {
    file->offset += r;
    if (file->offset > node->size)
      node->size = file->offset;
  }

  return r;
}

VFS_FILE_SEEK(soclib_fdaccess_file_seek)
{
  struct fs_node_s *node = file->node;

  switch (whence) {
  case VFS_SEEK_SET:
    break;
  case VFS_SEEK_CUR:
    offset += file->offset;
    break;
  case VFS_SEEK_END:
    offset += node->size;
    break;
  }

  if ( offset < 0 )
    offset = 0;

  file->offset = offset;

  return offset;
}

static VFS_FS_NODE_OPEN(soclib_fdaccess_node_open)
{
  struct vfs_file_s *f = vfs_file_new(NULL, node, soclib_fdaccess_node_refnew,
                                      soclib_fdaccess_node_refdrop);

  if (!f)
    return -ENOMEM;

  if ( flags & VFS_OPEN_DIR ) {
    return -ENOTSUP;
  } else {
    f->write = soclib_fdaccess_file_write;
    f->read = soclib_fdaccess_file_read;
    f->seek = soclib_fdaccess_file_seek;
  }

  *file = f;
  return 0;
}

static VFS_FS_CAN_UNMOUNT(soclib_fdaccess_can_unmount)
{
  return atomic_get(&fs->ref) == 1;
}

VFS_FS_LOOKUP(soclib_fdaccess_lookup)
{
  struct soclib_fdaccess_fs_s *fs = ref->fs;

  if ( ref->type != VFS_NODE_DIR )
    return -EINVAL;

  vfs_name_mangle(name, namelen, mangled_name);

  struct soclib_fdaccess_rq_s rq = {
    .op = SOCLIB_FDACCESS_NODE_LOOKUP,
    .buffer = (void*)name,
    .size = namelen,
    .fd = ref->host_fd,
  };

  if (soclib_fdaccess_rq(fs->dev, &rq))
    return -ENOENT; /* FIXME switch on errno */

  enum vfs_node_type_e type;

  if (rq.mode & SOCLIB_FDACCESS_S_IFDIR)
    type = VFS_NODE_DIR;
  else if (rq.mode & SOCLIB_FDACCESS_S_IFREG)
    type = VFS_NODE_FILE;
  else
    return -ENOTSUP;

  *node = (void*)soclib_fdaccess_node_new(NULL, fs, type, rq.fd);

  if (!*node)
    return -ENOMEM;

  return 0;
}

VFS_FS_CREATE(soclib_fdaccess_create)
{
  struct soclib_fdaccess_fs_s *pfs = (void*)fs;

  struct soclib_fdaccess_rq_s rq = {
    .op = SOCLIB_FDACCESS_NODE_CREATE,
  };

  switch (type)
    {
    case VFS_NODE_DIR:
      rq.mode = SOCLIB_FDACCESS_S_IFDIR;
      break;
    case VFS_NODE_FILE:
      rq.mode = SOCLIB_FDACCESS_S_IFREG;
      break;
    default:
      return -ENOTSUP;
    }

  if (soclib_fdaccess_rq(pfs->dev, &rq))
    return -ENOTSUP; /* FIXME switch on errno */

  struct fs_node_s *n = soclib_fdaccess_node_new(NULL, pfs, type, rq.fd);

  *node = (void*)n;
  return *node ? 0 : -ENOMEM;
}

VFS_FS_LINK(soclib_fdaccess_link)
{
  struct soclib_fdaccess_fs_s *pfs = parent->fs;

  if ( namelen >= CONFIG_VFS_NAMELEN )
    return -EINVAL;

  if ( parent->type != VFS_NODE_DIR )
    return -EINVAL;

  vfs_name_mangle(name, namelen, mangled_name);

  struct soclib_fdaccess_rq_s rq = {
    .op = SOCLIB_FDACCESS_NODE_LINK,
    .buffer = (void*)name,
    .size = namelen,
    .fd = parent->host_fd,
    .how = node->host_fd,
  };

  if (soclib_fdaccess_rq(pfs->dev, &rq))
    return -ENOTSUP; /* FIXME switch on errno */

  soclib_fdaccess_node_refnew(node);

  *rnode = node;
  return 0;
}

VFS_FS_MOVE(soclib_fdaccess_move)
{
  return -ENOTSUP;
}

VFS_FS_UNLINK(soclib_fdaccess_unlink)
{
  return -ENOTSUP;
}

VFS_FS_STAT(soclib_fdaccess_stat)
{
  return -ENOTSUP;
}

static const struct vfs_fs_ops_s soclib_fdaccess_fs_ops =
{
    .node_open = soclib_fdaccess_node_open,
    .can_unmount = soclib_fdaccess_can_unmount,
    .node_refdrop = soclib_fdaccess_node_refdrop,
    .node_refnew = soclib_fdaccess_node_refnew,

    .lookup = soclib_fdaccess_lookup,
    .create = soclib_fdaccess_create,
    .link = soclib_fdaccess_link,
    .move = soclib_fdaccess_move,
    .unlink = soclib_fdaccess_unlink,
    .stat = soclib_fdaccess_stat,
};

#if 0
struct fs_node_s *
soclib_fdaccess_node_new(void *mem, struct soclib_fdaccess_fs_s *fs,
                         enum vfs_node_type_e type, int32_t host_fd);
#endif

OBJECT_CONSTRUCTOR(soclib_fdaccess_node)
{
    struct soclib_fdaccess_fs_s *fs = va_arg(ap, struct soclib_fdaccess_fs_s *);
    enum vfs_node_type_e type = va_arg(ap, enum vfs_node_type_e);
    int32_t host_fd = va_arg(ap, int32_t);

    obj->type = type;
    obj->fs = fs;
    obj->host_fd = host_fd;

    return 0;
};

OBJECT_DESTRUCTOR(soclib_fdaccess_node)
{
  struct soclib_fdaccess_fs_s *pfs = obj->fs;

  struct soclib_fdaccess_rq_s rq = {
    .op = SOCLIB_FDACCESS_NODE_FREE,
    .fd = obj->host_fd,
  };

  soclib_fdaccess_rq(pfs->dev, &rq);
};

error_t soclib_fdaccess_open(struct vfs_fs_s **fs, struct device_s *dev)
{
  error_t err;
  dev = soclib_fdaccess_device;
  struct soclib_fdaccess_devpv_s *pv = dev->drv_pv;
  struct soclib_fdaccess_fs_s    *mnt = &pv->fs;

  memset(mnt, 0, sizeof(*mnt));
  vfs_fs_new(&mnt->fs);

  mnt->fs.ops = &soclib_fdaccess_fs_ops;
  mnt->fs.flag_ro = 0;
  mnt->fs.old_node = NULL;

  mnt->fs.root = soclib_fdaccess_node_new(NULL, &mnt->fs, VFS_NODE_DIR, 0);
  mnt->dev = dev;

  if (mnt->fs.root == NULL) {
    err = -ENOMEM;
    goto err;
  }

  *fs = &mnt->fs;

  printk("Soclib fdaccess _fs_ init done.\n");

  return 0;

 err:
  vfs_fs_refdrop(&mnt->fs);
  return err;
}

error_t soclib_fdaccess_close(struct vfs_fs_s *fs)
{
  /* FIXME */
  return -1;
}

