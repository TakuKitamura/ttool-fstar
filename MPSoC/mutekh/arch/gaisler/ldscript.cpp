
MEMORY
{
#if CONFIG_ROM_ADDR != CONFIG_RAM_ADDR
    mem_rom (RXAL): ORIGIN = CONFIG_ROM_ADDR, LENGTH = CONFIG_ROM_SIZE
#else
# define mem_rom mem_ram
#endif
    mem_ram (RWAL): ORIGIN = CONFIG_RAM_ADDR, LENGTH = CONFIG_RAM_SIZE
}

SECTIONS
{
	.boot : {
		. = ALIGN(CONFIG_CPU_EXCEPTION_ALIGN);
		__exception_base_ptr = .;
		KEEP(*(.excep*))
	} > mem_rom

        . = ALIGN(16);

      .text : {
		*(.init*)
		*(.text*)
		*(.glue*)
		*(.got2)
		  . = ALIGN(16);
	} > mem_rom

	.rodata : {
			*(.rodata*)
            . = ALIGN(32);
			global_driver_registry = .;
			KEEP(*(.drivers))
			global_driver_registry_end = .;
		  . = ALIGN(16);
	} > mem_rom

	/* TLS/CLS are templates for newly allocated contexts/cpu's
	 * private data. They are always read-only.
	 *
	 * On a non-smp machine, cpudata is read-write, but does not fall
	 * in the cpudata section (it is normal global data), so we can
	 * keep on linking this as r/o.
	 */

	/* CPU local data section */
        .cpudata  0x0 : { *(.cpudata*) 
		. = ALIGN(16);
        } AT > mem_rom

	__cpu_data_start = LOADADDR(.cpudata);
	__cpu_data_end = LOADADDR(.cpudata) + SIZEOF(.cpudata);

	/* Task local data section */
	.contextdata  0x0 : { *(.contextdata*) 
		. = ALIGN(16);
	} AT > mem_rom

	__context_data_start = LOADADDR(.contextdata);
	__context_data_end = LOADADDR(.contextdata) + SIZEOF(.contextdata);

	.data  :	{
		__data_start = ABSOLUTE(.);
		*(.sdata*)
		*(.data*)
		*(.cpuarchdata*)
		. = ALIGN(16);
	} > mem_ram

	__data_load_start = LOADADDR(.data);
	__data_load_end = LOADADDR(.data) + SIZEOF(.data);

	.bss  : {
		__bss_start = ABSOLUTE(.);
		*(.sbss*)
		*(COMMON)
		*(.common*)
		*(.scommon*)
		*(.bss*)
		. = ALIGN(16);
		__bss_end = ABSOLUTE(.);
	} > mem_ram

	__system_uncached_heap_start = .;
	__system_uncached_heap_end = ORIGIN(mem_ram) + LENGTH(mem_ram);

#if defined(CONFIG_CPU_RESET_HANDLER)
	. = ALIGN(CONFIG_HEXO_STACK_ALIGN);
	__initial_stack = __system_uncached_heap_end;
#endif

	/* GOT section */
 	/DISCARD/ : { *(.eh_frame) }

 	ASSERT(__system_uncached_heap_start == __bss_end, "Unlinked sections found, please report a bug")
}

ENTRY(cpu_boot)
