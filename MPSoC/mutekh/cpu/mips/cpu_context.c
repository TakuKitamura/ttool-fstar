
#include <stdlib.h>

#include <hexo/cpu.h>
#include <hexo/error.h>
#include <hexo/context.h>
#include <hexo/interrupt.h>

CONTEXT_LOCAL struct cpu_context_s mips_context_regs;

#ifdef CONFIG_HEXO_LAZY_SWITCH
/* last fpu restored context */
CPU_LOCAL struct cpu_context_s *mips_lazy_last = 0;
#endif

error_t
cpu_context_bootstrap(struct context_s *context)
{
    /* set context local storage base pointer */
    CPU_LOCAL_SET(__context_data_base, context->tls);

    /* nothing is saved for this context */
    CONTEXT_LOCAL_ADDR(mips_context_regs)->save_mask = 0;

    return 0;
}


/* context init function */
error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
  struct cpu_context_s *regs = CONTEXT_LOCAL_TLS_ADDR(context->tls, mips_context_regs);

  regs->save_mask = CPU_MIPS_CONTEXT_RESTORE_CALLER; /* for r4 */
  regs->gpr[CPU_MIPS_SP] = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end)
                         - CONFIG_HEXO_STACK_ALIGN;
#ifdef CONFIG_COMPILE_FRAMEPTR
    regs->gpr[CPU_MIPS_FP] = regs->gpr[CPU_MIPS_SP];
#endif
  regs->gpr[4] = (uintptr_t)param;

  regs->sr = CPU_MIPS_STATUS_IM;
#if defined (CONFIG_HEXO_FPU) && !defined(CONFIG_HEXO_LAZY_SWITCH)
  regs->sr |= CPU_MIPS_STATUS_FPU;
  regs->fcsr = 0;
#endif

  regs->gpr[CPU_MIPS_RA] = 0xa5a5a5a5; /* can not return from context entry */
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

// Local Variables:
// tab-width: 4;
// c-basic-offset: 4;
// c-file-offsets:((innamespace . 0)(inline-open . 0));
// indent-tabs-mode: nil;
// End:
//
// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

