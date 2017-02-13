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

#include <stdlib.h>
#include <hexo/error.h>
#include <hexo/context.h>

CONTEXT_LOCAL struct cpu_context_s x86emu_context;

error_t
cpu_context_bootstrap(struct context_s *context)
{
  /* set context local storage base pointer */
  CPU_LOCAL_SET(__context_data_base, context->tls);

  struct cpu_context_s *cctx = CONTEXT_LOCAL_ADDR(x86emu_context);

  cctx->mask = 0;

  return 0;
}

error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
  struct cpu_context_s *cctx = CONTEXT_LOCAL_TLS_ADDR(context->tls, x86emu_context);
  struct cpu_context_regs_s *regs = &cctx->kregs;

  cctx->mask = 0;

  // stack and entry param
  regs->esp = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end) - __MAX(8, CONFIG_HEXO_STACK_ALIGN);
  ((reg_t*)regs->esp)[0] = 0x5a5a5a5a;   /* return address */
  ((reg_t*)regs->esp)[1] = (reg_t)param; /* function parameter */

  // frame pointer
  regs->ebp = 0;
  // entry point
  regs->eip = (uintptr_t)entry;
  // flags, irq disabled
#if defined(CONFIG_CPU_X86_ALIGNCHECK)
  regs->eflags = 0x00040246;   /* EFLAGS with alignment chk */
#else
  regs->eflags = 0x00000246;   /* EFLAGS */
#endif

  return 0;
}

void
cpu_context_destroy(struct context_s *context)
{
}

