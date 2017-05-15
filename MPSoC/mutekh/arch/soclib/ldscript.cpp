/* The following contains information extracted from the deployment diagram */

#include <arch/soclib/deployinfo.h>

/*
  We may have aliasing in rom/except/boot segments. If so, we merge
  all we can in mem_rom segment.

  We may also have alisasing of ram/rom (for bootloaded kernels), so
  we may merge all we can in mem_ram.
 */

/* ram + rom */
#if CONFIG_RAM_ADDR == CONFIG_ROM_ADDR
# define mem_rom mem_ram
# if defined(CONFIG_CPU_RESET_HANDLER)
#  error You may not have a reset handler with rom in ram
# endif
#endif

/* The first two may only happen if reset handler is present */
#if defined(CONFIG_CPU_RESET_HANDLER)

/* boot + rom */
# if CONFIG_CPU_RESET_ADDR == CONFIG_ROM_ADDR
#  define mem_boot mem_rom
# endif

/* exception + boot
 *
 * boot may already be rom, and we'll have cascading defines */
# if defined(CONFIG_CPU_EXCEPTION_FIXED_ADDRESS) && \
     (CONFIG_CPU_EXCEPTION_FIXED_ADDRESS == CONFIG_CPU_RESET_ADDR)
#  define mem_except mem_boot
# endif

#endif /* end if reset handler */


/* exception + rom */
#if ( (CONFIG_CPU_EXCEPTION_FIXED_ADDRESS == CONFIG_ROM_ADDR) ||   \
	  !defined(CONFIG_CPU_EXCEPTION_FIXED_ADDRESS) ) &&			   \
    !defined(mem_except)
# define mem_except mem_rom
#endif


/*
  Implement .data from rom (copied at boot) by putting all r/w data at
  end of mem_rom. This is used for rom-only bootloaders.
 */
#if defined(CONFIG_DATA_FROM_ROM)
# define __AT_MEM_ROM AT>mem_rom
#else
# define __AT_MEM_ROM
#endif


MEMORY
{
#if defined(CONFIG_CPU_RESET_HANDLER) && !defined(mem_boot)
	mem_boot (RXAL)  : ORIGIN = CONFIG_CPU_RESET_ADDR, LENGTH = CONFIG_CPU_RESET_SIZE
#endif
#if !defined(mem_except)
	mem_except (RXAL)  : ORIGIN = CONFIG_CPU_EXCEPTION_FIXED_ADDRESS, LENGTH = 0x1000
#endif
#ifdef CONFIG_HET_BUILD
    mem_hetrom (RXAL): ORIGIN = CONFIG_HETROM_ADDR, LENGTH = CONFIG_HETROM_SIZE
#else
# define mem_hetrom mem_rom
#endif
#if !defined(mem_rom)
    mem_rom (RXAL): ORIGIN = CONFIG_ROM_ADDR, LENGTH = CONFIG_ROM_SIZE
#endif
    mem_ram (RWAL): ORIGIN = CONFIG_RAM_ADDR, LENGTH = CONFIG_RAM_SIZE
//ajoute DG provisiore
//mwmr_ram (RWAL): ORIGIN = 0xA0200000, LENGTH = 0x00001000
//mwmrd_ram (RWAL): ORIGIN = 0xB0200000, LENGTH = 0x00003000
//19.05. une seule RAMLOCKS en cas de besoin (actually unused)
vci_locks (RWAL): ORIGIN = 0xC0200000, LENGTH = 0x100
//ajoute DG
#if defined(DEPLOY_RAM0_NAME)
    DEPLOY_RAM0_NAME (RWAL): ORIGIN = DEPLOY_RAM0_ADDR, LENGTH = DEPLOY_RAM0_SIZE
#endif
#if defined(CACHED_RAM0_NAME)
    CACHED_RAM0_NAME (RWAL): ORIGIN = CACHED_RAM0_ADDR, LENGTH = CACHED_RAM0_SIZE
#endif
#if defined(DEPLOY_RAM1_NAME)
    DEPLOY_RAM1_NAME (RWAL): ORIGIN = DEPLOY_RAM1_ADDR, LENGTH = DEPLOY_RAM1_SIZE
#endif
#if defined(CACHED_RAM1_NAME)
    CACHED_RAM1_NAME (RWAL): ORIGIN = CACHED_RAM1_ADDR, LENGTH = CACHED_RAM1_SIZE
#endif
#if defined(DEPLOY_RAM2_NAME)
    DEPLOY_RAM2_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM2_NAME)
    CACHED_RAM2_NAME (RWAL): ORIGIN = CACHED_RAM2_ADDR, LENGTH = CACHED_RAM2_SIZE
