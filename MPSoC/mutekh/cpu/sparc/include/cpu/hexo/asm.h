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

#ifndef CPU_ASM_H_
#define CPU_ASM_H_

#ifdef __MUTEK_ASM__

.macro CPU_ID reg
#  ifdef CONFIG_ARCH_SMP
#   error missing CPUID macro for your architecture
#  else
        mov     %g0,                    \reg
#  endif
.endm

.macro CPU_LOCAL_ld name, rd
# ifdef CONFIG_ARCH_SMP
         ld     [%g6 + %lo(\name)], \rd
# else
         set    \name,   \rd
         ld     [\rd], \rd
# endif
.endm

.macro CPU_LOCAL_st name, rd, rt
# ifdef CONFIG_ARCH_SMP
         st     \rd, [%g6 + %lo(\name)]
# else
         set    \name,   \rt
         st     \rd,   [\rt]
# endif
.endm


.macro WAIT
# ifdef CONFIG_CPU_SPARC_LEON3
        wr %g0, %asr19
# endif 
.endm

# else /* not asm */

asm(
".macro WAIT \n"
# ifdef CONFIG_CPU_SPARC_LEON3
"        wr %g0, %asr19 \n"
# else

#  ifdef CONFIG_CPU_WAIT_IRQ
#   error No wait opcode defined for selected sparc processor
#  endif
# endif 
".endm \n"
);

#  define ASM_SECTION(name)              \
        ".section " name ",\"ax\",@progbits \n\t"
# endif

#endif

