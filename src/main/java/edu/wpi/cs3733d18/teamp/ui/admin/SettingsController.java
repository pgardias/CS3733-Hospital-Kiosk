package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;
import edu.wpi.cs3733d18.teamp.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.awt.event.ActionEvent;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext.PathfindingSetting;

public class SettingsController {

    @FXML
    JFXButton setAlgorithmButton;

    @FXML
    JFXComboBox algorithmComboBox;

    ObservableList<String> algorithmTypes = FXCollections.observableArrayList(
            "A*",
            "Depth-first",
            "Breadth-first",
            "Dijkstra's",
            "Best-first"
    );

    @FXML
    public void onStartUp() {
        switch (Settings.getPathfindingSettings()) {
            case AStar: {
                algorithmComboBox.setValue("A*");
                break;
            }
            case DepthFirst: {
                algorithmComboBox.setValue("Depth-first");
                break;
            }
            case BreadthFirst: {
                algorithmComboBox.setValue("Breadth-first");
                break;
            }
            default: {
                System.out.println("Admin Settings Panel was unable to set the current Pathfinding Algorithm Context");
                break;
            }
        }

    }

    @FXML
    public void submitmButtonOp(ActionEvent e) {

        switch (algorithmComboBox.getValue().toString()) {
            case "A*":
                Settings.setSettings(PathfindingContext.PathfindingSetting.AStar);
                break;
            case "Depth-first":
                Settings.setSettings(PathfindingContext.PathfindingSetting.DepthFirst);
                break;
            case "Breadth-first":
                Settings.setSettings(PathfindingContext.PathfindingSetting.BreadthFirst);
                break;
            case "Dijkstra's":
                break;
            case "Best-first":
                break;
        }
    }
}
