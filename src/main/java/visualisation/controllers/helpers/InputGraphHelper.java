package visualisation.controllers.helpers;

import files.DotParser;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class creates the initial input graph.
 * Currently, it does not follow the same layout as the shown graph in the example.
 */
public class InputGraphHelper {
    public SingleGraph createInputGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        SingleGraph graph = new SingleGraph("input");
        graph.setStrict(true);
        graph.setAutoCreate(false);
        graph.addAttribute("ui.stylesheet","url('visualisation/graphassets/inputGraph.css')");
        Graph inputGraph = retrieveInputGraph("input2.dot");
        HashMap<String, Edge> edges = inputGraph.getEdgeHashMap();
        HashMap<String, Vertex> vertices = inputGraph.getVertexHashMap();

        //TODO: Add listeners to allow for dynamic colouring. Well that's how I think you do it.
        //TODO: Also currently, it shows it in a weird order? Trying to fix this.
        Iterator vertexIt = vertices.entrySet().iterator();
        while (vertexIt.hasNext()) {
            Map.Entry<String,Vertex> pair = (Map.Entry)vertexIt.next();
            String key = pair.getKey();
            Vertex vertex = pair.getValue();
            graph.addNode(key);
            Node n = graph.getNode(key);
            n.setAttribute("ui.label",key);
           // n.setAttribute("weight",vertex.getCost());
        }

        Iterator edgeIt = edges.entrySet().iterator();
        while (edgeIt.hasNext()) {
            Map.Entry<String,Edge> pair = (Map.Entry) edgeIt.next();
            String key = pair.getKey();
            Edge edge = pair.getValue();
            graph.addEdge(key,edge.getFromVertex().getId(),edge.getToVertex().getId(),true);
            org.graphstream.graph.Edge e = graph.getEdge(key); //Can't just use import because there's two diff edges
            e.setAttribute("ui.label",edge.getSwitchCost());
        }

        return graph;
    }


    /**
     * Retrieving the input graph.
     * @return
     */
    private Graph retrieveInputGraph(String path) {
        Graph inputGraph = null;
        try {
            inputGraph = new DotParser(new File("data/"+path)).parseGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputGraph;
    }

}