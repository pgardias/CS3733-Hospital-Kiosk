package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.JFXButton;
import com.sun.scenario.effect.Effect;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.math.*;

public class MapScreenController {

    private int X_OFFSET = -523;
    private int Y_OFFSET = 0;
    private double X_SCALE = 1588.235294/5000.0;
    private double Y_SCALE = 1080.0/3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int WIDTH = 1380;
    public static final int HEIGHT = 776;


    private Node startNode;
    private Node endNode;
    private Edge selectedEdge;

    //this is for creating new edges
    private Node firstSelect = null;
    private Node secondSelect = null;
    private Boolean firstChoice = true;

    private Boolean pathDrawn = false;
    private Boolean toggleOn = false;

    private static HashMap<String, Circle> nodeDispSet = new HashMap<>();
    private static ArrayList<Polygon> arrowDispSet = new ArrayList<Polygon>();
    private static HashMap<String, Line> edgeDispSet = new HashMap<>();


    DBSystem db = DBSystem.getInstance();

    Node.floorType currentFloor;
    String floorState;

    @FXML
    BorderPane buttonOverlayPane;

    @FXML
    BorderPane searchBarOverlayPane;

    @FXML
    JFXButton backButton;

    @FXML
    Slider zoomSlider;

    @FXML
    StackPane mapPane;

    @FXML
    ImageView mapImage;

    @FXML
    AnchorPane nodesEdgesPane;

    @FXML
    JFXButton floorL2Button;

    @FXML
    JFXButton floorL1Button;


    @FXML
    JFXButton floorGButton;


    @FXML
    JFXButton floor1Button;


    @FXML
    JFXButton floor2Button;


    @FXML
    JFXButton floor3Button;



    SearchBarOverlayController searchBarOverlayController = null;
    MapScreenController mapScreenController;

