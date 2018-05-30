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

#include <hexo/init.h>
#include <hexo/types.h>
#include <hexo/endian.h>
#include <hexo/context.h>
#include <hexo/cpu.h>

#include <hexo/interrupt.h>
#include <hexo/local.h>
#include <hexo/iospace.h>
#include <hexo/lock.h>
#include <hexo/context.h>
#include <hexo/cpu.h>

#if defined(CONFIG_MUTEK_SCHEDULER)
# include <mutek/scheduler.h>
#endif

#include <device/char.h>
#include <device/timer.h>
#include <device/enum.h>

#include <device/device.h>
#include <device/driver.h>

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <mutek/timer.h>
#include <mutek/printk.h>
#include <mutek/console.h>

#if defined(CONFIG_VFS)
# include <vfs/vfs.h>
#endif

#if defined(CONFIG_ARCH_DEVICE_TREE)
# include <drivers/enum/fdt/enum-fdt.h>
# include <mutek/fdt.h>

extern struct device_s fdt_enum_dev;
extern void *arch_fdt;
#endif

#if defined (CONFIG_MUTEK_TIMER)
struct device_s *timerms_dev = NULL;
struct timer_s timer_ms;
#endif

#if defined(CONFIG_VFS)
struct device_s *root_dev;
#endif

#if defined(CONFIG_MUTEK_TIMER)
DEVTIMER_CALLBACK(timer_callback)
{
	//  printk("timer callback\n");
# if defined(CONFIG_MUTEK_SCHEDULER_PREEMPT)
        context_set_preempt(sched_preempt_switch, NULL);
# endif

	timer_inc_ticks(&timer_ms, 10);
}
#endif

static CPU_EXCEPTION_HANDLER(fault_handler);

static lock_t fault_lock;

int_fast8_t mutek_start()  /* FIRST CPU only */
{
	lock_init(&fault_lock);
	cpu_exception_sethandler(fault_handler);

#if defined(CONFIG_ARCH_DEVICE_TREE)
    cpu_interrupt_enable();
    enum_fdt_children_init(&fdt_enum_dev);
    mutek_parse_fdt_chosen(&fdt_enum_dev, arch_fdt);
    cpu_interrupt_disable();
#endif

#if defined(CONFIG_MUTEK_CONSOLE) && !defined(CONFIG_MUTEK_PRINTK_KEEP_EARLY)
	if ( console_dev )
		printk_set_output(__printf_out_tty, console_dev);
#endif

#if defined(CONFIG_LIBC_UNIXFD)
	libc_unixfd_init();
#endif

    cpu_interrupt_enable();

#if defined (CONFIG_MUTEK_TIMER)
	timer_init(&timer_ms.root);
	timer_ms.ticks = 0;

	if ( timerms_dev ) {
		dev_timer_setperiod(timerms_dev, 0, 1193180 / 100);
		dev_timer_setcallback(timerms_dev, 0, timer_callback, 0);
	} else {
		printk("Warning: no timer device available\n");
	}
#endif

  printk("MutekH is alive.\n");

#ifdef CONFIG_ARCH_SMP
  arch_start_other_cpu(); /* let other CPUs enter main_smp() */
#endif

  mutek_start_smp();

  return 0;
}

static CPU_EXCEPTION_HANDLER(fault_handler)
{
  int_fast8_t		i;
  reg_t			*sp = (reg_t*)stackptr;
#ifdef CPU_CONTEXT_REG_NAMES
  static const char		*reg_names[] = { CPU_CONTEXT_REG_NAMES };
#endif

#ifdef CPU_FAULT_NAMES
  static const char *const fault_names[CPU_FAULT_COUNT] = CPU_FAULT_NAMES;
  const char *name = type < CPU_FAULT_COUNT ? fault_names[type] : "unknown";
#else
  const char *name = "unknown";
#endif

  lock_spin(&fault_lock);

  printk("CPU Fault: cpuid(%u) faultid(%u-%s)\n", cpu_id(), type, name);
  printk("Execution pointer: %p, Bad address (if any): %p\n"
	 "Registers:"
		 , (void*)execptr, (void*)dataptr);

  for (i = CPU_CONTEXT_REG_FIRST; i < CPU_CONTEXT_REG_COUNT; i++)
#ifdef CPU_CONTEXT_REG_NAMES
    printk("%s=%p%c", reg_names[i], (reg_t*)(uintptr_t)regs + i, (i + 1) % 4 ? ' ' : '\n');
#else
    printk("%p%c", (void*)(uintptr_t)regs[i], (i + 1) % 4 ? ' ' : '\n');
#endif

  printk("Stack top (%p):\n", (void*)stackptr);

  for (i = 0; i < 12; i++)
	  printk("%p%c", (void*)(uintptr_t)sp[i], (i + 1) % 4 ? ' ' : '\n');

  lock_release(&fault_lock);

  while (1)
    ;
}

/** application main function */
void app_start();

#if defined(CONFIG_MUTEK_SCHEDULER)
static void bootstrap_cleanup(void *param)
{
/*   extern struct sched_context_s main_ctx; */
/*   context_destroy(&main_ctx.context); */

#if defined(CONFIG_ARCH_SOCLIB) && 0
  extern void mem_reclaim_initmem();
  mem_reclaim_initmem();
#endif

  /* scheduler context switch without saving */
  sched_context_exit();
}

static void other_cleanup(void *param)
{
#ifdef CONFIG_SOCLIB_MEMCHECK
  cpu_id_t id = (uintptr_t)param;
  soclib_mem_check_delete_ctx(id);
#endif

  /* scheduler context switch without saving */
  sched_context_exit();
}
#endif

void mutek_start_smp(void)  /* ALL CPUs execute this function */
{
  cpu_exception_sethandler(fault_handler);

  printk("CPU %i is up and running.\n", cpu_id());

#if defined(CONFIG_COMPILE_INSTRUMENT)
  //  mutek_instrument_trace(1);
  //  mutek_instrument_alloc_guard(1);
#endif

  if (cpu_isbootstrap()) {
#ifdef CONFIG_OPENMP
    void initialize_libgomp();
    initialize_libgomp();
#endif
    app_start();
#if defined(CONFIG_MUTEK_SCHEDULER)
    /* run bootstrap_cleanup() on temporary context stack */
    cpu_interrupt_disable();
    cpu_context_stack_use(sched_tmp_context(), bootstrap_cleanup, NULL);
#endif
  } else {
#if defined(CONFIG_MUTEK_SCHEDULER)
    cpu_id_t id = cpu_id();
#endif
#ifdef CONFIG_MUTEK_SMP_APP_START
    app_start();
#endif
#if defined(CONFIG_MUTEK_SCHEDULER)
    /* run bootstrap_cleanup() on temporary context stack */
    cpu_interrupt_disable();
    cpu_context_stack_use(sched_tmp_context(), other_cleanup, (void*)(uintptr_t)id);
#endif
  }
}
