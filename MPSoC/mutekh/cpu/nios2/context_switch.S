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

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

/*
        Nios2 ABI

        * r1-r15 and r24-r31 are caller saved
        * r16-r21 are callee saved
        * r22-r23,r28 are either callee saved gp regs or special purpose

        * r0 is 0
        * r1 is asm temp
        * r2-r3 are ret value
        * r4-r7 are func args
        * r22 may be used as GOT ptr on GNU/Linux (caller saved)
        * r23 may be used as thread ptr on GNU/Linux (caller saved)
        * r24 is exception temp
        * r25 is break temporary
        * r26 is global pointer
        * r27 is stack pointer
        * r28 is frame pointer, or callee saved gp reg
        * r29 is exception return address
        * r30 is break return address (in reg bank0) or sstatus (in other banks)
        * r31 is return address

        Have a look to interrupt.S for interrupt context switch and preemption.
*/

FUNC_START(.text, cpu_context_switch)
        .set nobreak
        .set noat

        // get context local storage
        CPU_LOCAL ldw, __context_data_base, r1, r1
        // get context registers save array
        addi    r2,     r1,     nios2_context_regs

        // save "callee saved" registers
        // save gp ptr, stack ptr, frame ptr, return address
        .irp    r, 16, 17, 18, 19, 20, 21, 22, 23, 27, 28, 31
        stw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r2)
        .endr

        // resume directly at return address
        stw     r31,            CPU_NIOS2_CONTEXT_PC(r2)

	rdctl   r8,             status
        stw     r8,             CPU_NIOS2_CONTEXT_STATUS(r2)

        movui   r8,             CPU_NIOS2_CONTEXT_RESTORE_CALLEE
        stw     r8,             CPU_NIOS2_CONTEXT_SAVE_MASK(r2)

        br      cpu_context_jumpto
FUNC_END(cpu_context_switch)



        
FUNC_START(.text, cpu_context_jumpto)
        .set nobreak
        .set noat

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        movia   r1,             SOCLIB_MC_MAGIC_VAL
        stw     r1,             SOCLIB_MC_MAGIC(zero)
        
        /* switch to associated memchecker context */
        stw     r4,             SOCLIB_MC_CTX_SET(zero)
#endif

#ifdef CONFIG_ARCH_SMP
        // unlock some atomic value on context restore if requested
        ldw     r8,             HEXO_CONTEXT_S_UNLOCK * 4 (r4)
        beq     zero,   r8,     1f

        // unlock and clear unlock address
        stw     zero,           HEXO_CONTEXT_S_UNLOCK * 4 (r4)
        stw     zero,           (r8)
1:      
#endif

        // restore tls pointer from context struct
        ldw     r8,             HEXO_CONTEXT_S_TLS * 4 (r4)
        CPU_LOCAL stw,          __context_data_base, r8, r7

        addi    r2,     r8,     nios2_context_regs

        // restore stack ptr, frame ptr, return address
        .irp    r, 27, 28, 31
        ldw      r\r,           CPU_NIOS2_CONTEXT_GPR(\r)(r2)
        .endr

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        stw     zero,           SOCLIB_MC_MAGIC(zero)
#endif

        // prepare eret to retore status and pc
        ldw     r29,            CPU_NIOS2_CONTEXT_PC(r2)
        ldw     r1,             CPU_NIOS2_CONTEXT_STATUS(r2)
        wrctl   estatus,        r1

        ldw     r8,             CPU_NIOS2_CONTEXT_SAVE_MASK(r2)

#if 0   // callee shall almost always get restored (except on context start)
        andi    r7,     r8,     CPU_NIOS2_CONTEXT_RESTORE_CALLEE
        beq     r7,     zero,   1f
# endif
        
        // restore "callee saved" registers
        .irp    r, 16, 17, 18, 19, 20, 21, 22, 23
        ldw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r2)
        .endr
1:      
        andi    r7,     r8,     CPU_NIOS2_CONTEXT_RESTORE_CALLER
        beq     r7,     zero,   1f

        // restore "caller saved" register
        .irp    r, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 2
        ldw     r\r,            CPU_NIOS2_CONTEXT_GPR(\r)(r2)
        .endr
1:

        // return and restore status
        eret
FUNC_END(cpu_context_jumpto)

FUNC_START(.text, cpu_context_set)
        .set nobreak
        .set noat
        
#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        movia   r1,             SOCLIB_MC_MAGIC_VAL
        stw     r1,             SOCLIB_MC_MAGIC(zero)
        
        /* switch to associated memchecker context */
        stw     r4,             SOCLIB_MC_CTX_SET(zero)

        /* mark current memchecker context as invalid */
        movi    r1,             SOCLIB_MC_CTX_ID_CURRENT
        stw     r1,             SOCLIB_MC_CTX_INVALIDATE(zero)

        /* create a new temporary memchecker context using passed stack */
        stw     r4,             SOCLIB_MC_R1(zero)
        stw     r5,             SOCLIB_MC_R2(zero)
        stw     r4,             SOCLIB_MC_CTX_CREATE_TMP(zero)

        /* switch to new temporary memchecker context */
        stw     r4,             SOCLIB_MC_CTX_SET(zero)
#endif

        /* Set stack pointer */
        add     r4,     r4,     r5
        addi    sp,     r4,     -CONFIG_HEXO_STACK_ALIGN
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov     fp,     sp
#endif
        
#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        stw     zero,           SOCLIB_MC_MAGIC(zero)
#endif

        /* Jump */
        jmp     r6
        
FUNC_END(cpu_context_set)

FUNC_START(.text, cpu_context_stack_use)
        .set nobreak
        .set noat

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* enter memchecker command mode */
        movia   r1,             SOCLIB_MC_MAGIC_VAL
        stw     r1,             SOCLIB_MC_MAGIC(zero)
        
        /* switch to associated memchecker context */
        stw     r4,             SOCLIB_MC_CTX_SET(zero)
#endif
        
        // get tls pointer from context struct
        ldw     r8,             HEXO_CONTEXT_S_TLS * 4 (r4)
        // get context registers save array
        addi    r2,     r8,     nios2_context_regs

        ldw     sp,             CPU_NIOS2_CONTEXT_GPR(27)(r2)
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov     fp,     sp
#endif
        
#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        stw     zero,           SOCLIB_MC_MAGIC(zero)
#endif
        
#ifndef CONFIG_RELEASE
        // prevent use of previous context local storage
        CPU_LOCAL stw,   __context_data_base, zero, r1
#endif

        // private param
        mov     r4,             r6

        // call function
        jmp     r5
FUNC_END(cpu_context_stack_use)

#if defined(CONFIG_HEXO_USERMODE)

FUNC_START(.text, cpu_context_set_user)

        /* set stack */
        mov     sp,     r4

        /* set arg */
        mov     r4,     r6

        /* set user mode on eret */
        rdctl   r1,     status
        ori     r1,     r1,     2
        wrctl   estatus,        r1

        /* set pc */
        mov     ea,     r5

        eret

FUNC_END(cpu_context_set_user)

#endif

