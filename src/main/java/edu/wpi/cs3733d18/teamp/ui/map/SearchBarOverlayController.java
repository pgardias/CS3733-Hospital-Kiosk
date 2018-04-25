package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Directions;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.ui.home.ShakeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SearchBarOverlayController implements Initializable{
    /*
    public static final int X_OFFSET = -523;
    public static final int Y_OFFSET = 0;
    public static final double X_SCALE = 1588.235294/5000.0;
    public static final double Y_SCALE = 1080.0/3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int WIDTH = 1380;
    public static final int HEIGHT = 776;
    private static final double STRAIGHT_BOUNDS = 15;
    private static final double SLIGHT_BOUNDS = 60;
    private static final double NORMAL_BOUNDS = 120;
    private static final double HARD_BOUNDS = 165;
    private static final double TURN_AROUND_BOUNDS = 180;

    Label startLabel = new Label();
    Label endLabel = new Label();*/

    // Camera for 3D
    PerspectiveCamera perspectiveCamera = new PerspectiveCamera();

    // Pathfinding variables
    private ArrayList<String> pathDirections; // The actual directions
    private Boolean directionsVisible = false; // Whether the box for directions are visible or not

    private Directions directionsList = new Directions();
    DBSystem db = DBSystem.getInstance();
    private Node endNode;

    @FXML
    Rectangle directionsRectangle;

    @FXML
    JFXTreeTableView<DirectionsTable> directionsTreeTableView;

    @FXML
    JFXTreeTableColumn<DirectionsTable, String> directionsTreeTableColumn;


    ObservableList<DirectionsTable> directions = FXCollections.observableArrayList();


    @FXML
    TreeItem<DirectionsTable> floorParent;

    @FXML
    TreeItem<DirectionsTable> floors;

    @FXML
    JFXButton directionsButton;

    @FXML
    JFXButton threeDButton;

    @FXML
    JFXButton emailButton;

    @FXML
    JFXButton phoneButton;

    @FXML
    JFXButton goButton;

    @FXML
    JFXTextField sourceSearchBar;

    @FXML
    JFXTextField destinationSearchBar;

    @FXML
    JFXToggleButton mapToggleButton;

    ArrayList<TreeItem<DirectionsTable>> floorChildren;
    ArrayList<TreeItem<DirectionsTable>> parents;

    private MapScreenController mapScreenController;
    private ThreeDMapScreenController threeDMapScreenController;
    private Boolean is3D = false;


    /**
     * sets up the search bars so they can be autofilled
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        setWordArrays();

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(destinationSearchBar, destinationWords);
        AutoCompletionBinding<String> sourceBinding = TextFields.bindAutoCompletion(sourceSearchBar, sourceWords);

        destBinding.setPrefWidth(destinationSearchBar.getPrefWidth());
        sourceBinding.setPrefWidth(sourceSearchBar.getPrefWidth());

        floorChildren = new ArrayList<>();
        parents = new ArrayList<>();
        floorParent = new TreeItem<>();
        floors = new TreeItem<>();


        directionsTreeTableColumn = new JFXTreeTableColumn<>("Directions");
        directionsTreeTableColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("directions"));
        directionsTreeTableColumn.setPrefWidth(340);
        directionsTreeTableColumn.setResizable(false);
        directionsTreeTableColumn.setSortable(false);
        directionsTreeTableColumn.getStyleClass().addAll("table-row-cell");
        directionsTreeTableView.getColumns().addAll(directionsTreeTableColumn);

    }

    public void startUp(MapScreenController mapScreenController){
        threeDButton.setText("3D");
        this.mapScreenController = mapScreenController;

        if (!mapScreenController.getPathDrawn()) {
            directionsTreeTableView.setVisible(false);
            directionsButton.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
            directionsRectangle.setVisible(false);
        }

        mapToggleButtonOp();
    }

    public void startUp3D(ThreeDMapScreenController threeDMapScreenController){
        threeDButton.setOpacity(0.0);
        threeDButton.setDisable(true);
        this.threeDMapScreenController = threeDMapScreenController;
        mapToggleButton.setVisible(false);
        if (!threeDMapScreenController.getPathDrawn()) {
            directionsTreeTableView.setVisible(false);
            directionsButton.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
            directionsRectangle.setVisible(false);
            is3D = true;
        }
    }

    public Boolean isSourceFocused(){
        return sourceSearchBar.isFocused();
    }

    public Boolean isDestinationFocused(){
        return destinationSearchBar.isFocused();
    }

    public void setSourceSearchBar(String startNode){
        sourceSearchBar.setText(startNode);
        hideDirections();
    }

    public void setDestinationSearchBar(String endNode){
        destinationSearchBar.setText(endNode);
        hideDirections();
    }

    ArrayList<String> destinationWords = new ArrayList<>();

    ArrayList<String> sourceWords = new ArrayList<>();

    Boolean toggledOn;
    @FXML
    public void mapToggleButtonOp(){
        if(mapToggleButton.isSelected())
            toggledOn = true;
        else
            toggledOn = false;
        mapScreenController.setToggleOn(toggledOn);
    }



    /**
     * sets up the word array both search bars will be using
     */
    private void setWordArrays() {
        HashMap<String, Node> nodeSet;

        nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getType() != Node.nodeType.HALL) {
                sourceWords.add(node.getLongName());
            }
        }
        destinationWords.addAll(sourceWords);
        destinationWords.add("NEAREST HALLWAY");
        destinationWords.add("NEAREST ELEVATOR");
        destinationWords.add("NEAREST RESTROOM");
        destinationWords.add("NEAREST STAIRCASE");
        destinationWords.add("NEAREST DEPARTMENT");
        destinationWords.add("NEAREST LAB");
        destinationWords.add("NEAREST INFO DESK");
        destinationWords.add("NEAREST CONFERENCE ROOM");
        destinationWords.add("NEAREST EXIT");
        destinationWords.add("NEAREST SHOP");
        destinationWords.add("NEAREST SERVICE STATION");
    }


    Boolean pathDrawn = false;
    /**
     * Handles the pathfinding operation given destination and source.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    @FXML
    public Boolean searchButtonOp(ActionEvent e) {
        if (pathDrawn && !is3D) {
            mapScreenController.resetPath();
            clearTable();
        }
        else if (pathDrawn) {
            threeDMapScreenController.resetPath();
        }
        //get all nodes
        HashMap<String, Node> nodeSet = db.getAllNodes();

        //Declare source node, destination node, and get the typed in inputs for both search boxes
        Node srcNode, dstNode;
        String src = sourceSearchBar.getText();
        String dst = destinationSearchBar.getText();

        // Check if the source node was input
        if (src.length() > 0 && !src.equals("Current Kiosk")) {
            // Source has been chosen by user, get Node entity from nodeID through NodeRepo
            srcNode = nodeSet.get(parseSourceInput(src).getID());
        } else {
            // Source is main.kiosk by default
            srcNode = nodeSet.get("PKIOS00102");
        }

        // Check if the destination node was input
        if (dst.length() > 0) {
            // Destination has been chosen by user, get Node entity from nodeID through NodeRepo
            destinationSearchBar.setUnFocusColor(Color.rgb(245,188,58));
            dstNode = nodeSet.get(parseDestinationInput(srcNode, dst).getID());
        } else {
            // Destination has not been set, set search bar to red and shake
            destinationSearchBar.setUnFocusColor(Color.rgb(255,0,0));
            ShakeTransition anim = new ShakeTransition(destinationSearchBar);
            anim.playFromStart();
            return false;
            //dstNode = nodeSet.get(endNode.getID());
        }

        Font font = new Font("verdana", 24.0);

        ArrayList<Node> path = Main.pathfindingContext.findPath(srcNode, dstNode);
        if (!is3D) {
            mapScreenController.drawPath(path);
        }
        else {
            threeDMapScreenController.drawPath(path);
        }

        directionsList.generateTextDirections(path);
        ArrayList<String> directions = directionsList.getDirections();
        pathDirections = directions;

        pathDrawn = true;
        directionsButton.setVisible(true);
        return true;
    }

    /**
     * Parses the input from the destination text box and returns the node that we wanted
     * @param string
     * @return the node corresponding to the input string
     */
    public Node parseSourceInput(String string) {
        Node aNode = new Node();

        HashMap<String, Node> nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getLongName().compareTo(string) == 0) {
                aNode = node;
            }
        }

        return aNode;
    }

    /**
     * Parses the input from the destination text box and returns the node that we wanted
     * @param srcNode
     * @param string
     * @return the node corresponding to the input string
     */
    public Node parseDestinationInput(Node srcNode, String string) {
        Node aNode = srcNode;


        switch (string) {
            case "NEAREST HALLWAY":
                aNode = getNearestOfType(srcNode, Node.nodeType.HALL);
                break;

            case "NEAREST ELEVATOR":
                aNode = getNearestOfType(srcNode, Node.nodeType.ELEV);
                break;

            case "NEAREST RESTROOM":
                aNode = getNearestOfType(srcNode, Node.nodeType.REST);
                break;

            case "NEAREST STAIRCASE":
                aNode = getNearestOfType(srcNode, Node.nodeType.STAI);
                break;

            case "NEAREST DEPARTMENT":
                aNode = getNearestOfType(srcNode, Node.nodeType.DEPT);
                break;

            case "NEAREST LAB":
                aNode = getNearestOfType(srcNode, Node.nodeType.LABS);
                break;

            case "NEAREST INFO DESK":
                aNode = getNearestOfType(srcNode, Node.nodeType.INFO);
                break;

            case "NEAREST CONFERENCE ROOM":
                aNode = getNearestOfType(srcNode, Node.nodeType.CONF);
                break;

            case "NEAREST EXIT":
                aNode = getNearestOfType(srcNode, Node.nodeType.EXIT);
                break;

            case "NEAREST SHOP":
                aNode = getNearestOfType(srcNode, Node.nodeType.RETL);
                break;

            case "NEAREST SERVICE STATION":
                aNode = getNearestOfType(srcNode, Node.nodeType.SERV);
                break;

            default:
                HashMap<String, Node> nodeSet = db.getAllNodes();

                for (Node node : nodeSet.values()) {
                    if (node.getLongName().compareTo(string) == 0) {
                        aNode = node;
                    }
                }
                break;
        }
        return aNode;
    }

    /**
     * Looks through entries in node database that math the type of input enum,
     * compares the distances to node,
     * returns the shortest one
     * @param srcNode
     * @param type
     * @return closestNode the closest node of a given type
     */
    private Node getNearestOfType(Node srcNode, Node.nodeType type) {
        HashMap<String, Node> nodeSet = db.getNodesOfType(type);

        Node shortestDistanceNode = srcNode;
        double distance = Double.POSITIVE_INFINITY;

        for (Node node : nodeSet.values()) {
            if (srcNode.distanceBetweenNodes(node) < distance && srcNode.getFloor() == node.getFloor()) {
                shortestDistanceNode = node;
                distance = srcNode.distanceBetweenNodes(node);
            }
        }

        return shortestDistanceNode;
    }

    /**
     * Creates a popup for inputting email information to send the map.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    @FXML
    public Boolean emailButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;
        EmailPopUpController emailPopUp;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/map/EmailPopUp.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        emailPopUp = loader.getController();
        emailPopUp.startUp(pathDirections);
        stage.setScene(new Scene(root, 360, 250));
        stage.setX(1500);
        stage.setY(130);
        stage.setTitle("Set Email");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(emailButton.getScene().getWindow());
        stage.show();
        return true;
    }

    /**
     * Create a popup for inputting phone information to text user the path.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    public Boolean phoneButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;
        PhonePopUpController phonePopUp;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/map/PhonePopUp.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        phonePopUp = loader.getController();
        phonePopUp.startUp(pathDirections);
        stage.setScene(new Scene(root, 360, 250));
        stage.setX(1500);
        stage.setY(130);
        stage.setTitle("Set Phone");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(phoneButton.getScene().getWindow());
        stage.show();
        return true;
    }

    /**
     * Displays the text directions on the map screen
     *
     */
    @FXML
    public Boolean directionsButtonOp(ActionEvent e) {
        directionsTreeTableColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("directions"));
        directionsTreeTableView.setTreeColumn(directionsTreeTableColumn);
        if (directionsVisible){
            clearTable();
            directionsTreeTableView.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
            directionsRectangle.setVisible(false);
            directionsButton.setText("Directions");
            directionsVisible = false;
        }
        else {
            emailButton.setVisible(true);
            phoneButton.setVisible(true);
            directionsRectangle.setVisible(true);
            directionsTreeTableView.setVisible(true);
            if (directionsList.getDirections() == null) {
                directions.add(new DirectionsTable("Path needs to be set first!"));
//                directionsTreeTableView.setPredicate(directions);
//                directionsTableViewColumn.setCellValueFactory(new PropertyValueFactory<>("directions"));
                directionsButton.setText("Directions X");
                directionsVisible = true;
            }
            else {
                if (!is3D) {
                    setParents(mapScreenController.getFloorsList());
                }
                else {
                    setParents(threeDMapScreenController.getFloorsList());
                }

                setChildren(directionsList.getDirections());

                directionsTreeTableView.setShowRoot(false);


                directionsButton.setText("Directions X");
                directionsVisible = true;
                if (!is3D) {
                    expandDirections(mapScreenController.getCurrentFloor());
                }
            }

        }

        return true;
    }

    /**
     * Switches map to 3D or back to 2D
     *
     */
    @FXML
    public void threeDButtonOp() {
        // If 3D, switch to 2D
        if (is3D) {
            FXMLLoader loader;
            Parent root;
            MapScreenController mapScreenController;

            loader = new FXMLLoader(getClass().getResource("/FXML/map/MapScreen.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return;
            }
            mapScreenController = loader.getController();
            mapScreenController.onStartUp();
            if (threeDMapScreenController.getPathDrawn()) {
                mapScreenController = loader.getController();
                mapScreenController.onStartUp3D(threeDMapScreenController.getPathDrawn(), threeDMapScreenController.getPathMade(), sourceSearchBar.getText(), destinationSearchBar.getText());
            } else {
                mapScreenController = loader.getController();
                mapScreenController.onNoPathStartup(sourceSearchBar.getText(), destinationSearchBar.getText());
            }
            threeDButton.getScene().setCamera(perspectiveCamera);
            threeDButton.getScene().setRoot(root);
        }
        // If 2D, switch to 3D
        else {
            FXMLLoader loader;
            Parent root;
            ThreeDMapScreenController threeDMapScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/map/ThreeDMap.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return;
            }
            if (mapScreenController.getPathDrawn()) {
                threeDMapScreenController = loader.getController();
                threeDMapScreenController.onStartUp(mapScreenController.getPathDrawn(), mapScreenController.getPathMade(), sourceSearchBar.getText(), destinationSearchBar.getText());
            } else {
                threeDMapScreenController = loader.getController();
                threeDMapScreenController.onNoPathStartup(sourceSearchBar.getText(), destinationSearchBar.getText());
            }
            threeDButton.getScene().setCamera(perspectiveCamera);
            threeDButton.getScene().setRoot(root);
        }
    }

    public void clearTable(){

        floorChildren.clear();
        parents.clear();
        floors.getChildren().removeAll(parents);
        directionsTreeTableView.setRoot(floors);
    }

    public void setSearchButtonFocus(){
        goButton.requestFocus();
    }

    public void setChildren(ArrayList<String> directions){

        int directionsIndex = 0;

        for(int i = 1; i < directionsTreeTableView.getRoot().getChildren().size(); i++){
            while(directionsIndex < directions.size() && !directions.get(directionsIndex).equals("Buffer")) {
                floorChildren.add(new TreeItem<>(new DirectionsTable(directions.get(directionsIndex))));
                directionsIndex++;
            }
            directionsIndex++;
            if(i == directionsTreeTableView.getRoot().getChildren().size() - 1){
                floorChildren.add(new TreeItem<>(new DirectionsTable("You have arrived at your destination!")));
            }
            directionsTreeTableView.getRoot().getChildren().get(i).getChildren().setAll(floorChildren);
            floorChildren.clear();
        }
        directionsTreeTableView.setRoot(floors);
    }

    public void expandDirections(Node.floorType floor){
        String level;
        String parentLevel;
        switch (floor){
            case LEVEL_1:
                level = "Floor 1";
                break;
            case LEVEL_3:
                level = "Floor 3";
                break;
            case LEVEL_G:
                level = "Floor G";
                break;
            case LEVEL_L1:
                level = "Floor L1";
                break;
            case LEVEL_L2:
                level = "Floor L2";
                break;
            default:
                level = "Floor 2";
                break;
        }

        for(int i = 1; i < directionsTreeTableView.getRoot().getChildren().size(); i++){
            parentLevel = directionsTreeTableView.getRoot().getChildren().get(i).getValue().getDirections().toString();
            if(parentLevel.equals(level)){
                directionsTreeTableView.getRoot().getChildren().get(i).setExpanded(true);
            } else {
                directionsTreeTableView.getRoot().getChildren().get(i).setExpanded(false);
            }
        }
    }
    public void hideDirections(){
        directionsTreeTableView.setVisible(false);
        directionsButton.setVisible(false);
        emailButton.setVisible(false);
        phoneButton.setVisible(false);
        directionsRectangle.setVisible(false);
        directionsVisible = false;
        directionsButton.setText("Directions");

    }

    public Boolean getDirectionsVisible() {
        return directionsVisible;
    }

    public void setDirectionsVisible(Boolean directionsVisible) {
        this.directionsVisible = directionsVisible;
    }

    public void setParents(ArrayList<Node.floorType> floorList){
        parents.add(new TreeItem<>(new DirectionsTable("Starting Route!")));
        if(floorList.size() == 0){
            parents.add(new TreeItem<>(new DirectionsTable("Floor " + mapScreenController.getCurrentFloor().toString())));
        }else {
            for (Node.floorType floor : floorList) {
                parents.add(new TreeItem<>(new DirectionsTable("Floor " + floor.toString())));
            }
        }


        floors.getChildren().setAll(parents);
        directionsTreeTableView.setRoot(floors);
    }
}


