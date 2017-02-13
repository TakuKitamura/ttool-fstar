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

*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

FUNC_START(.text, cpu_boot)

        /* get CPU id and adjust stack */
        lis     9,              __initial_stack@ha
        la      1,              __initial_stack@l(9)
        mfdcr   29,             0

        /* clear status */
        li      3,      0
        mtmsr   3

#ifdef CONFIG_ARCH_SMP_CAPABLE
        /* only first CPU is allowed to boot */
        cmpwi   cr0,    29,     CONFIG_CPU_MAXCOUNT
        bge     cr0,            cpu_boot_idle
#endif

#ifdef CONFIG_ARCH_SMP
        /* adjust initial stack from cpuid */
        slwi    3,      29,     CONFIG_HEXO_RESET_STACK_SIZE
        sub     1,      1,      3
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        addi    2,      0,      1 << CONFIG_HEXO_RESET_STACK_SIZE

        /* enter memchecker command mode */
        LI32    0,              SOCLIB_MC_MAGIC_VAL
        stw     0,              SOCLIB_MC_MAGIC (0)

        /* create a new initial memchecker context using cpuid as context id */
        stw     2,              SOCLIB_MC_R2 (0)
        subf    2,      2,      1
        stw     2,              SOCLIB_MC_R1 (0)
        stw     29,             SOCLIB_MC_CTX_CREATE (0)

        /* switch to new memchecker context */
        stw     29,             SOCLIB_MC_CTX_SET (0)

        /* enable all memchecker checks */
        addi    0,      0,      SOCLIB_MC_CHECK_ALL
        stw     0,              SOCLIB_MC_ENABLE (0)
#endif

        /* adjust stack pointer */
        addi    1,      1,      -CONFIG_HEXO_STACK_ALIGN

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
	li	3,		0
        stw     3,              SOCLIB_MC_MAGIC (0)
#endif

        /* set cpu interrupts entry point */
        LA32    2,              __exception_base_ptr
        mtevpr  2

        /* jump to architecture code */
        LA32    2,              boot_from_reset_vector
        mtctr   2
        bctr
FUNC_END(cpu_boot)


FUNC_START(.text, cpu_boot_idle)
#ifdef CONFIG_CPU_PPC_WAIT_OPCODE
        wait
#endif
        b               cpu_boot_idle
FUNC_END(cpu_boot_idle)


FUNC_START(.boot, cpu_boot_pointer)
        1:
        LA32	3,	cpu_boot
        mtctr   3
        bctr
        .org 0x80-4
        b      	1b
FUNC_END(cpu_boot_pointer)
