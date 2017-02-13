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

#ifndef DRIVER_GAISLER_APBUART_PV_H_
#define DRIVER_GAISLER_APBUART_PV_H_

#include <hexo/types.h>
#include <device/device.h>

#include <hexo/gpct_platform_hexo.h>
#include <gpct/cont_ring.h>

#define APBUART_REG_DATA	0
#define APBUART_REG_STATUS	4
#define  APBUART_REG_STATUS_DR 0x001
#define  APBUART_REG_STATUS_TF 0x200
#define APBUART_REG_CTRL	8
#define  APBUART_REG_CTRL_SI (1 << 14)
#define  APBUART_REG_CTRL_DI (1 << 13)
#define  APBUART_REG_CTRL_BI (1 << 12)
#define  APBUART_REG_CTRL_DB (1 << 11)
#define  APBUART_REG_CTRL_RF (1 << 10)
#define  APBUART_REG_CTRL_TF (1 << 9)
#define  APBUART_REG_CTRL_EC (1 << 8)
#define  APBUART_REG_CTRL_LB (1 << 7)
#define  APBUART_REG_CTRL_FL (1 << 6)
#define  APBUART_REG_CTRL_PE (1 << 5)
#define  APBUART_REG_CTRL_PS (1 << 4)
#define  APBUART_REG_CTRL_TI (1 << 3)
#define  APBUART_REG_CTRL_RI (1 << 2)
#define  APBUART_REG_CTRL_TE (1 << 1)
#define  APBUART_REG_CTRL_RE (1 << 0)
#define APBUART_REG_SCALER	12
#define APBUART_REG_DEBUG	16

/**************************************************************/

CONTAINER_TYPE(uart_fifo, RING, uint8_t, 32);
CONTAINER_FUNC(uart_fifo, RING, static inline, uart_fifo);

struct gaisler_apbuart_context_s
{
  /* tty input request queue and char fifo */
  dev_char_queue_root_t		read_q;
  dev_char_queue_root_t		write_q;
  uart_fifo_root_t		read_fifo;
#ifdef CONFIG_HEXO_IRQ
  uart_fifo_root_t		write_fifo;
#endif
};

#endif

