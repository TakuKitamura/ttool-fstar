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

#include <hexo/context.h>
#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

/*
        %g0      zero
        %g1,%y   temporary
        %g2-%g4  application/compiler use, preserved by system
                 (kernel compiled with -mno-app-regs)
        %g5-%g7  system reserved (%g7 tls, %g6 cls)

    Sparc system v ABI using reg windows
    ------------------------------------

        %o6      stack pointer
        %i6      frame pointer
        %i0,%o0  return value
        %i7,%o7  return address

        Stack in and local regs layout:
            l0 to l7 : sp + 0 words to sp + 7 words
            i0 to i7 : sp + 8 words to sp + 15 words

    Sparc system v ABI using flat regs
    ----------------------------------

        %i7       frame pointer
        %l7       PIC base
        %o0       return value
        %o0-5     function arguments
        %o6       stack pointer
        %o7       return address

        %l*,%i0-5  callee saved
        %o*,%y,%g*,%i6 caller saved

    gcc clobber regnums: %g* 0-7, %o* 8-15, %l* 16-23, %i* 24-31

    Have a look to interrupt.S for interrupt context switch and preemption.
*/


FUNC_START(.text, cpu_context_switch)

        // context registers save array
        add     %g7, %lo(sparc_context_regs), %g1

        // save callee saved
        std     %l0, [%g1 + CPU_SPARC_CONTEXT_L(0)]
        std     %l2, [%g1 + CPU_SPARC_CONTEXT_L(2)]
        std     %l4, [%g1 + CPU_SPARC_CONTEXT_L(4)]
        std     %l6, [%g1 + CPU_SPARC_CONTEXT_L(6)]
        std     %i0, [%g1 + CPU_SPARC_CONTEXT_I(0)]
        std     %i2, [%g1 + CPU_SPARC_CONTEXT_I(2)]
        std     %i4, [%g1 + CPU_SPARC_CONTEXT_I(4)]
        // save sp & fp
        st      %i7, [%g1 + CPU_SPARC_CONTEXT_I(7)]
        st      %o6, [%g1 + CPU_SPARC_CONTEXT_O(6)]

        // save status
        mov     %psr, %g2
        or      %g2, SPARC_PSR_PREV_SUSER_MODE, %g2
        st      %g2, [%g1 + CPU_SPARC_CONTEXT_PSR]

        // write save/restore mask
        mov     CPU_SPARC_CONTEXT_RESTORE_CALLEE, %g2
        st      %g2, [%g1 + CPU_SPARC_CONTEXT_SAVE_MASK]

        // resume directly at return address
        add     %o7, 8, %o7
        st      %o7, [%g1 + CPU_SPARC_CONTEXT_PC]
        add     %o7, 4, %o7
        st      %o7, [%g1 + CPU_SPARC_CONTEXT_NPC]

#ifdef CONFIG_HEXO_FPU
#endif

        b       cpu_context_jumpto
        nop
        
FUNC_END(cpu_context_switch)



FUNC_START(.text, cpu_context_jumpto)

#ifdef CONFIG_ARCH_SMP
        // unlock some atomic value on context restore if requested
        ld      [%o0 + HEXO_CONTEXT_S_UNLOCK * INT_REG_SIZE/8], %g1
        tst     %g1
        bz      1f
        nop

        // unlock and clear unlock address
        st      %g0, [%o0 + HEXO_CONTEXT_S_UNLOCK * INT_REG_SIZE/8]
        st      %g0, [%g1]
1:      
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        set     SOCLIB_MC_MAGIC_VAL, %g1
        st      %g1,            [ SOCLIB_MC_MAGIC ]

        /* switch to new memchecker context */
        st      %o0,            [ SOCLIB_MC_CTX_SET ]
#endif

        // restore tls pointer from context struct
        ld      [%o0 + HEXO_CONTEXT_S_TLS * INT_REG_SIZE/8], %g7

#ifdef CONFIG_HEXO_USERMODE
        // save tls for restoration when back from user mode
        CPU_LOCAL_st    __context_data_base, %g7, %g1
#endif

        // context registers save array
        add     %g7, %lo(sparc_context_regs), %g1

        // restore callee saved stack pointer
        ld      [%g1 + CPU_SPARC_CONTEXT_O(6)], %o6
        ld      [%g1 + CPU_SPARC_CONTEXT_I(7)], %i7

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        st      %g0,            [ SOCLIB_MC_MAGIC ]
#endif

        // restore status
        ld      [%g1 + CPU_SPARC_CONTEXT_PSR], %g5
        andn    %g5, SPARC_PSR_TRAP_ENABLED | SPARC_PSR_CWP_MASK, %g5
        mov     %g5, %psr

        ld      [%g1 + CPU_SPARC_CONTEXT_SAVE_MASK], %g5

