package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.AccessNotAllowedException;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;
import edu.wpi.cs3733d18.teamp.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPopUpController {

    private DBSystem db = DBSystem.getInstance();
    MainController mainController = null;

    @FXML
    TextField usernameTxt;

    @FXML
    PasswordField passwordTxt;

    @FXML
    JFXButton cancelButton;

    @FXML
    JFXButton loginButton;

    @FXML
    Label loginLabel;

    @FXML
    Label loginErrorLabel;


    /**
     * this is the method called from MainController.java which initializes the login screen
     * depending on whether or not the admin button was clicked
     * @param isAdmin Boolean
     */
    @FXML
    public void startUp(Boolean isAdmin, MainController mainController){
        this.mainController = mainController;
        if (isAdmin){
            loginLabel.setText("Administrator Login");
            loginButton.setOnAction(e -> adminLoginButtonOp(e));
            usernameTxt.textProperty().addListener((observable, oldValue, newValue) -> {adminSwipeLogin();});
        } else {
            loginLabel.setText("Staff Member Login");
            loginButton.setOnAction(e -> serviceRequestLoginButtonOp(e));
            usernameTxt.textProperty().addListener((observable, oldValue, newValue) -> {employeeSwipeLogin();});
        }
        usernameTxt.requestFocus();
    }
    /**
     * This method will bring the admin to the MapBuildingPage if the username
     * and password are accepted
     * @param e
     * @throws IOException
     */
    @FXML
    public void adminLoginButtonOp(ActionEvent e)  {
        FXMLLoader loader;
        Stage stage;
        Parent root;
        Stage toClose;

        String username = usernameTxt.getText();
        System.out.println("USERNAME: " + username);
        String password = passwordTxt.getText();
        System.out.println("PASSWORD: " + password);

        try {
            Main.currentUser = db.checkAdminLogin(username, password);
        } catch(LoginInvalidException le) {
            le.printStackTrace();
            loginErrorLabel.setText("Login failed, invalid username or password");
            loginErrorLabel.setVisible(true);
            return;
        } catch(AccessNotAllowedException ae){
            ae.printStackTrace();
            loginErrorLabel.setText("Login failed, access not allowed");
            loginErrorLabel.setVisible(true);
            return;
        }

        mainController.goToAdminRequestScreen();
        toClose = (Stage) loginButton.getScene().getWindow();
        toClose.close();
    }


    /**
     * This method will bring the user to the Service Request if the username
     * and password are accepted
     * @param e button press
     * @throws IOException
     */
    @FXML
    public void serviceRequestLoginButtonOp(ActionEvent e) {
        FXMLLoader loader;
        Stage stage;
        Parent root;
        Stage toClose;

        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        try {
            Main.currentUser = db.checkEmployeeLogin(username, password);
        } catch(LoginInvalidException le) {
            le.printStackTrace();
            loginErrorLabel.setText("Login failed, invalid username or password");
            loginErrorLabel.setVisible(true);
            return;
        }
        mainController.goToServiceRequestScreen();
        toClose = (Stage) loginButton.getScene().getWindow();
        toClose.close();
    }

    /**
     * The cancel button when pressed will close the login popup
     * @param e button press
     */
    @FXML
    public void cancelButtonOp(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    public void adminSwipeLogin() {
        usernameTxt.requestFocus();
        System.out.println(usernameTxt.getText());
        if (usernameTxt.getLength() > 11) {
            System.out.println("Swiped!");
            // %18262028501?+18262028501?
            // ;18262028501?+18262028501?
            // %89068008401?;89068008401?+89068008401?
            char[] login = usernameTxt.getText().toCharArray();
            int loginState = 0;
            int loginCount = 0;
            String loginString = "";

            for (char c : login) {
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
                        if (c == '?') {
                            loginState = 3;
                        }
                        break;
                }
                if (loginState == 3) {
                    break;
                }
            }
//            usernameTxt.setText(loginString);

            try {
                Main.currentUser = db.checkAdminLoginID(loginString);
            } catch (LoginInvalidException le) {
                le.printStackTrace();
                loginErrorLabel.setText("Login failed, please swipe again");
                loginErrorLabel.setVisible(true);
            } catch(AccessNotAllowedException ae){
                ae.printStackTrace();
                loginErrorLabel.setText("Login failed, access not allowed");
                loginErrorLabel.setVisible(true);
                return;
            }

            Stage toClose;
            mainController.goToAdminRequestScreen();
            toClose = (Stage) loginButton.getScene().getWindow();
            toClose.close();
        }
    }

    public void employeeSwipeLogin() {
        usernameTxt.requestFocus();
        if (usernameTxt.getLength() > 11) {
            System.out.println("Swiped!");
            // %18262028501?+18262028501?
            // ;18262028501?+18262028501?
            // %89068008401?;89068008401?+89068008401?
            char[] login = usernameTxt.getText().toCharArray();
            int loginState = 0;
            int loginCount = 0;
            String loginString = "";

            for (char c : login) {
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
                        if (c == '?') {
                            loginState = 3;
                        }
                        break;
                }
                if (loginState == 3) {
                    break;
                }
            }
//            usernameTxt.setText(loginString);

            try {
                Main.currentUser = db.checkEmployeeLoginID(loginString);
            } catch (LoginInvalidException le) {
                le.printStackTrace();
                loginErrorLabel.setText("Login failed, please swipe again");
                loginErrorLabel.setVisible(true);
            }

            Stage toClose;
            mainController.goToServiceRequestScreen();
            toClose = (Stage) loginButton.getScene().getWindow();
            toClose.close();
        }
    }

}
