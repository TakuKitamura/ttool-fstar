# Bus

Bus nodes connect other nodes together.

## Main attributes

They have the following attributes.

- A name
- An arbitration policy capturing how simultaneous trafsre request are handled
- A data size in byte.
- A pipeline size expressing the number of cycles spent by one transfer in this bus
- A slice time, i.e. the maximum number of cycles a transfer can use before being preempted
- A clock divider expresses the relation between the clock of the memory and the main clock of the architecture
- A bus privacy. A public bus can be spied at, while a private bus cannot. On a public bus, an attacker can reive messages and inject messages.
- A reference attack specifies which attack of attakc trees this bus relates to.
