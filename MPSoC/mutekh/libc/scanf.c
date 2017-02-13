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

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2011

*/

#include <hexo/types.h>

#include <stdio.h>
#include <stdarg.h>

#define EOFIELD -2

#define SCANF_INPUT_FUNC(x) int_fast16_t (x)(void *ctx)
typedef SCANF_INPUT_FUNC(scanf_input_func_t);

#define SCANF_UNGET_FUNC(x) void (x)(void *ctx, int_fast16_t c)
typedef SCANF_UNGET_FUNC(scanf_unget_func_t);

#define SCANF_EAT_SPACES(var)                   \
  do {                                          \
    do {                                        \
      var = in(ctx);                            \
                                                \
      if (var == EOF)                           \
        goto eof;                               \
      read_count++;                             \
    } while (var <= ' ');                       \
    width--;                                    \
  } while (0)

#define SCANF_EAT_CHAR(var)                     \
  do {                                          \
    var = in(ctx);                              \
                                                \
    if (var == EOF)                             \
      goto eof;                                 \
    read_count++;                               \
  } while (0)

#define SCANF_EAT_FIELD_CHAR(var)               \
  do {                                          \
    if (width == 0)                             \
      var = EOFIELD;                            \
    else                                        \
      {                                         \
        var = in(ctx);                          \
        if (var != EOF)                         \
          {                                     \
            width--;                            \
            read_count++;                       \
          }                                     \
      }                                         \
  } while (0)

#define SCANF_EAT_SIGN(var)                     \
  do {                                          \
    if (var == '-')                             \
      flags ^= flag_neg;                        \
    else if (var != '+')                        \
      break;                                    \
    SCANF_EAT_FIELD_CHAR(var);                  \
  } while (1)

#define SCANF_EAT_DECNUMBER(var, expr)          \
  do                                            \
    {                                           \
      int_fast8_t v;                            \
      if (var == EOF && !(flags & flag_valid))  \
        goto eof;                               \
      else if (var < 0)                         \
        break;                                  \
      else if (var <= '9' && var >= '0')        \
        v = var - '0';                          \
      else if (flags & flag_valid)              \
        break;                                  \
      else                                      \
        goto merr;                              \
      if (flags & flag_neg)                     \
        v = -v;                                 \
      expr;                                     \
      flags |= flag_valid;                      \
      SCANF_EAT_FIELD_CHAR(var);                \
    }                                           \
  while (1)

#define SCANF_UNGET_CHAR(var)                   \
  do {                                          \
    if (var >= 0)                               \
      {                                         \
        un(ctx, var);                           \
        read_count--;                           \
      }                                         \
  } while (0)

# define SCANF_INT_TYPE_(a, b, c) a##b##c
# define SCANF_INT_TYPE(b) SCANF_INT_TYPE_(flag_, b, bits)

enum scanf_flags_e
{
  flag_nostore = 1,
  flag_8bits   = 2,
  flag_16bits  = 4,
  flag_32bits  = 8,
  flag_64bits  = 16,
  flag_double   = 32,
  flag_long_double = 64,
  flag_int_size_mask = 2+4+8+16,
  flag_float_size_mask = 32+64,
  flag_size_mask = 2+4+8+16+32+64,
  flag_neg     = 128,
  flag_valid   = 256,

  flag_char      = flag_8bits,
  flag_short     = SCANF_INT_TYPE(CPU_SIZEOF_SHORT),
  flag_int       = SCANF_INT_TYPE(CPU_SIZEOF_INT),
  flag_long      = SCANF_INT_TYPE(CPU_SIZEOF_LONG),
  flag_long_long = flag_64bits,
  flag_ptr_t     = SCANF_INT_TYPE(INT_PTR_SIZE),
  flag_ptrdiff_t = SCANF_INT_TYPE(INT_PTR_SIZE),
  flag_size_t    = SCANF_INT_TYPE(INT_REG_SIZE),
  flag_intmax_t  = flag_64bits,
};

