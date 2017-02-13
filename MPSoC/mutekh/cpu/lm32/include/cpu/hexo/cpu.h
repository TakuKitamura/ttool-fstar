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

    Copyright Alexandre Becoulet <alexandre.becoulet@free.fr> (c) 2011
*/

#if !defined(__CPU_H_) || defined(CPU_CPU_H_)
#error This file can not be included directly
#else

#define CPU_CPU_H_

#ifndef __MUTEK_ASM__

/** general purpose regsiters count */
# define CPU_GPREG_COUNT	32

# define CPU_GPREG_NAMES                                       \
    "zero", "r1", "r2", "r3", "r4", "r5", "r6", "r7",             \
    "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15",     \
    "r16", "r17", "r18", "r19", "r20", "r21", "r22", "r23",   \
    "r24", "r25", "r26", "fp", "sp", "ra", "ea", "ba"

# undef lm32
# define CPU_TYPE_NAME lm32

static inline cpu_id_t
cpu_id(void)
{
  /** FIXME */

  return 0;
}

static inline
reg_t cpu_get_stackptr()
{
    reg_t ret;
    asm("mv %0, sp"
        : "=r" (ret));
    return ret;
}

static inline bool_t
cpu_isbootstrap(void)
{
  return cpu_id() == 0;
}

static inline cpu_cycle_t
cpu_cycle_count(void)
{
    reg_t ret;
    asm volatile ("rcsr %0, CC"
        : "=r" (ret));
    return ret;
}

static inline void
cpu_trap()
{
  asm volatile ("break");
}

static inline void *cpu_get_cls(cpu_id_t cpu_id)
{
  return NULL;
}

static inline void cpu_dcache_invld(void *ptr)
{
}

static inline size_t cpu_dcache_line_size()
{
  return CONFIG_CPU_CACHE_LINE;
}


#endif  /* __MUTEK_ASM__ */

#endif

