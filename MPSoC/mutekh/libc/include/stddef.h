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

#ifndef STDDEF_H_
#define STDDEF_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

/**
 * @file
 * @module{C library}
 */

#include <hexo/types.h>

#if (__GNUC__ > 3) || ((__GNUC__ == 3) && (__GNUC_MINOR__ >= 5))
/** standard @tt offsetof macro */
# define offsetof(t, f)	__builtin_offsetof(t, f)
#else
/** standard @tt offsetof macro */
# define offsetof(t, f) ((size_t) &((t*)0)->f)
#endif

C_HEADER_END

#endif

