package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;

import edu.wpi.cs3733d18.teamp.Exceptions.EdgeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;

import edu.wpi.cs3733d18.teamp.Exceptions.OrphanNodeException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.ui.admin.MapBuilderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MapBuilderEdgeFormController {

    DeletePopUpController deletePopUpController;

    DBSystem db = DBSystem.getInstance();

    Boolean editFlag = false;
    String editedEdgeID = null;
    Edge submittedEdge;


    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton deleteButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    JFXTextField startNodeTxt;

    @FXML
    JFXTextField endNodeTxt;

    @FXML
    JFXCheckBox isActiveEdgeCheckBox;

    @FXML
    Label edgeFormErrorLabel;

    @FXML
    GridPane formPane;

    /**
     * Initializes the MapBuilderController object and sets the delete button to invisible
     */
    MapBuilderController mapBuilderController;
    public void startUp(MapBuilderController mapBuilderController){
        this.mapBuilderController = mapBuilderController;
        deleteButton.setVisible(false);
    }

    public void startUp(MapBuilderController mapBuilderController, String edgeID, Node startNode,
                        Node endNode, Boolean isActive){
        this.mapBuilderController = mapBuilderController;
        editedEdgeID = edgeID;
        startNodeTxt.setText(startNode.getID());
        endNodeTxt.setText(endNode.getID());
        isActiveEdgeCheckBox.setSelected(isActive);
        deleteButton.setVisible(true);
        editFlag = true;
    }

    /**
     * Cancels the form if the user decides they don't need to make a new node or edge
     * @param e the action of clicking the button
     */
    @FXML
    public void cancelFormButtonOp(ActionEvent e){
        mapBuilderController.addOverlay();
    }

    /**
     * Submits the Edge form, creating a node and then passing it to the EdgeRepo
     * @param e
     */
    @FXML
    public void submitEdgeFormButtonOp(ActionEvent e) {
        Boolean isActive = isActiveEdgeCheckBox.isSelected();
        Node startNode, endNode;

        // Validate both fields have been filled in by user
        try {
            startNode = db.getOneNode(startNodeTxt.getText());
            endNode = db.getOneNode(endNodeTxt.getText());
        } catch (NodeNotFoundException nnfe) {
            edgeFormErrorLabel.setText("Please choose two valid nodes.");
            edgeFormErrorLabel.setVisible(true);
            return;
        }
        Edge edge = new Edge(startNode, endNode, isActive);

        // Validate an edge does not already exist between these nodes TODO probably not necessary

        // After validation, create the edge
        try {
            if (editFlag) {
                edge.setID(editedEdgeID);
                db.modifyEdge(edge);
                editedEdgeID = null;
            }
            else {
                db.createEdge(edge);
            }
        }
        catch(NodeNotFoundException nnfe){
            nnfe.printStackTrace();
        }
        catch(EdgeNotFoundException enfe){
            enfe.printStackTrace();
        }
        catch (OrphanNodeException oe) {
            edgeFormErrorLabel.setText("Modifying Edge will orphan Node: " + oe.getNodeID());
            edgeFormErrorLabel.setVisible(true);
            return;
        }

        mapBuilderController.updateMap();
        mapBuilderController.addOverlay();
    }

    /**
     * Sets the value of the start node label
     * @param startNodeID
     */
    public void setStartNodeTxt(String startNodeID){
        startNodeTxt.setText(startNodeID);
    }

    /**
     * Sets the value of the end node label
     * @param endNodeID
     */
    public void setEndNodeTxt(String endNodeID){
        endNodeTxt.setText(endNodeID);
    }

    /**
     * Checks if the focus is on the start node bar
     * @return
     */
    public Boolean checkStartNodeBar(){
        if (startNodeTxt.isFocused()){
            return true;
        }
        return false;
    }

    /**
     * Checks if the focus is on the end node bar
     * @return
     */
    public Boolean checkEndNodeBar(){
        if (endNodeTxt.isFocused())
            return true;
        return false;
    }

    /**
     * Deletes an edge
     */
    public void deleteButtonOp(){

        // Delete Node Popup if edge can be deleted
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/general/ConfirmationPopUp.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }

        deletePopUpController = loader.getController();
        deletePopUpController.StartUp(this);
        stage.setScene(new Scene(root, 600, 150));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(deleteButton.getScene().getWindow());
        stage.showAndWait();

        // If user confirms, delete edge if it will not orphan a node
        if (deletePopUpController.getChoice()) {
            try {
                db.deleteEdge(editedEdgeID);
            }
            catch (NodeNotFoundException | EdgeNotFoundException ne) {
                ne.printStackTrace();
            }
            catch (OrphanNodeException oe) {
                edgeFormErrorLabel.setText("Deleting Edge will orphan Node: " + oe.getNodeID());
                edgeFormErrorLabel.setVisible(true);
                return;
            }
            mapBuilderController.updateMap();
            mapBuilderController.addOverlay();
        }
    }

}
