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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006-2011
    Copyright Nicolas Pouillon, <nipo@ssji.net>, 2009

*/

#include <hexo/lock.h>
#include <hexo/iospace.h>
#include <hexo/ordering.h>
#include <string.h>

#include "early_console.h"

static lock_t early_console_lock;

#ifdef CONFIG_IBMPC_EARLY_CONSOLE_VGA
static uint_fast16_t cursor = 0;
#endif

void early_console_init()
{
  lock_init(&early_console_lock);
}

PRINTF_OUTPUT_FUNC(early_console_output)
{
  size_t i;

  lock_spin(&early_console_lock);

#ifdef CONFIG_IBMPC_EARLY_CONSOLE_E9HACK
  for (i = 0; i < len; ++i)
    cpu_io_write_8(0xe9, str[i]);
#endif

#ifdef CONFIG_IBMPC_EARLY_CONSOLE_VGA
  {
    uint16_t *ptr = (void*)0xb8000;
    static const size_t width = 80;
    static const size_t height = 25;

    for (i = 0; i < len; ++i)
      {
        char c = str[i];

        if (c == '\n') {
          if (cursor >= (height - 1) * width) {
            memmove(ptr, ptr + width, (height - 1) * width * 2);
            cursor = (height - 1) * width;
          } else {
            cursor = (cursor / width + 1) * width;
          }
          memset(ptr + cursor, 0, width * 2);
        } else {
          ptr[cursor++] = c | 0x0200;
        }

        if (cursor >= height * width)
          cursor = 0;
      }

    /* force memory write */
    order_compiler_mem();
  }
#endif

#ifdef CONFIG_IBMPC_EARLY_CONSOLE_UART
  for (i = 0; i < len; ++i)
    {
# warning CONFIG_IBMPC_EARLY_CONSOLE_UART may block on busy wait loop
      while(!(cpu_io_read_8(CONFIG_IBMPC_EARLY_CONSOLE_UART_PORT + 5) & (1<<6)))
        ;
      cpu_io_write_8(CONFIG_IBMPC_EARLY_CONSOLE_UART_PORT, str[i]);
    }
#endif

  lock_release(&early_console_lock);
}

