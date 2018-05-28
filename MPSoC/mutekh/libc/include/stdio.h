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

#ifndef STDIO_H_
#define STDIO_H_

#include <hexo/decls.h>

C_HEADER_BEGIN

/**
 * @file
 * @module{C library}
 */

#include <hexo/types.h>
#include <hexo/error.h>

#include <hexo/gpct_platform_hexo.h>
#include <gpct/cont_ring.h>

#include <stdarg.h>
#include <unistd.h>

ssize_t sprintf(char *str, const char *format, ...);

ssize_t snprintf(char *str, size_t size, const char *format, ...);

ssize_t vsprintf(char *str, const char *format, va_list ap);

ssize_t vsnprintf(char *str, size_t size, const char *format, va_list ap);

config_depend(CONFIG_LIBC_STREAM_STD)
ssize_t scanf(const char *format, ...);

config_depend(CONFIG_LIBC_STREAM_STD)
ssize_t vscanf(const char *format, va_list ap);

ssize_t sscanf(const char *str, const char *format, ...);

ssize_t vsscanf(const char *str, const char *format, va_list ap);

/** standard BUFSIZ macro */
#define BUFSIZ		CONFIG_LIBC_STREAM_BUFFER_SIZE

/** standard EOF macro */
#define EOF			-1

typedef int32_t			fpos_t;

enum				stdio_buf_mode_e
{
    _IONBF, _IOLBF, _IOFBF,
};

#ifdef CONFIG_LIBC_STREAM

CONTAINER_TYPE(stream_fifo, RING, uint8_t, CONFIG_LIBC_STREAM_BUFFER_SIZE);

typedef struct			file_s
{
  const struct fileops_s	*ops;
  void *hndl;

  error_t			(*rwflush)(struct file_s *file);
  stream_fifo_root_t		fifo_read;
  stream_fifo_root_t		fifo_write;

  fpos_t			pos;
  enum stdio_buf_mode_e		buf_mode;
  bool_t			eof, error;
}                               FILE;

/** @internal stream buffered read function.
    @returns 1 on success, 0 on end of stream and < 0 on error.
*/
error_t	__stdio_read(size_t size_, FILE *file, uint8_t *ptr);

/** @internal stream buffered write function.
    @returns 0 on success, < 0 on error.
*/
error_t __stdio_write(size_t size_, FILE *file, const uint8_t *ptr);

/** @internal perform stream object initialization */
void __stdio_stream_init(FILE *stream);

#else
typedef struct file_s { } FILE;
#endif /* CONFIG_LIBC_STREAM */

config_depend_and2(CONFIG_LIBC_STREAM, CONFIG_VFS)
FILE *fopen(const char *path, const char *mode);

config_depend(CONFIG_LIBC_STREAM)
error_t fclose(FILE *file);

config_depend(CONFIG_LIBC_STREAM)
int_fast16_t fputc(unsigned char c, FILE *file);

config_depend(CONFIG_LIBC_STREAM)
int_fast16_t fgetc(FILE *file);

config_depend(CONFIG_LIBC_STREAM)
size_t fread(void *ptr, size_t size, size_t nmemb, FILE *file);

config_depend(CONFIG_LIBC_STREAM)
size_t fwrite(const void *ptr, size_t size, size_t nmemb, FILE *file);

config_depend(CONFIG_LIBC_STREAM)
char *fgets(char *str, size_t size, FILE *file);

config_depend(CONFIG_LIBC_STREAM)
error_t fputs(const char *str, FILE *file);

config_depend(CONFIG_LIBC_STREAM_STD)
error_t puts(const char *str);

config_depend(CONFIG_LIBC_STREAM)
error_t fseek(FILE *file, fpos_t offset, int_fast8_t whence);

config_depend(CONFIG_LIBC_STREAM)
error_t fflush(FILE *file);

config_depend(CONFIG_LIBC_STREAM)
error_t fpurge(FILE *file);

config_depend(CONFIG_LIBC_STREAM)
ssize_t vfprintf(FILE *file, const char *format, va_list ap);

config_depend(CONFIG_LIBC_STREAM)
ssize_t fprintf(FILE *file, const char *format, ...);

config_depend(CONFIG_LIBC_STREAM)
ssize_t fscanf(FILE *file, const char *format, ...);

config_depend(CONFIG_LIBC_STREAM)
ssize_t vfscanf(FILE *file, const char *fmt, va_list ap);

config_depend(CONFIG_LIBC_STREAM)
int_fast16_t ungetc(int_fast16_t c, FILE *file);

config_depend(CONFIG_LIBC_STREAM)
error_t setvbuf(FILE *file, char *buf, enum stdio_buf_mode_e mode, size_t size);

config_depend_inline(CONFIG_LIBC_STREAM,
fpos_t ftell(FILE *file),
{
  return file->pos;
});

config_depend_inline(CONFIG_LIBC_STREAM,
void rewind(FILE *file),
{
  fseek(file, 0, SEEK_SET);
});

config_depend_inline(CONFIG_LIBC_STREAM,
error_t fgetpos(FILE *file, fpos_t *pos),
{
  *pos = file->pos;
  return 0;
});

config_depend_inline(CONFIG_LIBC_STREAM,
error_t fsetpos(FILE *file, const fpos_t *pos),
{
  return fseek(file, *pos, SEEK_SET);
});

config_depend_inline(CONFIG_LIBC_STREAM,
int_fast16_t getc(FILE *file),
{
  return fgetc(file);
});

config_depend_inline(CONFIG_LIBC_STREAM,
int_fast16_t putc(int_fast16_t c, FILE *file),
{
  return fputc(c, file);
});

config_depend_inline(CONFIG_LIBC_STREAM,
void clearerr(FILE *stream),
{
  stream->error = 0;
  stream->eof = 0;
});

config_depend_inline(CONFIG_LIBC_STREAM,
bool_t ferror(FILE *stream),
{
  return stream->error;
});

config_depend_inline(CONFIG_LIBC_STREAM,
bool_t feof(FILE *stream),
{
  return stream->eof;
});

config_depend(CONFIG_LIBC_STREAM_STD)
extern FILE * const stdin;

config_depend(CONFIG_LIBC_STREAM_STD)
extern FILE * const stdout;

config_depend(CONFIG_LIBC_STREAM_STD)
extern FILE * const stderr;

config_depend_inline(CONFIG_LIBC_STREAM_STD,
int_fast16_t getchar(),
{
  return fgetc(stdin);
});

config_depend_inline(CONFIG_LIBC_STREAM_STD,
int_fast16_t putchar(int_fast16_t c),
{
  return fputc(c, stdout);
});

config_depend_inline(CONFIG_LIBC_STREAM_STD,
ssize_t vprintf(const char *format, va_list ap),
{
  return vfprintf(stdout, format, ap);
});

/** This function use libc buffered streams and thus require @ref
    #CONFIG_LIBC_STREAM_STD enabled. Consider using @ref printk
    instead for direct output. */
config_depend(CONFIG_LIBC_STREAM_STD)
ssize_t printf(const char *format, ...);

config_depend_inline(CONFIG_LIBC_STREAM_STD,
char *gets(char *s),
{
  return fgets(s, 1024, stdin);
});

config_depend(CONFIG_LIBC_STREAM_STD)
void perror(const char *reason);

C_HEADER_END

#endif

