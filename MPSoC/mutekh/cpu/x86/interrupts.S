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

#include <cpu/hexo/pmode.h>
#include <cpu/hexo/msr.h>
#include <hexo/asm.h>
#include <hexo/context.h>
#include <hexo/local.h>
#include <hexo/interrupt.h>

/*
  System call convention is left to the high level C syscall
  handler with some restrictions:

  When using "int" instruction:
  * Only eax register is restored from regtable for use as return value.
  * All general purpose registers may be clobbered

  When using the "sysenter" instruction:

  * The %ecx register must contain user stack pointer to restore
  * The %edx register must contain user return address
*/

.equ INT_STACK_edi,     0
.equ INT_STACK_esi,     4
.equ INT_STACK_ebp,     8
.equ INT_STACK_ebx,     16
.equ INT_STACK_edx,     20
.equ INT_STACK_ecx,     24
.equ INT_STACK_eax,     28
.equ INT_STACK_ERR,     32
.equ INT_STACK_EIP,     36
.equ INT_STACK_CS,      40
.equ INT_STACK_EFLAGS,  44
.equ INT_STACK_esp,     48
.equ INT_STACK_SS,      52

/** entry point templates */

.macro PUT_ENTRY_ n
        // save all 8 gp registers on stack
        pusha
        // keep pointer to reg table on stack in %ebp
        mov     %esp,   %ebp
        // keep entry id in %edi
        mov	$\n, %edi
        // goto common handling code
        jmp	3f
.endm

.macro PUT_ENTRY n
        .align CPU_INTERRUPT_ENTRY_ALIGN
        // push a fake error code in this case
        push            $0
        PUT_ENTRY_      \n
.endm

.macro PUT_ENTRY_ERR n
        .align CPU_INTERRUPT_ENTRY_ALIGN
        PUT_ENTRY_      \n
.endm

.macro PUT_ENTRIES i j
        PUT_ENTRY \i
    .if \i+1<=\j
        PUT_ENTRY (\i+1)
    .endif
    .if \i+2<=\j
        PUT_ENTRY (\i+2)
    .endif
    .if \i+3<=\j
        PUT_ENTRY (\i+3)
    .endif
    .if \i<\j
        PUT_ENTRIES (\i+4), \j
    .endif
.endm

#if CPU_EXCEPT_VECTOR_COUNT + CPU_HWINT_VECTOR_COUNT + CPU_SYSCALL_VECTOR_COUNT > 256
# error Too many interrupts entry points (max 256)
#endif

# ifdef CONFIG_HEXO_FPU
        mov     %cr0, %ecx
#  ifdef CONFIG_HEXO_LAZY_SWITCH
        // set fpu lazy task switch flag
        or      %ecx, 0x8
        mov     %ecx, %cr0
#  else
        // save multimedia state if enabled for this context
        test    %ecx, 0x8
        jnz     2f
        fxsave  TLS_SEG(:) CPU_X86_CONTEXT_MM(x86_context)
2:
#  endif
# endif


.macro X86_STACK_TO_CTX offset
        // copy stack saved registers in context save area
        .irp    reg, edi, esi, ebp, ebx, edx, ecx, eax, EIP
        mov     INT_STACK_\reg(%ebp), %ecx
        mov     %ecx, (x86_context + CPU_X86_CONTEXT_\reg)\offset
        .endr
.endm

/******************************* common *******************************/

.section .text,"ax",@progbits


#ifdef CONFIG_HEXO_USERMODE

/* restore kernel mode segment registers */
.macro ADJUST_TLS_CLS_SEGS

        // restore kernel mode data segments
        movl	$CPU_X86_SEGSEL(ARCH_GDT_DATA_INDEX, CPU_X86_KERNEL),  %edx
        movl	%edx,   %ds
        movl	%edx,   %es

# ifdef CONFIG_ARCH_SMP
        // get cpu id from local APIC
        mov	$IA32_APIC_BASE_MSR, %ecx
        rdmsr
        and	$0xfffff000, %eax
        mov	0x20(%eax), %eax
        shr	$24, %eax

        // restore cls segment
        movl	cpu_local_storage_seg(,%eax,2), %eax
        shl	$3, %eax
        movl	%eax, CLS_SEG()
