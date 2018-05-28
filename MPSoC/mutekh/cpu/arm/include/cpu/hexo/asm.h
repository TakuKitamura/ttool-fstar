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

    Copyright (c) 2010, Nicolas Pouillon <nipo@ssji.net>
*/

#ifndef __ARM_ASM_H_
#define __ARM_ASM_H_

#ifdef __MUTEK_ASM__

# define ASM_SECTION(name) \
        .section name,"ax"

# define CPU_ASM_FUNC_END .ltorg

.macro CPU_ID reg
# if defined(CONFIG_CPU_ARM_SOCLIB)
        mrc    p15, 0, \reg, c0, c0, 5
# elif defined(CONFIG_ARCH_SMP)
        .emsg No CPUID
# else
        mov    \reg, #0
# endif
.endm

.macro CPU_YIELD zero
# if defined(CONFIG_CPU_ARM_SOCLIB) && defined(CONFIG_CPU_WAIT_IRQ)
        mcr p15, 0, \zero, c7, c0, 4
# endif
.endm

/* get a variable using a cp15 register as base */
.macro GET_CP15_REL name, rd, rt, op2
     mrc   p15,0, \rt, c13, c0, \op2
     ldr   \rd, =\name
     ldr   \rd, [\rd, \rt]
.endm

.macro SET_CP15_REL name, tmp, tmp2, val, op2
     mrc   p15,0, \tmp, c13, c0, \op2
     ldr   \tmp2, =\name
     str   \val, [\tmp, \tmp2]
.endm

/* get a global variable */
.macro GET_GLOBAL name, rd
     ldr   \rd, =\name
     ldr   \rd, [\rd]
.endm

.macro SET_GLOBAL name, rval, tmp
     ldr   \tmp, =\name
     str   \rval, [\tmp]
.endm

/* get a variable using another global as base */
.macro GET_GLOBAL_REL name, rd, rt, var
     ldr   \rt, =\var
     ldr   \rt, [\rt]
     ldr   \rd, =\name
     ldr   \rd, [\rt, \rd]
.endm

.macro GET_GLOBAL_REL_ADDR name, rd, rt, var
     ldr   \rt, =\var
     ldr   \rt, [\rt]
     ldr   \rd, =\name
     add   \rd, \rt, \rd
.endm

/* TLS stuff */

# ifdef CONFIG_CPU_ARM_TLS_IN_C15

.macro CONTEXT_LOCAL_ld name, rd, rt
     GET_CP15_REL \name, \rd, \rt, 4
.endm

.macro CONTEXT_LOCAL_addr name, rd, rt
     mrc   p15,0, \rt, c13, c0, 4
     ldr   \rd, =\name
     add   \rd, \rd, \rt
.endm

.macro TLS_BASE_SET reg, tmp
    mcr   p15, 0, \reg, c13, c0, 4
.endm

# else

.macro CONTEXT_LOCAL_addr name, rd, rt
     GET_GLOBAL_REL_ADDR \name, \rd, \rt, __context_data_base
.endm

.macro CONTEXT_LOCAL_ld name, rd, rt
     GET_GLOBAL_REL \name, \rd, \rt, __context_data_base
.endm

.macro TLS_BASE_SET reg, tmp
     SET_GLOBAL __context_data_base, \reg, \tmp
.endm

# endif

/* CLS stuff */

# ifdef CONFIG_ARCH_SMP

.macro CPU_LOCAL_ld name, rd, rt
     GET_CP15_REL \name, \rd, \rt, 3
.endm

.macro CPU_LOCAL_st name, tmp, tmp2, val
     SET_CP15_REL \name, \tmp, \tmp2, \val, 3
.endm

# else

.macro CPU_LOCAL_ld name, rd, rt
     GET_GLOBAL \name, \rd
.endm

.macro CPU_LOCAL_st name, tmp, tmp2, val
     SET_GLOBAL \name, \val, \tmp
.endm

# endif

#endif

#endif

