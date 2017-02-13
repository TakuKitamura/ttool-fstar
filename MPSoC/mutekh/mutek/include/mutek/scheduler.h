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

#ifndef MUTEK_SCHEDULER_H_
#define MUTEK_SCHEDULER_H_

/**
 * @file
 * @module{Mutek}
 * @short Kernel execution context scheduler
 */

#include <hexo/decls.h>
#include <hexo/context.h>

struct sched_context_s;

/** scheduler context candidate checking function */
#define SCHED_CANDIDATE_FCN(n) bool_t (n)(struct sched_context_s *sched_ctx)

/** scheduler context candidate checking function type */
typedef SCHED_CANDIDATE_FCN(sched_candidate_fcn_t);


#ifdef CONFIG_MUTEK_SCHEDULER

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/cont_dlist.h>

# define CONTAINER_LOCK_sched_queue HEXO_SPIN

struct sched_context_s
{
  CONTAINER_ENTRY_TYPE(DLIST) list_entry;
  struct scheduler_s *scheduler;		//< keep track of associated scheduler queue
  struct context_s	context;	//< execution context

  void			*priv;

# ifdef CONFIG_MUTEK_SCHEDULER_MIGRATION_AFFINITY
  cpu_bitmap_t		cpu_map;
# endif

# ifdef CONFIG_MUTEK_SCHEDULER_CANDIDATE_FCN
  sched_candidate_fcn_t	*is_candidate;
# endif

};

CONTAINER_TYPE       (sched_queue, DLIST, struct sched_context_s, list_entry);

CONTAINER_FUNC       (sched_queue, DLIST, static inline, sched_queue, list_entry);
CONTAINER_FUNC_NOLOCK(sched_queue, DLIST, static inline, sched_queue_nolock, list_entry);

# define SCHED_QUEUE_INITIALIZER CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST)

/** @internal */
extern CONTEXT_LOCAL struct sched_context_s *sched_cur;
/** @internal */
extern CPU_LOCAL struct sched_context_s sched_idle;

#else
typedef struct __empty_s sched_queue_root_t;
#endif

/** @This returns the current scheduler context */
config_depend_inline(CONFIG_MUTEK_SCHEDULER,
struct sched_context_s *sched_get_current(void),
{
  return CONTEXT_LOCAL_GET(sched_cur);
});

/** @This returns a cpu local context for temporary stack use with
    @ref cpu_context_stack_use, useful to run other context
    exit/destroy. The processor idle context is actually returned. This
    context must be used with interrupts disabled. */
config_depend_inline(CONFIG_MUTEK_SCHEDULER,
struct context_s * sched_tmp_context(void),
{
  return &CPU_LOCAL_ADDR(sched_idle)->context;
});

/** @This is a context preemption handler.

    @This pushes current context back on running queue and returns
    next scheduler candidate. If no other context is available on
    running queue, this function does nothing and return @tt NULL.

    @see context_set_preempt @see #CONTEXT_PREEMPT @see sched_context_switch */
config_depend(CONFIG_MUTEK_SCHEDULER)
CONTEXT_PREEMPT(sched_preempt_switch);

/** @This is a context preemption handler.

    @This returns next scheduler candidate or processor idle context
    if none is available. Current context is not pushed back on
    running queue.

    @see context_set_preempt @see #CONTEXT_PREEMPT @see sched_context_stop */
config_depend(CONFIG_MUTEK_SCHEDULER)
CONTEXT_PREEMPT(sched_preempt_stop);

/** @This is a context preemption handler.

    @This pushes current context on wait queue passed as preempt
    handler parameter, unlock the queue and finally returns next
    scheduler candidate or processor idle context if none is
    available.

    @see context_set_preempt @see #CONTEXT_PREEMPT @see sched_wait_unlock */
config_depend(CONFIG_MUTEK_SCHEDULER)
CONTEXT_PREEMPT(sched_preempt_wait_unlock);


