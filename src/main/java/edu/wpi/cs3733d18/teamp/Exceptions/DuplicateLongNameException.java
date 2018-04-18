package edu.wpi.cs3733d18.teamp.Exceptions;

public class DuplicateLongNameException extends Exception{
    private String nodeID;

    public DuplicateLongNameException(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeID() {
        return nodeID;
    }
}
