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
    Copyright (c) 2010, Nicolas Pouillon <nipo@ssji.net>
*/

#include <hexo/context.h>
#include <hexo/cpu.h>
#include <hexo/asm.h>

/*
  The exception handling code share the processor state
  array used to save context registers values. When an exception occurs,
  we only save "caller saved" registers before calling the handler.
  When the handler returns we have two possible choices:
  
    * Restore "caller saved" registers and return to interrupted code
    * Complete current context saving with "callee saved" and fpu registers
      and switch to an other context.
  
  The context preemtion handler is used to take the switching decision.
  This preemption handler must be setup by C code during exception handling
  if their is a need to switch context instead of returning.
  
  Doing this allow us to perform as few register saving operations as
  possible because registers are already partially saved for exception
  handling purpose if we decide to switch context.
*/


/*
   Callee saved: r16-r23, r28-r31
   Caller saved: r1-r15, r24-r25, lo, hi
*/

.section        .excep,"ax",@progbits         

        .set push                                       
        .set noreorder                                  
        b       mips_interrupt_entry
#if CONFIG_CPU_MIPS_VERSION >= 32
        .space 0x17c                                    
#else
        .space 0x7c                                     
#endif
        .set pop                                        

        .globl mips_interrupt_entry
        .func mips_interrupt_entry
        .type mips_interrupt_entry , %function
mips_interrupt_entry:
        .set push                                       
        .set noat                                       

#if defined(CONFIG_HEXO_USERMODE) && defined(CONFIG_ARCH_SMP)
        /* restore cpu local storage */
        CPU_ID  $26
        sll     $26,    $26,    2
        lw      $27,    cpu_local_storage($26)       
# endif

        // get context local storage
        CPU_LOCAL lw,   __context_data_base, $26, $26
        // get context registers save array
        addiu   $26,            %lo(mips_context_regs)

        // save only 6 "caller saved" registers yet (syscall args ragtable)
        // save gp ptr, stack ptr, frame ptr, return address
        .irp    r, 4, 5, 6, 7, 8, 9, 28, 29, 30, 31
        sw      $\r,            CPU_MIPS_CONTEXT_GPR(\r)($26)
        .endr

        // save status register
        mfc0    $9,     $CPU_MIPS_STATUS
        sw      $9,     CPU_MIPS_CONTEXT_SR($26)

#if defined(CONFIG_HEXO_FPU) && !defined(CONFIG_RELEASE)
        // disable fpu access
        li      $5,     CPU_MIPS_STATUS_FPU
        nor     $6,     $0,     $5
        and     $6,     $9,     $6
        MTC0_   $6,     CPU_MIPS_STATUS
#endif

#ifdef CONFIG_HEXO_USERMODE

        // from user mode ?
# ifdef CONFIG_CPU_MIPS_USE_ERET
        andi    $7,     $9,     CPU_MIPS_STATUS_UM
# else
        andi    $7,     $9,     CPU_MIPS_STATUS_KUp
# endif
        beq     $7,     $0,     1f

        // get tls
        li      $8,     %lo(mips_context_regs)
        sub     $8,     $26,    $8

        // use kernel context stack when from user mode
        lw      $sp,    context_stack_end($8)
        addiu   $sp,    $sp,    -4 * INT_REG_SIZE/8
1:
#endif

        // get cause
        mfc0    $6,     $CPU_MIPS_CAUSE                          
        andi    $4,     $6,     0x3c                 

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // clear context preempt handler
        CPU_LOCAL sw    cpu_preempt_handler, $0, $7
#endif

#ifdef CONFIG_HEXO_USERMODE
        // handle system call ?
        li      $7,     32                           
        beq     $4,     $7,     interrupt_sys        
#endif

        // save all remaining "caller saved" registers (not syscall args)
        .irp    r, 1, 2, 3, 10, 11, 12, 13, 14, 15, 24, 25
        sw      $\r,            CPU_MIPS_CONTEXT_GPR(\r)($26)
        .endr

        mflo    $1
        sw      $1,             CPU_MIPS_CONTEXT_LO($26)
        mfhi    $1
        sw      $1,             CPU_MIPS_CONTEXT_HI($26)

