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


#if !defined(SEGMENT_H_) || defined(ARCH_SEGMENT_H_)
#error This file can not be included directly
#else

#define ARCH_SEGMENT_H_

#include <hexo/cpu.h>

#include "hexo/types.h"
#include "mutek/mem_alloc.h"
#include "string.h"

/* System global heap memory */
extern __ldscript_symbol_t __system_heap_start;

#ifdef CONFIG_ARCH_SMP
/* cpu template segment load address defined in ld script*/
extern __ldscript_symbol_t __cpu_data_start, __cpu_data_end;
#endif

/* boot section address in memory */
extern __ldscript_symbol_t __boot_start, __boot_end;


static inline void *
arch_cpudata_alloc(void)
{
#ifdef CONFIG_ARCH_SMP
  void			*cls;

  /* allocate memory and copy from template */
  if ((cls = mem_alloc((char*)&__cpu_data_end - (char*)&__cpu_data_start, (mem_scope_sys))))
    {
      memcpy_from_code(cls, (char*)&__cpu_data_start, (char*)&__cpu_data_end - (char*)&__cpu_data_start);
    }

  return cls;
#else
  cpu_trap();
  return NULL;
#endif
}

static inline size_t
arch_cpudata_size(void)
{
#ifdef CONFIG_ARCH_SMP
  return (char*)&__cpu_data_end - (char*)&__cpu_data_start;
#else
  return 0;
#endif
}

/* context template segment load address defined in ld script*/
extern __ldscript_symbol_t __context_data_start, __context_data_end;

static inline void *
arch_contextdata_alloc(void)
{
  void			*tls;

  /* allocate memory and copy from template */
  if ((tls = mem_alloc((char*)&__context_data_end - (char*)&__context_data_start, (mem_scope_sys))))
    {
      memcpy_from_code(tls, (char*)&__context_data_start, (char*)&__context_data_end - (char*)&__context_data_start);
    }

  return tls;
}

static inline size_t
arch_contextdata_size(void)
{
  return (char*)&__context_data_end - (char*)&__context_data_start;
}

static inline void
arch_contextdata_free(void *ptr)
{
  mem_free(ptr);
}

static inline void *
arch_contextstack_alloc(size_t size)
{
  return mem_alloc(size, (mem_scope_sys));
}

static inline void
arch_contextstack_free(void *ptr)
{
  mem_free(ptr);
}

#endif

