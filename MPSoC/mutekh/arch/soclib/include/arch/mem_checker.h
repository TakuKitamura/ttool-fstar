
#ifndef SOCLIB_MEM_CHECKER_H_
#define SOCLIB_MEM_CHECKER_H_

/** Magic value must be stored here to enable other registers. 0 must
    be stored to exit magix state. */
#define SOCLIB_MC_MAGIC_OFFSET 0
#define SOCLIB_MC_MAGIC (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_MAGIC_OFFSET)
/** Magic value */
# define SOCLIB_MC_MAGIC_VAL 0x4d656d63

/** Data register 1, depends on action performed */
#define SOCLIB_MC_R1_OFFSET 4
#define SOCLIB_MC_R1 (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_R1_OFFSET)

/** Data register 2, depends on action performed */
#define SOCLIB_MC_R2_OFFSET 8
#define SOCLIB_MC_R2 (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_R2_OFFSET)

/** Creates a new context: R1 = base, R2 = size, value = id */
#define SOCLIB_MC_CTX_CREATE_OFFSET 12
#define SOCLIB_MC_CTX_CREATE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_CTX_CREATE_OFFSET)

/** Delete a context: value = id */
#define SOCLIB_MC_CTX_DELETE_OFFSET 16
#define SOCLIB_MC_CTX_DELETE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_CTX_DELETE_OFFSET)

/** Change a context id: R1 = old_id, value = new_id */
#define SOCLIB_MC_CTX_CHANGE_OFFSET 20
#define SOCLIB_MC_CTX_CHANGE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_CTX_CHANGE_OFFSET)

/** Set current context id */
#define SOCLIB_MC_CTX_SET_OFFSET 24
#define SOCLIB_MC_CTX_SET (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_CTX_SET_OFFSET)

/** Set allocated region state: R1 = base, R2 = size, value = state */
#define SOCLIB_MC_REGION_UPDATE_OFFSET 28
#define SOCLIB_MC_REGION_UPDATE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_REGION_UPDATE_OFFSET)
/** Region state is free */
# define SOCLIB_MC_REGION_FREE 1
/** Region state is allocated */
# define SOCLIB_MC_REGION_ALLOC 2
/** Region state is global data */
# define SOCLIB_MC_REGION_GLOBAL 8

/** Set enabled checks */
#define SOCLIB_MC_ENABLE_OFFSET 32
#define SOCLIB_MC_ENABLE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_ENABLE_OFFSET)

/** Set disabled checks */
#define SOCLIB_MC_DISABLE_OFFSET 36
#define SOCLIB_MC_DISABLE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_DISABLE_OFFSET)

/** Mark region as intialized */
#define SOCLIB_MC_INITIALIZED_OFFSET 40
#define SOCLIB_MC_INITIALIZED (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_INITIALIZED_OFFSET)

/** Invalidate current context */
#define SOCLIB_MC_CTX_INVALIDATE_OFFSET 44
#define SOCLIB_MC_CTX_INVALIDATE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_CTX_INVALIDATE_OFFSET)

/** Creates a temporary context */
#define SOCLIB_MC_CTX_CREATE_TMP_OFFSET 48
#define SOCLIB_MC_CTX_CREATE_TMP (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_CTX_CREATE_TMP_OFFSET)

/** Declare a spinlock adress */
#define SOCLIB_MC_LOCK_DECLARE_OFFSET 52
#define SOCLIB_MC_LOCK_DECLARE (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_LOCK_DECLARE_OFFSET)

/** Temporarily disable stack pointer range check until PC leaves [PC:val] range */
#define SOCLIB_MC_DELAYED_MAGIC_OFFSET 56
#define SOCLIB_MC_DELAYED_MAGIC (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_DELAYED_MAGIC_OFFSET)

/** Update PC interval where stack pointer range check is ignored
    with [r1:r2] range. Add to interval set when val!=0 */
#define SOCLIB_MC_BYPASS_SP_CHECK_OFFSET 60
#define SOCLIB_MC_BYPASS_SP_CHECK (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_BYPASS_SP_CHECK_OFFSET)

#define SOCLIB_MC_SET_SP_REG_OFFSET 64
#define SOCLIB_MC_SET_SP_REG (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_SET_SP_REG_OFFSET)

#define SOCLIB_MC_SET_FP_REG_OFFSET 68
#define SOCLIB_MC_SET_FP_REG (CONFIG_SOCLIB_MEMCHECK_ADDRESS + SOCLIB_MC_SET_FP_REG_OFFSET)

#define SOCLIB_MC_CTX_ID_UNKNOWN -1
#define SOCLIB_MC_CTX_ID_CURRENT -2

/** Stack pointer range must be checked */
# define SOCLIB_MC_CHECK_SP 1
/** Frame pointer range must be checked */
# define SOCLIB_MC_CHECK_FP 2
/** Memory initialization status must be checked */
# define SOCLIB_MC_CHECK_INIT 4
/** Memory allocation status must be checked */
# define SOCLIB_MC_CHECK_REGIONS 8
/** Critical sections checks */
# define SOCLIB_MC_CHECK_IRQS_LOCK 16

