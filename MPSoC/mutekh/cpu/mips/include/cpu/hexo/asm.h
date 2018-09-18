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

#ifndef __MIPS_SPECIFIC_H_
#define __MIPS_SPECIFIC_H_

#ifdef __MUTEK_ASM__

// rd and rt must be different for store
.macro GLOBAL_ACCESS op, name, rd, rt
	lui  \rt, %hi(\name)
        ori \rt, %lo(\name)
	\op  \rd, 0(\rt)
.endm

#ifdef CONFIG_ARCH_SMP
.macro CPU_LOCAL op, name, rd, rt
        \op \rd, %lo(\name) ($27)
.endm
#else
.macro CPU_LOCAL op, name, rd, rt
	GLOBAL_ACCESS \op, \name, \rd, \rt
.endm
#endif

.macro CONTEXT_LOCAL op, name, rd, rt
        CPU_LOCAL lw, __context_data_base, \rt, \rt
        \op \name, \rd (\rt)
.endm

.macro MTC0_ reg, creg
        mtc0   \reg,    $\creg
#if CONFIG_CPU_MIPS_VERSION > 32
	ehb
#else
	nop
	nop
#endif
.endm

.macro CPU_ID reg
#ifdef CONFIG_ARCH_SMP
# if CONFIG_CPU_MIPS_VERSION >= 32
        mfc0    \reg,    $15,    1
        andi    \reg,    \reg,    0x3ff
# else
        mfc0    \reg,    $15
        andi    \reg,    \reg,    0x3ff
# endif
#else
        or     \reg,  $0,      $0
#endif
.endm

// restore multiple fpu regs
.macro LxC1 i j
# if CONFIG_CPU_MIPS_FPU == 32
    .ifeq \i & 1
	ldc1	$\i,		\j
    .endif
# elif CONFIG_CPU_MIPS_FPU == 64
	ldc1	$\i,		\j
#endif
.endm

.macro SxC1 i j
# if CONFIG_CPU_MIPS_FPU == 32
    .ifeq \i & 1
	sdc1	$\i,		\j
    .endif
# elif CONFIG_CPU_MIPS_FPU == 64
	sdc1	$\i,		\j
#endif
.endm

#else /* not asm */

#define ASM_SECTION(name)              \
        ".section " name ",\"ax\",@progbits \n\t"

/** mtf0 instruction wait cycles (FIXME should depend on MIPS version) */
__asm__ (
	 ".macro MTC0_WAIT	\n"
	 "nop			\n"
	 "nop			\n"
	 ".endm			\n"
	 );

# endif  /* __MUTEK_ASM__ */

#endif

