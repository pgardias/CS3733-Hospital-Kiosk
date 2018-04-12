package edu.wpi.cs3733d18.teamp;

public class Record {

    // Attributes
    private int recordID;
    private Request.requesttype requestType;
    private String subType;
    private int totalOfType;
    private int totalTime;
    private int avgTime;

    // Constructors
    public Record(){} // Empty constructor
    public Record(int recordID, Request.requesttype requestType, String subType, int totalOfType, int totalTime, int avgTime) {
        this.recordID = recordID;
        this.requestType = requestType;
        this.subType = subType;
        this.totalOfType = totalOfType;
        this.totalTime = totalTime;
        this.avgTime = avgTime;
    }

    // Getters
    public int getRecordID() {
        return recordID;
    }

    public Request.requesttype getRequestType() {
        return requestType;
    }

    public String getSubType() {
        return subType;
    }

    public int getTotalOfType() {
        return totalOfType;
    }

    public int getTotalTime() { return totalTime; }

    public int getAvgTime() {
        return avgTime;
    }

    // Setters
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public void setRequestType(Request.requesttype requestType) {
        this.requestType = requestType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setTotalOfType(int totalOfType) {
        this.totalOfType = totalOfType;
    }

    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public void setAvgTime(int avgTime) {
        this.avgTime = avgTime;
    }
}
