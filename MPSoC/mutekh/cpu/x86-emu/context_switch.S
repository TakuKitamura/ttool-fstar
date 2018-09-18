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
        movl    __cpu_data_base, \r
        subl    $__cpu_data_start, \r
        addl    $__context_data_base, \r
#else
        leal    __context_data_base, \r
#endif
.endm

FUNC_START(.text, cpu_context_switch)

        GET_TLS_VAR %ecx

        // get context data base
        movl    (%ecx), %edx
        subl    $__context_data_start, %edx

        // save stack
        lea     4(%esp), %eax
        movl    %eax, (x86emu_context + CPU_X86EMU_CONTEXT_esp)(%edx)

        // eflags
        pushf
        popl    (x86emu_context + CPU_X86EMU_CONTEXT_EFLAGS)(%edx)

        // save pc
        movl    (%esp), %eax
        movl    %eax, (x86emu_context + CPU_X86EMU_CONTEXT_EIP)(%edx) // return address

        // save "callee saved" registers only
        .irp    reg, edi, esi, ebp, ebx
        movl    %\reg, (x86emu_context + CPU_X86EMU_CONTEXT_\reg)(%edx)
        .endr

        jmp     1f

FUNC_END(cpu_context_switch)




FUNC_START(.text, cpu_context_jumpto)

        GET_TLS_VAR %ecx

1:
        // get param
        movl    4(%esp), %edx

# ifdef CONFIG_ARCH_SMP
        // unlock some atomic value on context restore if requested
        movl    HEXO_CONTEXT_S_UNLOCK * 4(%edx), %eax
        test    %eax,   %eax
        jz      1f

        // unlock and clear unlock address
        movl    $0,     HEXO_CONTEXT_S_UNLOCK * 4(%edx)        
        movl    $0,     (%eax)        
1:      
# endif

        // get register save array
        movl    HEXO_CONTEXT_S_TLS * 4(%edx), %edx

        // switch context data base
        movl    %edx, (%ecx)
        subl    $__context_data_start, %edx

        // restore stack pointer
        mov     (x86emu_context + CPU_X86EMU_CONTEXT_esp)(%edx), %esp

        // prepare restoration
        pushl   (x86emu_context + CPU_X86EMU_CONTEXT_EIP)(%edx)
        pushl   (x86emu_context + CPU_X86EMU_CONTEXT_EFLAGS)(%edx)

        // restore gp regs
        .irp    reg, ebp, ebx, ecx, eax, edi, esi, edx
        mov     (x86emu_context + CPU_X86EMU_CONTEXT_\reg)(%edx), %\reg
        .endr

        popf
        ret

FUNC_END(cpu_context_jumpto)





FUNC_START(.text, cpu_context_set)

        movl    12(%esp), %eax

        // setup new stack
        movl    4(%esp), %ebx
        addl    8(%esp), %ebx
        movl    %ebx, %esp
        xorl	%ebp, %ebp

        // jump
        jmpl	*%eax

FUNC_END(cpu_context_set)





FUNC_START(.text, cpu_context_stack_use)

        // get params
        mov     4(%esp), %eax
        mov     8(%esp), %ecx
        mov     12(%esp), %edx

        // get register save array
        mov     HEXO_CONTEXT_S_TLS * 4(%eax), %eax
        subl    $__context_data_start, %eax

        // use context stack
        movl    (x86emu_context + CPU_X86EMU_CONTEXT_esp)(%eax), %esp

#ifndef CONFIG_RELEASE
        // prevent use of context local storage
        GET_TLS_VAR %esi
        movl    $0, (%esi)
#endif

        // jump to function
        pushl   %edx
        call    *%ecx

FUNC_END(cpu_context_stack_use)

