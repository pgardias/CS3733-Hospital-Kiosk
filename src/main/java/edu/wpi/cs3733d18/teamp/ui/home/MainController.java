package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.ui.map.MapScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    String userType;
    private DBSystem db = DBSystem.getInstance();
    @FXML
    JFXButton adminButton;

    @FXML
    JFXButton serviceButton;

    @FXML
    JFXButton mapButton;

    @FXML
    Button threeDMap;

    @FXML
    JFXTextField usernameTxt;

    @FXML
    JFXPasswordField passwordTxt;

    @FXML
    JFXButton loginButton;

    @FXML
    Label loginErrorLabel;
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

        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        try {
            Main.currentUser = db.checkEmployeeLogin(username, password);
        } catch(LoginInvalidException e1) {
            usernameTxt.setPromptText("Username");
            passwordTxt.setPromptText("Password");
            usernameTxt.clear();
            passwordTxt.clear();
            loginErrorLabel.setText("Login failed, invalid username or password");
            loginErrorLabel.setVisible(true);

            return;
        }

        if (Main.currentUser.getIsAdmin()){
            goToAdminRequestScreen();
        } else{
            goToServiceRequestScreen();
        }


//        loader = new FXMLLoader(getClass().getResource("/FXML/home/LoginPopUp.fxml"));
//        //setting the new fxml file to this instance of the mainController
//        //loading the new FXML file
//        try {
//            root = loader.load();
//        } catch (IOException ie) {
//            ie.printStackTrace();
//            return;
//        }
       // loginPopUpController = loader.getController();
        //setting the new root into a new scene and declaring it a pop-up
        //stage.setScene(new Scene(root, 600, 400));
//        stage.setTitle("Login Page");
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(adminButton.getScene().getWindow());
        //check which button is pressed to display the correct title
        //TODO fix the method to work with new mainController
//        if(e.getSource() == adminButton) {
//            loginPopUpController.startUp(true, this);
//        }
//        else {
//            loginPopUpController.startUp(false, this);
//        }
//        stage.show();
//        usernameTxt.requestFocus();
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
        loader = new FXMLLoader(getClass().getResource("/FXML/map/MapScreen.fxml"));
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

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        stage = (Stage) loginButton.getScene().getWindow();
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

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        stage = (Stage) loginButton.getScene().getWindow();
        stage.setFullScreen(false);
        stage.setScene(new Scene(root, 1920, 1080));
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    public void threeDMapOp(){
        FXMLLoader loader;
        Stage stage;
        Parent root;
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera();

        loader = new FXMLLoader(getClass().getResource("/FXML/map/ThreeDMap.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        stage = (Stage) threeDMap.getScene().getWindow();
        stage.setFullScreen(false);
        Scene scene = new Scene(root, 1920, 1080);
        scene.setCamera(perspectiveCamera);
        stage.setScene(scene);
        //stage.setScene(new Scene(root, 1920, 1080));
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    public void loginButtonOp() {
//        final Timeline timeline = new Timeline();
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.setAutoReverse(true);
//        final KeyValue kv = new KeyValue(loginTxt.opacityProperty(), 0);
//        final KeyFrame kf = new KeyFrame(Duration.millis(600), kv);
//        timeline.getKeyFrames().add(kf);
//        timeline.play();
    }
}