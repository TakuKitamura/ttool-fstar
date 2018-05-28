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
  02110-1301 USA

  Copyright Nicolas Pouillon, <nipo@ssji.net>, 2010
*/

#include <hexo/types.h>
#include <hexo/cpu.h>
#include <hexo/init.h>
#include <hexo/atomic.h>
#include <hexo/interrupt.h>
#include <string.h>

#include <arch/mem_checker.h>


#ifdef CONFIG_ARCH_DEVICE_TREE
void *arch_fdt;
#endif

void arch_init(uintptr_t init_sp);

static
void init_bss()
{
    extern __ldscript_symbol_t __bss_start;
    extern __ldscript_symbol_t __bss_end;

    soclib_mem_check_region_status(
        (uint8_t*)&__bss_start,
        (uint8_t*)&__bss_end-(uint8_t*)&__bss_start,
        SOCLIB_MC_REGION_GLOBAL);
    memset(
        (uint8_t*)&__bss_start,
        0,
        (uint8_t*)&__bss_end-(uint8_t*)&__bss_start);
}

#if defined(CONFIG_ARCH_SOCLIB_BOOTLOADER_MUTEKH)
void boot_from_bootloader(void *device_tree)
{
    // We can't expect anything from bootlaoder :'(
    cpu_interrupt_disable();

#ifdef CONFIG_ARCH_DEVICE_TREE
    arch_fdt = device_tree;
#endif
    arch_init(cpu_get_stackptr());
}
#endif

#if defined(CONFIG_CPU_RESET_HANDLER)

#define N (sizeof(atomic_t) * 8)
#define START_BARRIER_WORDS ((CONFIG_CPU_MAXCOUNT + N - 1) / N)
atomic_t start_barrier[START_BARRIER_WORDS];

static void start_barrier_wait()
{
    size_t bit = cpu_id() & (N - 1);
    atomic_t *bar = &start_barrier[cpu_id() / N];

    atomic_bit_clr(bar, bit);
    while ( atomic_bit_test(bar, bit) == 0 )
        order_compiler_mem();
}

void start_barrier_release(cpu_id_t cpu)
{
    size_t bit = cpu & (N - 1);
    atomic_t *bar = &start_barrier[cpu / N];

    atomic_bit_set(bar, bit);
}

void boot_from_reset_vector()
{
#ifdef CONFIG_ARCH_DEVICE_TREE
    extern __ldscript_symbol_t dt_blob_start;
#endif
    extern __ldscript_symbol_t __initial_stack;
    uintptr_t sp = (uintptr_t)&__initial_stack
        - (1 << CONFIG_HEXO_RESET_STACK_SIZE) * cpu_id();

    if ( cpu_isbootstrap() ) {
        init_bss();
#ifdef CONFIG_ARCH_DEVICE_TREE
        arch_fdt = &dt_blob_start;
#endif
    } else {
        soclib_mem_mark_initialized(start_barrier, START_BARRIER_WORDS);
        start_barrier_wait(start_barrier);
    }

    arch_init(sp);
}

#endif /* CONFIG_CPU_RESET_HANDLER */
