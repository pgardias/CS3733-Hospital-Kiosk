package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenuController {

    @FXML
    JFXButton employeeButton;

    @FXML
    JFXButton mapManagementButton;

    @FXML
    JFXButton serviceRequestScreenButton;

    @FXML
    JFXButton settingsButton;

    @FXML
    JFXButton backButton;

    @FXML
    Label helloMessage;

    @FXML
    public void onStartup() {
        helloMessage.setText("Hello " + Main.currentUser.getFirstName() + " " + Main.currentUser.getLastName() + ", ");
    }

    @FXML
    public void employeeButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/ManageEmployeeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        employeeButton.getScene().setRoot(root);
    }

    @FXML
    public void mapManagementButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        MapBuilderController mapBuilderController;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/MapBuilder.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        mapBuilderController = loader.getController();
        mapBuilderController.startUp();
        mapManagementButton.getScene().setRoot(root);
    }

    @FXML
    public void serviceRequestScreenButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        ServiceRequestScreen serviceRequestScreen;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        serviceRequestScreen = loader.getController();
        serviceRequestScreen.onStartup();
        serviceRequestScreenButton.getScene().setRoot(root);
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
        Main.logoutCurrentUser();
        backButton.getScene().setRoot(root);
    }

    @FXML
    public void settingsButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        SettingsController settingsController;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/SettingsScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        settingsController = loader.getController();
        settingsController.onStartUp();
        settingsButton.getScene().setRoot(root);
    }
}
