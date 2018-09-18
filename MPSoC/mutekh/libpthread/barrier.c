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
#include <hexo/atomic.h>
#include <hexo/iospace.h>
#include <hexo/ordering.h>
#include <hexo/error.h>
#include <pthread.h>
#include <assert.h>

error_t _pthread_barrier_normal_init(pthread_barrier_t *barrier, unsigned count)
{
    barrier->normal.count = count;
    return sched_queue_init(&barrier->normal.wait);
}

error_t _pthread_barrier_normal_destroy(pthread_barrier_t *barrier)
{
    sched_queue_destroy(&barrier->normal.wait);

    return 0;
}

error_t _pthread_barrier_normal_wait(pthread_barrier_t *barrier)
{
    error_t res = 0;

    CPU_INTERRUPT_SAVESTATE_DISABLE;
    sched_queue_wrlock(&barrier->normal.wait);

    assert(barrier->normal.count >= 1);
    
    if (barrier->normal.count == 1)
    {
        while (sched_wake(&barrier->normal.wait) != NULL)
            barrier->normal.count++;
        sched_queue_unlock(&barrier->normal.wait);
        res = PTHREAD_BARRIER_SERIAL_THREAD;
    }
    else
    {
        barrier->normal.count--;
        sched_wait_unlock(&barrier->normal.wait);
    }

    CPU_INTERRUPT_RESTORESTATE;

    return res;
}


#ifdef CONFIG_PTHREAD_BARRIER_ATTR

# ifdef CONFIG_PTHREAD_BARRIER_SPIN

error_t _pthread_barrier_spin_init(pthread_barrier_t *barrier, unsigned count)
{
    barrier->spin.max_count = count;
    cpu_mem_write_32((uintptr_t)&barrier->spin.count,barrier->spin.max_count);
    cpu_mem_write_8((uintptr_t)&barrier->spin.release, 0);
    order_smp_write();

    return 0;
}

error_t _pthread_barrier_spin_destroy(pthread_barrier_t *barrier)
{
    return 0;
}

error_t _pthread_barrier_spin_wait(pthread_barrier_t *barrier)
{
    uint8_t release = cpu_mem_read_8((uintptr_t)&barrier->spin.release);

    // see http://www.mutekh.org/api/?function:atomic_dec
    if ( !atomic_dec(&barrier->spin.count) )
    {
        cpu_mem_write_32((uintptr_t)&barrier->spin.count,barrier->spin.max_count);
        order_smp_write();
        // Toggle the release state
        cpu_mem_write_8((uintptr_t)&barrier->spin.release, !release);
        order_smp_write();

        return PTHREAD_BARRIER_SERIAL_THREAD;
    }

    while ( cpu_mem_read_8((uintptr_t)&barrier->spin.release) == release )
      order_smp_read();

    return 0;
}

static
struct _pthread_barrier_func_s barrier_spin_funcs = {
    .barrier_init = _pthread_barrier_spin_init,
    .barrier_wait = _pthread_barrier_spin_wait,
    .barrier_destroy = _pthread_barrier_spin_destroy,
};

# endif /* CONFIG_PTHREAD_BARRIER_SPIN */


struct _pthread_barrier_func_s barrier_normal_funcs = {
    .barrier_init = _pthread_barrier_normal_init,
    .barrier_wait = _pthread_barrier_normal_wait,
    .barrier_destroy = _pthread_barrier_normal_destroy,
};

error_t
pthread_barrierattr_settype(pthread_barrierattr_t *attr, int_fast8_t type)
{
    switch (type) {
    case PTHREAD_BARRIER_DEFAULT:
        attr->funcs = &barrier_normal_funcs;
        return 0;

# ifdef CONFIG_PTHREAD_BARRIER_SPIN
    case PTHREAD_BARRIER_SPIN:
        attr->funcs = &barrier_spin_funcs;
        return 0;
# endif

    default:
        return -EINVAL;
    }
}

#endif /* CONFIG_PTHREAD_BARRIER_ATTR */


error_t pthread_barrier_init(pthread_barrier_t *barrier,
                             const pthread_barrierattr_t *attr,
                             unsigned count)
{
#ifdef CONFIG_PTHREAD_BARRIER_ATTR
    const struct _pthread_barrier_func_s *funcs = &barrier_normal_funcs;

    if ( attr != NULL ) {
        funcs = attr->funcs;
    }

    barrier->funcs = funcs;

    return funcs->barrier_init(barrier, count);
#else
    return _pthread_barrier_normal_init(barrier, count);
#endif
}
