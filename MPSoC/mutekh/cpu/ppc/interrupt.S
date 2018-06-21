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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2010

*/

#include <hexo/context.h>
#include <hexo/asm.h>
#include <hexo/interrupt.h>
#include <hexo/cpu.h>

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

  Have a look to context_switch.S for more.
*/



/*
  The PPC_EXCEPT_SAVE macro contains code to patially save processor
  state. Only "caller saved" registers are saved here because "callee saved"
  registers won't be clobbered by C execption handler: it's enough to
  completly restore processor state on return.

  Fpu registers are not saved because irq handlers should not rely on
  hardware floating point operations.
*/

.macro PPC_EXCEPT_SAVE srr_pc srr_msr
        // preserve r2
        mtspr   PPC_SPRG(2),    2

        // get context local storage
        mfspr   2,              PPC_SPRG(4)
        // get context registers save array
        addi    2,      2,      ppc_context_regs@l

        // save stack pointer and r0
        stw     1,              CPU_PPC_CONTEXT_GPR( 1)(2)
        stw     0,              CPU_PPC_CONTEXT_GPR( 0)(2)

        // save condition register
        mfcr    0
        stw     0,              CPU_PPC_CONTEXT_CR(2)

#if defined(CONFIG_HEXO_FPU) && !defined(CONFIG_RELEASE)
        // disable fpu access during exception handling
        mfmsr   0
        ori     0,      0,      PPC_MSR_FPU_ENABLED
        xori    0,      0,      PPC_MSR_FPU_ENABLED
        mtmsr   0
#endif

        // preserve ppc abi redzone
        addi    1,      1,      -224

        // save caller saved gp registers 2 to 13
        SMW_GP  2,      3,      13
        mfspr   0,              PPC_SPRG(2),
        stw     0,              CPU_PPC_CONTEXT_GPR( 2)(2)

        // save status register
        \srr_msr 8
#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        stw     8,              CPU_PPC_CONTEXT_MSR(2)
#endif

        // save link register
        mflr    0
        stw     0,              CPU_PPC_CONTEXT_LR(2)

        // save counter register
        mfctr   0
        stw     0,              CPU_PPC_CONTEXT_CTR(2)

        // read pc
        \srr_pc 4
.endm




/*
  The PPC_EXCEPT_PREEMPT_CLEAR macro contains code to unregister preemption handler
  so that we can check on handler return if a new handler has been registered.
*/

.macro  PPC_EXCEPT_PREEMPT_CLEAR
#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // clear context preempt handler
        li      9,              0
        CPU_LOCAL       stw     cpu_preempt_handler, 9, 10
#endif
.endm




/*
  The PPC_EXCEPT_FAULT macro contains code to call exception fault handlers
*/

.macro PPC_EXCEPT_FAULT type

        // save pc
        stw     4,              CPU_PPC_CONTEXT_PC(2)

        // prepare handler call
        li      3,      \type           // arg0 type
//      mr      4,      4               // arg1 execptr
        mfdear  5                       // arg2 data error
        mr      6,      2               // arg3 reg table
        addi    7,      1,      224     // arg4 sp, skip redzone

        // find right handler
#ifdef CONFIG_HEXO_USERMODE
        // from user mode ?
        andi.   10,     8,      PPC_MSR_USERMODE
        beq     1f

        // use kernel context stack when from user mode
        CONTEXT_LOCAL   lwz     context_stack_end, 1, 10
        addi    1,      1,      -CONFIG_HEXO_STACK_ALIGN

        // use context local fault handler for user when available
        CONTEXT_LOCAL   lwz     cpu_user_exception_handler, 9, 10
        and.    9,      9,      9
        bne     2f
1:
#endif
        // use cpu local fault handler
        CPU_LOCAL       lwz     cpu_exception_handler, 9, 10
2:

        mtctr   9
        bctrl

        // read back pc
        lwz     4,              CPU_PPC_CONTEXT_PC(2)
        mtsrr0  4

.endm



/*
  The PPC_EXCEPT_SYSCALL macro contains code to call syscall handlers
*/

#ifdef CONFIG_HEXO_USERMODE
.macro PPC_EXCEPT_SYSCALL

        // save pc
        stw     4,              CPU_PPC_CONTEXT_PC(2)

        // from user mode ?
        andi.   10,     8,      PPC_MSR_USERMODE
        beq     1f

        // use kernel context stack
        CONTEXT_LOCAL   lwz     context_stack_end, 1, 10
        addi    1,      1,      -CONFIG_HEXO_STACK_ALIGN
