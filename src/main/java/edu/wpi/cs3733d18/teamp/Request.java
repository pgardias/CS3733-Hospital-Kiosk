package edu.wpi.cs3733d18.teamp;

import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {

    // Request enum
    public enum requesttype {LANGUAGEINTERP, HOLYPERSON, COMPUTER, SECURITY, MAINTENANCE, SANITATION, EMERGENCY, DEFAULT}

    // Attributes
    private int requestID;
    private requesttype requestType;
    private String subType;
    private String location;
    private String additionalInfo;
    private String madeBy;
    private String completedBy;
    private Timestamp timeMade;
    private Timestamp timeCompleted;
    private int completed;
    private int priority;

    // Constructor
    public Request(){} // Empty constructor
    public Request(Request.requesttype requestType, String subType, String location, String additionalInfo, String madeBy, String completedBy,
                   Timestamp timeMade, Timestamp timeCompleted, int completed) {
        this.requestType = requestType;
        this.subType = subType;
        this.location = location;
        this.additionalInfo = additionalInfo;
        this.madeBy = madeBy;
        this.completedBy = completedBy;
        this.timeMade = timeMade;
        this.timeCompleted = timeCompleted;
        this.completed = completed;

        if (this.requestType == requesttype.EMERGENCY) {
            this.priority = 1;
        }
        else {
            this.priority = 0;
        }
    }

    @Override
    public String toString() {
        String type = " ";
        switch(this.getRequestType()) {
            case COMPUTER:
                type = "Electronic Device \nRequest";
            case HOLYPERSON:
                type =  "Religious Request";
            case LANGUAGEINTERP:
                type = "Language Interpreter \nRequest";
            case SECURITY:
                type = "Security Request";
            case MAINTENANCE:
                type = "Maintenance Request";
            case SANITATION:
                type = "Sanitation Request";
        }
        return type;
    }

    /**
     * Converts the milliseconds given by the Timestamp into a readable date and time
     * @param time
     * @return the date and time as the string
     */
    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    // Getters
    public int getRequestID() {
        return requestID;
    }

    public requesttype getRequestType() {
        return requestType;
    }

    public String getSubType() { return subType; }

    public String getLocation() {
        return location;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getMadeBy() {
        return madeBy;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public Timestamp getTimeMade() {
        return timeMade;
    }

    public Timestamp getTimeCompleted() {
        return timeCompleted;
    }

    public int isCompleted() {
        return completed;
    }

    public int getPriority() {
        return priority;
    }

    // Setters
    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setRequestType(requesttype requestType) {
        this.requestType = requestType;
    }

    public void setSubType(String subType) { this.subType = subType; }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public void setTimeMade(Timestamp timeMade) {
        this.timeMade = timeMade;
    }

    public void setTimeCompleted(Timestamp timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}