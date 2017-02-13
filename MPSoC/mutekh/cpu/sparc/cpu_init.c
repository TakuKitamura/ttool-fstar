/*
    This file is part of MutekH.

    MutekH is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MutekH is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MutekH; if not, write to the Free Software Foundation,
    Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

    Copyright (c) 2011 Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr>
    Copyright (c) 2011 Institut Telecom / Telecom ParisTech
*/

#include <mutek/mem_alloc.h>
#include <hexo/init.h>
#include <hexo/context.h>
#include <hexo/segment.h>
#include <hexo/cpu.h>
#include <hexo/local.h>
#include <hexo/interrupt.h>

#include <assert.h>

#ifdef CONFIG_ARCH_SMP
void * cpu_local_storage[CONFIG_CPU_MAXCOUNT];
#endif

cpu_cycle_t sparc_fake_tsc = 0;

/* CPU Local Descriptor structure */

error_t
cpu_global_init(void)
{
  return 0;
}

void cpu_init(void)
{
  assert(cpu_sparc_wincount() == CONFIG_CPU_SPARC_WINCOUNT);

#ifdef CONFIG_ARCH_SMP
  void			*cls;

  /* setup cpu local storage */
  cls = cpu_local_storage[cpu_id()];

  /* set cpu local storage register base pointer */
  asm volatile("mov %0 %%g6" : : "r" (cls));
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
  /* all these functions may execute with briefly invalid stack & frame
     pointer registers due to register window switch. */

  void cpu_context_jumpto_end();
  soclib_mem_bypass_sp_check(&cpu_context_jumpto, &cpu_context_jumpto_end);

  extern __ldscript_symbol_t __exception_base_ptr;
  extern __ldscript_symbol_t __exception_base_ptr_end;
  soclib_mem_bypass_sp_check(&__exception_base_ptr, &__exception_base_ptr_end);

  void sparc_excep_entry();
  void sparc_excep_entry_end();
  soclib_mem_bypass_sp_check(&sparc_excep_entry, &sparc_excep_entry_end);

  void sparc_except_restore();
  void sparc_except_restore_end();
  soclib_mem_bypass_sp_check(&sparc_except_restore, &sparc_except_restore_end);

# ifdef CONFIG_HEXO_IRQ
  void sparc_irq_entry();
  void sparc_irq_entry_end();
  soclib_mem_bypass_sp_check(&sparc_irq_entry, &sparc_irq_entry_end);
# endif

# ifdef CONFIG_HEXO_USERMODE
  void sparc_syscall_entry();
  void sparc_syscall_entry_end();
  soclib_mem_bypass_sp_check(&sparc_syscall_entry, &sparc_syscall_entry_end);

  void cpu_context_set_user();
  void cpu_context_set_user_end();
  soclib_mem_bypass_sp_check(&cpu_context_set_user, &cpu_context_set_user_end);
# endif
#endif
}

