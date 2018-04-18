package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.awt.event.ActionEvent;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext.PathfindingSetting;

public class SettingsController {

    @FXML
    JFXTextField feetPerPixelTextField;

    @FXML
    JFXButton submitButton;

    @FXML
    JFXButton backButton;

    @FXML
    JFXComboBox algorithmComboBox;

    ObservableList<String> algorithmTypes = FXCollections.observableArrayList(
            "A*",
            "Depth-first",
            "Breadth-first",
            "Dijkstra's",
            "Best-first"
    );

    /**
     * Sets the fields in the settings screen based upon the values in the
     * settings singleton.
     */
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
            case Dijkstra: {
                algorithmComboBox.setValue("Dijkstra's");
                break;
            }
            case BestFirst: {
                algorithmComboBox.setValue("BestFirst");
                break;
            }
            default: {
                System.out.println("Admin Settings Panel was unable to set the current Pathfinding Algorithm Context");
                break;
            }
        }

    }

    /**
     * Sets the settings singleton based upon the current inputs when the
     * submit button is pressed.
     * @param e
     */
    @FXML
    public void submitmButtonOp(ActionEvent e) {
        switch (algorithmComboBox.getValue().toString()) {
            case "A*":
                Settings.setPathfindingAlgorithm(PathfindingSetting.AStar);
                break;
            case "Depth-first":
                Settings.setPathfindingAlgorithm(PathfindingSetting.DepthFirst);
                break;
            case "Breadth-first":
                Settings.setPathfindingAlgorithm(PathfindingSetting.BreadthFirst);
                break;
            case "Dijkstra's":
                Settings.setPathfindingAlgorithm(PathfindingSetting.Dijkstra);
                break;
            case "Best-first":
                Settings.setPathfindingAlgorithm(PathfindingSetting.BestFirst);
                break;
        }
        try {
            Settings.setFeetPerPixel(Double.parseDouble(feetPerPixelTextField.getText()));
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            // TODO Set error label appropriately
            return;
        }
    }
}
