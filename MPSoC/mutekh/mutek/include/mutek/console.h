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

#ifndef MUTEK_CONSOLE_H_
#define MUTEK_CONSOLE_H_

#include <mutek/fileops.h>
#include <libc/formatter.h>

extern const struct fileops_s console_file_ops;

#if defined(CONFIG_MUTEK_CONSOLE)

struct device_s;
extern struct device_s *console_dev;

PRINTF_OUTPUT_FUNC(__printf_out_tty);

#endif

#endif

