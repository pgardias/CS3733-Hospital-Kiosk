package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.AccessNotAllowedException;
import edu.wpi.cs3733d18.teamp.Exceptions.EmployeeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.ui.map.MapScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

public class MainController {
    String userType;
    private DBSystem db = DBSystem.getInstance();
    private ArrayList<Character> loginID = new ArrayList<>();

    @FXML
    JFXButton adminButton;

    @FXML
    JFXButton serviceButton;

    @FXML
    JFXButton mapButton;

    @FXML
    JFXTextField usernameTxt;

    @FXML
    JFXPasswordField passwordTxt;

    @FXML
    JFXButton loginButton;

    @FXML
    Label loginErrorLabel;

    @FXML
    Rectangle loginRectangle;


    @FXML
    public void initialize() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loginRectangle.requestFocus();
            }
        });
        loginRectangle.addEventHandler(KeyEvent.KEY_TYPED, keyEventEventHandler);
    }


    /**
     * this is the event handler for clicking on the map to create a node
     */
    EventHandler<KeyEvent> keyEventEventHandler = new EventHandler<KeyEvent>() {
        Boolean isDragging;

        @Override
        public void handle(KeyEvent event) {
            swipeLogin(event);
            event.consume();
        }
    };


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
            } catch (LoginInvalidException e1) {
                usernameTxt.setPromptText("Username");
                passwordTxt.setPromptText("Password");
                usernameTxt.clear();
                passwordTxt.clear();
                loginErrorLabel.setText("Login failed, invalid username or password");
                loginErrorLabel.setVisible(true);

                return;
            }

        if (Main.currentUser.getIsAdmin()){
            System.out.println("Logging in Admin");
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
            loginButton.getScene().setRoot(root);
        } else {
            System.out.println("Logging in Employee");
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
            loginButton.getScene().setRoot(root);
        }
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
    public void aboutButtonOp() {

    }


    @FXML
    public void swipeLogin(KeyEvent ke) {
        boolean goodToLogin = false;
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
                        }
                        break;

                    case 2:
                        if (c == '0') {
                            loginState = 3;
                        }
                        break;

                    case 3:
                        if (c == '1') {
                            loginState = 4;
                        }
                        break;

                    case 4:
                        if (c == '?') {
                            loginState = 5;
                        } else {
                            loginCount = 0;
                            loginState = 0;
                            loginString = "";
                        }
                        break;
                    case 5:
                        loginRectangle.removeEventHandler(KeyEvent.KEY_TYPED, keyEventEventHandler);
                        goodToLogin = true;
                        break;
                }
            }
            if (goodToLogin) {
                try {
                    Main.currentUser = db.checkLoginID(loginString);
                } catch (LoginInvalidException le) {
                    le.printStackTrace();
                    loginErrorLabel.setText("Login failed, please swipe again");
                    loginErrorLabel.setVisible(true);
                }

                usernameTxt.setText(Main.currentUser.getUserName());
                passwordTxt.setText(Main.currentUser.getPassword());

                loginButtonOp(new ActionEvent());
            }
//            toClose = (Stage) loginButton.getScene().getWindow();
//            toClose.close();
        }
    }
}