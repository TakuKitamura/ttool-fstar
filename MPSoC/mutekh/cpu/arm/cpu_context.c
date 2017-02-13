
#include <hexo/error.h>
#include <hexo/local.h>
#include <hexo/context.h>
#include <hexo/interrupt.h>
#include <mutek/scheduler.h>

CONTEXT_LOCAL struct cpu_context_s arm_context_regs;

#if !defined(CONFIG_CPU_ARM_TLS_IN_C15)
CPU_LOCAL void *__context_data_base;
#endif

void arm_setup_exception_stack(uintptr_t addr);

error_t
cpu_context_bootstrap(struct context_s *context)
{
    /* set context local storage register base pointer */
#if defined(CONFIG_CPU_ARM_TLS_IN_C15)
    THUMB_TMP_VAR;
    
    asm volatile (
        THUMB_TO_ARM
        "mcr p15,0,%0,c13,c0,4 \n\t"
        ARM_TO_THUMB
        /*:*/ THUMB_OUT(:)
        : "r" (context->tls));
#else
    __context_data_base = context->tls;
#endif

    arm_setup_exception_stack((uintptr_t)CONTEXT_LOCAL_ADDR(arm_context_regs));

    return 0;
}


/* context init function */

error_t
cpu_context_init(struct context_s *context, context_entry_t *entry, void *param)
{
    struct cpu_context_s *regs = CONTEXT_LOCAL_TLS_ADDR(context->tls, arm_context_regs);

    regs->save_mask =
        CPU_ARM_CONTEXT_RESTORE_CALLER |
        CPU_ARM_CONTEXT_RESTORE_CALLEE;
    regs->gpr[13] =
        CONTEXT_LOCAL_TLS_GET(context->tls, context_stack_end)
        - CONFIG_HEXO_STACK_ALIGN;
#ifdef CONFIG_COMPILE_FRAMEPTR
    regs->gpr[11] = regs->gpr[13];
#endif
    regs->gpr[0] = (uintptr_t)param;

    regs->cpsr = 0x000000d3;

    regs->gpr[14] = 0xa5a5a5a5; /* can not return from context entry */
    regs->gpr[15] = (uintptr_t)entry;

    return 0;
}



void
cpu_context_destroy(struct context_s *context)
{
#if 0
    reg_t		*stack = (reg_t*)context->stack_ptr;
#endif
}

__attribute__((noreturn))
extern void arm_context_jumpto_back();

inline struct context_s *arm_except_preempt()
{
    struct context_s *ctx = NULL;
# ifdef CONFIG_HEXO_CONTEXT_PREEMPT
    context_preempt_t *handler = CPU_LOCAL_GET(cpu_preempt_handler);
    if ( handler ) {
        ctx = handler(CPU_LOCAL_GET(cpu_preempt_param));
    }

# ifdef CONFIG_HEXO_CONTEXT_STATS
    if ( ctx ) {
        context_preempt_stats(ctx);
    }
# endif
#endif

    return ctx;
}

#ifdef CONFIG_HEXO_USERMODE
extern CONTEXT_LOCAL cpu_exception_handler_t  *cpu_user_exception_handler;
#endif

extern CPU_LOCAL cpu_exception_handler_t  *cpu_exception_handler;

struct context_s *arm_exc_common(reg_t no, struct cpu_context_s *context)
{
    cpu_exception_handler_t *handler = NULL;
#ifdef CONFIG_HEXO_USERMODE
    if ( (context->cpsr & 0xf) == 0 )
        handler = CONTEXT_LOCAL_GET(cpu_user_exception_handler);
    if ( handler == NULL )
#endif  
        handler = CPU_LOCAL_GET(cpu_exception_handler);
    handler(no,
            context->gpr[15],
            0, context,
            context->gpr[13]);

    return arm_except_preempt();
}

#ifdef CONFIG_HEXO_USERMODE
extern CONTEXT_LOCAL cpu_syscall_handler_t  *cpu_syscall_handler;

struct context_s *arm_swi_common(struct cpu_context_s *context)
{
    cpu_syscall_handler_t *handler =
        CONTEXT_LOCAL_GET(cpu_syscall_handler);
    handler(0, context);

    return arm_except_preempt();
}
#endif

#ifdef CONFIG_HEXO_IRQ
extern CPU_LOCAL cpu_interrupt_handler_t  *cpu_interrupt_handler;
extern CPU_LOCAL struct device_s *cpu_interrupt_handler_dev;

struct context_s *arm_irq_common(reg_t no, struct cpu_context_s *context)
{
    cpu_interrupt_handler_t *handler =
        CPU_LOCAL_GET(cpu_interrupt_handler);
    handler(no);

    return arm_except_preempt();
}
#endif

void cpu_exception_resume_pc(struct cpu_context_s *regs, uintptr_t pc)
{
  regs->gpr[15] = pc;
}

// Local Variables:
// tab-width: 4;
// c-basic-offset: 4;
// indent-tabs-mode: nil;
// End:
//
// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4
