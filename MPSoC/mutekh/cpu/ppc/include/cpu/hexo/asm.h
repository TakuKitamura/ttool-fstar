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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2010

*/

#ifndef CPU_ASM_H_
#define CPU_ASM_H_

#ifdef __MUTEK_ASM__

#define PPC_SPRG(n) (0x110 + n)

.macro LI32 reg value
	lis	\reg,		\value@h
	ori	\reg,	\reg,	\value@l  
.endm

.macro LA32 reg symbol
	lis	\reg,		\symbol@ha
	la	\reg,		\symbol@l(\reg)
.endm

/* access a variable using a SPR as base */
.macro SPRREL_ACCESS op, name, rd, rt, spr
        mfspr  \rt,      \spr
        \op    \rd,      \name (\rt)
    .if \rt == 0
        .fail 0	// rt can not be zero here
    .endif
.endm

/* access a global variable */
.macro GLOBAL_ACCESS op, name, rd, rt
        lis   \rt, \name@h
        ori   \rt, \rt, \name@l
        \op   \rd, 0(\rt)
    .if \rt == 0
        .fail 0	// rt can not be zero here
    .endif
.endm

#ifdef CONFIG_ARCH_SMP
.macro CPU_LOCAL op, name, rd, rt
        SPRREL_ACCESS \op, \name, \rd, \rt, 0x115
.endm
#else
.macro CPU_LOCAL op, name, rd, rt
        GLOBAL_ACCESS \op, \name, \rd, \rt
.endm
#endif

.macro CONTEXT_LOCAL op, name, rd, rt
        SPRREL_ACCESS \op, \name, \rd, \rt, 0x114
.endm

// restore multiple gp regs
.macro LMW_GP array i j
	lwz	\i,		CPU_PPC_CONTEXT_GPR(\i)(\array)
    .if \i-\j
	LMW_GP \array, "(\i+1)", \j
    .endif
.endm

// save multiple gp regs
.macro SMW_GP array i j
	stw	\i,		CPU_PPC_CONTEXT_GPR(\i)(\array)
    .if \i-\j
	SMW_GP \array, "(\i+1)", \j
    .endif
.endm

// restore multiple fpu regs
.macro LMD_FPU array i j
	lfd	\i,		CPU_PPC_CONTEXT_FR(\i)(\array)
    .if \i-\j
	LMD_FPU \array, "(\i+1)", \j
    .endif
.endm

// save multiple fpu regs
.macro SMD_FPU array i j
	stfd	\i,		CPU_PPC_CONTEXT_FR(\i)(\array)
    .if \i-\j
	SMD_FPU \array, "(\i+1)", \j
    .endif
.endm

#else /* not asm */

#define ASM_SECTION(name)              \
        ".section " name ",\"ax\",@progbits \n\t"
#endif

#endif

