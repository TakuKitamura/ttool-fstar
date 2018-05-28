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

#ifndef PTHREAD_H_
#define PTHREAD_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

/**
 * @file
 * @module{Pthread library}
 */

#ifndef CONFIG_PTHREAD
# warning pthread support is not enabled in configuration file
#else

#include <hexo/types.h>
#include <hexo/error.h>
#include <hexo/local.h>
#include <hexo/lock.h>
#include <hexo/atomic.h>
#include <hexo/context.h>
#include <hexo/ordering.h>
#include <mutek/scheduler.h>
#include <hexo/interrupt.h>

/************************************************************************
		PThread types
************************************************************************/

typedef void * pthread_start_routine_t(void *arg);

struct pthread_s;
typedef struct pthread_s * pthread_t;

struct pthread_attr_s;
typedef struct pthread_attr_s pthread_attr_t;


/************************************************************************
		PThread private objects
************************************************************************/

/** @internal pointer to current thread */
extern CONTEXT_LOCAL pthread_t __pthread_current;

/************************************************************************
		PThread Thread related public API
************************************************************************/

/** @this set a context local variable for the given pthread */
#define CONTEXT_LOCAL_PTHREAD_SET(th, n, v) \
  CONTEXT_LOCAL_TLS_SET((th)->sched_ctx.context.tls, n, v)

/** @this read a context local variable for the given pthread */
#define CONTEXT_LOCAL_PTHREAD_GET(th, n) \
  CONTEXT_LOCAL_TLS_GET((th)->sched_ctx.context.tls, n)

/** @internal pthread descriptor structure */
struct pthread_s
{
  /** context */
  struct sched_context_s	sched_ctx;

  /* thread state flags (detached, joinable, canceled ...) */
  atomic_t                      state;
  lock_t lock;

#ifdef CONFIG_PTHREAD_JOIN
  /** pointer to thread waiting for termination */
  struct pthread_s              *joined;
  /** joined thread exit value */
  void				*joined_retval;
#endif

  /** start routine argument */
  void				*arg;
  /** start routine pointer */
  pthread_start_routine_t	*start_routine;
};

#define _PTHREAD_STATE_DETACHED         0 //< thread is marked as detached
#define _PTHREAD_STATE_JOINABLE         1 //< thread is joinable
#define _PTHREAD_STATE_CANCELED         2 //< thread has been canceled
#define _PTHREAD_STATE_NOCANCEL         3 //< thread ignore cancel
#define _PTHREAD_STATE_CANCELASYNC      4 //< thread use asynchronous cancelation
#define _PTHREAD_STATE_TIMEDWAIT        5 //< thread can be woken up by timer callback
#define _PTHREAD_STATE_TIMEOUT          6 //< thread timed wait has reached timeout

#define _PTHREAD_ATTRFLAG_AFFINITY	0x01
#define _PTHREAD_ATTRFLAG_STACK		0x02
#define _PTHREAD_ATTRFLAG_DETACHED	0x04

/** @internal pthread attributes structure */
struct pthread_attr_s
{
#ifdef CONFIG_PTHREAD_ATTRIBUTES
  uint8_t flags;
  cpu_id_t cpucount;
  cpu_id_t cpulist[CONFIG_CPU_MAXCOUNT];
  void *stack_buf;
  size_t stack_size;
#endif
};

/** @this creates a new pthread attribute */
config_depend(CONFIG_PTHREAD_ATTRIBUTES)
error_t pthread_attr_init(pthread_attr_t *attr);

/** @this destroys a new pthread attribute */
config_depend(CONFIG_PTHREAD_ATTRIBUTES)
error_t pthread_attr_destroy(pthread_attr_t *attr);

/** @this adds a cpu affinity attribute */
config_depend(CONFIG_PTHREAD_ATTRIBUTES)
error_t pthread_attr_affinity(pthread_attr_t *attr, cpu_id_t cpu);

