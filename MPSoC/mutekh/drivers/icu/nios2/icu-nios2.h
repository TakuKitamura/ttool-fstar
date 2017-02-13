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

    Copyright (c) Nicolas Pouillon <nipo@ssji.net>, 2009

*/


#ifndef DRIVER_ICU_NIOS2_H_
#define DRIVER_ICU_NIOS2_H_

#include <device/icu.h>
#include <device/device.h>

/* icu device functions */

DEV_INIT(icu_nios2_init);
DEVICU_ENABLE(icu_nios2_enable);
DEVICU_SETHNDL(icu_nios2_sethndl);
DEVICU_DELHNDL(icu_nios2_delhndl);
DEV_CLEANUP(icu_nios2_cleanup);
void icu_nios2_update(struct device_s *dev);

#endif

