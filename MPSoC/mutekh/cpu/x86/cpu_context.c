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

#include <hexo/error.h>
#include <hexo/local.h>
#include <hexo/context.h>
#include <hexo/segment.h>
#include <hexo/interrupt.h>

#include <cpu/hexo/pmode.h>

#include <stdlib.h>

CONTEXT_LOCAL cpu_x86_segsel_t context_tls_seg;
CONTEXT_LOCAL struct cpu_context_s x86_context;

#ifdef CONFIG_HEXO_USERMODE
/* current context local storage segment descriptor, used to restore
   segment register when returning from user mode */
CPU_LOCAL cpu_x86_segsel_t *cpu_tls_seg = 0;
#endif

error_t
cpu_context_bootstrap(struct context_s *context)
{
  cpu_x86_segsel_t	tls_seg;

  /* get a new segment descriptor for tls */
  if (!(tls_seg = cpu_x86_segment_alloc((uintptr_t)context->tls,
					arch_contextdata_size(),
					CPU_X86_SEG_DATA_UP_RW)))
    return -ENOMEM;

  cpu_x86_dataseggs_use(tls_seg, 0);

  CONTEXT_LOCAL_SET(__context_data_base, context->tls);
  CONTEXT_LOCAL_SET(context_tls_seg, CPU_X86_SEGSEL(tls_seg, CPU_X86_KERNEL));

  struct cpu_context_s *cctx = CONTEXT_LOCAL_ADDR(x86_context);

  cctx->mask = 0;

  return 0;
}

error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
  struct cpu_context_s *cctx = CONTEXT_LOCAL_TLS_ADDR(context->tls, x86_context);
  struct cpu_context_regs_s *regs = &cctx->kregs;
  cpu_x86_segsel_t	tls_seg;

  cctx->mask = 0;

  /* get a new segment descriptor for tls */
  if (!(tls_seg = cpu_x86_segment_alloc((uintptr_t)context->tls,
					arch_contextdata_size(),
					CPU_X86_SEG_DATA_UP_RW)))
    return -ENOMEM;

  CONTEXT_LOCAL_TLS_SET(context->tls, __context_data_base, context->tls);
  CONTEXT_LOCAL_TLS_SET(context->tls, context_tls_seg, CPU_X86_SEGSEL(tls_seg, CPU_X86_KERNEL));

  // stack and entry param
  regs->esp = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end) - __MAX(8, CONFIG_HEXO_STACK_ALIGN);
  ((reg_t*)regs->esp)[0] = 0x5a5a5a5a;   /* return address */
  ((reg_t*)regs->esp)[1] = (reg_t)param; /* function parameter */

  // frame pointer
  regs->ebp = 0;
  // entry point
  regs->eip = (uintptr_t)entry;
  // flags, irq disabled
  regs->eflags = CPU_X86_EFLAGS_NONE;

  return 0;
}

void
cpu_context_destroy(struct context_s *context)
{
  cpu_x86_segsel_t tls_seg = CONTEXT_LOCAL_TLS_GET(context->tls, context_tls_seg) >> 3;
  /* free tls segment descriptor */
  cpu_x86_segdesc_free(tls_seg);
}

void cpu_exception_resume_pc(struct cpu_context_s *regs, uintptr_t pc)
{
# ifdef CONFIG_HEXO_USERMODE
  if (regs->mask & CPU_X86_CONTEXT_MASK_USER)
    regs->uregs.eip = pc;
  else
#endif
    regs->kregs.eip = pc;
}
