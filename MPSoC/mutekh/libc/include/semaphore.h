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

#ifndef SEMAPHORE_H_
#define SEMAPHORE_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

/**
 * @file
 * @module{C library}
 */

#include <mutek/semaphore.h>

config_depend(CONFIG_LIBC_SEMAPHORE)
typedef struct semaphore_s sem_t;

config_depend_inline(CONFIG_LIBC_SEMAPHORE,
error_t sem_init(sem_t *sem, bool_t pshared, uint_fast8_t value),
{
	return semaphore_init(sem, value);
});

config_depend_inline(CONFIG_LIBC_SEMAPHORE,
error_t sem_wait(sem_t *sem),
{
	semaphore_take(sem, 1);
	return 0;
});

config_depend_inline(CONFIG_LIBC_SEMAPHORE,
error_t sem_trywait(sem_t *sem),
{
	return semaphore_try_take(sem, 1);
});

config_depend_inline(CONFIG_LIBC_SEMAPHORE,
error_t sem_post(sem_t *sem),
{
	semaphore_give(sem, 1);
	return 0;
});

config_depend_inline(CONFIG_LIBC_SEMAPHORE,
error_t sem_getvalue(sem_t *sem, uint_fast8_t *sval),
{
	*sval = semaphore_value(sem);
	return 0;
});

config_depend_inline(CONFIG_LIBC_SEMAPHORE,
error_t sem_destroy(sem_t *sem),
{
	semaphore_destroy(sem);
	return 0;
});

C_HEADER_END

#endif