#endif
#if defined(DEPLOY_RAM3_NAME)
    DEPLOY_RAM3_NAME (RWAL): ORIGIN = DEPLOY_RAM3_ADDR, LENGTH = DEPLOY_RAM3_SIZE
#endif
#if defined(DEPLOY_RAM4_NAME)
    DEPLOY_RAM4_NAME (RWAL): ORIGIN = DEPLOY_RAM4_ADDR, LENGTH = DEPLOY_RAM4_SIZE
#endif
#if defined(DEPLOY_RAM5_NAME)
    DEPLOY_RAM5_NAME (RWAL): ORIGIN = DEPLOY_RAM5_ADDR, LENGTH = DEPLOY_RAM5_SIZE
#endif
#if defined(DEPLOY_RAM6_NAME)
    DEPLOY_RAM6_NAME (RWAL): ORIGIN = DEPLOY_RAM6_ADDR, LENGTH = DEPLOY_RAM6_SIZE
#endif
#if defined(DEPLOY_RAM7_NAME)
    DEPLOY_RAM7_NAME (RWAL): ORIGIN = DEPLOY_RAM7_ADDR, LENGTH = DEPLOY_RAM7_SIZE
#endif
#if defined(DEPLOY_RAM8_NAME)
    DEPLOY_RAM8_NAME (RWAL): ORIGIN = DEPLOY_RAM8_ADDR, LENGTH = DEPLOY_RAM8_SIZE
#endif
#if defined(DEPLOY_RAM9_NAME)
    DEPLOY_RAM9_NAME (RWAL): ORIGIN = DEPLOY_RAM9_ADDR, LENGTH = DEPLOY_RAM9_SIZE
#endif
#if defined(DEPLOY_RAM10_NAME)
    DEPLOY_RAM10_NAME (RWAL): ORIGIN = DEPLOY_RAM0_ADDR, LENGTH = DEPLOY_RAM0_SIZE
#endif
#if defined(CACHED_RAM10_NAME)
    CACHED_RAM10_NAME (RWAL): ORIGIN = CACHED_RAM0_ADDR, LENGTH = CACHED_RAM0_SIZE
#endif
#if defined(DEPLOY_RAM11_NAME)
    DEPLOY_RAM11_NAME (RWAL): ORIGIN = DEPLOY_RAM1_ADDR, LENGTH = DEPLOY_RAM1_SIZE
#endif
#if defined(CACHED_RAM11_NAME)
    CACHED_RAM1_NAME (RWAL): ORIGIN = CACHED_RAM1_ADDR, LENGTH = CACHED_RAM1_SIZE
#endif
#if defined(DEPLOY_RAM12_NAME)
    DEPLOY_RAM12_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM12_NAME)
    CACHED_RAM12_NAME (RWAL): ORIGIN = CACHED_RAM2_ADDR, LENGTH = CACHED_RAM2_SIZE
#endif
#if defined(DEPLOY_RAM13_NAME)
    DEPLOY_RAM13_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM13_NAME)
    CACHED_RAM13_NAME (RWAL): ORIGIN = CACHED_RAM3_ADDR, LENGTH = CACHED_RAM3_SIZE
#endif
#if defined(DEPLOY_RAM14_NAME)
    DEPLOY_RAM13_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM14_NAME)
    CACHED_RAM4_NAME (RWAL): ORIGIN = CACHED_RAM4_ADDR, LENGTH = CACHED_RAM4_SIZE