1:

        CONTEXT_LOCAL   lwz     cpu_syscall_handler, 9, 10
        li      3,      0               // arg0 number
        mr      4,      2               // arg1 reg table
        mtctr   9
        bctrl

        // read back pc
        lwz     4,              CPU_PPC_CONTEXT_PC(2)
        mtsrr0  4

.endm
#endif


/*
  The PPC_EXCEPT_IRQ macro contains code to call irq handlers
*/

.macro PPC_EXCEPT_IRQ number

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // save pc
        stw     4,              CPU_PPC_CONTEXT_PC(2)
#endif

#ifdef CONFIG_HEXO_USERMODE
        // from user mode ?
        andi.   10,     8,      PPC_MSR_USERMODE
        beq     1f

        // use kernel context stack
        CONTEXT_LOCAL   lwz     context_stack_end, 1, 10
        addi    1,      1,      -CONFIG_HEXO_STACK_ALIGN
1:
#endif
#ifndef CONFIG_RELEASE
        // ensure we do not use context local storage in irq handler
        li      0,              0
        mtspr   PPC_SPRG(4),    0
#endif

        CPU_LOCAL       lwz     cpu_interrupt_handler, 9, 10
        li      3,      \number         // arg0 number
        mtctr   9
        bctrl

#ifndef CONFIG_RELEASE
        // restore cls
        li      0,              ppc_context_regs@l
        sub     0,      2,      0
        mtspr   PPC_SPRG(4),    0
#endif
.endm




/*
  The PPC_EXCEPT_PREEMPT macro contains code to call preemption handler,
  finish current context registers saving and switch to preempted context.
*/

.macro  PPC_EXCEPT_PREEMPT
#ifdef CONFIG_HEXO_CONTEXT_PREEMPT

        // check if a preempt handler has been registered
        CPU_LOCAL       lwz     cpu_preempt_handler, 9, 10
        and.    9,      9,      9
        beq     2f

        // call preempt handler
        mtctr   9
        bctrl

        // got pointer to context to switch to ?
        and.    3,      3,      3
        beq     2f

        // save callee saved registers r14 to r31
        stmw    14,             CPU_PPC_CONTEXT_GPR(14)(2)

# ifdef CONFIG_HEXO_CONTEXT_STATS
        mr      14,             3
        LA32    9,              context_preempt_stats
        mtctr   9
        bctrl
        mr      3,              14
# endif

        // lets deals with all not yet saved registers

# ifdef CONFIG_HEXO_FPU
        // test fpu enabled for previous context
        lwz     8,              CPU_PPC_CONTEXT_MSR(2)
        andi.   5,      8,      PPC_MSR_FPU_ENABLED
        beq     1f

#  ifdef CONFIG_HEXO_LAZY_SWITCH
        // keep track of last context using fpu on this processor
        CPU_LOCAL       stw     ppc_lazy_last, 2, 10
#  endif

        // save fpu registers 0 to 31
        SMD_FPU 2,      0,      31
        // save fpu status
        mffs    0
        stfd    0,              CPU_PPC_CONTEXT_FPSCR(2)
1:
# endif

        // set save mask
        li      0,              CPU_PPC_CONTEXT_RESTORE_CALLER | CPU_PPC_CONTEXT_RESTORE_CALLEE
        stw     0,              CPU_PPC_CONTEXT_SAVE_MASK(2)

        // switch to new context
        b       cpu_context_jumpto
2:
#endif
.endm




/*
  The PPC_EXCEPT_RESTORE macro contains code to restore "caller saved" registers
  and return to interrupted code.
*/

.macro  PPC_EXCEPT_RESTORE

        // restore link register
        lwz     0,              CPU_PPC_CONTEXT_LR(2)
        mtlr    0

        // restore caller saved counter register
        lwz     0,              CPU_PPC_CONTEXT_CTR(2)
        mtctr   0

        // restore caller saved gp registers 3 to 13
        LMW_GP  2,      3,      13

        // restore condition register
        lwz     0,              CPU_PPC_CONTEXT_CR(2)
        mtcr    0

        // restore 0 to 2
        LMW_GP  2,      0,      2

        rfi
.endm

.section .excep,"ax",@progbits



        /* critical interrupt (critical, async) */
