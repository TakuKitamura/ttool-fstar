# Reachability graphs

## How to generate a reachability graph?
 Reachability Graphs can be created by using the internal model-checker of TTool.
This [video on formal verification](https://www.youtube.com/watch?v=8IYJ1UDUbvQ) details the steps to generate a RG.


## Working with RGs

Once a RG has been generated, it is listed in the left tree, sectiopn "R. graphs". A righ click on a RG offers several options:
- Getting statistics on the graph
- Displaying the graph
- Minimizing the graph

## Displaying the graph

A RG is displayed with an external library called "GraphStream" provided with TTool. A RG can displayed only if its number of states and transitions is of reasonable size. Usually, more than 500 states or transitions will make the displaying slow and useless.

The displaying of graphs can be customized using a CSS specification added to the configuration file of TTool. Below is provided an example of such a specification. All the specification given in [the reference CC for graphstream](https://graphstream-project.org/doc/Advanced-Concepts/GraphStream-CSS-Reference/) can be used in this definition.

    <RGStyleSheet data="node {fill-color: #B1CAF1; text-color: black; size: 20px, 20px; text-size:14;}     edge {text-color: black; shape: cubic-curve; text-size:10;}    edge.defaultedge {text-size:10; text-color:black;}  edge.external {text-color:blue; text-size:14; text-offset: -20, -20; text-alignment: along;}    node.deadlock {fill-color: red; text-color: white; size: 20px, 20px; text-size:16;}    node.init { fill-color: green; text-color: black; size: 20px, 20px; text-size:16;}" />

- *node.init* corresponds to the first node of the graph. Here, it is colored in green
- *node.deadlock* corresponds to nodes with no output transitions. They are colored in red.
- *node* corresponds to other nodes. They are colors with RGB color "B1CAF1"
- *edge* defines the characteristics of the normal edges.
- *edge.defaultedge defines the specification of edges with internal actions, i.e. with no communication action
- *edge.external* refers to edges used for communications between blocks.








