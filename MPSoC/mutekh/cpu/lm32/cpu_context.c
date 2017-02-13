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

    Copyright Alexandre Becoulet <alexandre.becoulet@free.fr> (c) 2011
*/

#include <hexo/error.h>
#include <hexo/context.h>

CONTEXT_LOCAL struct cpu_context_s lm32_context_regs;
uintptr_t tls_addr;

error_t
cpu_context_bootstrap(struct context_s *context)
{
  /* set context local storage register base pointer */
  asm volatile ("mv r25, %0" :: "r" (context->tls));

  /* nothing is saved for this context */
  CONTEXT_LOCAL_ADDR(lm32_context_regs)->save_mask = 0;

  return 0;
}


/* context init function */

error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
  struct cpu_context_s *regs = CONTEXT_LOCAL_TLS_ADDR(context->tls, lm32_context_regs);

  regs->save_mask = CPU_LM32_CONTEXT_RESTORE_CALLER;
  regs->gpr[28] = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end)
               - CONFIG_HEXO_STACK_ALIGN;
#ifdef CONFIG_COMPILE_FRAMEPTR
  regs->gpr[27] = regs->gpr[28];
#endif
  regs->gpr[1] = (uintptr_t)param;
  regs->ei = 0;
  regs->pc = (uintptr_t)entry;

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
}