FUNC_START_ORG(ppc_critical_entry, 0x100)
        PPC_EXCEPT_SAVE mfsrr2 mfsrr3
        PPC_EXCEPT_FAULT 0
1:      b 1b
FUNC_END(ppc_critical_entry)



        /* machine check (critical, async, imprecise) */
FUNC_START_ORG(ppc_machine_entry, 0x200)
        PPC_EXCEPT_SAVE mfsrr2 mfsrr3
        PPC_EXCEPT_FAULT 0
1:      b 1b
FUNC_END(ppc_machine_entry)



        /* data storage */
FUNC_START_ORG(ppc_data_storage_entry, 0x300)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
        PPC_EXCEPT_PREEMPT_CLEAR
        PPC_EXCEPT_FAULT CPU_EXCEPTION_DATA_ERROR
        b ppc_preempt_and_restore
FUNC_END(ppc_data_storage_entry)



        /* instr storage */
FUNC_START_ORG(ppc_instr_storage_entry, 0x400)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
        PPC_EXCEPT_PREEMPT_CLEAR
        PPC_EXCEPT_FAULT CPU_EXCEPTION_INS_ERROR
        b ppc_preempt_and_restore
FUNC_END(ppc_instr_storage_entry)



        /* external (async) */
FUNC_START_ORG(ppc_external_entry, 0x500)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
        PPC_EXCEPT_PREEMPT_CLEAR
#ifdef CONFIG_HEXO_IRQ
        PPC_EXCEPT_IRQ 0
#else
        PPC_EXCEPT_FAULT CPU_EXCEPTION_IRQ        
#endif
        b ppc_preempt_and_restore
FUNC_END(ppc_external_entry)



        /* alignment */
FUNC_START_ORG(ppc_align_entry, 0x600)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
        PPC_EXCEPT_PREEMPT_CLEAR
        PPC_EXCEPT_FAULT CPU_EXCEPTION_DATA_ALIGN
        b ppc_preempt_and_restore
FUNC_END(ppc_align_entry)



        /* program */
FUNC_START_ORG(ppc_program_entry, 0x700)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
#if defined(CONFIG_HEXO_LAZY_SWITCH) && defined(CONFIG_HEXO_FPU)
        // real error if fpu already enabled
        andi.   10,     8,      PPC_MSR_FPU_ENABLED
        bne     1f
        // enable fpu and retry
        ori     8,      8,      PPC_MSR_FPU_ENABLED
        mtsrr1  8
        b       ppc_restore_only
1:
#endif
        PPC_EXCEPT_PREEMPT_CLEAR
        PPC_EXCEPT_FAULT CPU_EXCEPTION_ILLEGAL_INS
        b ppc_preempt_and_restore
FUNC_END(ppc_program_entry)



        /* fpu unusable */
FUNC_START_ORG(ppc_fpu_entry, 0x800)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
        PPC_EXCEPT_PREEMPT_CLEAR
        PPC_EXCEPT_FAULT CPU_EXCEPTION_ILLEGAL_INS
        b ppc_preempt_and_restore
FUNC_END(ppc_fpu_entry)



        /* syscall */
FUNC_START_ORG(ppc_syscall_entry, 0xc00)
        PPC_EXCEPT_SAVE mfsrr0 mfsrr1
        PPC_EXCEPT_PREEMPT_CLEAR
#ifdef CONFIG_HEXO_USERMODE
        PPC_EXCEPT_SYSCALL
	// FIXME do not need to restore everything for syscall
#else
        PPC_EXCEPT_FAULT CPU_EXCEPTION_SYSCALL
#endif
        b ppc_preempt_and_restore
FUNC_END(ppc_syscall_entry)

FUNC_START(.excep, ppc_preempt_and_restore)
        PPC_EXCEPT_PREEMPT
  ppc_restore_only:
        PPC_EXCEPT_RESTORE
FUNC_END(ppc_preempt_and_restore)

/* apu unavailable */
.org 0xf20

/* programmable-interval timer (async) */
.org 0x1000

/* fixed-interval timer (async) */
.org 0x1010

/* watchdog (critical, async) */
.org 0x1020

/* data tlb */
.org 0x1100

/* ins tlb */
.org 0x1200

/* debug (critical, (a)sync) */
.org 0x2000

// Local Variables:
// indent-tabs-mode: nil;
// vim: filetype=cpp:expandtab

