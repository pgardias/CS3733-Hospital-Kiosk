package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.*;

public class BestFirst extends Pathfinder{


    public ArrayList <Node> getChildren(Node node) {

        ArrayList<Edge> edges = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Node> sortedNodes = new ArrayList<>();

        edges = node.getEdges();
        for (Edge edge : edges) {
            if (edge.getActive()) {
                Node iterateNode;
                if (edge.getEnd() == node) {
                    iterateNode = edge.getStart();
                    nodes.add(iterateNode);
                } else {
                    iterateNode = edge.getEnd();
                    nodes.add(iterateNode);
                }
            }
        }

        while(!nodes.isEmpty()){
            Node smallest = new Node();
            double weight = Double.MAX_VALUE;
            for (Node n : nodes){
                if (n.getEdge(node).getWeight() < weight) {
                    smallest = n;
                }
            }
            sortedNodes.add(smallest);
            nodes.remove(smallest);

        }
        return sortedNodes;
    }


    public void findPath(Node startNode, Node endNode){
        System.out.println("Best first");

        ArrayList<Node> alreadyVisited = new ArrayList<>();
        ArrayList<Node> children = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        startNode.setParent(null);

        Node currentNode;
        queue.add(startNode);
        while (!queue.isEmpty()){
            currentNode = queue.remove();

            queue.remove(currentNode);
            if (queue.contains(endNode)){
                return;
            }
            alreadyVisited.add(currentNode);
            children = getChildren(currentNode);
            for (Node n:children){
                if (!alreadyVisited.contains(n) && n.getActive()){
                    queue.add(n);
                    n.setParent(currentNode);
                }
            }
        }


    }
}

/*if(!endNode.equals(startNode)){
            while(!queue.isEmpty()){
                currentNode = queue.firstElement();
                queue.remove(currentNode);
                System.out.println("current" + currentNode);
                System.out.println("queue" + queue);
                if(!alreadyVisited.contains(currentNode)){
                    if(currentNode.equals(endNode)){
                        return;
                    }
                    alreadyVisited.add(currentNode);
                    children = getChildren(currentNode);
                    System.out.println("children" + children);
                    for (Node n: children){
                        if (!alreadyVisited.contains(n)) {
                            queue.add(n);
                        }
                    }
                }
            }
        }*/
