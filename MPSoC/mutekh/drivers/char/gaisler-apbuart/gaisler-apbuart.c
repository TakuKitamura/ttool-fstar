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

    Copyright (c) 2011 Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr>
    Copyright (c) 2011 Institut Telecom / Telecom ParisTech

*/

#include "gaisler-apbuart.h"

#include "gaisler-apbuart-private.h"

#include <device/icu.h>
#include <hexo/types.h>
#include <hexo/endian.h>
#include <device/device.h>
#include <device/driver.h>
#include <hexo/iospace.h>
#include <mutek/mem_alloc.h>
#include <hexo/interrupt.h>

void gaisler_apbuart_try_read(struct device_s *dev)
{
  struct gaisler_apbuart_context_s	*pv = dev->drv_pv;
  struct dev_char_rq_s		*rq;

  while ((rq = dev_char_queue_head(&pv->read_q)))
    {
      size_t size = 0;

      /* read as many characters as possible from driver fifo */
      size = uart_fifo_pop_array(&pv->read_fifo, rq->data, rq->size);

      /* try to read more characters directly from device fifo */
      while ( size < rq->size && (cpu_mem_read_32(dev->addr[0] + APBUART_REG_STATUS)
                                  & endian_be32(APBUART_REG_STATUS_DR)) )
        {
          rq->data[size++] = endian_be32(cpu_mem_read_32(dev->addr[0] + APBUART_REG_DATA));
        }

      if (size)
        {
          rq->size -= size;
          rq->error = 0;

          if (rq->callback(dev, rq, size) || rq->size == 0)
            {
              dev_char_queue_remove(&pv->read_q, rq);
              continue;
            }

          rq->data += size;
        }

#ifdef CONFIG_HEXO_IRQ
      /* more data will be available on next interrupt */
      return;
#endif
    }

  /* copy more data from device fifo to driver fifo if no request currently need it */
  while (cpu_mem_read_32(dev->addr[0] + APBUART_REG_STATUS)
         & endian_be32(APBUART_REG_STATUS_DR))
    {
      uart_fifo_pushback(&pv->read_fifo, endian_be32(cpu_mem_read_32(dev->addr[0] + APBUART_REG_DATA)));
    }
}

void gaisler_apbuart_try_write(struct device_s *dev)
{
  struct gaisler_apbuart_context_s	*pv = dev->drv_pv;
  struct dev_char_rq_s		*rq;

#ifdef CONFIG_HEXO_IRQ
  /* try to write data from driver fifo to device */
  while (!uart_fifo_isempty(&pv->write_fifo) &&
         !(cpu_mem_read_32(dev->addr[0] + APBUART_REG_STATUS)
           & endian_be32(APBUART_REG_STATUS_TF)) )
    {
      uint8_t c = uart_fifo_pop(&pv->write_fifo);
      cpu_mem_write_32(dev->addr[0] + APBUART_REG_DATA, endian_be32(c));
    }
#endif

  while ((rq = dev_char_queue_head(&pv->write_q)))
    {
      size_t size = 0;

#ifdef CONFIG_HEXO_IRQ
      if (uart_fifo_isempty(&pv->write_fifo))
        {
          /* driver fifo is empty, try to write as many characters as
             possible from request directly to the device fifo */
#endif
          while (size < rq->size)
            {
              if ( cpu_mem_read_32(dev->addr[0] + APBUART_REG_STATUS)
                   & endian_be32(APBUART_REG_STATUS_TF) )
                break;

              cpu_mem_write_32(dev->addr[0] + APBUART_REG_DATA, endian_be32(rq->data[size++]));
            }

#ifdef CONFIG_HEXO_IRQ
        }

      /* some characters were not written to the device fifo, push to driver fifo */
      if (size < rq->size)
          size += uart_fifo_pushback_array(&pv->write_fifo, rq->data + size, rq->size - size);
#endif

      if (size)
        {
          rq->size -= size;
          rq->error = 0;

          if (rq->callback(dev, rq, size) || rq->size == 0)
            {
              dev_char_queue_remove(&pv->write_q, rq);
              continue;
            }

          rq->data += size;
        }

#ifdef CONFIG_HEXO_IRQ
      /* more fifo space will be available on next interrupt */
      return;
#endif
    }
}

DEVCHAR_REQUEST(gaisler_apbuart_request)
{
  struct gaisler_apbuart_context_s	*pv = dev->drv_pv;

  assert(rq->size);

  LOCK_SPIN_IRQ(&dev->lock);

  switch (rq->type)
    {
    case DEV_CHAR_READ: {
#ifdef CONFIG_HEXO_IRQ
      bool_t empty = dev_char_queue_isempty(&pv->read_q);
#endif
      dev_char_queue_pushback(&pv->read_q, rq);
#ifdef CONFIG_HEXO_IRQ
      if (empty)
#endif
	gaisler_apbuart_try_read(dev);
      break;
    }

    case DEV_CHAR_WRITE: {
#ifdef CONFIG_HEXO_IRQ
      bool_t empty = dev_char_queue_isempty(&pv->write_q);
#endif
      dev_char_queue_pushback(&pv->write_q, rq);
#ifdef CONFIG_HEXO_IRQ
      if (empty)
#endif
	gaisler_apbuart_try_write(dev);
      break;
    }
    }

  LOCK_RELEASE_IRQ(&dev->lock);
}