#endif
#if defined(DEPLOY_RAM15_NAME)
    DEPLOY_RAM15_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM15_NAME)
    CACHED_RAM15_NAME (RWAL): ORIGIN = CACHED_RAM5_ADDR, LENGTH = CACHED_RAM5_SIZE
#endif
#if defined(DEPLOY_RAM16_NAME)
    DEPLOY_RAM16_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM16_NAME)
    CACHED_RAM16_NAME (RWAL): ORIGIN = CACHED_RAM6_ADDR, LENGTH = CACHED_RAM6_SIZE
#endif
#if defined(DEPLOY_RAM17_NAME)
    DEPLOY_RAM17_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM17_NAME)
    CACHED_RAM17_NAME (RWAL): ORIGIN = CACHED_RAM7_ADDR, LENGTH = CACHED_RAM7_SIZE
#endif
#if defined(DEPLOY_RAM18_NAME)
    DEPLOY_RAM18_NAME (RWAL): ORIGIN = DEPLOY_RAM0_ADDR, LENGTH = DEPLOY_RAM0_SIZE
#endif
#if defined(CACHED_RAM18_NAME)
    CACHED_RAM8_NAME (RWAL): ORIGIN = CACHED_RAM8_ADDR, LENGTH = CACHED_RAM8_SIZE
#endif
#if defined(DEPLOY_RAM19_NAME)
    DEPLOY_RAM19_NAME (RWAL): ORIGIN = DEPLOY_RAM0_ADDR, LENGTH = DEPLOY_RAM0_SIZE
#endif
#if defined(CACHED_RAM19_NAME)
    CACHED_RAM19_NAME (RWAL): ORIGIN = CACHED_RAM9_ADDR, LENGTH = CACHED_RAM9_SIZE
#endif
#if defined(DEPLOY_RAM20_NAME)
    DEPLOY_RAM20_NAME (RWAL): ORIGIN = DEPLOY_RAM0_ADDR, LENGTH = DEPLOY_RAM0_SIZE
#endif
#if defined(CACHED_RAM20_NAME)
    CACHED_RAM20_NAME (RWAL): ORIGIN = CACHED_RAM0_ADDR, LENGTH = CACHED_RAM0_SIZE
#endif
#if defined(DEPLOY_RAM21_NAME)
    DEPLOY_RAM21_NAME (RWAL): ORIGIN = DEPLOY_RAM1_ADDR, LENGTH = DEPLOY_RAM1_SIZE
#endif
#if defined(CACHED_RAM21_NAME)
    CACHED_RAM21_NAME (RWAL): ORIGIN = CACHED_RAM1_ADDR, LENGTH = CACHED_RAM1_SIZE
#endif
#if defined(DEPLOY_RAM22_NAME)
    DEPLOY_RAM22_NAME (RWAL): ORIGIN = DEPLOY_RAM2_ADDR, LENGTH = DEPLOY_RAM2_SIZE
#endif
#if defined(CACHED_RAM22_NAME)
    CACHED_RAM22_NAME (RWAL): ORIGIN = CACHED_RAM2_ADDR, LENGTH = CACHED_RAM2_SIZE
#endif
#if defined(DEPLOY_RAM23_NAME)
    DEPLOY_RAM23_NAME (RWAL): ORIGIN = DEPLOY_RAM3_ADDR, LENGTH = DEPLOY_RAM3_SIZE
#endif
#if defined(DEPLOY_RAM24_NAME)
    DEPLOY_RAM24_NAME (RWAL): ORIGIN = DEPLOY_RAM4_ADDR, LENGTH = DEPLOY_RAM4_SIZE
#endif
#if defined(DEPLOY_RAM25_NAME)
    DEPLOY_RAM25_NAME (RWAL): ORIGIN = DEPLOY_RAM5_ADDR, LENGTH = DEPLOY_RAM5_SIZE
#endif
#if defined(DEPLOY_RAM26_NAME)
    DEPLOY_RAM26_NAME (RWAL): ORIGIN = DEPLOY_RAM6_ADDR, LENGTH = DEPLOY_RAM6_SIZE
#endif
#if defined(DEPLOY_RAM27_NAME)
    DEPLOY_RAM27_NAME (RWAL): ORIGIN = DEPLOY_RAM7_ADDR, LENGTH = DEPLOY_RAM7_SIZE
