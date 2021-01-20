# Bridge

A bridge node interconnects two [Bus](file://bus.md) nodes.

## Main attributes

A bridge nos has the  following attributes.

- A name
- An arbitration policy capturing how simultaneous trafsre request are handled
- A buffer size, i.e. the maximum amount of data (in bytes) that can be stored in the bridge.
- A clock divider expresses the relation between the clock of the memory and the main clock of the architecture


