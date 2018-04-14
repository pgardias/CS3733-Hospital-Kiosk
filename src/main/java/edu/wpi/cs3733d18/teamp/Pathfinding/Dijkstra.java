package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

public class Dijkstra extends Pathfinder{

    // Takes in a node and returns the nodes it is connected to
    public ArrayList <Node> getChildren(Node root) {

        ArrayList<Edge> edges = new ArrayList<>();
        ArrayList<Node> children = new ArrayList<>();

        edges = root.getEdges();
        for (Edge e : edges) {
            Node nextNode;
            if (e.getStart() == root) {
                nextNode = e.getEnd();
                    children.add(nextNode);
            } else {
                nextNode = e.getStart();
                children.add(nextNode);
            }
        }
        return children;
    }

    // traces back the parents to get the path
    public ArrayList<Node> getPath(Node strNode, Node endNode){
        ArrayList<Node> path = new ArrayList<>();
        Stack<Node> reverse = new Stack<>();

        reverse.push(endNode);
        Node currentNode = endNode;
         while (!reverse.isEmpty()){
             currentNode = currentNode.getParent();
             reverse.push(currentNode);
             if (currentNode.equals(strNode)){
                 break;
             }
         }
         while (!reverse.isEmpty()){
             path.add(reverse.pop());
         }
         System.out.println(path);
         return path;
    }


    // Dijkstra's algorithm to find shortest path from s to all other nodes
    public ArrayList<Node> findPath ( Node strNode, Node destNode) {
        HashMap<Node, Double> dist = new HashMap<>();  // shortest known distance from "s"
        ArrayList<Node> visited = new ArrayList<>(); // all false initially
        ArrayList<Node> children = new ArrayList<>();
        Vector<Node> queue = new Vector<>();

        dist.put(strNode, 0.0);
        queue.add(strNode);
        Node currentNode;

        if (!destNode.equals(strNode)) {
            while (!queue.isEmpty()) {
                currentNode = queue.firstElement();
                System.out.println(currentNode);
                queue.remove(currentNode);
                if (!visited.contains(currentNode)) {
                    if (currentNode.equals(destNode)) {
                        return getPath(strNode, currentNode);
                    }
                    visited.add(currentNode);

                    children = getChildren(currentNode);
                    System.out.println(children);
                    // check if the node has no children
                    //iterate through the children nodes
                    for (Node n : children) {
                        if (!dist.containsKey(n)) {
                            Edge e = n.getEdge(currentNode);
                            // calculate the weight depending on the parent node
                            double weight = e.getWeight() + dist.get(currentNode);
                            n.setParent(currentNode);
                            dist.put(n, weight);
                            queue.add(n);
                        } else {
                            // calculate the weight depending on the parent node
                            double weight = n.getEdge(currentNode).getWeight() + dist.get(currentNode);
                            if (weight < dist.get(n)) {
                                n.setParent(currentNode);
                                dist.put(n, weight);
                            }
                        }
                    }

                }
            }
        }
        return null;
    }
}