/** @this sets stack buffer and size attribute */
config_depend(CONFIG_PTHREAD_ATTRIBUTES)
error_t pthread_attr_setstack(pthread_attr_t *attr, void *stack_buf, size_t stack_size);

/** @this sets stack size attribute */
config_depend(CONFIG_PTHREAD_ATTRIBUTES)
error_t pthread_attr_setstacksize(pthread_attr_t *attr, size_t stack_size);

/** @see pthread_attr_setdetachstate */
#define PTHREAD_CREATE_DETACHED 1
/** @see pthread_attr_setdetachstate */
#define PTHREAD_CREATE_JOINABLE 0

/** @this set initial thread state */
config_depend(CONFIG_PTHREAD_ATTRIBUTES)
error_t pthread_attr_setdetachstate(pthread_attr_t *attr, uint8_t state);

/** @this creates a new pthread */
error_t
pthread_create(pthread_t *thread, const pthread_attr_t *attr,
	       pthread_start_routine_t *start_routine, void *arg);

/** @this ends pthread execution */
void pthread_exit(void *retval);

/** @this returns current pthread */
static inline pthread_t
pthread_self(void)
{
  return CONTEXT_LOCAL_GET(__pthread_current);
}

/** @this switchs to next thread */
static inline void
pthread_yield(void)
{
  void __pthread_switch(void);
  __pthread_switch();
}

/** @this compare two thread objects */
static inline error_t
pthread_equal(pthread_t t1, pthread_t t2)
{
  return t1 == t2;
}

/** @this detachs a pthread */
config_depend(CONFIG_PTHREAD_JOIN)
error_t
pthread_detach(pthread_t thread);

/** @this waits for thread termination */
config_depend(CONFIG_PTHREAD_JOIN)
error_t
pthread_join(pthread_t thread, void **value_ptr);

/************************************************************************
		PThread Cancelation related public API
************************************************************************/

/** canceled thread exit value */
#define PTHREAD_CANCELED		((void*)-1)

/** @multiple @this may be used with @ref pthread_setcancelstate */
#define PTHREAD_CANCEL_DISABLE		0
#define PTHREAD_CANCEL_ENABLE		1

/** @multiple @this may be used with @ref pthread_setcanceltype */
#define PTHREAD_CANCEL_DEFERRED		0
#define PTHREAD_CANCEL_ASYNCHRONOUS	1

/** @internal */
typedef void __pthread_cleanup_fcn_t(void*);

/** @internal cancelation cleanup context */
config_depend(CONFIG_PTHREAD_CANCEL)
struct __pthread_cleanup_s
{
  __pthread_cleanup_fcn_t	*fcn;
  void				*arg;
  /* pointer to previous cancelation context */
  struct __pthread_cleanup_s	*prev;
};

/** @internal cleanup context linked list */
extern CONTEXT_LOCAL struct __pthread_cleanup_s *__pthread_cleanup_list;

/** @this must be matched with @ref #pthread_cleanup_pop */
#define pthread_cleanup_push(routine_, arg_)		\
{							\
  reg_t				__irq_state;            \
  cpu_interrupt_savestate_disable(&__irq_state);	\
							\
  const struct __pthread_cleanup_s	__cleanup =	\
    {							\
      .fcn = (routine_),				\
      .arg = (arg_),					\
      .prev = CONTEXT_LOCAL_GET(__pthread_cleanup_list),	\
    };							\
							\
  CONTEXT_LOCAL_SET(__pthread_cleanup_list, &__cleanup);	\
							\
  cpu_interrupt_restorestate(&__irq_state);

/** @this must be matched with @ref #pthread_cleanup_push */
#define pthread_cleanup_pop(execute)				\
  cpu_interrupt_savestate_disable(&__irq_state);		\
								\
  if (execute)							\
    __cleanup.fcn(__cleanup.arg);				\
								\
  CONTEXT_LOCAL_SET(__pthread_cleanup_list, __cleanup.prev);	\
								\
  cpu_interrupt_restorestate(&__irq_state);			\
}

