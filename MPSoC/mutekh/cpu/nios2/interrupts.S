/*
 *   This file is part of MutekH.
 *   
 *   MutekH is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; version 2.1 of the
 *   License.
 *   
 *   MutekH is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *   
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with MutekH; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *   02110-1301 USA.
 *
 *   Copyright Francois Charot <charot@irisa.fr>  (c) 2008
 *   INRIA Rennes Bretagne Atlantique
 *
 *   Copyright Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr> (c) 2011
 */

#include <hexo/context.h>
#include <hexo/cpu.h>
#include <hexo/asm.h>

.section        .excep,"ax",@progbits         

        nop
nios2_interrupt_entry:
        .set noat

#if defined(CONFIG_HEXO_USERMODE) && defined(CONFIG_ARCH_SMP)
        /* restore cpu local storage */
    	rdctl   r24,    cpuid                                    
        slli    r24,    r24,    2
        ldw     CPU_NIOS2_CLS_REG, cpu_local_storage(r24)
#endif

        // get context local storage
        CPU_LOCAL ldw, __context_data_base, r24, r24
        addi    r24,    r24,    %lo(nios2_context_regs)

        // save caller saved
        .irp    r, 4, 5, 6, 7, 8, 9, 27, 28, 31
        stw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r24)
        .endr

        // save status register
        rdctl   r9,             estatus
        stw     r9,             CPU_NIOS2_CONTEXT_STATUS(r24)

#ifdef CONFIG_HEXO_USERMODE
        // from user mode ?
        andi    r4,     r9,     2
        beq     r0,     r4,     1f

        // get tls
        movi    r8,             %lo(nios2_context_regs)
        sub     r8,     r24,    r8

        // use kernel context stack when from user mode
        ldw      r27,    context_stack_end(r8)
1:
#endif

        // get cause
        rdctl   r4, exception
        andi    r4,     r4,     0x7c

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // clear context preempt handler
        CPU_LOCAL stw     cpu_preempt_handler, r0, r5
#endif

#ifdef CONFIG_HEXO_USERMODE
        // handle system call ?
        movi    r5,     12
        beq     r4,     r5,     nios2_interrupt_sys
#endif

        // save all remaining "caller saved" registers (not syscall args)
        .irp    r, 1, 2, 3, 10, 11, 12, 13, 14, 15
        stw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r24)
        .endr

#ifdef CONFIG_HEXO_IRQ
        // handle irq ?
        movi    r5,     8
        beq     r4,     r5,     nios2_interrupt_hw
#endif

        /*************************************************************
          exception handling
         **************************************************************/
nios2_interrupt_ex:                                   

        // save return address
        addi     r29,    r29,   -4
        stw     r29,     CPU_NIOS2_CONTEXT_PC(r24)

#ifdef CONFIG_HEXO_USERMODE
        // from user mode ?
        andi    r5,     r9,     2
        beq     r0,     r5,     1f

        ldw     r1,     %lo(cpu_user_exception_handler)(r8)
        bne     r1,     r0,     2f
1:
#endif
        // use cpu local fault handler
        CPU_LOCAL ldw     cpu_exception_handler, r1, r1
2:      

        ldw     r2,     CPU_NIOS2_CONTEXT_GPR(27)(r24)
        addi    sp,     sp,     -4
        stw     r2,     0(sp)           // arg4 sp

        srli    r4,     r4,     2       // arg0 type
        mov     r5,     r29             // arg1 execptr
        rdctl   r6,     badaddr         // arg2 data error
        mov     r7,     r24             // arg3 reg table

        // call handler
        callr   r1

        ldw     r29,    CPU_NIOS2_CONTEXT_PC(r24)

        br      nios2_preempt_and_restore

        /*************************************************************
          syscall handling
         **************************************************************/

#ifdef CONFIG_HEXO_USERMODE
nios2_interrupt_sys:

        // save return address
        stw     r29,    CPU_NIOS2_CONTEXT_PC(r24)

        movi    r4,     0
        mov     r5,     r24

        // call handler
        ldw     r1,     %lo(cpu_syscall_handler)(r8)
        callr   r1

        ldw     r29,    CPU_NIOS2_CONTEXT_PC(r24)

        br      nios2_preempt_and_restore
#endif
        
        /*************************************************************
          hardware interrupts handling
         **************************************************************/

#ifdef CONFIG_HEXO_IRQ
nios2_interrupt_hw:                                   

        addi     r29,    r29,   -4
# ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // save return address
        stw     r29,    CPU_NIOS2_CONTEXT_PC(r24)
# endif

        /* hw interrupt line id */
        rdctl   r4,     ipending
        call    __ctzsi2
        mov     r4,     r2

        CPU_LOCAL ldw cpu_interrupt_handler, r1, r1
        callr   r1

#endif

nios2_preempt_and_restore:

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT

        // check if a preempt handler has been registered
        CPU_LOCAL ldw cpu_preempt_handler, r1, r1
        beq     r0,     r1,     1f

        // call preempt handler
        callr   r1

        // got pointer to context to switch to ?
        beq     r0,     r2,     1f

        // save callee saved registers
        .irp    r, 16, 17, 18, 19, 20, 21, 22, 23
        stw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r24)
        .endr

        // prepare cpu_context_jumpto arg
        mov     r4,     r2
# ifdef CONFIG_HEXO_CONTEXT_STATS
        mov     r16,     r2
        call    context_preempt_stats
        mov     r4,     r16
# endif

        // save pc
        movi    r1,             CPU_NIOS2_CONTEXT_RESTORE_CALLER | CPU_NIOS2_CONTEXT_RESTORE_CALLEE
        stw     r1,             CPU_NIOS2_CONTEXT_SAVE_MASK(r24)

        br      cpu_context_jumpto
1:
#endif

nios2_restore:

        // restore "caller saved" registers
        .irp    r, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 27, 28, 31
        ldw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r24)
        .endr

        eret

