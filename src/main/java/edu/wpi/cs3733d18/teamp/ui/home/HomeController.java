package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.ui.admin.AdminMenuController;
import edu.wpi.cs3733d18.teamp.ui.map.MapScreenController;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class HomeController {

    private DBSystem db = DBSystem.getInstance();
    private ArrayList<Character> loginID = new ArrayList<>();
    private boolean swipeDetected = false;

    @FXML
    JFXButton emergencyButton;

    @FXML
    JFXButton mapButton;

    @FXML
    JFXTextField usernameTxt;

    @FXML
    JFXPasswordField passwordTxt;

    @FXML
    JFXButton loginButton;

    @FXML
    Rectangle loginRectangle;

    @FXML
    ImageView aboutButton;

    @FXML
    public void initialize() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loginRectangle.requestFocus();
            }
        });
        loginRectangle.addEventHandler(KeyEvent.KEY_TYPED, keyEventEventHandler);
        usernameTxt.addEventHandler(KeyEvent.KEY_TYPED, keyEventEventHandler);
        passwordTxt.addEventHandler(KeyEvent.KEY_TYPED, keyEventEventHandler);
        usernameTxt.setText("");
        passwordTxt.setText("");
    }


    /**
     * this is the event handler for clicking on the map to create a node
     */
    EventHandler<KeyEvent> keyEventEventHandler = new EventHandler<KeyEvent>() {
        Boolean isDragging;

        @Override
        public void handle(KeyEvent event) {
            if (!swipeDetected) {
                if (event.getCharacter().equals("%") || event.getCharacter().equals(";")) {
                    swipeDetected = true;
                } else {
                    if (event.getSource().equals(usernameTxt)) {
                        usernameTxt.setText(usernameTxt.getText() + event.getCharacter());
                        usernameTxt.positionCaret(usernameTxt.getLength());
                    }
                }
                if (event.getSource().equals(passwordTxt)) {
                    passwordTxt.setText(passwordTxt.getText() + event.getCharacter());
                    passwordTxt.positionCaret(passwordTxt.getLength());
                }
            }
            if (swipeDetected) {
                swipeLogin(event);
            }
            event.consume();
        }
    };


    /**
     * This function respond to clicking the either the service request
     * or admin login buttons opening the Login.fxml
     * The login.fxml then has a different login button function and title
     * depending on which one was clicked
     *
     * @param e button press
     * @throws IOException
     */
    @FXML
    public void loginButtonOp(ActionEvent e) {

        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        AdminMenuController adminMenuController;


        try {
            Main.currentUser = db.checkEmployeeLogin(username, password);
        } catch (LoginInvalidException e1) {

            ShakeTransition anim = new ShakeTransition(usernameTxt, passwordTxt);
            anim.playFromStart();
            usernameTxt.clear();
            passwordTxt.clear();
            usernameTxt.requestFocus();
            return;
        }

        if (Main.currentUser.getIsAdmin()) {
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
            adminMenuController = loader.getController();
            adminMenuController.onStartup();
            loginButton.getScene().setRoot(root);
        } else {
            FXMLLoader loader;
            Stage stage;
            Parent root;
            ServiceRequestScreen serviceRequestScreen;

            loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return;
            }
            serviceRequestScreen = loader.getController();
            serviceRequestScreen.onStartup();
            loginButton.getScene().setRoot(root);
        }
    }


    /**
     * This method will bring the user to the main map screen when pressed
     *
     * @param e button press
     * @return returns true if successful
     */
    @FXML
    public Boolean mapButtonOp(ActionEvent e) {
        FXMLLoader loader;
        Parent root;
        MapScreenController mapScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/map/MapScreen.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        mapScreenController = loader.getController();
        mapScreenController.onStartUp();
        mapButton.getScene().setRoot(root);
        return true;
    }

    @FXML
    public void aboutButtonOp() {
        FXMLLoader loader;
        Parent root;
        AboutScreenController aboutScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/AboutScreen.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }

        aboutScreenController = loader.getController();
        aboutScreenController.StartUp();
        aboutButton.getScene().setRoot(root);
    }

    @FXML
    public void swipeLogin(KeyEvent ke) {
        boolean goodToLogin = false;
        boolean invalidSwipe = false;
        loginID.add(ke.getCharacter().toCharArray()[0]);
        if (loginID.size() > 13) {
            // %18262028501?+18262028501?
            // ;18262028501?+18262028501?
            // %89068008401?;89068008401?+89068008401?
            int loginState = 0;
            int loginCount = 0;
            String loginString = "";

            for (char c : loginID) {
                switch (loginState) {
                    case 0:
                        if (c == '%' || c == ';') {
                            loginState = 1;
                        } else {
                            loginCount = 0;
                            loginState = 0;
                            loginString = "";
                            invalidSwipe = true;
                        }
                        break;

                    case 1:
                        if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' ||
                                c == '6' || c == '7' || c == '8' || c == '9' || c == '0') {
                            loginCount++;
                            loginString = loginString + c;
                            if (loginCount == 9) {
                                loginState = 2;
                            }
                        } else {
                            loginCount = 0;
                            loginState = 0;
                            loginString = "";
                            invalidSwipe = true;
                        }
                        break;

                    case 2:
                        if (c == '0') {
                            loginState = 3;
                        } else {
                            loginCount = 0;
                            loginState = 0;
                            loginString = "";
                            invalidSwipe = true;
                        }
                        break;

                    case 3:
                        if (c == '1') {
                            loginState = 4;
                        } else {
                            loginCount = 0;
                            loginState = 0;
                            loginString = "";
                            invalidSwipe = true;
                        }
                        break;

                    case 4:
                        if (c == '?') {
                            loginState = 5;
                        } else {
                            loginCount = 0;
                            loginState = 0;
                            loginString = "";
                            invalidSwipe = true;
                        }
                        break;
                    case 5:
                        loginRectangle.removeEventHandler(KeyEvent.KEY_TYPED, keyEventEventHandler);
                        goodToLogin = true;
                        invalidSwipe = false;
                        break;
                }
            }
            if (goodToLogin) {
                try {
                    Main.currentUser = db.checkLoginID(loginString);
                } catch (LoginInvalidException le) {
                    swipeDetected = false;
                    loginID = new ArrayList<>();
                    usernameTxt.setText("");
                    usernameTxt.positionCaret(0);
                    passwordTxt.setText("");
                    passwordTxt.positionCaret(0);
//                    le.printStackTrace();
                    ShakeTransition anim = new ShakeTransition(usernameTxt, passwordTxt);
                    anim.playFromStart();
                    return;
                }

                usernameTxt.setText(Main.currentUser.getUserName());
                passwordTxt.setText(Main.currentUser.getPassword());

                loginButtonOp(new ActionEvent());
            } else {
                loginID = new ArrayList<>();
                usernameTxt.setText("");
                usernameTxt.positionCaret(0);
                passwordTxt.setText("");
                passwordTxt.positionCaret(0);
                if(invalidSwipe) {
                    ShakeTransition anim = new ShakeTransition(usernameTxt, passwordTxt);
                    anim.playFromStart();
                }
            }
        }
    }

    @FXML
    public Boolean emergencyButtonOp(ActionEvent e) {
        FXMLLoader loader;
        Scene scene;
        Parent root;
        EmergencyRequestButtonScreenController emergencyRequestButtonScreenController;

        scene =  emergencyButton.getScene();
        loader = new FXMLLoader(getClass().getResource("/FXML/home/EmergencyRequestButtonScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.getMessage();
            ie.printStackTrace();
            return false;
        }
        emergencyRequestButtonScreenController = loader.getController();
        emergencyRequestButtonScreenController.onStartUp(this);

        scene.setRoot(root);
        return true;
    }
}