#if 0   // callee should almost always get restored (except on context start)
        andcc   %g5, CPU_SPARC_CONTEXT_RESTORE_CALLEE, %g0
        beq     1f
        nop
#endif
        
        // restore callee saved
        ldd     [%g1 + CPU_SPARC_CONTEXT_L(0)], %l0
        ldd     [%g1 + CPU_SPARC_CONTEXT_L(2)], %l2
        ldd     [%g1 + CPU_SPARC_CONTEXT_L(4)], %l4
        ldd     [%g1 + CPU_SPARC_CONTEXT_L(6)], %l6

        ldd     [%g1 + CPU_SPARC_CONTEXT_I(0)], %i0
        ldd     [%g1 + CPU_SPARC_CONTEXT_I(2)], %i2
        ldd     [%g1 + CPU_SPARC_CONTEXT_I(4)], %i4
1:      
        andcc   %g5, CPU_SPARC_CONTEXT_RESTORE_CALLER, %g0
        bz      1f
        nop

        // restore caller saved
        ldd     [%g1 + CPU_SPARC_CONTEXT_O(0)], %o0
        ldd     [%g1 + CPU_SPARC_CONTEXT_O(2)], %o2
        ldd     [%g1 + CPU_SPARC_CONTEXT_O(4)], %o4
        ld      [%g1 + CPU_SPARC_CONTEXT_O(7)], %o7
        ld      [%g1 + CPU_SPARC_CONTEXT_I(6)], %i6

        ld      [%g1 + CPU_SPARC_CONTEXT_Y], %g5
        mov     %g5, %y

        save

        // pc and npc
        ldd     [%g1 + CPU_SPARC_CONTEXT_PC],   %l0

#if defined(CONFIG_CPU_SPARC_APP_REGS)
        ldd     [%g1 + CPU_SPARC_CONTEXT_G(2)], %g2
        ld      [%g1 + CPU_SPARC_CONTEXT_G(4)], %g4
#endif
        ld      [%g1 + CPU_SPARC_CONTEXT_G(1)], %g1

        jmp     %l0
        rett    %l1

1:      
        save

        // pc and npc
        ldd     [%g1 + CPU_SPARC_CONTEXT_PC],   %l0

        jmp     %l0
        rett    %l1

FUNC_END(cpu_context_jumpto)



FUNC_START(.text, cpu_context_set)

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        set     SOCLIB_MC_MAGIC_VAL, %g1
        st      %g1,            [ SOCLIB_MC_MAGIC ]

        mov     SOCLIB_MC_CTX_ID_CURRENT, %g1
        st      %g1,            [ SOCLIB_MC_CTX_INVALIDATE ]

        // create a new temporary memchecker context using passed stack
        st      %o0,            [ SOCLIB_MC_R1 ]
        st      %o1,            [ SOCLIB_MC_R2 ]
        st      %o0,            [ SOCLIB_MC_CTX_CREATE_TMP ]

        // switch to new temporary memchecker context
        st      %o0,            [ SOCLIB_MC_CTX_SET ]
#endif

        // set stack pointer
        add     %o0,    %o1,    %o6
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov     %o6,            %i7
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        st      %g0,            [ SOCLIB_MC_MAGIC ]
#endif

        jmp     %o2
        nop

FUNC_END(cpu_context_set)



FUNC_START(.text, cpu_context_stack_use)

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        set     SOCLIB_MC_MAGIC_VAL, %g1
        st      %g1,            [ SOCLIB_MC_MAGIC ]

        st      %o0,            [ SOCLIB_MC_CTX_SET ]
#endif

        // get context local storage
        ld      [%o0 + HEXO_CONTEXT_S_TLS * INT_REG_SIZE/8], %g1
        // context registers save array
        add     %g1, %lo(sparc_context_regs), %g1

        ld      [%g1 + CPU_SPARC_CONTEXT_O(6)], %o6
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov     %o6,            %i7
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        st      %g0,            [ SOCLIB_MC_MAGIC ]
#endif

#ifndef CONFIG_RELEASE
        // ensure we do not use context local storage from here
        mov     %g0,    %g7
#endif

        jmp     %o1
        mov     %o2,    %o0

FUNC_END(cpu_context_stack_use)

#ifdef CONFIG_HEXO_USERMODE
FUNC_START(.text, cpu_context_set_user)

        save
        
        /* set user mode in status */
        mov     %psr,   %l0
        andn    %l0,    SPARC_PSR_PREV_SUSER_MODE | SPARC_PSR_TRAP_ENABLED, %l0
        mov     %l0,    %psr

        /* set stack */
        mov     %i0,    %i6
        /* set arg */
        mov     %i2,    %i0
        nop

        jmp     %i1
        rett    %i1 + 4

FUNC_END(cpu_context_set_user)
#endif
