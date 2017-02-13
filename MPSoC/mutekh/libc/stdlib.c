
#include <stdlib.h>
#include <assert.h>
#include <mutek/printk.h>

void exit(uint_fast8_t status)
{
  printk("called exit() with %i. aborting ...", status);
  abort();
}

error_t atexit(void (*function)(void))
{
  /* FIXME */
  return -1;
}

void abort(void)
{
  cpu_trap();

  while (1)
    ;
}

void *bsearch(
  const void *key,
  const void *_base,
  size_t max,
  size_t width,
  __compiler_sint_t (*compar) (const void *, const void *)
  )
{
    const char *base = _base;
    size_t min = 0;
    size_t cur = (max + min) / 2;
        
    while ( cur < max ) {
        const void *ptr = base + cur * width;
        __compiler_sint_t cmp = compar(key, ptr);

        if ( cmp == 0 )
            return (void*)ptr;

        if ( cmp <= 0 )
            max = cur;
        else
            min = cur + 1;

        size_t tmp = (max + min) / 2;
        if ( cur == tmp )
            break;
        cur = tmp;
    }
    return NULL;
}

char *getenv(const char *key)
{
  return NULL;
}

error_t system(const char *cmd)
{
  return -1;
}


#define div_algo(i_t, result, dividend, divisor)                       \
    i_t remainder_sign = 1, quotient_sign = 1;                         \
                                                                       \
    if (dividend < 0) {                                                \
        dividend = -dividend;                                          \
        remainder_sign = -1;                                           \
    }                                                                  \
    if (divisor < 0) {                                                 \
        divisor = -divisor;                                            \
        quotient_sign = -1;                                            \
    }                                                                  \
                                                                       \
    result.quot = dividend;                                            \
    result.rem = 0;                                                    \
                                                                       \
    size_t i;                                                          \
    for ( i = 0; i < sizeof(i_t) * 8; ++i ) {                          \
        result.rem <<= 1;                                              \
        if (result.quot < 0)                                           \
            result.rem |= 1;                                           \
        result.quot <<= 1;                                             \
                                                                       \
        if (result.rem >= divisor) {                                   \
            result.rem -= divisor;                                     \
            result.quot++;                                             \
        }                                                              \
    }                                                                  \
                                                                       \
    result.quot *= quotient_sign * remainder_sign;;                    \
    result.rem *= remainder_sign;


div_t div(__compiler_sint_t dividend, __compiler_sint_t divisor)
{
    div_t result;

    div_algo(__compiler_sint_t, result, dividend, divisor);

    return result;
}

ldiv_t ldiv(__compiler_slong_t dividend, __compiler_slong_t divisor)
{
    ldiv_t r;

    div_algo(__compiler_sint_t, r, dividend, divisor);

    return r;
}
