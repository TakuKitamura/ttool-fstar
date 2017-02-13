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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2010

*/

#if !defined(CONTEXT_H_) || defined(CPU_CONTEXT_H_)
#error This file can not be included directly
#else

#ifndef __MUTEK_ASM__

struct cpu_context_regs_s
{
  uint64_t rdi;
  uint64_t rsi;
  uint64_t rbp;
  uint64_t rsp;
  uint64_t rbx;
  uint64_t rdx;
  uint64_t rcx;
  uint64_t rax;
  uint64_t rn[8];
  uint64_t rip;
  uint64_t rflags;
};

struct cpu_context_s
{
  uint64_t mask;
  /* sorted in iret order */
  struct cpu_context_regs_s kregs;
# ifdef CONFIG_HEXO_FPU
  __attribute__((aligned(16)))
  uint8_t mm[512];  /* fpu and multimedia state */
# endif
};

# define CPU_CONTEXT_REG_NAMES "savemask", CPU_GPREG_NAMES, "rip", "rflags"
# define CPU_CONTEXT_REG_FIRST 1
# define CPU_CONTEXT_REG_COUNT 17

#else

.extern x86emu_context
.equ CPU_X86EMU_CONTEXT_mask,    0

.equ CPU_X86EMU_CONTEXT_rdi,     8
.equ CPU_X86EMU_CONTEXT_rsi,     16
.equ CPU_X86EMU_CONTEXT_rbp,     24
.equ CPU_X86EMU_CONTEXT_rsp,     32
.equ CPU_X86EMU_CONTEXT_rbx,     40
.equ CPU_X86EMU_CONTEXT_rdx,     48
.equ CPU_X86EMU_CONTEXT_rcx,     56
.equ CPU_X86EMU_CONTEXT_rax,     64
.equ CPU_X86EMU_CONTEXT_r8,      72
.equ CPU_X86EMU_CONTEXT_r9,      80 
.equ CPU_X86EMU_CONTEXT_r10,     88 
.equ CPU_X86EMU_CONTEXT_r11,     96 
.equ CPU_X86EMU_CONTEXT_r12,     104
.equ CPU_X86EMU_CONTEXT_r13,     112
.equ CPU_X86EMU_CONTEXT_r14,     120
.equ CPU_X86EMU_CONTEXT_r15,     128
.equ CPU_X86EMU_CONTEXT_RIP,     136

.equ CPU_X86EMU_CONTEXT_RFLAGS,  144

.equ CPU_X86EMU_CONTEXT_REGS_OFFSET, 8

.equ CPU_X86EMU_CONTEXT_MM,      160

#endif

#endif

