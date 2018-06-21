
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
  regs->rsp = CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end);

  regs->rdi = (reg_t)param;
  // frame pointer
  regs->rbp = 0;
  // entry point
  regs->rip = (uintptr_t)entry;
  // flags, irq disabled
#if defined(CONFIG_CPU_X86_ALIGNCHECK)
  regs->rflags = 0x40246;   /* EFLAGS with alignment chk */
#else
  regs->rflags = 0x00246;   /* EFLAGS */
#endif

  return 0;
}

void
cpu_context_destroy(struct context_s *context)
{
}

