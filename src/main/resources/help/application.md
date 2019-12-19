# Describing an application in DIPLODOCUS
An application is described within a functional view. It is built upon a set of:
- Composite components
- Primitive components that can contain ports
- Ports. A port is the origin or destination of either a [channel](file://channel.md), an event or a request.
- Connectors between ports, thus building a logical communication path. A Path can contain [fork](file://fork.md) or [join](file://join.md) operators, but not both.
