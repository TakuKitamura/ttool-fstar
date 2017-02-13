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

#ifndef BLOCK_FILE_EMU_PRIVATE_H_
#define BLOCK_FILE_EMU_PRIVATE_H_

#include <hexo/types.h>
#include <hexo/lock.h>

#define BLOCK_FILE_EMU_MAX_RQ_COUNT 64

struct block_file_emu_context_s
{
  struct dev_block_params_s params;
  __compiler_sint_t fd;
};

#endif
