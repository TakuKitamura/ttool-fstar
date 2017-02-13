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

#ifndef DRIVER_UART_8250_H_
#define DRIVER_UART_8250_H_

#include <device/char.h>
#include <device/device.h>

/* devices addresses slots */

#define UART_8250_ADDR	0

struct uart_8250_param_s {
    uint32_t crystal_hz;
    uint32_t line_baud;
};

/* tty device functions */

DEV_IRQ(uart_8250_irq);
DEV_INIT(uart_8250_init);
DEV_CLEANUP(uart_8250_cleanup);
DEVCHAR_REQUEST(uart_8250_request);

#endif

