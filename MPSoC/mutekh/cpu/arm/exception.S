/*
   This file is part of MutekH.
   
   MutekH is free software; you can redistribute it and/or modify it
   under the terms of the GNU Lesser General Public License as published
   by the Free Software Foundation; version 2.1 of the License.
   
   MutekH is distributed in the hope that it will be useful, but WITHOUT
   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
   License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with MutekH; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
   02110-1301 USA.
  
   Copyright (c) 2010, Nicolas Pouillon <nipo@ssji.net>
*/

#include <hexo/asm.h>
#include <hexo/interrupt.h>
#include <hexo/cpu.h>
#include <hexo/context.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

.macro PREPARE_EXCEPTION offset
    /* uniformize return address, but avoid a sub when 0 */
    .if \offset != 0
        sub    lr, lr, #\offset
    .endif

    /* SP points to r0 in cpu_context_s */

    /* store most registers in tmp buffer */
    stmia  sp, {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12}
    mov    r1, sp

    /* Save PC */
    str    lr, [r1, #CPU_ARM_CONTEXT_GPR(15)]

    /* Tell what to restore */
    mov     r0, #CPU_ARM_CONTEXT_RESTORE_CALLEE|CPU_ARM_CONTEXT_RESTORE_CALLER
    str     r0, [r1, #CPU_ARM_CONTEXT_SAVE_MASK]

    /* Take old msr, save it */
    mrs    r0, spsr
    str    r0, [r1, #CPU_ARM_CONTEXT_CPSR]

    /* last but not least, we have to save sp and lr, this
       may involve shadowed registers */

    /* Switch to super (After taking sp+x in r1) */
    msr      cpsr_c, #0xd3

    add    r2, r1, #CPU_ARM_CONTEXT_GPR(13)

#if defined(CONFIG_HEXO_USERMODE)
    /* r0 is still cpsr */
    tst    r0, #0xf
    /* if 0 (eq), from user mode */
    /* We were from super, go to save super */
    bne 1f
    
    /* from user mode, abuse stmia^ */
    stmia  r2, {r13, r14}^
    /* Always restore kernel stack pointer */
    CONTEXT_LOCAL_ld context_stack_end, r3, r13
    b 2f
1:  
#endif        
    /* We were from super, so save super's sp and lr */
    stmia  r2, {r13, r14}
2:  

    /* align stack */
    bic    sp, sp, #63
#ifdef CONFIG_COMPILE_FRAMEPTR
    mov  r11, r13
#endif

    /* r1 still points to arm_context_regs */
.endm

.macro PREEMPT_CLEAR
#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
    mov    r3, #0
    CPU_LOCAL_st cpu_preempt_handler, r1, r2, r3
#endif
.endm

    
    /* r14 is exc pc + 4 */
FUNC_START(.text, arm_exc_undef)
    PREPARE_EXCEPTION 4
    PREEMPT_CLEAR
    ldr    lr, =arm_exc_restore
    mov    r0, #CPU_EXCEPTION_ILLEGAL_INS
    b      arm_exc_common
FUNC_END(arm_exc_undef)

    /* r14 is error pc + 4 */
FUNC_START(.text, arm_exc_pabt)
    PREPARE_EXCEPTION 4
    PREEMPT_CLEAR
    ldr    lr, =arm_exc_restore
    mov    r0, #CPU_EXCEPTION_INS_ERROR
    b      arm_exc_common
FUNC_END(arm_exc_pabt)

    /* r14 is error pc + 8 */
FUNC_START(.text, arm_exc_dabt)
    PREPARE_EXCEPTION 8
    PREEMPT_CLEAR
    ldr    lr, =arm_exc_restore
    mov    r0, #CPU_EXCEPTION_DATA_ERROR
    b      arm_exc_common
FUNC_END(arm_exc_dabt)

#if defined(CONFIG_HEXO_IRQ) && !defined(CONFIG_CPU_ARM_CUSTOM_IRQ_HANDLER)
    /* r14 is error pc + 4 */
FUNC_START(.text, arm_exc_irq)
    PREPARE_EXCEPTION 4
    PREEMPT_CLEAR
    ldr    lr, =arm_exc_restore
    mov    r0, #0
    b      arm_irq_common
FUNC_END(arm_exc_irq)

    /* r14 is error pc + 4 */
FUNC_START(.text, arm_exc_fiq)
    .warning "Context saving is not correct for fiq handling"
    PREPARE_EXCEPTION 4
    PREEMPT_CLEAR
    ldr    lr, =arm_exc_restore
    mov    r0, #1
    b      arm_irq_common
FUNC_END(arm_exc_fiq)
#endif

#ifdef CONFIG_HEXO_USERMODE
FUNC_START(.text, arm_exc_swi)
    /* r14 is swi pc + 4 */

    /* We are in swi from userland, we cant trust sp, restore it */
    CONTEXT_LOCAL_ld context_stack_end, sp, r12
    bic    sp, sp, #63

    // Now we have a trusted stack, we can work...
    /* We must save r11, but we have nothing else than a stack, so
     * use it. Beware of side-effects when playing with sp (see below)
     */
    push   {r11}
    // Use r11 to reference cpu_context
    CONTEXT_LOCAL_addr arm_context_regs, r11, r12

    // Save return address
    str    lr, [r11, #CPU_ARM_CONTEXT_GPR(15)]

    mov    r12, #CPU_ARM_CONTEXT_RESTORE_CALLEE|CPU_ARM_CONTEXT_RESTORE_CALLER
    str    r12, [r11, #CPU_ARM_CONTEXT_SAVE_MASK]

    // Save caller-saved regs (arguments)
    stmia  r11, {r0, r1, r2, r3}

    // Save mode
    mrs    r1, spsr
    str    r1, [r11, #CPU_ARM_CONTEXT_CPSR]

    /* From user mode, use stmia^ to save user sp and lr */
    add    r12, r11, #CPU_ARM_CONTEXT_GPR(13)
    stmia  r12, {r13, r14}^

    /* And finally save r11 */
    pop    {r12}
    str    r12, [r11, #CPU_ARM_CONTEXT_GPR(11)]

    PREEMPT_CLEAR

    ldr    lr, =arm_exc_restore
    mov    r0, r11
    b      arm_swi_common
FUNC_END(arm_exc_swi)
#endif

FUNC_START(.text, arm_exc_restore)

#if defined(CONFIG_HEXO_CONTEXT_PREEMPT)
    /* got pointer to new context to restore ? */
    movs  r0, r0
    bne   cpu_context_jumpto
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK_
    /* enter memchecker command mode */
    ldr  r2, =SOCLIB_MC_MAGIC_VAL 
    ldr  r1, =CONFIG_SOCLIB_MEMCHECK_ADDRESS 
    str  r2, [r1, #SOCLIB_MC_MAGIC_OFFSET]

    /* leave memchecker command mode */
    ldr     r3, =2f
    str     r3, [r1, #SOCLIB_MC_DELAYED_MAGIC_OFFSET]
#endif
    // context registers save array
    CONTEXT_LOCAL_addr arm_context_regs, r2, r3

    b arm_context_jumpto_internal
FUNC_END(arm_exc_restore)


// Local Variables:
// tab-width: 4;
// c-basic-offset: 4;
// indent-tabs-mode: nil;
// End:
//
// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4
