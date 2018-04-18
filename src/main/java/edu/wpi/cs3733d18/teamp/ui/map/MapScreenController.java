package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.JFXButton;
import com.sun.scenario.effect.Effect;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.math.*;

public class MapScreenController {

    private int X_OFFSET = -523;
    private int Y_OFFSET = 0;
    private double X_SCALE = 1588.235294 / 5000.0;
    private double Y_SCALE = 1080.0 / 3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int IMG_WIDTH = 5000;
    public int IMG_HEIGHT = 3400;
    private static final double ZOOM_3D_MIN = 1.013878875;
    private static final double ZOOM_2D_MIN = 1.208888889;
    private double zoomForTranslate = 0;


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
    private static ArrayList<Polygon> arrowDispSet = new ArrayList<>();
    private static ArrayList<String> arrowFloorSet = new ArrayList<>();
    private static HashMap<String, Line> edgeDispSet = new HashMap<>();
    private static ArrayList<Label> labelDispSet = new ArrayList<>();
    private ArrayList<Node> stairNodeSet = new ArrayList<Node>();
    private ArrayList<Node> recentStairNodeSet = new ArrayList<Node>();
    private ArrayList<Node> recentElevNodeSet = new ArrayList<Node>();


    DBSystem db = DBSystem.getInstance();

    Node.floorType currentFloor;
    String floorState;

    @FXML
    BorderPane buttonOverlayPane;

    @FXML
    BorderPane searchBarOverlayPane;

    @FXML
    AnchorPane labelPane;

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

    @FXML
    static PopOver popOver;
    Boolean popOverHidden = true;

    SearchBarOverlayController searchBarOverlayController = null;
    MapScreenController mapScreenController;

    /**
     * intializes values such as
     * adminMapViewController to this object
     * sets the current floor
     * initializes the zoom slider and binds it to the map
     * draws the initial screen and sets mouse events
     */
    @FXML
    public void onStartUp() {
        mapScreenController = this;

        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;
        zoomSlider.setValue(1);

        zoomSlider.setMin(1.20888889);
        zoomSlider.setValue(zoomSlider.getMin());
        zoomForTranslate = zoomSlider.getValue();

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

        searchBarOverlayController.setSourceSearchBar("Primary Kiosk");
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

        backButton.getScene().setRoot(root);
    }


