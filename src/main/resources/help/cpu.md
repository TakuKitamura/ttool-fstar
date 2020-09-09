# CPU


CPU nodes abstract multi-core processors.

## Main attributes

They have the following attributes.

- [CPU name](file://cpuname.md)
- [Scheduling policy](file://schedulingpolicy.md)
- [Slice time](file://slicetime.md) (given in milliseconds)
- [Nb of cores](file://numbercores.md)
- [Data size](file://datasize.md)
- [Pipeline size](file://pipelinesize.md)
- [Task switching time](file://taskswitchingtime.md)
- [Percentage of mis branching prediction](file://misbranchingprediction.md)
- [Percentage of cache miss](file://cachemiss.md)
- [Go idle time](file://goidletime.md)
- [Max consecutive cycles before going idle](file://maxconsecutivecycles.md)
- [ExecI execution cycles](file://execi.md)
- [ExecC execution cycles](file://execc.md)
- [Clock divider](file://clockdivider.md)


The latter attributes are taken into account by the DIPLODOCUS simulator to determine
how software tasks executed on a processor behave.


## Other attributes

The following attributes are also defined in CPUs:

- [Encryption](file://encryption.md)

- [Operation](file://operation.md)

- [Extension construct](file://cpuextension.md)