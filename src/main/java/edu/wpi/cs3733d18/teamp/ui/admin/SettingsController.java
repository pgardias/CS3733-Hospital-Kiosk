package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext.PathfindingSetting;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsController {
    ConfirmationPopUpController confirmationPopUpController;
    boolean isSaved = false;


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

        algorithmComboBox.setItems(algorithmTypes);
        switch (Settings.getPathfindingSettings()) {
            case AStar:
                algorithmComboBox.setValue("A*");
                break;
            case DepthFirst:
                algorithmComboBox.setValue("Depth-first");
                break;
            case BreadthFirst:
                algorithmComboBox.setValue("Breadth-first");
                break;
            case Dijkstra:
                algorithmComboBox.setValue("Dijkstra's");
                break;
            case BestFirst:
                algorithmComboBox.setValue("BestFirst");
                break;
            default:
                break;
        }
        feetPerPixelTextField.setText(Double.toString(Settings.getFeetPerPixel()));
    }
        /**
         * Sets the settings singleton based upon the current inputs when the
         * submit button is pressed.
         */
        @FXML
        public void submitButtonOp () {
            Stage stage;
            Parent root;
            FXMLLoader loader;

            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/FXML/general/ConfirmationPopUp.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return;
            }

            confirmationPopUpController = loader.getController();
            confirmationPopUpController.setSettingsButtonOp("Submit");
            confirmationPopUpController.StartUp(this);
            stage.setScene(new Scene(root, 600, 150));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(submitButton.getScene().getWindow());
            stage.showAndWait();

            if(confirmationPopUpController.getChoice()){
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

        @FXML
        public void backButtonOp(ActionEvent e) {
            Stage stage;
            Parent root;
            FXMLLoader loader;

            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/FXML/general/ConfirmationPopUp.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return;
            }

            confirmationPopUpController = loader.getController();
            confirmationPopUpController.setSettingsButtonOp("Back");
            confirmationPopUpController.StartUp(this);
            stage.setScene(new Scene(root, 600, 150));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(submitButton.getScene().getWindow());
            stage.showAndWait();

            if(confirmationPopUpController.getChoice()) {

                loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
                try {
                    root = loader.load();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return;
                }
                backButton.getScene().setRoot(root);
            }
        }
}