# endif

        // restore tls segment
        movl	CLS_SEG(:) (cpu_tls_seg), %eax
        movl	%eax, TLS_SEG()

.endm

#endif



.macro X86_PREEMPT_CLEAR
#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        movl    $0, CLS_SEG(:) (cpu_preempt_handler)
#endif
.endm

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
.macro X86_PREEMPT_IRQ
        // call preempt handler
        pushl   CLS_SEG(:) (cpu_preempt_param)
        call    *%edx
        add     $4, %esp

        // got pointer to context to switch to ?
        test    %eax, %eax
        jz      10f

        pushl   %eax
# ifdef CONFIG_HEXO_CONTEXT_STATS
        call    context_preempt_stats
# endif

        // switch to returned context
        call    cpu_context_jumpto
10:
.endm

.macro X86_PREEMPT

        // use cpu local stack from now
        movl    $(cpu_interrupt_stack + CONFIG_HEXO_INTERRUPT_STACK_SIZE), %edi
# ifdef CONFIG_ARCH_SMP
        addl    CLS_SEG(:) (__cpu_data_base), %edi
# endif
        xchg    %esp, %edi

        X86_PREEMPT_IRQ
        
        // no context switch needed, back to context stack
        mov     %edi, %esp
.endm
#endif

.macro X86_INTERRUPT_END

.endm

#ifdef CONFIG_HEXO_USERMODE
# define CTX_REGS_BASE %esi,%ebx
#else
# define CTX_REGS_BASE %esi
#endif


/******************************* fault *******************************/

#if CPU_EXCEPT_VECTOR_COUNT != 32
# error x86 exceptions entry points count must be 32
#endif

        .globl x86_interrupt_ex_entry
        .func x86_interrupt_ex_entry
        .align CPU_INTERRUPT_ENTRY_ALIGN
x86_interrupt_ex_entry:
        PUT_ENTRIES     0x00 0x07
        PUT_ENTRY_ERR	0x08
        PUT_ENTRY	0x09
        PUT_ENTRY_ERR	0x0a
        PUT_ENTRY_ERR	0x0b
        PUT_ENTRY_ERR	0x0c
        PUT_ENTRY_ERR	0x0d
        PUT_ENTRY_ERR	0x0e
        PUT_ENTRY	0x0f
        PUT_ENTRY	0x10
        PUT_ENTRY_ERR	0x11
        PUT_ENTRIES     0x12 0x1f

3:

        /* We are running on cpu local interrupt stack if we come from
	   user mode. We are already running on kernel context stack if not. */
        
#ifdef CONFIG_HEXO_USERMODE
        // test if we come from user mode
        testl	$CPU_X86_USER, INT_STACK_CS(%ebp)
        je	1f

        ADJUST_TLS_CLS_SEGS

        // switch to context kernel stack
        movl    TLS_SEG(:) (context_stack_end), %esp

        // will save in user regs bank
        mov     $CPU_X86_CONTEXT_USER_SHIFT, %ebx
        // get esp from stack
        mov     INT_STACK_esp(%ebp), %eax

        // get context local handler
        mov     TLS_SEG(:) (cpu_user_exception_handler), %edx
        test    %edx, %edx
        jnz     2f
        jmp     3f
1:
        // will save in kernel regs bank
        xorl    %ebx, %ebx
#endif
        // get kernel esp from stack pointer on entry
        lea     INT_STACK_esp(%ebp), %eax

3:
        // default cpu local fault handler
        mov     CLS_SEG(:) (cpu_exception_handler), %edx
2:

        movl    TLS_SEG(:) (__context_data_base), %esi
#ifdef CONFIG_HEXO_USERMODE
        addl    %ebx, %esi