/* 
 * device close operation
 */

DEV_CLEANUP(gaisler_apbuart_cleanup)
{
  struct gaisler_apbuart_context_s	*pv = dev->drv_pv;

  uint32_t c = endian_be32(cpu_mem_read_32(dev->addr[0] + APBUART_REG_CTRL));

#ifdef CONFIG_HEXO_IRQ
  if ( dev->icudev )
    DEV_ICU_UNBIND(dev->icudev, dev, dev->irq, gaisler_apbuart_irq);

  uart_fifo_destroy(&pv->write_fifo);

  /* disable irqs */
  c &= ~(APBUART_REG_CTRL_RF | APBUART_REG_CTRL_TF | APBUART_REG_CTRL_DI |
         APBUART_REG_CTRL_BI | APBUART_REG_CTRL_SI | APBUART_REG_CTRL_TI | APBUART_REG_CTRL_RI);

#endif

  /* disable transmitter and receiver */
  c &= ~(APBUART_REG_CTRL_TE | APBUART_REG_CTRL_RE);

  cpu_mem_write_32(dev->addr[0] + APBUART_REG_CTRL, endian_be32(c));

  uart_fifo_destroy(&pv->read_fifo);

  dev_char_queue_destroy(&pv->read_q);
  dev_char_queue_destroy(&pv->write_q);

  mem_free(pv);
}

#ifdef CONFIG_HEXO_IRQ

/*
 * device irq
 */

DEV_IRQ(gaisler_apbuart_irq)
{
  lock_spin(&dev->lock);

  gaisler_apbuart_try_read(dev);
  gaisler_apbuart_try_write(dev);

  lock_release(&dev->lock);

  return 1;
}

#endif

/* 
 * device open operation
 */

static const struct devenum_ident_s	gaisler_apbuart_ids[] =
{
	DEVENUM_GAISLER_ENTRY(0x1, 0x00c),
	{ 0 }
};

const struct driver_s	gaisler_apbuart_drv =
{
  .class		= device_class_char,
  .id_table		= gaisler_apbuart_ids,
  .f_init		= gaisler_apbuart_init,
  .f_cleanup		= gaisler_apbuart_cleanup,
#ifdef CONFIG_HEXO_IRQ
  .f_irq		= gaisler_apbuart_irq,
#endif
  .f.chr = {
    .f_request		= gaisler_apbuart_request,
  }
};

REGISTER_DRIVER(gaisler_apbuart_drv);

DEV_INIT(gaisler_apbuart_init)
{
  struct gaisler_apbuart_context_s	*pv;
  device_mem_map( dev , 1 << 0 );
  dev->drv = &gaisler_apbuart_drv;

  pv = mem_alloc(sizeof(*pv), (mem_scope_sys));

  if (!pv)
    return -1;

  dev->drv_pv = pv;

  dev_char_queue_init(&pv->read_q);
  dev_char_queue_init(&pv->write_q);

  uart_fifo_init(&pv->read_fifo);

  uint32_t c = endian_be32(cpu_mem_read_32(dev->addr[0] + APBUART_REG_CTRL));

  /* enable transmitter and receiver */
  c |= (APBUART_REG_CTRL_TE | APBUART_REG_CTRL_RE);

#ifdef CONFIG_HEXO_IRQ
  uart_fifo_init(&pv->write_fifo);

  if ( dev->icudev )
	  DEV_ICU_BIND(dev->icudev, dev, dev->irq, gaisler_apbuart_irq);

  c |=  (APBUART_REG_CTRL_TI | APBUART_REG_CTRL_RI);
  c &= ~(APBUART_REG_CTRL_DI | APBUART_REG_CTRL_BI | APBUART_REG_CTRL_SI | APBUART_REG_CTRL_RF | APBUART_REG_CTRL_TF);

#else

  /* disable irqs */
  c &= ~(APBUART_REG_CTRL_RF | APBUART_REG_CTRL_TF | APBUART_REG_CTRL_DI |
         APBUART_REG_CTRL_BI | APBUART_REG_CTRL_SI | APBUART_REG_CTRL_TI | APBUART_REG_CTRL_RI);
#endif

  cpu_mem_write_32(dev->addr[0] + APBUART_REG_CTRL, endian_be32(c));

  return 0;
}

