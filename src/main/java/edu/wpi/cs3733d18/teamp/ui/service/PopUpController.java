package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PopUpController implements Initializable {

    LanguageInterpreterController languageInterpreterController;
    ReligiousServiceController religiousServiceController;
    ComputerServiceController computerServiceController;
    GiftDeliveryController giftDeliveryController;
    MaintenanceController maintenanceController;
    AudioVisualController audioVisualController;
    SanitationController sanitationController;
    SecurityController securityController;

    ServiceRequestScreen serviceRequestScreen;
    DBSystem db = DBSystem.getInstance();

    // Elements for popup Screen
    @FXML
    BorderPane serviceRequestPopup;

    @FXML
    JFXComboBox selectFormComboBox;

    static ObservableList<String> requestTypes = FXCollections.observableArrayList(
            "Language Interpreter Request",
            "Religious Request",
            "Computer Service Request",
            "Security Request",
            "Maintenance Request",
            "Sanitation Request",
            "Audio or Visual Help Request",
            "Gift Delivery Request"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectFormComboBox.setItems(requestTypes);
    }

    /**
     * Allows the two .java files to communicate to each other
     * @param serviceRequestScreen
     */
    public void StartUp(ServiceRequestScreen serviceRequestScreen, String selection) {
        this.serviceRequestScreen = serviceRequestScreen;

        selectFormComboBox.setValue(selection);
        String requestType;
        Parent root = null;
        FXMLLoader loader;
        Scene scene;
        //removes the word request from the MenuItem as well as the spaces
        String regex = "\\s*\\bRequest\\b\\s*";
        String formName = selection.replaceAll(regex, "");
        formName = formName.replaceAll("\\s", "");
        requestType = formName;



        //loads the appropriate scene depending on the option selected
        loader = new FXMLLoader(getClass().getResource("/FXML/service/" + requestType + "Form.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        switch(requestType) {
            case "LanguageInterpreter":
                languageInterpreterController = loader.getController();
                languageInterpreterController.StartUp(serviceRequestScreen, this);
                break;
            case "Religious":
                religiousServiceController = loader.getController();
                religiousServiceController.StartUp(serviceRequestScreen, this);
                break;
            case "ComputerService":
                computerServiceController = loader.getController();
                computerServiceController.StartUp(serviceRequestScreen, this);
                break;
            case "Security":
                securityController = loader.getController();
                securityController.StartUp(serviceRequestScreen, this);
                break;
            case "Maintenance":
                maintenanceController = loader.getController();
                maintenanceController.StartUp(serviceRequestScreen,this);
                break;
            case "Sanitation":
                sanitationController = loader.getController();
                sanitationController.StartUp(serviceRequestScreen, this);
                break;
            case "AudioorVisualHelp":
                audioVisualController = loader.getController();
                audioVisualController.StartUp(serviceRequestScreen,this);
                break;
            case "GiftDelivery":
                giftDeliveryController = loader.getController();
                giftDeliveryController.StartUp(serviceRequestScreen,this);
                break;

        }
        serviceRequestPopup.setCenter(root);
    }

    /**
     * Will switch the middle of the border pane depending upon the type of form that was selected
     * @param e MenuItem Selected
     * @throws IOException
     */
    @FXML
    public Boolean serviceRequestFormSelectOp(ActionEvent e) {
        String requestType;
        Parent root;
        FXMLLoader loader;
        Scene scene;
        //removes the word request from the MenuItem as well as the spaces
        String regex = "\\s*\\bRequest\\b\\s*";
        String formName = selectFormComboBox.getSelectionModel().getSelectedItem().toString().replaceAll(regex, "");
        formName = formName.replaceAll("\\s", "");
        requestType = formName;

        //loads the appropriate scene depending on the option selected
        loader = new FXMLLoader(getClass().getResource("/FXML/service/" + requestType + "Form.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }

        switch(requestType) {
            case "LanguageInterpreter":
                languageInterpreterController = loader.getController();
                languageInterpreterController.StartUp(serviceRequestScreen, this);
                break;
            case "Religious":
                religiousServiceController = loader.getController();
                religiousServiceController.StartUp(serviceRequestScreen, this);
                break;
            case "ComputerService":
                computerServiceController = loader.getController();
                computerServiceController.StartUp(serviceRequestScreen, this);
                break;
            case "Security":
                securityController = loader.getController();
                securityController.StartUp(serviceRequestScreen, this);
                break;
            case "Maintenance":
                maintenanceController = loader.getController();
                maintenanceController.StartUp(serviceRequestScreen,this);
                break;
            case "Sanitation":
                sanitationController = loader.getController();
                sanitationController.StartUp(serviceRequestScreen, this);
                break;
            case "AudioorVisualHelp":
                audioVisualController = loader.getController();
                audioVisualController.StartUp(serviceRequestScreen,this);
                break;
            case "GiftDelivery":
                giftDeliveryController = loader.getController();
                giftDeliveryController.StartUp(serviceRequestScreen,this);
                break;

        }
        serviceRequestPopup.setCenter(root);

        return true;
    }
}
