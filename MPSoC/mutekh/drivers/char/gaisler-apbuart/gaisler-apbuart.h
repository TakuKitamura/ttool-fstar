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

#ifndef DRIVER_GAISLER_APBUART_H_
#define DRIVER_GAISLER_APBUART_H_

#include <device/char.h>
#include <device/device.h>

/* tty device functions */

DEV_IRQ(gaisler_apbuart_irq);
DEV_INIT(gaisler_apbuart_init);
DEV_CLEANUP(gaisler_apbuart_cleanup);
DEVCHAR_REQUEST(gaisler_apbuart_request);

#endif

