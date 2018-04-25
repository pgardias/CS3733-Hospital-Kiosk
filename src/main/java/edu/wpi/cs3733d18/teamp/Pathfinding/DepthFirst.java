


package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.*;

public class DepthFirst extends Pathfinder {
    /**
     * getChildren takes a node and returns the nodes it is connected to
     * @param root
     * @return ArrayList<Node> the list of nodes that is connected to the root node with an edge
     */

    // Takes in a node and returns the nodes it is connected to
    public ArrayList<Node> getChildren(Node root) {

        ArrayList<Edge> edges = new ArrayList<Edge>();
        ArrayList<Node> children = new ArrayList<Node>();

        edges = root.getEdges();
        for (Edge e : edges) {
            if (e.getActive()) {
                Node nextNode;
                if (e.getStart() == root) {
                    nextNode = e.getEnd();
                    if (nextNode.getActive()) {
                        children.add(nextNode);
                    }
                }
                if (e.getEnd() == root) {
                    nextNode = e.getStart();
                    if (nextNode.getActive()) {
                        children.add(nextNode);
                    }
                }
            }
        }
        return children;
    }

    /**
     * findPath implemented with depth first search algorithm
     * @param srcNode
     * @param destNode
     * @return ArrayList<Node> that constitutes the path
     */
    @Override
    public void findPath(Node srcNode, Node destNode) {
        Stack<Node> stack = new Stack<Node>();
        ArrayList<Node> visited = new ArrayList<Node>();
        stack.add(srcNode);
        visited.add(srcNode);
        srcNode.setParent(null);
        // the loop will run until there are no more nodes left in the stack
        // or we find the destination and return the path
        while (!stack.isEmpty()) {
            Node currentNode = stack.pop();

            // checks if it is the destination node
            if (currentNode.equals(destNode)) {
                return;
            }

            // if it is not the destination node it adds it to the visited nodes
            // and checks its children
            visited.add(currentNode);

            ArrayList<Node> children = getChildren(currentNode);
            for (Node node : children) {
                if (!visited.contains(node)) {
                    stack.add(node);
                    node.setParent(currentNode);
                }
            }
        }
        return;
    }
}
