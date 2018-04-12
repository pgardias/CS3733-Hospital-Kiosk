package edu.wpi.cs3733d18.teamp.Exceptions;

public class OrphanNodeException extends Exception {

    private String nodeID;

    public OrphanNodeException(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeID() {return nodeID;}
}
