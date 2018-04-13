package edu.wpi.cs3733d18.teamp.ui.map;

import edu.wpi.cs3733d18.teamp.SendEmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EmailPopUpController implements Initializable {
    ArrayList<String> pathDirections;

    // Elements for popup
    @FXML
    Label titleLabel;

    @FXML
    Label warningLabel;

    @FXML
    TextField emailField;

    @FXML
    Button cancelButton;

    @FXML
    Button submitButton;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public boolean startUp(ArrayList<String> directions) {
        emailField.requestFocus();
        pathDirections = directions;
        return true;
    }

    @FXML
    public void cancelButtonOp(ActionEvent e) {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void submitButtonOp(ActionEvent e) {
        // Send directions thru email
        String emailAddr = emailField.getText();
        if (pathDirections == null) {
            warningLabel.setText("Please choose a path first");
        }
        else if (emailAddr.equals("")) {
            warningLabel.setText("Please submit a non-empty email");
        }
        else if (!validateEmail(emailAddr)) {
            warningLabel.setText("Please submit a valid email");
        }
        else {
            SendEmail email = new SendEmail(emailAddr);
//        SendEmail email = new SendEmail("8605783287@txt.att.net");
            email.sendEmail(pathDirections);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        }
    }

    private boolean validateEmail(String email) {
        if (!email.contains("@")) {
            return false;
        }
        else if (!email.contains(".com") && !email.contains(".edu")) {
            return false;
        }
        else {
            return true;
        }
    }
}