ssize_t __scanf(void *ctx, scanf_input_func_t *in,
                scanf_unget_func_t *un,
                const char *fmt, va_list ap)
{
  ssize_t res = 0;
  size_t read_count = 0;
  uintmax_t val = 0;
  int_fast16_t c;
  uint_fast8_t state = 0;
  uint_fast16_t flags = 0;
  ssize_t width = 0;
  int_fast8_t base;
  char f;
  uint8_t bitmap[256/8];

  while (1)
    {
      f = *fmt++;
      switch (state)
        {
          /***** state 0 : reading normal string */
        case 0:
          switch (f)
            {
            case 0:
              return res;

            case '%':
              state = '%';
              flags = flag_int;
              width = -1;
              base = 0;
              val = 0;
              break;

            case 1 ... ' ': { /* all characters below ' ' are considered spaces */
              int_fast16_t c;
              SCANF_EAT_SPACES(c);
              SCANF_UNGET_CHAR(c);
              break;
            }

            default: {
              int_fast16_t c;
              SCANF_EAT_CHAR(c);
              if (c != f)
                goto merr;
              break;
            }
            }
          break;

          /***** state '%' : reading convertion info */
        case '%':
          switch (f)
            {
            case 0:
              goto merr;

            case '*':
              flags |= flag_nostore;
              break;
            case '0' ... '9':
              width = (width < 0 ? 0 : width) * 10 + f - '0';
              break;
              /* modifiers char */
            case 'h':
              if (flags & flag_int)
                flags ^= flag_int | flag_short;
              else
                flags = (flags & ~flag_int_size_mask) | flag_char;
              break;
            case 'j':
              flags = (flags & ~flag_int_size_mask) | flag_intmax_t;
              break;
            case 'l':
              if (!(flags & (flag_double | flag_long_double)))
                {
                  flags = (flags & ~flag_size_mask) | flag_long | flag_double;
                  break;
                }
            case 'L': case 'q':
              flags = (flags & ~flag_size_mask) | flag_long_long | flag_long_double;
              break;
            case 't':
              flags = (flags & ~flag_int_size_mask) | flag_ptrdiff_t;
              break;
            case 'z':
              flags = (flags & ~flag_int_size_mask) | flag_size_t;
              break;

              /* convertion chars */

            case 'n':
              val = read_count;
              state = 0;
              if (flags & flag_nostore)
                break;
              res--;
              goto int_store;

              /****** integers ******/

            case 'u':
            case 'd': case 'D':
              base = 10;
              goto int_conv;

            case 'o':
              base = 8;
              goto int_conv;

            case 'i':
              base = 10;
              SCANF_EAT_SPACES(c);
              SCANF_EAT_SIGN(c);
              if (c == '0')
                {
                  base = 8;
                  SCANF_EAT_FIELD_CHAR(c);
                  if ((c | 32) == 'x')
                    {
                      base = 16;
                      SCANF_EAT_FIELD_CHAR(c);
                    }
                  else
                    flags |= flag_valid;
                }
              goto uint_conv;

            case 'p': case 'P':
              flags = (flags & ~flag_int_size_mask) | flag_ptr_t;
              base = 16;
              SCANF_EAT_SPACES(c);
              goto uint_conv;

            case 'x': case 'X':
              base = 16;
              SCANF_EAT_SPACES(c);
              SCANF_EAT_SIGN(c);
              if (c == '0')
                {
                  SCANF_EAT_FIELD_CHAR(c);
                  if ((c | 32) == 'x')
                    SCANF_EAT_FIELD_CHAR(c);
                  else
                    flags |= flag_valid;
                }
              goto uint_conv;

            int_conv:
              SCANF_EAT_SPACES(c);
              SCANF_EAT_SIGN(c);

            uint_conv:
              while (1)
                {
                  int_fast8_t v;
                  if (c == EOF && !(flags & flag_valid))
                    goto eof;
                  else if (c < 0)
                    break;
                  else if (c <= '9' && c >= '0')
                    v = c - '0';
                  else
                    {
                      c |= 32;
                      if (c <= 'f' && c >= 'a')
                        v = c - 'a' + 10;
                      else if (flags & flag_valid)
                        break;
                      else
                        goto merr;
                    }
                  if (v >= base)
                    goto merr;
                  if (flags & flag_neg)
                    v = -v;
                  val = val * base + v;
                  flags |= flag_valid;
                  SCANF_EAT_FIELD_CHAR(c);
                }
              SCANF_UNGET_CHAR(c);

            int_store:
              state = 0;
              if (flags & flag_nostore)
                break;

              switch (flags & flag_int_size_mask)
                {
                case flag_8bits:
                  *va_arg(ap, uint8_t*) = val;
                  break;
                case flag_16bits:
                  *va_arg(ap, uint16_t*) = val;
                  break;
                case flag_32bits:
                  *va_arg(ap, uint32_t*) = val;
                  break;
                case flag_64bits:
                  *va_arg(ap, uint64_t*) = val;
                  break;
                default:
                  abort();
                }
              res++;
              break;

              /****** floats ******/

#if defined(CONFIG_LIBC_FORMATTER_FLOAT)
            case 'f': case 'e': case 'g': case 'E': {
              double fval = 0;
              SCANF_EAT_SPACES(c);
              SCANF_EAT_SIGN(c);
              if (c != '.')
                SCANF_EAT_DECNUMBER(c, fval = fval * 10 + v);
              if (c == '.')
                {
                  double pval = 0;
                  double scale = 1;
                  SCANF_EAT_FIELD_CHAR(c);
                  SCANF_EAT_DECNUMBER(c, pval = pval * 10 + v; scale *= 10);
                  fval += pval / scale;
                }
              if ((flags & flag_valid) && (c | 32) == 'e')
                {
                  int_fast16_t i, pw = 0;
                  SCANF_EAT_FIELD_CHAR(c);
                  flags &= ~flag_neg;
                  SCANF_EAT_SIGN(c);
                  double p = (flags & flag_neg) ? .1f : 10.0f;
                  flags &= ~flag_neg;
                  SCANF_EAT_DECNUMBER(c, pw = pw * 10 + v);
                  for (i = 1; i < 256; i <<= 1)
                    {
                      if (i & pw)
                        fval *= p;
                      p *= p;
                    }
                }
              SCANF_UNGET_CHAR(c);

              state = 0;
              if (flags & flag_nostore)
                break;

              switch (flags & flag_float_size_mask)
                {
                default:
                  *va_arg(ap, float*) = fval;
                  break;
                case flag_double:
                  *va_arg(ap, double*) = fval;
                  break;
                case flag_long_double:
                  *va_arg(ap, __compiler_longdouble_t*) = fval;
                  break;
                }
              res++;
              break;
            }
#endif

              /****** %s string ******/

            case 's': {
              char *s = NULL;
              SCANF_EAT_SPACES(c);
              state = 0;

              if (!(flags & flag_nostore))
                s = va_arg(ap, char*);

              while (1)
                {
                  if (c == EOF && !(flags & flag_valid))
                    goto eof;
                  if (/*c < 0 ||*/ c <= ' ')
                    break;
                  if (s)
                    *s++ = c;
                  flags |= flag_valid;
                  SCANF_EAT_FIELD_CHAR(c);
                }
              SCANF_UNGET_CHAR(c);

              if (s)
                {
                  *s = 0;
                  res++;
                }
              break;
            }

              /****** %c string ******/

            case 'c': {
              char *s = NULL;
              state = 0;

              if (width < 0)
                width = 1;
              if (!(flags & flag_nostore))
                s = va_arg(ap, char*);

              while (1)
                {
                  SCANF_EAT_FIELD_CHAR(c);
                  if (c == EOF && !(flags & flag_valid))
                    goto eof;
                  if (c < 0)
                    break;
                  if (s)
                    *s++ = c;
                  flags |= flag_valid;
                }
              SCANF_UNGET_CHAR(c);

              if (s)
                {
                  *s = 0;
                  res++;
                }
              break;
            }

              /****** range string ******/

            case '[':
              state = '[';
              memset(bitmap, 0, sizeof(bitmap));
              break;

            default:
              state = 0;
              SCANF_EAT_CHAR(c);
              if (c != f)
                goto merr;
              break;
            }
          break;

          /***** state '[' : set head */
        case '[':
          switch (f)
            {
            case 0:
              goto merr;

            case '^':
              if (flags & flag_neg)
                goto ubitmap;
              flags |= flag_neg;
              break;

            default:
              state = ']';
              goto ubitmap;
            }
          break;

          /***** state ']' : set tail */
        case ']':
          switch (f)
            {
            case 0:
              goto merr;

            case ']': {
              char *s = NULL;
              uint8_t mask = flags & flag_neg ? 255 : 0;
              state = 0;

              if (!(flags & flag_nostore))
                s = va_arg(ap, char*);

#if 0 /* print range bitmap */
              size_t i;
              for (i = 32; i < 127; i++)
                if (bitmap[i >> 3] & (1 << (i & 7)))
                  printk("%c", i);
#endif

              while (1)
                {
                  SCANF_EAT_FIELD_CHAR(c);
                  if (c == EOF && !(flags & flag_valid))
                    goto eof;
                  if (c < 0 || !((mask ^ bitmap[c >> 3]) & (1 << (c & 7))))
                    break;
                  if (s)
                    *s++ = c;
                  flags |= flag_valid;
                }
              SCANF_UNGET_CHAR(c);

              if (s)
                {
                  *s = 0;
                  res++;
                }
              break;
            }

            case '-': {
              char next = *fmt;
              uint_fast16_t i;
              if (next == ']')
                goto ubitmap;

              char a = __MIN(fmt[-2], next);
              char b = __MAX(fmt[-2], next);
              for (i = a; i <= b; i++)
                bitmap[i >> 3] |= 1 << (i & 7);
              fmt++;
              break;
            }

            default:
            ubitmap:
              bitmap[f >> 3] |= 1 << (f & 7);
              break;
            }
          break;

          /***** bad state */
        default:
          abort();
        }
    }

 merr:                          /* match error */
  // FIXME set errno
  return EOF;

 eof:                           /* eof / read error */
  if (res == 0)
    return EOF;
  return res;
}

