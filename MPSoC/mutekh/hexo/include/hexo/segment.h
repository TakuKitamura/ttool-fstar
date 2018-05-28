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

/**
 * @file
 * @module{Hexo}
 * @short Specific memory segments allocation functions
 */

#ifndef SEGMENT_H_
#define SEGMENT_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

#include "types.h"

/** allocate and setup cpu local storage memory */
static void * arch_cpudata_alloc(void);



/** allocate and setup context local storage memory */
static void * arch_contextdata_alloc(void);

/** free context local storage memory */
static void arch_contextdata_free(void *ptr);



/** allocate context stack memory */
static void * arch_contextstack_alloc(size_t size);

/** free context stack memory */
static void arch_contextstack_free(void *ptr);



#include "arch/hexo/segment.h"

C_HEADER_END

#endif

