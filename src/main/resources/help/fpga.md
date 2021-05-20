# FPGA

The FPGA node abstracts programmable Field-programmable gate array.

## Attributes

They have the following attributes.

- _FPGA name_

- _Data size_ gives the size of data that can manipulated in one clock cycle. For a FPGA, this represents for instance the size of the bus to access
  the internal memory

- _Mapping penalty_ gives the number of cycles before tasks can start

- _The reconfiguration time_, given in ms, models the time the FPGA takes to switch from a task set to another task set
- _Go idle time_ gives the number of cycles it takes for a FPGA to switch to a power saving mode, or to switch to regular power mode
- _Max consecutive cycles before going idle_ expresses the number of cycles before the FPGA goes to a power saving mode
- _ExecI_ gives the number of clock cycles the FPGA takes to execute one integer operation (average value)
- _ExecC_ gives the number of clock cycles the FPGA takes to execute one _custom_ operation (average value)
- _Operation_ documents the typical operations performed by this FPGA. This could be "JPEG", "LDPC" or else.
- _Scheduling_ expresses how tasks are scheduled. Next section focuses on this attribute
- _Clock divider_ gives the ratio between the master clock and the clock of the FPGA

## Scheduling

Let us assume that n tasks have been mapped to a FPGA.

If the _Scheduling_ attribute is left empty, the system assumes that all tasks mapped to the FPGA can be executed at the same time, in parallel. Said
differently, the designer thinks that all tasks can fit at the same time in the FPGA.

IF all tasks cannot fit at the same time in the FPGA matrix, then the design can give a static schedule that represents in which sequence tasks are
executed.

A scheduling is described by sets of tasks executed together, separated by ";".

For instance:
`T1 T2 ; T3 ; T4 T5`

A ";" means that the FPGA performs a dynamic reconfiguration before
switching to the next set of tasks. The FPGA switches to the next set of tasks only once the tasks of the previous set have all terminated their
execution.