config_depend_inline(CONFIG_PTHREAD_CANCEL,
void pthread_testcancel(void),
{
  void __pthread_cancel_self(void);

  if (atomic_bit_test(&pthread_self()->state, _PTHREAD_STATE_CANCELED))
    __pthread_cancel_self();
});

config_depend(CONFIG_PTHREAD_CANCEL)
error_t
pthread_setcancelstate(int_fast8_t state, int_fast8_t *oldstate);

config_depend(CONFIG_PTHREAD_CANCEL)
error_t
pthread_setcanceltype(int_fast8_t type, int_fast8_t *oldtype);

config_depend_inline(CONFIG_PTHREAD_CANCEL,
error_t pthread_cancel(pthread_t thread),
{
  return atomic_bit_testset(&thread->state, _PTHREAD_STATE_CANCELED);
});

/************************************************************************
		PThread Mutex related public API
************************************************************************/

typedef struct pthread_mutexattr_s pthread_mutexattr_t;

/** @internal Mutex object structure */
struct				pthread_mutex_s
{
  /** mutex counter */
  uint_fast8_t				count;

# ifdef CONFIG_PTHREAD_MUTEX_ATTR
  /** mutex attributes */
  const struct pthread_mutexattr_s	*attr;

  /** owner thread */
  struct sched_context_s		*owner;
# endif

  /** blocked threads wait queue */
  sched_queue_root_t			wait;
};

typedef struct pthread_mutex_s pthread_mutex_t;

/** @internal mutex attributes structure */
struct				pthread_mutexattr_s
{
  /** @internal pointers to lock/trylock/unlock actions depends on type */
  struct {
    error_t (*mutex_lock)	(pthread_mutex_t *mutex);
    error_t (*mutex_trylock)	(pthread_mutex_t *mutex);
    error_t (*mutex_unlock)	(pthread_mutex_t *mutex);
  }				type;
};

/** Normal mutex type identifier */
#  define PTHREAD_MUTEX_NORMAL		0
/** Error checking mutex type identifier */
#  define PTHREAD_MUTEX_ERRORCHECK	1
/** Recurvive mutex type identifier */
#  define PTHREAD_MUTEX_RECURSIVE	2
/** Default mutex type identifier */
#  define PTHREAD_MUTEX_DEFAULT		3

/** @multiple @internal */
extern pthread_mutexattr_t __pthread_mutex_attr_normal;
extern pthread_mutexattr_t __pthread_mutex_attr_errorcheck;
extern pthread_mutexattr_t __pthread_mutex_attr_recursive;

# ifdef CONFIG_PTHREAD_MUTEX_ATTR

/** @this is the normal mutex object static initializer */
#  define PTHREAD_MUTEX_INITIALIZER						       \
  {										       \
    .wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST), \
    .attr = &__pthread_mutex_attr_normal					       \
  }

/** @this is the recurvive mutex object static initializer */
#  define PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP				      \
  {										       \
    .wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST), \
    .attr = &__pthread_mutex_attr_recursive						\
  }

/** @this is error checking mutex object static initializer */
#  define PTHREAD_ERRORCHECK_MUTEX_INITIALIZER_NP				       \
  {										       \
    .wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST), \
    .attr = &__pthread_mutex_attr_errorcheck					       \
  }

# else

#  define PTHREAD_MUTEX_INITIALIZER						       \
  {										       \
    .wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST), \
  }

#  define PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP				      \
  error_PTHREAD_RECURSIVE_MUTEX_require_CONFIG_PTHREAD_MUTEX_ATTR

#  define PTHREAD_ERRORCHECK_MUTEX_INITIALIZER_NP				      \
  error_PTHREAD_ERRORCHECK_MUTEX_require_CONFIG_PTHREAD_MUTEX_ATTR

