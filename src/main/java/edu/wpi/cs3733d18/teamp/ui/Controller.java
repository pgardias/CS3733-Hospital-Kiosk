package edu.wpi.cs3733d18.teamp.ui;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.EmployeeRepo;
import edu.wpi.cs3733d18.teamp.Exceptions.AccessNotAllowedException;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    JFXButton adminButton;

    @FXML
    JFXButton serviceButton;

    @FXML
    JFXButton mapButton;

    /**
     * This function respond to clicking the either the service request
     * or admin login buttons opening the Login.fxml
     * The login.fxml then has a different login button function and title
     * depending on which one was clicked
     * @param e button press
     * @throws IOException
     */
    @FXML
    public void loginButtonOp(ActionEvent e) {
        LoginPopUpController loginPopUpController;
        Parent root;
        Stage stage;
        FXMLLoader loader;
        String text;

        stage = new Stage();
        //getting the FXML file to load into the scene
        loader = new FXMLLoader(getClass().getResource("/LoginPopUp.fxml"));
        //setting the new fxml file to this instance of the controller
        //loading the new FXML file
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        loginPopUpController = loader.getController();
        //setting the new root into a new scene and declaring it a pop-up
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Login Page");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(adminButton.getScene().getWindow());
        //check which button is pressed to display the correct title
        //TODO fix the method to work with new controller
        if(e.getSource() == adminButton) {
            loginPopUpController.startUp(true, this);
        }
        else {
            loginPopUpController.startUp(false, this);
        }
        stage.show();
    }


    /**
     * This method will bring the user to the main map screen when pressed
     * @param e button press
     * @return returns true if successful
     */
    @FXML
    public Boolean mapButtonOp(ActionEvent e) {
        FXMLLoader loader;
        Stage stage;
        Parent root;
        MapScreenController mapScreenController;
        Group showNodes = new Group();

        stage = (Stage) mapButton.getScene().getWindow();
        loader = new FXMLLoader(getClass().getResource("/MapScreen-Rebuild.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.getMessage();
            return false;
        }
        mapScreenController = loader.getController();
        mapScreenController.onStartUp();

        stage.setScene(new Scene(root, 1920, 1080));
        stage.setFullScreen(true);
        stage.show();
        return true;
    }

    @FXML
    public void goToServiceRequestScreen(){
        FXMLLoader loader;
        Stage stage;
        Parent root;

        loader = new FXMLLoader(getClass().getResource("/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        stage = (Stage) serviceButton.getScene().getWindow();
        stage.setFullScreen(false);
        stage.setScene(new Scene(root, 1920, 1080));
        stage.show();
        stage.setFullScreen(true);
    }

    @FXML
    public void goToAdminRequestScreen(){
        FXMLLoader loader;
        Stage stage;
        Parent root;

        loader = new FXMLLoader(getClass().getResource("/AdminMenuScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        stage = (Stage) adminButton.getScene().getWindow();
        stage.setFullScreen(false);
        stage.setScene(new Scene(root, 1920, 1080));
        stage.setFullScreen(true);
        stage.show();
    }
}