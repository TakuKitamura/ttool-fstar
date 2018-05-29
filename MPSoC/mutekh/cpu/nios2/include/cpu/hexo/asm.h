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

#ifndef __HEXO_NIOS2_ASM_H_
#define __HEXO_NIOS2_ASM_H_

#ifdef __MUTEK_ASM__

.macro GLOBAL_ACCESS op, name, rd, rt
         movia  \rt,     \name
         \op    \rd,     (\rt)
.endm

#ifdef CONFIG_ARCH_SMP
.macro CPU_LOCAL op, name, rd, rt
        \op \rd, \name(CPU_NIOS2_CLS_REG)
.endm
#else
.macro CPU_LOCAL op, name, rd, rt
	GLOBAL_ACCESS \op, \name, \rd, \rt
.endm
#endif

#else /* not asm */

#define ASM_SECTION(name)              \
        ".section " name ",\"ax\",@progbits \n\t"

# endif  /* __MUTEK_ASM__ */

#endif