# endif

/** @this initializes a mutex */
config_depend(CONFIG_PTHREAD_MUTEX)
error_t
pthread_mutex_init(pthread_mutex_t *mutex,
		   const pthread_mutexattr_t *attr);

/** @this destroy a mutex */
config_depend(CONFIG_PTHREAD_MUTEX)
error_t
pthread_mutex_destroy(pthread_mutex_t *mutex);

# ifdef CONFIG_PTHREAD_MUTEX_ATTR

/** @this takes a mutex */
static inline error_t
pthread_mutex_lock(pthread_mutex_t *mutex)
{
  return mutex->attr->type.mutex_lock(mutex);
}

/** @this tries to take a mutex */
static inline error_t
pthread_mutex_trylock(pthread_mutex_t *mutex)
{
  return mutex->attr->type.mutex_trylock(mutex);
}

/** @this free a mutex */
static inline error_t
pthread_mutex_unlock(pthread_mutex_t *mutex)
{
  return mutex->attr->type.mutex_unlock(mutex);
}

/** @this sets the mutek type */
config_depend(CONFIG_PTHREAD_MUTEX_ATTR)
error_t
pthread_mutexattr_settype(pthread_mutexattr_t *attr, int_fast8_t type);

/** @this initialize a mutex attribute object */
config_depend_inline(CONFIG_PTHREAD_MUTEX_ATTR,
error_t pthread_mutexattr_init(pthread_mutexattr_t *attr),
{
  return pthread_mutexattr_settype(attr, PTHREAD_MUTEX_DEFAULT);
});

/** @this destroy a mutex attribute object */
config_depend_inline(CONFIG_PTHREAD_MUTEX_ATTR,
error_t pthread_mutexattr_destroy(pthread_mutexattr_t *attr),
{
  return 0;
});

# else /* !CONFIG_PTHREAD_MUTEX_ATTR */

config_depend_inline(CONFIG_PTHREAD_MUTEX,
error_t pthread_mutex_lock(pthread_mutex_t *mutex),
{
  error_t __pthread_mutex_normal_lock(pthread_mutex_t *mutex);
  return __pthread_mutex_normal_lock(mutex);
});

config_depend_inline(CONFIG_PTHREAD_MUTEX,
error_t pthread_mutex_trylock(pthread_mutex_t *mutex),
{
  error_t __pthread_mutex_normal_trylock(pthread_mutex_t *mutex);
  return __pthread_mutex_normal_trylock(mutex);
});

config_depend_inline(CONFIG_PTHREAD_MUTEX,
error_t pthread_mutex_unlock(pthread_mutex_t *mutex),
{
  error_t __pthread_mutex_normal_unlock(pthread_mutex_t *mutex);
  return __pthread_mutex_normal_unlock(mutex);
});

# endif

/************************************************************************
		PThread Cond related public API
************************************************************************/

/** @internal */
struct pthread_cond_s
{
  /** blocked threads wait queue */
  sched_queue_root_t		wait;
};

typedef struct pthread_cond_s pthread_cond_t;
typedef struct pthread_condattr_s pthread_condattr_t;

config_depend(CONFIG_PTHREAD_COND)
error_t
pthread_cond_init(pthread_cond_t *cond,
		  const pthread_condattr_t *attr);

config_depend(CONFIG_PTHREAD_COND)
error_t
pthread_cond_destroy(pthread_cond_t *cond);

config_depend(CONFIG_PTHREAD_COND)
error_t
pthread_cond_broadcast(pthread_cond_t *cond);

config_depend(CONFIG_PTHREAD_COND)
error_t
pthread_cond_signal(pthread_cond_t *cond);

struct timespec;

config_depend_and2(CONFIG_PTHREAD_COND, CONFIG_PTHREAD_TIME)
error_t
pthread_cond_timedwait(pthread_cond_t *cond, 
		       pthread_mutex_t *mutex,
		       const struct timespec *delay);

