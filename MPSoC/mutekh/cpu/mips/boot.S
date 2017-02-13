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

   Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006

*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

FUNC_START(.boot, cpu_boot)

        .set push
        .set noreorder

        /* set up IT disable and kernel mode */
        li      $8,             CPU_MIPS_STATUS_IM
        MTC0_   $8,             CPU_MIPS_STATUS
        MTC0_   $0,             CPU_MIPS_CAUSE

        /* get CPU id and stack end */
        CPU_ID  $9
        la      $sp,            __initial_stack

#ifdef CONFIG_ARCH_SMP_CAPABLE
        /* spin if cpuid >= CONFIG_CPU_MAXCOUNT */
        slt     $8,     $9,     CONFIG_CPU_MAXCOUNT
        beq     $0,     $8,     cpu_boot_idle
#endif

#ifdef CONFIG_ARCH_SMP
        /* adjust initial stack from cpuid */
        sll     $8,     $9,     CONFIG_HEXO_RESET_STACK_SIZE
        subu    $sp,    $sp,    $8
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        .set push
        .set noat
        addiu   $8,     $0,     1 << CONFIG_HEXO_RESET_STACK_SIZE

        /* enter memchecker command mode */
        li      $1,             SOCLIB_MC_MAGIC_VAL
        sw      $1,             SOCLIB_MC_MAGIC ($0)

        /* create a new initial memchecker context using cpuid as context id */
        sw      $8,             SOCLIB_MC_R2 ($0)
        subu    $8,             $sp,    $8
        sw      $8,             SOCLIB_MC_R1 ($0)
        sw      $9,             SOCLIB_MC_CTX_CREATE ($0)

        /* switch to new memchecker context */
        sw      $9,             SOCLIB_MC_CTX_SET ($0)

        /* enable all memchecker checks */
        li      $1,             SOCLIB_MC_CHECK_ALL
        sw      $1,             SOCLIB_MC_ENABLE ($0)
#endif

        /* adjust stack pointer, 64 bits aligned, mips abi requires 4 free words */
        addiu   $sp,    $sp,    -16
#ifdef CONFIG_COMPILE_FRAMEPTR
        move    $fp,            $sp
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* leave memchecker command mode */
        sw      $0,             SOCLIB_MC_MAGIC ($0)
        .set pop
#endif

#if CONFIG_CPU_MIPS_VERSION >= 32
        /* set cpu interrupts entry point */
        la      $4,             __exception_base_ptr
        mtc0    $4,     $15,    1
#endif

        /* jump to architecture code */
        la      $8,             boot_from_reset_vector
        j       $8
        nop

        .set pop
FUNC_END(cpu_boot)


FUNC_START(.boot, cpu_boot_idle)
#ifdef CONFIG_CPU_WAIT_IRQ
        wait
#endif
        b               cpu_boot_idle
FUNC_END(cpu_boot_idle)
