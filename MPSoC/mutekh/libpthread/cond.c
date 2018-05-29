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

#include <mutek/scheduler.h>
#include <hexo/error.h>
#include <pthread.h>

error_t
pthread_cond_init(pthread_cond_t *cond,
		  const pthread_condattr_t *attr)
{
  return sched_queue_init(&cond->wait);
}

error_t
pthread_cond_destroy(pthread_cond_t *cond)
{
  sched_queue_destroy(&cond->wait);

  return 0;
}

error_t
pthread_cond_signal(pthread_cond_t *cond)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  sched_queue_wrlock(&cond->wait);
  struct sched_context_s *sched_ctx;

  sched_ctx = sched_wake(&cond->wait);
#ifdef CONFIG_PTHREAD_TIME
  if (sched_ctx)
  {
    struct pthread_s *thread = sched_ctx->priv;
    atomic_bit_clr(&thread->state, _PTHREAD_STATE_TIMEDWAIT);
  }
#endif

  sched_queue_unlock(&cond->wait);
  CPU_INTERRUPT_RESTORESTATE;

  return 0;
}

error_t
pthread_cond_broadcast(pthread_cond_t *cond)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  sched_queue_wrlock(&cond->wait);
  struct sched_context_s *sched_ctx;

  while ((sched_ctx = sched_wake(&cond->wait)))
    {
#ifdef CONFIG_PTHREAD_TIME
      struct pthread_s *thread = sched_ctx->priv;
      atomic_bit_clr(&thread->state, _PTHREAD_STATE_TIMEDWAIT);
#endif
    }

  sched_queue_unlock(&cond->wait);
  CPU_INTERRUPT_RESTORESTATE;

  return 0;
}

error_t
pthread_cond_wait(pthread_cond_t *cond, pthread_mutex_t *mutex)
{
  error_t	res = 0;

#ifdef CONFIG_PTHREAD_CANCEL
  pthread_testcancel();
#endif

  CPU_INTERRUPT_SAVESTATE_DISABLE;
  sched_queue_wrlock(&cond->wait);

  if (!pthread_mutex_unlock(mutex))
    {
      sched_wait_unlock(&cond->wait);

      pthread_mutex_lock(mutex);
    }
#ifdef CONFIG_PTHREAD_CHECK
  else
    {
      sched_queue_unlock(&cond->wait);
      res = EINVAL;
    }
#endif

  CPU_INTERRUPT_RESTORESTATE;

  return res;
}

#ifdef CONFIG_PTHREAD_TIME

#include <time.h>
#include <mutek/timer.h>

struct pthread_cond_timedwait_ctx_s
{
  sched_queue_root_t *wait;
  struct sched_context_s *sched_ctx;
};

static TIMER_CALLBACK(pthread_cond_timer)
{
  struct pthread_cond_timedwait_ctx_s *ev_ctx = pv;
  struct sched_context_s *sched_ctx = ev_ctx->sched_ctx;
  struct pthread_s *thread = sched_ctx->priv;

  sched_queue_wrlock(ev_ctx->wait);

  if (atomic_bit_testclr(&thread->state, _PTHREAD_STATE_TIMEDWAIT))
    {
      sched_context_wake(ev_ctx->wait, sched_ctx);
      atomic_bit_set(&thread->state, _PTHREAD_STATE_TIMEOUT);
    }

  sched_queue_unlock(ev_ctx->wait);
}

error_t
pthread_cond_timedwait(pthread_cond_t *cond, 
		       pthread_mutex_t *mutex,
		       const struct timespec *delay)
{
  error_t	res = 0;

#ifdef CONFIG_PTHREAD_CANCEL
  pthread_testcancel();
#endif

  CPU_INTERRUPT_SAVESTATE_DISABLE;
  sched_queue_wrlock(&cond->wait);

  if (!pthread_mutex_unlock(mutex))
    {
      struct timer_event_s ev;
      struct pthread_cond_timedwait_ctx_s ev_ctx;

      ev_ctx.wait = &cond->wait;
      ev_ctx.sched_ctx = sched_get_current();
      struct pthread_s	*this = ev_ctx.sched_ctx->priv;

      ev.callback = pthread_cond_timer;
      ev.pv = &ev_ctx;
      ev.delay = timer_sec2tu(delay->tv_sec)
               + timer_nsec2tu(delay->tv_nsec);

      atomic_bit_set(&this->state, _PTHREAD_STATE_TIMEDWAIT);

      timer_add_event(&timer_ms, &ev);
      sched_wait_unlock(&cond->wait);
      timer_cancel_event(&ev, 0);

      if (atomic_bit_testclr(&this->state, _PTHREAD_STATE_TIMEOUT))
        res = ETIMEDOUT;

      pthread_mutex_lock(mutex);
    }
#ifdef CONFIG_PTHREAD_CHECK
  else
    {
      sched_queue_unlock(&cond->wait);
      res = EINVAL;
    }
#endif

  CPU_INTERRUPT_RESTORESTATE;

  return res;
}

#endif
