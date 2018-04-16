package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class MapBuilderController implements Initializable {

    public int X_OFFSET = -523;
    public int Y_OFFSET = 0;
    public double X_SCALE = 1588.235294/5000.0;
    public double Y_SCALE = 1080.0/3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int IMG_WIDTH = 5000;
    public int IMG_HEIGHT = 3400;
    private static final double ZOOM_3D_MIN = 1.013878875;
    private static final double ZOOM_2D_MIN = 1.208888889;

    Circle newNodeCircle = new Circle();

    private Node startNode;
    private Node endNode;
    private Edge selectedEdge;
    private Node dragNodeOrg;
    //this is for creating new edges
    private Node firstSelect = null;
    private Node secondSelect = null;
    private Boolean firstChoice = true;

    private Boolean edgeSelected = false;
    private Boolean pathDrawn = false;
    private Boolean isNewNode = false;
    private Boolean toggleOn = false;

    private static HashMap<String, Circle> nodeDispSet = new HashMap<>();
    private static HashMap<String, Line> edgeDispSet = new HashMap<>();

    private String nodeID;
    private String dragNodeID;
    private String edgeID;

    ImageView kioskPip = new ImageView();

    DBSystem db = DBSystem.getInstance();

    Node.floorType currentFloor;

    @FXML
    JFXTextField sourceSearchBar;

    @FXML
    JFXTextField destinationSearchBar;

    @FXML
    JFXButton goButton;

    @FXML
    JFXToggleButton mapToggleButton;

    @FXML
    AnchorPane basePane;

    @FXML
    AnchorPane nodesEdgesPane;

    @FXML
    StackPane mapPane;

    @FXML
    ImageView mapImage;

    @FXML
    BorderPane formOverlayPane;

    @FXML
    BorderPane searchBarOverlayPane;

    @FXML
    JFXRadioButton aStarRadioButton;

    @FXML
    JFXRadioButton depthFirstRadioButton;

    @FXML
    JFXRadioButton breadthFirstRadioButton;

    @FXML
    Slider zoomSlider;

    @FXML
    Spinner<String> floorSpinner;

    ObservableList<String> floorsList = FXCollections.observableArrayList(
            Node.floorType.LEVEL_3.toString(),
            Node.floorType.LEVEL_2.toString(),
            Node.floorType.LEVEL_1.toString(),
            Node.floorType.LEVEL_G.toString(),
            Node.floorType.LEVEL_L1.toString(),
            Node.floorType.LEVEL_L2.toString()
    );

    SpinnerValueFactory<String> floors = new SpinnerValueFactory.ListSpinnerValueFactory<String>(floorsList);


    MapBuilderController mapBuilderController = null;
    MapBuilderOverlayController mapBuilderOverlayController = null;


    ArrayList<String> destinationWords = new ArrayList<>();

    ArrayList<String> sourceWords = new ArrayList<>();

    ArrayList<Node> path = new ArrayList<>();

    /**
     * sets up the word array both search bars will be using
     */
    private void setWordArrays() {
        HashMap<String, Node> nodeSet;

        nodeSet = db.getAllNodes();

        int i = 0;
        for (Node node : nodeSet.values()) {
            sourceWords.add(node.getShortName());
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
    }

    /**
     * intializes values such as
     *      mapBuilderController to this object
     *      sets the current floor
     *      initializes the zoom slider and binds it to the map
     *      draws the initial screen and sets mouse events
     */
    @FXML
    public void startUp(){
        mapBuilderController = this;
        floors.setValue("2");
        currentFloor = Node.floorType.LEVEL_2;
        floorSpinner.setValueFactory(floors);
        zoomSlider.setMin(ZOOM_2D_MIN);
        zoomSlider.setValue(ZOOM_2D_MIN);
        Image newImage = new Image("/img/maps/2d/02_thesecondfloor.png");
        mapImage.setImage(newImage);
        mapImage.scaleXProperty().bind(zoomSlider.valueProperty());
        mapImage.scaleYProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleXProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleYProperty().bind(zoomSlider.valueProperty());


        mapImage.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);
        getMap();
        drawEdges();
        drawNodes();
        nodesEdgesPane.getChildren().add(newNodeCircle);
        //newNodeCircle.addEventHandler(MouseEvent.ANY, nodeClickHandler);
        addOverlay();

    }


    @FXML
    void aStarSetOp(ActionEvent event) {
        Main.settings.setSettings(PathfindingContext.PathfindingSetting.AStar);
        Main.pathfindingContext.setPathfindingContext(Main.settings.getPathfindingSettings());
    }

    @FXML
    void breadthFirstSetOp(ActionEvent event) {
        Main.settings.setSettings(PathfindingContext.PathfindingSetting.BreadthFirst);
        Main.pathfindingContext.setPathfindingContext(Main.settings.getPathfindingSettings());
    }

    @FXML
    void depthFirstSetOp(ActionEvent event) {
        Main.settings.setSettings(PathfindingContext.PathfindingSetting.DepthFirst);
        Main.pathfindingContext.setPathfindingContext(Main.settings.getPathfindingSettings());
    }


    /**
     * switch statement that determines which map is loaded in
     */
    public void getMap(){
        Image image;
        floorState = floorSpinner.getValue().toString();
        if (toggleOn){
            switch(floorState) {
                case "3":
                    image = new Image("/img/maps/3d/3-ICONS.png");
                    break;
                case "2":
                    image = new Image("/img/maps/3d/2-ICONS.png");
                    break;
                case "1":
                    image = new Image("/img/maps/3d/1-ICONS.png");
                    break;
                case "G":
                    image = new Image("/img/maps/3d/1-ICONS.png");
                    break;
                case "L1":
                    image = new Image("/img/maps/3d/L1-ICONS.png");
                    break;
                default:
                    image = new Image("/img/maps/3d/L2-ICONS.png");
                    break;
            }
        } else {
            switch (floorState) {
                case "3":
                    image = new Image("/img/maps/2d/03_thethirdfloor.png");
                    break;
                case "2":
                    image = new Image("/img/maps/2d/02_thesecondfloor.png");
                    break;
                case "1":
                    image = new Image("/img/maps/2d/01_thefirstfloor.png");
                    break;
                case "G":
                    image = new Image("/img/maps/2d/00_thegroundfloor.png");
                    break;
                case "L1":
                    image = new Image("/img/maps/2d/00_thelowerlevel1.png");
                    break;
                default:
                    image = new Image("/img/maps/2d/00_thelowerlevel2.png");
                    break;
            }
        }
        mapImage.setImage(image);
    }

    /**
     * dont think i need this but its assigned to a button somewhere
     */
    @FXML
    public void updateMapSize(){
//        double xSize = zoomSlider.getValue() * 108;
//        double ySize = zoomSlider.getValue() * 192;
//        double yBar = mapPane.getVvalue();
//        double xBar = mapPane.getHvalue();
//
////        mapImage.setFitHeight(ySize);
////        mapImage.setFitWidth(xSize);
//        nodesEdgesPane.setScaleX(1/xSize);
//        nodesEdgesPane.setScaleY(1/ySize);

    }

    double orgSceneX = 0;
    double orgSceneY = 0;
    double orgTranslateX = 0;
    double orgTranslateY = 0;

    /**
     * this gets the current position of the mouse before using the mouse
     * to pan the map
     * @param t
     */
    @FXML
    public void getMouseValue(MouseEvent t){
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
        orgTranslateX = mapImage.getTranslateX();
        orgTranslateY = mapImage.getTranslateY();
    }


    /**
     * this lets the user scroll the wheel to move the zoom slider
     * This also readjusts the screen based on the bounds
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s){
        double newValue = (s.getDeltaY())/15 + zoomSlider.getValue();
        zoomSlider.setValue(newValue);

        double translateSlopeX = X_SCALE*mapImage.getScaleX()*IMG_WIDTH;
        double translateSlopeY = Y_SCALE*mapImage.getScaleX()*IMG_HEIGHT;

        if(newTranslateX > (translateSlopeX - 1920)/2)
            newTranslateX = (translateSlopeX - 1920)/2;
        if(newTranslateX < -(translateSlopeX - 1920)/2)
            newTranslateX = -(translateSlopeX - 1920)/2;
        if(newTranslateY > (translateSlopeY - 1080)/2)
            newTranslateY = (translateSlopeY - 1080)/2;
        if(newTranslateY < -(translateSlopeY - 1080)/2)
            newTranslateY = -(translateSlopeY - 1080)/2;
        mapImage.setTranslateX(newTranslateX);
        mapImage.setTranslateY(newTranslateY);
        nodesEdgesPane.setTranslateX(newTranslateX);
        nodesEdgesPane.setTranslateY(newTranslateY);
    }


    /**
     * Draws the nodes according to what was given back from the database
     */
    public void drawNodes() {
        HashMap<String, Node> nodeSet;

        nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            Circle circle = new Circle();
            circle.setRadius(NODE_RADIUS);
            if (node.getFloor() != currentFloor) {
               circle.setVisible(false);
               circle.setDisable(true);
               circle.setPickOnBounds(false);
            }
            nodesEdgesPane.getChildren().add(circle);
            if (!toggleOn) {
                circle.setCenterX((node.getX() - X_OFFSET) * X_SCALE);
                circle.setCenterY((node.getY() - Y_OFFSET) * Y_SCALE);
            } else {
                circle.setCenterX((node.getxDisplay() - X_OFFSET) * X_SCALE);
                circle.setCenterY((node.getyDisplay() - Y_OFFSET) * Y_SCALE);
            }
            //System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
            circle.setFill(Color.DODGERBLUE);
            circle.setStroke(Color.BLACK);
            circle.setStrokeType(StrokeType.INSIDE);
            if(!node.getActive()) {
                circle.setOpacity(0.5);
                circle.setFill(Color.GRAY);
            }
            circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);
//                circle.setOnMouseClicked(clickCallback());

            String label = node.getID();
            nodeDispSet.put(label, circle);

        }
        System.out.println("Printed All Nodes");
    }

    /**
     * Draws the edges according to what was given back from the database
     */
    public void drawEdges() {
        HashMap<String, Edge> edgeSet;


        edgeSet = db.getAllEdges();

        for (Edge edge : edgeSet.values()) {

            Line line = new Line();
            nodesEdgesPane.getChildren().add(line);
            if (!toggleOn) {
                line.setStartX((edge.getStart().getX() - X_OFFSET) * X_SCALE);
                line.setStartY((edge.getStart().getY() - Y_OFFSET) * Y_SCALE);
                line.setEndX((edge.getEnd().getX() - X_OFFSET) * X_SCALE);
                line.setEndY((edge.getEnd().getY() - Y_OFFSET) * Y_SCALE);
            } else {
                line.setStartX((edge.getStart().getxDisplay() - X_OFFSET) * X_SCALE);
                line.setStartY((edge.getStart().getyDisplay() - Y_OFFSET) * Y_SCALE);
                line.setEndX((edge.getEnd().getxDisplay() - X_OFFSET) * X_SCALE);
                line.setEndY((edge.getEnd().getyDisplay() - Y_OFFSET) * Y_SCALE);
            }

            line.setStrokeWidth(EDGE_WIDTH);
            line.setStrokeType(StrokeType.CENTERED);
            if(!edge.getActive()){
                line.getStrokeDashArray().addAll(5.0, 2.5);
                line.setOpacity(0.5);
            }
                line.setOnMouseClicked(edgeClickHandler);
            if (edge.getStart().getFloor() != currentFloor || edge.getEnd().getFloor() != currentFloor) {
                line.setVisible(false);
                line.setDisable(true);
                line.setPickOnBounds(false);
            }
            String label = edge.getID();
            edgeDispSet.put(label, line);
        }
    }

    /**
     * This handles all mouse events related to dragging around a cirlce
     * that is not yet a node but a node in development
     */
    EventHandler<MouseEvent> dragCircleHandler = new EventHandler<MouseEvent>() {
        double orgMouseX;
        double orgMouseY;
        double orgCenterX;
        double orgCenterY;
        double newCenterX;
        double newCenterY;
        @Override
        public void handle(MouseEvent event) {

            if(event.getEventType() == MouseEvent.MOUSE_PRESSED){
                orgMouseX = event.getSceneX();
                orgMouseY = event.getSceneY();
                orgCenterX = newNodeCircle.getCenterX();
                orgCenterY = newNodeCircle.getCenterY();
                //System.out.println("Original Mouse X: " + orgMouseX + " New Mouse Y: " + orgMouseY);
                //System.out.println("Original Center X: " + newCenterX + " New Original Y: " + newCenterX);
            }
            else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

                double offsetX = event.getSceneX() - orgMouseX;
                double offsetY = event.getSceneY() - orgMouseY;

                double scaledx2Offset = (offsetX)/nodesEdgesPane.getScaleX();
                double scaledy2Offset = (offsetY)/nodesEdgesPane.getScaleY();
                //System.out.println("New Scaled Offset X: " + scaledx2Offset + " New Scaled Offset Y: " + scaledy2Offset);

                newCenterX = orgCenterX + scaledx2Offset;
                newCenterY = orgCenterY + scaledy2Offset;

                double nodex2Coord = newCenterX/X_SCALE + X_OFFSET;
                double nodey2Coord = newCenterY/Y_SCALE + Y_OFFSET;
                if (!toggleOn) {
                    mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorSpinner.getValue());
                } else {
                    mapBuilderNodeFormController.set3XYCoords(nodex2Coord, nodey2Coord);
                }

                //System.out.println("New Center X: " + newCenterX + " New Center Y: " + newCenterY + "\n");
                //TODO drag bounds

                newNodeCircle.setCenterX(newCenterX);
                newNodeCircle.setCenterY(newCenterY);
            }

        }
    };

    int nodeState = 0;
    /**
     * this handles all mouse events related to nodes that exist in the database
     * and have been drawn
     */
    EventHandler<MouseEvent> nodeClickHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging = false;
        double orgMouseX;
        double orgMouseY;
        double orgCenterX;
        double orgCenterY;
        double newCenterX;
        double newCenterY;
        @Override
        public void handle(MouseEvent event) {
            HashMap<String, Node> dragNodeSet;
            dragNodeSet = db.getAllNodes();
            for (String string : nodeDispSet.keySet()) {
                if (nodeDispSet.get(string) == (Circle) event.getSource()) {
                    Node dragNode = dragNodeSet.get(string);
                    dragNodeOrg = dragNode;
                    dragNodeID = dragNode.getID();
                    break;
                }
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED){
                orgMouseX = event.getSceneX();
                orgMouseY = event.getSceneY();
                orgCenterX = nodeDispSet.get(dragNodeID).getCenterX();
                orgCenterY = nodeDispSet.get(dragNodeID).getCenterY();
                isDragging = false;
            }
            else if (event.getEventType() == MouseEvent.DRAG_DETECTED){
                isDragging = true;
            }
            else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                if(isDragging && !isNewNode){
                    ArrayList<Edge> dragEdges = dragNodeOrg.getEdges();

                    double offsetX = event.getSceneX() - orgMouseX;
                    double offsetY = event.getSceneY() - orgMouseY;

                    double scaledx2Offset = (offsetX)/nodesEdgesPane.getScaleX();
                    double scaledy2Offset = (offsetY)/nodesEdgesPane.getScaleY();

                    newCenterX = orgCenterX + scaledx2Offset;
                    newCenterY = orgCenterY + scaledy2Offset;
                    nodeDispSet.get(dragNodeOrg.getID()).setFill(Color.rgb(205, 35, 0, 0.99));
                    nodeDispSet.get(dragNodeID).setCenterX(newCenterX);
                    nodeDispSet.get(dragNodeID).setCenterY(newCenterY);

                    double nodex2Coord = newCenterX/X_SCALE + X_OFFSET;
                    double nodey2Coord = newCenterY/Y_SCALE + Y_OFFSET;

                    for (Edge edge: dragEdges){
                        if (edge.getStart() == dragNodeOrg){
                            edgeDispSet.get(edge.getID()).setStartX(newCenterX);
                            edgeDispSet.get(edge.getID()).setStartY(newCenterY);
                        } else{
                            edgeDispSet.get(edge.getID()).setEndX(newCenterX);
                            edgeDispSet.get(edge.getID()).setEndY(newCenterY);
                        }
                    }

                    newNodeForm(dragNodeOrg.getID(), dragNodeOrg.getLongName(), dragNodeOrg.getX(), dragNodeOrg.getY(),
                            dragNodeOrg.getxDisplay(), dragNodeOrg.getyDisplay(), dragNodeOrg.getFloor().toString(),
                            dragNodeOrg.getBuilding().toString(), dragNodeOrg.getType().toString(), dragNodeOrg.getActive());
                    if (!toggleOn) {
                        mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorSpinner.getValue());
                    } else {
                        mapBuilderNodeFormController.set3XYCoords(nodex2Coord, nodey2Coord);
                    }

                }
            }
            else if(event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                if(!isDragging) {
                    HashMap<String, Node> nodeSet;
                    nodeSet = db.getAllNodes();
                    if (getSourceFocus()){
                        // if the start search bar is clicked the node gets put in there
                        removeFocus();
                        clearCircles();
                        System.out.println("clear all nodes");
                        for (String string : nodeDispSet.keySet()) {
                            if (nodeDispSet.get(string) == event.getSource()) {
                                Node node = nodeSet.get(string);
                                nodeDispSet.get(node.getID()).setFill(Color.GREEN);
                                sourceSearchBar.setText(node.getShortName());
                            }
                        }
                    }else if (getDestinationFocus()){
                        // if the destination bar is clicked, the clicked node gets put in there
                        removeFocus();
                        clearCircles();
                        System.out.println("clear all nodes");
                        for (String string : nodeDispSet.keySet()) {
                            if (nodeDispSet.get(string) == event.getSource()) {
                                Node node = nodeSet.get(string);
                                nodeDispSet.get(node.getID()).setFill(Color.RED);
                                destinationSearchBar.setText(node.getShortName());
                            }
                        }
                    } else if (isNewNode){
                        for (String string : nodeDispSet.keySet()) {
                            if (nodeDispSet.get(string) == event.getSource()) {
                                Node node = nodeSet.get(string);
                                nodeDispSet.get(node.getID()).setFill(Color.GREEN);
                                mapBuilderNodeFormController.setConnectingNodeTextBox(node.getLongName());
                            }
                        }
                    } else if (!edgeSelected) {
                        // if an edge was not selected, clicking a node opens up the modify node form
                        clearCircles();
                        System.out.println("clear all nodes");
                        for (String string : nodeDispSet.keySet()) {
                            if (nodeDispSet.get(string) == event.getSource()) {
                                Node node = nodeSet.get(string);
                                endNode = node;
                                nodeDispSet.get(node.getID()).setFill(Color.rgb(205, 35, 0, 0.99));
                                nodeID = endNode.getShortName();


                                System.out.println(endNode.getID());
                                newNodeForm(endNode.getID(), endNode.getLongName(), endNode.getX(), endNode.getY(),
                                        endNode.getxDisplay(), endNode.getyDisplay(), endNode.getFloor().toString(),
                                        endNode.getBuilding().toString(), endNode.getType().toString(), endNode.getActive());
                            }
                        }
                    } else {
                        //This modifies the edge by replacing the start and end node in the edge
                        //when clicking on new nodes
                        if (mapBuilderEdgeFormController.checkEndNodeBar()) nodeState = 1;
                        if (mapBuilderEdgeFormController.checkStartNodeBar()) nodeState = 0;
                        switch (nodeState) {
                            case 0:
                                if (firstSelect != null) {
                                    nodeDispSet.get(firstSelect.getID()).setFill(Color.DODGERBLUE);
                                }
                                for (String string : nodeDispSet.keySet()) {
                                    if (nodeDispSet.get(string) == event.getSource()) {
                                        Node node = nodeSet.get(string);
                                        firstSelect = node;
                                        nodeDispSet.get(node.getID()).setFill(Color.rgb(205, 35, 0, 0.99));
                                        nodeID = firstSelect.getShortName();

                                        mapBuilderEdgeFormController.setStartNodeTxt(firstSelect.getID());
                                        System.out.println("First Node: " + firstSelect.getShortName());
                                        firstChoice = false;
                                        break;
                                    }
                                }
                                nodeState = 1;
                                break;
                            case 1:
                                if (secondSelect != null) {
                                    nodeDispSet.get(secondSelect.getID()).setFill(Color.DODGERBLUE);
                                }
                                for (String string : nodeDispSet.keySet()) {
                                    if (nodeDispSet.get(string) == event.getSource()) {
                                        Node node = nodeSet.get(string);
                                        secondSelect = node;
                                        nodeDispSet.get(node.getID()).setFill(Color.rgb(100, 215, 0, 0.99));
                                        nodeID = secondSelect.getShortName();

                                        mapBuilderEdgeFormController.setEndNodeTxt(secondSelect.getID());
                                        System.out.println("Second Node: " + secondSelect.getShortName());
                                        firstChoice = true;
                                        break;
                                    }
                                }
                                nodeState = 0;
                                break;
                        }
                    }
                }
            }
            event.consume();
        }
    };

    String floorState;

    /**
     * this just updates the map if the spinner is clicked
     */
    @FXML
    public void spinnerOp(){
        floorState = floorSpinner.getValue();
        switch (floorState) {
            case "3":
                currentFloor = Node.floorType.LEVEL_3;
                break;
            case "2":
                currentFloor = Node.floorType.LEVEL_2;
                break;
            case "1":
                currentFloor = Node.floorType.LEVEL_1;
                break;
            case "G":
                currentFloor = Node.floorType.LEVEL_G;
                break;
            case "L1":
                currentFloor = Node.floorType.LEVEL_L1;
                break;
            case "L2":
                currentFloor = Node.floorType.LEVEL_L2;
                break;
        }
        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }

    /**
     * this is the event handler for clicking on edges
     */
    EventHandler<MouseEvent> edgeClickHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            removeFocus();
            Parent root;
            Stage stage;
            FXMLLoader loader;
            MapBuilderEdgeFormController mapBuilderEdgeFormController;

            HashMap<String, Edge> edgeSet;
            edgeSet = db.getAllEdges();
            clearCircles();
            System.out.println("cleared all edges");
            for (String string : edgeDispSet.keySet()) {
                if (edgeDispSet.get(string) == event.getSource()) {
                    Edge edge = edgeSet.get(string);
                    selectedEdge = edge;

                    edgeDispSet.get(edge.getID()).setStroke(Color.rgb(205,35, 0, 0.99));

                    edgeID = selectedEdge.getID();


                    System.out.println(selectedEdge.getID());
                    newEdgeForm(selectedEdge.getID(), selectedEdge.getStart(), selectedEdge.getEnd(),
                            selectedEdge.getActive());
                }
            }
            event.consume();
        }
    };

    double newTranslateX = 0;
    double newTranslateY = 0;
    /**
     * this is the event handler for clicking on the map to create a node
     */
    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;
        @Override
        public void handle(MouseEvent event) {
            if(event.getEventType() == MouseEvent.MOUSE_PRESSED){
                System.out.println("Mouse_Pressed");
                isDragging = false;
            }
            else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                System.out.println("Drag_Detected");
                isDragging = true;
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                System.out.println("Mouse_Dragged");
                double offsetX = event.getSceneX() - orgSceneX;
                double offsetY = event.getSceneY() - orgSceneY;
                newTranslateX = orgTranslateX + offsetX;
                newTranslateY = orgTranslateY + offsetY;

                double translateSlopeX = X_SCALE*mapImage.getScaleX()*IMG_WIDTH;
                double translateSlopeY = Y_SCALE*mapImage.getScaleX()*IMG_HEIGHT;

                if(newTranslateX > (translateSlopeX - 1920)/2)
                    newTranslateX = (translateSlopeX - 1920)/2;
                if(newTranslateX < -(translateSlopeX - 1920)/2)
                    newTranslateX = -(translateSlopeX - 1920)/2;
                if(newTranslateY > (translateSlopeY - 1080)/2)
                    newTranslateY = (translateSlopeY - 1080)/2;
                if(newTranslateY < -(translateSlopeY - 1080)/2)
                    newTranslateY = -(translateSlopeY - 1080)/2;
                mapImage.setTranslateX(newTranslateX);
                mapImage.setTranslateY(newTranslateY);
                nodesEdgesPane.setTranslateX(newTranslateX);
                nodesEdgesPane.setTranslateY(newTranslateY);
            }
            else if (event.getEventType() == MouseEvent.MOUSE_CLICKED){
                System.out.println("ready to click");
                if(!isDragging) {
                    System.out.println("Mouse Clicked");
                    clearCircles();
                    double x2Coord = event.getSceneX();
                    double y2Coord = event.getSceneY();
                    double scaledx2Coord = (x2Coord - 960)/nodesEdgesPane.getScaleX() + (960 - newTranslateX/nodesEdgesPane.getScaleX());
                    double scaledy2Coord = (y2Coord - 540)/nodesEdgesPane.getScaleY() + (540 - newTranslateY/nodesEdgesPane.getScaleY());
                    System.out.println("x: " + x2Coord + " y: " + y2Coord);
                    newNodeCircle.setCenterX(scaledx2Coord);
                    newNodeCircle.setCenterY(scaledy2Coord);
                    newNodeCircle.setRadius(NODE_RADIUS);
                    newNodeCircle.setFill(Color.RED);
                    newNodeCircle.setStroke(Color.BLACK);
                    newNodeCircle.setStrokeType(StrokeType.INSIDE);
                    newNodeCircle.addEventHandler(MouseEvent.ANY, dragCircleHandler);

                    double nodex2Coord = scaledx2Coord/X_SCALE + X_OFFSET;
                    double nodey2Coord = scaledy2Coord/Y_SCALE + Y_OFFSET;
                    if (!isNewNode) {
                        isNewNode = true;
                        newNodeForm();
                        mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorSpinner.getValue());
                        mapBuilderNodeFormController.setFloor(currentFloor.toString());
                    } else {
                        mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorSpinner.getValue());
                    }

                }
            }
        }
    };

    /**
     * this brings the basic leftbar overlays back in and resets some values
     */
    public void addOverlay(){
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/MpaBuilderOverlay.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie){
            ie.printStackTrace();
            return;
        }
        clearCircles();
        firstSelect = null;
        secondSelect = null;
        mapBuilderOverlayController = loader.getController();
        mapBuilderOverlayController.startUp(mapBuilderController);
        edgeSelected = false;
        isNewNode = false;
        nodeState = 0;
        formOverlayPane.setLeft(root);
    }

    MapBuilderNodeFormController mapBuilderNodeFormController;

    /**
     * brings in a fresh new node form to create a new node
     */
    public void newNodeForm(){
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/NodeForm.fxml"));

        try{
            root = loader.load();
        } catch (IOException ie){
            ie.printStackTrace();
            return;
        }
        mapBuilderNodeFormController = loader.getController();
        mapBuilderNodeFormController.startUp(mapBuilderController);
        formOverlayPane.setLeft(root);
    }

    /**
     * brings in a filled out node form for an existing node to be edited
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
    public void newNodeForm( String nodeID, String nodeLongName,
                             double x2d, double y2d, double x3d, double y3d, String nodeFloor, String nodeBuilding, String nodeType,
                             Boolean isActive){
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/NodeForm.fxml"));

        try{
            root = loader.load();
        } catch (IOException ie){
            ie.printStackTrace();
            return;
        }
        mapBuilderNodeFormController = loader.getController();
        mapBuilderNodeFormController.startUp(mapBuilderController, nodeID, nodeLongName, x2d, y2d, x3d, y3d, nodeFloor,
                nodeBuilding, nodeType, isActive);
        formOverlayPane.setLeft(root);
    }

    MapBuilderEdgeFormController mapBuilderEdgeFormController;

    /**
     * brings in a blank edge form to be filled in to create a new edge
     */
    public void newEdgeForm(){
        //TODO for edges
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/EdgeForm.fxml"));

        try{
            root = loader.load();
        } catch (IOException ie){
            ie.printStackTrace();
            return;
        }
        edgeSelected = true;
        nodeState = 0;
        mapBuilderEdgeFormController = loader.getController();
        mapBuilderEdgeFormController.startUp(mapBuilderController);
        formOverlayPane.setLeft(root);
    }

    /**
     * loads a filled out edge form to be able to modify an edge
     * @param edgeID
     * @param startNode
     * @param endNode
     * @param isActive
     */
    public void newEdgeForm(String edgeID, Node startNode,
                            Node endNode, Boolean isActive){
        //TODO for edges
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/EdgeForm.fxml"));

        try{
            root = loader.load();
        } catch (IOException ie){
            ie.printStackTrace();
            return;
        }
        edgeSelected = true;
        nodeState = 0;
        mapBuilderEdgeFormController = loader.getController();
        mapBuilderEdgeFormController.startUp(mapBuilderController, edgeID, startNode,
                endNode, isActive);
        formOverlayPane.setLeft(root);
    }

    /**
     * this clears the anchor pane, draws the edges and the nodes and loads in the new map
     * this also brings in the basic overlays
     */
    public void updateMap(){
        nodeDispSet.clear();
        endNode = null;
        firstSelect = null;
        secondSelect = null;
        edgeDispSet.clear();
        selectedEdge = null;
        nodesEdgesPane.getChildren().clear();
        getMap();
        drawEdges();
        drawNodes();
        nodesEdgesPane.getChildren().add(newNodeCircle);
        //addOverlay();
    }

    /**
     * resets any nodes or edges clicked on the anchor pane
     */
    public void clearCircles(){
        if(pathDrawn) resetPath();
        if (endNode != null) {
            nodeDispSet.get(endNode.getID()).setFill(Color.DODGERBLUE);
        }
        if (firstSelect != null) {
            nodeDispSet.get(firstSelect.getID()).setFill(Color.DODGERBLUE);
        }
        if (secondSelect != null) {
            nodeDispSet.get(secondSelect.getID()).setFill(Color.DODGERBLUE);
        }
        if (selectedEdge != null) {
            edgeDispSet.get(selectedEdge.getID()).setStroke(Color.BLACK);
        }
        if (newNodeCircle != null){
            newNodeCircle.setRadius(0);
            newNodeCircle.setStroke(null);
        }
    }

    /**
     * Handles the pathfinding operation given destination and source.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    public Boolean searchButtonOp(ActionEvent e) {
        addOverlay();
        if (pathDrawn) {
            resetPath();
        }
        System.out.println("get path");
        //get all nodes
        HashMap<String, Node> nodeSet = db.getAllNodes();

        //Declare source node, destination node, and get the typed in inputs for both search boxes
        Node srcNode, dstNode;
        String src = sourceSearchBar.getText();
        String dst = destinationSearchBar.getText();

//        System.out.println("test, " + src);

        // Check if the source node was input
        if (src.length() > 0 && !src.equals("Current Kiosk")) {
            // Source has been chosen by user, get Node entity from nodeID through NodeRepo
//            System.out.println("Something typed in! " + src);
            srcNode = nodeSet.get(parseSourceInput(src).getID());
//            System.out.println("Source node: " + srcNode);
        } else {
            // Source is main.kiosk
            srcNode = nodeSet.get("PKIOS00102");
//            System.out.println("Nothing typed in! " + srcNode + " " + srcNode.getEdges());
        }

        // Check if the destination node was input
        if (dst.length() > 0) {
            dstNode = nodeSet.get(parseDestinationInput(srcNode, dst).getID());
//            System.out.println("Something typed into destination! " + dst);
        } else {
            dstNode = nodeSet.get(endNode.getID());
//            System.out.println("Nothing typed in! " + dstNode);
        }
//        System.out.println("destination: " + dstNode);

        Font font = new Font("verdana", 24.0);
//        System.out.println("Source Node ID: "+srcNode.getID());

//        startLabel.setLayoutX((srcNode.getxDisplay()+5- X_OFFSET)*X_SCALE);
//        startLabel.setLayoutY((srcNode.getyDisplay()-40- Y_OFFSET)*Y_SCALE);
//        startLabel.setText(srcNode.getShortName());
//        startLabel.setFont(font);
//
//        startLabel.toFront();
//
//        endLabel.setLayoutX((dstNode.getxDisplay()+5- X_OFFSET)*X_SCALE);
//        endLabel.setLayoutY((dstNode.getyDisplay()-34- Y_OFFSET)*Y_SCALE);
//        endLabel.setText(dstNode.getShortName());
//        endLabel.setFont(font);

        ArrayList<Node> path = Main.pathfindingContext.findPath(srcNode, dstNode);
        System.out.println(path);
        drawPath(path);
        this.path = path;
        return true;
    }

    /**
     * Parses the input from the destination text box and returns the node that we wanted
     * @param string
     * @return the node corresponding to the input string
     */
    public Node parseSourceInput(String string) {
        Node aNode = new Node();
//        System.out.println("Input string: " + string);

        HashMap<String, Node> nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getShortName().compareTo(string) == 0) {
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
//        System.out.println("Input string: " + string);
//        System.out.println("source node:" + srcNode);

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
                    if (node.getShortName().compareTo(string) == 0) {
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

//        System.out.println(srcNode);
        Node shortestDistanceNode = srcNode;
        double distance = Double.POSITIVE_INFINITY;

        for (Node node : nodeSet.values()) {
//            System.out.println(node.getID());
//            System.out.println(srcNode.getID());
            if (srcNode.distanceBetweenNodes(node) < distance) {
                shortestDistanceNode = node;
                distance = srcNode.distanceBetweenNodes(node);
//                System.out.println("distance: " + srcNode.distanceBetweenNodes(node));
            }
        }

        return shortestDistanceNode;
    }


    ArrayList<Node> pathMade;
    /**
     * Used to draw the list of nodes returned by AStar
     * @param path List of Nodes to be drawn
     */
    //removed static hope it didn't break anything
    public void drawPath(ArrayList<Node> path) {
        Node currentNode = null, pastNode = null;

        if(pathMade != null){
            resetPath();
        }
        this.pathMade = path;

        double maxXCoord = 0;
        double maxYCoord = 0;
        double minXCoord = 5000;
        double minYCoord = 3400;

        for (Node n : path) {

            if (toggleOn) {
                if (n.getxDisplay() < minXCoord) minXCoord = n.getxDisplay();
                if (n.getxDisplay() > maxXCoord) maxXCoord = n.getxDisplay();
                if (n.getyDisplay() < minYCoord) minYCoord = n.getyDisplay();
                if (n.getyDisplay() > maxYCoord) maxYCoord = n.getyDisplay();
            } else {
                if (n.getX() < minXCoord) minXCoord = n.getX();
                if (n.getX() > maxXCoord) maxXCoord = n.getX();
                if (n.getY() < minYCoord) minYCoord = n.getY();
                if (n.getY() > maxYCoord) maxYCoord = n.getY();
            }

            pastNode = currentNode;
            currentNode = n;
            //nodeDispSet.get(currentNode.getID()).setFill(Color.rgb(250, 150, 0));
            if (path.get(0).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.GREEN);
            }
            if (path.get(path.size()-1).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.RED);
            }
            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        edgeDispSet.get(e.getID()).setStroke(Color.rgb(250, 150, 0));
                        edgeDispSet.get(e.getID()).setStrokeWidth(5.0);
                        edgeDispSet.get(e.getID()).setVisible(true);
                        if(e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor){
                            edgeDispSet.get(e.getID()).setOpacity(1.0);
                        } else {
                            edgeDispSet.get(e.getID()).setOpacity(0.3);
                        }

                    }
                }
            }
        }
        minXCoord -= 200;
        minYCoord -= 400;
        maxXCoord += 200;
        maxYCoord += 100;

        System.out.println("MaxX: " + maxXCoord + "Min X: " + minXCoord
                + "Max Y: " + maxYCoord + "Min Y: " + minYCoord);
        double rangeX = maxXCoord - minXCoord;
        double rangeY = maxYCoord - minYCoord;


        double desiredZoomX = 1920/(rangeX * X_SCALE);
        double desiredZoomY = 1080/(rangeY * Y_SCALE);
        System.out.println("desired X zoom: " + desiredZoomX +  " desired Zoom Y: " + desiredZoomY);

        double centerX = (maxXCoord + minXCoord)/2;
        double centerY = (maxYCoord + minYCoord)/2;

        autoTranslateZoom(desiredZoomX, desiredZoomY, centerX, centerY);

        pathDrawn = true;
    }

    /**
     * Used to draw the list of nodes returned by AStar
     */
    public void resetPath() {
        Node currentNode = null, pastNode = null;
        Circle waypoint;
        Line line;
        for (Node n : pathMade) {
            pastNode = currentNode;
            currentNode = n;
            nodeDispSet.get(n.getID()).setFill(Color.DODGERBLUE);

            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        edgeDispSet.get(e.getID()).setStroke(Color.BLACK);
                        edgeDispSet.get(e.getID()).setStrokeWidth(EDGE_WIDTH);
                        if(e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor){

                        } else{
                            edgeDispSet.get(e.getID()).setVisible(false);
                        }
                    }
                }
            }
        }
