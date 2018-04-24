package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.fxml.FXML;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class ConfirmationPopUpController {
    private MapBuilderNodeFormController mapBuilderNodeFormController;
    private MapBuilderEdgeFormController mapBuilderEdgeFormController;
    private ManageEmployeeScreenController manageEmployeeScreenController;
    private ServiceRequestScreen serviceRequestScreen;
    private SettingsController settingsController;
    private Boolean actionConfirmed = false;
    private String settingsButtonOp;

    @FXML
    Label itemLabel;

    @FXML
    JFXButton confirmationButton;

    @FXML
    JFXButton cancelButton;

    /**
     * Constructor for deleting Nodes
     * @param mapBuilderNodeFormController
     */
    public void StartUp(MapBuilderNodeFormController mapBuilderNodeFormController) {
        this.mapBuilderNodeFormController = mapBuilderNodeFormController;
        itemLabel.setText("Are you sure you want to delete this node? This cannot be undone.");
        confirmationButton.setText("Delete");
    }

    /**
     * Constructor for deleting Edges
     * @param mapBuilderEdgeFormController
     */
    public void StartUp(MapBuilderEdgeFormController mapBuilderEdgeFormController) {
        this.mapBuilderEdgeFormController = mapBuilderEdgeFormController;
        itemLabel.setText("Are you sure you want to delete this edge? This cannot be undone.");
        confirmationButton.setText("Delete");
    }

    /**
     * Constructor for deleting Employees
     * @param
     */
    public void StartUp(ManageEmployeeScreenController manageEmployeeScreenController) {
        this.manageEmployeeScreenController = manageEmployeeScreenController;
        itemLabel.setText("Are you sure you want to delete this employee? This cannot be undone.");
        confirmationButton.setText("Delete");
    }

    /**
     * Constructor for deleting Employees
     * @param
     */
    public void StartUp(ServiceRequestScreen serviceRequestScreen) {
        this.serviceRequestScreen = serviceRequestScreen;
        itemLabel.setText("You are now opening a new application. Are you sure you want to do this?");
        confirmationButton.setText("Open");
    }

    public void StartUp(SettingsController settingsController) {
        this.settingsController = settingsController;


        switch(settingsButtonOp){
            case"Back":
                itemLabel.setText("Unsaved changes will be lost. Do you wish to proceed?");
                confirmationButton.setText("Confirm");
                break;
            case "Submit":
                itemLabel.setText("Submit changes?");
                confirmationButton.setText("Confirm");
                break;
        }
    }

    @FXML
    public void confirmationButtonOp(){
        actionConfirmed = true;
        Stage stage = (Stage)confirmationButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelButtonOp(){
        actionConfirmed = false;
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }

    public Boolean getChoice() {
        return actionConfirmed;
    }

    public void setSettingsButtonOp(String settingsButtonOp) {
        this.settingsButtonOp = settingsButtonOp;
    }
}