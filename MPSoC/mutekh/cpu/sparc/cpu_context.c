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

#include <hexo/error.h>
#include <hexo/context.h>
#include <hexo/local.h>

#ifdef CONFIG_HEXO_USERMODE
CPU_LOCAL void *__context_data_base;
#endif

__attribute__((aligned(8)))
CONTEXT_LOCAL struct cpu_context_s sparc_context_regs;

#ifdef CONFIG_HEXO_LAZY_SWITCH
/* last fpu restored context */
CPU_LOCAL struct cpu_context_s *sparc_lazy_last = 0;
#endif

error_t
cpu_context_bootstrap(struct context_s *context)
{
  /* set context local storage register base pointer */
  asm volatile("mov %0, %%g7" : : "r" (context->tls));
#ifdef CONFIG_HEXO_USERMODE
  CPU_LOCAL_SET(__context_data_base, context->tls);
#endif

  /* nothing is saved for this context */
  CONTEXT_LOCAL_ADDR(sparc_context_regs)->save_mask = 0;

  return 0;
}


/* context init function */

error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
  struct cpu_context_s *regs = CONTEXT_LOCAL_TLS_ADDR(context->tls, sparc_context_regs);

  regs->save_mask = CPU_SPARC_CONTEXT_RESTORE_CALLER;
  regs->g[7] = (uintptr_t)context->tls;
  regs->o[6] = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end) - CONFIG_HEXO_STACK_ALIGN;
#ifdef CONFIG_COMPILE_FRAMEPTR
  regs->i[7] = regs->o[6];
#endif
  regs->o[0] = (uintptr_t)param;
  regs->psr = SPARC_PSR_PREV_SUSER_MODE | SPARC_PSR_SUSER_MODE | SPARC_PSR_PIL_MASK | SPARC_PSR_TRAP_ENABLED;
  regs->pc = (uintptr_t)entry;
  regs->npc = regs->pc + 4;

  return 0;
}

void
cpu_context_destroy(struct context_s *context)
{
#if 0
  reg_t		*stack = (reg_t*)context->stack_ptr;
#endif
}

void cpu_exception_resume_pc(struct cpu_context_s *regs, uintptr_t pc)
{
  regs->pc = pc;
  regs->npc = pc + 4;
}

