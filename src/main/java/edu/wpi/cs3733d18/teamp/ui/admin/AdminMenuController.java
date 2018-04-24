package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class AdminMenuController {

    private Parent root;
    private FXMLLoader loader;
    private MapBuilderController mapBuilderController;

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
        mapManagementButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
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

                        mapBuilderController.formOverlayPane.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
                    }
                });
                return null;
            }
        };

        Thread th = new Thread(task);
        th.start();
    }

    @FXML
    public void serviceRequestScreenButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
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
