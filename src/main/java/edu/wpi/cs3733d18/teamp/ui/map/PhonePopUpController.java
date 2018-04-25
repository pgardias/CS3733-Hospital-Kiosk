package edu.wpi.cs3733d18.teamp.ui.map;

import edu.wpi.cs3733d18.teamp.SendEmail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PhonePopUpController implements Initializable {
    ArrayList<String> pathDirections;

    // Elements for popup
    @FXML
    Label titleLabel;

    @FXML
    Label warningLabel;

    @FXML
    TextField phoneNumField;

    @FXML
    ComboBox carrierComboBox;

    @FXML
    Button cancelButton;

    @FXML
    Button submitButton;

    ObservableList<String> carriers = FXCollections.observableArrayList(
            "AT&T",
            "Verizon",
            "T-Mobile",
            "Sprint",
            "Boost",
            "MetroPCS"
    );

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        carrierComboBox.setItems(carriers);
    }

    @FXML
    public boolean startUp(ArrayList<String> directions) {
        carrierComboBox.requestFocus();
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
        // Send directions thru text email
        if (pathDirections == null) {
            warningLabel.setText("Please choose a path first");
        }
        else if (carrierComboBox.getSelectionModel().isEmpty()) {
            warningLabel.setText("Please choose a carrier first");
        }
        else {
            String phoneNum = phoneNumField.getText();
            String phoneCarrier = carrierComboBox.getValue().toString();
            String emailAddr = "";
            if (phoneNum.equals("")) {
                warningLabel.setText("Please submit a non-empty phone number");
            } else if (phoneCarrier.equals("Choose Carrier")) {
                warningLabel.setText("Please choose a carrier");
            } else if (!validateNumber(phoneNum)) {
                warningLabel.setText("Please submit a valid phone number");
            } else {
                emailAddr = emailAddr.concat(phoneNum);
                emailAddr = emailAddr.concat(parseCarrier(phoneCarrier));
                SendEmail email = new SendEmail(emailAddr);
                email.sendText(pathDirections);
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Test to see if a phone number is legitimate
     * @param number The phone # to check
     * @return True if the # is valid
     */
    private boolean validateNumber(String number) {
        if (number.length() < 10 || number.length() > 11) {
            return false;
        }
        return true;
    }

    /** Switch to return the desired email suffix for the carrier
     *
     * @param carrier The carrier we are using
     * @return The suffix of the text email
     */
    private String parseCarrier(String carrier) {
        String carrierSuffix;
        switch(carrier) {
            case "AT&T":
                carrierSuffix = "@mms.att.net";
                break;
            case "Verizon":
                carrierSuffix = "@vtext.com";
                break;
            case "T-Mobile":
                carrierSuffix = "@tmomail.net";
                break;
            case "Sprint":
                carrierSuffix = "@messaging.sprintpcs.com";
                break;
            case "Boost":
                carrierSuffix = "@myboostmobile.com";
                break;
            case "MetroPCS":
                carrierSuffix = "@mymetropcs.com";
                break;
            default:
                carrierSuffix = "";
                break;
        }
        return carrierSuffix;
    }
}
