package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Coordinate;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.DuplicateLongNameException;
import edu.wpi.cs3733d18.teamp.Exceptions.EdgeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.OrphanNodeException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MapBuilderNodeFormController implements Initializable{

    DeletePopUpController deletePopUpController;

    public static final int FULL_WIDTH = 5000;
    public static final int FULL_HEIGHT = 3400;

    Boolean editFlag = false;

    DBSystem db = DBSystem.getInstance();
    String editedNodeID = null;

    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton deleteButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    JFXTextField longNameTxt;

    @FXML
    JFXTextField nodex2Txt;

    @FXML
    JFXTextField nodey2Txt;

    @FXML
    JFXTextField nodex3Txt;

    @FXML
    JFXTextField nodey3Txt;

    @FXML
    JFXCheckBox isActiveNodeCheckBox;

    @FXML
    JFXTextField connectingNodeTextBox;

    @FXML
    Label connectingNodeLabel;

    ObservableList<String> floorOptions = FXCollections.observableArrayList(
            Node.floorType.LEVEL_3.toString(),
            Node.floorType.LEVEL_2.toString(),
            Node.floorType.LEVEL_1.toString(),
            Node.floorType.LEVEL_G.toString(),
            Node.floorType.LEVEL_L1.toString(),
            Node.floorType.LEVEL_L2.toString()
    );

    @FXML
    JFXComboBox floorComboBox;

    //setting up combobox
    ObservableList<String> buildingOptions = FXCollections.observableArrayList(
            Node.buildingType.BTM.toString(),
            Node.buildingType.SHAPIRO.toString(),
            Node.buildingType.TOWER.toString(),
            Node.buildingType.FRANCIS_45.toString(),
            Node.buildingType.FRANCIS_15.toString()
    );

    @FXML
    JFXComboBox buildingComboBox;


    ObservableList<String> nodeTypes = FXCollections.observableArrayList(
            Node.nodeType.KIOS.toString(),
            Node.nodeType.CONF.toString(),
            Node.nodeType.HALL.toString(),
            Node.nodeType.DEPT.toString(),
            Node.nodeType.INFO.toString(),
            Node.nodeType.LABS.toString(),
            Node.nodeType.REST.toString(),
            Node.nodeType.SERV.toString(),
            Node.nodeType.STAI.toString(),
            Node.nodeType.EXIT.toString(),
            Node.nodeType.RETL.toString(),
            Node.nodeType.ELEV.toString()
    );

    @FXML
    JFXComboBox nodeTypeComboBox;

    @FXML
    Label nodeFormErrorLabel;

    ArrayList<String> destinationWords = new ArrayList<>();

    ArrayList<String> sourceWords = new ArrayList<>();

    ArrayList<Node> path = new ArrayList<>();

    Node connectingNode = null; // Needs to be global because of multithreading

    /**
     * sets up the word array both search bars will be using
     */
    private void setWordArrays() {
        HashMap<String, Node> nodeSet;

        nodeSet = db.getAllNodes();

        int i = 0;
        for (Node node : nodeSet.values()) {
            sourceWords.add(node.getLongName());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        setWordArrays();

        AutoCompletionBinding<String> sourceBinding = TextFields.bindAutoCompletion(connectingNodeTextBox, sourceWords);

        sourceBinding.setPrefWidth(connectingNodeTextBox.getPrefWidth());
    }

    MapBuilderController mapBuilderController;

    /**
     * initializes a blank node form fxml
     * MapBuilderController is initializes
     * and the combo boxes are set up
     * @param mapBuilderController
     */
    public void startUp(MapBuilderController mapBuilderController){
        this.mapBuilderController = mapBuilderController;

        nodeTypeComboBox.setItems(nodeTypes);
        buildingComboBox.setItems(buildingOptions);
        floorComboBox.setItems(floorOptions);
        deleteButton.setVisible(false);
        connectingNodeTextBox.setDisable(false);
        connectingNodeTextBox.setVisible(true);
    }

    /**
     * intializes a filled in node form fxml
     * MapBuilderController is intialized
     * @param mapBuilderController
     * @param nodeID
     * @param nodeLongName
     * @param x2d
     * @param y2d
     * @param x3d
     * @param y3d
     * @param nodeFloor
     * @param nodeBuilding
     * @param nodeType
     * @param isActive
     */
    public void startUp(MapBuilderController mapBuilderController, String nodeID, String nodeLongName,
                        double x2d, double y2d, double x3d, double y3d, String nodeFloor, String nodeBuilding,
                        String nodeType, Boolean isActive){
        this.mapBuilderController = mapBuilderController;
        longNameTxt.setText(nodeLongName);
        nodex2Txt.setText(Double.toString(x2d));
        nodey2Txt.setText(Double.toString(y2d));
        nodex3Txt.setText(Double.toString(x3d));
        nodey3Txt.setText(Double.toString(y3d));
        buildingComboBox.setValue(nodeBuilding);
        floorComboBox.setValue(nodeFloor);
        System.out.println("Node type: " + nodeType);
        nodeTypeComboBox.setValue(nodeType);
        isActiveNodeCheckBox.setSelected(isActive);
        System.out.println("Initialized is active check box: " + isActiveNodeCheckBox.isSelected());
        editedNodeID = nodeID;
        connectingNodeTextBox.setDisable(true);
        connectingNodeTextBox.setVisible(false);
        connectingNodeLabel.setVisible(false);

        deleteButton.setVisible(true);
        nodeTypeComboBox.setItems(nodeTypes);
        buildingComboBox.setItems(buildingOptions);
        floorComboBox.setItems(floorOptions);

        editFlag = true;

        /*
        submitFormButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                Task task = new Task() {
                    @Override
                    protected Integer call() {
                        submitFormButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style
                        //scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                        return 1;
                    }
                };
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });
        */
    }

    /**
     * Cancels the form if the user decides they don't need to make a new node or edge
     * @param e the action of clicking the button
     */
    @FXML
    public void cancelFormButtonOp(ActionEvent e){

        mapBuilderController.addOverlay();
        mapBuilderController.updateMap();
    }

    /**
     * Submits the Node form, creating a node and then passing it to the NodeRepo
     * @param e
     */
    @FXML
    public void submitNodeFormButtonOp(ActionEvent e) {

        double x, y, xDisplay, yDisplay;

        // No need for validation, floor type is set to current floor by default
        Node.floorType floor = Node.stringToFloorType(floorComboBox.getValue().toString());

        // Validate that user has input a longName
        String longName;
        if (!(longNameTxt.getText() == null || longNameTxt.getText().trim().isEmpty())) {
            longName = longNameTxt.getText();
        } else {
            nodeFormErrorLabel.setText("Please enter a unique name for this node.");
            nodeFormErrorLabel.setVisible(true);
            return;
        }

        // Validate that user has chosen an option from the building combobox
        Node.buildingType building;
        if (!buildingComboBox.getSelectionModel().isEmpty()) {
            building = Node.stringToBuildingType(buildingComboBox.getValue().toString());
        } else {
            nodeFormErrorLabel.setText("Please choose a building from the dropdown.");
            nodeFormErrorLabel.setVisible(true);
            return;
        }

        // Validate that user has chosen an option from the node type combobox
        Node.nodeType type;
        if (!nodeTypeComboBox.getSelectionModel().isEmpty()) {
            type = Node.stringToNodeType(nodeTypeComboBox.getValue().toString());
        } else {
            nodeFormErrorLabel.setText("Please choose a node type from the dropdown.");
            nodeFormErrorLabel.setVisible(true);
            return;
        }

        Boolean isActive = isActiveNodeCheckBox.isSelected();

        // Validate correct number input
        try {
            x = Double.parseDouble(nodex2Txt.getText());
            y = Double.parseDouble(nodey2Txt.getText());
            xDisplay = Double.parseDouble(nodex3Txt.getText());
            yDisplay = Double.parseDouble(nodey3Txt.getText());
        } catch (NumberFormatException ne) {
            nodeFormErrorLabel.setText("Incompatible input for fields which require numbers.");
            nodeFormErrorLabel.setVisible(true);
            return;
        }

        // Validate coordinates are within our specified bounds
        if (!checkCoordinateBounds(x, y, xDisplay, yDisplay)) {
            nodeFormErrorLabel.setText("Coordinates entered are not within bounds.");
            nodeFormErrorLabel.setVisible(true);
            return;
        }

        // Validate connectingNode is selected and exists in the database if creating
        Node newNode = new Node(longName, isActive, x, y, xDisplay, yDisplay, floor, building, type);
        if (!editFlag) {
            try {
                connectingNode = db.getOneNode(getNodeID(connectingNodeTextBox.getText()));
            } catch (NodeNotFoundException nnfe) {
                nodeFormErrorLabel.setText("Please choose a valid node to connect to this node.");
                nodeFormErrorLabel.setVisible(true);
                System.out.println(nnfe.getNodeID());
                return;
            }
        }

        submitFormButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        mapBuilderController.formOverlayPane.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style

                        // After validation, create or modify node (unless the user has input a duplicate longName)
                        try {
                            if (editFlag) {
                                newNode.setID(editedNodeID);
                                db.modifyNode(newNode);
                                editedNodeID = null;
                            } else {
                                db.createNode(newNode, connectingNode);
                            }
                        } catch (NodeNotFoundException nnfe) {
                            nnfe.printStackTrace();
                        } catch (EdgeNotFoundException enfe) {
                            enfe.printStackTrace();
                        } catch (DuplicateLongNameException de) {
                            nodeFormErrorLabel.setText("Node " + de.getNodeID() + " already has this Long Name.");
                            nodeFormErrorLabel.setVisible(true);
                            mapBuilderController.formOverlayPane.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
                            return;
                        }

                        mapBuilderController.updateMap();
                        mapBuilderController.addOverlay();

                        mapBuilderController.formOverlayPane.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
                    }
                });
                return null;
            }
        };

        Thread th = new Thread(task);
        th.start();
    }

    /**
     * Checks the coordinates input for a new node against the bounds of the map
     * @param x 2D x-coordinate
     * @param y 2D y-coordinate
     * @param xDisplay 3D x-coordinate
     * @param yDisplay 3D y-coordinate
     * @return true if all coordinates are within bounds otherwise false
     */
    private Boolean checkCoordinateBounds(double x, double y, double xDisplay, double yDisplay) {
        if (x > 0 && x < FULL_WIDTH) {
            if (y > 0 && y < FULL_HEIGHT) {
                if (xDisplay > 0 && xDisplay < FULL_WIDTH) {
                    if (yDisplay > 0 && yDisplay < FULL_HEIGHT) {
                        return true;
                    } else {
                        System.out.println("3D Y-coordinate entered is not within bounds.");
                        return false;
                    }
                } else {
                    System.out.println("3D X-coordinate entered is not within bounds.");
                    return false;
                }
            } else {
                System.out.println("2D Y-coordinate entered is not within bounds.");
                return false;
            }
        } else {
            System.out.println("2D X-coordinate entered is not within bounds.");
            return false;
        }
    }

    /**
     * sets the 2Dx and 2Dy coordinate text fields
     * @param x2
     * @param y2
     */
    public void set2XYCoords(double x2, double y2, String floor){
        nodex2Txt.setText(Double.toString(x2));
        nodey2Txt.setText(Double.toString(y2));
        Coordinate coord = new Coordinate((int)Math.round(x2), (int)Math.round(y2), floor);
        nodex3Txt.setText(Double.toString(coord.getX3D()));
        nodey3Txt.setText(Double.toString(coord.getY3D()));
        nodeFormErrorLabel.setText("Your 3D coordinates have been changed!");
    }

    public void set3XYCoords(double x3, double y3){
        nodex3Txt.setText(Double.toString(x3));
        nodey3Txt.setText(Double.toString(y3));
    }

    public void setFloor(String floor){
        //TODO pick building based off of node coordinates
        floorComboBox.setValue(floor);
    }

    /**
     * deletes the selected node object
     */
    @FXML
    public void deleteButtonOp(){

        // Delete Node Popup
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

        // If user confirms deletion, delete the node
        if (deletePopUpController.getChoice()) {
            try {
                db.deleteNode(editedNodeID);
            } catch (NodeNotFoundException | EdgeNotFoundException ne) {
                ne.printStackTrace();
            } catch (OrphanNodeException oe) {
                nodeFormErrorLabel.setText("Deleting this node will orphan other nodes.");
                nodeFormErrorLabel.setVisible(true);
                return;
            }

            mapBuilderController.updateMap();
            mapBuilderController.addOverlay();
        }
    }

    public void setConnectingNodeTextBox(String nodeLongName){
        connectingNodeTextBox.setText(nodeLongName);
    }

    public String getNodeID(String nodeLongName){
        HashMap<String, Node> nodeSet = db.getAllNodes();
        for(Node node: nodeSet.values()){
            if (node.getLongName().equals(nodeLongName)){
                return node.getID();
            }
        }
        return null;
    }

    public double getNode2XCoord(){
        return Double.parseDouble(nodex2Txt.getText());
    }

    public double getNode2YCoord(){
        return Double.parseDouble(nodey2Txt.getText());
    }

    public double getNode3XCoord(){
        return Double.parseDouble(nodex3Txt.getText());
    }

    public double getNode3YCoord(){
        return Double.parseDouble(nodey3Txt.getText());
    }
}
