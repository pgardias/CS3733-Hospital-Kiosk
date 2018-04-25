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


        while (!openSet.isEmpty()) {
            currentNode = openSet.remove();
            closedSet.add(currentNode);

            if (currentNode.equals(destNode)) {
                return;
            }

            ArrayList<Edge> edges = currentNode.getEdges();

            for (Edge e : edges) {
                if (!closedSet.contains(e.getStart())) {
                    e.getStart().setParent(currentNode);
                    openSet.add(e.getStart());
                }
                if (!closedSet.contains(e.getEnd())) {
                    e.getEnd().setParent(currentNode);
                    openSet.add(e.getEnd());
                }
            }
        }
        return;
    }
}