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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2011

*/

#include <hexo/power.h>
#include <hexo/iospace.h>
#include <hexo/interrupt.h>

error_t power_reboot()
{
  cpu_interrupt_disable();

  /* try 8042 keyboard controller hack */
  while (cpu_io_read_8(0x64) & 2)
    ;
  cpu_io_write_8(0x64, 0xfe);

  /* try ibm ps2 hack */
  cpu_io_write_8(0x92, 0x01);

  /* try pci chipset */
  cpu_io_write_8(0xcf9, 0x02);
  cpu_io_write_8(0xcf9, 0x06);

  return 0;
}

error_t power_shutdown()
{
  /* bochs/qemu ACPI poweroff hack */
  cpu_io_write_16(0xb004, 0x2000);

  return 0;
}

