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

#include <hexo/cpu.h>

#include <hexo/interrupt.h>
#include <hexo/local.h>
#include <hexo/atomic.h>
#include <arch/hexo/emu_syscalls.h>

typedef void (*__sighandler_t)(__compiler_sint_t);

extern __compiler_sint_t cpu_pids[CONFIG_CPU_MAXCOUNT];

/* special case for emu: CPU_LOCAL does not require
   access macro when never accessed from other cpus.
   This allow use before cpu_init() */
static CPU_LOCAL bool_t irq_state = 0;

extern CPU_LOCAL cpu_interrupt_handler_t  *cpu_interrupt_handler;

static void emu_interrupts_sig(__compiler_sint_t sig)
{
  cpu_interrupt_handler_t *hndl = CPU_LOCAL_GET(cpu_interrupt_handler);
  hndl(sig != EMU_SIG_USR1);
}

/* Linux sigaction */
struct sigaction_s {
  void               (*hndl)(__compiler_sint_t sig);
  __compiler_ulong_t flags;
  void *             restorer;
  __compiler_ulong_t set[64 / (8 * sizeof(__compiler_ulong_t))];
};

static const struct sigaction_s sig_usr_action = {
  .hndl = &emu_interrupts_sig,
  .flags = EMU_SIG_SA_RESTART,
  .set = { [0] = (1 << (EMU_SIG_USR1-1)) | (1 << (EMU_SIG_USR2-1)) }
};

void emu_interrupts_post(cpu_id_t cpu, uint_fast8_t irq)
{
  emu_do_syscall(EMU_SYSCALL_KILL, 2, cpu_pids[cpu],
                 irq % 2 ? EMU_SIG_USR1 : EMU_SIG_USR2);
}

void emu_interrupts_set(bool_t state)
{
  if (state != irq_state)
    {
      emu_do_syscall(EMU_SYSCALL_RT_SIGPROCMASK, 4,
                     state ? EMU_SIG_UNBLOCK : EMU_SIG_BLOCK,
                     &sig_usr_action.set, 0, sizeof(sig_usr_action.set));
      irq_state = state;
      order_compiler_mem();
    }
}

bool_t emu_interrupts_get(void)
{
  order_compiler_mem();
  return irq_state;
}

void emu_interrupts_wait(void)
{
  emu_do_syscall(EMU_SYSCALL_RT_SIGTIMEDWAIT, 4, &sig_usr_action.set, 0, 0, sizeof(sig_usr_action.set));
}

void emu_interrupts_init(void)
{
  irq_state = 0;
  emu_do_syscall(EMU_SYSCALL_RT_SIGPROCMASK, 4, EMU_SIG_BLOCK, &sig_usr_action.set, 0, sizeof(sig_usr_action.set));
  emu_do_syscall(EMU_SYSCALL_RT_SIGACTION, 4, EMU_SIG_USR1, &sig_usr_action, 0, sizeof(sig_usr_action.set));
  emu_do_syscall(EMU_SYSCALL_RT_SIGACTION, 4, EMU_SIG_USR2, &sig_usr_action, 0, sizeof(sig_usr_action.set));
}

