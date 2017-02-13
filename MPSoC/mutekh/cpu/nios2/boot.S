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

    Copyright Francois Charot <charot@irisa.fr>  (c) 2008
    INRIA Rennes Bretagne Atlantique

    Copyright Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr> (c) 2011

*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

FUNC_START(.boot, cpu_boot)

    .set noat

        /* get CPU id and adjust stack */
        rdctl   r16,            cpuid
        andi    r16,    r16,    0x000003ff
        movia   sp,             __initial_stack

        /* clear status */
        wrctl   status,         r0

#ifdef CONFIG_ARCH_SMP_CAPABLE
        /* only CPU id below CONFIG_CPU_MAXCOUNT are allowed to boot */
        movia   r2,     CONFIG_CPU_MAXCOUNT
        cmplt   r3,     r16,    r2
        beq     zero,   r3,     cpu_boot_idle
#endif

#ifdef CONFIG_ARCH_SMP
        /* adjust initial stack from cpuid */
        slli    r17,    r16,    CONFIG_HEXO_RESET_STACK_SIZE
        sub     sp,     sp,     r17
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        movi    r17,            1 << CONFIG_HEXO_RESET_STACK_SIZE

    /* enter memchecker command mode */
        movia   r1,             SOCLIB_MC_MAGIC_VAL
        stw     r1,             SOCLIB_MC_MAGIC(zero)

    /* create a new initial memchecker context using cpuid as context id */
        stw     r17,            SOCLIB_MC_R2(zero)
        sub     r17,    sp,     r17
        stw     r17,            SOCLIB_MC_R1(zero)
        stw     r16,            SOCLIB_MC_CTX_CREATE(zero)

    /* switch to new memchecker context */
        stw     r16,            SOCLIB_MC_CTX_SET(zero)

    /* enable all memchecker checks */
        ori     r1,     zero,   SOCLIB_MC_CHECK_ALL
        stw     r1,             SOCLIB_MC_ENABLE(zero)
#endif

        addi    sp,     sp,     -CONFIG_HEXO_STACK_ALIGN
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov     fp,     sp
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        stw     r0,             SOCLIB_MC_MAGIC(zero)
#endif

       movia   r4,              __exception_base_ptr
       wrctl   ctl17,           r4

       movia   r16,             boot_from_reset_vector
       jmp     r16
FUNC_END(cpu_boot)

FUNC_START(.boot, cpu_boot_idle)
#ifdef CONFIG_CPU_PPC_WAIT_OPCODE
# error No hlt/wait opcode available on nios2
#else
        br      cpu_boot_idle
#endif        
FUNC_END(cpu_boot_idle)