struct sscanf_ctx_s
{
  const char *str;
  size_t index;
};

static SCANF_INPUT_FUNC(sscanf_in)
{
  struct sscanf_ctx_s *ctx_ = ctx;
  char res = ctx_->str[ctx_->index];

  if (!res)
    return EOF;

  ctx_->index++;
  return res;
}

static SCANF_UNGET_FUNC(sscanf_un)
{
  struct sscanf_ctx_s *ctx_ = ctx;

  ctx_->index--;
}

ssize_t sscanf(const char *str, const char *fmt, ...)
{
  va_list ap;
  ssize_t res;
  struct sscanf_ctx_s ctx = { str, 0 };

  va_start(ap, fmt);
  res = __scanf(&ctx, sscanf_in, sscanf_un, fmt, ap);
  va_end(ap);

  return res;
}

ssize_t vsscanf(const char *str, const char *fmt, va_list ap)
{
  struct sscanf_ctx_s ctx = { str, 0 };
  return __scanf(&ctx, sscanf_in, sscanf_un, fmt, ap);
}

#ifdef CONFIG_LIBC_STREAM

static SCANF_INPUT_FUNC(fscanf_in)
{
  return fgetc(ctx);
}

static SCANF_UNGET_FUNC(fscanf_un)
{
  ungetc(c, ctx);
}

ssize_t fscanf(FILE *file, const char *fmt, ...)
{
  va_list ap;
  ssize_t res;

  va_start(ap, fmt);
  res = __scanf(file, fscanf_in, fscanf_un, fmt, ap);
  va_end(ap);

  return res;
}

ssize_t vfscanf(FILE *file, const char *fmt, va_list ap)
{
  return __scanf(file, fscanf_in, fscanf_un, fmt, ap);
}

# ifdef CONFIG_LIBC_STREAM_STD

ssize_t scanf(const char *fmt, ...)
{
  va_list ap;
  ssize_t res;

  va_start(ap, fmt);
  res = __scanf(stdin, fscanf_in, fscanf_un, fmt, ap);
  va_end(ap);

  return res;
}

ssize_t vscanf(const char *fmt, va_list ap)
{
  return __scanf(stdin, fscanf_in, fscanf_un, fmt, ap);
}

# endif

#endif