config_depend(CONFIG_PTHREAD_COND)
error_t
pthread_cond_wait(pthread_cond_t *cond, pthread_mutex_t *mutex);

/** normal cond object static initializer */
# define PTHREAD_COND_INITIALIZER						       \
  {										       \
    .wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST), \
  }

/************************************************************************
		PThread RWLock related public API
************************************************************************/

#include <mutek/rwlock.h>

/** @internal */
struct rwlock_s;

typedef struct pthread_rwlockattr_s pthread_rwlockattr_t;
typedef struct rwlock_s pthread_rwlock_t;

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_destroy(pthread_rwlock_t *rwlock),
{
  return rwlock_destroy(rwlock);
});

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_init(pthread_rwlock_t *rwlock,
                            const pthread_rwlockattr_t *attr),
{
  return rwlock_init(rwlock);
});

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_rdlock(pthread_rwlock_t *rwlock),
{
# ifdef CONFIG_PTHREAD_CANCEL
  pthread_testcancel();
# endif
  return rwlock_rdlock(rwlock);
});

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_tryrdlock(pthread_rwlock_t *rwlock),
{
  return rwlock_tryrdlock(rwlock);
});

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_wrlock(pthread_rwlock_t *rwlock),
{
# ifdef CONFIG_PTHREAD_CANCEL
  pthread_testcancel();
# endif
  return rwlock_wrlock(rwlock);
});

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_trywrlock(pthread_rwlock_t *rwlock),
{
  return rwlock_trywrlock(rwlock);
});

config_depend_inline(CONFIG_PTHREAD_RWLOCK,
error_t pthread_rwlock_unlock(pthread_rwlock_t *rwlock),
{
  return rwlock_unlock(rwlock);
});

/** normal rwlock object static initializer */
# define PTHREAD_RWLOCK_INITIALIZER RWLOCK_INITIALIZER

/************************************************************************
		PThread barrier related public API
************************************************************************/

typedef struct pthread_barrierattr_s pthread_barrierattr_t;

/** @internal */
struct				pthread_barrier_s
{
#ifdef CONFIG_PTHREAD_BARRIER_ATTR
  const struct _pthread_barrier_func_s *funcs;
#endif

  union {
    struct {
      int_fast32_t				count;
      /** blocked threads waiting for read */
      sched_queue_root_t			wait;
    } normal;
#ifdef CONFIG_PTHREAD_BARRIER_SPIN
    struct {
      int_fast32_t			max_count;
      atomic_t				count;
# ifdef CONFIG_CPU_CACHE_LINE
      /** The padding is here to avoid 
       ** cache line conflicts between count and release */
      uint8_t                           padding[CONFIG_CPU_CACHE_LINE];
# endif
      uint8_t				release;
    } spin;
#endif
  };
};

#ifdef CONFIG_PTHREAD_BARRIER_ATTR

/** @internal pointers to wait action depends on type */
struct _pthread_barrier_func_s {
  error_t (*barrier_init)	(struct pthread_barrier_s *barrier, unsigned count);
  error_t (*barrier_wait)	(struct pthread_barrier_s *barrier);
  error_t (*barrier_destroy)	(struct pthread_barrier_s *barrier);
};

struct _pthread_barrier_func_s barrier_normal_funcs;

# define PTHREAD_BARRIER_DEFAULT 0

# ifdef CONFIG_PTHREAD_BARRIER_SPIN
#  define PTHREAD_BARRIER_SPIN    1
# endif

/** @internal barrier attributes structure */
struct pthread_barrierattr_s
{
  const struct _pthread_barrier_func_s *funcs;
};

config_depend(CONFIG_PTHREAD_BARRIER_ATTR)
error_t
pthread_barrierattr_settype(pthread_barrierattr_t *attr, int_fast8_t type);