#endif
#if defined(DEPLOY_RAM28_NAME)
    DEPLOY_RAM28_NAME (RWAL): ORIGIN = DEPLOY_RAM8_ADDR, LENGTH = DEPLOY_RAM8_SIZE
#endif
#if defined(DEPLOY_RAM29_NAME)
    DEPLOY_RAM29_NAME (RWAL): ORIGIN = DEPLOY_RAM9_ADDR, LENGTH = DEPLOY_RAM9_SIZE
#endif
//fin ajoute DG
}

SECTIONS
{
#if defined(CONFIG_CPU_RESET_HANDLER)
	.boot : {
		KEEP(*(.boot*))
	} > mem_boot
#endif

	.text : {
		*(.init*)
		*(.text*)
		*(.glue*)
		*(.got2)
	} > mem_hetrom

	.rodata : {
			*(.rodata*)
            . = ALIGN(4);
			global_driver_registry = .;
			KEEP(*(.drivers))
			global_driver_registry_end = .;
	} > mem_rom

	.excep : {
#if !defined(CONFIG_CPU_EXCEPTION_FIXED_ADDRESS)
		/* On some architectures, exception vector is freely
		 * relocatable up to a given alignment.
		 *
		 * We must set the correct pointer ASAP in the boot sequence,
		 * dont forget reset vector is optional...
		 */
		. = ALIGN(CONFIG_CPU_EXCEPTION_ALIGN);
#endif
		__exception_base_ptr = .;
		KEEP(*(.excep*))
	} > mem_except


	/* TLS/CLS are templates for newly allocated contexts/cpu's
	 * private data. They are always read-only.
	 *
	 * On a non-smp machine, cpudata is read-write, but does not fall
	 * in the cpudata section (it is normal global data), so we can
	 * keep on linking this as r/o.
	 */

	/* CPU local data section */
	.cpudata  0x0 : { *(.cpudata*) } AT> mem_rom

	__cpu_data_start = LOADADDR(.cpudata);
	__cpu_data_end = LOADADDR(.cpudata) + SIZEOF(.cpudata);

	/* Task local data section */
	.contextdata  0x0 : { *(.contextdata*) } AT> mem_rom

	__context_data_start = LOADADDR(.contextdata);
	__context_data_end = LOADADDR(.contextdata) + SIZEOF(.contextdata);

	.data :	{
		__data_start = ABSOLUTE(.);
		*(.sdata*)
		*(.data*)
		*(.cpuarchdata*)
	} > mem_ram __AT_MEM_ROM
//ajoute DG
#include <arch/soclib/deployinfo_map.h>
//MAP_A
//DG 19.5. a single RAMLOCKS
// .lock0 : { *(section_lock0)} > vci_locks
//fin ajoute DG
	__data_load_start = LOADADDR(.data);
	__data_load_end = LOADADDR(.data) + SIZEOF(.data);

// #if defined(CONFIG_HET_BUILD)
//     /DISCARD/ : {
// #else
    .bss : {
		__bss_start = ABSOLUTE(.);
// #endif
		*(.sbss*)
		*(COMMON)
		*(.common*)
		*(.scommon*)
		*(.bss*)
// #if !defined(CONFIG_HET_BUILD)
		__bss_end = ABSOLUTE(.);
	} > mem_ram
// #else
//     }

//     __bss_end = 0;
//     __bss_start = 0;
// #endif

	__system_uncached_heap_start = .;
	__system_uncached_heap_end = ORIGIN(mem_ram) + LENGTH(mem_ram);

#if defined(CONFIG_CPU_RESET_HANDLER)
	. = ALIGN(CONFIG_HEXO_STACK_ALIGN);
	__initial_stack = __system_uncached_heap_end;
#endif

	/* GOT section */
 	/DISCARD/ : { *(.eh_frame) }

#if !defined(CONFIG_CPU_NIOS2)
 	ASSERT(__system_uncached_heap_start == __bss_end, "Unlinked sections found, please report a bug")
#endif
}

ENTRY(arch_init)
