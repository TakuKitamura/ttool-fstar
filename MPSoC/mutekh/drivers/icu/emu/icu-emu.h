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


#ifndef DRIVER_ICU_emu_H_
#define DRIVER_ICU_emu_H_

#include <device/icu.h>
#include <device/device.h>

#include <hexo/local.h>

/* icu device functions */
DEV_INIT(icu_emu_init);
DEVICU_ENABLE(icu_emu_enable);
DEVICU_SETHNDL(icu_emu_sethndl);
DEVICU_DELHNDL(icu_emu_delhndl);
DEV_CLEANUP(icu_emu_cleanup);

/** @this function raise an interrupt for the given device.
 device must be attached to an emu icu. */
void icu_emu_post(struct device_s *source);

#endif

