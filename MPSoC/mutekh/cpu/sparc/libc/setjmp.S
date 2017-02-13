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

    Copyright (c) 2011 Alexandre Becoulet <alexandre.becoulet@telecom-paristech.fr>
    Copyright (c) 2011 Institut Telecom / Telecom ParisTech

*/

#include <hexo/asm.h>
#include <hexo/cpu.h>

FUNC_START(.text, setjmp)

        std     %l0, [%o0 + 0]
        std     %l2, [%o0 + 8]
        std     %l4, [%o0 + 16]
        std     %l6, [%o0 + 24]

        std     %i0, [%o0 + 32]
        std     %i2, [%o0 + 40]
        std     %i4, [%o0 + 48]

        std     %o6, [%o0 + 56] // stack & ret addr

#ifdef CONFIG_COMPILE_FRAMEPTR
        st      %i7, [%o0 + 64] // frame ptr if any
#endif

        st      %g1, [%o0 + 68]
        std     %g2, [%o0 + 72]
        st      %g4, [%o0 + 80]

        retl
        clr    %o0

FUNC_END(setjmp)

FUNC_START(.text, longjmp)

        ldd     [%o0 + 0],  %l0 
        ldd     [%o0 + 8],  %l2 
        ldd     [%o0 + 16], %l4 
        ldd     [%o0 + 24], %l6
        
        ldd     [%o0 + 32], %i0 
        ldd     [%o0 + 40], %i2 
        ldd     [%o0 + 48], %i4
        
        ldd     [%o0 + 56], %o6 // stack & ret addr

#ifdef CONFIG_COMPILE_FRAMEPTR
        ld      [%o0 + 64], %i7 // frame ptr if any
#endif

        ld      [%o0 + 68], %g1
        ldd     [%o0 + 72], %g2 
        ld      [%o0 + 80], %g4 

        tst     %o1
        bz      1f
        nop
        inc     %o1
1:
        retl
        mov     %o1, %o0

FUNC_END(longjmp)

