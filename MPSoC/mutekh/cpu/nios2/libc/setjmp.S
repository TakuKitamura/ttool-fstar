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
 *   Copyright Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr> (c) 2011
 */

#include <hexo/asm.h>
#include <hexo/cpu.h>

FUNC_START(.text, setjmp)
	.set noat

	stw	r16,	0(r4)
	stw	r17,	4(r4)
	stw	r18,	8(r4)
	stw	r19,	12(r4)
	stw	r20,	16(r4)
	stw	r21,	20(r4)
	stw	r22,	24(r4)
	stw	r23,	28(r4)

	stw	r26,	32(r4)
	stw	r27,	36(r4)
	stw	r28,	40(r4)
	stw	r29,	44(r4)

	mov	r2, 	zero
	jmp	ra

	.set at
FUNC_END(setjmp)

FUNC_START(.text, longjmp)
	.set noat

	ldw	r16,	0(r4)
	ldw	r17,	4(r4)
	ldw	r18,	8(r4)
	ldw	r19,	12(r4)
	ldw	r20,	16(r4)
	ldw	r21,	20(r4)
	ldw	r22,	24(r4)
	ldw	r23,	28(r4)

	ldw	r26,	32(r4)
	ldw	r27,	36(r4)
	ldw	r28,	40(r4)
	ldw	r29,	44(r4)

	bne	r5,	zero,	1f
	addi	r5,	r5,	1
1:
	mov	r2,	r5
	jmp	ra

	.set at
FUNC_END(longjmp)
