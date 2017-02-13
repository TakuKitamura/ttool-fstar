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

#if !defined(__CPU_H_) || defined(CPU_CPU_H_)
#error This file can not be included directly
#else
#define CPU_CPU_H_

#if defined(CONFIG_CPU_MIPS_VERSION) && CONFIG_CPU_MIPS_VERSION != CONFIG_CPU_MIPS_VERSION
# warning compiler mips version doesnt match configuration
#endif

#define CPU_MIPS_GP             28
#define CPU_MIPS_SP             29
#define CPU_MIPS_FP             30
#define CPU_MIPS_RA             31

#define CPU_MIPS_STATUS         12
#define CPU_MIPS_CAUSE          13
#define CPU_MIPS_EPC            14
#define CPU_MIPS_BADADDR        8
#define CPU_MIPS_EEPC           30

# define CPU_MIPS_STATUS_FPU    0x20000000
/** interrupts enabled */
# define CPU_MIPS_STATUS_EI     0x00000001

# define CPU_MIPS_STATUS_IM     0x0000fc00
# define CPU_MIPS_STATUS_IM_SHIFT 10

#ifdef CONFIG_CPU_MIPS_USE_ERET
/** exception mode */
# define CPU_MIPS_STATUS_EXL    0x00000002
/** user mode */
# define CPU_MIPS_STATUS_UM     0x00000010

#else
/** interruptes enabled */
# define CPU_MIPS_STATUS_EIc    CPU_MIPS_STATUS_EI
/** kernel mode when set */
# define CPU_MIPS_STATUS_KUc    0x00000002
/** previous interruptes enabled */
# define CPU_MIPS_STATUS_EIp    0x00000004
/** previous kernel mode when set */
# define CPU_MIPS_STATUS_KUp    0x00000008

#endif

# define CPU_MIPS_CAUSE_BD      0x80000000

#ifndef __MUTEK_ASM__

#include <hexo/endian.h>

# ifdef CONFIG_ARCH_SMP
extern void * cpu_local_storage[CONFIG_CPU_MAXCOUNT];
# endif

/** general purpose regsiters count */
# define CPU_GPREG_COUNT	32

# define CPU_GPREG_NAMES 								   \
"zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",					   \
"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",						   \
"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",						   \
"t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra"						   \


# if CONFIG_CPU_MIPS_VERSION >= 32 

# define cpu_mips_mfc0(id, sel)			\
({						\
  reg_t _reg;					\
						\
  asm volatile ("mfc0	%0,	$%1, %2	\n"	\
		: "=r" (_reg)			\
		: "i" (id)			\
		, "i" (sel)			\
		);				\
						\
  _reg;						\
})

# define cpu_mips_mtc0(id, sel, val)		\
({						\
  reg_t _reg = val;				\
						\
  asm volatile ("mtc0	%0,	$%1, %2	\n"	\
				:: "r" (_reg)	\
				, "i" (id)	\
		, "i" (sel)			\
		);				\
})

# define cpu_mips_mfc2(id, sel)			\
({						\
  reg_t _reg;					\
						\
  asm volatile ("mfc2	%0,	$%1, %2	\n"	\
		: "=r" (_reg)			\
		: "i" (id)			\
		, "i" (sel)			\
		);				\
						\
  _reg;						\
})

# define cpu_mips_mtc2(id, sel, val)		\
({						\
  reg_t _reg = val;				\
						\
  asm volatile ("mtc2	%0,	$%1, %2	\n"	\
				:: "r" (_reg)	\
				, "i" (id)	\
		, "i" (sel)			\
		);				\
})


# else

# define cpu_mips_mfc0(id, sel)			\
({						\
  reg_t _reg;					\
						\
  asm volatile ("mfc0	%0,	$%1 \n"		\
		: "=r" (_reg)			\
		: "i" (id)			\
		);				\
						\
  _reg;						\
})

# define cpu_mips_mtc0(id, sel, val)										   \
({						\
  reg_t _reg = val;				\
						\
  asm volatile ("mtc0	%0,	$%1 \n"		\
				:: "r" (_reg)	\
				, "i" (id)	\
		);				\
})

# define cpu_mips_mfc2(id, sel)			\
({						\
  reg_t _reg;					\
						\
  asm volatile ("mfc2	%0,	$%1 \n"		\
		: "=r" (_reg)			\
		: "i" (id)			\
		);				\
						\
  _reg;						\
})

# define cpu_mips_mtc2(id, sel, val)		\
({						\
  reg_t _reg = val;				\
						\
  asm volatile ("mtc2	%0,	$%1 \n"		\
				:: "r" (_reg)	\
				, "i" (id)	\
		);				\
})


# endif

static inline
reg_t cpu_get_stackptr()
{
    reg_t ret;
    asm("move %0, $sp": "=r"(ret));
    return ret;
}

# define CPU_TYPE_NAME mips32

static inline cpu_id_t
cpu_id(void)
{
	return (reg_t)cpu_mips_mfc0(15, 1) & (reg_t)0x000003ff;
}

static inline bool_t
cpu_isbootstrap(void)
{
  return cpu_id() == 0;
}

static inline cpu_cycle_t
cpu_cycle_count(void)
{
  return cpu_mips_mfc0(9, 0);
}

static inline void
cpu_trap()
{
  asm volatile ("break 0");
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
  asm volatile (
# if CONFIG_CPU_MIPS_VERSION >= 32
		" cache %0, %1"
		: : "i" (0x11) , "R" (*(uint8_t*)(ptr))
# else
#  ifdef CONFIG_ARCH_SOCLIB
		" lw $0, (%0)"
		: : "r" (ptr)
#  else
		"nop"::
#  endif
# endif
		: "memory"
		);
}

static inline size_t cpu_dcache_line_size()
{
  reg_t r0 = cpu_mips_mfc0(16, 0);
  reg_t r1 = cpu_mips_mfc0(16, 1);

  if (BIT_EXTRACT(r0, 31))
    {
      r1 = BITS_EXTRACT_FL(r1, 10, 12);

      if (r1)
	return 2 << r1;
    }

  return 8;
}

# endif  /* __MUTEK_ASM__ */

# endif

