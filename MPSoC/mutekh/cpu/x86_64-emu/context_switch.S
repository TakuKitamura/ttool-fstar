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

.macro GET_TLS_VAR r
#ifdef CONFIG_ARCH_SMP
        movq    __cpu_data_base, \r
        subq    $__cpu_data_start, \r
        addq    $__context_data_base, \r
#else
        leaq    __context_data_base, \r
#endif
.endm

FUNC_START(.text, cpu_context_switch)

        GET_TLS_VAR %rcx

        // get context data base
        movq    (%rcx), %rdx
        subq    $__context_data_start, %rdx

        // save stack
        leaq    8(%rsp), %rax
        movq    %rax, (x86emu_context + CPU_X86EMU_CONTEXT_rsp)(%rdx)

        // eflags
        pushf
        popq    (x86emu_context + CPU_X86EMU_CONTEXT_RFLAGS)(%rdx)

        // save pc
        movq    (%rsp), %rax
        movq    %rax, (x86emu_context + CPU_X86EMU_CONTEXT_RIP)(%rdx) // return address

        // save "callee saved" registers only
        .irp    reg, rbp, rbx, r12, r13, r14, r15
        mov     %\reg, (x86emu_context + CPU_X86EMU_CONTEXT_\reg)(%rdx)
        .endr

        jmp     1f

FUNC_END(cpu_context_switch)




FUNC_START(.text, cpu_context_jumpto)

        GET_TLS_VAR %rcx

1:

# ifdef CONFIG_ARCH_SMP
        // unlock some atomic value on context restore if requested
        movq    HEXO_CONTEXT_S_UNLOCK * 8(%rdi), %rax
        test    %rax,   %rax
        jz      1f

        // unlock and clear unlock address
        movq    $0,     HEXO_CONTEXT_S_UNLOCK * 8(%rdi)
        movl    $0,     (%rax)
1:      
# endif

        // get register save array
        movq    HEXO_CONTEXT_S_TLS * 8(%rdi), %rdx

        // switch context data base
        movq    %rdx, (%rcx)
        subq    $__context_data_start, %rdx

        // restore stack pointer
        mov     (x86emu_context + CPU_X86EMU_CONTEXT_rsp)(%rdx), %rsp

        // prepare restoration
        pushq   (x86emu_context + CPU_X86EMU_CONTEXT_RIP)(%rdx)
        pushq   (x86emu_context + CPU_X86EMU_CONTEXT_RFLAGS)(%rdx)

        // restore gp regs
        .irp    reg, rbp, rbx, rcx, rax, rdi, rsi, r8, r9, r10, r11, r12, r13, r14, r15, rdx
        mov     (x86emu_context + CPU_X86EMU_CONTEXT_\reg)(%rdx), %\reg
        .endr

        popf
        retq

FUNC_END(cpu_context_jumpto)





FUNC_START(.text, cpu_context_set)

        // setup new stack
        leaq    (%rsi, %rdx), %rsp
        xorq	%rbp, %rbp

        // jump
        jmp	*%rdi

FUNC_END(cpu_context_set)





FUNC_START(.text, cpu_context_stack_use)

        // get register save array
        movq    HEXO_CONTEXT_S_TLS * 8(%rdi), %rax
        subq    $__context_data_start, %rax

        // use context stack
        movq    (x86emu_context + CPU_X86EMU_CONTEXT_rsp)(%rax), %rsp

#ifndef CONFIG_RELEASE
        // prevent use of context local storage
        GET_TLS_VAR %rax
        movq    $0, (%rax)
#endif

        // jump to function
        movq    %rdx, %rdi
        call    *%rsi

FUNC_END(cpu_context_stack_use)

