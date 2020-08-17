# Introduction

AVATAR stands for Automated Verification of reAl Time softwARe. AVATAR targets the modeling and formal verification of the software of real-time embedded systems.

## Diagrams

The AVATAR profile reuses eight of the SysML diagrams (Package diagrams are not supported). AVATAR supports the following methodological phases:

- [Requirement capture](file://requirements.md). Requirements and properties are structured using AVATAR Requirement Diagrams. At this step, properties are just defined with a specific label.

- **Assumption modeling**. Assumptions of system may be captured with an assumption modeling diagram, based on a SysML requirement diagram.

- **System analysis**. A system may be analyzed using Use Case Diagrams, Activity Diagrams and Sequence Diagrams.

- [Software design](file://avatarsoftwaredesign.md). Software is designed in terms of communicating SysML blocks described in an AVATAR Block Diagram, and in terms of behaviors described with AVATAR State Machines.

 - **Property modeling**. The formal semantics of properties is defined within TEPE Parametric Diagrams (PDs). Since TEPE PDs involve elements defined in system design (e.g, a given integer attribute of a block), TEPE PDs may be defined only after a first system design has been performed.

- **Software deploiement** is performed with UML deploiement diagrams

## Verifications
- Formal verification can be performed from software design. Formal verification relies on internal tools (e.g. internal model-checker, reachability graph generator, graph minimization, test sequences generation) or UPPAAL.

- Simulation can be performed from deploiement diagram. It relies on the SoCLib environment.

- Executable code generation can be performed from software design diagrams. Code is generated in C/POSIX format. 




