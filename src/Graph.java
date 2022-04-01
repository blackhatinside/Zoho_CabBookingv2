import java.util.*;

public class Graph {
    private final int dist[];
    private final Set<Integer> visited;     // if node already checked
    private final PriorityQueue<Node> q;  // min priority queue
    private final int V; // Number of vertices
    private final List<List<Node>> adj_list;

    public List<List<Node>> getAdj_list() {
        return adj_list;
    }

    //class constructor
    public Graph(int V) {
        this.V = V;
        dist = new int[V];
        visited = new HashSet<>();
        // by default implement min Priority Queue
        q = new PriorityQueue<>(V, new Node());
        // adjacency list representation of graph
        this.adj_list = new ArrayList<>();
        createVertex();
    }

    public void createVertex() {
        for (int i = 0; i < V; i++) {
            List<Node> item = new ArrayList<>();
            adj_list.add(item);
        }
    }

    public void addEdge(int u, int v, int w) {
        boolean isDirected = false;
        adj_list.get(u).add(new Node(v, w));
        if (!isDirected) {
            adj_list.get(v).add(new Node(u, w));
        }
    }

    public void removeEdge(int u, int v) {
        boolean isDirected = false;
        if (!isDirected) {
            List<Node> children = adj_list.get(u);
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i).node == v) {
                    children.remove(i);
                    break;
                }
            }
        }
    }

    // Dijkstra's Algorithm implementation
    public int dijkstra(int src, int dest) {
        for (int i = 0; i < V; i++)
            dist[i] = Integer.MAX_VALUE;

        // Distance to the source from itself is 0
        dist[src] = 0;

        // first add source vertex to PriorityQueue
        q.add(new Node(src, 0));

        while (visited.size() != V) {

            // u is removed from PriorityQueue and has min distance
            int u = q.remove().node;

            // add node to finalized list (visited)
            visited.add(u);
            process_neighbours(u);
        }
        return dist[dest];
    }

    // this methods processes all neighbours of the just visited node
    private void process_neighbours(int u) {

        // process all neighbouring nodes of u
        for (int i = 0; i < adj_list.get(u).size(); i++) {
            Node v = adj_list.get(u).get(i);

            //  proceed only if current node is not in 'visited'
            if (!visited.contains(v.node)) {

                // compare distances    - not [d(v) < d(u) + w(u,v)]
                if (dist[u] + v.cost < dist[v.node])
                    dist[v.node] = dist[u] + v.cost;

                // Add the current vertex to the PriorityQueue
                q.add(new Node(v.node, dist[v.node]));
            }
        }
    }
}