/** @This initializes scheduler context. The @ref context_init function must
    have been called before on @ref sched_context_s::context. */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_context_init(struct sched_context_s *sched_ctx);

/** @This switches to next context. Must be called with interrupts
    disabled. @see sched_preempt_switch */
config_depend_inline(CONFIG_MUTEK_SCHEDULER,
void sched_context_switch(void),
{
  struct context_s *next = sched_preempt_switch(NULL);

  if (next)
    context_switch_to(next);
});

/** @This jumps to next context without saving current context. current
    context will be lost. Must be called with interrupts disabled and
    main sched queue locked. @see sched_preempt_stop */
config_depend_inline(CONFIG_MUTEK_SCHEDULER,
void sched_context_exit(void),
{
  context_jump_to(sched_preempt_stop(NULL));
});

/** @This pushes current context in the 'queue', unlock it and switch
   to next context available in the 'root' queue. Must be called with
   interrupts disabled. @see sched_preempt_wait_unlock */
config_depend_inline(CONFIG_MUTEK_SCHEDULER,
void sched_wait_unlock(sched_queue_root_t *queue),
{
  context_switch_to(sched_preempt_wait_unlock(queue));
});

/** @This enqueues scheduler context for execution. Must be called
    with interrupts disabled */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_context_start(struct sched_context_s *sched_ctx);

/** @This switches to next context without pushing current context
    back in running queue and unlock passed scheduler queue. @see
    sched_context_stop */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_stop_unlock(lock_t *lock);

/** @This locks context queue. */
config_depend(CONFIG_MUTEK_SCHEDULER)
error_t sched_queue_lock(sched_queue_root_t *queue);

/** @This unlocks context queue. */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_queue_unlock(sched_queue_root_t *queue);

/** @This initializes context queue. */
config_depend(CONFIG_MUTEK_SCHEDULER)
error_t sched_queue_init(sched_queue_root_t *queue);

/** @This frees resources associated with context queue. */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_queue_destroy(sched_queue_root_t *queue);

typedef void (sched_wait_cb_t)(void *ctx);

/** @This removes first context from passed queue and push it back in
    running queue.  @This returns a pointer to context or NULL if
    queue was empty. @This Must be called with interrupts disabled and
    queue locked. */
config_depend(CONFIG_MUTEK_SCHEDULER)
struct sched_context_s *sched_wake(sched_queue_root_t *queue);

/** @This function removes a given context from passed queue and push it back in
    running queue. @This Must be called with interrupts disabled and
    queue locked. */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_context_wake(sched_queue_root_t *queue, struct sched_context_s *sched_ctx);

/** @internal @This function performs scheduler intialization, must be
    called once. */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_global_init(void);

/** @internal @This function performs scheduler intialization, must be
    called for each processor. */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_cpu_init(void);

/** @This function set processor affinity sothat scheduler context
    will run on this cpu */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_affinity_add(struct sched_context_s *sched_ctx, cpu_id_t cpu);

/** @This function set processor affinity sothat scheduler context
    will not run on this cpu */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_affinity_remove(struct sched_context_s *sched_ctx, cpu_id_t cpu);

/** @This function set processor affinity sothat scheduler context
    will run on a single cpu */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_affinity_single(struct sched_context_s *sched_ctx, cpu_id_t cpu);

/** @This function set processor affinity sothat scheduler context
    will run on all cpu */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_affinity_all(struct sched_context_s *sched_ctx);

/** @This function set processor affinity sothat scheduler context
    will run on all cpu */
config_depend(CONFIG_MUTEK_SCHEDULER)
void sched_affinity_clear(struct sched_context_s *sched_ctx);

/** @This function setups a scheduler context candidate checking
    function. */
config_depend(CONFIG_MUTEK_SCHEDULER_CANDIDATE_FCN)
void sched_context_candidate_fcn(struct sched_context_s *sched_ctx, sched_candidate_fcn_t *fcn);

#endif

