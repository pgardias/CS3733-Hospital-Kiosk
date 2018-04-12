package edu.wpi.cs3733d18.teamp.Pathfinding;

public class Edge {
    private String id;
    private Node start;
    private Node end;
    private Boolean isActive;
    private double weight;

    //TODO Look at how constructors are used and whether we need all of them
    // Constructors
    public Edge() {}
    //Constructor for adding a new edge with a startNode, endNode, and an ID
    public Edge(String id, Node startNode, Node endNode) {
        this.id = id;
        this.start = startNode;
        this.end = endNode;
        this.isActive = true;
        // If this edge goes between floors, set weight as high value to discourage going between floors
        if ((startNode.getType() == Node.nodeType.ELEV  && endNode.getType() == Node.nodeType.ELEV)
                || (startNode.getType() == Node.nodeType.STAI && endNode.getType() == Node.nodeType.STAI)) {
            this.weight = 1000;
        } else {
            this.weight = Math.hypot((this.start.getX() - this.end.getX()), (this.start.getY() - this.end.getY()));
            System.out.println(this.weight);
        }
    }
    //Maybe this Edge ID is for creating an edge to show initially, before it's submitted to the database?
    public Edge(Node startNode, Node endNode) {
        this.start = startNode;
        this.end = endNode;
        this.isActive = true;
        // If this edge goes between floors, set weight as high value to discourage going between floors
        if ((startNode.getType() == Node.nodeType.ELEV  && endNode.getType() == Node.nodeType.ELEV)
                || (startNode.getType() == Node.nodeType.STAI && endNode.getType() == Node.nodeType.STAI)) {
            this.weight = 1000;
        } else {
            this.weight = Math.hypot((this.start.getX()-this.end.getX()), (this.start.getY()-this.end.getY()));
            System.out.println(this.weight);
        }
    }
    //Edge constructor that shows whether or not it is active
    public Edge(Node startNode, Node endNode, Boolean isActive) {
        this.start = startNode;
        this.end = endNode;
        this.isActive = isActive;
        // If this edge goes between floors, set weight as high value to discourage going between floors
        if ((startNode.getType() == Node.nodeType.ELEV  && endNode.getType() == Node.nodeType.ELEV)
                || (startNode.getType() == Node.nodeType.STAI && endNode.getType() == Node.nodeType.STAI)) {
            this.weight = 1000;
        } else {
            this.weight = Math.hypot((this.start.getX()-this.end.getX()), (this.start.getY()-this.end.getY()));
            System.out.println(this.weight);
        }
    }

    /**
     * equals checks whether or not a given edge is equal to the current edge object (two edge comparison)
     * @param e
     * @return true if a given Edge is equal to the current edge, false if not
     */
    public boolean equals(Edge e) {
        if (e.getStart().equals(this.start) && e.getEnd().equals(this.end)) {
                return true;
        }
        return false;
    }

    /**
     * contains checks whether or not a specific node is part of the current edge
     * @param n
     * @return true if the selected edge contains a particular node, false if not
     */
    public boolean contains(Node n) {
        if (this.start.equals(n) || this.end.equals(n)) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return id;
    }

    // Getters

    public String getID() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Node getEnd() {
        return end;
    }

    public Node getStart() {
        return start;
    }

    // Setters

    public void setStart(Node start) {
        this.start = start;
    }
    //TODO setWeight is never used. Do we want to keep it?
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setID(String id) {
        this.id = id;
    }
}