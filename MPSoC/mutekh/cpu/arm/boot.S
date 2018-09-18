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
    Copyright Nicolas Pouillon <nipo@ssji.net> (c) 2009-2010
*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

FUNC_START(.boot, cpu_boot)
        ldr     pc, =arm_boot
        ldr     pc, =arm_exc_undef
#ifdef CONFIG_HEXO_USERMODE
        ldr     pc, =arm_exc_swi
#else
        ldr     pc, =arm_exc_undef
#endif
        ldr     pc, =arm_exc_pabt
        ldr     pc, =arm_exc_dabt
        ldr     pc, =arm_exc_undef // reserved
#ifdef CONFIG_HEXO_IRQ
# if defined(CONFIG_CPU_ARM_CUSTOM_IRQ_HANDLER)
        ldr     pc, =arm_c_irq_handler
        ldr     pc, =arm_c_fiq_handler
# else
        ldr     pc, =arm_exc_irq
        ldr     pc, =arm_exc_fiq
# endif
#endif
FUNC_END(cpu_boot)


FUNC_START(.text, arm_boot)
        CPU_ID r4
        cmp    r4, # CONFIG_CPU_MAXCOUNT
        bge    cpu_boot_idle

        ldr  r13, =__initial_stack

#ifdef CONFIG_ARCH_SMP
        lsl  r5, r4, # CONFIG_HEXO_RESET_STACK_SIZE
        subs r13, r13, r5
#endif

# ifdef CONFIG_SOCLIB_MEMCHECK
	    mov  r0, #1<< CONFIG_HEXO_RESET_STACK_SIZE

	    /* enter memchecker command mode */
	    ldr  r2, = SOCLIB_MC_MAGIC_VAL
	    ldr  r1, = CONFIG_SOCLIB_MEMCHECK_ADDRESS
	    str  r2, [r1, # SOCLIB_MC_MAGIC_OFFSET ]

	    /* create a new initial memchecker context using cpuid as
	       context id */
	    str  r0, [r1, # SOCLIB_MC_R2_OFFSET ]
	    sub  r0, sp, r0
	    add  r0, r0, #4
	    str  r0, [r1, # SOCLIB_MC_R1_OFFSET ]
	    str  r4, [r1, # SOCLIB_MC_CTX_CREATE_OFFSET ]

	    /* switch to new memchecker context */
	    str  r4, [r1, # SOCLIB_MC_CTX_SET_OFFSET ]

	    /* enable all memchecker checks */
	    mov  r0, # SOCLIB_MC_CHECK_ALL
	    str  r0, [r1, # SOCLIB_MC_ENABLE_OFFSET ]
# endif

        /* adjust stack pointer, 64 bits aligned */
        bic  r13, r13, #63
#ifdef CONFIG_COMPILE_FRAMEPTR
        mov  r11, r13
#endif

# ifdef CONFIG_SOCLIB_MEMCHECK
	    /* leave memchecker command mode */
	    mov  r0, #0
	    str  r0, [r1, # SOCLIB_MC_MAGIC_OFFSET ]
# endif

        ldr  r12, =boot_from_reset_vector
        bx   r12
FUNC_END(arm_boot)

FUNC_START(.text, cpu_boot_idle)
        mov   r0, #0
        CPU_YIELD r0
        b     cpu_boot_idle
FUNC_END(cpu_boot_idle)

// Local Variables:
// tab-width: 4;
// c-basic-offset: 4;
// indent-tabs-mode: nil;
// End:
//
// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4