/** @this initialize a barrier attribute object */
config_depend_inline(CONFIG_PTHREAD_BARRIER_ATTR,
error_t pthread_barrierattr_init(pthread_barrierattr_t *attr)
{
  return pthread_barrierattr_settype(attr, PTHREAD_BARRIER_DEFAULT);
});

#endif

typedef struct pthread_barrier_s pthread_barrier_t;

config_depend(CONFIG_PTHREAD_BARRIER)
error_t pthread_barrier_init(pthread_barrier_t *barrier,
                             const pthread_barrierattr_t *attr,
                             unsigned count);

config_depend(CONFIG_PTHREAD_BARRIER)
error_t _pthread_barrier_normal_destroy(pthread_barrier_t *barrier);
config_depend(CONFIG_PTHREAD_BARRIER)
error_t _pthread_barrier_normal_wait(pthread_barrier_t *barrier);


config_depend_inline(CONFIG_PTHREAD_BARRIER,
error_t pthread_barrier_destroy(pthread_barrier_t *barrier),
{
#ifdef CONFIG_PTHREAD_BARRIER_ATTR
  return barrier->funcs->barrier_destroy(barrier);
#else
  return _pthread_barrier_normal_destroy(barrier);
#endif
});

config_depend_inline(CONFIG_PTHREAD_BARRIER,
error_t pthread_barrier_wait(pthread_barrier_t *barrier),
{
#ifdef CONFIG_PTHREAD_BARRIER_ATTR
  return barrier->funcs->barrier_wait(barrier);
#else
  return _pthread_barrier_normal_wait(barrier);
#endif
});

/** @this may be returned by @ref pthread_barrier_wait */
#define PTHREAD_BARRIER_SERIAL_THREAD	-1

#ifdef CONFIG_PTHREAD_BARRIER_ATTR

/** normal rwlock object static initializer */
# define PTHREAD_BARRIER_INITIALIZER(n)                                 \
  {                                                                     \
    .funcs = barrier_normal_funcs,                                      \
      .normal.wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST),    \
      .normal.count = (n),                                              \
  }

#else

/** normal rwlock object static initializer */
# define PTHREAD_BARRIER_INITIALIZER(n)							  \
  {											  \
    .normal.wait = CONTAINER_ROOT_INITIALIZER(sched_queue, DLIST),	  \
    .normal.count = (n),									  \
  }

#endif

/************************************************************************
		PThread Spinlock related public API
************************************************************************/

typedef atomic_int_t pthread_spinlock_t;

config_depend_inline(CONFIG_PTHREAD_SPIN,
error_t pthread_spin_init(pthread_spinlock_t *spinlock,
                  bool_t pshared),
{
  *spinlock = 0;
  order_smp_write();
  return 0;
});

config_depend_inline(CONFIG_PTHREAD_SPIN,
error_t pthread_spin_destroy(pthread_spinlock_t *spinlock),
{
  return 0;
});

config_depend_inline(CONFIG_PTHREAD_SPIN,
error_t pthread_spin_lock(pthread_spinlock_t *spinlock),
{
  cpu_atomic_bit_waitset(spinlock, 0);
  order_smp_mem();
  return 0;
});

config_depend_inline(CONFIG_PTHREAD_SPIN,
error_t pthread_spin_trylock(pthread_spinlock_t *spinlock),
{
  bool_t res = cpu_atomic_bit_testset(spinlock, 0);
  order_smp_mem();
  return res ? -EBUSY : 0;
});

config_depend_inline(CONFIG_PTHREAD_SPIN,
error_t pthread_spin_unlock(pthread_spinlock_t *spinlock),
{
  order_smp_mem();
  *spinlock = 0;
  order_smp_write();
  return 0;
});

/************************************************************************
		PThread Once
************************************************************************/

/** @internal */
struct pthread_once_s
{
  pthread_mutex_t lock;
  bool_t done;
};

