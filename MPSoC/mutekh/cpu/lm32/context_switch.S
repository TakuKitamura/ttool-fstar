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

    Copyright Alexandre Becoulet <alexandre.becoulet@free.fr> (c) 2011
*/

#include <hexo/context.h>
#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

/*
*/

FUNC_START(.text, cpu_context_switch)

        // context registers save array
        ori     r2,     r0,      lo(lm32_context_regs)
        add     r2,     r25,     r2

        // save callee saved, fp, sp
        .irp r, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 27, 28
        sw      (r2 + CPU_LM32_CONTEXT_GPR(\r)), r\r
        .endr

        // resume directly at return address
        sw      (r2 + CPU_LM32_CONTEXT_PC), r29

        // save irq status
        rcsr    r3, IE
        sw      (r2 + CPU_LM32_CONTEXT_IE), r3

        // write save/restore mask
        seti    r3, CPU_LM32_CONTEXT_RESTORE_CALLEE
        sw      (r2 + CPU_LM32_CONTEXT_SAVE_MASK), r3

        bi       cpu_context_jumpto
        
FUNC_END(cpu_context_switch)



FUNC_START(.text, cpu_context_jumpto)

#ifdef CONFIG_ARCH_SMP
        // unlock some atomic value on context restore if requested
        lw      r2,     (r1 + HEXO_CONTEXT_S_UNLOCK * INT_REG_SIZE/8)
        be      r0,     r2,     1f

        // unlock and clear unlock address
        sw      (r1 + HEXO_CONTEXT_S_UNLOCK * INT_REG_SIZE/8), r0
        sw      (r2),   r0

1:      
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        seti    r2,     SOCLIB_MC_MAGIC_VAL
        sw      (r0 + SOCLIB_MC_MAGIC),      r2

        /* switch to new memchecker context */
        sw      (r0 + SOCLIB_MC_CTX_SET),    r1
#endif

        // restore tls pointer from context struct
        lw      r25,    (r1 + HEXO_CONTEXT_S_TLS * INT_REG_SIZE/8)
        
        // context registers save array
        ori     r2,     r0,      lo(lm32_context_regs)
        add     r2,     r25,     r2

        lw      sp,     (r2 + CPU_LM32_CONTEXT_GPR(28))
        // restore callee saved stack pointer

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        sw      (r0 + SOCLIB_MC_MAGIC),      r0
#endif

        lw      r4,     (r2 + CPU_LM32_CONTEXT_SAVE_MASK)
#if 0   // callee should almost always get restored (except on context start)
        andi    r3,     r4,     CPU_LM32_CONTEXT_RESTORE_CALLEE
        be      r3,     r0,     1f
#endif

        // restore callee saved
        .irp r, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 27
        lw      r\r,    (r2 + CPU_LM32_CONTEXT_GPR(\r))
        .endr
1:      

        // prepare eret
        lw      r30,    (r2 + CPU_LM32_CONTEXT_PC)
        lw      r1,     (r2 + CPU_LM32_CONTEXT_IE)
        add     r1,     r1,     r1 // shift  IE to EIE
        wcsr    IE,     r1
        
        // restore caller saved
        andi    r3,     r4,     CPU_LM32_CONTEXT_RESTORE_CALLER
        be      r3,     r0,     1f

        .irp r, 1, 3, 4, 5, 6, 7, 8, 9, 10, 29, 2
        lw      r\r,    (r2 + CPU_LM32_CONTEXT_GPR(\r))
        .endr

1:      
        eret

FUNC_END(cpu_context_jumpto)



FUNC_START(.text, cpu_context_set)

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        seti    r9,     SOCLIB_MC_MAGIC_VAL
        sw      (r0 + SOCLIB_MC_MAGIC),      r9

        seti    r9,     SOCLIB_MC_CTX_ID_CURRENT
        sw      (r0 + SOCLIB_MC_CTX_INVALIDATE),    r9

        // create a new temporary memchecker context using passed stack
        sw      (r0 + SOCLIB_MC_R1), r1
        sw      (r0 + SOCLIB_MC_R2), r2
        sw      (r0 + SOCLIB_MC_CTX_CREATE_TMP), r1

        // switch to new temporary memchecker context
        sw      (r0 + SOCLIB_MC_CTX_SET), r1
#endif

        // set stack pointer
        add     sp,     r1,     r2
#ifdef CONFIG_COMPILE_FRAMEPTR
        mv      fp,     sp
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        sw      (r0 + SOCLIB_MC_MAGIC),      r0
#endif

        b       r3

FUNC_END(cpu_context_set)



FUNC_START(.text, cpu_context_stack_use)

#ifdef CONFIG_SOCLIB_MEMCHECK
        seti    r9,     SOCLIB_MC_MAGIC_VAL
        sw      (r0 + SOCLIB_MC_MAGIC),      r9

        sw      (r0 + SOCLIB_MC_CTX_SET),    r1
#endif

        // get context local storage
        lw      r8,    (r1 + HEXO_CONTEXT_S_TLS * INT_REG_SIZE/8)
        
        // context registers save array
        ori     r9,     r0,     lo(lm32_context_regs)
        add     r9,     r8,     r9

        lw      sp,     (r9 + CPU_LM32_CONTEXT_GPR(28))
#ifdef CONFIG_COMPILE_FRAMEPTR
        mv      fp,     sp
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        sw      (r0 + SOCLIB_MC_MAGIC),      r0
#endif

#ifndef CONFIG_RELEASE
        // ensure we do not use context local storage from here
        mv      r25,    r0
#endif

        mv      r1,     r3
        b       r2

FUNC_END(cpu_context_stack_use)

