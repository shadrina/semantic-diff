# semantic-diff
IDEA plugin for representing changes to hierarchically structured data

## Algorithm
We consider the tree as a <b>syntax tree</b> if each node has a label and a value. In our case, the label is the node type, and the value is the code fragment described by the subtree rooted by the node.

Let's assume we have a tuple `<T1, T2, M>`, where `T1` and `T2` are initial and final states of a syntax tree, and `M` is a binary relation (<b>matching</b>) between two sets of nodes of `T1` and `T2` such that `(x, y)` belongs to the relation `M`, if the subtrees whose roots are the nodes `x` and `y` respectively, are <b>approximately equal</b> to each other.

In this case, our task is just to compute a minimal set of transformations, by means of which we obtain a tree `T1'` isomorphic to `T2`. It can be done by applying 4 types of operations to the initial state of a syntax tree: `UPDATE`, `MOVE`, `INSERT` and `DELETE`. Operations are added to the edit script depending on the state of matching by visiting the nodes of `T2` in breadth-first order.

## Good Matching Problem
Matching nodes (calculating binary relation `M`) can be done in different ways, but we want to use the fastest one.

We also need to take into account the following:
 - Minimal edit script is sometimes suboptimal solution. In some cases it is better to split `MOVE` into separate `INSERT` and `DELETE`.
 - Analyzed data consists of tokens, which are almost always not unique. A single node can be matched in several ways.

To achieve the most accurate result and consider all of the above, let's split matching stage into 3 parts:

### Preprocessing
Match identified nodes (and their direct children), e.g. classes, named functions, properties and everything, that has identifier.
### FastMatch
Proceeding bottom-up match nodes by searching for the longest common subsequence. Here it is necessary to consider the contexts compatibility.
### Postprocessing
Proceeding top-down check:
- If node is unmatched, search for the partner in parent's partner children.
- If parents of matched nodes are <b>not</b> matched with each other, try to find better partners for the nodes.

## Supported Languages
- Kotlin
- Java `coming soon`
- Groovy `coming soon`

## References
- Sudarshan S. Chawathe, Anand Rajaraman, Hector Garcia-Molina, Jennifer Widom. *Change Detection in Hierarchically Structured Information*, 1995
