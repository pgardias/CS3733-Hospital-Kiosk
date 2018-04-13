package edu.wpi.cs3733d18.teamp.ui.service;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ServiceRequestTable {

    private SimpleIntegerProperty requestID;
    private SimpleStringProperty requestType;

    public ServiceRequestTable(int rID, String rType) {
        this.requestID = new SimpleIntegerProperty(rID);
        this.requestType = new SimpleStringProperty(rType);
    }

    public Integer getRequestID() {
        return requestID.get();
    }

    public void setRequestID(Integer i) {
        requestID.set(i);
    }

    public String getRequestType() {
        return requestType.get();
    }

    public void setRequestType(String s) {
        requestType.set(s);
    }
}
