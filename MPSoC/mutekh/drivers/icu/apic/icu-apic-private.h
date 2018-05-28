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

#ifndef __ICU_apic_PRIVATE_H_
#define __ICU_apic_PRIVATE_H_

#include <device/device.h>

struct icu_apic_handler_s
{
  dev_irq_t		*hndl;
  void			*data;
};

struct icu_apic_private_s
{
  struct icu_apic_handler_s	table[CPU_HWINT_VECTOR_COUNT];
  struct device_s		*dev;
};

#define APIC_IPI_VECTOR 0x5e
#define APIC_IPI_RQ_VECTOR 0x5f

#endif

