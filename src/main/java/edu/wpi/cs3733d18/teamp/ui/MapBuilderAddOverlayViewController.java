package edu.wpi.cs3733d18.teamp.ui;

import edu.wpi.cs3733d18.teamp.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MapBuilderAddOverlayViewController {

    AdminMapViewController adminMapViewController = null;

    @FXML
    Button addNewNodeButton;

    @FXML
    Button addNewEdgeButton;

    @FXML
    Button backButton;

    /**
     * initializes the adminMapViewController so this object can interact with the main screen
     * @param adminMapViewController
     */
    public void startUp(AdminMapViewController adminMapViewController){
        this.adminMapViewController = adminMapViewController;
    }

    /**
     * calls the newNodeForm() from the AdminMapViewController class
     */
    @FXML
    public void newNodeButtonOp(){
        adminMapViewController.newNodeForm();

    }

    /**
     * calls the newEdgeForm() from the AdminMapViewController class
     */
    @FXML
    public void newEdgeButtonOp(){
        adminMapViewController.newEdgeForm();
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
        loader = new FXMLLoader(getClass().getResource("/AdminMenuScreen.fxml"));
        root = loader.load();

        stage.setScene(new Scene(root, 1920, 1080));
        stage.setFullScreen(true);
        stage.show();
    }
}
