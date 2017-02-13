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

    Copyright Institut Telecom / Telecom ParisTech (c) 2011
    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2009-2011
*/

#ifndef TIME_H_
#define TIME_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

#include <sys/types.h>
#include <hexo/error.h>

/**
   @file
   @module{C library}
   @short Time-related function stubs
 */

struct timeval;

struct timespec
{
  time_t          tv_sec;     /* seconds */
  time_nsec_t     tv_nsec;    /* nanoseconds */
};

struct timezone
{
  int_fast8_t tz_minuteswest;         /* minutes west of greenwich */
  int_fast8_t tz_dsttime;             /* type of DST correction */
};

config_depend(CONFIG_MUTEK_TIMER)
error_t gettimeofday(struct timeval *tv, struct timezone *tz);

config_depend(CONFIG_MUTEK_TIMER)
error_t settimeofday(const struct timeval *tv, const struct timezone *tz);

config_depend(CONFIG_MUTEK_TIMER)
time_t time(time_t *t);

enum clockid_e
{
  CLOCK_REALTIME           = 1,
#define CLOCK_REALTIME           CLOCK_REALTIME
};

typedef enum clockid_e clockid_t;

config_depend(CONFIG_MUTEK_TIMER)
error_t clock_getres(clockid_t clk_id, struct timespec *res);

config_depend(CONFIG_MUTEK_TIMER)
error_t clock_gettime(clockid_t clk_id, struct timespec *tp);

config_depend(CONFIG_MUTEK_TIMER)
error_t clock_settime(clockid_t clk_id, const struct timespec *tp);

config_depend(CONFIG_MUTEK_TIMER_EVENTS)
error_t nanosleep(const struct timespec *rqtp, struct timespec *rmtp);

C_HEADER_END

#endif