//        path = new ArrayList<>();
    }

    @FXML
    public void mapToggleButtonOp(){
        if (mapToggleButton.isSelected()){
            X_OFFSET = 0;
            Y_OFFSET = -19;
            X_SCALE = 1920.0/5000.0;
            Y_SCALE = 1065.216/2774.0;
            IMG_HEIGHT = 2774;
            zoomSlider.setMin(ZOOM_3D_MIN);
            zoomSlider.setValue(ZOOM_3D_MIN);
            toggleOn = true;
        } else {
            X_OFFSET = -523;
            Y_OFFSET = 0;
            X_SCALE = 1588.235294/5000.0;
            Y_SCALE = 1080.0/3400.0;
            IMG_HEIGHT = 3400;
            zoomSlider.setMin(ZOOM_2D_MIN);
            zoomSlider.setValue(ZOOM_2D_MIN);
            toggleOn = false;
        }
        updateMap();
    }

    public Boolean getSourceFocus(){
        return sourceSearchBar.isFocused();
    }

    public Boolean getDestinationFocus(){
        return destinationSearchBar.isFocused();
    }

    public void removeFocus(){
        goButton.requestFocus();
    }


    public void autoTranslateZoom(double zoomX, double zoomY, double centerX, double centerY){

        double zoom;
        if (zoomX > zoomY)
            zoom = zoomY;
        else
            zoom = zoomX;

        if (zoom > zoomSlider.getMax()) zoom = zoomSlider.getMax();
        if (zoom < zoomSlider.getMin()) zoom = zoomSlider.getMin();

        System.out.println("chosen zoom: " + zoom);

        zoomSlider.setValue(zoom);

        System.out.println("Center X: " + centerX + " Center Y: " + centerY);
        double screenX = (centerX - X_OFFSET)*X_SCALE;
        double screenY = (centerY - Y_OFFSET)*Y_SCALE;
        System.out.println("Screen x: " + screenX + " Screen Y: " + screenY);

        double translateX = 960 - screenX;
        double translateY = 540 - screenY;
        double screenTranslateX = (translateX * zoom);
        double screenTranslateY = (translateY * zoom);
        System.out.println("translate X: " + translateX + " translate Y: " + translateY);

        double translateSlopeX = X_SCALE*mapImage.getScaleX()*IMG_WIDTH;
        double translateSlopeY = Y_SCALE*mapImage.getScaleX()*IMG_HEIGHT;
        if(screenTranslateX > (translateSlopeX - 1920)/2)
            screenTranslateX = (translateSlopeX - 1920)/2;
        if(screenTranslateX < -(translateSlopeX - 1920)/2)
            screenTranslateX = -(translateSlopeX - 1920)/2;
        if(screenTranslateY > (translateSlopeY - 1080)/2)
            screenTranslateY = (translateSlopeY - 1080)/2;
        if(screenTranslateY < -(translateSlopeY - 1080)/2)
            screenTranslateY = -(translateSlopeY - 1080)/2;

        System.out.println("Chosen translate X: " + screenTranslateX + " Chosen translate Y: " + screenTranslateY);
        mapImage.setTranslateX(screenTranslateX);
        mapImage.setTranslateY(screenTranslateY);
        nodesEdgesPane.setTranslateX(screenTranslateX);
        nodesEdgesPane.setTranslateY(screenTranslateY);
    }
}