    @FXML
    public void floorL2ButtonOp(ActionEvent e) {
        floorState = floorL2Button.getText();
        currentFloor = Node.floorType.LEVEL_L2;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floorL1ButtonOp(ActionEvent e) {
        floorState = floorL1Button.getText();
        currentFloor = Node.floorType.LEVEL_L1;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floorGButtonOp(ActionEvent e) {
        floorState = floorGButton.getText();
        currentFloor = Node.floorType.LEVEL_G;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor1ButtonOp(ActionEvent e) {
        floorState = floor1Button.getText();
        currentFloor = Node.floorType.LEVEL_1;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor2ButtonOp(ActionEvent e) {
        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor3ButtonOp(ActionEvent e) {
        floorState = floor3Button.getText();
        currentFloor = Node.floorType.LEVEL_3;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }


    /**
     * switch statement that determines which map is loaded in
     */
    public void getMap() {
        Image image;

        if (toggleOn) {
            X_OFFSET = 0;
            Y_OFFSET = -19;
            X_SCALE = 1920.0/5000.0;
            Y_SCALE = 1065.216/2774.0;
            IMG_HEIGHT = 2774;
            zoomSlider.setMin(ZOOM_3D_MIN);
            zoomSlider.setValue(ZOOM_3D_MIN);
            zoomForTranslate = zoomSlider.getValue();
            switch(floorState) {
                case "3":
                    image = new Image("/img/maps/3d/3-ICONS.png"); //TODO use this bit of information for image drawing
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
            X_OFFSET = -523;
            Y_OFFSET = 0;
            X_SCALE = 1588.235294/5000.0;
            Y_SCALE = 1080.0/3400.0;
            IMG_HEIGHT = 3400;
            zoomSlider.setMin(ZOOM_2D_MIN);
            zoomSlider.setValue(ZOOM_2D_MIN);
            zoomForTranslate = zoomSlider.getValue();
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


    double newTranslateX = 0;
    double newTranslateY = 0;

    /**
     * this lets the user scroll the wheel to move the zoom slider
     * This also readjusts the screen based on the bounds
     *
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s) {
        double newValue = (s.getDeltaY()) / 200.0 + zoomSlider.getValue();
        System.out.println("mouse scroll change: " + s.getDeltaY());
        System.out.println("source: " + s.getSource().toString());
        double change = 0;

        if ((s.getDeltaY() < 0 ) && (zoomSlider.getValue() != zoomSlider.getMin())) change  = 1;
        if ((s.getDeltaY() > 0 ) &&(zoomSlider.getValue() != zoomSlider.getMax())) change = 1;
        System.out.println("s.getDeltaY: " + s.getDeltaY());
        System.out.println("change: " + change);

        double mouseX = s.getSceneX();
        double mouseY = s.getSceneY();
        System.out.println("mouseX: " + mouseX + " mouseY: " + mouseY);

        double orgTranslateX = mapImage.getTranslateX();
        double orgTranslateY = mapImage.getTranslateY();
        System.out.println("orgTranslate X: " + orgTranslateX + " orgTranslate Y: " + orgTranslateY);

        double mouseAdjustX = (orgTranslateX + (IMG_WIDTH * zoomSlider.getValue() * X_SCALE *(mouseX/1920.0)));
        double mouseAdjustY = (orgTranslateY + (IMG_HEIGHT * zoomSlider.getValue() * Y_SCALE *(mouseY/1080.0)));
        System.out.println("mouse adjustX: " + mouseAdjustX + " mouse adjustY: " + mouseAdjustY);

        double imageCenterX = (orgTranslateX + (IMG_WIDTH * zoomSlider.getValue() * X_SCALE *0.5));
        double imageCenterY = (orgTranslateY + (IMG_HEIGHT * zoomSlider.getValue() * Y_SCALE *0.5));
        System.out.println(" image centerx : " + imageCenterX + " image centery: " + imageCenterY);

        double mouseChangeX = mouseAdjustX - imageCenterX;
        double mouseChangeY = mouseAdjustY - imageCenterY;
        System.out.println("Mouse ChangeX: " + mouseChangeX + " Mouse Change Y: " + mouseChangeY);

        newTranslateX = (orgTranslateX * zoomSlider.getValue()/zoomForTranslate) - (change * mouseChangeX * s.getDeltaY()/256.0);
        newTranslateY = (orgTranslateY * zoomSlider.getValue()/zoomForTranslate) - (change * mouseChangeY * s.getDeltaY()/256.0);
        System.out.println("new translate x: " + newTranslateX + " new translate Y: " + newTranslateY);

        zoomSlider.setValue(newValue);

        zoomForTranslate = zoomSlider.getValue();

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
            circle.setOnScroll(nodeScrollHandler);

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
            HashMap<String, Node> nodeSet;
            nodeSet = db.getAllNodes();
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                if (searchBarOverlayController.isSourceFocused()) {
                    clearStartNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.GREEN);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                    removeFocus();
                } else if (searchBarOverlayController.isDestinationFocused()) {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                        }
                    }
                    removeFocus();
                } else {
                    Boolean foundStair = false;
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string).equals(event.getSource())) {
                            for (int i = 0; i < stairNodeSet.size(); i += 2) {
                                System.out.println("entered for loop for stair nodes");
                                if (stairNodeSet.get(i).getID().equals(string)) {
                                    System.out.println("choose floor");
                                    currentFloor = stairNodeSet.get(i + 1).getFloor();
                                    floorState = currentFloor.toString();
                                    foundStair = true;
                                    updateMap();
                                    if (pathDrawn) {
                                        drawPath(pathMade);
                                    }
                                    break;
                                }
                            }
                            System.out.println("found stair state" + foundStair.toString());
                            if (!foundStair) {
                                clearEndNode();
                                Node node = nodeSet.get(string);
                                nodeDispSet.get(string).setFill(Color.RED);
                                searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                            }
                            foundStair = false;
                            break;
                        }
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_ENTERED && popOverHidden) {
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
                        Label nodeBuildingLabel = new Label("Building: "+ node.getBuilding().toString());
                        nodeTypeLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #0b2f5b; -fx-font-weight: 700; -fx-padding: 10px 10px 0 10px;");
                        nodeTypeLabel.setAlignment(Pos.CENTER);
                        nodeLongNameLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 0 10px;");
                        nodeBuildingLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 10px 10px;");
                        VBox popOverVBox = new VBox(nodeTypeLabel, nodeLongNameLabel, nodeBuildingLabel);
                        popOver = new PopOver(popOverVBox);
//                        popOver.show((javafx.scene.Node) event.getSource(), -5);
                        System.out.println("Popover width: " + nodeLongNameLabel.getWidth());
                        if (event.getSceneX() < 960) {
                            System.out.println(" Left side of screeNn: " + event.getSceneX() + " " + event.getSceneY());
                            popOver.show(/*(javafx.scene.Node)  event.getSource() */labelPane, event.getSceneX() + 5, event.getSceneY());
                            //popOver.setArrowLocation(ArrowLocation.LEFT_TOP);
                        }
                       else {
                            System.out.println(" Right side of screen " + event.getSceneX() + " " + event.getSceneY());
                            popOver.setArrowLocation(ArrowLocation.RIGHT_TOP);
                            popOver.show((javafx.scene.Node) event.getSource(), -5);
                            System.out.println("Popover X: " + popOver.getX() + " PopOver Y: " + popOver.getY());
                            System.out.println("Popover length: " + popOver.getWidth() + " popover height: " + popOver.getHeight());
                        }
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

    EventHandler<ScrollEvent> nodeScrollHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            System.out.println("handle node scroll event");
            zoomScrollWheel(event);
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
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                System.out.println("Mouse_Pressed");
                getMouseValue(event);
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
    public void updateMap() {
        nodeDispSet.clear();
        nodesEdgesPane.getChildren().clear();
        getMap();
        drawEdges();
        drawNodes();
    }

    ArrayList<Node> pathMade;

    /**
     * Used to draw the list of nodes returned by AStar
     *
     * @param path List of Nodes to be drawn
     */
    //removed static hope it didn't break anything
    public void drawPath(ArrayList<Node> path) {
        double width, height, angle;
        double distanceCounter = 0;



        Node currentNode = null, pastNode = null;
        if (pathMade != null) {
            resetPath();
        }

        stairNodeSet.clear();
        this.pathMade = path;

        double maxXCoord = 0;
        double maxYCoord = 0;
        double minXCoord = 5000;
        double minYCoord = 3400;
        Font font = new Font("verdana", 10.0);

        Label startLabel = new Label();
        Label endLabel = new Label();

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

            //checks if if the current node should go in stair set
            checkStairNodeSet(currentNode);

            //Draws the arrows
            if (!path.get(0).equals(n)) {
                if(toggleOn) {
                    width = currentNode.getxDisplay() - pastNode.getxDisplay();
                    height = currentNode.getyDisplay() - pastNode.getyDisplay();
                } else {
                    width = currentNode.getX() - pastNode.getX();
                    height = currentNode.getY() - pastNode.getY();
                }
                angle = Math.atan2(height , width);
                //increment the distanceCounter
                distanceCounter += currentNode.distanceBetweenNodes(pastNode);
                if(distanceCounter >= 175) {
                    distanceCounter = 0;

                    arrowFloorSet.add(currentNode.getFloor().toString());
                    if(toggleOn) {
                        drawTriangle(angle, pastNode.getxDisplay(), pastNode.getyDisplay());
                    } else {
                        drawTriangle(angle, pastNode.getX(), pastNode.getY());
                    }
                }
            }
            
            //set start node to Green and end node to red
            if (path.get(0).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.GREEN);

                if (toggleOn) {
                    startLabel.setLayoutX((n.getxDisplay() + 5 - X_OFFSET) * X_SCALE);
                    startLabel.setLayoutY((n.getyDisplay() - 40 - Y_OFFSET) * Y_SCALE);
                }
                else {
                    startLabel.setLayoutX((n.getX()+5- X_OFFSET)*X_SCALE);
                    startLabel.setLayoutY((n.getY()-40- Y_OFFSET)*Y_SCALE);
                }
                startLabel.setText(n.getLongName());
                startLabel.setFont(font);
                startLabel.toFront();
                labelDispSet.add(startLabel);
                nodesEdgesPane.getChildren().add(startLabel);

            } else if (path.get(path.size()-1).equals(n)) {
                nodeDispSet.get(currentNode.getID()).setFill(Color.RED);
                //if the last node was a stair or an elevator then it should check the else in the checkStairNode function
                if (currentNode.getType().equals(Node.nodeType.ELEV) || currentNode.getType().equals(Node.nodeType.STAI)){
                    addToStairNodeSet();
                }
                if(toggleOn) {
                    endLabel.setLayoutX((n.getxDisplay()+5- X_OFFSET)*X_SCALE);
                    endLabel.setLayoutY((n.getyDisplay()-34- Y_OFFSET)*Y_SCALE);
                }
                else {
                    endLabel.setLayoutX((n.getX()+5- X_OFFSET)*X_SCALE);
                    endLabel.setLayoutY((n.getY()-34- Y_OFFSET)*Y_SCALE);
                }
                endLabel.setText(n.getLongName());
                endLabel.setFont(font);
                endLabel.toFront();
                labelDispSet.add(endLabel);
                nodesEdgesPane.getChildren().add(endLabel);
            }
            //Color in the path appropriately
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
        //this sets the proper opacity for the arrows based on floor
        for(int i = 0; i < arrowDispSet.size(); i++) {
            System.out.println(arrowFloorSet.get(i));
            if(arrowFloorSet.get(i).equals(currentFloor.toString())) {
                arrowDispSet.get(i).setOpacity(1.0);
            } else {
                arrowDispSet.get(i).setOpacity(0.3);
            }
        }

        System.out.println("list of stair nodes: " + stairNodeSet.toString());
        minXCoord -= 200;
        minYCoord -= 400;
        maxXCoord += 200;
        maxYCoord += 100;
        double rangeX = maxXCoord - minXCoord;
        double rangeY = maxYCoord - minYCoord;

        double desiredZoomX = 1920/(rangeX * X_SCALE);
        double desiredZoomY = 1080/(rangeY * Y_SCALE);
        System.out.println("desired X zoom: " + desiredZoomX +  " desired Zoom Y: " + desiredZoomY);

        double centerX = (maxXCoord + minXCoord)/2;
        double centerY = (maxYCoord + minYCoord)/2;

        autoTranslateZoom(desiredZoomX, desiredZoomY, centerX, centerY);

        System.out.println(toggleOn.toString());

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

        x1 = initX + (9 * Math.cos(angle));
        y1 = initY + (9 * Math.sin(angle));

        x2 = initX + (4 * Math.cos(angle - (2 * Math.PI / 3)));
        y2 = initY + (4 * Math.sin(angle - (2 * Math.PI / 3)));

        x3 = initX + (4 * Math.cos(angle + (2 * Math.PI / 3)));
        y3 = initY + (4 * Math.sin(angle + (2 * Math.PI / 3)));


        arrow.getPoints().addAll(new Double[]{
                initX, initY,
                x2, y2,
                x1, y1,
                x3, y3});
        arrow.setFill(Color.rgb(200, 30, 0));

        nodesEdgesPane.getChildren().add(arrow);

        arrowDispSet.add(arrow);
    }



    public void checkStairNodeSet(Node currentNode){

        if (currentNode.getType().equals(Node.nodeType.STAI)){
            recentElevNodeSet.clear();
            recentStairNodeSet.add(currentNode);
        } else if (currentNode.getType().equals(Node.nodeType.ELEV)){
            recentStairNodeSet.clear();
            recentElevNodeSet.add(currentNode);
        } else{
            addToStairNodeSet();
        }
    }

    public void addToStairNodeSet(){
        if (recentStairNodeSet.size() > 1){
            stairNodeSet.add(recentStairNodeSet.get(0));
            stairNodeSet.add(recentStairNodeSet.get(recentStairNodeSet.size() - 1));
        } else if (recentElevNodeSet.size() > 1){
            stairNodeSet.add(recentElevNodeSet.get(0));
            stairNodeSet.add(recentElevNodeSet.get(recentElevNodeSet.size() - 1));
        }
        recentElevNodeSet.clear();
        recentStairNodeSet.clear();
    }


    /**
     * Used to draw the list of nodes returned by AStar
     */
    public void resetPath() {
        Node currentNode = null, pastNode = null;
        Circle waypoint;
        Line line;
        stairNodeSet.clear();
        for (Node n : pathMade) {
            pastNode = currentNode;
            currentNode = n;
            //nodeDispSet.get(n.getID()).setFill(Color.DODGERBLUE);

            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {

                        edgeDispSet.get(e.getID()).setVisible(false);
                    }
                }
            }
            for (Polygon p : arrowDispSet) {
                p.setVisible(false);
                p.setPickOnBounds(false);
            }
            arrowDispSet.clear();
            arrowFloorSet.clear();
            for(Label l : labelDispSet) {
                l.setVisible(false);
                l.setPickOnBounds(false);
                nodesEdgesPane.getChildren().remove(l);
            }
            labelDispSet.clear();
        }
    }

    public void setToggleOn(Boolean toggleOn) {
        this.toggleOn = toggleOn;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }

    }

    public void clearStartNode() {
        if (pathDrawn) resetPath();
        for (Circle c : nodeDispSet.values()) {
            if (c.getFill().equals(Color.GREEN)) {
                c.setFill(Color.DODGERBLUE);
            }

        }
    }

    public void clearEndNode() {
        if (pathDrawn) resetPath();
        for (Circle c : nodeDispSet.values()) {
            if (c.getFill().equals(Color.RED)) {
                c.setFill(Color.DODGERBLUE);
            }

        }
    }

    public void removeFocus(){
        searchBarOverlayController.setSearchButtonFocus();
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


    public void setFloorStyleClass(Node.floorType floor){

        switch(floor){
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
