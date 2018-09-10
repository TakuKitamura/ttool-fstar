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

    Copyright Alexandre Becoulet <alexandre.becoulet@free.fr> (c) 2011

*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

FUNC_START(.text, cpu_boot)

        xor     r0,     r0,     r0
        /* get CPU id and adjust stack */

        mv      r3,     r0 /* FIXME read cpu id */
        seta    sp,     __initial_stack

#ifdef CONFIG_ARCH_SMP_CAPABLE
        /* only first CPU is allowed to boot */
        bne     r3,     r0,     cpu_boot_idle
#endif

#ifdef CONFIG_ARCH_SMP
        /* adjust initial stack from cpuid */
        // FIXME use calli   __ashlsi3
        sli     r1,     r3,     CONFIG_HEXO_RESET_STACK_SIZE,
        sub     sp,     sp,     r1
#endif

#ifdef CONFIG_COMPILE_FRAMEPTR
        mv      fp,     sp
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK

        /* enter memchecker command mode */
        seti    r1,     SOCLIB_MC_MAGIC_VAL
        sw      (r0 + SOCLIB_MC_MAGIC),      r1

        /* create a new initial memchecker context using cpuid as context id */
        seti    r1,     1 << CONFIG_HEXO_RESET_STACK_SIZE
        sw      (r0 + SOCLIB_MC_R2),         r1
        sub     r1,     sp,     r1
        sw      (r0 + SOCLIB_MC_R1),         r1
        sw      (r0 + SOCLIB_MC_CTX_CREATE), r3

        /* switch to new memchecker context */
        sw      (r0 + SOCLIB_MC_CTX_SET),    r3

        /* enable all memchecker checks except FP (gcc generated
	code sometimes gives invalid FP for a few cycles) */
        seti    r1,     SOCLIB_MC_CHECK_ALL & ~SOCLIB_MC_CHECK_FP
        sw      (r0 + SOCLIB_MC_ENABLE),     r1

        /* leave memchecker command mode */
        sw      (r0 + SOCLIB_MC_MAGIC),      r0
#endif

        /* set cpu interrupts entry point */
        seta    r1,     __exception_base_ptr
        wcsr    EBA,    r1

        /* jump to architecture code */
        bi      boot_from_reset_vector
FUNC_END(cpu_boot)


FUNC_START(.text, cpu_boot_idle)
        bi      cpu_boot_idle
FUNC_END(cpu_boot_idle)

