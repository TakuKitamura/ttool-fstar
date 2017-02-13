/*
 *   This file is part of MutekH.
 *   
 *   MutekH is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; version 2.1 of the
 *   License.
 *   
 *   MutekH is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *   
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with MutekH; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *   02110-1301 USA.
 *
 *   Copyright Francois Charot <charot@irisa.fr>  (c) 2008
 *   INRIA Rennes Bretagne Atlantique
 *
 */


/**
   @file Task local and CPU local variables access
*/

#include <hexo/asm.h>

#if !defined(LOCAL_H_) || defined(CPU_LOCAL_H_)
#error This file can not be included directly
#else

#define CPU_LOCAL_H_

/************************************************************************/

#ifdef CONFIG_ARCH_SMP

# undef CPU_LOCAL
# define CPU_LOCAL	__attribute__((section (".cpudata")))

# define CPU_LOCAL_ADDR(n)                                              \
  ({									\
    typeof(n) *_ptr_;                                                   \
                                                                        \
    __asm__ (                                                           \
             ".set noat             \n"                                 \
             "	mov	r1, " ASM_STR(CPU_NIOS2_CLS_REG) "         \n"  \
             "	add	%0, r1, %1	    \n"                         \
             ".set at             \n"                                   \
             : "=r" (_ptr_)                                             \
             : "r" (&n)							\
            );                                                          \
                                                                        \
    _ptr_;								\
  })


#define CPU_GET_CLS()                                                   \
  ({									\
    uintptr_t _ptr_;                                                    \
                                                                        \
    __asm__ (                                                           \
             "	mov	%0, " ASM_STR(CPU_NIOS2_CLS_REG) "        \n"    \
	 : "=r" (_ptr_)                                                 \
								);      \
                                                                        \
    _ptr_;								\
  })

#else /* CONFIG_ARCH_SMP */

# define CPU_LOCAL

#endif /* CONFIG_ARCH_SMP */


/************************************************************************/

/** context local storage type attribute */
#define CONTEXT_LOCAL	__attribute__((section (".contextdata")))

/** pointer to context local storage in cpu local storage */
extern CPU_LOCAL void *__context_data_base;

#define CONTEXT_GET_TLS() ((uintptr_t)CPU_LOCAL_GET(__context_data_base))


#endif

