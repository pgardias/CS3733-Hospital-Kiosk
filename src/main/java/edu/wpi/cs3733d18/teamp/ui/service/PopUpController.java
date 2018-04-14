package edu.wpi.cs3733d18.teamp.ui.service;

import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.ui.home.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class PopUpController {

    LanguageInterpreterController languageInterpreterController;
    ReligiousServiceController religiousServiceController;
    ServiceRequestScreen serviceRequestScreen;
    DBSystem db = DBSystem.getInstance();
    MainController mainController = null;
    private String requestType;

    // Elements for popup Screen
    @FXML
    BorderPane serviceRequestPopup;

    @FXML
    MenuItem languageInterpreter;

    @FXML
    MenuItem religiousPeople;

    @FXML
    MenuButton serviceRequestMenu;

    /**
     * Allows the two .java files to communicate to each other
     * @param serviceRequestScreen
     */
    public void StartUp(ServiceRequestScreen serviceRequestScreen) {
        this.serviceRequestScreen = serviceRequestScreen;
    }

    /**
     * Will switch the middle of the border pane depending upon the type of form that was selected
     * @param e MenuItem Selected
     * @throws IOException
     */
    @FXML
    public Boolean serviceRequestFormSelectOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        Scene scene;
        MenuItem selectedForm = (MenuItem) e.getSource();
        //removes the word request from the MenuItem as well as the spaces
        String regex = "\\s*\\bRequest\\b\\s*";
        String formName = selectedForm.getText().replaceAll(regex, "");
        formName = formName.replaceAll("\\s", "");
        requestType = formName;

        //loads the appropriate scene depending on the option selected
        serviceRequestMenu.setText(selectedForm.getText());
        loader = new FXMLLoader(getClass().getResource("/FXML/service/" + requestType + "Form.fxml"));

        try {
            root = loader.load();
            System.out.println("LOADED");
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }

        if (requestType.equals("LanguageInterpreter")) {
            languageInterpreterController = loader.getController();
            languageInterpreterController.StartUp(serviceRequestScreen, this);
            serviceRequestPopup.setCenter(root);
        }
        if (requestType.equals("Religious")) {
            religiousServiceController = loader.getController();
            religiousServiceController.StartUp(serviceRequestScreen, this);
            serviceRequestPopup.setCenter(root);
        }

        return true;
    }
}