# if defined(CONFIG_COMPILE_FRAMEPTR)
#  define SOCLIB_MC_CHECK_SPFP (SOCLIB_MC_CHECK_SP | SOCLIB_MC_CHECK_FP)
# else
#  define SOCLIB_MC_CHECK_SPFP SOCLIB_MC_CHECK_SP
# endif

# if defined(CONFIG_HEXO_IRQ)
#  define SOCLIB_MC_CHECK_ALL (SOCLIB_MC_CHECK_INIT | SOCLIB_MC_CHECK_REGIONS | SOCLIB_MC_CHECK_SPFP | SOCLIB_MC_CHECK_IRQS_LOCK)
# else
#  define SOCLIB_MC_CHECK_ALL (SOCLIB_MC_CHECK_INIT | SOCLIB_MC_CHECK_REGIONS | SOCLIB_MC_CHECK_SPFP)
# endif

# ifndef __MUTEK_ASM__

# include <stdint.h>

#  if defined(CONFIG_SOCLIB_MEMCHECK)

# include <hexo/iospace.h>
# include <hexo/interrupt.h>
# include <hexo/ordering.h>

static inline __attribute__ ((always_inline)) void
soclib_mem_check_declare_lock(void *lock, uint32_t islock)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_R1, (uint32_t)lock);
  cpu_mem_write_32(SOCLIB_MC_LOCK_DECLARE, islock);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  CPU_INTERRUPT_RESTORESTATE;
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_change_id(uint32_t old_id, uint32_t new_id)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_R1, old_id);
  cpu_mem_write_32(SOCLIB_MC_CTX_CHANGE, new_id);
  cpu_mem_write_32(SOCLIB_MC_CTX_SET, new_id);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  CPU_INTERRUPT_RESTORESTATE;
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_create_ctx(uint32_t ctx_id, void *stack_start, void *stack_end)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_R1, (uint32_t)stack_start);
  cpu_mem_write_32(SOCLIB_MC_R2, (uint32_t)(stack_end - stack_start));
  cpu_mem_write_32(SOCLIB_MC_CTX_CREATE, ctx_id);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  CPU_INTERRUPT_RESTORESTATE;
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_delete_ctx(uint32_t ctx_id)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_CTX_DELETE, ctx_id);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  CPU_INTERRUPT_RESTORESTATE;
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_region_status(void *region, size_t size, uint32_t status)
{
  CPU_INTERRUPT_SAVESTATE_DISABLE;
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_R1, (uint32_t)region);
  cpu_mem_write_32(SOCLIB_MC_R2, size);
  cpu_mem_write_32(SOCLIB_MC_REGION_UPDATE, status);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  CPU_INTERRUPT_RESTORESTATE;
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_disable(uint32_t flags)
{
  order_compiler_mem();
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_DISABLE, flags);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  order_compiler_mem();
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_enable(uint32_t flags)
{
  order_compiler_mem();
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_ENABLE, flags);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  order_compiler_mem();
}

static inline __attribute__ ((always_inline)) void
soclib_mem_mark_initialized(void* addr, size_t size)
{
  size_t i;

  order_compiler_mem();
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  for ( i = 0; i < size; i += 4 )
    cpu_mem_write_32(SOCLIB_MC_INITIALIZED, (uintptr_t)addr + i);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  order_compiler_mem();
}

static inline __attribute__ ((always_inline)) void
soclib_mem_bypass_sp_check(void* pc_start, void *pc_end)
{
  order_compiler_mem();
  cpu_mem_write_32(SOCLIB_MC_MAGIC, SOCLIB_MC_MAGIC_VAL);
  cpu_mem_write_32(SOCLIB_MC_R1, (uint32_t)pc_start);
  cpu_mem_write_32(SOCLIB_MC_R2, (uint32_t)pc_end);
  cpu_mem_write_32(SOCLIB_MC_BYPASS_SP_CHECK, 1);
  cpu_mem_write_32(SOCLIB_MC_MAGIC, 0);
  order_compiler_mem();
}

#  else /* CONFIG_SOCLIB_MEMCHECK */

static inline __attribute__ ((always_inline)) void
soclib_mem_check_declare_lock(void *lock, uint32_t islock)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_change_id(uint32_t old_id, uint32_t new_id)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_create_ctx(uint32_t ctx_id, void *stack_start, void *stack_end)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_delete_ctx(uint32_t ctx_id)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_region_status(void *region, size_t size, uint32_t status)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_disable(uint32_t flags)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_check_enable(uint32_t flags)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_mark_initialized(void* addr, size_t size)
{
}

static inline __attribute__ ((always_inline)) void
soclib_mem_bypass_sp_check(uintptr_t pc_start, uintptr_t pc_end)
{
}

#  endif

# endif  /* __MUTEK_ASM__ */

#endif

