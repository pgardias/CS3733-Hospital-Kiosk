package edu.wpi.cs3733d18.teamp.Exceptions;

public class NodeNotFoundException extends Exception {
    private String nodeID;

    public NodeNotFoundException() {}

    // Constructor to keep track of Node that cannot be found
    public NodeNotFoundException(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeID() {
        return nodeID;
    }
}