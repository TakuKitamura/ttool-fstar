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
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

/*
    PowerPc system v ABI

    * r1, r14 - r31, f14 - f31 are callee saved
    * cr2, cr3, cr4 of the condition register are callee saved
    * r0, r3 - r12, f0 - f13, CTR, XER are caller saved
    * r0, r11, r12 may be altered by calls and may not reflect values from caller

    * r1 is the stack pointer
    * r2 is reserved for system use
    * r3 - r10 are params
    * r13 is the small data area pointer
    * VE, OE, UE, ZE, XE, NI, and RN (rounding mode) bits of the FPSCR are
      callee saved the rest of the FPSCR is caller saved.

    Have a look to interrupt.S for interrupt context switch and preemption.
*/


FUNC_START(.text, cpu_context_switch)

        // context local storage
        mfspr   2,              0x114
        // context registers save array
        addi    2,      2,      ppc_context_regs@l

        // save stack pointer
        stw     1,              CPU_PPC_CONTEXT_GPR(1)(2)
        // save callee saved gp registers r14 to r31
        stmw    14,             CPU_PPC_CONTEXT_GPR(14)(2)

        // save status register
        mfmsr   8
        stw     8,              CPU_PPC_CONTEXT_MSR(2)

        // write save/restore mask
        li      0,              CPU_PPC_CONTEXT_RESTORE_CALLEE
        stw     0,              CPU_PPC_CONTEXT_SAVE_MASK (2)

        // save condition register
        mfcr    0
        stw     0,              CPU_PPC_CONTEXT_CR(2)

        // save link register and pc
        mflr    0
        stw     0,              CPU_PPC_CONTEXT_LR(2)
        // resume directly at return address
        stw     0,              CPU_PPC_CONTEXT_PC(2)

#ifdef CONFIG_HEXO_FPU
        // test fpu enabled
        andi.   5,      8,      PPC_MSR_FPU_ENABLED
        beq     1f

# ifdef CONFIG_HEXO_LAZY_SWITCH
        // keep track of last context using fpu on this processor
        CPU_LOCAL       stw     ppc_lazy_last, 2, 10
# endif

        // save callee saved fpu registers 14 to 31
        SMD_FPU 2,      14,     31
        // save fpu status
        mffs    0
        stfd    0,              CPU_PPC_CONTEXT_FPSCR(2)
1:
#endif

        b       cpu_context_jumpto

FUNC_END(cpu_context_switch)





FUNC_START(.text, cpu_context_jumpto)

# ifdef CONFIG_ARCH_SMP
        // unlock some atomic value on context restore if requested
        lwz     2,              HEXO_CONTEXT_S_UNLOCK * 4 (3)
        cmpwi   2,      0
        beq     1f

        // unlock and clear unlock address
        addi    0,      0,      0
        stw     0,              HEXO_CONTEXT_S_UNLOCK * 4 (3)
        stw     0,              0(2)
1:      
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        // enter memchecker command mode
        LI32    0,              SOCLIB_MC_MAGIC_VAL
        stw     0,              SOCLIB_MC_MAGIC (0) 

        // switch to associated memchecker context
        stw     3,              SOCLIB_MC_CTX_SET (0)
#endif

        // restore tls pointer from context struct
        lwz     2,              HEXO_CONTEXT_S_TLS * 4 (3)
        mtspr   0x114,          2
        // context registers save array
        addi    2,      2,      ppc_context_regs@l

        // restore callee saved stack pointer
        lwz     1,              CPU_PPC_CONTEXT_GPR(1)(2)

#ifdef CONFIG_SOCLIB_MEMCHECK
        // leave memchecker command mode
        addi    0,      0,      0
        stw     0,              SOCLIB_MC_MAGIC (0)
#endif

        // restore condition register
        lwz     0,              CPU_PPC_CONTEXT_CR(2)
        mtcr    0

        // restore link register
        lwz     0,              CPU_PPC_CONTEXT_LR(2)
        mtlr    0

        // prepare pc and status register restore with rfi
        lwz     0,              CPU_PPC_CONTEXT_PC(2)
        mtsrr0  0
        lwz     8,              CPU_PPC_CONTEXT_MSR(2)
        mtsrr1  8

        // get restore mask
        lwz     4,              CPU_PPC_CONTEXT_SAVE_MASK (2)

#ifdef CONFIG_HEXO_FPU
        // check if next context use fpu
        andi.   5,      8,      PPC_MSR_FPU_ENABLED
        beq     1f

