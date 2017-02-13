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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006

*/


#include <mutek/mem_alloc.h>
#include <hexo/init.h>
#include <hexo/segment.h>
#include <hexo/cpu.h>
#include <hexo/local.h>
#include <hexo/interrupt.h>
#include <hexo/context.h>

#ifdef CONFIG_SOCLIB_MEMCHECK
# include <arch/mem_checker.h>
#endif

#ifdef CONFIG_CPU_ARM_CUSTOM_IRQ_HANDLER
CPU_LOCAL
static uint32_t arm_irq_stack[128/4];
#endif

#ifdef CONFIG_ARCH_SMP
void * cpu_local_storage[CONFIG_CPU_MAXCOUNT];
#endif

/* CPU Local Descriptor structure */

error_t
cpu_global_init(void)
{

  return 0;
}

void cpu_init(void)
{
#ifdef CONFIG_ARCH_SMP
# if !defined(CONFIG_CPU_ARM_TLS_IN_C15)
#  error SMP and TLS unsupported
# endif

	void			*cls;

	cls = cpu_local_storage[cpu_id()];
		
	asm volatile ("mcr p15,0,%0,c13,c0,3":: "r" (cls));
#endif

#ifdef CONFIG_SOCLIB_MEMCHECK
        /* all these function may execute with invalid stack pointer
           register due to arm shadow registers bank switching. */
        void cpu_boot_end();
        soclib_mem_bypass_sp_check(&cpu_boot, &cpu_boot_end);
        void arm_exc_undef();
        void arm_exc_undef_end();
        soclib_mem_bypass_sp_check(&arm_exc_undef, &arm_exc_undef_end);
        void arm_exc_pabt();
        void arm_exc_pabt_end();
        soclib_mem_bypass_sp_check(&arm_exc_pabt, &arm_exc_pabt_end);
        void arm_exc_dabt();
        void arm_exc_dabt_end();
        soclib_mem_bypass_sp_check(&arm_exc_dabt, &arm_exc_dabt_end);
# ifdef CONFIG_HEXO_IRQ
        void arm_exc_irq();
        void arm_exc_irq_end();
        soclib_mem_bypass_sp_check(&arm_exc_irq, &arm_exc_irq_end);
        void arm_exc_fiq();
        void arm_exc_fiq_end();
        soclib_mem_bypass_sp_check(&arm_exc_fiq, &arm_exc_fiq_end);
# endif

# ifdef CONFIG_HEXO_USERMODE
        void arm_exc_swi();
        void arm_exc_swi_end();
        soclib_mem_bypass_sp_check(&arm_exc_swi, &arm_exc_swi_end);

        void cpu_context_set_user();
        void cpu_context_set_user_end();
        soclib_mem_bypass_sp_check(&cpu_context_set_user, &cpu_context_set_user_end);
# endif

        void arm_context_jumpto_internal_end();
        soclib_mem_bypass_sp_check(&cpu_context_jumpto, &arm_context_jumpto_internal_end);
#endif
}

