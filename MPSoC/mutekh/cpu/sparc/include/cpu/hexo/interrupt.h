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

#if !defined(INTERRUPT_H_) || defined(CPU_INTERRUPT_H_)
#error This file can not be included directly
#else

#include <hexo/asm.h>

#define CPU_INTERRUPT_H_

#define CPU_EXCEPTION_UNDEFINED       0
#define CPU_EXCEPTION_INS_ACCESS_EX   1
#define CPU_EXCEPTION_INS_ILL         2
#define CPU_EXCEPTION_INS_PRIVILEGED  3
#define CPU_EXCEPTION_FP_DISABLED     4
#define CPU_EXCEPTION_WIN_OVER        5
#define CPU_EXCEPTION_WIN_UNDER       6
#define CPU_EXCEPTION_BAD_ALIGN       7
#define CPU_EXCEPTION_FP_EX           8
#define CPU_EXCEPTION_DATA_ACCESS_EX  9
#define CPU_EXCEPTION_TAG_OVER        10
#define CPU_EXCEPTION_WATCHPOINT      11
#define CPU_EXCEPTION_R_REG_ACCESS    12
#define CPU_EXCEPTION_INS_ACCESS_ERR  13
#define CPU_EXCEPTION_CP_DISABLED     14
#define CPU_EXCEPTION_UNIMP_FLUSH     15
#define CPU_EXCEPTION_CP_EX           16
#define CPU_EXCEPTION_DATA_ACCESS_ERR 17
#define CPU_EXCEPTION_DIV_BY_0        18
#define CPU_EXCEPTION_DATA_STORE_ERR  19
#define CPU_EXCEPTION_DATA_MMU_MISS   20
#define CPU_EXCEPTION_INS_MMU_MISS    21

#define CPU_FAULT_COUNT 22

#ifndef __MUTEK_ASM__

# define CPU_FAULT_NAMES {       \
/* 0  */ "Undefined exception",          \
/* 1  */ "Instruction access exception", \
/* 2  */ "Illegal instruction",          \
/* 3  */ "Privileged instruction",       \
/* 4  */ "FPU disabled",                 \
/* 5  */ "Window overflow",              \
/* 6  */ "Window underflow",             \
/* 7  */ "Address not aligned",          \
/* 8  */ "FPU exception",                \
/* 9  */ "Data access exception",        \
/* 10 */ "Tag overflow",                 \
/* 11 */ "Watchpoint detected",          \
/* 12 */ "R register access error",      \
/* 13 */ "Instruction access error",     \
/* 14 */ "Cp disabled",                  \
/* 15 */ "Unimplemented flush",          \
/* 16 */ "Cp exception",                 \
/* 17 */ "Data access error",            \
/* 18 */ "Divide by zero",               \
/* 19 */ "Data store error",             \
/* 20 */ "Data mmu miss",                \
/* 21 */ "Instruction mmu miss",         \
}

# include "hexo/local.h"

# ifdef CONFIG_DRIVER_ICU_SPARC
struct device_s;
extern CPU_LOCAL struct device_s cpu_icu_dev;
# endif

static inline void
cpu_interrupt_disable(void)
{
# ifdef CONFIG_HEXO_IRQ
  reg_t tmp;

  asm volatile (
		"rd %%psr, %0		\n\t"
                "or %0, 0xf00, %0        \n\t"
		"wr %0, %%psr	\n\t"
                "nop \n nop \n nop \n"
		: "=r" (tmp)
                :: "memory"     /* compiler memory barrier */
	  );
# endif
}

static inline void
cpu_interrupt_enable(void)
{
# ifdef CONFIG_HEXO_IRQ
  reg_t tmp;

  asm volatile (
		"rd %%psr, %0		\n\t"
                "andn %0, 0xf00, %0      \n\t"
		"wr %0, %%psr	\n\t"
                "nop \n nop \n nop \n"
		: "=r" (tmp)
                :
                : "memory"     /* compiler memory barrier */
	  );
# endif
}

static inline void
cpu_interrupt_process(void)
{
# ifdef CONFIG_HEXO_IRQ
  cpu_interrupt_enable();
  __asm__ volatile ("nop"
		    :
		    :
    /* memory clobber is important here as cpu_interrupt_process()
       will let pending intterupts change global variables checked in
       a function loop (scheduler root queue for instance) */
		    : "memory"
		    );
  cpu_interrupt_disable();
# endif
}

static inline void
cpu_interrupt_savestate(reg_t *state)
{
# ifdef CONFIG_HEXO_IRQ
  __asm__ volatile (
                    "rd %%psr, %0		\n\t"
		    : "=r" (*state)
		    );
# endif
}

static inline void
cpu_interrupt_savestate_disable(reg_t *state)
{
# ifdef CONFIG_HEXO_IRQ
  reg_t tmp;

  asm volatile (	
		"rd %%psr, %1		\n\t"
                "or %1, 0xf00, %0      \n\t"
		"wr %0, %%psr           \n\t"
                "nop \n nop \n nop \n"
		: "=r" (tmp),
                  "=r" (*state)
                :
                : "memory"     /* compiler memory barrier */
	  );
# endif
}

static inline void
cpu_interrupt_restorestate(const reg_t *state)
{
# ifdef CONFIG_HEXO_IRQ
  __asm__ volatile (
                    "wr %0, %%psr           \n\t"
                    "nop \n nop \n nop \n"
		    :
		    : "r" (*state)
                    : "memory", "cc"     /* compiler memory barrier */
		    );
# endif
}

static inline bool_t
cpu_interrupt_getstate(void)
{
# ifdef CONFIG_HEXO_IRQ
  reg_t		state;

  __asm__ volatile (
                    "rd %%psr, %0		\n\t"
		    : "=r" (state)
		    );

  return (state & 0xf00) < 0xf00;
# else
  return 0;
# endif
}

static inline bool_t
cpu_is_interruptible(void)
{
# ifdef CONFIG_HEXO_IRQ
  reg_t		state;

  __asm__ volatile (
                    "rd %%psr, %0		\n\t"
		    : "=r" (state)
		    );

  return (state & 0xf00) < 0xf00 && (state & 0x20);
# else
	return 0;
# endif
}

# ifdef CONFIG_CPU_WAIT_IRQ
static inline void cpu_interrupt_wait(void)
{
  __asm__ volatile (
                    "WAIT\n"	/* defined in asm.h */
		    ::: "memory"
                    );
}
# endif

#endif  /* __MUTEK_ASM__ */

#endif

