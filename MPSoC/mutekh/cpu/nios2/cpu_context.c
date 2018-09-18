/*
 *   This file is part of MutekH.
 *   
 *   MutekH is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; version 2.1 of the
 *   License.
 *   
 *   MutekH is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *   
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with MutekH; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *   02110-1301 USA.
 *
 *   Copyright Francois Charot <charot@irisa.fr>  (c) 2008
 *   INRIA Rennes Bretagne Atlantique
 *
 */


#include <stdlib.h>
#include <hexo/error.h>
#include <hexo/context.h>
#include <hexo/interrupt.h>

CONTEXT_LOCAL struct cpu_context_s nios2_context_regs;

error_t
cpu_context_bootstrap(struct context_s *context)
{
  /* set context local storage base pointer */
  CPU_LOCAL_SET(__context_data_base, context->tls);

  /* nothing is saved for this context */
  CONTEXT_LOCAL_ADDR(nios2_context_regs)->save_mask = 0;

  return 0;
}

  /* Nios2 ABI requires 4 free words in the stack. */

error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
  struct cpu_context_s *regs = CONTEXT_LOCAL_TLS_ADDR(context->tls, nios2_context_regs);

  regs->save_mask = CPU_NIOS2_CONTEXT_RESTORE_CALLER; /* for r4 */
  regs->gpr[CPU_NIOS2_SP] = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end)
                         - CONFIG_HEXO_STACK_ALIGN;
#ifdef CONFIG_COMPILE_FRAMEPTR
  regs->gpr[CPU_NIOS2_FP] = regs->gpr[CPU_NIOS2_SP];
#endif
  regs->gpr[4] = (uintptr_t)param;

  regs->status = 0;

  regs->gpr[CPU_NIOS2_RA] = 0xa5a5a5a5; /* can not return from context entry */
  regs->pc = (uintptr_t)entry;

  return 0;
}

void
cpu_context_destroy(struct context_s *context)
{
}

void cpu_exception_resume_pc(struct cpu_context_s *regs, uintptr_t pc)
{
  regs->pc = pc;
}

