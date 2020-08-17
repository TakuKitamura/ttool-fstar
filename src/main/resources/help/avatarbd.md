# AVATAR block diagram

An AVATAR block diagram can be sued to describe the dtructure of a software in an abstract way.

The following modeling elements can be used for this purpose:

- **Blocks**. A block corresponds to a software component. A block defines its own private attributes, methods and signals.

- **Crypto blocks**. Crypto blocks are blocks containing cryptographic methods

- **Data type blocks**. They can be used to define custom data types built upon baisc data types (integers, booleans).

- **Library functions** are meant to factorize the bahavior of blocks

-  **Crypto Library functions** are library functions containing a set of cryptographic methods.



Additionally, properties to be verified by this design are captured using pragmas:

- [Safety pragmas](file://avatarsafetypragmas.md)

- Security pragmas

- Performance pragmas