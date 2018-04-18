package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class BestFirst extends Pathfinder{


    public ArrayList <Node> getChildren(Node node) {

        ArrayList<Edge> edges = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Node> sortedNodes = new ArrayList<>();

        edges = node.getEdges();
        for (Edge edge : edges) {
            Node iterateNode;
            if (edge.getEnd() == node) {
                iterateNode = edge.getStart();
                nodes.add(iterateNode);
            } else {
                iterateNode = edge.getEnd();
                nodes.add(iterateNode);
            }
        }

        double currentWeight = Double.MAX_VALUE;
        Node smallest = new Node();
        while(!nodes.isEmpty()){
            for(Node n: nodes) {
                if (node.getEdge(n).getWeight() < currentWeight) {
                    smallest = n;
                    currentWeight = node.getEdge(n).getWeight();
                }
            }
            sortedNodes.add(smallest);
            smallest.setParent(node);
            nodes.remove(smallest);
        }
        return sortedNodes;
    }


    public void findPath(Node startNode, Node endNode){
        System.out.println("Best first");

        HashMap<Node, Double> lengths = new HashMap<>();
        ArrayList<Node> alreadyVisited = new ArrayList<>();
        ArrayList<Node> children;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Node currentNode;




        lengths.put(startNode, 0.0);
        queue.add(startNode);

        if(!endNode.equals(startNode)){
            while(!queue.isEmpty()){
                currentNode = queue.poll();
                if(!alreadyVisited.contains(currentNode)){
                    if(queue.contains(endNode)){
                        return;
                    }
                    alreadyVisited.add(currentNode);
                    children = getChildren(currentNode);
                    queue.addAll(children);
                }
            }
        }
    }
}
