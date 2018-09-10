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
   @file CPU atomic operations
*/


#if !defined(ATOMIC_H_) || defined(CPU_ATOMIC_H_)
#error This file can not be included directly
#else

#define CPU_ATOMIC_H_

#define HAS_CPU_ATOMIC_INC

static inline bool_t
cpu_atomic_inc(atomic_int_t *a)
{
  reg_t result, tmp;

  //  (*a)++;

  __asm__ volatile(
 	       "1:     custom  0, %[result], %[addr_a], zero  \n"
	       "       addi    %[tmp], %[result], 1           \n"
	       "       custom  1, %[tmp], %[addr_a], zero     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       : [tmp] "=&r" (tmp), [result] "=&r" (result), [clobber] "=m" (*a)
	       : [addr_a] "r" (a)
			);

  return result + 1 != 0;
}


#define HAS_CPU_ATOMIC_DEC

static inline bool_t
cpu_atomic_dec(atomic_int_t *a)
{
  reg_t result, tmp;

  //  (*a)--;

  __asm__ volatile(
	       "1:     custom  0, %[result], %[addr_a], zero   \n"
	       "       addi    %[tmp], %[result], -1           \n"
	       "       custom  1, %[tmp], %[addr_a], zero      \n"
	       "       beq     %[tmp], zero, 1b                \n"
	       : [tmp] "=&r" (tmp), [result] "=&r" (result), [clobber] "=m" (*a)
	       : [addr_a] "r" (a)
	       );

  return result - 1 != 0;
}

#define HAS_CPU_ATOMIC_TESTSET

static inline bool_t
cpu_atomic_bit_testset(atomic_int_t *a, uint_fast8_t n)
{
  reg_t result, tmp, loaded;
  reg_t mask = 1 << n;

  //  *a |= (1 << n);

  __asm__ volatile (
		"1:     custom  0, %[loaded], %[addr_a], zero  \n"
		"       and     %[result], %[loaded], %[mask]  \n"
		"       bne     %[result], zero, 2f            \n"
		"       or      %[tmp], %[loaded], %[mask]     \n"
		"       custom  1, %[tmp], %[addr_a], zero     \n"
		"       beq     %[tmp], zero, 1b               \n"
		"2:                                            \n"
		: [tmp] "=&r" (tmp), [loaded] "=&r" (loaded), [result] "=&r" (result), [clobber] "=m" (*a)
		: [mask] "r" (mask), [addr_a] "r" (a)
		);

  return result != 0;
}

#define HAS_CPU_ATOMIC_WAITSET

static inline void
cpu_atomic_bit_waitset(atomic_int_t *a, uint_fast8_t n)
{
  reg_t tmp, loaded;
  reg_t mask = 1 << n;

  //  while (!(*a & (1 << n)))

  __asm__ volatile(
	       "1:     custom  0, %[loaded], %[addr_a], zero  \n"
	       "       and     %[tmp], %[loaded], %[mask]     \n"
	       "       bne     %[tmp], zero, 1b               \n"
	       "       or      %[tmp], %[loaded], %[mask]     \n"
	       "       custom  1, %[tmp], %[addr_a], zero     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       "2:                                            \n"
	       : [tmp] "=&r" (tmp), [loaded] "=&r" (loaded), [clobber] "=m" (*a)
	       : [mask] "r" (mask), [addr_a] "r" (a)
	       );
}

#define HAS_CPU_ATOMIC_TESTCLR

static inline bool_t
cpu_atomic_bit_testclr(atomic_int_t *a, uint_fast8_t n)
{
  reg_t result, tmp, loaded;
  reg_t mask = 1 << n;

  //  *a &= ~(1 << n);

  __asm__ volatile(
	       "1:     custom  0, %[loaded], %[addr_a], zero  \n"
	       "       and     %[result], %[loaded], %[mask]  \n"
	       "       beq     %[result], zero, 2f            \n"
	       "       xor     %[tmp], %[loaded], %[mask]     \n"
	       "       custom  1, %[tmp], %[addr_a], zero     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       "2:                                            \n"
	       : [tmp] "=&r" (tmp), [loaded] "=&r" (loaded), [result] "=&r" (result), [clobber] "=m" (*a)
	       : [mask] "r" (mask), [addr_a] "r" (a)
	       );

  return result != 0;
}

#define HAS_CPU_ATOMIC_WAITCLR

static inline void
cpu_atomic_bit_waitclr(atomic_int_t *a, uint_fast8_t n)
{

  reg_t tmp, loaded;
  reg_t mask = 1 << n;

  //  while ((*a & (1 << n)))

  __asm__ volatile(
	       "1:     custom  0, %[loaded], %[addr_a], zero  \n"
	       "       and     %[tmp], %[loaded], %[mask]     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       "       xor     %[tmp], %[loaded], %[mask]     \n"
	       "       custom  1, %[tmp], %[addr_a], zero     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       "2:                                            \n"
	       : [tmp] "=&r" (tmp), [loaded] "=&r" (loaded), [clobber] "=m" (*a)
	       : [mask] "r" (mask), [addr_a] "r" (a)
	       );
}

#define HAS_CPU_ATOMIC_SET

static inline void
cpu_atomic_bit_set(atomic_int_t *a, uint_fast8_t n)
{
  reg_t tmp;
  reg_t mask = 1 << n;

  //  *a |= (1 << n);

  __asm__ volatile(
	       "1:     custom  0, %[tmp], %[addr_a], zero     \n"
	       "       or      %[tmp], %[tmp], %[mask]        \n"
	       "       custom  1, %[tmp], %[addr_a], zero     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       : [tmp] "=&r" (tmp), [clobber]"=m" (*a)
	       : [mask] "r" (mask), [addr_a] "r" (a)
	       );
}

#define HAS_CPU_ATOMIC_CLR

static inline void
cpu_atomic_bit_clr(atomic_int_t *a, uint_fast8_t n)
{
  reg_t tmp;
  reg_t mask = ~(1 << n);

  //  *a &= ~(1 << n);

  __asm__ volatile(
	       "1:     custom  0, %[tmp], %[addr_a], zero     \n"
	       "       and     %[tmp], %[tmp], %[mask]        \n"
	       "       custom  1, %[tmp], %[addr_a], zero     \n"
	       "       beq     %[tmp], zero, 1b               \n"
	       : [tmp] "=&r" (tmp), "=m" (*a)
	       : [mask] "r" (mask), [addr_a] "r" (a)
	       );
}

static inline bool_t
cpu_atomic_compare_and_swap(atomic_int_t *a, atomic_int_t old, atomic_int_t new)
{
    reg_t tmp, loaded;

    asm volatile (
	       "1:     custom  0, %[loaded], %[addr_a], zero     \n"
	       "       bne     %[loaded], %[old], 2f             \n"
	       "       move    %[tmp], %[new]                    \n"
	       "       custom  1, %[tmp], %[addr_a], zero        \n"
	       "       beq    %[tmp], 1b                         \n"
	       "       nop                                       \n"
	       "2:                                               \n"
	       : [tmp] "=&r" (tmp), [loaded] "=&r" (loaded), "=m" (*a)
               : [old] "r" (old), [new] "r" (new), [addr_a] "r" (a)
               : "memory"
        );

    return loaded == old;
}

#endif
