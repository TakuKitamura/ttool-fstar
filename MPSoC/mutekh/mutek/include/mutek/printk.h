#ifndef MUTEK_PRINTK_H_
#define MUTEK_PRINTK_H_

#include <stdarg.h>
#include <libc/formatter.h>

/**
 * @file
 * @module{Mutek}
 * @short Debugging messages output API
 */

#if defined(CONFIG_MUTEK_PRINTK)

/**
   @this defines the backend function for printk() output

   @param f The function to call
   @param ctx Context to give back to the @tt f
 */
void printk_set_output(printf_output_func_t *f, void *ctx);

/**
   @this prints a kernel message to the printk backend set by @tt
   printk_set_output. If available. There is no guarantee the printk
   actually writes anywhere.

   @param format Format syntax, and variadic parameters, like printf()
   @returns count of bytes actually emitted
 */
ssize_t printk(const char *format, ...);

/**
   @this prints a kernel message to the printk backend set by @tt
   printk_set_output. If available. There is no guarantee the printk
   actually writes anywhere.
   
   @param format Format syntax
   @param ap variadic parameters, like vprintf()
   @returns count of bytes actually emitted
 */
inline ssize_t vprintk(const char *format, va_list ap);

/**
   @this prints a binary memory dump of memory to the current printk()
   backend. Output is terminal-protected (all characters between
   ascii(0) and ascii(31) are replaced by "." in output).

   Address appearing on the side of the dump may not be the address of
   the memory buffer holding the data.
   
   @param address Printed address for the first byte of dump
   @param data Data buffer to print
   @param len Length of buffer to print
 */
void hexdumpk(uintptr_t address, const void *data, size_t len);

/**
   Write to the current printk() backend, as set by printk_set_output.

   @param data Data buffer to write
   @param len Length of data buffer to write
 */
void writek(const char *data, size_t len);

#else /* no printk */

static inline
void printk_set_output(printf_output_func_t *f, void *ctx)
{}

static inline
ssize_t printk(const char *format, ...)
{
	return 0;
}

static inline
void hexdumpk(uintptr_t address, const void *data, size_t len)
{
}

static inline
inline ssize_t vprintk(const char *format, va_list ap)
{
	return 0;
}

static inline
void writek(const char *data, size_t len)
{
}

#endif /* printk */

#define PRINTK_RET(val, ...)			\
do {						\
	printk(__VA_ARGS__);			\
	return (val);				\
} while (0)

#endif

