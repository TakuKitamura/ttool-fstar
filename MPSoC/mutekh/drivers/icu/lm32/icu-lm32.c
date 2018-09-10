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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2011

*/

#include "icu-lm32.h"

#include "icu-lm32-private.h"

#include <string.h>
#include <stdio.h>

#include <hexo/types.h>
#include <device/device.h>
#include <device/driver.h>
#include <hexo/iospace.h>
#include <mutek/mem_alloc.h>
#include <hexo/interrupt.h>

#include <mutek/printk.h>

DEVICU_ENABLE(icu_lm32_enable)
{
	//	struct icu_lm32_private_s	*pv = dev->drv_pv;
#ifdef CONFIG_ARCH_SMP
 #error check we are executing on right cpu
#endif
	reg_t status;

	asm volatile ("rcsr	%0, IM" : "=r" (status));

	if (enable)
		status |= (1 << irq);
	else
		status &= ~(1 << irq);

	asm volatile ("wcsr	IM, %0" :: "r" (status));

	return 0;
}

DEVICU_SETHNDL(icu_lm32_sethndl)
{
	struct icu_lm32_private_s	*pv = dev->drv_pv;
	struct icu_lm32_handler_s	*h = pv->table + irq;

	h->hndl = hndl;
	h->data = data;

	return 0;
}

DEVICU_DELHNDL(icu_lm32_delhndl)
{
	struct icu_lm32_private_s	*pv = dev->drv_pv;
	struct icu_lm32_handler_s	*h = pv->table + irq;

	h->hndl = NULL;
	h->data = NULL;

	return 0;
}

static CPU_INTERRUPT_HANDLER(icu_lm32_handler)
{
	struct device_s *dev = CPU_LOCAL_GET(cpu_interrupt_handler_dev);
	struct icu_lm32_private_s	*pv = dev->drv_pv;
	struct icu_lm32_handler_s	*h;

	if ( irq >= CONFIG_CPU_LM32_IRQ_COUNT )
		goto lost;

	h = pv->table + irq;
	
	if (!h->hndl)
		goto lost;

	h->hndl(h->data);

	return;
 lost:
	printk("Lm32 %d got spurious interrupt %i\n", cpu_id(), irq);
}

DEV_CLEANUP(icu_lm32_cleanup)
{
	struct icu_lm32_private_s *pv = dev->drv_pv;

	mem_free(pv);
}

static const struct devenum_ident_s	icu_lm32_ids[] =
{
	DEVENUM_FDTNAME_ENTRY("cpu:lm32", 0, 0),
	{ 0 }
};

const struct driver_s	icu_lm32_drv =
{
	.class		= device_class_icu,
	.id_table   = icu_lm32_ids,
	.f_init		= icu_lm32_init,
	.f_irq      = (dev_irq_t *)icu_lm32_handler,
	.f_cleanup		= icu_lm32_cleanup,
	.f.icu = {
		.f_enable		= icu_lm32_enable,
		.f_sethndl		= icu_lm32_sethndl,
		.f_delhndl		= icu_lm32_delhndl,
	}
};

REGISTER_DRIVER(icu_lm32_drv);

DEV_INIT(icu_lm32_init)
{
	struct icu_lm32_private_s	*pv;

	dev->drv = &icu_lm32_drv;

	/* FIXME allocation scope ? */
	pv = mem_alloc(sizeof (*pv), (mem_scope_sys));

	if ( pv == NULL )
		goto memerr;

	dev->drv_pv = pv;

	memset(pv, 0, sizeof(*pv));

	return 0;

  memerr:
	return -ENOMEM;
}
