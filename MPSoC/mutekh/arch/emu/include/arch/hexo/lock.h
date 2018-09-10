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

/**
   @file ARCH lock
*/

#if !defined(LOCK_H_) || defined(ARCH_LOCK_H_)
#error This file can not be included directly
#else

#define ARCH_LOCK_H_

#include <arch/hexo/emu_syscalls.h>
#include <assert.h>
#include <hexo/atomic.h>
#include <hexo/ordering.h>

#define ARCH_HAS_ATOMIC

struct		arch_lock_s
{
  atomic_int_t	a;
};

#define ARCH_LOCK_INITIALIZER	{ .a = 0 }

static inline error_t arch_lock_init(struct arch_lock_s *lock)
{
  lock->a = 0;
  order_smp_write();
  return 0;
}

static inline void arch_lock_destroy(struct arch_lock_s *lock)
{
}

static inline bool_t arch_lock_try(struct arch_lock_s *lock)
{
  bool_t res = cpu_atomic_bit_testset(&lock->a, 0);
  order_smp_mem();
  return res;
}

static inline void arch_lock_spin(struct arch_lock_s *lock)
{
#ifdef CONFIG_DEBUG_SPINLOCK_LIMIT
  uint32_t deadline = CONFIG_DEBUG_SPINLOCK_LIMIT;

  while (cpu_atomic_bit_testset(&lock->a, 0))
    assert(deadline-- > 0);
#else
  cpu_atomic_bit_waitset(&lock->a, 0);
#endif
  order_smp_mem();
}

static inline bool_t arch_lock_state(struct arch_lock_s *lock)
{
  bool_t res = lock->a & 1;
  order_smp_read();
  return res;
}

static inline void arch_lock_release(struct arch_lock_s *lock)
{
  order_smp_mem();
  lock->a = 0;
  order_smp_write();
#if defined(CONFIG_ARCH_EMU_UNLOCK_YIELD)
  emu_do_syscall(EMU_SYSCALL_SCHED_YIELD, 0);
#endif
}

#endif

