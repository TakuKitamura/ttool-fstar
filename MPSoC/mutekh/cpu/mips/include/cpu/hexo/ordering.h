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

    Copyright Eric Guthmuller (c) 2011

*/

/**
   @file CPU memory ordering operations
*/

#if !defined(_HEXO_ORDERING_H_) || defined(CPU_ORDERING_H_)
#error This file can not be included directly
#else

#define CPU_ORDERING_H_

#define CPU_ORDER_MEM "sync"

#define CPU_ORDER_WRITE "sync"

#define CPU_ORDER_IO_MEM "sync"

#define CPU_ORDER_IO_WRITE "sync"

#endif
