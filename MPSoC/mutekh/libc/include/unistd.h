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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006

*/

#ifndef UNISTD_H_
#define UNISTD_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

/**
 * @file
 * @module{C library}
 */

#include <hexo/types.h>
#include <hexo/error.h>
#include <mutek/fileops.h>

#define PATH_MAX 512

typedef int_fast8_t fd_t;
typedef int16_t mode_t;

/* ************************************************** */

#ifdef CONFIG_LIBC_UNIXFD
/* setup fd 0, 1, 2 */
void libc_unixfd_init();
#endif

enum open_flags_e
  {
    O_RDONLY	= 0x01,
    O_WRONLY	= 0x02,
    O_RDWR	= 0x03,
    O_CREAT	= 0x10,
    O_TRUNC	= 0x20,
    O_APPEND	= 0x40,
  };

config_depend_and2(CONFIG_LIBC_UNIXFD, CONFIG_VFS)
fd_t creat(const char *pathname, mode_t mode);

config_depend_and2(CONFIG_LIBC_UNIXFD, CONFIG_VFS)
fd_t open(const char *pathname, enum open_flags_e flags, /* mode_t mode */...);

config_depend(CONFIG_LIBC_UNIXFD)
off_t lseek(fd_t fildes, off_t offset, enum seek_whence_e whence);

config_depend(CONFIG_LIBC_UNIXFD)
ssize_t read(fd_t fd, void *buf, size_t count);

config_depend(CONFIG_LIBC_UNIXFD)
ssize_t write(fd_t fd, const void *buf, size_t count);

config_depend(CONFIG_LIBC_UNIXFD)
error_t close(fd_t fd);

/* ************************************************** */

typedef uint_fast8_t dev_t;
typedef uint_fast32_t ino_t;
typedef uint_fast8_t nlink_t;
typedef uint_fast16_t uid_t;
typedef uint_fast16_t gid_t;
typedef uint_fast16_t blksize_t;
typedef uint_fast32_t blkcnt_t;

#include <sys/stat.h>

config_depend(CONFIG_VFS)
error_t stat(const char *path, struct stat *buf);

config_depend(CONFIG_VFS)
error_t lstat(const char *path, struct stat *buf);

enum access_perm_e
  {
    R_OK,               /* Test for read permission.  */
    W_OK,               /* Test for write permission.  */
    X_OK,               /* Test for execute permission.  */
    F_OK,               /* Test for existence.  */
  };

config_depend(CONFIG_VFS)
error_t access(const char *pathname, enum access_perm_e mode);

/* ************************************************** */

config_depend(CONFIG_VFS)
error_t remove(const char *pathname);

config_depend(CONFIG_VFS)
error_t mkdir(const char *pathname, mode_t mode);

/* ************************************************** */

config_depend(CONFIG_MUTEK_TIMER_EVENTS)
error_t usleep(uint_fast32_t usec);

config_depend(CONFIG_MUTEK_TIMER_EVENTS)
error_t sleep(uint_fast32_t usec);

C_HEADER_END

#endif

