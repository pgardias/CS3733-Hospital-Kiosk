package edu.wpi.cs3733d18.teamp.ui.admin;

import edu.wpi.cs3733d18.teamp.ui.admin.MapBuilderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MapBuilderOverlayController {

    MapBuilderController mapBuilderController = null;

    @FXML
    Button addNewNodeButton;

    @FXML
    Button addNewEdgeButton;

    @FXML
    Button backButton;

    /**
     * initializes the mapBuilderController so this object can interact with the main screen
     * @param mapBuilderController
     */
    public void startUp(MapBuilderController mapBuilderController){
        this.mapBuilderController = mapBuilderController;
    }

    /**
     * calls the newNodeForm() from the MapBuilderController class
     */
    @FXML
    public void newNodeButtonOp(){
        mapBuilderController.newNodeForm();

    }

    /**
     * calls the newEdgeForm() from the MapBuilderController class
     */
    @FXML
    public void newEdgeButtonOp(){
        mapBuilderController.newEdgeForm();
    }

    /**
     * brings the user back to the home screen
     * @param e button press
     * @throws IOException
     */
    @FXML
    public void backButtonOp(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        //setting stage back to home screen
        stage = (Stage) backButton.getScene().getWindow();
        loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
        root = loader.load();

        backButton.getScene().setRoot(root);
    }
}
