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

public class AboutScreenController {

    @FXML
    JFXButton backButton;

    @FXML
    Label introLabel;

    @FXML
    Label teamLabel;

    @FXML
    Label finalLabel;

    public void StartUp() {
        introLabel.setText(
                "We are a team of ten WPI students. This application is our project for the course CS3733-D18 Software Engineering. \nHere is our team:"
        );
        teamLabel.setText(
                "Professor Wilson Wong - Instructor\n" +
                        "Kyle Corry - Team Coach\n" +
                        "Kyle Savell - Lead Developer \n" +
                        "Ben Titus - Assistant Lead Developer\n" +
                        "Przemek Gardias - Assistant Lead Developer\n" +
                        "Jason King - Assistant Lead Developer\n" +
                        "Cem Alemdar - Project Manager\n" +
                        "Jarod Thompson - Assistant Project Manager\n" +
                        "Nick Pacheco - Product Owner\n" +
                        "Marc McFatter - UI/UX Engineer\n" +
                        "Harry Saperstein - Test Engineer\n" +
                        "Eoin O’Connell - Documentation Analyst\n"
        );
        finalLabel.setText(
                "Special Thanks To:\n" +
                        "WPI Computer Science Department,\n" +
                        "Brigham and Women’s Faulkner Hospital\n" +
                        "and\n" +
                        "Andrew Shinn\n"
        );
    }

    @FXML
    public void backButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        backButton.getScene().setRoot(root);
    }
}
