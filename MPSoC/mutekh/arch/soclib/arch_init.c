/*
   This file is part of MutekH.
   
   MutekH is free software; you can redistribute it and/or modify it
   under the terms of the GNU Lesser General Public License as published
   by the Free Software Foundation; version 2.1 of the License.
   
   MutekH is distributed in the hope that it will be useful, but WITHOUT
   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
   License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with MutekH; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
   02110-1301 USA.

   Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006

*/

#include <assert.h>

#if defined(CONFIG_ARCH_DEVICE_TREE)
# include <drivers/enum/fdt/enum-fdt.h>
# include <device/enum.h>
# include <device/driver.h>
# include <device/device.h>
# include <mutek/fdt.h>
#endif

#include <arch/mem_checker.h>

#if defined(CONFIG_SOCLIB_EARLY_CONSOLE)
void soclib_early_console(uintptr_t addr);
#endif

#include <hexo/types.h>
#include <hexo/init.h>
#include <hexo/lock.h>
#include <hexo/cpu.h>
#include <mutek/printk.h>
#include <mutek/scheduler.h>
#include <hexo/context.h>

#include <string.h>

#if defined(CONFIG_ARCH_DEVICE_TREE)
struct device_s fdt_enum_dev;
extern void *arch_fdt;
#endif

#ifdef CONFIG_HEXO_MMU
#include <hexo/mmu.h>

extern __ldscript_symbol_t __system_uncached_heap_start, __system_uncached_heap_end;
#endif

#define START_MAGIC 0x67c51264

#ifdef CONFIG_ARCH_SMP
static uint_fast8_t cpu_count = 1;
uint32_t     cpu_start_flag = 0;
static lock_t       cpu_init_lock;    /* cpu intialization lock */
/* integer atomic operations global spin lock */
lock_t              __atomic_arch_lock;
#endif

#if defined (CONFIG_MUTEK_SCHEDULER)
extern struct sched_context_s main_ctx;
#else
struct context_s main_ctx;
#endif

static
void soclib_setup_memory()
{
#ifdef CONFIG_DATA_FROM_ROM
    extern __ldscript_symbol_t __data_start;
    extern __ldscript_symbol_t __data_load_start;
    extern __ldscript_symbol_t __data_load_end;

    soclib_mem_check_region_status(
        (uint8_t*)&__data_start,
        (uint8_t*)&__data_load_end-(uint8_t*)&__data_load_start,
        SOCLIB_MC_REGION_GLOBAL);
    memcpy_from_code(
        (uint8_t*)&__data_start,
        (uint8_t*)&__data_load_start,
        (uint8_t*)&__data_load_end-(uint8_t*)&__data_load_start);
#endif

#ifdef CONFIG_ARCH_SMP
    lock_init(&__atomic_arch_lock);
    lock_init(&cpu_init_lock);
#endif
}

static
void soclib_vmem_init()
{
#ifdef CONFIG_HEXO_MMU
    uint32_t t0=(uint32_t)(&__system_uncached_heap_start);
    uint32_t t1=(uint32_t)(&__system_uncached_heap_end);
    t0+=CONFIG_SOCLIB_VMEM_MALLOC_REGION_SIZE;

# ifdef CONFIG_VMEM_PHYS_ALLOC
    vmem_ppage_ops_init(&vmem_ops);
    initial_ppage_region = vmem_ops.ppage_initial_region_get();
    vmem_ops.ppage_region_init(initial_ppage_region, t0, t1);
# endif

# ifdef CONFIG_VMEM_KERNEL_ALLOC
    vmem_vpage_ops_init(&vmem_ops);
    vmem_ops.vpage_init();
# endif

#endif /*CONFIG_HEXO_MMU*/

#ifdef CONFIG_HEXO_MMU
    mmu_cpu_init();
#endif
}

static
void hw_init()
{
#if defined(CONFIG_ARCH_DEVICE_TREE)
    device_init(&fdt_enum_dev);
    enum_fdt_init(&fdt_enum_dev, arch_fdt);
    mutek_parse_fdt(&fdt_enum_dev, arch_fdt);
    //TODO: change with mem_parse_fdt when lib topology is done
    mem_region_init();
    //        mem_parse_fdt(arch_fdt);
#elif defined(CONFIG_ARCH_HW_INIT_USER)
    user_hw_init();
#elif defined(CONFIG_ARCH_HW_INIT)
# error CONFIG_ARCH_HW_INIT unsupported for SoCLib platforms
#endif
}

static inline void start_other_cpus(void);

