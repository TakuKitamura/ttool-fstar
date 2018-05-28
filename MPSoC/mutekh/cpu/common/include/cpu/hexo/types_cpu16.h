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


/**
   @file

   CPU dependant prefered types definition
 */

#if !defined(TYPES_H_) || defined(CPU_TYPES_H_)
#error This file can not be included directly
#else

#define CPU_TYPES_H_

# define INT_FAST8_SIZE		16
# define INT_FAST16_SIZE	16
# define INT_FAST32_SIZE	32
# define INT_FAST64_SIZE	64

#define CPU_SIZEOF_SHORT	8
#define CPU_SIZEOF_INT		16
#define CPU_SIZEOF_LONG		32
#define INT_PTR_SIZE		16
#define INT_REG_SIZE		16

#endif

