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

#ifndef DRIVER_TTY_VGA_H_
#define DRIVER_TTY_VGA_H_

#include <device/char.h>
#include <device/device.h>

/* devices addresses slots */

#define VGA_TTY_ADDR_BUFFER	0
#define VGA_TTY_ADDR_CRTC	1

/* tty device functions */

DEV_IRQ(tty_vga_irq);
DEV_INIT(tty_vga_init);
DEV_CLEANUP(tty_vga_cleanup);
DEVCHAR_REQUEST(tty_vga_request);

#endif

