package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Control;

public class DirectionsTable extends RecursiveTreeObject<DirectionsTable>{

    private SimpleStringProperty directions;

    public DirectionsTable(String directions){
        this.directions = new SimpleStringProperty(directions);
    }

    public String getDirections() {
        return directions.get();
    }

    public void setDirections(String directions) {
        this.directions.set(directions);
    }


    @Override
    public String toString(){
        return directions.toString();
    }
}
