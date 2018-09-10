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
 * @short Startup and misc cpu related functions
 */

#ifndef __CPU_H_
#define __CPU_H_

#include <hexo/types.h>
#include <hexo/error.h>

#include <hexo/decls.h>

#ifndef __MUTEK_ASM__

C_HEADER_BEGIN

typedef uint64_t cpu_cycle_t;

#endif

#include <cpu/hexo/cpu.h>

#ifndef __MUTEK_ASM__

/** init system wide cpu data */
error_t cpu_global_init(void);

/** Setup CPU specific data */
void cpu_init(void);

/** get cpu local storage */
static void *cpu_get_cls(cpu_id_t cpu_id);

/** return CPU id number */
static cpu_id_t cpu_id(void);

/** return CPU architecture type name */
static const char *cpu_type_name(void);

/** return true if bootstap processor */
static bool_t cpu_isbootstrap(void);

/** return total cpus count */
size_t arch_get_cpu_count(void);

/** unlock non first CPUs so that they can enter main_smp() */
void arch_start_other_cpu(void);

/** return processor cycles count timestamp */
cpu_cycle_t cpu_cycle_count(void);

/** return number of cycles spent since @tt start stamp. */
static inline cpu_cycle_t cpu_cycle_diff(cpu_cycle_t start)
{
  cpu_cycle_t now = cpu_cycle_count();

  /* handle 32bits wrap */
  if (now < start)
    now += 0x100000000ULL;
  return now - start;
}

static inline
void cpu_cycle_wait(cpu_cycle_t delta)
{
    delta += cpu_cycle_count();
    while ( cpu_cycle_count() < delta )
        ;
}

/** cpu trap instruction */
void cpu_trap();

/** get cpu cache line size, return 0 if no dcache */
static size_t cpu_dcache_line_size();

/** invalidate the cpu data cache line containing this address */
static void cpu_dcache_invld(void *ptr);

#  if defined(CONFIG_CPU_CACHE)

/** invalidate all the cpu data cache lines within given range.
    size is in bytes. */
void cpu_dcache_invld_buf(void *ptr, size_t size);

#  else

static inline void
cpu_dcache_invld_buf(void *ptr, size_t size)
{
}

#  endif

# define _TO_STR(x) #x
# define TO_STR(x) _TO_STR(x)

/** @this returns the cpu type name */
static inline const char *
cpu_type_name(void)
{
  return TO_STR(CPU_TYPE_NAME);
}

# define __CPU_NAME_DECL(t, x) t##_##x
# define _CPU_NAME_DECL(t, x) __CPU_NAME_DECL(t, x)
/** @this can be used to declare and refer to a variable
    or function prefixed by cpu type name. */
# define CPU_NAME_DECL(x) _CPU_NAME_DECL(CPU_TYPE_NAME, x)

C_HEADER_END

#endif  /* __MUTEK_ASM__ */

#endif
