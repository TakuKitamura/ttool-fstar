/*
    This file is part of MutekH.

    MutekH is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MutekH is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MutekH; if not, write to the Free Software Foundation,
    Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

    Copyright Alexandre Becoulet <alexandre.becoulet@free.fr> (c) 2011

*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

FUNC_START(.text, setjmp)

        sw      (r1 + 0),  r11
        sw      (r1 + 4),  r12
        sw      (r1 + 8),  r13
        sw      (r1 + 12), r14
        sw      (r1 + 16), r15
        sw      (r1 + 20), r16
        sw      (r1 + 24), r17
        sw      (r1 + 28), r18
        sw      (r1 + 32), r19
        sw      (r1 + 36), r20
        sw      (r1 + 40), r21
        sw      (r1 + 44), r22
        sw      (r1 + 48), r23
        sw      (r1 + 52), r24
        sw      (r1 + 56), r25
        sw      (r1 + 60), r26
        sw      (r1 + 64), r27
        sw      (r1 + 68), r28
        sw      (r1 + 72), r29

        xor     r1,     r1,     r1
        ret

FUNC_END(setjmp)

FUNC_START(.text, longjmp)

        lw      r11, (r1 + 0)  
        lw      r12, (r1 + 4)  
        lw      r13, (r1 + 8)  
        lw      r14, (r1 + 12) 
        lw      r15, (r1 + 16) 
        lw      r16, (r1 + 20) 
        lw      r17, (r1 + 24) 
        lw      r18, (r1 + 28) 
        lw      r19, (r1 + 32) 
        lw      r20, (r1 + 36) 
        lw      r21, (r1 + 40) 
        lw      r22, (r1 + 44) 
        lw      r23, (r1 + 48) 
        lw      r24, (r1 + 52) 
        lw      r25, (r1 + 56) 
        lw      r26, (r1 + 60) 
        lw      r27, (r1 + 64) 
        lw      r28, (r1 + 68) 
        lw      r29, (r1 + 72) 

        bne     r2, r0, 1f
        addi    r2, r2, 1
1:
        mv      r1, r2
        ret

FUNC_END(longjmp)

