import java.util.Comparator;

// Node class
public class Node implements Comparator<Node> {
    public int node;
    public int cost;

    public Node() {
    } //empty constructor

    public Node(int node, int cost) {
        this.node = node;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2) {
        return node1.cost - node2.cost; // inbuilt checking by comparator
    }

    @Override
    public String toString() {
        return String.valueOf(node);
    }
}