package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CreditsScreenController {

    @FXML
    JFXButton backButton;

    @FXML
    Label introLabel;

    @FXML
    Label teamLabel;

    public void StartUp() {
        introLabel.setText(
                "\nWe used the following API's:"
        );
        teamLabel.setText(
                "\nPrescriptions API - Team R" +
                "\n Obj importer API - Interactive Mesh"
        );
    }

    @FXML
    public void backButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        AboutScreenController aboutScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/AboutScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        aboutScreenController = loader.getController();
        aboutScreenController.StartUp();
        backButton.getScene().setRoot(root);
    }
}
