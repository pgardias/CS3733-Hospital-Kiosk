package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapScreenController {

    private int X_OFFSET = -523;
    private int Y_OFFSET = 0;
    private double X_SCALE = 1588.235294/5000.0;
    private double Y_SCALE = 1080.0/3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int IMG_WIDTH = 5000;
    public int IMG_HEIGHT = 3400;
    private static final double ZOOM_3D_MIN = 1.013878875;
    private static final double ZOOM_2D_MIN = 1.208888889;



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
    Spinner<String> floorSpinner;
    ObservableList<String> floorsList = FXCollections.observableArrayList( "L2",
            "L1",
            "G",
            "1",
            "2",
            "3");

    SpinnerValueFactory<String> floors = new SpinnerValueFactory.ListSpinnerValueFactory<String>(floorsList);


    @FXML
    StackPane mapPane;

    @FXML
    ImageView mapImage;

    @FXML
    AnchorPane nodesEdgesPane;


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
        floors.setValue("2");
        floorSpinner.setValueFactory(floors);
        spinnerOp();
        zoomSlider.setMin(1.20888889);
        zoomSlider.setValue(zoomSlider.getMin());
        Image newImage = new Image("/img/maps/2d/02_thesecondfloor.png");
        mapImage.setImage(newImage);
        mapImage.scaleXProperty().bind(zoomSlider.valueProperty());
        mapImage.scaleYProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleXProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleYProperty().bind(zoomSlider.valueProperty());
        firstSelected = true;

        mapImage.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);
        drawEdges();
        drawNodes();
        getMap();
        addOverlay();

        searchBarOverlayController.setSourceSearchBar("Current Kiosk");
        nodeDispSet.get("PKIOS00102").setFill(Color.GREEN);
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

        stage.setScene(new Scene(root, 1920, 1080));
        stage.setTitle("Home Screen");
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    public void spinnerOp(){
        floorState = floorSpinner.getValue().toString();
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
     * switch statement that determines which map is loaded in
     */
    public void getMap(){
        Image image;
        floorState = floorSpinner.getValue().toString();
        if (toggleOn){
            X_OFFSET = 0;
            Y_OFFSET = -19;
            X_SCALE = 1920.0/5000.0;
            Y_SCALE = 1065.216/2774.0;
            IMG_HEIGHT = 2774;
            zoomSlider.setMin(ZOOM_3D_MIN);
            zoomSlider.setValue(ZOOM_3D_MIN);
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
            X_OFFSET = -523;
            Y_OFFSET = 0;
            X_SCALE = 1588.235294/5000.0;
            Y_SCALE = 1080.0/3400.0;
            IMG_HEIGHT = 3400;
            zoomSlider.setMin(ZOOM_2D_MIN);
            zoomSlider.setValue(ZOOM_2D_MIN);
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
        System.out.println("drawing nodes");
        for (Node node : nodeSet.values()) {
            Circle circle = new Circle();
            circle.setRadius(NODE_RADIUS);
            if (node.getFloor() != currentFloor || node.getType() == Node.nodeType.HALL) {
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
    }


    /**
     * Draws the edges according to what was given back from the database
     */
    public void drawEdges() {
        HashMap<String, Edge> edgeSet;

        edgeSet = db.getAllEdges();

        for (Edge edge : edgeSet.values()) {
            if (edge.getActive()) {
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
                line.setStrokeWidth(5.0);
                line.setStrokeType(StrokeType.CENTERED);
                line.setVisible(false);
                line.setPickOnBounds(false);
                line.setDisable(true);

                String label = edge.getID();
                edgeDispSet.put(label, line);
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
                            searchBarOverlayController.setSourceSearchBar(node.getShortName());
                        }
                    }
                } else if (searchBarOverlayController.isDestinationFocused()) {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getShortName());
                        }
                    }
                } else {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getShortName());
                        }
                    }
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

                double translateSlopeX = X_SCALE*mapImage.getScaleX()*IMG_WIDTH;
                double translateSlopeY = Y_SCALE*mapImage.getScaleX()*IMG_HEIGHT;

                System.out.println("Offset X: " + offsetX + " Offset Y: " + offsetY);
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
        floorSpinner.setValueFactory(floors);
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
            } else if (path.get(path.size()-1).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.RED);
            }
            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        edgeDispSet.get(e.getID()).setStroke(Color.rgb(250, 150, 0));
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
                        edgeDispSet.get(e.getID()).setVisible(false);
                    }
                }
            }
        }
    }

    public void setToggleOn(Boolean toggleOn){
        this.toggleOn = toggleOn;
        spinnerOp();
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
