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


#include <hexo/types.h>
#include <hexo/init.h>
#include <hexo/cpu.h>

#ifdef CONFIG_HEXO_IPI
# include <device/icu.h>
# include <device/device.h>
# include <device/driver.h>
# include <hexo/ipi.h>
#endif

#include <hexo/lock.h>
#include <mutek/mem_alloc.h>
#if defined(CONFIG_MUTEK_SCHEDULER)
# include <mutek/scheduler.h>
#endif
#include <mutek/printk.h>

#ifdef CONFIG_DRIVER_ICU_APIC
# include <drivers/icu/apic/icu-apic.h>
#endif

#ifdef CONFIG_HEXO_MMU
#include <hexo/mmu.h>

#ifdef CONFIG_VMEM_PHYS_ALLOC
MMU_PPAGE_ALLOCATOR(ppage_alloc);
MMU_PPAGE_REFDROP(ppage_refdrop);
#else /*CONFIG_VMEM_PHYS_ALLOC*/
# error Add physical page allocator here
#endif /*CONFIG_VMEM_PHYS_ALLOC*/


#ifdef CONFIG_VMEM_KERNEL_ALLOC
MMU_VPAGE_ALLOCATOR(vmem_vpage_kalloc);
MMU_VPAGE_FREE(vmem_vpage_kfree);
#else /*CONFIG_VMEM_KERNEL_ALLOC*/
# error Add kernel virtual memory allocator here 
#endif /*CONFIG_VMEM_KERNEL_ALLOC*/

#endif /*CONFIG_HEXO_MMU*/

#include "multiboot.h"
#include "early_console.h"

/* conform to Multiboot Specification */

__attribute__((section (".multiboot")))
struct multiboot_header_s multiboot_header =
{
  .magic = MULTIBOOT_MAGIC,
  .flags = 0,
  .checksum = 0 - MULTIBOOT_MAGIC,
};

#ifdef CONFIG_DRIVER_ICU_APIC
static void apic_init()
{
  struct device_s *lapic = CPU_LOCAL_ADDR(apic_dev);

  device_init(lapic);
  lapic->addr[0] = 0xfee00000;
  icu_apic_init(lapic, NULL);

#ifdef CONFIG_HEXO_IPI
  dev_icu_setup_ipi_ep(lapic, CPU_LOCAL_ADDR(ipi_endpoint), cpu_id());
#endif
}
#endif

#ifdef CONFIG_ARCH_SMP
static lock_t		cpu_init_lock;	/* cpu intialization lock */
static lock_t		cpu_start_lock;	/* cpu wait for start lock */
static size_t           cpu_count = 1;
#endif

#if defined (CONFIG_MUTEK_SCHEDULER)
extern struct sched_context_s main_ctx;
#endif

extern __ldscript_symbol_t __initial_stack;

/* architecture specific init function */
void arch_init(uintptr_t init_sp)
{
#ifdef CONFIG_ARCH_SMP
  if (cpu_isbootstrap())
    /* First CPU */
    {
      lock_init(&cpu_init_lock);
      lock_init(&cpu_start_lock);
#endif

#ifdef CONFIG_MUTEK_EARLY_CONSOLE
      early_console_init();
      printk_set_output(early_console_output, NULL);
#endif
      cpu_global_init();

      mem_init();

      hexo_global_init();

#ifdef CONFIG_HEXO_MMU

#ifdef CONFIG_VMEM_PHYS_ALLOC
      vmem_ops.ppage_alloc = &ppage_alloc;
      vmem_ops.ppage_refdrop = &ppage_refdrop;
      initial_ppage_region = (struct vmem_page_region_s *)ppage_initial_region_get();
      ppage_region_init(initial_ppage_region ,CONFIG_HEXO_MMU_INITIAL_END, CONFIG_ARCH_IBMPC_MEMORY);
#else
# error Add physical page allocator init 
#endif

      mmu_global_init();

#ifdef CONFIG_VMEM_KERNEL_ALLOC
      vmem_ops.vpage_alloc = &vmem_vpage_kalloc;
      vmem_ops.vpage_free = &vmem_vpage_kfree;
      //	vmem_init(t0, t1);
#else
# error Add virtual kernel page allocator init 
#endif

#endif /*CONFIG_HEXO_MMU*/

      /* send reset/init signal to other CPUs */
#ifdef CONFIG_ARCH_SMP
      lock_try(&cpu_start_lock);
#endif

      /* configure first CPU and start app CPUs */
      cpu_init();
#ifdef CONFIG_HEXO_MMU
      mmu_cpu_init();
#endif

#ifdef CONFIG_DRIVER_ICU_APIC
      apic_init();
#endif

      {
            uintptr_t stack_end = (uintptr_t)&__initial_stack - (1 << CONFIG_HEXO_RESET_STACK_SIZE) * cpu_id();

#if defined(CONFIG_MUTEK_SCHEDULER)
            sched_global_init();
            sched_cpu_init();

            /* FIXME initial stack space will never be freed ! */
            context_bootstrap(&main_ctx.context, 0, stack_end);
            sched_context_init(&main_ctx);
#endif
        }

#if defined(CONFIG_ARCH_HW_INIT_USER)
	  user_hw_init();
#elif defined(CONFIG_ARCH_HW_INIT)
	  arch_hw_init();
#else
# error No supported hardware initialization
#endif


      /* run mutek_start() */
      mutek_start();
#ifdef CONFIG_ARCH_SMP
    }
  else
    /* Other CPUs */
    {
      /* configure other CPUs */

      lock_spin(&cpu_init_lock);

      cpu_init();

#ifdef CONFIG_HEXO_MMU
      mmu_cpu_init();
#endif

#ifdef CONFIG_DRIVER_ICU_APIC
      apic_init();
#endif
      cpu_count++;

      lock_release(&cpu_init_lock);

      /* wait for start signal */
      while (lock_state(&cpu_start_lock))
	;

#if defined(CONFIG_MUTEK_SCHEDULER)
      sched_cpu_init();
#endif

      /* run mutek_start_smp() */
      mutek_start_smp();
    }
#endif
}

void arch_start_other_cpu(void)
{
#ifdef CONFIG_ARCH_SMP
  lock_release(&cpu_start_lock);
#endif
}

size_t arch_get_cpu_count(void)
{
#ifdef CONFIG_ARCH_SMP
  return cpu_count;
#else
  return 1;
#endif
}

