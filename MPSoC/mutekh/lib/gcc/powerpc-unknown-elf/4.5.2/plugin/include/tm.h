#ifndef GCC_TM_H
#define GCC_TM_H
#ifdef IN_GCC
# include "options.h"
# include "config/rs6000/rs6000.h"
# include "config/dbxelf.h"
# include "config/elfos.h"
# include "config/usegas.h"
# include "config/svr4.h"
# include "config/freebsd-spec.h"
# include "config/newlib-stdint.h"
# include "config/rs6000/sysv4.h"
# include "config/rs6000/option-defaults.h"
# include "defaults.h"
#endif
#if defined IN_GCC && !defined GENERATOR_FILE && !defined USED_FOR_TARGET
# include "insn-constants.h"
# include "insn-flags.h"
#endif
#endif /* GCC_TM_H */
