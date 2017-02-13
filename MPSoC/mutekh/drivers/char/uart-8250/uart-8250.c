/*
  This file is part of MutekH.
  
  MutekH is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; version 2.1 of the License.
  
  MutekH is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
  License for more details.
  
  You should have received a copy of the GNU Lesser General Public
  License along with MutekH; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
  02110-1301 USA.

  Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006

*/


#include <hexo/types.h>
#include <stdlib.h>

#include <device/icu.h>
#include <device/char.h>
#include <device/device.h>
#include <device/driver.h>

#include <hexo/iospace.h>
#include <mutek/mem_alloc.h>
#include <hexo/lock.h>
#include <hexo/interrupt.h>

#include "uart-8250.h"

#include "uart-8250-private.h"

#include "8250.h"

#if 0
#include <mutek/printk.h>
#define dprintk(...) printk(__VA_ARGS__)
#else
#define dprintk(...) do{}while(0)
#endif


static inline void update_ier(struct device_s *dev)
{
    struct uart_8250_context_s *pv = dev->drv_pv;
    uint8_t ier = 0;
    if ( pv->read_rq != NULL )
        ier |= UART_8250_IER_RX;
    if ( pv->write_rq != NULL )
        ier |= UART_8250_IER_TX;
    dprintk("%s %02x\n", __FUNCTION__, ier);
    cpu_io_write_8(dev->addr[0] + UART_8250_IER, ier);
}

static void try_read(struct device_s *dev, struct dev_char_rq_s *rq)
{
    struct uart_8250_context_s *pv = dev->drv_pv;

    while ( cpu_io_read_8(dev->addr[0] + UART_8250_LSR) & UART_8250_LSR_RX )
    {
        dprintk("%s %p %d\n", __FUNCTION__, rq, rq->size);

        *rq->data = cpu_io_read_8(dev->addr[0] + UART_8250_RBR);
        ++(rq->data);
        --(rq->size);

        if (rq->callback(dev, rq, 1) || rq->size == 0)
        {
            LOCK_SPIN_IRQ(&dev->lock);
            pv->read_rq = rq = dev_char_queue_pop(&pv->read_q);
            LOCK_RELEASE_IRQ(&dev->lock);

            if ( rq == NULL )
                break;
        }
    }
}

static void try_write(struct device_s *dev, struct dev_char_rq_s *rq)
{
    struct uart_8250_context_s *pv = dev->drv_pv;

    while ( cpu_io_read_8(dev->addr[0] + UART_8250_LSR) & UART_8250_LSR_TXEMPTY )
    {
        dprintk("%s %p %d\n", __FUNCTION__, rq, rq->size);

        cpu_io_write_8(dev->addr[0] + UART_8250_THR, *rq->data);
        rq->data++;
        rq->size--;

        if (rq->callback(dev, rq, 1) || rq->size == 0)
        {
            LOCK_SPIN_IRQ(&dev->lock);
            pv->write_rq = rq = dev_char_queue_pop(&pv->write_q);
            LOCK_RELEASE_IRQ(&dev->lock);

            if ( rq == NULL )
                break;
        }
    }
}

DEVCHAR_REQUEST(uart_8250_request)
{
    struct uart_8250_context_s	*pv = dev->drv_pv;
    bool_t must_start = 0;

    if (rq->size == 0) {
        if (rq->callback)
            rq->callback(dev, rq, 0);
        return;
    }

    LOCK_SPIN_IRQ(&dev->lock);

    switch (rq->type)
    {
    case DEV_CHAR_READ:
        if ( pv->read_rq == NULL ) {
            pv->read_rq = rq;
            must_start = 1;
        } else {
            dev_char_queue_pushback(&pv->read_q, rq);
        }
        break;

    case DEV_CHAR_WRITE:
        if ( pv->write_rq == NULL ) {
            pv->write_rq = rq;
            must_start = 1;
        } else {
            dev_char_queue_pushback(&pv->write_q, rq);
        }
        break;
    }

    LOCK_RELEASE_IRQ(&dev->lock);

    if ( !must_start )
        return;

    update_ier(dev);
}

/* 
 * device close operation
 */

DEV_CLEANUP(uart_8250_cleanup)
{
    struct uart_8250_context_s	*pv = dev->drv_pv;

    DEV_ICU_UNBIND(dev->icudev, dev, dev->irq, uart_8250_irq);

    dev_char_queue_destroy(&pv->read_q);
    dev_char_queue_destroy(&pv->write_q);

    mem_free(pv);
}

/*
 * IRQ handler
 */

