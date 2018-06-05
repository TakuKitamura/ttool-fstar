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

#if !defined(IOSPACE_H_) || defined(CPU_IOSPACE_H_)
#error This file can not be included directly
#else

#include <hexo/cpu.h>

#define CPU_IOSPACE_H_

static inline void
cpu_io_write_8(uintptr_t addr, uint8_t data)
{
  cpu_trap();
}

static inline uint8_t
cpu_io_read_8(uintptr_t addr)
{
  cpu_trap();
  return 0;
}

static inline void
cpu_io_write_16(uintptr_t addr, uint16_t data)
{
  cpu_trap();
}

static inline uint16_t
cpu_io_read_16(uintptr_t addr)
{
  cpu_trap();
  return 0;
}

static inline void
cpu_io_write_32(uintptr_t addr, uint32_t data)
{
  cpu_trap();
}

static inline uint32_t
cpu_io_read_32(uintptr_t addr)
{
  cpu_trap();
  return 0;
}

/****************************************************/

static inline void
cpu_mem_write_8(uintptr_t addr, uint8_t data)
{
  cpu_trap();
}

static inline uint8_t
cpu_mem_read_8(uintptr_t addr)
{
  cpu_trap();
  return 0;
}

static inline void
cpu_mem_write_16(uintptr_t addr, uint16_t data)
{
  cpu_trap();
}

static inline uint16_t
cpu_mem_read_16(uintptr_t addr)
{
  cpu_trap();
  return 0;
}

static inline void
cpu_mem_write_32(uintptr_t addr, uint32_t data)
{
  cpu_trap();
}

static inline uint32_t
cpu_mem_read_32(uintptr_t addr)
{
  cpu_trap();
  return 0;
}

#endif

