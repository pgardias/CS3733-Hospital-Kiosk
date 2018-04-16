package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class DeletePopUpController {
    private MapBuilderNodeFormController mapBuilderNodeFormController;
    private MapBuilderEdgeFormController mapBuilderEdgeFormController;
    private Boolean isDelete;

    @FXML
    Label itemLabel;

    @FXML
    JFXButton deleteButton;

    @FXML
    JFXButton cancelButton;

    /**
     * Constructor for deleting Nodes
     * @param mapBuilderNodeFormController
     */
    public void StartUp(MapBuilderNodeFormController mapBuilderNodeFormController) {
        this.mapBuilderNodeFormController = mapBuilderNodeFormController;
        itemLabel.setText("Are you sure you want to delete this node? This cannot be undone.");
    }

    /**
     * Constructor for deleting Edges
     * @param mapBuilderEdgeFormController
     */
    public void StartUp(MapBuilderEdgeFormController mapBuilderEdgeFormController) {
        this.mapBuilderEdgeFormController = mapBuilderEdgeFormController;
        itemLabel.setText("Are you sure you want to delete this edge? This cannot be undone.");
    }

    @FXML
    public void deleteButtonOp(){
        isDelete = true;
        Stage stage = (Stage)deleteButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelButtonOp(){
        isDelete = false;
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }

    public Boolean getChoice() {
        return isDelete;
    }
}