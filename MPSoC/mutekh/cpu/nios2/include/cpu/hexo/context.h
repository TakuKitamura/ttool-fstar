/*
 *   This file is part of MutekH.
 *   
 *   MutekH is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; version 2.1 of the
 *   License.
 *   
 *   MutekH is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *   
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with MutekH; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *   02110-1301 USA.
 *
 *   Copyright Francois Charot <charot@irisa.fr>  (c) 2008
 *   INRIA Rennes Bretagne Atlantique
 *
 */


#if !defined(CONTEXT_H_) || defined(CPU_CONTEXT_H_)
#error This file can not be included directly
#else

# include <hexo/types.h>

# define CPU_NIOS2_CONTEXT_RESTORE_CALLEE   1
# define CPU_NIOS2_CONTEXT_RESTORE_CALLER   2

# define CPU_NIOS2_CONTEXT_RESTORE_NONE     (~3)

#define CPU_NIOS2_CONTEXT_SAVE_MASK      0
#define CPU_NIOS2_CONTEXT_GPR(n)         (CPU_NIOS2_CONTEXT_SAVE_MASK + INT_REG_SIZE/8 + (n - 1) * INT_REG_SIZE/8)
#define CPU_NIOS2_CONTEXT_STATUS         (CPU_NIOS2_CONTEXT_GPR(32))
#define CPU_NIOS2_CONTEXT_PC             (CPU_NIOS2_CONTEXT_GPR(33))

#ifndef __MUTEK_ASM__

# include <hexo/cpu.h>

struct cpu_context_s
{
  union {
    reg_t save_mask;       //< what is being saved and restored
    reg_t gpr[32];
  };
  reg_t status;
  reg_t pc;
};

# define CPU_CONTEXT_REG_NAMES CPU_GPREG_NAMES, "status", "pc"
# define CPU_CONTEXT_REG_FIRST 1
# define CPU_CONTEXT_REG_COUNT 34

# endif  /* __MUTEK_ASM__ */

#endif

