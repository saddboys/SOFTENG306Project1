package ForAlgorhithms;

import Graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to represent a schedule
 */
public class State {
    List<Processor> processors;
    int currentCost;
    int currentLevel;
    int costToBottomLevel;

    List<Vertex> traversed;
    List<Vertex> toTraverse;

    public State addVertex(int processorNum, Vertex v) {
        State result = null;
        //TODO Clone state then add the new vertex.Will also have to clone the processor list and processor block
        // list within it -> reference dissapears once u clone so must use int

        //TODO add the vertex to processor x, at the earliest possible time.
        //TODO Set the new currentCost && current level

        //Requried to check for duplicates later.
        Collections.sort(result.processors);
        return result;
    }

    public boolean canVisit(Vertex v) {
        //Vertex / Edges to be update to have the from vertices f
        //TODO
        return v.canVisit(traversed);
    }

    public boolean allVisited() {
        //Checks if any more vertexes exist to expand
        return toTraverse.isEmpty();
    }

    public List<State> generatePossibilities() {
        //Generates a list of possible states to visit
        List<State> possibleStates = new ArrayList<>();
        if (!allVisited()) {
            for (Vertex v : toTraverse) {
                if (canVisit(v)) {
                    for (int i = 0; i < processors.size(); i++) {
                        possibleStates.add(addVertex(i, v));
                    }
                }
            }
        }

        return possibleStates;

    }
    //TODO return a copy of State, fpr a;; addVertex here.

    @Override
    public String toString() {
        return processors.toString();
    }
}