    /**
     * intializes values such as
     *      adminMapViewController to this object
     *      sets the current floor
     *      initializes the zoom slider and binds it to the map
     *      draws the initial screen and sets mouse events
     */
    @FXML
    public void onStartUp() {
        mapScreenController = this;
        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;
        zoomSlider.setValue(1);
        Image newImage = new Image("/img/maps/2d/02_thesecondfloor.png");
        mapImage.setImage(newImage);
        mapImage.scaleXProperty().bind(zoomSlider.valueProperty());
        mapImage.scaleYProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleXProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleYProperty().bind(zoomSlider.valueProperty());

        mapImage.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);
        drawEdges();
        drawNodes();
        getMap();
        addOverlay();

    }

    @FXML
    public void backButtonOp() {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = (Stage) backButton.getScene().getWindow();
        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }

        backButton.getScene().setRoot(root);
    }


    @FXML
    public void floorL2ButtonOp(ActionEvent e){
        floorState = floorL2Button.getText();
        currentFloor = Node.floorType.LEVEL_L2;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }

    @FXML
    public void floorL1ButtonOp(ActionEvent e){
        floorState = floorL1Button.getText();
        currentFloor = Node.floorType.LEVEL_L1;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }

    @FXML
    public void floorGButtonOp(ActionEvent e){
        floorState = floorGButton.getText();
        currentFloor = Node.floorType.LEVEL_G;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor1ButtonOp(ActionEvent e){
        floorState = floor1Button.getText();
        currentFloor = Node.floorType.LEVEL_1;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor2ButtonOp(ActionEvent e){
        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor3ButtonOp(ActionEvent e){
        floorState = floor3Button.getText();
        currentFloor = Node.floorType.LEVEL_3;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }
    }


    /**
     * switch statement that determines which map is loaded in
     */
    public void getMap(){
        Image image;

        if (toggleOn){
            X_OFFSET = 0;
            Y_OFFSET = -19;
            X_SCALE = 1920.0/5000.0;
            Y_SCALE = 1065.216/2774.0;
            switch(floorState) {
                case "3":
                    image = new Image("/img/maps/3d/3-ICONS.png"); //TODO use this bit of information for image drawing
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
            X_OFFSET = -523;
            Y_OFFSET = 0;
            X_SCALE = 1588.235294/5000.0;
            Y_SCALE = 1080.0/3400.0;
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


    double newTranslateX = 0;
    double newTranslateY = 0;
    /**
     * this lets the user scroll the wheel to move the zoom slider
     * This also readjusts the screen based on the bounds
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s){
        double newValue = (s.getDeltaY())/15 + zoomSlider.getValue();
        zoomSlider.setValue(newValue);

        if(newTranslateX > (X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880))
            newTranslateX = X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880;
        if(newTranslateX < -(X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880))
            newTranslateX = -(X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880);
        if(newTranslateY > (Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530))
            newTranslateY = (Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530);
        if(newTranslateY < -(Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530))
            newTranslateY = -(Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530);
        mapImage.setTranslateX(newTranslateX);
        mapImage.setTranslateY(newTranslateY);
        nodesEdgesPane.setTranslateX(newTranslateX);
        nodesEdgesPane.setTranslateY(newTranslateY);
    }


    /**
     * Draws the nodes according to what was given back from the database
     */
    public void drawNodes() {
        if (!toggleOn) {
            HashMap<String, Node> nodeSet;

            nodeSet = db.getAllNodes();
            System.out.println("drawing nodes");
            for (Node node : nodeSet.values()) {
                if (node.getFloor() == currentFloor && node.getType() != Node.nodeType.HALL) {
                    Circle circle = new Circle(NODE_RADIUS);
                    nodesEdgesPane.getChildren().add(circle);
                    circle.setCenterX((node.getX() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((node.getY() - Y_OFFSET) * Y_SCALE);
                    System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
                    circle.setFill(Color.DODGERBLUE);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeType(StrokeType.INSIDE);
                    if (!node.getActive()) {
                        circle.setOpacity(0.5);
                        circle.setFill(Color.GRAY);
                    }
                    circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);
//                circle.setOnMouseClicked(clickCallback());

                    String label = node.getID();
                    nodeDispSet.put(label, circle);

//                if(node.getType() == Node.nodeType.KIOS) {
//                    File file = new File("main/kiosk/resources/img/pip.png");
//                    Image pip = new Image(file.toURI().toString());
//                    ImageView position = new ImageView(pip);
//                    position.setX((node.getX() - X_OFFSET) * X_SCALE);
//                    position.setY((node.getY() - Y_OFFSET - 10) * Y_SCALE);
//                    kioskPip = position;
//                }
                } else {
                    Circle circle = new Circle(0);
                    nodesEdgesPane.getChildren().add(circle);
                    circle.setCenterX((node.getX() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((node.getY() - Y_OFFSET) * Y_SCALE);
                    //System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
                    circle.setFill(Color.DODGERBLUE);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeType(StrokeType.INSIDE);
                    circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);

                    String label = node.getID();
                    nodeDispSet.put(label, circle);
                }
            }
            System.out.println("Printed All Nodes");
        }
        else {
            HashMap<String, Node> nodeSet;

            nodeSet = db.getAllNodes();
            System.out.println("drawing nodes");
            for (Node node : nodeSet.values()) {
                if (node.getFloor() == currentFloor && node.getType() != Node.nodeType.HALL) {
                    Circle circle = new Circle(NODE_RADIUS);
                    nodesEdgesPane.getChildren().add(circle);
                    circle.setCenterX((node.getxDisplay() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((node.getyDisplay() - Y_OFFSET) * Y_SCALE);
                    System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
                    circle.setFill(Color.DODGERBLUE);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeType(StrokeType.INSIDE);
                    if (!node.getActive()) {
                        circle.setOpacity(0.5);
                        circle.setFill(Color.GRAY);
                    }
                    circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);
//                circle.setOnMouseClicked(clickCallback());

                    String label = node.getID();
                    nodeDispSet.put(label, circle);

//                if(node.getType() == Node.nodeType.KIOS) {
//                    File file = new File("main/kiosk/resources/img/pip.png");
//                    Image pip = new Image(file.toURI().toString());
//                    ImageView position = new ImageView(pip);
//                    position.setX((node.getX() - X_OFFSET) * X_SCALE);
//                    position.setY((node.getY() - Y_OFFSET - 10) * Y_SCALE);
//                    kioskPip = position;
//                }
                } else {
                    Circle circle = new Circle(0);
                    nodesEdgesPane.getChildren().add(circle);
                    circle.setCenterX((node.getxDisplay() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((node.getyDisplay() - Y_OFFSET) * Y_SCALE);
                    //System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
                    circle.setFill(Color.DODGERBLUE);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeType(StrokeType.INSIDE);
                    circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);

                    String label = node.getID();
                    nodeDispSet.put(label, circle);
                }
            }
            System.out.println("Printed All Nodes");
        }
    }


    /**
     * Draws the edges according to what was given back from the database
     */
    public void drawEdges() {
        if (!toggleOn) {
            HashMap<String, Edge> edgeSet;

            edgeSet = db.getAllEdges();

            for (Edge edge : edgeSet.values()) {
                if (!edge.getActive()) {

                } else {
                    Line line = new Line();
                    nodesEdgesPane.getChildren().add(line);
                    line.setStartX((edge.getStart().getX() - X_OFFSET) * X_SCALE);
                    line.setStartY((edge.getStart().getY() - Y_OFFSET) * Y_SCALE);
                    line.setEndX((edge.getEnd().getX() - X_OFFSET) * X_SCALE);
                    line.setEndY((edge.getEnd().getY() - Y_OFFSET) * Y_SCALE);
                    line.setStrokeWidth(0);
                    line.setStrokeType(StrokeType.CENTERED);
                    if (!edge.getActive()) {
                        line.getStrokeDashArray().addAll(5.0, 2.5);
                        line.setOpacity(0.5);
                    }

                    String label = edge.getID();
                    edgeDispSet.put(label, line);
                }
            }
        }
        else {
            HashMap<String, Edge> edgeSet;

            edgeSet = db.getAllEdges();

            for (Edge edge : edgeSet.values()) {
                if (!edge.getActive()) {

                } else {
                    Line line = new Line();
                    nodesEdgesPane.getChildren().add(line);
                    line.setStartX((edge.getStart().getxDisplay() - X_OFFSET) * X_SCALE);
                    line.setStartY((edge.getStart().getyDisplay() - Y_OFFSET) * Y_SCALE);
                    line.setEndX((edge.getEnd().getxDisplay() - X_OFFSET) * X_SCALE);
                    line.setEndY((edge.getEnd().getyDisplay() - Y_OFFSET) * Y_SCALE);
                    line.setStrokeWidth(0);
                    line.setStrokeType(StrokeType.CENTERED);
                    if (!edge.getActive()) {
                        line.getStrokeDashArray().addAll(5.0, 2.5);
                        line.setOpacity(0.5);
                    }

                    String label = edge.getID();
                    edgeDispSet.put(label, line);
                }
            }
        }
    }



    int nodeState = 0;
    /**
     * this handles all mouse events related to nodes that exist in the database
     * and have been drawn
     */
    Boolean firstSelected = false;
    Boolean secondSelected = false;
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
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                HashMap<String, Node> nodeSet;
                nodeSet = db.getAllNodes();
                if (searchBarOverlayController.isSourceFocused()) {
                    clearStartNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.GREEN);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                } else if (searchBarOverlayController.isDestinationFocused()) {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                        }
                    }
                } else if (!firstSelected){
                    clearStartNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.GREEN);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                    firstSelected = true;
                } else {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                        }
                    }
                    firstSelected = false;
                }
            }
            event.consume();
        }
    };

        /**
     * this is the event handler for clicking on the map to create a node
     */
    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;
        @Override
        public void handle(MouseEvent event) {
            if(event.getEventType() == MouseEvent.MOUSE_PRESSED){
                System.out.println("Mouse_Pressed");
                getMouseValue(event);
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

                System.out.println("Offset X: " + offsetX + " Offset Y: " + offsetY);
                if(newTranslateX > (X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880))
                    newTranslateX = X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880;
                if(newTranslateX < -(X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880))
                    newTranslateX = -(X_SCALE*mapImage.getScaleX()*2880 - X_SCALE*2880);
                if(newTranslateY > (Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530))
                    newTranslateY = (Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530);
                if(newTranslateY < -(Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530))
                    newTranslateY = -(Y_SCALE*mapImage.getScaleX()*1530 - X_SCALE*1530);
                mapImage.setTranslateX(newTranslateX);
                mapImage.setTranslateY(newTranslateY);
                nodesEdgesPane.setTranslateX(newTranslateX);
                nodesEdgesPane.setTranslateY(newTranslateY);
            }
        }
    };

    public void addOverlay() {
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/map/SearchBarOverlay.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
       searchBarOverlayController = loader.getController();
       searchBarOverlayController.startUp(mapScreenController);
       searchBarOverlayPane.setTop(root);
    }

    /**
     * this clears the anchor pane, draws the edges and the nodes and loads in the new map
     * this also brings in the basic overlays
     */
    public void updateMap(){
        nodeDispSet.clear();
        nodesEdgesPane.getChildren().clear();
        getMap();
        drawEdges();
        drawNodes();
    }

    ArrayList<Node> pathMade;
    /**
     * Used to draw the list of nodes returned by AStar
     * @param path List of Nodes to be drawn
     */
    //removed static hope it didn't break anything
    public void drawPath(ArrayList<Node> path) {
        double width, height, angle;
        double distanceCounter = 0;



        Node currentNode = null, pastNode = null;
        if(pathMade != null){
            resetPath();
        }

        this.pathMade = path;
        //setSpinner();
        Line line;
        for (Node n : path) {
            pastNode = currentNode;
            currentNode = n;
            if (!path.get(0).equals(n)) {
                width = currentNode.getX() - pastNode.getX();
                height = currentNode.getY() - pastNode.getY();
                angle = Math.atan2(height , width);

                //increment the distanceCounter
                distanceCounter += currentNode.distanceBetweenNodes(pastNode);

                if(distanceCounter >= 175) {
                    distanceCounter = 0;

                    drawTriangle(angle, pastNode.getX(), pastNode.getY());
                }




            }
            nodeDispSet.get(currentNode.getID()).setFill(Color.rgb(250, 150, 0));
            // }
            if (path.get(0).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.GREEN);
            }
            if (path.get(path.size()-1).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.RED);
            }
            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        line = edgeDispSet.get(e.getID());
                        edgeDispSet.get(e.getID()).setStroke(Color.rgb(250, 150, 0));
                        //edgeDispSet.get(e.getID()).setStrokeWidth(5.0);
                        if(e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor){
                            edgeDispSet.get(e.getID()).setStrokeWidth(5.0);
                        }
                        if(e.getStart().getFloor() != e.getEnd().getFloor()) {
                            nodeDispSet.get(pastNode.getID()).setFill(Color.DARKMAGENTA);
                            nodeDispSet.get(currentNode.getID()).setFill(Color.DARKMAGENTA);
                        }
                    }
                }
            }
        }
        pathDrawn = true;
    }

    /**
     * drawTriangle is the function that draws directional arrows for the function
     * @param angle is the angle used to generate this
     * @param initX is where it starts on the X axis
     * @param initY is where it starts on the Y axis
     *
     * This outputs an arrow on the screen along the line.
     */
    public void drawTriangle(double angle, double initX, double initY) {

        double x1, x2, x3, y1, y2, y3;


        Polygon arrow = new Polygon();



        initX = (initX - X_OFFSET) * X_SCALE;
        initY = (initY - Y_OFFSET) * Y_SCALE;

        //System.out.println("X:  " + initX + "  Y:  " + initY);

        x1 = initX + (12 * Math.cos(angle));
        y1 = initY + (12 * Math.sin(angle));

        x2 = initX + (10 * Math.cos(angle - (2 * Math.PI / 3)));
        y2 = initY + (10 * Math.sin(angle - (2 * Math.PI / 3)));

        x3 = initX + (10 * Math.cos(angle + (2 * Math.PI / 3)));
        y3 = initY + (10 * Math.sin(angle + (2 * Math.PI / 3)));


        arrow.getPoints().addAll(new Double[]{
                initX, initY,
                x2, y2,
                x1, y1,
                x3, y3});
        arrow.setFill(Color.rgb(250, 150, 0));

        nodesEdgesPane.getChildren().add(arrow);

        arrowDispSet.add(arrow);
    }
    /*
    public void setSpinner(){
        ObservableList<String> allowedFloorsList = FXCollections.observableArrayList((pathMade.get(pathMade.size()-1).getFloor().toString()),
                (pathMade.get(0).getFloor().toString()));
        floors = new SpinnerValueFactory.ListSpinnerValueFactory<String>(allowedFloorsList);

        floorSpinner.setValueFactory(floors);
    }
    */

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
                        edgeDispSet.get(e.getID()).setStrokeWidth(0);
                        if(e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor){
                            edgeDispSet.get(e.getID()).setStrokeWidth(0);
                        }
                    }
                }
            }
            for (Polygon p : arrowDispSet){
                p.setVisible(false);
                p.setPickOnBounds(false);
            }
            arrowDispSet.clear();
        }
    }

    public void setToggleOn(Boolean toggleOn){
        this.toggleOn = toggleOn;

        updateMap();
        if (pathDrawn){
            drawPath(pathMade);
        }

    }

    public void clearStartNode(){
        if (pathDrawn) resetPath();
        for (Circle c: nodeDispSet.values()){
            if(c.getFill().equals(Color.GREEN)) {
                c.setFill(Color.DODGERBLUE);
            }

        }
    }

    public void clearEndNode(){
        if (pathDrawn) resetPath();
        for (Circle c: nodeDispSet.values()){
            if(c.getFill().equals(Color.RED)) {
                c.setFill(Color.DODGERBLUE);
            }

        }
    }
}
