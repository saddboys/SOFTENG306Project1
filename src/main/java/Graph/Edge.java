package Graph;

public class Edge {
    private String id;
    private Vertex fromVertex;
    private Vertex toVertex;
    private int switchCost;

     Edge(String id, int cost, Vertex fromVertex, Vertex toVertex){
        this.id = id;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        switchCost = cost;
    }


    public String getId() {
        return id;
    }

    public int getSwitchCost() {
        return switchCost;
    }

    public Vertex getFromVertex() {
        return fromVertex;
    }

    public Vertex getToVertex() {
        return toVertex;
    }

    @Override
    public String toString() {
        return switchCost + id;
    }
}
