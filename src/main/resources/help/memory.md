# Memory

Memory nodes are helpful to model storage capabilities of architectures

## Main attributes

They have the following attributes.

- A name
- A data size in byte.
- They can be monitored (SoCLiB simulation only) using VCI probes.
- A clock divider expresses the relation between the clock of the memory and the main clock of the architecture

Apart from the monitor attribute, all latter attributes are taken into account by the DIPLODOCUS simulator to determine communication paths.

## Other attributes

### Code generation

The following attributes are also defined in Memories:

- A memory extension construct: used for specific applications

- The size of the memory
