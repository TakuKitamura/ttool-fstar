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
 * @file
 * @module{Hexo}
 * @short Interrupts, exceptions and syscall events management
 */

#ifndef INTERRUPT_H_
#define INTERRUPT_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

#ifndef __MUTEK_ASM__

#include "local.h"
#include "types.h"

/************************************************************ hw irq */

# ifdef CONFIG_HEXO_IRQ

/** CPU interrupt handler function template
    @see cpu_interrupt_handler_t
    @showcontent
*/
# define CPU_INTERRUPT_HANDLER(n) void (n) (uint_fast8_t irq)

/** CPU interrupt handler function type.

    @param irq Highest priority pending processor irq line number.

    @see #CPU_INTERRUPT_HANDLER
*/
typedef CPU_INTERRUPT_HANDLER(cpu_interrupt_handler_t);

/** @this sets the hardware interrupt handler for the current cpu */
void cpu_interrupt_sethandler(cpu_interrupt_handler_t *handler);

extern CPU_LOCAL struct device_s *cpu_interrupt_handler_dev;

struct device_s;

/** @this sets hardware interrupt handler device for the current cpu */
void cpu_interrupt_sethandler_device(struct device_s *dev);
# endif


/** @this disables all maskable interrupts for the current cpu.
    This acts as a compiler memory barrier. */
__attribute__ ((always_inline))
static inline void cpu_interrupt_disable();

/** @this enables all maskable interrupts for the current cpu.
    This acts as a compiler memory barrier. */
__attribute__ ((always_inline))
static inline void cpu_interrupt_enable();

/** @this saves interrupts enable state (may use stack) */
__attribute__ ((always_inline))
static inline void cpu_interrupt_savestate(reg_t *state);

/** @this saves interrupts enable state end disable interrupts.
    This acts as a compiler memory barrier. */
__attribute__ ((always_inline))
static inline void cpu_interrupt_savestate_disable(reg_t *state);

/** @this restores interrupts enable state (may use stack).
    This acts as a compiler memory barrier. */
__attribute__ ((always_inline))
static inline void cpu_interrupt_restorestate(const reg_t *state);

/** @this reads current interrupts state as boolean */
__attribute__ ((always_inline))
static inline bool_t cpu_interrupt_getstate();

/** @this checks if the cpu is interruptible */
__attribute__ ((always_inline))
static inline bool_t cpu_is_interruptible();

/** @this enables interrupts and give a chance to pending requests to
    execute. This function must be used to avoid the "sti; cli"
    sequence which don't let interrupts raise on some
    processors. Memory is marked as clobbered by this function to
    force global variable reload after interrupts occured. */
__attribute__ ((always_inline))
static inline void cpu_interrupt_process();

# ifdef CONFIG_CPU_WAIT_IRQ
/** @this enables interrupts and enters in interrupt wait state. The
    @ref #CONFIG_CPU_WAIT_IRQ token may be used to check for
    availability.  */
__attribute__ ((always_inline))
static inline void cpu_interrupt_wait();
# endif

/** @showcontent
    @this saves interrupts enable state end disable interrupts. This macro
    must be matched with the @ref #CPU_INTERRUPT_RESTORESTATE macro.
    This acts as a compiler memory barrier. */
#define CPU_INTERRUPT_SAVESTATE_DISABLE				\
{								\
  reg_t	__interrupt_state;					\
  cpu_interrupt_savestate_disable(&__interrupt_state);

/** @showcontent
    @this restores interrupts enable state. This macro must be matched with
    the @ref #CPU_INTERRUPT_SAVESTATE_DISABLE macro. 
    This acts as a compiler memory barrier. */
#define CPU_INTERRUPT_RESTORESTATE				\
  cpu_interrupt_restorestate(&__interrupt_state);		\
}


/************************************************************ exceptions */

struct cpu_context_s;

/** CPU exception handler function template
    @see cpu_exception_handler_t
    @showcontent
*/
# define CPU_EXCEPTION_HANDLER(n) void (n) (uint_fast8_t type, uintptr_t execptr, \
                                            uintptr_t dataptr, struct cpu_context_s *regs, \
                                            uintptr_t stackptr)
/**
   This function is called when a fault exception occurs.

   @param type Exception ID which is processor specific.
   @param execptr Pointer to faulty instruction.

   @param dataptr Bad memory data access address, validity depends on
    processor and fault.

   @param regs Can be used with the @ref cpu_exception_set_resume
    function. Some general purpose registers may be saved depending on
    processor.

   @param stackptr Value of stack pointer.

   A context local handler may be defined for each @ref context_s
   instance and will be used when a fault occurs in user mode. The cpu
   local handler is used for other cases.

   @see #CPU_EXCEPTION_HANDLER @see cpu_exception_set_resume
*/
typedef CPU_EXCEPTION_HANDLER(cpu_exception_handler_t);


/** Set exception interrupt handler for the current cpu */
void cpu_exception_sethandler(cpu_exception_handler_t *hndl);

/** Change exception resume addess. This function must only be called
    from an exception or syscall handler. */
void cpu_exception_resume_pc(struct cpu_context_s *regs, uintptr_t pc);

# ifdef CONFIG_HEXO_USERMODE

/** Set user exception interrupt handler for the current context */
void cpu_user_exception_sethandler(cpu_exception_handler_t *hndl);

struct context_s;
/** Set user exception interrupt handler for the given context */
void cpu_user_exception_sethandler_ctx(struct context_s *context,
				       cpu_exception_handler_t *hndl);
# endif

/************************************************************ syscalls */

#ifdef CONFIG_HEXO_USERMODE

/** CPU syscall handler function template
    @see cpu_syscall_handler_t
    @showcontent
*/
# define CPU_SYSCALL_HANDLER(n) void (n) (uint_fast8_t number, struct cpu_context_s *regs)

/**
    This function is called when a syscall trap is generated by
    execution of a software interrupt instruction in user mode.
    Syscall traps from processor kernel mode have undefined behavior.

    @param number Processor specific trap number. Some processors only
     have one trap (0).

    @param regs Contains valid values for argument passing registers
     on current processor ABI and can be used with the @ref
     cpu_exception_set_resume function.  The return value register is
     restored from this object on syscall return.

    Syscall handler is context local and must be defined for each @ref
    context_s instance.

    @see #CPU_SYSCALL_HANDLER
*/
typedef CPU_SYSCALL_HANDLER(cpu_syscall_handler_t);
struct context_s;

/** @this sets the syscall handler for the current @ref context_s. */
void cpu_syscall_sethandler(cpu_syscall_handler_t *hndl);

/** @this sets syscall interrupt handler for an other @ref context_s. */
void cpu_syscall_sethandler_ctx(struct context_s *context,
				cpu_syscall_handler_t *hndl);

#endif

/************************************************************/

#endif  /* __MUTEK_ASM__ */

# include "cpu/hexo/interrupt.h"

C_HEADER_END

#endif