# ifdef CONFIG_HEXO_LAZY_SWITCH
        // do not need to restore if no other context used processor fpu since
        CPU_LOCAL       lwz     ppc_lazy_last, 9, 10
        sub.    10,     9,      2
        beq     1f
# endif

        // enable fpu to restore registers
        mfmsr   0
        ori     0,      0,      PPC_MSR_FPU_ENABLED
        mtmsr   0

        // restore callee saved fpu registers
        LMD_FPU 2,      14,     31
        lfd     0,              CPU_PPC_CONTEXT_FPSCR(2)
        mtfsf   0xff,           0

        andi.   5,      4,      CPU_PPC_CONTEXT_RESTORE_CALLER
        beq     1f

        // restore caller saved fpu registers
        LMD_FPU 2,      0,      13
        lwz     0,              CPU_PPC_CONTEXT_XER(2)
        mtxer   0
1:
#endif

#if 0   // callee should almost always get restored (except on context start)
        andi.   5,      4,      CPU_PPC_CONTEXT_RESTORE_CALLEE
        beq     1f
#endif

        // restore callee saved gp registers r14 to r31
        lmw     14,             CPU_PPC_CONTEXT_GPR(14)(2)

1:      

        andi.   5,      4,      CPU_PPC_CONTEXT_RESTORE_CALLER
        beq     1f

        // restore caller saved counter register
        lwz     0,              CPU_PPC_CONTEXT_CTR(2)
        mtctr   0

        // restore caller saved gp registers 3 - 13, 0, 2
        LMW_GP  2,      3,      13
        lwz     0,              CPU_PPC_CONTEXT_GPR( 0)(2)
        lwz     2,              CPU_PPC_CONTEXT_GPR( 2)(2)

1:      
        rfi

FUNC_END(cpu_context_jumpto)





FUNC_START(.text, cpu_context_set)

#ifdef CONFIG_SOCLIB_MEMCHECK
        // enter memchecker command mode
        LI32    0,              SOCLIB_MC_MAGIC_VAL
        stw     0,              SOCLIB_MC_MAGIC (0) 

        li      0,              SOCLIB_MC_CTX_ID_CURRENT
        stw     0,              SOCLIB_MC_CTX_INVALIDATE (0)

        // create a new temporary memchecker context using passed stack
        stw     3,              SOCLIB_MC_R1 (0)
        stw     4,              SOCLIB_MC_R2 (0)
        stw     3,              SOCLIB_MC_CTX_CREATE_TMP (0)

        // switch to new temporary memchecker context
        stw     3,              SOCLIB_MC_CTX_SET (0)
#endif

        // Set stack pointer, 64 bits aligned
        add     3,      3,      4
        addi    1,      3,      -CONFIG_HEXO_STACK_ALIGN

#ifdef CONFIG_SOCLIB_MEMCHECK
        // leave memchecker command mode
        addi    0,      0,      0
        stw     0,              SOCLIB_MC_MAGIC (0)
#endif

        mtctr   5
        bctr

FUNC_END(cpu_context_set)





FUNC_START(.text, cpu_context_stack_use)

#ifdef CONFIG_SOCLIB_MEMCHECK
        // enter memchecker command mode
        LI32    0,              SOCLIB_MC_MAGIC_VAL
        stw     0,              SOCLIB_MC_MAGIC (0) 

        stw     3,              SOCLIB_MC_CTX_SET (0)
#endif

        // get context local storage
        lwz     2,              HEXO_CONTEXT_S_TLS * 4(3)
        // context registers save array
        addi    2,      2,      ppc_context_regs@l

        // use context stack
        lwz     1,              CPU_PPC_CONTEXT_GPR(1)(2)
        // preserve ppc abi redzone
        addi    1,      1,      -224

#ifdef CONFIG_SOCLIB_MEMCHECK
        // leave memchecker command mode
        li      0,              0
        stw     0,              SOCLIB_MC_MAGIC (0)
#endif

#ifndef CONFIG_RELEASE
        // ensure we do not use context local storage from here
        li      0,              0
        mtspr   0x114,          0
#endif

        // private param
        or      3,      5,      5

        // call function
        mtctr   4
        bctr

FUNC_END(cpu_context_stack_use)


#ifdef CONFIG_HEXO_USERMODE

FUNC_START(.text, cpu_context_set_user)

        /* set stack */
        mr      1,      3

        /* set pc */
        mtsrr0  4
        
        /* set arg */
        mr      3,      5

        /* user mode */
        mfmsr   2
        ori     2,      2,      PPC_MSR_USERMODE
        mtsrr1  2

        rfi

FUNC_END(cpu_context_set_user)

#endif
