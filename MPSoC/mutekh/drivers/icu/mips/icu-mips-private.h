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

#ifndef __ICU_mips_PRIVATE_H_
#define __ICU_mips_PRIVATE_H_

#include <device/icu.h>
#include <device/device.h>

struct icu_mips_handler_s
{
  dev_irq_t		*hndl;
  void			*data;
};

#define ICU_MIPS_MAX_VECTOR	6

struct icu_mips_private_s
{
	struct icu_mips_handler_s	table[ICU_MIPS_MAX_VECTOR];
};

#endif

