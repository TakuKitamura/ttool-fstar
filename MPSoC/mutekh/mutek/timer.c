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

    Copyright Matthieu Bucchianeri <matthieu.bucchianeri@epita.fr> (c) 2006
    Copyright Institut Telecom / Telecom ParisTech (c) 2011
    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2011
*/

#include <mutek/timer.h>
#include <hexo/types.h>
#include <hexo/error.h>

#include <hexo/gpct_platform_hexo.h>
#include <hexo/gpct_lock_hexo.h>
#include <gpct/cont_clist.h> /* XXX change to b-list */

#ifdef CONFIG_MUTEK_SCHEDULER
# include <mutek/scheduler.h>
# include <hexo/lock.h>
#endif

CONTAINER_FUNC(timer, CLIST, inline, timer);

/*
 * add a delay timer.
 */

error_t			timer_add_event(struct timer_s		*timer,
					struct timer_event_s	*event)
{
  struct timer_event_s	*e;
  timer_delay_t		t;
  error_t		err = 0;

  event->timer = timer;

  /* set the start time */
  t = event->start = timer->ticks;
  t += event->delay;

  CPU_INTERRUPT_SAVESTATE_DISABLE;

  /* FIXME use gpct insert ascend */
  /* insert the timer in the list (sorted) */
  for (e = timer_head(&timer->root);
       e != NULL && t > e->start + e->delay;
       e = timer_next(&timer->root, e))
    ;
  if (e == NULL)
    err = !timer_pushback(&timer->root, event);
  else
    err = !timer_insert_pre(&timer->root, e, event);

  CPU_INTERRUPT_RESTORESTATE;

  return err;
}

/*
 * cancel a timer.
 */

error_t	timer_cancel_event(struct timer_event_s	*event,
			   bool_t		callback)
{
  error_t err = 0;

  CPU_INTERRUPT_SAVESTATE_DISABLE;

  /* remove the timer */
  if (timer_remove(&event->timer->root, event))
    err = -ENOENT;

  CPU_INTERRUPT_RESTORESTATE;

  if ( err )
	  return err;

  /* perform the callback if requested by user */
  if (callback)
    event->callback(event, event->pv);

  return 0;
}

/*
 * called by the clock to increment the current tick count.
 */

void			timer_inc_ticks(struct timer_s		*timer,
					timer_delay_t		ticks)
{
  struct timer_event_s	*event;

  /* adjust current tick count */
  timer->ticks += ticks;

  /* XXX use FOREACH ?! */
  /* the timers are sorted by expire time */
  while ((event = timer_head(&timer->root)) != NULL &&
	 event->start + event->delay <= timer->ticks)
    {
      /* remove the timer */
      timer_pop(&timer->root);

      /* perform the callback */
      event->callback(event, event->pv);
    }
}

/*
 * get the current tick count.
 */

inline timer_delay_t	timer_get_tick(struct timer_s		*timer)
{
  return timer->ticks;
}

struct sleep_wait_s
{
#ifdef CONFIG_MUTEK_SCHEDULER
  lock_t lock;
  struct sched_context_s *ctx;
#endif
  bool_t done;
};

#ifdef CONFIG_MUTEK_SCHEDULER

static TIMER_CALLBACK(timer_sleep_callback)
{
  struct sleep_wait_s *sw = pv;
  lock_spin(&sw->lock);

  if (sw->ctx != NULL)
    sched_context_start(sw->ctx);

  sw->done = 1;
  lock_release(&sw->lock);
}

error_t timer_sleep(struct timer_s *timer, timer_delay_t delay)
{
  struct timer_event_s ev;
  struct sleep_wait_s sw;

  lock_init(&sw.lock);
  sw.ctx = NULL;
  sw.done = 0;
  ev.callback = timer_sleep_callback;
  ev.pv = &sw;
  ev.delay = delay;

  timer_add_event(timer, &ev);

  /* ensure callback doesn't occur here */
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  lock_spin(&sw.lock);

  if (!sw.done)
    {
      sw.ctx = sched_get_current();
      sched_stop_unlock(&sw.lock);
    }
  else
    lock_release(&sw.lock);

  CPU_INTERRUPT_RESTORESTATE;

  lock_destroy(&sw.lock);

  return 0;
}

#else

error_t timer_sleep(struct timer_s *timer, timer_delay_t delay)
{
  timer_delay_t t = timer_get_tick(timer);
  
  while ((delay > timer_get_tick(timer) - t))
    ;

  return 0;
}

#endif