/** @this is the POSIX type definition for pthread_once_t */
typedef struct pthread_once_s pthread_once_t;

/** @this is the POSIX type definition for PTHREAD_ONCE_INIT */
#define PTHREAD_ONCE_INIT { PTHREAD_MUTEX_INITIALIZER, 0 }

/**
   @this calls a given function only once according to the flag
   variable @tt once.

   @tt once must have beed initialized with PTHREAD_ONCE_INIT.

   @param once Flag variable used to ensure call is made only once
   @param func Function to call once
   @returns 0 when done
*/
config_depend_inline(CONFIG_PTHREAD_ONCE,
error_t pthread_once(pthread_once_t *once, void (*func)()),
{
  /* No contention here... */
  if ( once->done )
    return 0;

  pthread_mutex_lock(&once->lock);
  if ( ! once->done ) {
    /*
      FIXME: The function pthread_once() is not a cancellation
      point. However, if init_routine() is a cancellation point and is
      canceled, the effect on once_control is as if pthread_once() was
      never called.
    */
    func();
    once->done = 1;
  }
  pthread_mutex_unlock(&once->lock);

  return 0;
});

/************************************************************************
		PThread Keys
************************************************************************/

/** @this is the maximum number of keys in the system. */
#define PTHREAD_KEYS_MAX CONFIG_PTHREAD_KEYS_MAX

/** @this is the maximum number of iterations for destructor calling */
#define PTHREAD_DESTRUCTOR_ITERATIONS CONFIG_PTHREAD_KEYS_DESTRUCTOR_ITERATIONS

typedef size_t pthread_key_t;

/**
   @this creates a new key and associates an optional destructor to
   the keyed data.  If there cannot be any more key allocated in the
   system, an error is signaled.

   Each non-NULL @tt destructor will be called once for each exitting
   thread with non-NULL keyed data.  Destructor must still
   @ref pthread_key_delete the data.  If some keyed data is still
   non-NULL after deletion, the whole deletion process is started
   again; at most @ref #PTHREAD_DESTRUCTOR_ITERATIONS times.

   @param key Key handle to fill
   @param destructor Optional destructor function; @tt NULL if not needed
   @returns 0 if done, @tt -EAGAIN if no key is available any more
 */
config_depend(CONFIG_PTHREAD_KEYS)
error_t pthread_key_create(pthread_key_t *key, void (*destructor)(void *));

/**
   @this deletes a key.  The destructor optionnaly attached to this
   key is not called, it is up to the application to free data
   attached to this key.

   @param key Key handle to delete
   @returns 0 if done
 */
config_depend(CONFIG_PTHREAD_KEYS)
error_t pthread_key_delete(pthread_key_t key);

extern CONTEXT_LOCAL const void *_key_values[CONFIG_PTHREAD_KEYS_MAX];

/**
   @this retrieves data associated to key in the current thread.

   @param key Key handle to retrieve
   @returns Value associated to key, NULL if none
 */
config_depend_inline(CONFIG_PTHREAD_KEYS,
void *pthread_getspecific(pthread_key_t key),
{
  if ( key >= CONFIG_PTHREAD_KEYS_MAX )
    return NULL;

  const void **_values = CONTEXT_LOCAL_ADDR(_key_values[0]);
  return (void*)_values[key];
});

/**
   @this sets data to associate to key in the current thread.

   @param key Key handle to set
   @param value Value associated to key, NULL if resetting needed
 */
config_depend_inline(CONFIG_PTHREAD_KEYS,
error_t pthread_setspecific(pthread_key_t key, const void *value),
{
  if ( key >= CONFIG_PTHREAD_KEYS_MAX )
    return -EINVAL;

  const void **_values = CONTEXT_LOCAL_ADDR(_key_values[0]);
  _values[key] = value;
  return 0;
});

#endif /* CONFIG_PTHREAD */
C_HEADER_END

#endif /* PTHREAD_H_ */

