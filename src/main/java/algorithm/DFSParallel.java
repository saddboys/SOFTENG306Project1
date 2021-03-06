package algorithm;

import graph.Graph;
import scheduler.AStarComparator;
import scheduler.State;
import visualisation.AlgorithmDataStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 *  Algorithm which uses dfs to solve the problem at hand.
 *  This code is adapted from the DFS algorithm.
 *  This algorithm runs on multiple cores.
 */
public class DFSParallel extends AlgorithmHandler implements Algorithm {
    private int minFullPath = Integer.MAX_VALUE;
    private boolean traversed;
    private Stack<State> candidate;
    private HashSet<State> visited;
    private Graph graph;
    private int totalBranches = 1;
    private boolean updateStatistics = false;

    private int currentThreads;
    private int MAX_THREADS;


    private AStarComparator aStarComparator;
    private State result;


    public DFSParallel(int numProcessors, Graph graph, int maxThreads) {
        candidate = new Stack<>();
        visited = new HashSet();
        this.graph = graph;
        traversed = false;
        candidate.add(new State(numProcessors, graph));
        aStarComparator = new AStarComparator();
        //limit number of threads used to the number of processors since if there are more threads than processors,
        //the search thing does nothing
        if (maxThreads < numProcessors) {
            MAX_THREADS = maxThreads;
        } else {
            MAX_THREADS = numProcessors;
        }
    }


    public DFSParallel(int numProcessors, Graph graph) {
        candidate = new Stack<>();
        visited = new HashSet();
        this.graph = graph;
        traversed = false;
        candidate.add(new State(numProcessors, graph));
        aStarComparator = new AStarComparator();
        int maxThreads = 2;
        if (maxThreads < numProcessors) {
            MAX_THREADS = maxThreads;
        } else {
            MAX_THREADS = numProcessors;
        }

    }

    /**
     * Runs the algorithm
     *
     * @return
     */
    public State runAlgorithm() {
        List<DFSThread> threadList = new ArrayList<>();
        while (!candidate.isEmpty()) {
            //create as many threads as needed before starting
            if (currentThreads < MAX_THREADS) {
                currentThreads++;
                DFSThread newThread = new DFSThread();
                threadList.add(newThread);
                newThread.start();
            } else {
                iterate();
            }
        }
        for (int i = 0; i < threadList.size(); i++) {
            DFSThread thread =threadList.get(i);
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void stackPush(State s) {
        candidate.push(s);
    }

    private  void pruneStack(State s) {
        candidate.removeIf((state) -> aStarComparator.compare(s, state) < 0);
    }

    /*This must be synchronised*/
    private synchronized State stackPop() {
        if (!candidate.empty()) {
            State s = candidate.pop();
            return s;
        } else {
            return null;
        }
    }

    /*This must be synchronised*/
    private synchronized void setResult(State s) {
        if (s.allVisited() && s.getCostToBottomLevel() < minFullPath) {
            result = s;
            minFullPath = s.getCostToBottomLevel();
        }

    }


    private boolean stackCompare(State s) {
        return s.getCostToBottomLevel() < minFullPath;
    }

    /**
     * Checks to see if a state could more optimal than the one provided.
     * If it is, then the algorithm can be run on that state.
     */
    public void iterate() {
        //each thread gets a unique 's'
        State s = stackPop();
        if (s != null) {
            //Generate possibilities for the given state
            for (State s1 : s.generatePossibilities()) {

                //Only check unique states which haven't been visited before
                if (!visited.contains(s1)) {
                    totalBranches++;

                    //Ensure that it could create a better than optimal solution
                    if (stackCompare(s1)) {
                        AlgorithmDataStorage.getInstance().incrementVisited();
                        stackPush(s1);

                        // If it is better than the optimal state, update.
                        if (s1.allVisited()) {
                            pruneStack(s1);
                            setResult(s1);
                        }
                    }else{
                        AlgorithmDataStorage.getInstance().incrementPruned();
                    }
                    visited.add(s1);
                }else{
                    AlgorithmDataStorage.getInstance().incrementDuplicates();
                }
            }
        }
    }

    /**
     * Object representing this class on a new thread
     */
    private class DFSThread extends Thread {
        @Override
        public void run() {
            runAlgorithm();
        }
    }

}

