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


#ifndef LCD_S1D15G00_H_
#define LCD_S1D15G00_H_

#include <hexo/types.h>
#include <device/lcd.h>

DEVLCD_REQUEST(s1d15g00_request);
DEVLCD_GETINFO(s1d15g00_getinfo);
DEV_CLEANUP(s1d15g00_cleanup);

struct device_s;

struct s1d15g00_param_s
{
	struct device_s *spi;
	uint_fast8_t spi_lun;
	struct device_s *set_reset_gpio_dev;
	uint_fast8_t set_reset_gpio_id;
};

DEV_INIT(s1d15g00_init);

#endif

