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

    Copyright (c) 2010, Nicolas Pouillon <nipo@ssji.net>
*/

#include <stdlib.h>
#include "qsort_libc.h"

/*
 Libc implementation
 */

static
int_fast8_t qsort_cmp(const void * a, const void * b)
{
    const elem_t *t_a = a;
    const elem_t *t_b = b;

    return *t_a - *t_b;
}
	 
void do_libc(struct array_s *array)
{
    qsort(array->array, array->size, sizeof(* array->array), qsort_cmp);
}