#ifdef CONFIG_HEXO_IRQ
        // handle irq ?
        beq     $4,     $0,     interrupt_hw
#endif

        /*************************************************************
          exception handling
         **************************************************************/
interrupt_ex:                                   

        // get epc
        mfc0    $5,     $CPU_MIPS_EPC
        sw      $5,     CPU_MIPS_CONTEXT_PC($26)

        // faulty instruction is epc + 4 if 'cause branch delay' bit is set
        li      $7,     CPU_MIPS_CAUSE_BD
        and     $6,     $6,     $7
        beq     $6,     $0,     1f
        addiu   $5,     $5,     4
1:      

#ifdef CONFIG_HEXO_USERMODE

        // from user mode ?
# ifdef CONFIG_CPU_MIPS_USE_ERET
        andi    $7,     $9,     CPU_MIPS_STATUS_UM
# else
        andi    $7,     $9,     CPU_MIPS_STATUS_KUp
# endif
        beq     $7,     $0,     1f
        
        // use context local fault handler for user mode when available
        lw      $1,     cpu_user_exception_handler($8)
        bne     $1,     $0,     2f
1:
#endif
        // use cpu local fault handler
        CPU_LOCAL lw,   cpu_exception_handler, $1, $1
2:

        addiu   $sp,    $sp,    -5 * INT_REG_SIZE/8

        lw      $6,             CPU_MIPS_CONTEXT_GPR(29)($26)
        sw      $6,             (4 * INT_REG_SIZE/8)($sp) // arg5: stackptr

        srl     $4,     $4,     2                  // arg0 type
        //      arg1: $5 is execptr
        mfc0    $6,     $CPU_MIPS_BADADDR          // arg2 data error
        move    $7,     $26                        // arg3 reg table

        // call handler
        jalr    $1

#ifdef CONFIG_CPU_MIPS_USE_ERET
        // read back pc
        lw      $1,    CPU_MIPS_CONTEXT_PC($26)
        MTC0_   $1,    CPU_MIPS_EPC
#endif

        j       mips_preempt_and_restore

        /*************************************************************
          syscall handling
         **************************************************************/
#ifdef CONFIG_HEXO_USERMODE
interrupt_sys:                                  

        // get epc
        mfc0    $5,     $CPU_MIPS_EPC
        // resume pc += 4
        addiu   $5,     $5,     4
        sw      $5,     CPU_MIPS_CONTEXT_PC($26)

        move    $4,     $0                             // arg0 id == 0
        move    $5,     $26                            // arg1 reg table

        lw      $1,     cpu_syscall_handler($8)

        addiu   $sp,    $sp,    -4 * INT_REG_SIZE/8
        jalr    $1                                   

# ifdef CONFIG_CPU_MIPS_USE_ERET
        // read back pc
        lw      $1,     CPU_MIPS_CONTEXT_PC($26)
        MTC0_   $1,     CPU_MIPS_EPC
# endif

        // FIXME cleanup all caller saved registers here for security reasons ?
        // FIXME do not need to restore everything for syscall
        j       mips_preempt_and_restore
#endif

        /*************************************************************
          hardware interrupts handling
         **************************************************************/
#ifdef CONFIG_HEXO_IRQ
interrupt_hw:                                   

#if defined (CONFIG_HEXO_CONTEXT_PREEMPT) || !defined (CONFIG_CPU_MIPS_USE_ERET)
        // get epc
        mfc0    $5,     $CPU_MIPS_EPC
        sw      $5,     CPU_MIPS_CONTEXT_PC($26)
#endif

        /* pending hw irq enabled in status */
        and     $4,     $6,     $9
        srl     $4,     $4,     CPU_MIPS_STATUS_IM_SHIFT
        andi    $4,     $4,     CPU_MIPS_STATUS_IM >> CPU_MIPS_STATUS_IM_SHIFT

        /* find lowest pending irq line */
#if (CPU_MIPS_STATUS_IM >> CPU_MIPS_STATUS_IM_SHIFT) > 256
# warning Interrupt mask larger than 8 bits
        jal     __ffssi2
        addiu   $4,     $2,     -1
