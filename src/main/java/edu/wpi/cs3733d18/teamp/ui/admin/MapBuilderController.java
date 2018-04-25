package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.*;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import edu.wpi.cs3733d18.teamp.ui.home.ShakeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
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
    public double X_SCALE = 1588.235294 / 5000.0;
    public double Y_SCALE = 1080.0 / 3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int IMG_WIDTH = 5000;
    public int IMG_HEIGHT = 3400;
    private static final double ZOOM_3D_MIN = 1.013878875;
    private static final double ZOOM_2D_MIN = 1.208888889;
    private double zoomForTranslate = 0;

    Circle newNodeCircle = new Circle();

    private Node startNode;
    private Node endNode;
    private Node nodeModify;
    private Edge selectedEdge;
    private Node dragNodeOrg;
    //this is for creating new edges
    private Node firstSelect = null;
    private Node secondSelect = null;
    private Boolean firstChoice = true;
    private Node.floorType newNodeFloor;

    private Boolean edgeSelected = false;
    private Boolean pathDrawn = false;
    private Boolean modifyingNode = false;
    private Boolean isNewNode = false;
    private Boolean toggleOn = false;

    private static HashMap<String, Circle> nodeDispSet = new HashMap<>();
    private static HashMap<String, Line> edgeDispSet = new HashMap<>();

    private String nodeID;
    private String dragNodeID;
    private String edgeID;

    Thread thread;

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
    Slider zoomSlider;

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

    @FXML
    static PopOver popOver;

    Boolean popOverHidden = true;

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

    /**
     * sets up the search bars so they can be autofilled
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setWordArrays();

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(destinationSearchBar, destinationWords);
        AutoCompletionBinding<String> sourceBinding = TextFields.bindAutoCompletion(sourceSearchBar, sourceWords);

        destBinding.setPrefWidth(destinationSearchBar.getPrefWidth());
        sourceBinding.setPrefWidth(sourceSearchBar.getPrefWidth());
    }


    /**
     * intializes values such as
     * mapBuilderController to this object
     * sets the current floor
     * initializes the zoom slider and binds it to the map
     * draws the initial screen and sets mouse events
     */
    @FXML
    public void startUp() {
        mapBuilderController = this;
        floorState = "2";
        currentFloor = Node.floorType.LEVEL_2;
        zoomSlider.setMin(ZOOM_2D_MIN);
        zoomSlider.setValue(ZOOM_2D_MIN);
        zoomForTranslate = zoomSlider.getValue();
        Image newImage = new Image("/img/maps/2d/02_thesecondfloor.png");
        mapImage.setImage(newImage);
        mapImage.scaleXProperty().bind(zoomSlider.valueProperty());
        mapImage.scaleYProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleXProperty().bind(zoomSlider.valueProperty());
        nodesEdgesPane.scaleYProperty().bind(zoomSlider.valueProperty());

        newNodeCircle.setPickOnBounds(false);
        mapImage.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);
        getMap();
        drawEdges();
        drawNodes();
        nodesEdgesPane.getChildren().add(newNodeCircle);
        //newNodeCircle.addEventHandler(MouseEvent.ANY, nodeClickHandler);
        addOverlay();

        basePane.addEventHandler(MouseEvent.ANY, testMouseEvent);
        thread = new Thread(task);
        thread.start();

    }

    /**
     * Creates new thread that increments a counter while mouse is inactive, revert to homescreen if
     * timer reaches past a set value by administrator
     */
    Task task = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                int timeout = Settings.getTimeDelay();
                int counter = 0;

                while(counter <= timeout) {
                    Thread.sleep(5);
                    counter += 5;
                }
                Scene scene;
                Parent root;
                FXMLLoader loader;
                scene = mapBuilderOverlayController.backButton.getScene();

                loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
                try {
                    root = loader.load();
                    scene.setRoot(root);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            } catch (InterruptedException v) {
                System.out.println(v);
                thread = new Thread(task);
                thread.start();
                return null;
            }
            return null;
        }
    };

    /**
     * Handles active mouse events by interrupting the current thread and setting a new thread and timer
     * when the mouse moves. This makes sure that while the user is active, the screen will not time out.
     */
    EventHandler<MouseEvent> testMouseEvent = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            Originator localOriginator = new Originator();
            long start, now;
            localOriginator.setState("Active");
            localOriginator.saveStateToMemento();
            thread.interrupt();

            try{
                thread.join();
            } catch (InterruptedException ie){
                System.out.println(ie);
            }

            Task task2 = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        int timeout = Settings.getTimeDelay();
                        int counter = 0;

                        while(counter <= timeout) {
                            Thread.sleep(5);
                            counter += 5;
                        }
                        Scene scene;
                        Parent root;
                        FXMLLoader loader;
                        scene = mapBuilderOverlayController.backButton.getScene();

                        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
                        try {
                            root = loader.load();
                            scene.setRoot(root);
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    } catch (InterruptedException v) {
                        System.out.println(v);
                        thread = new Thread(task);
                        thread.start();
                        return null;
                    }
                    return null;
                }
            };

            thread = new Thread(task2);
            thread.start();
        }
    };

    @FXML
    void aStarSetOp(ActionEvent event) {
        Main.settings.setPathfindingAlgorithm(PathfindingContext.PathfindingSetting.AStar);
        Main.pathfindingContext.setPathfindingContext(Main.settings.getPathfindingSettings());
    }

    @FXML
    void breadthFirstSetOp(ActionEvent event) {
        Main.settings.setPathfindingAlgorithm(PathfindingContext.PathfindingSetting.BreadthFirst);
        Main.pathfindingContext.setPathfindingContext(Main.settings.getPathfindingSettings());
    }

    @FXML
    void depthFirstSetOp(ActionEvent event) {
        Main.settings.setPathfindingAlgorithm(PathfindingContext.PathfindingSetting.DepthFirst);
        Main.pathfindingContext.setPathfindingContext(Main.settings.getPathfindingSettings());
    }


    /**
     * switch statement that determines which map is loaded in
     */
    public void getMap() {
        Image image;
        if (toggleOn) {
            switch (floorState) {
                case "3":
                    image = new Image("/img/maps/3d/3-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_3);

                    break;
                case "2":
                    image = new Image("/img/maps/3d/2-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_2);

                    break;
                case "1":
                    image = new Image("/img/maps/3d/1-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_1);
                    break;
                case "G":
                    image = new Image("/img/maps/3d/1-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_G);

                    break;
                case "L1":
                    image = new Image("/img/maps/3d/L1-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L1);

                    break;
                default:
                    image = new Image("/img/maps/3d/L2-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L2);

                    break;
            }
        } else {
            switch (floorState) {
                case "3":
                    image = new Image("/img/maps/2d/03_thethirdfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_3);

                    break;
                case "2":
                    image = new Image("/img/maps/2d/02_thesecondfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_2);

                    break;
                case "1":
                    image = new Image("/img/maps/2d/01_thefirstfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_1);

                    break;
                case "G":
                    image = new Image("/img/maps/2d/00_thegroundfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_G);

                    break;
                case "L1":
                    image = new Image("/img/maps/2d/00_thelowerlevel1.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L1);

                    break;
                default:
                    image = new Image("/img/maps/2d/00_thelowerlevel2.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L2);
                    break;
            }
        }
        mapImage.setImage(image);
        autoTranslateZoom(zoomSlider.getMin(), zoomSlider.getMin(), IMG_WIDTH / 2, IMG_HEIGHT / 2);
    }

    /**
     * dont think i need this but its assigned to a button somewhere
     */
    @FXML
    public void updateMapSize() {
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
     *
     * @param t
     */
    @FXML
    public void getMouseValue(MouseEvent t) {
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
        orgTranslateX = mapImage.getTranslateX();
        orgTranslateY = mapImage.getTranslateY();
    }


    /**
     * this lets the user scroll the wheel to move the zoom slider
     * This also readjusts the screen based on the bounds
     * this will also zoom in the direction of the mouse
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s) {
        //get the change in the scroll wheel, scale it and add it to the current zoom
        double newValue = (s.getDeltaY()) / 200.0 + zoomSlider.getValue();
//        System.out.println("mouse scroll change: " + s.getDeltaY());
//        System.out.println("source: " + s.getSource().toString());
        //initialize change to 0 which means the map will zoom in on its center
        double change = 0;

        // if the scroll wheel was a negative change and the app is not zoomed out all the way activate change
        if ((s.getDeltaY() < 0 ) && (zoomSlider.getValue() != zoomSlider.getMin())) change  = 1;
        // if the scroll wheel was positive change and the app is not zoomed in all the way activate change
        if ((s.getDeltaY() > 0 ) &&(zoomSlider.getValue() != zoomSlider.getMax())) change = 1;
        System.out.println("s.getDeltaY: " + s.getDeltaY());
        System.out.println("change: " + change);

        double mouseX = s.getSceneX();
        double mouseY = s.getSceneY();
        System.out.println("mouseX: " + mouseX + " mouseY: " + mouseY);

        double orgTranslateX = mapImage.getTranslateX();
        double orgTranslateY = mapImage.getTranslateY();
        System.out.println("orgTranslate X: " + orgTranslateX + " orgTranslate Y: " + orgTranslateY);

        // adjusts the mouse based on the image width the zoom slider and the x scale as well as its position on the scene
        double mouseAdjustX = (orgTranslateX + (IMG_WIDTH * zoomSlider.getValue() * X_SCALE *(mouseX/1920.0)));
        double mouseAdjustY = (orgTranslateY + (IMG_HEIGHT * zoomSlider.getValue() * Y_SCALE *(mouseY/1080.0)));
        System.out.println("mouse adjustX: " + mouseAdjustX + " mouse adjustY: " + mouseAdjustY);

        //Get the image center
        double imageCenterX = (orgTranslateX + (IMG_WIDTH * zoomSlider.getValue() * X_SCALE *0.5));
        double imageCenterY = (orgTranslateY + (IMG_HEIGHT * zoomSlider.getValue() * Y_SCALE *0.5));
        System.out.println(" image centerx : " + imageCenterX + " image centery: " + imageCenterY);

        //find out how far off hte mosue is from the center
        double mouseChangeX = mouseAdjustX - imageCenterX;
        double mouseChangeY = mouseAdjustY - imageCenterY;
        System.out.println("Mouse ChangeX: " + mouseChangeX + " Mouse Change Y: " + mouseChangeY);

        //translate based off of mouse distance from center and the amount scrolled
        newTranslateX = (orgTranslateX * zoomSlider.getValue()/zoomForTranslate) - (change * mouseChangeX * s.getDeltaY()/256.0);
        newTranslateY = (orgTranslateY * zoomSlider.getValue()/zoomForTranslate) - (change * mouseChangeY * s.getDeltaY()/256.0);
        System.out.println("new translate x: " + newTranslateX + " new translate Y: " + newTranslateY);

        zoomSlider.setValue(newValue);

        zoomForTranslate = zoomSlider.getValue();

        double translateSlopeX = X_SCALE*mapImage.getScaleX()*IMG_WIDTH;
        double translateSlopeY = Y_SCALE*mapImage.getScaleX()*IMG_HEIGHT;

        // keeps the translation within particular bounds
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

        // if we are currently adding a new node get circle position based on form fields
        if (isNewNode) {
            if (!toggleOn) { // 2D map
                newNodeCircle.setCenterX((mapBuilderNodeFormController.getNode2XCoord() - X_OFFSET) * X_SCALE);
                newNodeCircle.setCenterY((mapBuilderNodeFormController.getNode2YCoord() - Y_OFFSET) * Y_SCALE);
            } else {  // 3D map
                newNodeCircle.setCenterX((mapBuilderNodeFormController.getNode3XCoord() - X_OFFSET) * X_SCALE);
                newNodeCircle.setCenterY((mapBuilderNodeFormController.getNode3YCoord() - Y_OFFSET) * Y_SCALE);
            }
            //checks to make sure we are making the node visible on the rihgt floor
            if (newNodeFloor.equals(currentFloor)) {
                newNodeCircle.setRadius(NODE_RADIUS);
                newNodeCircle.setVisible(true);
                newNodeCircle.setDisable(false);
                newNodeCircle.setFill(Color.RED);
            } else {
                newNodeCircle.setVisible(false);
                newNodeCircle.setDisable(true);
            }
        }
        // iterate through ever node in the database
        for (Node node : nodeSet.values()) {
            Circle circle = new Circle();
            circle.setRadius(NODE_RADIUS);
            if (node.getFloor() != currentFloor) { //if node circle is not on current floor
                circle.setVisible(false);
                circle.setDisable(true);
                circle.setPickOnBounds(false);
            }
            nodesEdgesPane.getChildren().add(circle);

            // if we are currentlfy modifying a node and node we are on in the list matches the modifynode id
            if (modifyingNode && node.getID() == nodeModify.getID()) { //reads from form labels
                if (!toggleOn) { //2D map
                    circle.setCenterX((mapBuilderNodeFormController.getNode2XCoord() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((mapBuilderNodeFormController.getNode2YCoord() - Y_OFFSET) * Y_SCALE);
                } else { //3D map
                    circle.setCenterX((mapBuilderNodeFormController.getNode3XCoord() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((mapBuilderNodeFormController.getNode3YCoord() - Y_OFFSET) * Y_SCALE);
                }
                circle.setFill(Color.RED);
            } else { //if we are not modifying a node, just add circle based off of map coordinates
                if (!toggleOn) {
                    circle.setCenterX((node.getX() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((node.getY() - Y_OFFSET) * Y_SCALE);
                } else {
                    circle.setCenterX((node.getxDisplay() - X_OFFSET) * X_SCALE);
                    circle.setCenterY((node.getyDisplay() - Y_OFFSET) * Y_SCALE);
                }
                circle.setFill(Color.DODGERBLUE);
            }


            //System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());

            circle.setStroke(Color.BLACK);
            circle.setStrokeType(StrokeType.INSIDE);
            if (!node.getActive()) { //if node is inactive make it gray and opaque
                circle.setOpacity(0.5);
                circle.setFill(Color.GRAY);
            }

            circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);

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
        Boolean fixEdgeStart = false;
        Boolean fixEdgeEnd = false;
        //iterates through the database of edges
        for (Edge edge : edgeSet.values()) {

            Line line = new Line();
            nodesEdgesPane.getChildren().add(line);


            if (modifyingNode) {
                //modifying a node so we need to change either the start or end of the line
                ArrayList<Edge> dragEdges = nodeModify.getEdges();

                for (Edge modifyEdge : dragEdges) {
                    if (modifyEdge.equals(edge)) {
                        System.out.println("modify edge equals the edge");
                        if (modifyEdge.getStart() == nodeModify) fixEdgeStart = true; // fix the start of the line
                        if (modifyEdge.getEnd() == nodeModify) fixEdgeEnd = true; // fix the end of the line
                        break;
                    }
                }
            }

            if (!toggleOn) { //2D
                line.setStartX((edge.getStart().getX() - X_OFFSET) * X_SCALE);
                line.setStartY((edge.getStart().getY() - Y_OFFSET) * Y_SCALE);
                line.setEndX((edge.getEnd().getX() - X_OFFSET) * X_SCALE);
                line.setEndY((edge.getEnd().getY() - Y_OFFSET) * Y_SCALE);
                if (fixEdgeEnd) { //fixing the start of the line based off of the node form label
                    line.setEndX((mapBuilderNodeFormController.getNode2XCoord() - X_OFFSET) * X_SCALE);
                    line.setEndY((mapBuilderNodeFormController.getNode2YCoord() - Y_OFFSET) * Y_SCALE);
                } else if (fixEdgeStart) { //fixing the end of the line
                    line.setStartX((mapBuilderNodeFormController.getNode2XCoord() - X_OFFSET) * X_SCALE);
                    line.setStartY((mapBuilderNodeFormController.getNode2YCoord() - Y_OFFSET) * Y_SCALE);
                }
            } else {//3D
                line.setStartX((edge.getStart().getxDisplay() - X_OFFSET) * X_SCALE);
                line.setStartY((edge.getStart().getyDisplay() - Y_OFFSET) * Y_SCALE);
                line.setEndX((edge.getEnd().getxDisplay() - X_OFFSET) * X_SCALE);
                line.setEndY((edge.getEnd().getyDisplay() - Y_OFFSET) * Y_SCALE);
                if (fixEdgeEnd) { // fixing the start of the line
                    line.setEndX((mapBuilderNodeFormController.getNode3XCoord() - X_OFFSET) * X_SCALE);
                    line.setEndY((mapBuilderNodeFormController.getNode3YCoord() - Y_OFFSET) * Y_SCALE);
                } else if (fixEdgeStart) { //fixing the end of the line
                    line.setStartX((mapBuilderNodeFormController.getNode3XCoord() - X_OFFSET) * X_SCALE);
                    line.setStartY((mapBuilderNodeFormController.getNode3YCoord() - Y_OFFSET) * Y_SCALE);
                }
            }

            line.setStrokeWidth(EDGE_WIDTH);
            line.setStrokeType(StrokeType.CENTERED);
            if (!edge.getActive()) {
                line.getStrokeDashArray().addAll(5.0, 2.5);
                line.setOpacity(0.5);
            }
            line.setOnMouseClicked(edgeClickHandler);
            if (edge.getStart().getFloor() != currentFloor || edge.getEnd().getFloor() != currentFloor) {
                //we are not on the floor of the edge, disable it and make it invisible
                line.setVisible(false);
                line.setDisable(true);
                line.setPickOnBounds(false);
            }
            fixEdgeEnd = false;
            fixEdgeStart = false;

            String label = edge.getID();
            edgeDispSet.put(label, line);
        }
    }

    /**
     * This handles all mouse events related to dragging around a circle
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

            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                //the mouse was pressed so we get where in the scene it was pressed and the location of the circle that
                //that was pressed on relative to the nodeEdgesPane
                orgMouseX = event.getSceneX();
                orgMouseY = event.getSceneY();
                orgCenterX = newNodeCircle.getCenterX();
                orgCenterY = newNodeCircle.getCenterY();
                //System.out.println("Original Mouse X: " + orgMouseX + " New Mouse Y: " + orgMouseY);
                //System.out.println("Original Center X: " + newCenterX + " New Original Y: " + newCenterX);
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                // the mouse was dragged; using the information we got from mouse pressed we move the circle
                double offsetX = event.getSceneX() - orgMouseX;
                double offsetY = event.getSceneY() - orgMouseY;

                double scaledx2Offset = (offsetX) / nodesEdgesPane.getScaleX();
                double scaledy2Offset = (offsetY) / nodesEdgesPane.getScaleY();
                //System.out.println("New Scaled Offset X: " + scaledx2Offset + " New Scaled Offset Y: " + scaledy2Offset);

                newCenterX = orgCenterX + scaledx2Offset;
                newCenterY = orgCenterY + scaledy2Offset;

                //takes the new center and finds out where the actual coordinates are based on its position in the
                // node edge pane
                double nodex2Coord = newCenterX / X_SCALE + X_OFFSET;
                double nodey2Coord = newCenterY / Y_SCALE + Y_OFFSET;
                if (!toggleOn) {//2D
                    mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorState);
                } else {//3D
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
     * this includes dragging nodes, hovering over nodes and clicking on nodes
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
                if (nodeDispSet.get(string) == event.getSource()) {
                    //the circle that caused the event is the node that will be dragged
                    Node dragNode = dragNodeSet.get(string);
                    dragNodeOrg = dragNode;
                    dragNodeID = dragNode.getID();
                    break;
                }
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                //the mouse was pressed so we get its location and the location of the circle it was pressed on
                orgMouseX = event.getSceneX();
                orgMouseY = event.getSceneY();
                orgCenterX = nodeDispSet.get(dragNodeID).getCenterX();
                orgCenterY = nodeDispSet.get(dragNodeID).getCenterY();
                isDragging = false;
            } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                isDragging = true;
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                //the circle is being dragged
                if (isDragging && !isNewNode && !edgeSelected) {
                    // we are not creating a new node and an edge has not been selected
                    // so we go ahead and move the circles location in both 2D and 3D
                    clearCircles();

                    nodeModify = dragNodeOrg;
                    ArrayList<Edge> dragEdges = dragNodeOrg.getEdges();
                    //change in mouse since it was pressed
                    double offsetX = event.getSceneX() - orgMouseX;
                    double offsetY = event.getSceneY() - orgMouseY;
                    // scale the mouse change based on the zoom of the map
                    double scaledx2Offset = (offsetX) / nodesEdgesPane.getScaleX();
                    double scaledy2Offset = (offsetY) / nodesEdgesPane.getScaleY();

                    // the new center is the offset + the original center
                    newCenterX = orgCenterX + scaledx2Offset;
                    newCenterY = orgCenterY + scaledy2Offset;
                    // mark the drag node red and actually set its new center
                    nodeDispSet.get(dragNodeOrg.getID()).setFill(Color.rgb(205, 35, 0, 0.99));
                    nodeDispSet.get(dragNodeID).setCenterX(newCenterX);
                    nodeDispSet.get(dragNodeID).setCenterY(newCenterY);

                    // inverse calculate the coordinates based on its position in the node pane
                    double nodex2Coord = newCenterX / X_SCALE + X_OFFSET;
                    double nodey2Coord = newCenterY / Y_SCALE + Y_OFFSET;

                    // for each edge attached to the drag node we move them as well
                    for (Edge edge : dragEdges) {
                        if (edge.getStart() == dragNodeOrg) {
                            //the drag node is the edges start node
                            edgeDispSet.get(edge.getID()).setStartX(newCenterX);
                            edgeDispSet.get(edge.getID()).setStartY(newCenterY);
                        } else {
                            // the drag node is the edges end node
                            edgeDispSet.get(edge.getID()).setEndX(newCenterX);
                            edgeDispSet.get(edge.getID()).setEndY(newCenterY);
                        }
                    }
                    if (!modifyingNode) {
                        // if its not the first time through this loop open the new node form with fields filled in
                        //based on the drag node
                        newNodeForm(dragNodeOrg.getID(), dragNodeOrg.getLongName(), dragNodeOrg.getX(), dragNodeOrg.getY(),
                                dragNodeOrg.getxDisplay(), dragNodeOrg.getyDisplay(), dragNodeOrg.getFloor().toString(),
                                dragNodeOrg.getBuilding().toString(), dragNodeOrg.getType().toString(), dragNodeOrg.getActive());
                    }
                    modifyingNode = true;
                    if (!toggleOn) { //2D
                        //sets the 2D coordinates of the from and generates 3D
                        mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorState);
                    } else {//3D
                        mapBuilderNodeFormController.set3XYCoords(nodex2Coord, nodey2Coord);
                    }

                }
            } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                //node was clicked ot be modified
                popOver.hide();
                popOverHidden = true;
                if (!isDragging) {
                    HashMap<String, Node> nodeSet;
                    nodeSet = db.getAllNodes();
                    if (getSourceFocus()) {
                        // if the start search bar is clicked the node gets put in there
                        removeFocus();
                        clearCircles();
                        System.out.println("clear all nodes");
                        for (String string : nodeDispSet.keySet()) {
                            if (nodeDispSet.get(string) == event.getSource()) {
                                Node node = nodeSet.get(string);
                                nodeDispSet.get(node.getID()).setFill(Color.GREEN);
                                sourceSearchBar.setText(node.getLongName());
                            }
                        }
                    } else if (getDestinationFocus()) {
                        // if the destination bar is clicked, the clicked node gets put in there
                        removeFocus();
                        clearCircles();
                        System.out.println("clear all nodes");
                        for (String string : nodeDispSet.keySet()) {
                            if (nodeDispSet.get(string) == event.getSource()) {
                                Node node = nodeSet.get(string);
                                nodeDispSet.get(node.getID()).setFill(Color.RED);
                                destinationSearchBar.setText(node.getLongName());
                            }
                        }
                    } else if (isNewNode) {
                        // if we are creating a new node, put the added node in its connecting node
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
                                nodeModify = node;
                                nodeDispSet.get(node.getID()).setFill(Color.rgb(205, 35, 0, 0.99));
                                nodeID = nodeModify.getLongName();


                                System.out.println(nodeModify.getID());
                                newNodeForm(nodeModify.getID(), nodeModify.getLongName(), nodeModify.getX(), nodeModify.getY(),
                                        nodeModify.getxDisplay(), nodeModify.getyDisplay(), nodeModify.getFloor().toString(),
                                        nodeModify.getBuilding().toString(), nodeModify.getType().toString(), nodeModify.getActive());
                            }
                        }
                        modifyingNode = true;
                    } else if (edgeSelected) {
                        //This modifies the edge by replacing the start and end node in the edge
                        //when clicking on new nodes
                        System.out.println("lets edit an edge");
                        if (mapBuilderEdgeFormController.checkEndNodeBar()) nodeState = 1;
                        if (mapBuilderEdgeFormController.checkStartNodeBar()) nodeState = 0;
                        switch (nodeState) {
                            case 0: // the first node in the field will be modified
                                if (firstSelect != null) {
                                    nodeDispSet.get(firstSelect.getID()).setFill(Color.DODGERBLUE);
                                }
                                for (String string : nodeDispSet.keySet()) {
                                    if (nodeDispSet.get(string) == event.getSource()) {
                                        Node node = nodeSet.get(string);
                                        firstSelect = node;
                                        nodeDispSet.get(node.getID()).setFill(Color.rgb(205, 35, 0, 0.99));
                                        nodeID = firstSelect.getLongName();

                                        mapBuilderEdgeFormController.setStartNodeTxt(firstSelect.getID());
                                        System.out.println("First Node: " + firstSelect.getLongName());
                                        firstChoice = false;
                                        break;
                                    }
                                }
                                nodeState = 1;
                                break;
                            case 1: // the second node in the field will be modified
                                if (secondSelect != null) {
                                    nodeDispSet.get(secondSelect.getID()).setFill(Color.DODGERBLUE);
                                }
                                for (String string : nodeDispSet.keySet()) {
                                    if (nodeDispSet.get(string) == event.getSource()) {
                                        Node node = nodeSet.get(string);
                                        secondSelect = node;
                                        nodeDispSet.get(node.getID()).setFill(Color.rgb(100, 215, 0, 0.99));
                                        nodeID = secondSelect.getLongName();

                                        mapBuilderEdgeFormController.setEndNodeTxt(secondSelect.getID());
                                        System.out.println("Second Node: " + secondSelect.getLongName());
                                        firstChoice = true;
                                        break;
                                    }
                                }
                                nodeState = 0;
                                break;
                        }
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_ENTERED && popOverHidden && !isDragging) {
                // puts the popOver shown on mouse entered
                HashMap<String, Node> nodeSet = db.getAllNodes();
                System.out.println("MOUSE_ENTERED event at " + event.getSource());
                for (String string : nodeDispSet.keySet()) {
                    if (nodeDispSet.get(string) == event.getSource()) {
                        if (popOver != null && popOver.getOpacity() == 0) {
                            popOver.hide();
                            popOver = null;
                        }
                        Node node = nodeSet.get(string);
                        // Correcting enum toString conversion
                        String type = node.getType().toString().toUpperCase();
                        if (type.equals("STAIR")) {
                            type = "STAIRS";
                        } else if (type.equals("CONFERENCE")) {
                            type = "CONFERENCE ROOM";
                        } else if (type.equals("HALL")) {
                            type = "HALLWAY";
                        } else if (type.equals("INFORMATION")) {
                            type = "INFORMATION DESK";
                        } else if (type.equals("LABS")) {
                            type = "LABORATORY";
                        } else if (type.equals("SERVICE")) {
                            type = "SERVICES";
                        }
                        Label nodeTypeLabel = new Label(type);
                        Label nodeLongNameLabel = new Label("Name: " + node.getLongName());
                        Label nodeBuildingLabel = new Label("Building: " + node.getBuilding().toString());
                        nodeTypeLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #0b2f5b; -fx-font-weight: 700; -fx-padding: 10px 10px 0 10px;");
                        nodeTypeLabel.setAlignment(Pos.CENTER);
                        nodeLongNameLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 0 10px;");
                        nodeBuildingLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 10px 10px;");
                        VBox popOverVBox = new VBox(nodeTypeLabel, nodeLongNameLabel, nodeBuildingLabel);
                        popOver = new PopOver(popOverVBox);

                        if (event.getSceneX() < 960) {
                            popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
                        }
                        else {
                            popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_TOP);
                        }

                        popOver.show((javafx.scene.Node) event.getSource(), -6);

                        popOverHidden = false;
                        popOver.setCloseButtonEnabled(false);
                        popOver.setAutoFix(true);
                        popOver.setDetachable(false);

                        nodeDispSet.get(string).setStroke(Color.YELLOW);
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
                popOver.hide();
                popOverHidden = true;
                for (String string : nodeDispSet.keySet()) {
                    if (nodeDispSet.get(string) == event.getSource()) {
                        nodeDispSet.get(string).setStroke(Color.BLACK);
                    }
                }
            }
            event.consume();
        }
    };

    String floorState;

    /**
     * this just updates the map if the floor L2 button is clicked
     */
    @FXML
    public void floorL2ButtonOp(ActionEvent e) {
        floorState = floorL2Button.getText();
        currentFloor = Node.floorType.LEVEL_L2;

        updateMap();

    }

    /**
     * this just updates the map if the floor L1 button is clicked
     */
    @FXML
    public void floorL1ButtonOp(ActionEvent e) {
        floorState = floorL1Button.getText();
        currentFloor = Node.floorType.LEVEL_L1;

        updateMap();
    }

    /**
     * this just updates the map if the floor G button is clicked
     */
    @FXML
    public void floorGButtonOp(ActionEvent e) {
        floorState = floorGButton.getText();
        currentFloor = Node.floorType.LEVEL_G;

        updateMap();
    }

    /**
     * this just updates the map if the floor 1 button is clicked
     */
    @FXML
    public void floor1ButtonOp(ActionEvent e) {
        floorState = floor1Button.getText();
        currentFloor = Node.floorType.LEVEL_1;

        updateMap();
    }
    /**
     * this just updates the map if the floor 2 button is clicked
     */
    @FXML
    public void floor2ButtonOp(ActionEvent e) {
        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;

        updateMap();
    }
    /**
     * this just updates the map if the floor 3 button is clicked
     */
    @FXML
    public void floor3ButtonOp(ActionEvent e) {
        floorState = floor3Button.getText();
        currentFloor = Node.floorType.LEVEL_3;

        updateMap();
    }

    /**
     * this is the event handler for clicking on edges which will
     * open up the edge form with the start and end node filled in
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
                    edgeDispSet.get(edge.getID()).setStroke(Color.rgb(205, 35, 0, 0.99));
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
     * as well as panning the map
     */
    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                System.out.println("Mouse_Pressed");
                isDragging = false;
            } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                System.out.println("Drag_Detected");
                isDragging = true;
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                System.out.println("Mouse_Dragged");
                double offsetX = event.getSceneX() - orgSceneX;
                double offsetY = event.getSceneY() - orgSceneY;
                newTranslateX = orgTranslateX + offsetX;
                newTranslateY = orgTranslateY + offsetY;

                zoomForTranslate = zoomSlider.getValue();

                double translateSlopeX = X_SCALE * mapImage.getScaleX() * IMG_WIDTH;
                double translateSlopeY = Y_SCALE * mapImage.getScaleX() * IMG_HEIGHT;

                if (newTranslateX > (translateSlopeX - 1920) / 2)
                    newTranslateX = (translateSlopeX - 1920) / 2;
                if (newTranslateX < -(translateSlopeX - 1920) / 2)
                    newTranslateX = -(translateSlopeX - 1920) / 2;
                if (newTranslateY > (translateSlopeY - 1080) / 2)
                    newTranslateY = (translateSlopeY - 1080) / 2;
                if (newTranslateY < -(translateSlopeY - 1080) / 2)
                    newTranslateY = -(translateSlopeY - 1080) / 2;
                mapImage.setTranslateX(newTranslateX);
                mapImage.setTranslateY(newTranslateY);
                nodesEdgesPane.setTranslateX(newTranslateX);
                nodesEdgesPane.setTranslateY(newTranslateY);
            } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                //creates a new node at the location the mouse was clicked based
                System.out.println("ready to click");
                if (!isDragging) {
                    System.out.println("Mouse Clicked");
                    clearCircles();
                    //get the position the mouse was clicked
                    double x2Coord = event.getSceneX();
                    double y2Coord = event.getSceneY();
                    //scale it based on the zoom and add it too the translation of the nodes and edges pane descaled
                    double scaledx2Coord = (x2Coord - 960) / nodesEdgesPane.getScaleX() + (960 - newTranslateX / nodesEdgesPane.getScaleX());
                    double scaledy2Coord = (y2Coord - 540) / nodesEdgesPane.getScaleY() + (540 - newTranslateY / nodesEdgesPane.getScaleY());
                    System.out.println("x: " + x2Coord + " y: " + y2Coord);
                    //create the new node circle
                    newNodeCircle.setCenterX(scaledx2Coord);
                    newNodeCircle.setCenterY(scaledy2Coord);
                    newNodeCircle.setRadius(NODE_RADIUS);
                    newNodeCircle.setFill(Color.RED);
                    newNodeCircle.setStroke(Color.BLACK);
                    newNodeCircle.setStrokeType(StrokeType.INSIDE);
                    newNodeCircle.addEventHandler(MouseEvent.ANY, dragCircleHandler);

                    newNodeCircle.setVisible(true);
                    newNodeCircle.setDisable(false);
                    newNodeFloor = currentFloor;

                    // get the coordinates from the nodesEdgesPane location
                    double nodex2Coord = scaledx2Coord / X_SCALE + X_OFFSET;
                    double nodey2Coord = scaledy2Coord / Y_SCALE + Y_OFFSET;
                    if (!isNewNode) {
                        isNewNode = true;
                        newNodeForm();
                        mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorState);
                        mapBuilderNodeFormController.setFloor(currentFloor.toString());
                    } else {
                        mapBuilderNodeFormController.set2XYCoords(nodex2Coord, nodey2Coord, floorState);
                    }

                }
            }
        }
    };

    /**
     * this brings the basic add Edge/addNod/back buttons back to the screen
     * it also resets the nodeModify, firstSelect,secondSelect, edgeSelected, and modifyingNode
     */
    public void addOverlay() {
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/MapBuilderOverlay.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        nodeModify = null;
        clearCircles();
        firstSelect = null;
        secondSelect = null;
        mapBuilderOverlayController = loader.getController();
        mapBuilderOverlayController.startUp(mapBuilderController);
        edgeSelected = false;
        modifyingNode = false;
        isNewNode = false;
        nodeState = 0;
        formOverlayPane.setLeft(root);
    }

    MapBuilderNodeFormController mapBuilderNodeFormController;

    /**
     * brings in a fresh new node form to create a new node
     */
    public void newNodeForm() {
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/NodeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        mapBuilderNodeFormController = loader.getController();
        mapBuilderNodeFormController.startUp(mapBuilderController);
        formOverlayPane.setLeft(root);
    }

    /**
     * brings in a filled out node form for an existing node to be edited
     *
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
    public void newNodeForm(String nodeID, String nodeLongName,
                            double x2d, double y2d, double x3d, double y3d, String nodeFloor, String nodeBuilding, String nodeType,
                            Boolean isActive) {
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/NodeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
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
    public void newEdgeForm() {
        //TODO for edges
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/EdgeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
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
     *
     * @param edgeID
     * @param startNode
     * @param endNode
     * @param isActive
     */
    public void newEdgeForm(String edgeID, Node startNode,
                            Node endNode, Boolean isActive) {
        //TODO for edges
        Parent root;
        Stage stage;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/EdgeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
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
    public void updateMap() {

        nodesEdgesPane.getChildren().clear();
        nodeDispSet.clear();
        edgeDispSet.clear();
        getMap();
        drawEdges();
        nodesEdgesPane.getChildren().add(newNodeCircle);
        drawNodes();
        firstSelect = null;
        secondSelect = null;
        selectedEdge = null;

        if (pathDrawn) {
            drawPath(pathMade);
        }
        //addOverlay();
    }

    /**
     * resets any nodes or edges clicked on the anchor pane
     */
    public void clearCircles() {
        if (pathDrawn) resetPath();
        if (nodeModify != null) {
            //if we were modifying a new node
            nodeDispSet.get(nodeModify.getID()).setFill(Color.DODGERBLUE);
            if (toggleOn) { //resets 3D position
                nodeDispSet.get(nodeModify.getID()).setCenterX((nodeModify.getxDisplay() - X_OFFSET) * X_SCALE);
                nodeDispSet.get(nodeModify.getID()).setCenterY((nodeModify.getyDisplay() - Y_OFFSET) * Y_SCALE);
            } else { //resets 2D position
                nodeDispSet.get(nodeModify.getID()).setCenterX((nodeModify.getX() - X_OFFSET) * X_SCALE);
                nodeDispSet.get(nodeModify.getID()).setCenterY((nodeModify.getY() - Y_OFFSET) * Y_SCALE);
            }
            for (Edge modifiedEdge : nodeModify.getEdges()) {
                //resets the edges
                if (modifiedEdge.getStart().equals(nodeModify)) { // node was a start node
                    if (toggleOn) { //3D
                        edgeDispSet.get(modifiedEdge.getID()).setStartX((nodeModify.getxDisplay() - X_OFFSET) * X_SCALE);
                        edgeDispSet.get(modifiedEdge.getID()).setStartY((nodeModify.getyDisplay() - Y_OFFSET) * Y_SCALE);
                    } else { //2D
                        edgeDispSet.get(modifiedEdge.getID()).setStartX((nodeModify.getX() - X_OFFSET) * X_SCALE);
                        edgeDispSet.get(modifiedEdge.getID()).setStartY((nodeModify.getY() - Y_OFFSET) * Y_SCALE);
                    }
                } else if (modifiedEdge.getEnd().equals(nodeModify)) { //node was an end node
                    if (toggleOn) { //3D
                        edgeDispSet.get(modifiedEdge.getID()).setEndX((nodeModify.getxDisplay() - X_OFFSET) * X_SCALE);
                        edgeDispSet.get(modifiedEdge.getID()).setEndY((nodeModify.getyDisplay() - Y_OFFSET) * Y_SCALE);
                    } else { //2D
                        edgeDispSet.get(modifiedEdge.getID()).setEndX((nodeModify.getX() - X_OFFSET) * X_SCALE);
                        edgeDispSet.get(modifiedEdge.getID()).setEndY((nodeModify.getY() - Y_OFFSET) * Y_SCALE);
                    }
                }
            }
        }
        if (firstSelect != null) {
            //resets first edge selected color
            nodeDispSet.get(firstSelect.getID()).setFill(Color.DODGERBLUE);
        }
        if (secondSelect != null) {
            //resets second edge selected color
            nodeDispSet.get(secondSelect.getID()).setFill(Color.DODGERBLUE);
        }
        if (selectedEdge != null) {
            //resets selected Edge color
            edgeDispSet.get(selectedEdge.getID()).setStroke(Color.BLACK);
        }
        if (newNodeCircle != null) {
            //removes the new Node circle
            newNodeCircle.setVisible(false);
            newNodeCircle.setDisable(true);
            isNewNode = false;
        }
    }

    /**
     * Handles the pathfinding operation given destination and source.
     * this method reads the two search bars, then calls the algorithm, then calls
     * draw path with the returned path
     *
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    public Boolean searchButtonOp(ActionEvent e) {
        addOverlay();
        if (pathDrawn) {
            resetPath();
        }
        System.out.println("get path");
        // Get all nodes
        HashMap<String, Node> nodeSet = db.getAllNodes();

        // Declare source node, destination node, and get the typed in inputs for both search boxes
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
            // Destination has not been set, set search bar to red
            destinationSearchBar.setUnFocusColor(Color.rgb(255,0,0));
            ShakeTransition anim = new ShakeTransition(destinationSearchBar);
            anim.playFromStart();
            return false;
            //dstNode = nodeSet.get(endNode.getID());
        }

        Font font = new Font("verdana", 24.0);

        ArrayList<Node> path = Main.pathfindingContext.findPath(srcNode, dstNode);
        System.out.println(path);
        drawPath(path);
        this.path = path;
        return true;
    }

    /**
     * Parses the input from the destination text box and returns the node that we wanted
     *
     * @param string
     * @return the node corresponding to the input string
     */
    public Node parseSourceInput(String string) {
        Node aNode = new Node();
//        System.out.println("Input string: " + string);

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
     *
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
                    if (node.getLongName().compareTo(string) == 0) {
                        aNode = node;
                    }
                }
                break;
        }
        return aNode;
    }

    /**
     * Looks through entries in node database that match the type of input enum,
     * compares the distances to node,
     * returns the shortest one
     *
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
     * Used to draw the list of nodes returned by the Algorithm
     *
     * @param path List of Nodes to be drawn
     */
    //removed static hope it didn't break anything
    public void drawPath(ArrayList<Node> path) {
        Node currentNode = null, pastNode = null;

        if (pathMade != null) {
            resetPath();
        }
        this.pathMade = path;

        double maxXCoord = 0;
        double maxYCoord = 0;
        double minXCoord = 5000;
        double minYCoord = 3400;

        for (Node n : path) {
            //this gets the max and min x,y coordinates based on all the nodes in the path
            if (toggleOn) { //3D
                if (n.getxDisplay() < minXCoord) minXCoord = n.getxDisplay();
                if (n.getxDisplay() > maxXCoord) maxXCoord = n.getxDisplay();
                if (n.getyDisplay() < minYCoord) minYCoord = n.getyDisplay();
                if (n.getyDisplay() > maxYCoord) maxYCoord = n.getyDisplay();
            } else { //2D
                if (n.getX() < minXCoord) minXCoord = n.getX();
                if (n.getX() > maxXCoord) maxXCoord = n.getX();
                if (n.getY() < minYCoord) minYCoord = n.getY();
                if (n.getY() > maxYCoord) maxYCoord = n.getY();
            }

            pastNode = currentNode;
            currentNode = n;
            //nodeDispSet.get(currentNode.getID()).setFill(Color.rgb(250, 150, 0));
            if (path.get(0).equals(n)) {
                //start node is green
                nodeDispSet.get(currentNode.getID()).setFill(Color.GREEN);
            }
            if (path.get(path.size() - 1).equals(n)) {
                //end node is red
                nodeDispSet.get(currentNode.getID()).setFill(Color.RED);
            }
            for (Edge e : currentNode.getEdges()) {
                //color in all the edges and increase their width
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        edgeDispSet.get(e.getID()).setStroke(Color.rgb(250, 150, 0));
                        edgeDispSet.get(e.getID()).setStrokeWidth(5.0);
                        edgeDispSet.get(e.getID()).setVisible(true);
                        if (e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor) {
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

        //math to put in the auto translator
        System.out.println("MaxX: " + maxXCoord + "Min X: " + minXCoord
                + "Max Y: " + maxYCoord + "Min Y: " + minYCoord);
        double rangeX = maxXCoord - minXCoord;
        double rangeY = maxYCoord - minYCoord;


        double desiredZoomX = 1920 / (rangeX * X_SCALE);
        double desiredZoomY = 1080 / (rangeY * Y_SCALE);
        System.out.println("desired X zoom: " + desiredZoomX + " desired Zoom Y: " + desiredZoomY);

        double centerX = (maxXCoord + minXCoord) / 2;
        double centerY = (maxYCoord + minYCoord) / 2;
        //zooms in on path
        autoTranslateZoom(desiredZoomX, desiredZoomY, centerX, centerY);

        pathDrawn = true;
    }

    /**
     * Resets the path drawn based on the pathMade by going and decoloring the edges
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
                        if (e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor) {

                        } else {
                            edgeDispSet.get(e.getID()).setVisible(false);
                        }
                    }
                }
            }
        }
        pathDrawn = false;
    }

    /**
     * changes constants based off of whether we have a 2D map or a 3D map
     */
    @FXML
    public void mapToggleButtonOp(){
        if (mapToggleButton.isSelected()){
            //3D
            X_OFFSET = 0;
            Y_OFFSET = -19;
            X_SCALE = 1920.0 / 5000.0;
            Y_SCALE = 1065.216 / 2774.0;
            IMG_HEIGHT = 2774;
            zoomSlider.setMin(ZOOM_3D_MIN);
            zoomSlider.setValue(ZOOM_3D_MIN);
            toggleOn = true;
        } else {
            //2D
            X_OFFSET = -523;
            Y_OFFSET = 0;
            X_SCALE = 1588.235294 / 5000.0;
            Y_SCALE = 1080.0 / 3400.0;
            IMG_HEIGHT = 3400;
            zoomSlider.setMin(ZOOM_2D_MIN);
            zoomSlider.setValue(ZOOM_2D_MIN);
            toggleOn = false;
        }
        zoomForTranslate = zoomSlider.getValue();
        updateMap();
    }

    public Boolean getSourceFocus() {
        return sourceSearchBar.isFocused();
    }

    public Boolean getDestinationFocus() {
        return destinationSearchBar.isFocused();
    }

    public void removeFocus() {
        goButton.requestFocus();
    }


    /**
     * autotranslates the map and zooms in based on the coordinates it needs to fit between and a
     * desired zoom in the x and y directions
     * @param zoomX
     * @param zoomY
     * @param centerX
     * @param centerY
     */
    public void autoTranslateZoom(double zoomX, double zoomY, double centerX, double centerY) {

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
        double screenX = (centerX - X_OFFSET) * X_SCALE;
        double screenY = (centerY - Y_OFFSET) * Y_SCALE;
        System.out.println("Screen x: " + screenX + " Screen Y: " + screenY);

        double translateX = 960 - screenX;
        double translateY = 540 - screenY;
        double screenTranslateX = (translateX * zoom);
        double screenTranslateY = (translateY * zoom);
        System.out.println("translate X: " + translateX + " translate Y: " + translateY);

        double translateSlopeX = X_SCALE * mapImage.getScaleX() * IMG_WIDTH;
        double translateSlopeY = Y_SCALE * mapImage.getScaleX() * IMG_HEIGHT;
        if (screenTranslateX > (translateSlopeX - 1920) / 2)
            screenTranslateX = (translateSlopeX - 1920) / 2;
        if (screenTranslateX < -(translateSlopeX - 1920) / 2)
            screenTranslateX = -(translateSlopeX - 1920) / 2;
        if (screenTranslateY > (translateSlopeY - 1080) / 2)
            screenTranslateY = (translateSlopeY - 1080) / 2;
        if (screenTranslateY < -(translateSlopeY - 1080) / 2)
            screenTranslateY = -(translateSlopeY - 1080) / 2;

        System.out.println("Chosen translate X: " + screenTranslateX + " Chosen translate Y: " + screenTranslateY);
        mapImage.setTranslateX(screenTranslateX);
        mapImage.setTranslateY(screenTranslateY);
        nodesEdgesPane.setTranslateX(screenTranslateX);
        nodesEdgesPane.setTranslateY(screenTranslateY);
    }

    public void setFloorStyleClass(Node.floorType floor){

        switch(floor) {
            case LEVEL_1:
                floor1Button.getStyleClass().removeAll("floor-button");
                floor1Button.getStyleClass().add("highlight-floor-button");

                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_2:
                floor2Button.getStyleClass().removeAll("floor-button");
                floor2Button.getStyleClass().add("highlight-floor-button");


                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_3:
                floor3Button.getStyleClass().removeAll("floor-button");
                floor3Button.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_G:
                floorGButton.getStyleClass().removeAll("floor-button");
                floorGButton.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_L1:
                floorL1Button.getStyleClass().removeAll("floor_button");
                floorL1Button.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_L2:
                floorL2Button.getStyleClass().removeAll("floor_button");
                floorL2Button.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                break;
        }
    }
}