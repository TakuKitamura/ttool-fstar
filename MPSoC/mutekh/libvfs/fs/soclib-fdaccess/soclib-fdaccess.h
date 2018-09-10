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

#ifndef _SOCLIB_FDACCESS_H_
#define _SOCLIB_FDACCESS_H_

#include <vfs/fs.h>

error_t soclib_fdaccess_open(struct vfs_fs_s **fs, struct device_s *dev);
error_t soclib_fdaccess_close(struct vfs_fs_s *fs);

VFS_FS_LOOKUP(soclib_fdaccess_lookup);
VFS_FS_CREATE(soclib_fdaccess_create);
VFS_FS_LINK(soclib_fdaccess_link);
VFS_FS_UNLINK(soclib_fdaccess_unlink);
VFS_FS_STAT(soclib_fdaccess_stat);

#include <device/device.h>

DEV_IRQ(soclib_fdaccess_irq);
DEV_INIT(soclib_fdaccess_init);
DEV_CLEANUP(soclib_fdaccess_cleanup);

#endif

