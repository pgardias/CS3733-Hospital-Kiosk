package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AStar extends Pathfinder {
    /**
     * findPath implemented with A* search algorithm
     * @param srcNode
     * @param destNode
     * @return ArrayList<Node> that constitutes the path
     */
    @Override
    public ArrayList<Node> findPath(Node srcNode, Node destNode) {
        System.out.println("Astar");
        // Total cost from start node
        HashMap<String, Double> gScore = new HashMap<String, Double>();

        // Total cost to end node
        HashMap<String, Double> fScore = new HashMap<String, Double>();

        // Path of nodes to return
        ArrayList<Node> path = new ArrayList<Node>();
        path.add(srcNode); //Add start node to path

        // Nodes to explore in the future
        // Really should be a key/value priority queue sorted by fScore
        HashSet<Node> openSet = new HashSet<Node>();
        openSet.add(srcNode);

        // Add gScore for current node
        gScore.put(srcNode.getID(), 0.0);

        // add fScore for current node
        fScore.put(srcNode.getID(), srcNode.distanceBetweenNodes(destNode));

        // Nodes that have been explored
        HashSet<Node> closedSet = new HashSet<Node>();
        closedSet.add(srcNode);

        srcNode.setParent(null);

        // Unexplored node with shortest distance to goal
        // Initialize to first item in openSet
        // (if only 1 item then it is the shortest to goal)
        Node currentNode = srcNode;

        // Start searching
        while (!openSet.isEmpty()) {
            // Temporary fScore value for comparing to current fScore
            // Since we want the lowest fScore, make the default super large
            double tempFScore = Double.POSITIVE_INFINITY;

            // Get item in openSet that has smallest
            for (Node tempNode : openSet) {
                if (fScore.get(tempNode.getID()) < tempFScore) {
                    tempFScore = fScore.get(tempNode.getID());
                    currentNode = tempNode;
                }
            }
            openSet.remove(currentNode);
            closedSet.add(currentNode);

            // Check if we found the goal
            if (currentNode.equals(destNode)) {
                path = getPath(currentNode);
                return path;
            }

            // Add neighbors to openSet
            for (Edge e : currentNode.getEdges()) {
                // Temporary variable for comparing gScores
                Double tempGScore = 0.0;

                // Set neighbor to the node in edge that is not current node
                Node neighbor;
                if (e.getStart().equals(currentNode)) {
                    neighbor = e.getEnd();
                } else {
                    neighbor = e.getStart();
                }
                if (!closedSet.contains(neighbor)) {

                    // Add neighbor gScore value to gScore
                    tempGScore = gScore.get(currentNode.getID()) + e.getWeight();
                    System.out.println(e.getWeight());

                    if (!gScore.containsKey(neighbor.getID())) {

                        // Set parent for recreating the path later
                        neighbor.setParent(currentNode);

                        // Set the gScore for neighbor to tempGScore
                        gScore.put(neighbor.getID(), tempGScore);

                        // Calculate tempFScore for readability
                        tempFScore = gScore.get(neighbor.getID()) + destNode.distanceBetweenNodes(neighbor);

                        // Set fScore for neighbor to tempFScore
                        fScore.put(neighbor.getID(), tempFScore);
                    }

                    // Add neighbor nodes with weights to the openSet
                    openSet.add(neighbor);
                }
            }
        }
        System.out.println("Path not found!");
        return null;
    }
}
