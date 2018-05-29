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

#ifndef QSORT_CAPSULE_H_
#define QSORT_CAPSULE_H_

#include "array.h"

void qsort_param_init();
void do_capsule(struct array_s *array);
void do_capsule_grouped(struct array_s *array);
void do_seq(struct array_s *array);

#endif
