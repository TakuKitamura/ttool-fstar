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
    Copyright (c) 2011 Institut Telecom / Telecom ParisTech
*/

#if !defined(__CPU_H_) || defined(CPU_CPU_H_)
#error This file can not be included directly
#else

#define CPU_CPU_H_

/** sparc psr trap enabled bit */
#define SPARC_PSR_TRAP_ENABLED     0x20
/** sparc psr previous super user mode */
#define SPARC_PSR_PREV_SUSER_MODE 0x40
/** sparc psr current super user mode */
#define SPARC_PSR_SUSER_MODE      0x80
/** sparc psr proc interrupt level bits */
#define SPARC_PSR_PIL_MASK        0xf00
/** sparc psr fpu enabled */
#define SPARC_PSR_FPU_ENABLED     0x1000
/** sparc current window pointer mask */
#define SPARC_PSR_CWP_MASK        0x1f

#define SPARC_TRAP_USERBREAK    1
#define SPARC_TRAP_WINFLUSH     3

#ifndef __MUTEK_ASM__

# ifdef CONFIG_ARCH_SMP
extern void * cpu_local_storage[CONFIG_CPU_MAXCOUNT];
# endif

/** general purpose regsiters count */
# define CPU_GPREG_COUNT	32

# define CPU_GPREG_NAMES                                                          \
                "%g1", "%g2", "%g3", "%g4", "%g5", "%g6", "%g7",             \
                "%o0", "%o1", "%o2", "%o3", "%o4", "%o5", "%o6", "%o7",             \
                "%l0", "%l1", "%l2", "%l3", "%l4", "%l5", "%l6", "%l7",             \
                "%i0", "%i1", "%i2", "%i3", "%i4", "%i5", "%i6", "%i7"

# undef sparc
# define CPU_TYPE_NAME sparc

/** return sparc cpu windows count */
static inline size_t
cpu_sparc_wincount(void)
{
  uint32_t wim_mask = 0xffffffff;
  uint32_t tmp;

  asm ("  rd %%wim, %0   \n" // save wim
       "  wr %1, %%wim   \n" // write all ones
       "  nop \n nop \n nop \n"
       "  rd %%wim, %1   \n" // read back
       "  wr %0, %%wim   \n" // restore wim
       : "=r" (tmp), "=r" (wim_mask): "1" (wim_mask)
       );

  return __builtin_popcount(wim_mask);
}

static inline cpu_id_t
cpu_id(void)
{
  //  reg_t         reg;
  /** FIXME */

  return 0;
}

static inline
reg_t cpu_get_stackptr()
{
    reg_t ret;
    asm("mov %%o6, %0"
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
  extern cpu_cycle_t sparc_fake_tsc;
  return sparc_fake_tsc++;
}

static inline void
cpu_trap()
{
  asm volatile ("ta %0 \n"
                "nop \n"
                : : "i" (SPARC_TRAP_USERBREAK) );
}

static inline void *cpu_get_cls(cpu_id_t cpu_id)
{
# ifdef CONFIG_ARCH_SMP
  return cpu_local_storage[cpu_id];
# endif
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

