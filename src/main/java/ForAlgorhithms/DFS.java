package ForAlgorhithms;

import Graph.Graph;

import java.util.HashSet;
import java.util.Stack;

public class DFS {
    private final int numP;
    private Stack<State> stateStack;
    Graph graph;
    private int boundValue;

    public DFS(int numProcessors, Graph g) {
        graph = g;
        stateStack = new Stack<State>();
        numP = numProcessors;

        //Init state
        stateStack.push(new State(numP, graph));
        boundValue = Integer.MAX_VALUE;



    }

    public State runDFS() {
        State bestState = new State(numP, graph);
        while (!stateStack.empty()) {
            State state = stateStack.pop();



            int currentBoundValue = boundValue;
            if (state.currentCost < currentBoundValue) {
                if (state.allVisited()) {
                    boundValue = state.currentCost;
                    bestState = state;
                } else {
                    for (State nextState : state.generatePossibilities()) {

                        stateStack.push(nextState);
                    }
                }
            }

        }
        return bestState;
    }

}