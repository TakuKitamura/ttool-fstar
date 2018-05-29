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

#ifndef __TIMER_EMU_PRIVATE_H_
#define __TIMER_EMU_PRIVATE_H_

#include <device/timer.h>

#define TIMER_EMU_IDCOUNT	16

struct			timer_emu_context_s
{
  devtimer_callback_t	*cb[TIMER_EMU_IDCOUNT];
  void			*pv[TIMER_EMU_IDCOUNT];
};

#endif

