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

#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

FUNC_START(.text, cpu_boot)

        /* set wim and psr. window -1 is reserved for traps */
        wr      %g0, SPARC_PSR_SUSER_MODE | SPARC_PSR_PIL_MASK | SPARC_PSR_TRAP_ENABLED, %psr
        wr      %g0, 0, %wim

        /* get CPU id and adjust stack */
        CPU_ID  %g3
        set     __initial_stack,        %sp

#ifdef CONFIG_ARCH_SMP_CAPABLE
        /* only first CPU is allowed to boot */
        tst     %g3
        bne     cpu_boot_idle
#endif

#ifdef CONFIG_ARCH_SMP
        /* adjust initial stack from cpuid */
        sll     %g3,    CONFIG_HEXO_RESET_STACK_SIZE, %g1
        sub     %sp,    %g1,    %sp
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        set     SOCLIB_MC_MAGIC_VAL, %g1
        st      %g1,            [ SOCLIB_MC_MAGIC ]

        /* create a new initial memchecker context using cpuid as context id */
        set     1 << CONFIG_HEXO_RESET_STACK_SIZE, %g1
        st      %g1,            [ SOCLIB_MC_R2 ]
        sub     %sp,    %g1,    %g1
        st      %g1,            [ SOCLIB_MC_R1 ]
        st      %g3,            [ SOCLIB_MC_CTX_CREATE ]

        /* switch to new memchecker context */
        st      %g3,            [ SOCLIB_MC_CTX_SET ]

        /* enable all memchecker checks */
        mov     SOCLIB_MC_CHECK_ALL, %g1
        st      %g1,            [ SOCLIB_MC_ENABLE ]

        /* adjust frame pointer reg id for sparc flat abi */
        mov     31, %g1
        st      %g1,            [ SOCLIB_MC_SET_FP_REG ]
#endif

        /* leave room for register window save */
        add     %sp,            -INT_REG_SIZE/8*16,      %sp
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov     %sp,            %i7
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        st      %g0,            [ SOCLIB_MC_MAGIC ]
#endif

        /* set cpu interrupts entry point with simple window overflow handling */
        set     __exception_base_ptr, %g1
        mov     %g1,    %tbr

        /* jump to architecture code */
        set     boot_from_reset_vector, %g1
        jmp     %g1
        mov     %sp,            %o0
FUNC_END(cpu_boot)


FUNC_START(.text, cpu_boot_idle)
        WAIT
        b               cpu_boot_idle
        nop
FUNC_END(cpu_boot_idle)

