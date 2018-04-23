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
        System.out.println("breadthfirst");
        //System.out.println("breadth first");
        Queue<Node> openSet = new LinkedList<>();
        HashSet<Node> closedSet = new HashSet<>();
        Node currentNode = srcNode;
        openSet.add(currentNode);
        srcNode.setParent(null);

        //System.out.println("Source node: " + srcNode + " Destination node: " + destNode);

        while (!openSet.isEmpty()) {
            currentNode = openSet.remove();
            closedSet.add(currentNode);

            if (currentNode.equals(destNode)) {
                //System.out.println(getPath(currentNode));
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
        //System.out.println("Path not found!");
        return;
    }
}