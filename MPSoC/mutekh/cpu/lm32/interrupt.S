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
#include <hexo/interrupt.h>
#include <hexo/cpu.h>

.section .excep,"ax",@progbits

.macro LM32_GET_REGARRAY  // get context registers save array
#if 0
        addi    r31,    r25,     lo(lm32_context_regs)
          // lm32 As doesnt support lo() with addi
#else
        .int    0x373f0000 + lm32_context_regs
#endif
.endm

.macro LM32_EXCEPT_ENTRY id

        LM32_GET_REGARRAY

        // save r1 & r2
        sw      (r31 + CPU_LM32_CONTEXT_GPR(1)), r1
        sw      (r31 + CPU_LM32_CONTEXT_GPR(2)), r2
        // store excep id
        mvi     r1,     \id

        // goto asm handler
        seta    r2,    lm32_excep_entry
        b       r2

.endm

__exception_base_ptr:

      /* reset */
        xor     r0,     r0,     r0
        seta    r1, cpu_boot
        b       r1
        nop
        nop
        nop
        nop

        /* breakpoint */
        .org __exception_base_ptr + 1*32
        mv      r30,    r31
        LM32_EXCEPT_ENTRY CPU_FAULT_BREAKPOINT

        /* iberr*/
        .org __exception_base_ptr + 2*32
        LM32_EXCEPT_ENTRY CPU_FAULT_INS_BERR

        /* watchpoint */
        .org __exception_base_ptr + 3*32
        mv      r30,    r31
        LM32_EXCEPT_ENTRY CPU_FAULT_WATCHPOINT

        /* dberr */
        .org __exception_base_ptr + 4*32
        LM32_EXCEPT_ENTRY CPU_FAULT_DATA_BERR

        /* div by 0 */
        .org __exception_base_ptr + 5*32
        LM32_EXCEPT_ENTRY CPU_FAULT_DIV_BY_0

        /* irq */
        .org __exception_base_ptr + 6*32

#ifdef CONFIG_HEXO_IRQ
        seta    r31, lm32_irq_entry
        b       r31
#else
        LM32_EXCEPT_ENTRY CPU_FAULT_LOST_IRQ
#endif

        /* syscall */
        .org __exception_base_ptr + 7*32

        LM32_EXCEPT_ENTRY CPU_FAULT_SYSCALL

        .org __exception_base_ptr + 8*32

FUNC_START(.text, lm32_excep_entry)

        // save caller saved & r11, ra (r1 & r2 already saved)
        .irp r, 3, 4, 5, 6, 7, 8, 9, 10, 11, 29
        sw      (r31 + CPU_LM32_CONTEXT_GPR(\r)), r\r
        .endr

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // clear context preempt handler
        seta    r2, cpu_preempt_handler
        sw      (r2+0), r0
#endif

        // save PC
        sw      (r31 + CPU_LM32_CONTEXT_PC), r30

        // call fault handler
        mv      r2,     r30
        mv      r3,     r0
        mv      r4,     r31
        mv      r5,     sp

        seta    r10,    cpu_exception_handler
        lw      r10,    (r10 + 0)
        call    r10

        // readback PC
        lw      r30, (r31 + CPU_LM32_CONTEXT_PC)

        bi      lm32_restore_preempt

FUNC_END(lm32_excep_entry)

#ifdef CONFIG_HEXO_IRQ
FUNC_START(.text, lm32_irq_entry)

        LM32_GET_REGARRAY

        // save caller saved regs & r11, ra
        .irp r, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 29
        sw      (r31 + CPU_LM32_CONTEXT_GPR(\r)), r\r
        .endr

# ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // clear context preempt handler
        seta    r1, cpu_preempt_handler
        sw      (r1+0), r0
# endif

        // find lowest irq number
        rcsr    r1,     IP
        mv      r11,    r1
        calli   __ctzsi2

        // call irq handler
        seta    r2,     cpu_interrupt_handler
        lw      r2,     (r2 + 0)
        call    r2

        // acknowledge lowest pending irq
        addi    r2,     r11,    -1
        and     r2,     r11,    r2
        xor     r2,     r11,    r2
        wcsr    IP,     r2

FUNC_END(lm32_irq_entry)
#endif

FUNC_START(.text, lm32_restore_preempt)

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT

        // check if a preempt handler has been registered
        seta    r1, cpu_preempt_handler
        lw      r1, (r1+0)
        be      r1,     r0,     1f

        // call preempt handler
        call    r1
        
        // got pointer to context to switch to ?
        be      r1,     r0,     1f

        // save callee saved registers
        .irp r, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 27, 28
        sw      (r31 + CPU_LM32_CONTEXT_GPR(\r)), r\r
        .endr

        // save PC
        sw      (r31 + CPU_LM32_CONTEXT_PC), r30

        // save irq status
        rcsr    r2,     IE
        andi    r2,     r2,     2
        cmpne   r2,     r2,     r0
        sw      (r31 + CPU_LM32_CONTEXT_IE), r2

        // write save/restore mask
        seti    r2, CPU_LM32_CONTEXT_RESTORE_CALLEE | CPU_LM32_CONTEXT_RESTORE_CALLER
        sw      (r31 + CPU_LM32_CONTEXT_SAVE_MASK), r2

# ifdef CONFIG_HEXO_CONTEXT_STATS
        // update context stats
        mv      r11,    r1
        calli   context_preempt_stats
        mv      r1,    r11
# endif

        // switch to new context
        bi      cpu_context_jumpto
1:      
#endif

        // restore regs
        .irp r, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 29
        lw      r\r, (r31 + CPU_LM32_CONTEXT_GPR(\r))
        .endr

        // return
        eret

FUNC_END(lm32_restore_preempt)

// Local Variables:
// indent-tabs-mode: nil;
// vim: filetype=cpp:expandtab

