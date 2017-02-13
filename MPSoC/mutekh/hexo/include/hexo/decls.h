#ifndef HEXO_DECLS_H_
#define HEXO_DECLS_H_

/* warp C header in C++ */
# define _GNUC_VERSION      (__GNUC__ * 10000 + __GNUC_MINOR__ * 100 + __GNUC_PATCHLEVEL__)

# if __cplusplus &&! defined(__MUTEK_ASM__)
#  define C_HEADER_BEGIN extern "C" {
#  define C_HEADER_END }
# else
#  define C_HEADER_BEGIN
#  define C_HEADER_END
# endif

/* make unavailable functions deprecated */

# ifndef __MUTEK_ASM__ // mkdoc:skip

#  define _CONFIG_DEPEND_1(name, attr, proto, ...) \
  attr proto __VA_ARGS__
#  if _GNUC_VERSION >= 40500
#   define _CONFIG_DEPEND_0(name, attr, proto, ...) \
  __attribute__((deprecated("this symbol depends on " name ", not defined in configuration"))) \
  proto
#  else
#   define _CONFIG_DEPEND_0(name, attr, proto, ...) \
  __attribute__((deprecated)) \
  proto
#  endif

#  define _CONFIG_DEPEND_AND_00(name, attr, proto, ...) _CONFIG_DEPEND_0(name, attr, proto, __VA_ARGS__)
#  define _CONFIG_DEPEND_AND_01(name, attr, proto, ...) _CONFIG_DEPEND_0(name, attr, proto, __VA_ARGS__)
#  define _CONFIG_DEPEND_AND_10(name, attr, proto, ...) _CONFIG_DEPEND_0(name, attr, proto, __VA_ARGS__)
#  define _CONFIG_DEPEND_AND_11(name, attr, proto, ...) _CONFIG_DEPEND_1(name, attr, proto, __VA_ARGS__)

#  define _CONFIG_DEPEND_PASTE2(a, b) a ## b
#  define _CONFIG_DEPEND_PASTE3(a, b, c) a ## b ## c

#  define _CONFIG_DEPEND(a, b, attr, proto, ...) \
  _CONFIG_DEPEND_PASTE2(_CONFIG_DEPEND_, b)(a, attr, proto, __VA_ARGS__)
#  define _CONFIG_DEPEND_AND2(a1, a2, b1, b2, attr, proto, ...)            \
  _CONFIG_DEPEND_PASTE3(_CONFIG_DEPEND_AND_, b1, b2)(a1 " and " a2, attr, proto, __VA_ARGS__)

#  define config_depend(token) \
  _CONFIG_DEPEND(#token, _##token, , , )
#  define config_depend_inline(token, proto, ...) \
  _CONFIG_DEPEND(#token, _##token, static inline, proto, __VA_ARGS__)

#  define config_depend_and2(token1, token2) \
  _CONFIG_DEPEND_AND2(#token1, #token2, _##token1, _##token2, , , )
#  define config_depend_and2_inline(token1, token2, proto, ...) \
  _CONFIG_DEPEND_AND2(#token1, #token2, _##token1, _##token2, static inline, proto, __VA_ARGS__)

# endif

#ifdef __MKDOC__
# define config_depend(token)
# define config_depend_inline(token, proto, ...) static inline proto __VA_ARGS__
# define config_depend_and2(token1, token2)
# define config_depend_and2_inline(token1, token2, proto, ...) static inline proto __VA_ARGS__
#endif

#endif /* HEXO_DECLS_H_ */
