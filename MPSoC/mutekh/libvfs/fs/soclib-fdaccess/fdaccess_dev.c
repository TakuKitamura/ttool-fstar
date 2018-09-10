/*
    This file is part of MutekH.

    MutekH is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MutekH is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MutekH; if not, write to the Free Software Foundation,
    Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

    Copyright (c) 2011 Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr>
*/

#include <hexo/endian.h>
#include <hexo/types.h>
#include <hexo/lock.h>
#include <hexo/interrupt.h>
#include <hexo/iospace.h>

#include <device/device.h>
#include <device/icu.h>
#include <device/driver.h>

#include <mutek/mem_alloc.h>
#include <mutek/printk.h>

#include "soclib-fdaccess.h"
#include "soclib-fdaccess-private.h"

struct device_s *soclib_fdaccess_device; /* FIXME use device api to find device from fs part */

static void soclib_fdaccess_start_rq(struct device_s *dev,
                                     const struct soclib_fdaccess_rq_s *rq)
{
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_BUFFER, endian_le32((uint32_t)rq->buffer));
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_SIZE, endian_le32(rq->size));
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_FD, endian_le32(rq->fd));
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_HOW, endian_le32(rq->how));
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_MODE, endian_le32(rq->mode));
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_OP, endian_le32(rq->op));
}

static void soclib_fdaccess_read_rq(struct device_s *dev,
                                    struct soclib_fdaccess_rq_s *rq)
{
  rq->size = endian_le32(cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_SIZE));
  rq->fd = endian_le32(cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_FD));
  rq->how = endian_le32(cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_HOW));
  rq->mode = endian_le32(cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_MODE));
  rq->retval = endian_le32(cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_RETVAL));
}

int32_t soclib_fdaccess_rq(struct device_s *dev,
                           struct soclib_fdaccess_rq_s *rq)
{
  struct soclib_fdaccess_devpv_s	*pv = dev->drv_pv;

#ifdef CONFIG_HEXO_IRQ
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  lock_spin(&dev->lock);

  rq->done = 0;

  /* start request on device if no other existing requests */
  if (soclib_fdaccess_rq_isempty(&pv->queue))
    soclib_fdaccess_start_rq(dev, rq);

  /* enqueue request */
  soclib_fdaccess_rq_pushback(&pv->queue, rq);

# ifdef CONFIG_MUTEK_SCHEDULER
  rq->ctx = sched_get_current();
  sched_stop_unlock(&dev->lock);
  CPU_INTERRUPT_RESTORESTATE;

# else
  lock_release(&dev->lock);
  CPU_INTERRUPT_RESTORESTATE;

  assert(cpu_is_interruptible());

  while (!rq->done)
    order_compiler_mem();
# endif

#else

  soclib_fdaccess_start_rq(dev, rq);

  while (cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_OP)
         != endian_le32(SOCLIB_FDACCESS_NOOP))
    ;

  soclib_fdaccess_read_rq(dev, rq);

#endif

  return rq->retval;
}

static const struct devenum_ident_s	soclib_fdaccess_ids[] =
{
  DEVENUM_FDTNAME_ENTRY("soclib:fdaccess", 0, 0),
  { 0 }
};

const struct driver_s	soclib_fdaccess_drv =
{
  .class		= device_class_none,
  .id_table		= soclib_fdaccess_ids,
  .f_init		= soclib_fdaccess_init,
  .f_cleanup		= soclib_fdaccess_cleanup,
#ifdef CONFIG_HEXO_IRQ
  .f_irq		= soclib_fdaccess_irq,
#endif
};

REGISTER_DRIVER(soclib_fdaccess_drv);

#ifdef CONFIG_HEXO_IRQ
DEV_IRQ(soclib_fdaccess_irq)
{
  struct soclib_fdaccess_devpv_s	*pv = dev->drv_pv;

  lock_spin(&dev->lock);

  do {
    struct soclib_fdaccess_rq_s *rq = soclib_fdaccess_rq_pop(&pv->queue);

    if (rq)
      {
        soclib_fdaccess_read_rq(dev, rq);
#if defined(CONFIG_MUTEK_SCHEDULER)
        if (rq->ctx)
          sched_context_start(rq->ctx);
#endif
        rq->done = 1;
      }

    rq = soclib_fdaccess_rq_head(&pv->queue);

    if (rq)
      soclib_fdaccess_start_rq(dev, rq);

  } while (cpu_mem_read_32(dev->addr[0] + SOCLIB_FDACCESS_IRQ_ENABLE));

  lock_release(&dev->lock);

  return 1;
}
#endif

DEV_INIT(soclib_fdaccess_init)
{
  struct soclib_fdaccess_devpv_s	*pv;

  dev->drv = &soclib_fdaccess_drv;

  /* allocate private driver data */
  pv = mem_alloc(sizeof(*pv), (mem_scope_sys));

  if (!pv)
    goto err;

  soclib_fdaccess_rq_init(&pv->queue);

#ifdef CONFIG_HEXO_IRQ
  DEV_ICU_BIND(dev->icudev, dev, dev->irq, soclib_fdaccess_irq);
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_IRQ_ENABLE, endian_le32(1));
#else
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_IRQ_ENABLE, 0);
#endif

  dev->drv_pv = pv;
  soclib_fdaccess_device = dev;

  printk("Soclib fdaccess _device_ init done. irq=%u addr=%08x\n", dev->irq, dev->addr[0]);
  return 0;

#if 0
 err_pv:
  mem_free(pv);
#endif
 err:
  return -1;
}

DEV_CLEANUP(soclib_fdaccess_cleanup)
{
  struct soclib_fdaccess_devpv_s	*pv = dev->drv_pv;

  soclib_fdaccess_rq_destroy(&pv->queue);

#ifdef CONFIG_HEXO_IRQ
  cpu_mem_write_32(dev->addr[0] + SOCLIB_FDACCESS_IRQ_ENABLE, 0);
  DEV_ICU_UNBIND(dev->icudev, dev, dev->irq, soclib_fdaccess_irq);
#endif

  mem_free(pv);
}

