package edu.wpi.cs3733d18.teamp.Pathfinding;

import org.junit.Test;

import java.util.*;

public class BreadthFirst extends Pathfinder {

    /**
     * findPath implemented with breadth first search algorithm
     * @param srcNode
     * @param destNode
     * @return ArrayList<Node> that constitutes the path
     */

    @Override
    public void findPath(Node srcNode, Node destNode) {
        Queue<Node> openSet = new LinkedList<>();
        HashSet<Node> closedSet = new HashSet<>();
        Node currentNode = srcNode;
        openSet.add(currentNode);
        srcNode.setParent(null);


        while (!openSet.isEmpty()) {
            currentNode = openSet.remove();
            closedSet.add(currentNode);

            if (currentNode.equals(destNode)) {
                return;
            }

            ArrayList<Edge> edges = currentNode.getEdges();

            for (Edge e : edges) {
                if (e.getActive()) {
                    Node neighbor = null;
                    if (!closedSet.contains(e.getStart())) {
                        neighbor = e.getStart();
                        neighbor.setParent(currentNode);
                    } else

                    if (!closedSet.contains(e.getEnd())) {
                        neighbor = e.getEnd();
                        neighbor.setParent(currentNode);
                    }

                    if (neighbor != null) {
                        if (neighbor.getActive()) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }
        return;
    }
}