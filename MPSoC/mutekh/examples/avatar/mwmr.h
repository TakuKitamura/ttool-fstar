/*
 * This file is distributed under the terms of the GNU General Public
 * License.
 * 
 * Copyright (c) UPMC / Lip6
 *     2005-2008, Nicolas Pouillon, <nipo@ssji.net>
 */

#ifndef MWMR_H_
#define MWMR_H_

/**
   @file
   @module{MWMR}
   @short MWMR channels access
 */

/**
   @this is an abstract MWMR channel structure.
 */
struct mwmr_s;

#if defined(CONFIG_MWMR_PTHREAD)

# include <pthread.h>

/** @hidden */
struct mwmr_status_s
{
	pthread_mutex_t lock;
	pthread_cond_t nempty;
	pthread_cond_t nfull;
	size_t rptr, wptr;
	size_t usage;
};

/** @hidden */
struct mwmr_s {
	struct mwmr_status_s *status;
	size_t width;
	size_t depth;
	size_t gdepth;
	uint8_t *buffer;
	const char *const name;
};

/** @hidden */
typedef struct {} srl_mwmr_lock_t;
/** @hidden */
# define MWMR_LOCK_INITIALIZER {}

/** @hidden */
# define MWMR_STATUS_INITIALIZER(x,y)							\
	{															\
		.lock = PTHREAD_MUTEX_INITIALIZER,						\
		.nempty = PTHREAD_COND_INITIALIZER,						\
		.nfull = PTHREAD_COND_INITIALIZER,						\
		.rptr = 0,      										\
		.wptr = 0,      										\
		.usage = 0,												\
	}

/** @hidden */
# define MWMR_INITIALIZER(w, d, b, st, n, l)					   \
	{														   \
		.width = w,											   \
		.depth = d,											   \
		.gdepth = (w)*(d),									   \
		.status = st,									   	   \
		.buffer = (void*)b,									   \
		.name = n,											   \
	}

#elif defined CONFIG_MWMR_SOCLIB

# ifdef CONFIG_MWMR_LOCKFREE

/** @hidden */
enum SoclibMwmrRegisters {
    MWMR_IOREG_MAX = 16,
    MWMR_RESET = MWMR_IOREG_MAX,
    MWMR_CONFIG_FIFO_WAY,
    MWMR_CONFIG_FIFO_NO,
    MWMR_CONFIG_STATUS_ADDR,
    MWMR_CONFIG_DEPTH, // bytes
    MWMR_CONFIG_BUFFER_ADDR,
    MWMR_CONFIG_RUNNING,
    MWMR_CONFIG_WIDTH, // bytes
    MWMR_CONFIG_ENDIANNESS, // Write 0x11223344 here
    MWMR_FIFO_FILL_STATUS,
};

/** @hidden */
enum SoclibMwmrWay {
    MWMR_TO_COPROC,
    MWMR_FROM_COPROC,
};

/** @hidden */
struct mwmr_status_s
{
	uint32_t free_tail; // bytes
	uint32_t free_head; // bytes
	uint32_t free_size; // bytes

	uint32_t data_tail; // bytes
	uint32_t data_head; // bytes
	uint32_t data_size; // bytes
};

/** @hidden */
#  define MWMR_STATUS_INITIALIZER(w, d) {0,0,(w*d),0,0,0}

# else /* not CONFIG_MWMR_LOCKFREE */

/** @hidden */
enum SoclibMwmrRegisters {
    MWMR_IOREG_MAX = 16,
    MWMR_RESET = MWMR_IOREG_MAX,
    MWMR_CONFIG_FIFO_WAY,
    MWMR_CONFIG_FIFO_NO,
    MWMR_CONFIG_STATUS_ADDR,
    MWMR_CONFIG_DEPTH,
    MWMR_CONFIG_BUFFER_ADDR,
    MWMR_CONFIG_LOCK_ADDR,
    MWMR_CONFIG_RUNNING,
    MWMR_CONFIG_WIDTH,
    MWMR_FIFO_FILL_STATUS,
};

/** @hidden */
struct mwmr_status_s
{
	uint32_t rptr;
	uint32_t wptr;
	uint32_t usage;
	uint32_t lock;
};

/** @hidden */
#  define MWMR_STATUS_INITIALIZER(w,d) {0,0,0,0}

# endif /* CONFIG_MWMR_LOCKFREE */

