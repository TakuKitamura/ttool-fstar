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
  02110-1301 USA

  Copyright Nicolas Pouillon, <nipo@ssji.net>, 2010
*/

#include <string.h>

#undef memcpy

#define reg_t_log2_m1 (sizeof(reg_t)-1)

#define I(p) ((uintptr_t)(p))
#define P(i) ((void *)(i))
#define C(i) ((char *)(i))

static inline
void memcpy32(void *dst, const void *src)
{
    reg_t tmp[8];
    struct {
        uint32_t foo[8];
    } *dst_struct = dst;
    asm volatile(
        "lw %[tmp0], 0*4(%[src])   \r\n"
        "lw %[tmp1], 1*4(%[src])   \r\n"
        "lw %[tmp2], 2*4(%[src])   \r\n"
        "lw %[tmp3], 3*4(%[src])   \r\n"
        "lw %[tmp4], 4*4(%[src])   \r\n"
        "lw %[tmp5], 5*4(%[src])   \r\n"
        "lw %[tmp6], 6*4(%[src])   \r\n"
        "lw %[tmp7], 7*4(%[src])   \r\n"
        "sw %[tmp0], 0*4(%[dst])   \r\n"
        "sw %[tmp1], 1*4(%[dst])   \r\n"
        "sw %[tmp2], 2*4(%[dst])   \r\n"
        "sw %[tmp3], 3*4(%[dst])   \r\n"
        "sw %[tmp4], 4*4(%[dst])   \r\n"
        "sw %[tmp5], 5*4(%[dst])   \r\n"
        "sw %[tmp6], 6*4(%[dst])   \r\n"
        "sw %[tmp7], 7*4(%[dst])   \r\n"
        : [tmp0] "=&r" (tmp[0]), [tmp1] "=&r" (tmp[1])
        , [tmp2] "=&r" (tmp[2]), [tmp3] "=&r" (tmp[3])
        , [tmp4] "=&r" (tmp[4]), [tmp5] "=&r" (tmp[5])
        , [tmp6] "=&r" (tmp[6]), [tmp7] "=&r" (tmp[7])
        , "=m" (*dst_struct)
        : [dst] "r" (dst)
        , [src] "r" (src)
        );
}

static inline
size_t memcpy_reg(reg_t *dst, const reg_t *src, size_t size)
{
    size_t done = 0;

    if ( ! (I(dst) & reg_t_log2_m1)
         && ! (I(src) & reg_t_log2_m1) )
        while (size >= sizeof(reg_t)) {
            *dst++ = *src++;
            size -= sizeof(reg_t);
            done += sizeof(reg_t);
        }
    return done;
}

static inline
void memcpy1(uint8_t *dst, const uint8_t *src, size_t size)
{
    while (size--)
        *dst++ = *src++;
}

void *
memcpy(void *_dst, const void *_src, size_t size)
{
    void *ret = _dst;

    if ( size > 32
         && (I(_dst) & 3) == (I(_src) & 3) ) {
        char *dstc = _dst;
        const char *srcc = _src;
        while ( I(dstc) & 3 ) {
            *dstc++ = *srcc++;
            size--;
        }

        while ( size >= 32 ) {
            memcpy32(dstc, srcc);
            dstc += 32;
            srcc += 32;
            size -= 32;
        }
        _dst = dstc;
        _src = srcc;
    }

    size_t done = memcpy_reg(_dst, _src, size);
    _dst = P(I(_dst)+done);
    _src = P(I(_src)+done);
    size -= done;

    memcpy1(_dst, _src, size);

    return ret;
}
