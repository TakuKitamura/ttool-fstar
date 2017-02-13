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

#if !defined(CONTEXT_H_) || defined(CPU_CONTEXT_H_)
#error This file can not be included directly
#else

# include <hexo/types.h>

# define CPU_SPARC_CONTEXT_RESTORE_CALLEE   1
# define CPU_SPARC_CONTEXT_RESTORE_CALLER   2

/** @multiple @this describes @ref cpu_context_s field offset */
#define CPU_SPARC_CONTEXT_SAVE_MASK       0
#define CPU_SPARC_CONTEXT_G(n)  	((n) * INT_REG_SIZE/8)
#define CPU_SPARC_CONTEXT_O(n)  	CPU_SPARC_CONTEXT_G((n)+8)
#define CPU_SPARC_CONTEXT_L(n)  	CPU_SPARC_CONTEXT_G((n)+16)
#define CPU_SPARC_CONTEXT_I(n)  	CPU_SPARC_CONTEXT_G((n)+24)
# define CPU_SPARC_CONTEXT_Y            (CPU_SPARC_CONTEXT_G(32))
# define CPU_SPARC_CONTEXT_PSR          (CPU_SPARC_CONTEXT_G(33))
# define CPU_SPARC_CONTEXT_PC           (CPU_SPARC_CONTEXT_G(34))
# define CPU_SPARC_CONTEXT_NPC          (CPU_SPARC_CONTEXT_G(35))
#ifdef CONFIG_HEXO_FPU
# define CPU_SPARC_CONTEXT_FP(n)        (CPU_SPARC_CONTEXT_G((n)+36))
#endif
/** */

#ifndef __MUTEK_ASM__

# include <hexo/cpu.h>

/** Sparc processor context state */
struct cpu_context_s
{
  union {
    reg_t save_mask;       //< what is being saved and restored
    struct {
      reg_t g[8];
      reg_t o[8];
      reg_t l[8];
      reg_t i[8];
    };
  };
  reg_t y;
  reg_t psr;
  reg_t pc;
  reg_t npc;
# ifdef CONFIG_HEXO_FPU
  union {
    float       s[32];
    double      d[16];
    __float128  q[8];
  }             fp;
# endif
};

# define CPU_CONTEXT_REG_NAMES CPU_GPREG_NAMES, "y", "psr", "pc", "npc"
# define CPU_CONTEXT_REG_FIRST 1
# define CPU_CONTEXT_REG_COUNT 36

# endif  /* __MUTEK_ASM__ */

#endif

