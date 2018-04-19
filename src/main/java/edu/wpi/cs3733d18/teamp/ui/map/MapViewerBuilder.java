package edu.wpi.cs3733d18.teamp.ui.map;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MapViewerBuilder implements Initializable{

    // Symbolic Constants
    private static final int VIEWPORT_SIZE = 800;
    private int X_OFFSET = 160;
    private int Y_OFFSET = -15;
    private int Z_OFFSET = -2720;
    private double X_SCALE = 1588.235294 / 5000.0;
    private double Y_SCALE = 1080.0 / 3400.0;
    private static final double NODE_RADIUS = 10.0;
    private static final Color lightColor = Color.rgb(244, 255, 250);
    private static final Color buildingColor = Color.rgb(255, 255, 255);

    private Group root;
    private PointLight pointLight;

    private static final int ROTATION_SPEED = 2;
    private static final int PAN_SPEED = 10;

    // Changeable global variables
    private double mouseInSceneX;
    private double mouseInSceneY;
    private int curXRotation;
    private int curYRotation;
    private int curZRotation;
    private double curXTranslation;
    private double curYTranslation;
    private double curZTranslation;
    private double curScale;
    private double curZoom;

    // Searchbar overlay controller
    SearchBarOverlayController searchBarOverlayController = null;
    MapScreenController mapScreenController;

    // DB instance
    DBSystem db = DBSystem.getInstance();

    // Display hashmaps
    private static HashMap<String, Sphere> nodeDispSet = new HashMap<>();
    private static HashMap<String, Line> edgeDispSet = new HashMap<>();
    private Boolean pathDrawn = false;
    ArrayList<Node> pathMade;
    Node.floorType currentFloor;
    String floorState;

    // FXML variables
    @FXML
    AnchorPane threeDAnchorPane;

    @FXML
    AnchorPane nodeAnchorPane;

    @FXML
    BorderPane buttonOverlayPane;

    @FXML
    BorderPane searchbarBorderPane;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Load initial model
        URL pathName = getClass().getResource("/models/B&WWholeFloor.obj");
        Group group = buildScene(pathName);
        group.setScaleX(2);
        group.setScaleY(2);
        group.setScaleZ(2);
        group.setTranslateX(500);
        group.setTranslateY(100);
        threeDAnchorPane.getChildren().add(group);

        // Add mouse handler
        threeDAnchorPane.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);

        // Add searchbar overlay
        addOverlay();
    }

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
        //searchBarOverlayController.startUp(mapScreenController);
        searchbarBorderPane.setTop(root);
    }

    /**
     * this is the event handler for clicking on the map to create a node
     */
    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;
        @Override
        public void handle(MouseEvent event) {
            MeshView mv = getMesh();

            // Left Mouse Button, Rotating
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    //System.out.println("Mouse_Pressed");
                    mouseInSceneX = event.getSceneX();
                    mouseInSceneY = event.getSceneY();
                    isDragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    //System.out.println("Drag_Detected");
                    isDragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    //System.out.println("Mouse_Dragged");
                    double newMouseInSceneX = event.getSceneX();
                    double newMouseInSceneY = event.getSceneY();
                    double xChange = newMouseInSceneX - mouseInSceneX;
                    double yChange = newMouseInSceneY - mouseInSceneY;

                    //System.out.println("Previous getSceneX: "+mouseInSceneX+" xChange: "+xChange);
                    // Change Rotation Y
                    if (xChange < 0) { // Rotate clockwise
                        curYRotation += ROTATION_SPEED;
                        threeDAnchorPane.getTransforms().setAll(new Rotate(curYRotation, 1920/2, 1080/2, 0, Rotate.Y_AXIS),
                                new Rotate(curXRotation, 1920/2, 1080/2, 0, Rotate.X_AXIS)); // Rotate Map
                    } else if (xChange > 0) { // Rotate counterclockwise
                        curYRotation -= ROTATION_SPEED;
                        threeDAnchorPane.getTransforms().setAll(new Rotate(curYRotation, 1920/2, 1080/2, 0, Rotate.Y_AXIS),
                                new Rotate(curXRotation, 1920/2, 1080/2, 0, Rotate.X_AXIS)); // Rotate Map
                    }
                    // Change Rotation X
                    if (yChange < 0) {
                        curXRotation += ROTATION_SPEED;
                        threeDAnchorPane.getTransforms().setAll(new Rotate(curYRotation, 1920/2, 1080/2, 0, Rotate.Y_AXIS),
                                new Rotate(curXRotation, 1920/2, 1080/2, 0, Rotate.X_AXIS)); // Rotate Map
                    } else if (yChange > 0) {
                        curXRotation -= ROTATION_SPEED;
                        threeDAnchorPane.getTransforms().setAll(new Rotate(curYRotation, 1920/2, 1080/2, 0, Rotate.Y_AXIS),
                                new Rotate(curXRotation, 1920/2, 1080/2, 0, Rotate.X_AXIS)); // Rotate Map
                    }
                    mouseInSceneX = newMouseInSceneX;
                    mouseInSceneY = newMouseInSceneY;
                }
            }

            // Right Mouse Button, Panning
            else if (event.getButton() == MouseButton.SECONDARY) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    //System.out.println("Mouse_Pressed");
                    mouseInSceneX = event.getSceneX();
                    mouseInSceneY = event.getSceneY();
                    isDragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    //System.out.println("Drag_Detected");
                    isDragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    //System.out.println("Mouse_Dragged");
                    double newMouseInSceneX = event.getSceneX();
                    double newMouseInSceneY = event.getSceneY();
                    double xChange = newMouseInSceneX - mouseInSceneX;
                    double yChange = newMouseInSceneY - mouseInSceneY;

                    // Change Panning X
                    if (xChange < 0) { // Pan left
                        curXTranslation -= PAN_SPEED;
                        threeDAnchorPane.setTranslateX(curXTranslation); // Map panning
                    } else if (xChange > 0) { // Pan right
                        curXTranslation += PAN_SPEED;
                        threeDAnchorPane.setTranslateX(curXTranslation); // Map panning
                    }
                    // Change Panning Y
                    if (yChange < 0) { // Pan down
                        curYTranslation -= PAN_SPEED;
                        threeDAnchorPane.setTranslateY(curYTranslation); // Map panning
                    } else if (yChange > 0) { // Pan up
                        curYTranslation += PAN_SPEED;
                        threeDAnchorPane.setTranslateY(curYTranslation); // Map panning
                    }

                    mouseInSceneX = newMouseInSceneX;
                    mouseInSceneY = newMouseInSceneY;
                }
            }
        }
    };

    @FXML
    public void floorL2ButtonOp(ActionEvent e) {
        URL pathName = getClass().getResource("/models/B&WL2Floor.obj");
        switchMesh(pathName);
        currentFloor = Node.floorType.LEVEL_L2;
        updateMap();
    }

    @FXML
    public void floorL1ButtonOp(ActionEvent e) {
        URL pathName = getClass().getResource("/models/B&WL1Floor.obj");
        switchMesh(pathName);
        currentFloor = Node.floorType.LEVEL_L1;
        updateMap();
    }

    @FXML
    public void floorGButtonOp(ActionEvent e) {
        URL pathName = getClass().getResource("/models/B&WGFloor.obj");
        switchMesh(pathName);
        currentFloor = Node.floorType.LEVEL_G;
        updateMap();
    }

    @FXML
    public void floor1ButtonOp(ActionEvent e) {
        URL pathName = getClass().getResource("/models/B&W1stFloor.obj");
        switchMesh(pathName);
        currentFloor = Node.floorType.LEVEL_1;
        updateMap();
    }

    @FXML
    public void floor2ButtonOp(ActionEvent e) {
        URL pathName = getClass().getResource("/models/B&W2ndFloor.obj");
        switchMesh(pathName);
        currentFloor = Node.floorType.LEVEL_2;
        updateMap();
    }

    @FXML
    public void floor3ButtonOp(ActionEvent e) {
        URL pathName = getClass().getResource("/models/B&W3rdFloor.obj");
        switchMesh(pathName);
        currentFloor = Node.floorType.LEVEL_3;
        updateMap();
    }

    /**
     * this lets the user scroll the wheel to scale the 3D model
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s){
        double newZoom = (s.getDeltaY()); // Get value from scroll wheel
        if (newZoom > 0 && curScale + 5 <= 100) { // Zoom in (already zoomed in)
            curScale += 5;
            threeDAnchorPane.setScaleX(curScale);
            threeDAnchorPane.setScaleY(curScale);
            threeDAnchorPane.setScaleZ(curScale);
        }
        else if (newZoom < 0 && curScale - 5 > 0) { // Zoomed out (already zoomed in)
            curScale -= 5;
            threeDAnchorPane.setScaleX(curScale);
            threeDAnchorPane.setScaleY(curScale);
            threeDAnchorPane.setScaleZ(curScale);
        }
        curZoom = newZoom; // Set next zoom value to compare to
    }

    static MeshView[] loadMeshView(URL fileName) {
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(fileName);

        return importer.getImport();
    }

    private Group buildScene(URL fileName) {
        MeshView[] meshViews = loadMeshView(fileName);
        for (int i = 0; i < meshViews.length; i++) {
            curXTranslation = (VIEWPORT_SIZE / 2);
            curYTranslation = (VIEWPORT_SIZE / 2);
            curZTranslation = (VIEWPORT_SIZE / 2);
            curScale = 15;
            meshViews[i].setTranslateX(curXTranslation);
            meshViews[i].setTranslateY(curYTranslation);
            meshViews[i].setTranslateZ(curZTranslation);
            meshViews[i].setScaleX(curScale);
            meshViews[i].setScaleY(curScale);
            meshViews[i].setScaleZ(curScale);

            /*PhongMaterial sample = new PhongMaterial(buildingColor);
            sample.setSpecularColor(lightColor);
            sample.setSpecularPower(16);
            meshViews[i].setMaterial(sample);*/

            curXRotation = 20;
            curYRotation = 0;
            curZRotation = 0;
            meshViews[i].getTransforms().setAll(/*new Rotate(38, Rotate.Z_AXIS),*/ new Rotate(curXRotation, Rotate.X_AXIS));
        }


        pointLight = new PointLight(lightColor);
        pointLight.setTranslateX(VIEWPORT_SIZE*3/4);
        pointLight.setTranslateY(VIEWPORT_SIZE/2);
        pointLight.setTranslateZ(VIEWPORT_SIZE/2);
        PointLight pointLight2 = new PointLight(lightColor);
        pointLight2.setTranslateX(VIEWPORT_SIZE*1/4);
        pointLight2.setTranslateY(VIEWPORT_SIZE*3/4);
        pointLight2.setTranslateZ(VIEWPORT_SIZE*3/4);
        PointLight pointLight3 = new PointLight(lightColor);
        pointLight3.setTranslateX(VIEWPORT_SIZE*5/8);
        pointLight3.setTranslateY(VIEWPORT_SIZE/2);
        pointLight3.setTranslateZ(0);

        Color ambientColor = Color.rgb(80, 80, 80, 0);
        AmbientLight ambient = new AmbientLight(ambientColor);

        root = new Group(meshViews);
        root.getChildren().add(pointLight);
        root.getChildren().add(pointLight2);
        root.getChildren().add(pointLight3);
        root.getChildren().add(ambient);

        return root;
    }

    /**
     * Gets mesh value from group hierarchy for convenience
     * @return The MeshView we are looking for
     */
    private MeshView getMesh() {
        Group group = (Group)threeDAnchorPane.getChildren().get(0); // TODO index needs to be incremented whenever a new element is added to the pane
        return (MeshView)group.getChildren().get(0);
    }

    /**
     * Loads in a new mesh when switching floors
     */
    @FXML
    public void switchMesh(URL fileName) {
        threeDAnchorPane.getChildren().remove(0);
        Group newRoot = buildScene(fileName);
        newRoot.setScaleX(2);
        newRoot.setScaleY(2);
        newRoot.setScaleZ(2);
        newRoot.setTranslateX(500);
        newRoot.setTranslateY(100);
        threeDAnchorPane.getChildren().add(newRoot);
    }

    /**
     * Draws all of the nodes on the map depending on which map is loaded
     */
    public void draw3DNodes() {
        HashMap<String, Node> nodeSet;

        nodeSet = db.getAllNodes();
        System.out.println("drawing nodes");
        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_standard.png").toExternalForm()));
        MeshView mv = getMesh();
        for (Node node : nodeSet.values()) {
            if (node.getFloor() == currentFloor && node.getType() != Node.nodeType.HALL) {
                Sphere sphere = new Sphere(NODE_RADIUS);
                threeDAnchorPane.getChildren().add(sphere);
                sphere.setTranslateX((node.getX() - X_OFFSET) * X_SCALE);
                sphere.setTranslateY(mv.getTranslateY() - Y_OFFSET);
                sphere.setTranslateZ((-node.getY() - Z_OFFSET) * Y_SCALE);
                sphere.getTransforms().setAll(new Rotate(curYRotation, mv.getTranslateX() - sphere.getTranslateX(), mv.getTranslateY() - sphere.getTranslateY(), mv.getTranslateZ() - sphere.getTranslateZ(), Rotate.Y_AXIS),
                        new Rotate(curXRotation, mv.getTranslateX() - sphere.getTranslateX(), mv.getTranslateY() - sphere.getTranslateY(), mv.getTranslateZ() - sphere.getTranslateZ(), Rotate.X_AXIS));
                //System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
                sphere.setMaterial(phongMaterial);
                //circle.setStroke(Color.BLACK);
                //circle.setStrokeType(StrokeType.INSIDE);
                if (!node.getActive()) {
                    sphere.setOpacity(0.5);
                    //circle.setFill(Color.GRAY);
                }
                sphere.addEventHandler(MouseEvent.ANY, nodeClickHandler);
//                circle.setOnMouseClicked(clickCallback());

                String label = node.getID();
                nodeDispSet.put(label, sphere);

//                if(node.getType() == Node.nodeType.KIOS) {
//                    File file = new File("main/kiosk/resources/img/pip.png");
//                    Image pip = new Image(file.toURI().toString());
//                    ImageView position = new ImageView(pip);
//                    position.setX((node.getX() - X_OFFSET) * X_SCALE);
//                    position.setY((node.getY() - Y_OFFSET - 10) * Y_SCALE);
//                    kioskPip = position;
//                }
            } else {
                Sphere sphere = new Sphere(0);
                threeDAnchorPane.getChildren().add(sphere);
                sphere.setTranslateX((node.getX() - X_OFFSET) * X_SCALE);
                sphere.setTranslateY(curYTranslation);
                sphere.setTranslateZ((-node.getY() - Z_OFFSET) * Y_SCALE);
                //System.out.println("Center X: " + circle.getCenterX() + "Center Y: " + circle.getCenterY());
                //circle.setFill(Color.DODGERBLUE);
                //circle.setStroke(Color.BLACK);
                //circle.setStrokeType(StrokeType.INSIDE);
                //circle.addEventHandler(MouseEvent.ANY, nodeClickHandler);

                String label = node.getID();
                nodeDispSet.put(label, sphere);
            }
        }
        System.out.println("Printed All Nodes");
    }

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
                            //nodeDispSet.get(string).setFill(Color.GREEN);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                } else if (searchBarOverlayController.isDestinationFocused()) {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            //nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                        }
                    }
                } else if (!firstSelected) {
                    clearStartNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            //nodeDispSet.get(string).setFill(Color.GREEN);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                    firstSelected = true;
                } else {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            //nodeDispSet.get(string).setFill(Color.RED);
                            searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                        }
                    }
                    firstSelected = false;
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_ENTERED) { // TODO Check zoom level to prevent graphical glitches
                System.out.println("MOUSE_ENTERED event at " + event.getSource());
                for (String string : nodeDispSet.keySet()) {
                    if (nodeDispSet.get(string) == event.getSource()) {
                        /*if (popOver != null && popOver.getOpacity() == 0) {
                            popOver.hide();
                            popOver = null;
                        }*/
                        Node node = nodeSet.get(string);
                        Label nodeTypeLabel = new Label(node.getType().toString().toUpperCase());
                        Label nodeLongNameLabel = new Label("Name: " + node.getLongName());
                        Label nodeBuildingLabel = new Label("Building: "+ node.getBuilding().toString());
                        nodeTypeLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #0b2f5b; -fx-font-weight: 700; -fx-padding: 10px 10px 0 10px;");
                        nodeTypeLabel.setAlignment(Pos.CENTER);
                        nodeLongNameLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 0 10px;");
                        nodeBuildingLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 10px 10px;");
                        VBox popOverVBox = new VBox(nodeTypeLabel, nodeLongNameLabel, nodeBuildingLabel);
//                        popOverVBox.getParent().setStyle("-fx-effect: dropshadow(gaussian, BLACK, 10, 0, 0, 1);  ");
                        //popOver = new PopOver(popOverVBox);
                        //popOver.show((javafx.scene.Node) event.getSource());
                        //popOverHidden = false;
                        //popOver.setCloseButtonEnabled(false);
//                        popOver.setCornerRadius(20);
                        //popOver.setAutoFix(true);
                        //popOver.setDetachable(false);
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
                //popOver.hide();
                //popOverHidden = true;
            }
            event.consume();
        }
    };

    /**
     * this clears the anchor pane, draws the edges and the nodes and loads in the new map
     * this also brings in the basic overlays
     */
    public void updateMap() {
        nodeDispSet.clear();
        nodeAnchorPane.getChildren().clear();
        draw3DNodes();
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
            //nodeDispSet.get(n.getID()).setFill(Color.DODGERBLUE);

            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        edgeDispSet.get(e.getID()).setStroke(Color.BLACK);
                        edgeDispSet.get(e.getID()).setStrokeWidth(0);
                        if (e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor) {
                            edgeDispSet.get(e.getID()).setStrokeWidth(0);
                        }
                    }
                }
            }
        }
    }

    public void clearStartNode() {
        if (pathDrawn) resetPath();
        for (Sphere s: nodeDispSet.values()) {
            /*if (c.getFill().equals(Color.GREEN)) {
                c.setFill(Color.DODGERBLUE);
            }*/
        }
    }

    public void clearEndNode() {
        if (pathDrawn) resetPath();
        for (Sphere s : nodeDispSet.values()) {
            /*if (s.getFill().equals(Color.RED)) {
                s.setFill(Color.DODGERBLUE);
            }*/
        }
    }

}