#endif

        // save registers
        X86_STACK_TO_CTX (%esi)
        mov     %eax, (x86_context + CPU_X86_CONTEXT_esp)(%esi)

        X86_PREEMPT_CLEAR

        // context switch can happend in exception handler
        pushl   TLS_SEG(:) (x86_context + CPU_X86_CONTEXT_mask)
        movl    $0, TLS_SEG(:) (x86_context + CPU_X86_CONTEXT_mask)
        
        // prepare fault handler arguments
        pushl	%eax            	/* stackptr */
        
        leal    (x86_context)(%esi), %ecx
        pushl	%ecx			/* regtable */
        
        movl	%cr2,   %ecx
        pushl	%ecx			/* dataptr */
        
        movl    (x86_context + CPU_X86_CONTEXT_EIP)(%esi), %ecx
        pushl	%ecx                    /* execptr */
        
        pushl	%edi			/* type */

        // call handler
        call	*%edx
        addl    $4*5,   %esp

        popl    TLS_SEG(:) (x86_context + CPU_X86_CONTEXT_mask)

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // check if a preempt handler has been registered
        mov     CLS_SEG(:) (cpu_preempt_handler), %edx
        test    %edx, %edx
        jz      1f

        X86_PREEMPT
#endif
1:
#ifdef CONFIG_HEXO_USERMODE
        test    %ebx, %ebx
        jnz     cpu_context_restore_user
#endif
        jmp     cpu_context_restore_kernel

FUNC_END(x86_interrupt_ex_entry_)



/******************************* irq *******************************/

#ifdef CONFIG_HEXO_IRQ

# if CPU_HWINT_VECTOR_COUNT <= 0
#  error x86 irq entry points count is zero
# endif

        .globl x86_interrupt_hw_entry
        .func x86_interrupt_hw_entry
        .align CPU_INTERRUPT_ENTRY_ALIGN
x86_interrupt_hw_entry:
        PUT_ENTRIES     0, (CPU_HWINT_VECTOR_COUNT-1)

3:

        /* We are running on cpu local interrupt stack if we come from
	   user mode. We are running on kernel context stack otherwise. */
        
# ifdef CONFIG_HEXO_USERMODE
        // test if we come from user mode
        testl	$CPU_X86_USER, INT_STACK_CS(%ebp)
        je	1f

        ADJUST_TLS_CLS_SEGS

        // will save in user regs bank
        movl    $CPU_X86_CONTEXT_USER_SHIFT, %ebx
        // get esp from stack
        mov     INT_STACK_esp(%ebp), %esi
        
#  ifndef CONFIG_HEXO_INTERRUPT_STACK
        // switch to context kernel stack
        movl    TLS_SEG(:) (context_stack_end), %esp
#  else
        jmp     2f
#  endif
1:
        // will save in kernel regs bank
        xorl    %ebx, %ebx
# endif
        // get kernel esp from stack pointer on entry
        lea     INT_STACK_esp(%ebp), %esi

# ifdef CONFIG_HEXO_INTERRUPT_STACK
        // switch to interrupt stack
        movl    CLS_SEG(:) (__cpu_data_base), %eax
        lea     (cpu_interrupt_stack + CONFIG_HEXO_INTERRUPT_STACK_SIZE)(%eax), %esp
# endif
2:
        
        X86_PREEMPT_CLEAR

        pushl	%edi            // arg is irq entry number

        call	CLS_SEG(:) *(cpu_interrupt_handler)
        addl	$4, %esp

#ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // check if a preempt handler has been registered
        mov     CLS_SEG(:) (cpu_preempt_handler), %edx
        test    %edx, %edx
        jz      2f
 
        mov     %esi, %eax
        
        movl    TLS_SEG(:) (__context_data_base), %esi
# ifdef CONFIG_HEXO_USERMODE
        addl    %ebx, %esi
# endif

        // save registers
        X86_STACK_TO_CTX (%esi)
        mov     %eax, (x86_context + CPU_X86_CONTEXT_esp)(%esi)

        X86_PREEMPT_IRQ

# ifdef CONFIG_HEXO_USERMODE
        test    %ebx, %ebx
        jnz     cpu_context_restore_user
# endif
        jmp     cpu_context_restore_kernel

#endif

2:      
#ifdef CONFIG_HEXO_USERMODE
        /* return to kernel code ? */
        test    %ebx, %ebx
        jz	1f

        /* restore user data segments */
        mov	$CPU_X86_SEGSEL(ARCH_GDT_USER_DATA_INDEX, CPU_X86_KERNEL), %eax
        mov	%eax, %ds
        mov	%eax, %es

# ifndef CONFIG_RELEASE
        // invalidate cls and tls segments
        xorl    %eax, %eax
        movl    %eax, %gs
        movl    %eax, %fs
