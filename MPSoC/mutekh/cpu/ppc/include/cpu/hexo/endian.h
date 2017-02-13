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

#if !defined(__ENDIAN_H_) || defined(__CPU_ENDIAN_H_)
#error This file can not be included directly
#else

#define __CPU_ENDIAN_H_

/** PPC CPU is big endian */
#define CPU_ENDIAN_ISBIG
#undef CPU_ENDIAN_ISLITTLE

#undef CPU_NATIVE_NONALIGNED_ACCESS

//#define HAS_CPU_ENDIAN_SWAP16
//#define HAS_CPU_ENDIAN_SWAP32
//#define HAS_CPU_ENDIAN_SWAP64

#endif

