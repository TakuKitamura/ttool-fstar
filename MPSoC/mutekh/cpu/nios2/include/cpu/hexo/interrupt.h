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


/**
   @file

   CPU specific interrupt handling
*/

#if !defined(INTERRUPT_H_) || defined(CPU_INTERRUPT_H_)
#error This file can not be included directly
#else

#define CPU_INTERRUPT_H_

#define CPU_EXCEPTION_RESET                     0
#define CPU_EXCEPTION_PRESET                    1
#define CPU_EXCEPTION_INTERRUPT                 2
#define CPU_EXCEPTION_TRAP                      3
#define CPU_EXCEPTION_UNIMP_INS                 4
#define CPU_EXCEPTION_ILLEGAL_INS               5
#define CPU_EXCEPTION_MISALIGNED_DATA           6
#define CPU_EXCEPTION_MISALIGNED_DEST           7
#define CPU_EXCEPTION_DIVISION_ERROR            8
#define CPU_EXCEPTION_SUPERVISOR_INS_ADDR       9
#define CPU_EXCEPTION_SUPERVISOR_INS            10
#define CPU_EXCEPTION_SUPERVISOR_DATA_ADDR      11
#define CPU_EXCEPTION_FAST_TLB_MISS             12
#define CPU_EXCEPTION_TLB_EXEC_PERM             13
#define CPU_EXCEPTION_TLB_READ_PERM             14
#define CPU_EXCEPTION_TLB_WRITE_PERM            15
#define CPU_EXCEPTION_MPU_REGION_INS            16
#define CPU_EXCEPTION_MPU_REGION_DATA           17

# define CPU_FAULT_COUNT 18

# define CPU_FAULT_NAMES	 {              \
      /* 0  */ "Reset",                         \
      /* 1  */ "PReset",                        \
      /* 2  */ "Interrupt",                     \
      /* 3  */ "Trap",                          \
      /* 4  */ "Unimplemented instruction"      \
      /* 5  */ "Illegal instruction"            \
      /* 6  */ "Misaligned data access"         \
      /* 7  */ "Misaligned destination",        \
      /* 8  */ "Division error",                \
      /* 9  */ "Supervisor ins address",        \
      /* 10 */ "Supervisor instruction",        \
      /* 11 */ "Supervisor data address",       \
      /* 12 */ "Fast TLB Miss",                 \
      /* 13 */ "TLB exec perm error",           \
      /* 14 */ "TLB read perm error",           \
      /* 15 */ "TLB write perm error",          \
      /* 16 */ "MPU region ins error",          \
      /* 17 */ "MPU region data error",         \
      }

#ifndef __MUTEK_ASM__

# include <hexo/local.h>

# ifdef CONFIG_DRIVER_ICU_NIOS2
struct device_s;
extern CPU_LOCAL struct device_s cpu_icu_dev;
# endif

static inline void
cpu_interrupt_disable(void)
{
# ifdef CONFIG_HEXO_IRQ
  __asm__ volatile (
		    ".set noat 					\n"
		    "	rdctl	r1, status		\n"
		    "	ori	    r1, r1, 0x1     \n"
		    "	addi	r1, r1, -1      \n"
		    "	wrctl	status, r1		\n"
		    ".set at 					\n"
                    :
                    :
                    : "memory"     /* compiler memory barrier */
		    );
# endif
}

static inline void
cpu_interrupt_enable(void)
{
# ifdef CONFIG_HEXO_IRQ
  __asm__ volatile (
		    ".set noat 					\n"
		    "	rdctl	r1, status		\n"
		    "	ori	    r1, r1, 0x1     \n"
		    "	wrctl	status, r1		\n"
		    ".set at 					\n"

                    :
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
//  reg_t state;
//  cpu_interrupt_savestate(&state);
//  cpu_interrupt_enable();
  __asm__ volatile (
		    "nop"
		    :
		    :
		    /* memory clobber is important here as cpu_interrupt_process()
		       will let pending interrupts change global variables checked in
		       a function loop (scheduler root queue for instance) */
		    : "memory"
		    );
  cpu_interrupt_disable();
//  cpu_interrupt_restorestate(&state);
# endif
}

static inline void
cpu_interrupt_savestate(reg_t *state)
{
# ifdef CONFIG_HEXO_IRQ
  __asm__ volatile (
		    "	rdctl	%0, status\n"
		    : "=r" (*state)
                    :
                    : "memory"     /* compiler memory barrier */
		    );
# endif
}

static inline void
cpu_interrupt_savestate_disable(reg_t *state)
{
# ifdef CONFIG_HEXO_IRQ
  __asm__ volatile (
		    ".set noat 					\n"
		    "	rdctl	%0, status      \n"
		    "	ori	    r1, %0, 0x1     \n"
		    "	addi	r1, r1, -1      \n"
		    "	wrctl	status, r1		\n"
		    ".set at 					\n"
		  : "=r" (*state)
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
		    "	wrctl	status, %0		\n"
		    :
		    : "r" (*state)
                    : "memory"     /* compiler memory barrier */
		    );
# endif
}

static inline bool_t
cpu_interrupt_getstate(void)
{
# ifdef CONFIG_HEXO_IRQ
  reg_t state;

  __asm__ (
		    "	rdctl	%0, status		\n"
		    : "=r" (state)
		    );

  return state & 0x1;
# else
  return 0;
# endif
}

static inline bool_t
cpu_is_interruptible(void)
{
  return cpu_interrupt_getstate();
}

# ifdef CONFIG_CPU_WAIT_IRQ
static inline void
cpu_interrupt_wait(void)
{
#  ifdef CONFIG_HEXO_IRQ
#   error Wait instruction not available, CONFIG_CPU_WAIT_IRQ shall be undefined
#  endif
}
# endif

#endif
#endif

