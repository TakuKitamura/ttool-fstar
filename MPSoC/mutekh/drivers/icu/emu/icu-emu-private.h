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

#ifndef __ICU_emu_PRIVATE_H_
#define __ICU_emu_PRIVATE_H_

#include <device/device.h>
#include <hexo/atomic.h>

#define CONFIG_DRIVER_ICU_EMU_IRQCOUNT 32

struct icu_emu_handler_s
{
  dev_irq_t		*hndl;
  void			*data;
};

struct icu_emu_private_s
{
  cpu_id_t cpuid;
  atomic_t mask;
  atomic_t pending;
  struct icu_emu_handler_s	table[CONFIG_DRIVER_ICU_EMU_IRQCOUNT];
};

#endif