#else
        // optimized 8 bits version
        subu    $2,	$0,	$4
        and     $2,	$2,	$4
        addiu   $2,	$2,	-1
        srl     $4,	$2,	1
        andi    $4,	$4,	0x55
        subu    $4,	$2,	$4
        srl     $2,	$4,	2
        andi    $2,	$2,	0x33
        andi    $4,	$4,	0x33
        addu    $2,	$2,	$4
        srl     $3,	$2,	4
        addu    $4,	$3,	$2
#endif

        CPU_LOCAL lw,   cpu_interrupt_handler, $1, $1

        addiu   $sp,    $sp,    -4 * INT_REG_SIZE/8
        jalr    $1                                   

        j       mips_preempt_and_restore
#endif

mips_preempt_and_restore:

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT

        // check if a preempt handler has been registered
        CPU_LOCAL lw    cpu_preempt_handler, $1, $1
        beq     $1,     $0,     mips_restore

        // call preempt handler
        jalr    $1

        // got pointer to context to switch to ?
        beq     $2,     $0,     mips_restore

        // save callee saved registers
        .irp    r, 16, 17, 18, 19, 20, 21, 22, 23
        sw      $\r,            CPU_MIPS_CONTEXT_GPR(\r)($26)
        .endr

        // prepare cpu_context_jumpto arg
        move    $4,     $2
# ifdef CONFIG_HEXO_CONTEXT_STATS
        move    $16,    $2
        jal     context_preempt_stats
        move    $4,     $16
# endif

# ifdef CONFIG_HEXO_FPU
        // test fpu enabled for previous context
        lw      $9,     CPU_MIPS_CONTEXT_SR($26)
        li      $7,     CPU_MIPS_STATUS_FPU
        and     $7,     $7,     $9
        beq     $7,     $0,     1f

#  ifdef CONFIG_HEXO_LAZY_SWITCH
        // keep track of last context using fpu on this processor
        CPU_LOCAL sw    mips_lazy_last, $26, $1
#  endif

        // save fpu registers 0 to 31
        .irp    r, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19
        SxC1    \r,            CPU_MIPS_CONTEXT_FR(\r)($26)
        .endr
        .irp    r, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
        SxC1    \r,            CPU_MIPS_CONTEXT_FR(\r)($26)
        .endr

        // save fpu status
        cfc1    $1,     $31
        sw      $1,     CPU_MIPS_CONTEXT_FCSR($26)
1:
# endif

        li      $1,     CPU_MIPS_CONTEXT_RESTORE_CALLER | CPU_MIPS_CONTEXT_RESTORE_CALLEE
        sw      $1,     CPU_MIPS_CONTEXT_SAVE_MASK($26)

        // switch to new context
        b       cpu_context_jumpto
#endif

mips_restore:

#if defined(CONFIG_HEXO_FPU) && !defined(CONFIG_RELEASE)
        // restore, fpu have been disabled during irq handling
        lw      $4,     CPU_MIPS_CONTEXT_SR($26)
        MTC0_   $4,     CPU_MIPS_STATUS
#endif

        // restore some "caller saved" registers
        lw      $1,             CPU_MIPS_CONTEXT_LO($26)
        mtlo    $1
        lw      $1,             CPU_MIPS_CONTEXT_HI($26)
        mthi    $1

        .irp    r, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 24, 25
        lw      $\r,            CPU_MIPS_CONTEXT_GPR(\r)($26)
        .endr

        // FIXME syscall could restore from this point
        // restore return value register
        lw      $2,             CPU_MIPS_CONTEXT_GPR(2)($26)

        // restore frame, stack...
        .irp    r, 28, 29, 30, 31
        lw      $\r,            CPU_MIPS_CONTEXT_GPR(\r)($26)
        .endr

#ifdef CONFIG_CPU_MIPS_USE_ERET
        eret
#else
        lw      $26,    CPU_MIPS_CONTEXT_PC($26)
        .set noreorder                                  
        jr      $26                                  
        rfe                                          
#endif
        .set pop                                        
FUNC_END(mips_interrupt_entry)

// Local Variables:
// indent-tabs-mode: nil;
// vim: filetype=cpp:expandtab

