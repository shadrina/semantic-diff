# semantic-diff
IDEA plugin for representing changes to hierarchically structured data

## Algorithm
All of the following is a brief overview of the [techreport](http://ilpubs.stanford.edu:8090/115/) from the Stanford InfoLab.

We consider the tree as a <b>syntax tree</b> if each node has: 
- label
- value (`optional`)
- identifier (`optional`)

Let's assume we have a tuple `<T1, T2, M>`, where `T1` and `T2` are initial and final states of a syntax tree, and `M` is a binary relation between two sets of nodes of `T1` and `T2` such that `(x, y)` belongs to the relation `M`, if the subtrees whose roots are the nodes `x` and `y` respectively, are <b>approximately equal</b> to each other.

In this case, our task is just to compute a minimal set of transformations, by means of which we obtain a tree `T1'` isomorphic to `T2`.

It can be done by applying 4 types of operations to the initial state of a syntax tree in the following order:
- Update (only if nodes have value)
- Move
- Insert
- Delete