# endif
1:
#endif
        // restore entry point stack
        mov     %ebp,   %esp

        // restore registers and return
        popa
        add	$4, %esp	; /* error code */
        iret
FUNC_END(x86_interrupt_hw_entry_)

#endif



/******************************* syscall *******************************/

#ifdef CONFIG_HEXO_USERMODE

# if CPU_SYSCALL_VECTOR_COUNT <= 0
#  error x86 syscall entry points count is zero
# endif

        .globl x86_interrupt_sys_entry
        .func x86_interrupt_sys_entry
        .align CPU_INTERRUPT_ENTRY_ALIGN
x86_interrupt_sys_entry:
        PUT_ENTRIES     0, (CPU_SYSCALL_VECTOR_COUNT-1)

# ifdef CONFIG_CPU_X86_SYSENTER
        /* special entry point used by sysenter instructions, use same
        stack layout as other entry points */
        .globl x86_interrupt_sys_enter
x86_interrupt_sys_enter:
        pushl   $CPU_X86_SEGSEL(ARCH_GDT_USER_DATA_INDEX, CPU_X86_USER)
        pushl	%ecx		/* stack ptr, passed by user */
        pushf                   /* eflags */

        pushl   $CPU_X86_SEGSEL(ARCH_GDT_USER_CODE_INDEX, CPU_X86_USER)
        pushl	%edx		/* return address, passed by user */

        pushl	$0		/* dummy error code */

        pusha                   /* push all gp registers */

        mov     %esp,   %ebp
        mov	$0,     %edi    /* claim first syscall entry */
# endif

3:

        /* We are running on cpu local interrupt stack if we come from
	   user mode. We are running on kernel context stack otherwise. */

        ADJUST_TLS_CLS_SEGS

        // switch to context kernel stack
        movl    TLS_SEG(:) (context_stack_end), %esp

        movl    TLS_SEG(:) (__context_data_base), %esi
        addl    $CPU_X86_CONTEXT_USER_SHIFT, %esi

        // save registers
        mov     INT_STACK_esp(%ebp), %eax
        X86_STACK_TO_CTX (%esi)
        mov     %eax, (x86_context + CPU_X86_CONTEXT_esp)(%esi)

        X86_PREEMPT_CLEAR

        // context switch can happend in syscall
        pushl   TLS_SEG(:) (x86_context + CPU_X86_CONTEXT_mask)
        movl    $0, TLS_SEG(:) (x86_context + CPU_X86_CONTEXT_mask)

        // prepare handler arguments
        leal    (x86_context)(%esi), %ecx
        pushl	%ecx				; /* regtable */
        
        pushl	%edi				; /* number */

        // call syscall handler
        call    TLS_SEG(:) *(cpu_syscall_handler)
        addl	$4*2, %esp

        popl    TLS_SEG(:) (x86_context + CPU_X86_CONTEXT_mask)

# ifdef CONFIG_HEXO_CONTEXT_PREEMPT
        // check if a preempt handler has been registered
        mov     CLS_SEG(:) (cpu_preempt_handler), %edx
        test    %edx, %edx
        jz      1f

        X86_PREEMPT
1:      
# endif

# ifndef CONFIG_CPU_X86_SYSENTER
        jmp     cpu_context_restore_user
# else

        // restore user data segments
        mov	$CPU_X86_SEGSEL(ARCH_GDT_USER_DATA_INDEX, CPU_X86_KERNEL), %eax
        mov	%eax, %ds
        mov	%eax, %es

#  ifndef CONFIG_RELEASE
        xorl    %eax, %eax
        movl    %eax, %gs
        movl    %eax, %fs
#  endif

        // prepare sysexit
        movl    (x86_context + CPU_X86_CONTEXT_EIP)(%esi), %edx
        movl    (x86_context + CPU_X86_CONTEXT_esp)(%esi), %ecx

        // restore "callee saved" gp regs & eax for return value
        .irp    reg, ebp, ebx, eax, edi, esi
        mov     (x86_context + CPU_X86_CONTEXT_\reg)(%esi), %\reg
        .endr

        sti
        sysexit
# endif

FUNC_END(x86_interrupt_sys_entry_)



#endif
