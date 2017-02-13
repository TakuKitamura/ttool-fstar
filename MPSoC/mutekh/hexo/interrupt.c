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

	Copyright (c) Nicolas Pouillon, <nipo@ssji.net>, 2009
*/

#include <hexo/interrupt.h>
#include <hexo/context.h>

#include <device/icu.h>

#include <device/device.h>
#include <device/driver.h>

#include <mutek/printk.h>

/************************************************************ irqs */

#ifdef CONFIG_HEXO_IRQ

CPU_LOCAL cpu_interrupt_handler_t  *cpu_interrupt_handler;
CPU_LOCAL struct device_s *cpu_interrupt_handler_dev;

void
cpu_interrupt_sethandler_device(struct device_s *dev)
{
	printk("Setting CPU IRQ handler for cpuid %d: %p drv: %p\n",
		   cpu_id(), dev, dev->drv);
	assert(dev && dev->drv);

	CPU_LOCAL_SET(cpu_interrupt_handler, (cpu_interrupt_handler_t *)dev->drv->f_irq);
	CPU_LOCAL_SET(cpu_interrupt_handler_dev, dev);
}

void
cpu_interrupt_sethandler(cpu_interrupt_handler_t *handler)
{
  CPU_LOCAL_SET(cpu_interrupt_handler, handler);
}

#endif

/************************************************************ syscalls */

#ifdef CONFIG_HEXO_USERMODE

/** syscall handler for current context */
CONTEXT_LOCAL cpu_syscall_handler_t  *cpu_syscall_handler = NULL;

void
cpu_syscall_sethandler_ctx(struct context_s *context,
			   cpu_syscall_handler_t *hndl)
{
  CONTEXT_LOCAL_TLS_SET(context->tls, cpu_syscall_handler, hndl);
}

void
cpu_syscall_sethandler(cpu_syscall_handler_t *hndl)
{
  CONTEXT_LOCAL_SET(cpu_syscall_handler, hndl);
}

#endif

/************************************************************ exceptions */

#ifdef CONFIG_HEXO_USERMODE
/** user mode exception handler for current context */
CONTEXT_LOCAL cpu_exception_handler_t  *cpu_user_exception_handler = NULL;

void
cpu_user_exception_sethandler(cpu_exception_handler_t *hndl)
{
  CPU_LOCAL_SET(cpu_user_exception_handler, hndl);
}

void
cpu_user_exception_sethandler_ctx(struct context_s *context,
				  cpu_exception_handler_t *hndl)
{
  CONTEXT_LOCAL_TLS_SET(context->tls, cpu_user_exception_handler, hndl);
}
#endif

CPU_LOCAL cpu_exception_handler_t  *cpu_exception_handler;

void
cpu_exception_sethandler(cpu_exception_handler_t *hndl)
{
  CPU_LOCAL_SET(cpu_exception_handler, hndl);
}


