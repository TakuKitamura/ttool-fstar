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

    Copyright (c) 2010, Nicolas Pouillon, <nipo@ssji.net>
*/

#include <hexo/context.h>
#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

/*
   Callee saved: r4-r11 r13
   Caller saved: r12
    + Arguments: r0-r3
*/

FUNC_START(.text, cpu_context_switch)
    // get context registers save array
    CONTEXT_LOCAL_ADDR arm_context_regs, r2, r3

    // save "callee saved" registers
    add     r1, r2, #CPU_ARM_CONTEXT_GPR(4)
    stmia   r1, {r4, r5, r6, r7, r8, r9, r10, r11}
    str     r13, [r2, #CPU_ARM_CONTEXT_GPR(13)]

    // write save/restore mask
    mov     r4, #CPU_ARM_CONTEXT_RESTORE_CALLEE
    str     r4, [r2, #CPU_ARM_CONTEXT_SAVE_MASK]

    mrs     r4, cpsr
    str     r4, [r2, #CPU_ARM_CONTEXT_CPSR]
    str     r14, [r2, #CPU_ARM_CONTEXT_GPR(15)]
    
    b       cpu_context_jumpto
FUNC_END(cpu_context_switch)


.macro CPU_EXC_SP_SETUP rsp
    // IRQ
    msr  cpsr_c, #0xd2
    mov  sp, \rsp
    // Abort
    msr  cpsr_c, #0xd7
    mov  sp, \rsp
    // Undef
    msr  cpsr_c, #0xdb
    mov  sp, \rsp
    // We're back
    msr  cpsr_c, #0xd3
.endm


FUNC_START(.text, arm_setup_exception_stack)

#ifdef CONFIG_SOCLIB_MEMCHECK
    /* enter memchecker command mode */
    ldr  r2, =SOCLIB_MC_MAGIC_VAL 
    ldr  r1, =CONFIG_SOCLIB_MEMCHECK_ADDRESS 
    str  r2, [r1, #SOCLIB_MC_MAGIC_OFFSET]
#endif
        
    CPU_EXC_SP_SETUP r0

#ifdef CONFIG_SOCLIB_MEMCHECK
    mov  r3, #0
    str  r3, [r1, #SOCLIB_MC_MAGIC_OFFSET]
#endif

    mov pc, lr
FUNC_END(arm_setup_exception_stack)

        
FUNC_START(.text, cpu_context_jumpto)

# ifdef CONFIG_ARCH_SMP
    // unlock some atomic value on context restore if requested
    ldr   r2, [r0, #HEXO_CONTEXT_S_UNLOCK * 4]
    cmp   r2, #0
    movne r1, #0
    strne r1, [r0, #HEXO_CONTEXT_S_UNLOCK * 4]
    strne r1, [r2]
#endif

    // restore tls pointer from context struct
    ldr  r3, [r0, #HEXO_CONTEXT_S_TLS * 4]
    TLS_BASE_SET r3, r2

    // context registers save array
    CONTEXT_LOCAL_addr arm_context_regs, r2, r3

#ifdef CONFIG_SOCLIB_MEMCHECK
    /* enter memchecker command mode */
    ldr  r3, =SOCLIB_MC_MAGIC_VAL 
    ldr  r1, =CONFIG_SOCLIB_MEMCHECK_ADDRESS 
    str  r3, [r1, #SOCLIB_MC_MAGIC_OFFSET]

    /* switch to associated memchecker context */
    str  r0, [r1, #SOCLIB_MC_CTX_SET_OFFSET]

    mov  r3, #0
    str  r3, [r1, #SOCLIB_MC_MAGIC_OFFSET]
#endif

    // Set sp to context &arm_context_regs for all error modes
    CPU_EXC_SP_SETUP r2

FUNC_END(cpu_context_jumpto)

        
FUNC_START(.text, arm_context_jumpto_internal)

    mov     r0, r2

    // Take &[r13 r14] in sp
    add     sp, r2, #CPU_ARM_CONTEXT_GPR(13)

#if defined(CONFIG_HEXO_USERMODE)
    // Load spsr
    ldr     lr, [r2, #CPU_ARM_CONTEXT_CPSR]
#endif

    // Test whether we need to restore caller saved
    ldr     r1, [r2, #CPU_ARM_CONTEXT_SAVE_MASK]
    tst     r1, #CPU_ARM_CONTEXT_RESTORE_CALLER

    // We do
    // r0 is &r0
    ldmneia r0, {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12}
    // We dont
    addeq   r0, r2, #CPU_ARM_CONTEXT_GPR(4)
    ldmeqia r0, {r4, r5, r6, r7, r8, r9, r10, r11}

#if defined(CONFIG_HEXO_USERMODE)
    /* From now on, we can only touch lr, which is spsr for now. */
    
    /* If we are from user mode, restore user regs */
    tst     lr, #0xf
    /* if 0 (eq), from user mode */
    /* from user mode, use ldmia^ */
    ldmeqia  sp, {r13, r14}^
    /* We were from super, so restore in place */
    ldmneia  sp, {r13, r14}
#else
    ldmia    sp, {r13, r14}
#endif
    
    // Switch to any error mode, say, abort
    msr      cpsr_c, #0xd7

    // Stack pointer is now abort mode's one we set previously in
	// CPU_EXC_SP_SETUP. It points to &r0

    // restore status
    ldr     lr, [sp, #CPU_ARM_CONTEXT_CPSR]
    msr     spsr, lr

    // Load pc
    ldr      lr, [sp, #CPU_ARM_CONTEXT_GPR(15)]
    // Restore
    movs     pc, lr

arm_context_jumpto_internal_end:
FUNC_END(arm_context_jumpto_internal)


FUNC_START(.text, cpu_context_set)
#ifdef CONFIG_SOCLIB_MEMCHECK
    /* enter memchecker command mode */
    ldr  r3, =SOCLIB_MC_MAGIC_VAL 
    ldr  r4, =CONFIG_SOCLIB_MEMCHECK_ADDRESS 
    str  r3, [r4, #SOCLIB_MC_MAGIC_OFFSET]

    /* switch to associated memchecker context */
    str  r0, [r4, #SOCLIB_MC_CTX_SET_OFFSET]

    /* mark current memchecker context as invalid */
    mov  r3, #SOCLIB_MC_CTX_ID_CURRENT
    str  r3, [r4, #SOCLIB_MC_CTX_INVALIDATE_OFFSET]

    /* create a new temporary memchecker context using passed stack */
    str  r0, [r4, #SOCLIB_MC_R1_OFFSET]
    str  r1, [r4, #SOCLIB_MC_R2_OFFSET]
    str  r0, [r4, #SOCLIB_MC_CTX_CREATE_TMP_OFFSET]

    /* switch to new temporary memchecker context */
    str  r0, [r4, #SOCLIB_MC_CTX_SET_OFFSET]
#endif

    /* Set stack pointer, 64 bits aligned */
    add  r13, r0, r1
    bic  r13, #63
#ifdef CONFIG_COMPILE_FRAMEPTR
    mov  r11, r13
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
    /* leave memchecker command mode */
    mov  r0, #0
    str  r0, [r4, #SOCLIB_MC_MAGIC_OFFSET]
#endif

    /* Jump */
    bx   r2
FUNC_END(cpu_context_set)



FUNC_START(.text, cpu_context_stack_use)
#ifdef CONFIG_SOCLIB_MEMCHECK
    /* enter memchecker command mode */
    ldr  r3, =SOCLIB_MC_MAGIC_VAL 
    ldr  r4, =CONFIG_SOCLIB_MEMCHECK_ADDRESS 
    str  r3, [r4, #SOCLIB_MC_MAGIC_OFFSET]

    /* switch to associated memchecker context */
    str  r0, [r4, #SOCLIB_MC_CTX_SET_OFFSET]
#endif

    // restore tls pointer from context struct
    ldr  r5, [r0, #HEXO_CONTEXT_S_TLS * 4]
    TLS_BASE_SET r5, r6

    // context registers save array
    CONTEXT_LOCAL_addr arm_context_regs, r6, r3

    // use stack ptr
    ldr     r13, [r6, #CPU_ARM_CONTEXT_GPR(13)]
#ifdef CONFIG_COMPILE_FRAMEPTR
    mov  r11, r13
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
    /* leave memchecker command mode */
    mov  r0, #0
    str  r0, [r4, #SOCLIB_MC_MAGIC_OFFSET]
#endif

#ifndef CONFIG_RELEASE
    // prevent use of previous context local storage
    mov  r6, #0
    TLS_BASE_SET r6, r3
#endif

    // private param
    mov     r0, r2

    // call function
    bx      r1
FUNC_END(cpu_context_stack_use)




#ifdef CONFIG_HEXO_USERMODE

FUNC_START(.text, cpu_context_set_user)
    /* set user stack reg */
    stmda   sp, {r0}
    ldmda   sp, {r13}^

    /* set arg */
    mov     r0,     r2

    /* set previous state as user and restore */
# ifdef CONFIG_HEXO_IRQ
    msr     spsr,   #0x00
# else
    msr     spsr,   #0xd0
# endif

    movs    pc,     r1
FUNC_END(cpu_context_set_user)

#endif

