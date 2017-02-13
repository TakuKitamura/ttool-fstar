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


#ifdef CONFIG_ARCH_DEVICE_TREE
void *arch_fdt;
#endif

void arch_init(uintptr_t init_sp);

static
void init_bss()
{
    extern __ldscript_symbol_t __bss_start;
    extern __ldscript_symbol_t __bss_end;

    memset(
        (uint8_t*)&__bss_start,
        0,
        (uint8_t*)&__bss_end-(uint8_t*)&__bss_start);
}


#if defined(CONFIG_CPU_RESET_HANDLER)

void boot_from_reset_vector()
{
#ifdef CONFIG_ARCH_DEVICE_TREE
    extern __ldscript_symbol_t dt_blob_start;
#endif
    extern __ldscript_symbol_t __initial_stack;
    uintptr_t sp = (uintptr_t)&__initial_stack
      - (1 << CONFIG_HEXO_RESET_STACK_SIZE);

    init_bss();
#ifdef CONFIG_ARCH_DEVICE_TREE
    arch_fdt = &dt_blob_start;
#endif

    arch_init(sp);
}


#endif /* CONFIG_CPU_RESET_HANDLER */
