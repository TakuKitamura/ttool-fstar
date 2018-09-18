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

#ifndef DRIVER_TIMER_EMU_H_
#define DRIVER_TIMER_EMU_H_

#include <device/timer.h>
#include <device/device.h>

/* timer device functions */

DEVTIMER_SETCALLBACK(timer_emu_setcallback);
DEVTIMER_SETPERIOD(timer_emu_setperiod);
DEVTIMER_SETVALUE(timer_emu_setvalue);
DEVTIMER_GETVALUE(timer_emu_getvalue);
DEV_CLEANUP(timer_emu_cleanup);
DEV_INIT(timer_emu_init);

#endif

