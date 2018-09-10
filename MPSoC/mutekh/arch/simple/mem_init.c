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

#include <mutek/mem_alloc.h>
#include <hexo/segment.h>
#include <hexo/lock.h>
#include <hexo/endian.h>


void mem_init(void)
{
  void	*mem_start = (uint8_t*)&__system_heap_start;
  void	*mem_end = (uint8_t*)&__system_heap_end;

  mem_end = ALIGN_ADDRESS_LOW(mem_end, CONFIG_MUTEK_MEMALLOC_ALIGN);
  mem_start = ALIGN_ADDRESS_UP(mem_start, CONFIG_MUTEK_MEMALLOC_ALIGN);

  default_region = memory_allocator_init(
    NULL, 
    mem_start,
    (void*)((uintptr_t)mem_end -
            (1 << CONFIG_HEXO_RESET_STACK_SIZE) * CONFIG_CPU_MAXCOUNT));
}

