package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RecordsTable extends RecursiveTreeObject<RecordsTable>{
    private SimpleStringProperty requestType;
    private SimpleStringProperty subType;
    private SimpleIntegerProperty totalOfType;
    private SimpleIntegerProperty totalTime;
    private SimpleIntegerProperty avgTime;

    public RecordsTable(String requestType, String subType, int totalOfType, int totalTime, int avgTime){
     this.requestType = new SimpleStringProperty(requestType);
     this.subType = new SimpleStringProperty(subType);
     this.totalOfType = new SimpleIntegerProperty(totalOfType);
    this.totalTime = new SimpleIntegerProperty(totalTime);
    this.avgTime = new SimpleIntegerProperty(avgTime);
    }

    public String getRequestType() {
        return requestType.get();
    }

    public SimpleStringProperty requestTypeProperty() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType.set(requestType);
    }

    public String getSubType() {
        return subType.get();
    }

    public SimpleStringProperty subTypeProperty() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType.set(subType);
    }

    public int getTotalOfType() {
        return totalOfType.get();
    }

    public SimpleIntegerProperty totalOfTypeProperty() {
        return totalOfType;
    }

    public void setTotalOfType(int totalOfType) {
        this.totalOfType.set(totalOfType);
    }

    public int getTotalTime() {
        return totalTime.get();
    }

    public SimpleIntegerProperty totalTimeProperty() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime.set(totalTime);
    }

    public int getAvgTime() {
        return avgTime.get();
    }

    public SimpleIntegerProperty avgTimeProperty() {
        return avgTime;
    }

    public void setAvgTime(int avgTime) {
        this.avgTime.set(avgTime);
    }
}