# ifdef CONFIG_MWMR_USE_RAMLOCKS
/** @hidden */
#  define MWMR_USE_SEPARATE_LOCKS
/** @hidden */
typedef uint32_t srl_mwmr_lock_t;
/** @hidden */
#  define MWMR_LOCK_INITIALIZER 0
# endif

/** @hidden */
struct mwmr_s {
	size_t width;
	size_t depth;
	size_t gdepth;
	void *buffer;
	struct mwmr_status_s *status;
	const char *const name;
# ifdef CONFIG_MWMR_INSTRUMENTATION
	uint32_t n_read;
	uint32_t n_write;
	uint32_t time_read;
	uint32_t time_write;
# endif
# ifdef CONFIG_MWMR_USE_RAMLOCKS
/** @hidden */
	srl_mwmr_lock_t *lock;
# endif
};

# ifdef CONFIG_MWMR_USE_RAMLOCKS

/** @hidden */
#  define MWMR_INITIALIZER(w, d, b, st, n, l)				   \
	{														   \
		.width = w,											   \
		.depth = d,											   \
		.gdepth = (w)*(d),									   \
		.buffer = (void*)b,									   \
		.status = st,									   	   \
		.name = n,											   \
		.lock = l,											   \
	}
# else

/** @hidden */
typedef struct {} srl_mwmr_lock_t;
/** @hidden */
#  define MWMR_LOCK_INITIALIZER {}

/** @hidden */
#  define MWMR_INITIALIZER(w, d, b, st, n, l)				   \
	{														   \
		.width = w,											   \
		.depth = d,											   \
		.gdepth = (w)*(d),									   \
		.buffer = (void*)b,									   \
		.status = st,									   	   \
		.name = n,											   \
	}
# endif

#else
# error No valid MWMR implementation
#endif

#ifdef CONFIG_MWMR_INSTRUMENTATION

/**
   @this dumps statistics about usage of the channel to current
   console.

   @param channel The channel
*/
void mwmr_dump_stats( const struct mwmr_s *channel );

/**
   @this resets statistics about usage of the channel.

   @param channel The channel
*/
void mwmr_clear_stats( struct mwmr_s *channel );
#endif

/**
   @this is the way of the channel designated when configuring an
   hardware MWMR controller
 */
enum SoclibMwmrWay {
    MWMR_TO_COPROC,
    MWMR_FROM_COPROC,
};

/**
   @this initializes an hardware MWMR controller for usage of a given
   channel. Controller starts to use channel as soon as configured.

   @param coproc Base address of controller
   @param way Way of the channel
   @param no Number of the channel. Channels are numbered from 0 in
             each way.
   @param mwmr Channel to use from coprocessor
 */
void mwmr_hw_init( void *coproc, enum SoclibMwmrWay way,
				   size_t no, const struct mwmr_s* mwmr );

/**
   @this resets the channel's internal state. All data inside it will
   be lost.

   @param channel The channel to reset
 */
void mwmr_init( struct mwmr_s *channel );

/**
   @this reads a given size from a mwmr channel. If the size is not
   available in the channel, the read will block.

   @param channel The mwmr channel
   @param buffer The buffer to retrieve data into
   @param size The size (in bytes) of the requested transfer. This has
   to be a multiple of the channel's width.
 */
void mwmr_read( struct mwmr_s *channel, void *buffer, size_t size );

/**
   @this writes a given size from a mwmr channel. If the size is not
   free in the channel, the write will block.

   @param channel The mwmr channel
   @param buffer The buffer to retrieve data from
   @param size The size (in bytes) of the requested transfer. This has
   to be a multiple of the channel's width.
 */
void mwmr_write( struct mwmr_s *channel, const void *buffer, size_t size );

/**
   @this reads a given size from a mwmr channel. If the size is not
   available in the channel, or if the lock is not available, @this will
   return without transfering the whole buffer.

   @param channel The mwmr channel
   @param buffer The buffer to retrieve data into
   @param size The size (in bytes) of the requested transfer. This has
   to be a multiple of the channel's width.
   @return the amount of bytes actually transfered
 */
size_t mwmr_try_read( struct mwmr_s *channel, void *buffer, size_t size );

/**
   @this writes a given size from a mwmr channel. If the size is not
   free in the channel, or if the lock is not available, @this will
   return without transfering the whole buffer.

   @param channel The mwmr channel
   @param buffer The buffer to retrieve data from
   @param size The size (in bytes) of the requested transfer. This has
   to be a multiple of the channel's width.
   @return the amount of bytes actually transfered
 */
size_t mwmr_try_write( struct mwmr_s *channel, const void *buffer, size_t size );

#endif