DEV_IRQ(uart_8250_irq)
{
    struct uart_8250_context_s *pv = dev->drv_pv;
    uint8_t iir = cpu_io_read_8(dev->addr[0] + UART_8250_IIR);

    dprintk("%s %x %x\n", __FUNCTION__, 
            cpu_io_read_8(dev->addr[0] + UART_8250_IER),
            iir);

    switch ( iir & 0x7 ) {
    case 1:
        // NOTPENDING
        update_ier(dev);
        return 0;

    case 0:
        // MSR mod
//        printk("Spurious UART irq: iir = %x\n", iir);
        break;

    case 6:
        // LSR mod
//        printk("Spurious UART irq: iir = %x\n", iir);
        break;

    case 4:
        // RHR full
        if ( pv->read_rq )
            try_read(dev, pv->read_rq);
        break;

    case 2:
        // THR empty:
        if ( pv->write_rq )
            try_write(dev, pv->write_rq);
        break;
    }

    update_ier(dev);

    return 1;
}

/* 
 * device open operation
 */

#ifdef CONFIG_DRIVER_ENUM_FDT
static const struct driver_param_binder_s binder[] =
{
    PARAM_BIND(struct uart_8250_param_s, crystal_hz, PARAM_DATATYPE_INT),
    PARAM_BIND(struct uart_8250_param_s, line_baud, PARAM_DATATYPE_INT),
    { 0 }
};

static const struct devenum_ident_s	uart_8250_ids[] =
{
    DEVENUM_FDTNAME_ENTRY("uart8250", sizeof(struct uart_8250_param_s), binder),
    DEVENUM_FDTNAME_ENTRY("uart8250", 0, 0),
    DEVENUM_FDTNAME_ENTRY("uart16550", sizeof(struct uart_8250_param_s), binder),
    DEVENUM_FDTNAME_ENTRY("uart16550", 0, 0),
    { 0 }
};
#endif

const struct driver_s	uart_8250_drv =
{
    .class		= device_class_char,
#ifdef CONFIG_DRIVER_ENUM_FDT
    .id_table     = uart_8250_ids,
#endif
    .f_init		= uart_8250_init,
    .f_cleanup		= uart_8250_cleanup,
    .f_irq		= uart_8250_irq,
    .f.chr = {
        .f_request		= uart_8250_request,
    }
};

#ifdef CONFIG_DRIVER_ENUM_FDT
REGISTER_DRIVER(uart_8250_drv);
#endif

DEV_INIT(uart_8250_init)
{
    struct uart_8250_context_s	*pv;
    struct uart_8250_param_s *param = params;

    dev->drv = &uart_8250_drv;
  
    /* alocate private driver data */
    pv = mem_alloc(sizeof(*pv), (mem_scope_sys));

    if (!pv)
        return -1;

    dev_char_queue_init(&pv->read_q);
    dev_char_queue_init(&pv->write_q);
    pv->read_rq = NULL;
    pv->write_rq = NULL;

    dev->drv_pv = pv;

    pv->line_mode = UART_8250_LCR_8BITS | UART_8250_LCR_PARNO | UART_8250_LCR_1STOP;

    pv->crystal_hz = 115200*16;
    pv->line_baud = 9600;
    if ( param ) {
        pv->crystal_hz = param->crystal_hz;
        pv->line_baud = param->line_baud;
    }

    uint32_t hz = pv->crystal_hz >> 4;
    div_t d = div(hz + pv->line_baud/2, pv->line_baud);
    pv->divisor = d.quot;

    dprintk("%s crystal: %d\n", __FUNCTION__, pv->crystal_hz);
    dprintk("%s baud   : %d\n", __FUNCTION__, pv->line_baud);
    dprintk("%s divisor: %d\n", __FUNCTION__, pv->divisor);

    cpu_io_write_8(dev->addr[0] + UART_8250_IER, 0);
    cpu_io_write_8(dev->addr[0] + UART_8250_LCR, 0);

    cpu_io_write_8(dev->addr[0] + UART_8250_FCR, UART_8250_FCR_FIFO | UART_8250_FCR_CLRRX | UART_8250_FCR_CLRTX);
    cpu_io_write_8(dev->addr[0] + UART_8250_FCR, UART_8250_FCR_FIFO);

    cpu_io_write_8(dev->addr[0] + UART_8250_MCR, 0
#if defined(CONFIG_ARCH_IBMPC)
                   /* GP Output pins must be set on ibmpc to activate IRQ routing */
                   | UART_8250_MCR_OUT1 | UART_8250_MCR_OUT2
#endif
        );

    cpu_io_write_8(dev->addr[0] + UART_8250_LCR, UART_8250_LCR_DLAB);
    cpu_io_write_8(dev->addr[0] + UART_8250_DLL, pv->divisor & 0xff);
    cpu_io_write_8(dev->addr[0] + UART_8250_DLM, pv->divisor >> 8);

    cpu_io_write_8(dev->addr[0] + UART_8250_LCR, pv->line_mode);

    DEV_ICU_BIND(dev->icudev, dev, dev->irq, uart_8250_irq);

    update_ier(dev);

    return 0;
}