/**
   @this is the function run by the bootstrap CPU of the platform. It
   initializes all the hardware.
 */
static
void arch_init_bootstrap(uintptr_t init_sp)
{
    soclib_setup_memory();

#if defined(CONFIG_SOCLIB_EARLY_CONSOLE)
    soclib_early_console(CONFIG_SOCLIB_EARLY_CONSOLE_ADDR);
#endif

    /* configure system wide cpu data */
    cpu_global_init();
    mem_init();
    hexo_global_init();
    soclib_vmem_init();

    hw_init();

    /* configure first CPU */
    cpu_init();

#if defined(CONFIG_ARCH_DEVICE_TREE)
# if defined(CONFIG_HEXO_IRQ)
    struct device_s *icu = enum_fdt_icudev_for_cpuid(&fdt_enum_dev, cpu_id());
    if ( icu )
        cpu_interrupt_sethandler_device(icu);
# endif
#endif

    start_other_cpus();

#if defined(CONFIG_MUTEK_SCHEDULER)
    sched_global_init();
    sched_cpu_init();

    /* FIXME initial stack space will never be freed ! */
    context_bootstrap(&main_ctx.context, 0, init_sp);
    sched_context_init(&main_ctx);
#else
    context_bootstrap(&main_ctx, 0, init_sp);
#endif

#ifdef CONFIG_ARCH_SMP
    uint_fast8_t last_count = 0;
        
    for ( ;; ) {
        order_compiler_mem();

        if ( last_count == cpu_count )
            break;
        last_count = cpu_count;

        if ( last_count == CONFIG_CPU_MAXCOUNT )
            break;
            
        cpu_cycle_wait(100000);
    }
#endif

    /* run mutek_start() */
    mutek_start();
}

#ifdef CONFIG_ARCH_SMP
/**
   @this is the function run by all CPUs other than the bootstrap one.
 */
static
void arch_init_other()
{
    assert(cpu_id() < CONFIG_CPU_MAXCOUNT);

    /* configure other CPUs */
    lock_spin(&cpu_init_lock);

#ifdef CONFIG_HEXO_MMU
    mmu_cpu_init();
#endif      
    cpu_init();
        

#if defined(CONFIG_ARCH_DEVICE_TREE) && defined(CONFIG_HEXO_IRQ)
    struct device_s *icu = enum_fdt_icudev_for_cpuid(&fdt_enum_dev, cpu_id());
    if ( icu )
        cpu_interrupt_sethandler_device(icu);
#endif

    ++cpu_count;

    lock_release(&cpu_init_lock);

    /* wait for start signal */

    while (cpu_start_flag != START_MAGIC)
        order_compiler_mem();

    /* run mutek_start_smp() */

#if defined(CONFIG_MUTEK_SCHEDULER)
    sched_cpu_init();
#endif

    /* FIXME should have context_bootstrap for non bsp processors,
       especially when CONFIG_MUTEK_SMP_APP_START is defined. */

    mutek_start_smp();
}
#endif

static inline
void start_other_cpus(void)
{
#ifdef CONFIG_ARCH_SMP
    size_t i;
    for ( i=0; i<CONFIG_CPU_MAXCOUNT; ++i ) {
        if ( i == cpu_id() )
            continue;
# if defined(CONFIG_ARCH_SOCLIB_BOOTLOADER_MUTEKH)
        error_t err = enum_fdt_wake_cpuid(&fdt_enum_dev, i, arch_init_other);
        if (err)
            printk("Error waking cpu %d from FDT: %s\n", i, strerror(err));
# endif
# if defined(CONFIG_CPU_RESET_HANDLER)
        extern void start_barrier_release(cpu_id_t cpu);
        start_barrier_release(i);
# endif
    }
#endif
}

/* architecture specific init function */
void arch_init(uintptr_t init_sp)
{
#ifdef CONFIG_ARCH_SMP
    if (cpu_isbootstrap())
        arch_init_bootstrap(init_sp);
    else
        arch_init_other();
#else
    arch_init_bootstrap(init_sp);
#endif
}

void arch_start_other_cpu(void)
{
#ifdef CONFIG_ARCH_SMP
    cpu_start_flag = START_MAGIC;
#endif
}

inline size_t arch_get_cpu_count(void)
{
#ifdef CONFIG_ARCH_SMP
    return cpu_count;
#else
    return 1;
#endif
}

// Local Variables:
// tab-width: 4;
// c-basic-offset: 4;
// c-file-offsets:((innamespace . 0)(inline-open . 0));
// indent-tabs-mode: nil;
// End:
//
// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

