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

#include <stdlib.h>

static __rand_type_t	random_seed = 1;

/* Use Linear congruential PRNG */
__rand_type_t rand_r(__rand_type_t *seedp)
{
  __rand_type_t	res;

  if (sizeof(__rand_type_t) > 1)
    {
      /* Knuth & Lewis */
      uint32_t	x = *seedp * 1664525 + 1013904223;
      *seedp = x;

      res = (x >> 16) & 0x7fff;
    }
  else
    {
      uint8_t	x = *seedp * 137 + 187;
      *seedp = x;
      res = x;
    }

  return res;
}

__rand_type_t rand(void)
{
  return rand_r(&random_seed);
}

void srand(__rand_type_t seed)
{
  random_seed = seed;
}

/* ************************************************** */

__rand_type_t random(void)
{
  /* FIXME */
  return rand();
}

void srandom(__rand_type_t seed)
{
  /* FIXME */
  srand(seed);
}

char *initstate(__rand_type_t seed, char *state, size_t n)
{
  /* FIXME */
  srand(seed);
  return state;
}

char *setstate(char *state)
{
  /* FIXME */
  return state;
}

