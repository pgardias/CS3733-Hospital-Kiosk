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
import javafx.geometry.Point3D;
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
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ThreeDMapScreenController implements Initializable{

    // Symbolic Constants
    private double X_SCALE = 1588.235294 / 5000.0 * 0.53;
    private double Z_SCALE = 1080.0 / 3400.0 * 0.53;
    private static final double NODE_RADIUS = 5.0;
    private static final Color lightColor = Color.rgb(150, 150, 150);

    private int X_OFFSET = 300;
    private int Y_OFFSET = -170;
    private int Z_OFFSET = -4500;

    private Group root;
    private PointLight pointLight;

    private static final int ROTATION_SPEED = 2;
    private static final int PAN_SPEED = 10;

    // Position variables
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
    private int curXOffset;
    private int curZOffset;

    // Searchbar overlay controller
    SearchBarOverlayController searchBarOverlayController = null;

    // DB instance
    DBSystem db = DBSystem.getInstance();

    // Display hashmaps
    private static ArrayList<MeshView[]> allModels = new ArrayList<>();
    private static ArrayList<PhongMaterial> allTextures = new ArrayList<>();
    private static Group allLights = new Group();
    private static ArrayList<Integer> floorOffsets = new ArrayList<>();
    private static HashMap<String, Sphere> nodeDispSet = new HashMap<>();
    private static ArrayList<Polygon> arrowDispSet = new ArrayList<>();
    private static ArrayList<String> arrowFloorSet = new ArrayList<>();
    private static HashMap<String, Cylinder> edgeDispSet = new HashMap<>();
    private static ArrayList<Line> lineDispSet = new ArrayList<>();
    private static ArrayList<Label> labelDispSet = new ArrayList<>();
    private ArrayList<Node> stairNodeSet = new ArrayList<Node>();
    private ArrayList<Node> recentStairNodeSet = new ArrayList<Node>();
    private ArrayList<Node> recentElevNodeSet = new ArrayList<Node>();
    private Boolean pathDrawn = false;
    ArrayList<Node> pathMade;
    Node.floorType currentFloor;
    String floorState;

    // Node Groups
    Group L2Nodes = new Group();
    Group L1Nodes = new Group();
    Group GNodes = new Group();
    Group FirstNodes = new Group();
    Group SecondNodes = new Group();
    Group ThirdNodes = new Group();

    // Colors
    PhongMaterial fillBlue;
    PhongMaterial fillGreen;
    PhongMaterial fillRed;
    PhongMaterial fillOrange;
    PhongMaterial fillPurple;

    // Floor visibility
    private Boolean L2Visible = true;
    private Boolean L1Visible = true;
    private Boolean GVisible = true;
    private Boolean FirstVisible = true;
    private Boolean SecondVisible = true;
    private Boolean ThirdVisible = true;

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

    @FXML
    JFXButton defaultButton;

    @FXML
    JFXButton topDownButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Add mouse handler
        threeDAnchorPane.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);

        // Add searchbar overlay
        addOverlay();

        // Load color materials
        fillBlue = new PhongMaterial();
        fillBlue.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_standard.png").toExternalForm()));
        fillGreen = new PhongMaterial();
        fillGreen.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_start.png").toExternalForm()));
        fillRed = new PhongMaterial();
        fillRed.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_end.png").toExternalForm()));
        fillOrange = new PhongMaterial();
        fillOrange.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_path.png").toExternalForm()));
        fillPurple = new PhongMaterial();
        fillPurple.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_stair.png").toExternalForm()));

        // Load all models
        curScale = 1;
        URL pathName = getClass().getResource("/models/B&WL2Floor.obj");
        allModels.add(loadMeshView(pathName));
        pathName = getClass().getResource("/models/B&WL1Floor.obj");
        allModels.add(loadMeshView(pathName));
        pathName = getClass().getResource("/models/B&WGFloor.obj");
        allModels.add(loadMeshView(pathName));
        pathName = getClass().getResource("/models/B&W1stFloor.obj");
        allModels.add(loadMeshView(pathName));
        pathName = getClass().getResource("/models/B&W2ndFloor.obj");
        allModels.add(loadMeshView(pathName));
        pathName = getClass().getResource("/models/B&W3rdFloor.obj");
        allModels.add(loadMeshView(pathName));

        // Load all model textures
        PhongMaterial floorL2Texture = new PhongMaterial();
        floorL2Texture.setDiffuseMap(new Image(getClass().getResource("/models/textures/L2Floor_UV.png").toExternalForm()));
        allTextures.add(floorL2Texture);
        PhongMaterial floorL1Texture = new PhongMaterial();
        floorL1Texture.setDiffuseMap(new Image(getClass().getResource("/models/textures/L1Floor_UV.png").toExternalForm()));
        allTextures.add(floorL1Texture);
        PhongMaterial floorGTexture = new PhongMaterial();
        floorGTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/groundFloor_UV.png").toExternalForm()));
        allTextures.add(floorGTexture);
        PhongMaterial floor1stTexture = new PhongMaterial();
        floor1stTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/1stFloor_UV.png").toExternalForm()));
        allTextures.add(floor1stTexture);
        PhongMaterial floor2ndTexture = new PhongMaterial();
        floor2ndTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/2ndFloor_UV.png").toExternalForm()));
        allTextures.add(floor2ndTexture);
        PhongMaterial floor3rdTexture = new PhongMaterial();
        floor3rdTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/3rdFloor_UV.png").toExternalForm()));
        allTextures.add(floor3rdTexture);

        // Build scene
        currentFloor = Node.floorType.LEVEL_L2;
        Group group = buildScene();
        drawEdges();
        group.setScaleX(2);
        group.setScaleY(2);
        group.setScaleZ(2);
        group.setTranslateX(500);
        group.setTranslateY(100);
        threeDAnchorPane.getChildren().add(group);
        threeDAnchorPane.getChildren().add(allLights);
        //draw3DNodes();
        curScale = 1;
    }

    private void addOverlay() {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/map/SearchBarOverlay.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        searchBarOverlayController = loader.getController();
        searchBarOverlayController.startUp3D(this);
        searchbarBorderPane.setTop(root);
    }

    /**
     * this is the event handler for clicking on the map to create a node
     */
    private EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;
        @Override
        public void handle(MouseEvent event) {

            // Left Mouse Button, Rotating
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    mouseInSceneX = event.getSceneX();
                    mouseInSceneY = event.getSceneY();
                    isDragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    isDragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    double newMouseInSceneX = event.getSceneX();
                    double newMouseInSceneY = event.getSceneY();
                    double xChange = newMouseInSceneX - mouseInSceneX;
                    double yChange = newMouseInSceneY - mouseInSceneY;

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
                    mouseInSceneX = event.getSceneX();
                    mouseInSceneY = event.getSceneY();
                    isDragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    isDragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
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
        currentFloor = Node.floorType.LEVEL_L2;
        MeshView mv = getFloor(currentFloor);
        if (L2Visible) {
            mv.setOpacity(0);
            L2Visible = false;
            hideNodes(currentFloor);
            floorL2Button.getStyleClass().removeAll("highlight-floor-button");
            floorL2Button.getStyleClass().add("floor-buttons");

        }
        else {
            mv.setOpacity(1);
            L2Visible = true;
            unhideNodes(currentFloor);
            floorL2Button.getStyleClass().removeAll("floor-buttons");
            floorL2Button.getStyleClass().add("highlight-floor-button");
        }
    }

    @FXML
    public void floorL1ButtonOp(ActionEvent e) {
        currentFloor = Node.floorType.LEVEL_L1;
        MeshView mv = getFloor(currentFloor);
        if (L1Visible) {
            mv.setOpacity(0);
            L1Visible = false;
            hideNodes(currentFloor);
            floorL1Button.getStyleClass().removeAll("highlight-floor-button");
            floorL1Button.getStyleClass().add("floor-buttons");
        }
        else {
            mv.setOpacity(1);
            L1Visible = true;
            unhideNodes(currentFloor);
            floorL1Button.getStyleClass().removeAll("floor-buttons");
            floorL1Button.getStyleClass().add("highlight-floor-button");
        }
    }

    @FXML
    public void floorGButtonOp(ActionEvent e) {
        currentFloor = Node.floorType.LEVEL_G;
        MeshView mv = getFloor(currentFloor);
        if (GVisible) {
            mv.setOpacity(0);
            GVisible = false;
            hideNodes(currentFloor);
            floorGButton.getStyleClass().removeAll("highlight-floor-button");
            floorGButton.getStyleClass().add("floor-buttons");
        }
        else {
            mv.setOpacity(1);
            GVisible = true;
            unhideNodes(currentFloor);
            floorGButton.getStyleClass().removeAll("floor-buttons");
            floorGButton.getStyleClass().add("highlight-floor-button");
        }
    }

    @FXML
    public void floor1ButtonOp(ActionEvent e) {
        currentFloor = Node.floorType.LEVEL_1;
        MeshView mv = getFloor(currentFloor);
        if (FirstVisible) {
            mv.setOpacity(0);
            FirstVisible = false;
            hideNodes(currentFloor);
            floor1Button.getStyleClass().removeAll("highlight-floor-button");
            floor1Button.getStyleClass().add("floor-buttons");
        }
        else {
            mv.setOpacity(1);
            FirstVisible = true;
            unhideNodes(currentFloor);
            floor1Button.getStyleClass().removeAll("floor-buttons");
            floor1Button.getStyleClass().add("highlight-floor-button");
        }
    }

    @FXML
    public void floor2ButtonOp(ActionEvent e) {
        currentFloor = Node.floorType.LEVEL_2;
        MeshView mv = getFloor(currentFloor);
        if (SecondVisible) {
            mv.setOpacity(0);
            SecondVisible = false;
            hideNodes(currentFloor);
            floor2Button.getStyleClass().removeAll("highlight-floor-button");
            floor2Button.getStyleClass().add("floor-buttons");

        }
        else {
            mv.setOpacity(1);
            SecondVisible = true;
            unhideNodes(currentFloor);
            floor2Button.getStyleClass().removeAll("floor-buttons");
            floor2Button.getStyleClass().add("highlight-floor-button");
        }
    }

    @FXML
    public void floor3ButtonOp(ActionEvent e) {
        currentFloor = Node.floorType.LEVEL_3;
        MeshView mv = getFloor(currentFloor);
        if (ThirdVisible) {
            mv.setOpacity(0);
            ThirdVisible = false;
            hideNodes(currentFloor);
            floor3Button.getStyleClass().removeAll("highlight-floor-button");
            floor3Button.getStyleClass().add("floor-buttons");
        }
        else {
            mv.setOpacity(1);
            ThirdVisible = true;
            unhideNodes(currentFloor);
            floor3Button.getStyleClass().removeAll("floor-buttons");
            floor3Button.getStyleClass().add("highlight-floor-button");

        }
    }

    @FXML
    public void defaultButtonOP(ActionEvent e) {

        threeDAnchorPane.getTransforms().setAll(new Rotate(0, 1920/2, 1080/2, 0, Rotate.Y_AXIS),
                new Rotate(0, 1920/2, 1080/2, 0, Rotate.X_AXIS)); // Rotate Map

        // Set panning
        threeDAnchorPane.setTranslateX(0);
        threeDAnchorPane.setTranslateY(0);
        threeDAnchorPane.setTranslateZ(0);

        /* Set Zoom
        threeDAnchorPane.setScaleX(15);
        threeDAnchorPane.setScaleY(15);
        threeDAnchorPane.setScaleZ(15);
        */

    }

    @FXML
    public void topDownButtonOP(ActionEvent e) {
        // Rotate
        threeDAnchorPane.getTransforms().setAll(new Rotate(0, 1920/2, 1080/2, 0, Rotate.Y_AXIS),
                new Rotate(90, 1920/2, 1080/2, 0, Rotate.X_AXIS)); // Rotate Map

        // Panning
        threeDAnchorPane.setTranslateX(0);
        threeDAnchorPane.setTranslateY(300);
        threeDAnchorPane.setTranslateZ(500);
    }

    /**
     * this lets the user scroll the wheel to scale the 3D model
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s){
        double newZoom = (s.getDeltaY()); // Get value from scroll wheel
        if (newZoom > 0 && curScale < 5) { // Zoom in (already zoomed out)
            curScale += 0.2;
            threeDAnchorPane.setScaleX(curScale);
            threeDAnchorPane.setScaleY(curScale);
            threeDAnchorPane.setScaleZ(curScale);
        }
        else if (newZoom > 0 && curScale + 5 <= 100) { // Zoom in (already zoomed in)
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
        else if (newZoom < 0 && curScale - 0.2 > 0) { // Zoomed out (already zoomed out)
            curScale -= 0.2;
            threeDAnchorPane.setScaleX(curScale);
            threeDAnchorPane.setScaleY(curScale);
            threeDAnchorPane.setScaleZ(curScale);
        }
        curZoom = newZoom; // Set next zoom value to compare to
    }

    static private MeshView[] loadMeshView(URL fileName) {
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(fileName);

        return importer.getImport();
    }

    private Group buildScene() {
        root = new Group();
        curScale = 16;
        curXRotation = 20;
        curYRotation = 0;
        curZRotation = 0;
        int height = 0;
        int index = 0;
        ArrayList<Double> floorScale;
        PhongMaterial phongMaterial;
        for (MeshView[] model : allModels) {
            getFloorOffset(index); // Get current floor translation offsets
            phongMaterial = allTextures.get(index); // Get texture for current floor
            floorScale = getFloorScale(index);

            model[0].setTranslateX(400 + floorOffsets.get(0));
            model[0].setTranslateY(400 + height);
            model[0].setTranslateZ(400 + floorOffsets.get(2));
            model[0].setScaleX(curScale + floorScale.get(0));
            model[0].setScaleY(curScale);
            model[0].setScaleZ(curScale + floorScale.get(1));

            model[0].getTransforms().setAll(new Rotate(curXRotation, Rotate.X_AXIS), new Rotate(curYRotation, Rotate.Y_AXIS));
            model[0].setMaterial(phongMaterial);
            root.getChildren().add(model[0]);

            switch(index) {
                case 0:
                    L2Nodes = draw3DNodes(Node.floorType.LEVEL_L2);
                    root.getChildren().add(L2Nodes);
                    break;
                case 1:
                    L1Nodes = draw3DNodes(Node.floorType.LEVEL_L1);
                    root.getChildren().add(L1Nodes);
                    break;
                case 2:
                    GNodes = draw3DNodes(Node.floorType.LEVEL_G);
                    root.getChildren().add(GNodes);
                    break;
                case 3:
                    FirstNodes = draw3DNodes(Node.floorType.LEVEL_1);
                    root.getChildren().add(FirstNodes);
                    break;
                case 4:
                    SecondNodes = draw3DNodes(Node.floorType.LEVEL_2);
                    root.getChildren().add(SecondNodes);
                    break;
                case 5:
                    ThirdNodes = draw3DNodes(Node.floorType.LEVEL_3);
                    root.getChildren().add(ThirdNodes);
                    break;
                default:
                    break;
            }

            height -= 50;
            index += 1;
            floorOffsets.clear();
        }


        pointLight = new PointLight(lightColor);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-1000);
        pointLight.setTranslateZ(0);
        PointLight pointLight2 = new PointLight(lightColor);
        pointLight2.setTranslateX(200);
        pointLight2.setTranslateY(600);
        pointLight2.setTranslateZ(600);
        PointLight pointLight3 = new PointLight(lightColor);
        pointLight3.setTranslateX(500);
        pointLight3.setTranslateY(400);
        pointLight3.setTranslateZ(0);

        Color ambientColor = Color.rgb(140, 140, 140, 0);
        AmbientLight ambient = new AmbientLight(ambientColor);

        allLights.getChildren().add(pointLight);
        //allLights.getChildren().add(pointLight2);
        //allLights.getChildren().add(pointLight3);
        allLights.getChildren().add(ambient);

        return root;
    }

    /**
     * Gets mesh value from group hierarchy for convenience
     * @return The MeshView we are looking for
     */
    private MeshView getMesh() {
        return (MeshView)root.getChildren().get(0);
    }

    /**
     * Draws all of the nodes on the map depending on which map is loaded
     */
    private Group draw3DNodes(Node.floorType floor) {
        Group nodeGroup = new Group();
        HashMap<String, Node> nodeSet;
        nodeSet = db.getAllNodes();
        System.out.println("drawing nodes");
        MeshView mv = getMesh();
        ArrayList<Integer> nodeYPos = nodeFloorPos(); // Get the correct y values for the different floors

        for (Node node : nodeSet.values()) {

            if (node.getFloor() == floor) {
                // Get y position for this node
                int yPos;
                switch (node.getFloor()) {
                    case LEVEL_L2:
                        yPos = nodeYPos.get(0);
                        break;
                    case LEVEL_L1:
                        yPos = nodeYPos.get(1);
                        break;
                    case LEVEL_G:
                        yPos = nodeYPos.get(2);
                        break;
                    case LEVEL_1:
                        yPos = nodeYPos.get(3);
                        break;
                    case LEVEL_2:
                        yPos = nodeYPos.get(4);
                        break;
                    case LEVEL_3:
                        yPos = nodeYPos.get(5);
                        break;
                    default:
                        yPos = nodeYPos.get(0);
                        break;
                }

                // Node is on current floor
                if (node.getType() != Node.nodeType.HALL) {
                    Sphere sphere = new Sphere(NODE_RADIUS);
                    nodeGroup.getChildren().add(sphere);
                    sphere.setTranslateX((node.getX() - X_OFFSET) * X_SCALE);
                    sphere.setTranslateY(mv.getTranslateY() - yPos);
                    sphere.setTranslateZ((-node.getY() - Z_OFFSET) * Z_SCALE);
                    sphere.getTransforms().setAll(new Rotate(curYRotation, mv.getTranslateX() - sphere.getTranslateX(), mv.getTranslateY() - sphere.getTranslateY(), mv.getTranslateZ() - sphere.getTranslateZ(), Rotate.Y_AXIS),
                            new Rotate(curXRotation, mv.getTranslateX() - sphere.getTranslateX(), mv.getTranslateY() - sphere.getTranslateY(), mv.getTranslateZ() - sphere.getTranslateZ(), Rotate.X_AXIS));
                    sphere.setMaterial(fillBlue);
                    if (!node.getActive()) {
                        sphere.setOpacity(0.5);
                        //circle.setFill(Color.GRAY);
                    }
                    sphere.addEventHandler(MouseEvent.ANY, nodeClickHandler);

                    String label = node.getID();
                    nodeDispSet.put(label, sphere);
                }

                // Node is on other floor
                else {
                    Sphere sphere = new Sphere(0);
                    nodeGroup.getChildren().add(sphere);
                    sphere.setTranslateX((node.getX() - X_OFFSET) * X_SCALE);
                    sphere.setTranslateY(mv.getTranslateY() - yPos);
                    sphere.setTranslateZ((-node.getY() - Z_OFFSET) * Z_SCALE);
                    sphere.getTransforms().setAll(new Rotate(curYRotation, mv.getTranslateX() - sphere.getTranslateX(), mv.getTranslateY() - sphere.getTranslateY(), mv.getTranslateZ() - sphere.getTranslateZ(), Rotate.Y_AXIS),
                            new Rotate(curXRotation, mv.getTranslateX() - sphere.getTranslateX(), mv.getTranslateY() - sphere.getTranslateY(), mv.getTranslateZ() - sphere.getTranslateZ(), Rotate.X_AXIS));

                    String label = node.getID();
                    nodeDispSet.put(label, sphere);
                }
            }
        }
        System.out.println("Printed All Nodes");
        return nodeGroup;
    }

    /**
     * Draws the edges according to what was given back from the database
     * Source for getting cylinders drawn: http://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
     */
    private void drawEdges() {
        Group edgeGroup = new Group();
        HashMap<String, Edge> edgeSet;
        edgeSet = db.getAllEdges();
        MeshView mv = getMesh();

        for (Edge edge : edgeSet.values()) {
            if (edge.getActive()) {
                Sphere start = nodeDispSet.get(edge.getStart().getID());
                Sphere end = nodeDispSet.get(edge.getEnd().getID());
                Point3D origin = new Point3D(start.getTranslateX(), start.getTranslateY(), start.getTranslateZ());
                Point3D target = new Point3D(end.getTranslateX(), end.getTranslateY(), end.getTranslateZ());
                Point3D yAxis = new Point3D(0, 1, 0);
                Point3D diff = target.subtract(origin);
                double height = diff.magnitude();

                Point3D mid = target.midpoint(origin);
                Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

                Point3D axisOfRotation = diff.crossProduct(yAxis);
                double angle = Math.acos(diff.normalize().dotProduct(yAxis));
                Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

                Cylinder line = new Cylinder(2, height);
                line.setMaterial(fillOrange);
                line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
                line.setVisible(false);
                edgeGroup.getChildren().add(line);

                String label = edge.getID();
                edgeDispSet.put(label, line);
            }
        }

        // Set translations and move edges to behind nodes
        edgeGroup.getTransforms().setAll(new Rotate(curXRotation, mv.getTranslateX() - edgeGroup.getTranslateX(), mv.getTranslateY() - edgeGroup.getTranslateY(), mv.getTranslateZ() - edgeGroup.getTranslateZ(), Rotate.X_AXIS),
                        new Rotate(curYRotation, mv.getTranslateX() - edgeGroup.getTranslateX(), mv.getTranslateY() - edgeGroup.getTranslateY(), mv.getTranslateZ() - edgeGroup.getTranslateZ(), Rotate.Y_AXIS));
        root.getChildren().add(edgeGroup);
        /*edgeGroup.toBack(); //TODO get edges behind nodes
        mv.toBack();*/
    }

    /**
     * this handles all mouse events related to nodes that exist in the database
     * and have been drawn
     */
    Boolean firstSelected = false;
    Boolean secondSelected = false;
    private EventHandler<MouseEvent> nodeClickHandler = new EventHandler<MouseEvent>() {
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

                if (!firstSelected) {
                    clearStartNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setMaterial(fillGreen);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                    firstSelected = true;
                } else {
                    clearEndNode();
                    for (String string : nodeDispSet.keySet()) {
                        if (nodeDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            nodeDispSet.get(string).setMaterial(fillRed);
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
     * Hides the nodes for a floor that is not visible
     * @param floor The floor of nodes to hide
     */
    private void hideNodes(Node.floorType floor) {
        HashMap<String, Node> allNodes = db.getAllNodes();
        for (Node n : allNodes.values()) {
            if (n.getFloor().equals(floor)) {
                Sphere curSphere = nodeDispSet.get(n.getID());
                curSphere.setDisable(true);
                curSphere.setVisible(false);
            }
        }
    }

    /**
     * Unhides the nodes for a floor that is visible
     * @param floor The floor of nodes to unhide
     */
    private void unhideNodes(Node.floorType floor) {
        HashMap<String, Node> allNodes = db.getAllNodes();
        for (Node n : allNodes.values()) {
            if (n.getFloor().equals(floor)) {
                Sphere curSphere = nodeDispSet.get(n.getID());
                curSphere.setDisable(false);
                curSphere.setVisible(true);
            }
        }
    }

    /**
     * Used to draw the list of nodes returned by AStar
     */
    public void resetPath() {
        Node currentNode = null, pastNode = null;
        for (Node n : pathMade) {
            pastNode = currentNode;
            currentNode = n;
            nodeDispSet.get(n.getID()).setMaterial(fillBlue);

            for (Edge e : currentNode.getEdges()) { //TODO set this for cylinders
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        edgeDispSet.get(e.getID()).setVisible(false);
                        if (e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor) {
                            edgeDispSet.get(e.getID()).setVisible(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Used to draw the list of nodes returned by AStar
     *
     * @param path List of Nodes to be drawn
     */
    public void drawPath(ArrayList<Node> path) {
        double width, height, angle;
        double distanceCounter = 0;
        PhongMaterial phongMaterial = new PhongMaterial();

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
            if (n.getX() < minXCoord) minXCoord = n.getX();
            if (n.getX() > maxXCoord) maxXCoord = n.getX();
            if (n.getY() < minYCoord) minYCoord = n.getY();
            if (n.getY() > maxYCoord) maxYCoord = n.getY();

            pastNode = currentNode;
            currentNode = n;

            //checks if if the current node should go in stair set
            checkStairNodeSet(currentNode);

            //Draws the arrows
            if (!path.get(0).equals(n)) {
                width = currentNode.getX() - pastNode.getX();
                height = currentNode.getY() - pastNode.getY();
                angle = Math.atan2(height, width);
                //increment the distanceCounter
                distanceCounter += currentNode.distanceBetweenNodes(pastNode);
                /*if (distanceCounter >= 175) {
                    distanceCounter = 0;

                    arrowFloorSet.add(currentNode.getFloor().toString());
                    if (toggleOn) {
                        drawTriangle(angle, pastNode.getxDisplay(), pastNode.getyDisplay());
                    } else {
                        drawTriangle(angle, pastNode.getX(), pastNode.getY());
                    //}
                }*/
            }

            //set start node to Green and end node to red
            if (path.get(0).equals(n)) {
                //phongMaterial.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_start.png").toExternalForm()));
                //nodeDispSet.get(currentNode.getID()).setMaterial(fillPurple);
                nodeDispSet.get(currentNode.getID()).setVisible(true);
                if (!currentNode.getFloor().equals(currentFloor))
                    nodeDispSet.get(currentNode.getID()).setOpacity(0.5);

                /*if (toggleOn) {
                    startLabel.setLayoutX((n.getxDisplay() + 5 - X_OFFSET) * X_SCALE);
                    startLabel.setLayoutY((n.getyDisplay() - 40 - Y_OFFSET) * Y_SCALE);
                } else {*/
                    startLabel.setLayoutX((n.getX() + 5 - curXOffset) * X_SCALE);
                    startLabel.setLayoutY((n.getY() - 40 - Y_OFFSET) * Z_SCALE);
                //}
                startLabel.setText(n.getLongName());
                startLabel.setFont(font);
                startLabel.toFront();
                labelDispSet.add(startLabel);
                //threeDAnchorPane.getChildren().add(startLabel); //TODO Set this on it's on pane, make it so labels move with map

            } else if (path.get(path.size() - 1).equals(n)) {
                //phongMaterial.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_end.png").toExternalForm()));
                //nodeDispSet.get(currentNode.getID()).setMaterial(phongMaterial);
                nodeDispSet.get(currentNode.getID()).setVisible(true);
                if (!currentNode.getFloor().equals(currentFloor))
                    nodeDispSet.get(currentNode.getID()).setOpacity(0.5);

                //if the last node was a stair or an elevator then it should check the else in the checkStairNode function
                if (currentNode.getType().equals(Node.nodeType.ELEV) || currentNode.getType().equals(Node.nodeType.STAI)) {
                    addToStairNodeSet();
                }
                /*if (toggleOn) {
                    endLabel.setLayoutX((n.getxDisplay() + 5 - X_OFFSET) * X_SCALE);
                    endLabel.setLayoutY((n.getyDisplay() - 34 - Y_OFFSET) * Y_SCALE);
                } else {*/
                    endLabel.setLayoutX((n.getX() + 5 - curXOffset) * X_SCALE);
                    endLabel.setLayoutY((n.getY() - 34 - Y_OFFSET) * Z_SCALE);
                //}
                endLabel.setText(n.getLongName());
                endLabel.setFont(font);
                endLabel.toFront();
                labelDispSet.add(endLabel);
                //nodesEdgesPane.getChildren().add(endLabel); //TODO again put this on its own pane
            }
            //Color in the path appropriately
            for (Edge e : currentNode.getEdges()) {
                if (pastNode != null) {
                    if (e.contains(pastNode)) {
                        //edgeDispSet.get(e.getID()).setStroke(Color.rgb(250, 150, 0)); //TODO set for cylinder
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
        //this sets the proper opacity for the arrows based on floor
        for (int i = 0; i < arrowDispSet.size(); i++) {
            System.out.println(arrowFloorSet.get(i));
            if (arrowFloorSet.get(i).equals(currentFloor.toString())) {
                arrowDispSet.get(i).setOpacity(1.0);
            } else {
                arrowDispSet.get(i).setOpacity(0.3);
            }
        }

        // Create PopOver for Stair or Elevator nodes
        for (int i = 0; i < stairNodeSet.size(); i += 2) {
            for (String str : nodeDispSet.keySet()) {
                if (str.equals(stairNodeSet.get(i).getID()) && stairNodeSet.get(i).getFloor().equals(currentFloor)) {
                    phongMaterial.setDiffuseMap(new Image(getClass().getResource("/models/textures/node_stair.png").toExternalForm()));
                    nodeDispSet.get(str).setMaterial(phongMaterial);
                    Label clickMeLabel = new Label("Click me to follow the path");
                    clickMeLabel.setFont(font);
                    labelDispSet.add(clickMeLabel);
                    //nodesEdgesPane.getChildren().add(clickMeLabel); //TODO add label to labels pane
                    //if (!toggleOn) {
                        // 2d view
                        double actualX = (stairNodeSet.get(i).getX() + 10 - curXOffset) * X_SCALE;
                        double actualY = (stairNodeSet.get(i).getY() + 10 - Y_OFFSET) * Z_SCALE;
                        clickMeLabel.setLayoutX(actualX + 20);
                        clickMeLabel.setLayoutY(actualY + 20);
                        Line line = new Line(actualX, actualY, actualX + 20, actualY + 20);
                        //nodesEdgesPane.getChildren().add(line); //TODO add label to labels pane
                        lineDispSet.add(line);
                        break;
                    /*} else {
                        // 3d view
                        double actualX = (stairNodeSet.get(i).getxDisplay() + 10 - X_OFFSET) * X_SCALE;
                        double actualY = (stairNodeSet.get(i).getyDisplay() + 10 - Y_OFFSET) * Y_SCALE;
                        clickMeLabel.setLayoutX(actualX + 20);
                        clickMeLabel.setLayoutY(actualY + 20);
                        Line line = new Line(actualX, actualY, actualX + 20, actualY + 20);
                        nodesEdgesPane.getChildren().add(line);
                        lineDispSet.add(line);
                        break;
                    }*/
                }
            }
        }

        System.out.println("list of stair nodes: " + stairNodeSet.toString());
        minXCoord -= 200;
        minYCoord -= 400;
        maxXCoord += 200;
        maxYCoord += 100;
        double rangeX = maxXCoord - minXCoord;
        double rangeY = maxYCoord - minYCoord;

        double desiredZoomX = 1920 / (rangeX * X_SCALE);
        double desiredZoomY = 1080 / (rangeY * Z_SCALE);
        System.out.println("desired X zoom: " + desiredZoomX + " desired Zoom Y: " + desiredZoomY);

        double centerX = (maxXCoord + minXCoord) / 2;
        double centerY = (maxYCoord + minYCoord) / 2;

        //autoTranslateZoom(desiredZoomX, desiredZoomY, centerX, centerY); //TODO get auto zoom on path working

        pathDrawn = true;
    }

    private void checkStairNodeSet(Node currentNode) {

        if (currentNode.getType().equals(Node.nodeType.STAI)) {
            recentElevNodeSet.clear();
            recentStairNodeSet.add(currentNode);
        } else if (currentNode.getType().equals(Node.nodeType.ELEV)) {
            recentStairNodeSet.clear();
            recentElevNodeSet.add(currentNode);
        } else {
            addToStairNodeSet();
        }
    }

    private void addToStairNodeSet() {
        if (recentStairNodeSet.size() > 1) {
            stairNodeSet.add(recentStairNodeSet.get(0));
            stairNodeSet.add(recentStairNodeSet.get(recentStairNodeSet.size() - 1));
        } else if (recentElevNodeSet.size() > 1) {
            stairNodeSet.add(recentElevNodeSet.get(0));
            stairNodeSet.add(recentElevNodeSet.get(recentElevNodeSet.size() - 1));
        }
        recentElevNodeSet.clear();
        recentStairNodeSet.clear();
    }

    private void clearStartNode() { //TODO change these to the correct materials
        if (pathDrawn) resetPath();
        for (Sphere s: nodeDispSet.values()) {
            if (s.getMaterial() == fillGreen) {
               s.setMaterial(fillBlue);
           }
        }
    }

    private void clearEndNode() {
        if (pathDrawn) resetPath();
        for (Sphere s : nodeDispSet.values()) {
            if (s.getMaterial() == fillRed) {
                s.setMaterial(fillBlue);
            }
        }
    }

    /**
     * Sets the correct y positions of the floor nodes
     * @return The list of node Y positions
     */
    private ArrayList<Integer> nodeFloorPos() {
        ArrayList<Integer> floorYPos = new ArrayList<>();
        floorYPos.add(Y_OFFSET - 50);
        floorYPos.add(Y_OFFSET + 50);
        floorYPos.add(Y_OFFSET + 150);
        floorYPos.add(Y_OFFSET + 250);
        floorYPos.add(Y_OFFSET + 350);
        floorYPos.add(Y_OFFSET + 450);
        return floorYPos;
    }

    /**
     * Fetches the specified floor mesh from root
     * @param floor The floor to fetch
     * @return The mesh of the specified floor
     */
    private MeshView getFloor(Node.floorType floor) {
        MeshView mesh;
        switch(floor) {
            case LEVEL_L2:
                mesh = (MeshView)root.getChildren().get(0);
                break;
            case LEVEL_L1:
                mesh = (MeshView)root.getChildren().get(2);
                break;
            case LEVEL_G:
                mesh = (MeshView)root.getChildren().get(4);
                break;
            case LEVEL_1:
                mesh = (MeshView)root.getChildren().get(6);
                break;
            case LEVEL_2:
                mesh = (MeshView)root.getChildren().get(8);
                break;
            case LEVEL_3:
                mesh = (MeshView)root.getChildren().get(10);
                break;
            default:
                mesh = (MeshView)root.getChildren().get(0);
                break;
        }
        return mesh;
    }

    /**
     * Fetches offsets to align floors to nodes
     */
    private void getFloorOffset(int index) {
        switch(index) {
            case 0:
                floorOffsets.add(0, -130); // X Offset
                floorOffsets.add(1, 0); // Y Offset
                floorOffsets.add(2, 50); // Z Offset
                break;
            case 1:
                floorOffsets.add(0, 35); // X Offset
                floorOffsets.add(1, 10); // Y Offset
                floorOffsets.add(2, 70); // Z Offset
                break;
            case 2:
                floorOffsets.add(0, 35); // X Offset
                floorOffsets.add(1, 0); // Y Offset
                floorOffsets.add(2, 70); // Z Offset
                break;
            case 3:
                floorOffsets.add(0, 35); // X Offset
                floorOffsets.add(1, 0); // Y Offset
                floorOffsets.add(2, 47); // Z Offset
                break;
            case 4:
                floorOffsets.add(0, 32); // X Offset
                floorOffsets.add(1, 0); // Y Offset
                floorOffsets.add(2, 32); // Z Offset
                break;
            case 5:
                floorOffsets.add(0, 25); // X Offset
                floorOffsets.add(1, 0); // Y Offset
                floorOffsets.add(2, 35); // Z Offset
                break;
            default:
                floorOffsets.add(0, 0); // X Offset
                floorOffsets.add(1, 0); // Y Offset
                floorOffsets.add(2, 0); // Z Offset
                break;
        }
    }

    private ArrayList<Double> getFloorScale(int index) {
        ArrayList<Double> floorScale = new ArrayList<>();
        switch(index) {
            case 0:
                floorScale.add(0, 0.0); // X Scale
                floorScale.add(1, 0.0); // Z Scale
                break;
            case 1:
                floorScale.add(0, 0.0); // X Scale
                floorScale.add(1, 0.0); // Z Scale
                break;
            case 2:
                floorScale.add(0, 0.0); // X Scale
                floorScale.add(1, 0.0); // Z Scale
                break;
            case 3:
                floorScale.add(0, 0.0); // X Scale
                floorScale.add(1, 0.0); // Z Scale
                break;
            case 4:
                floorScale.add(0, -0.2); // X Scale
                floorScale.add(1, -0.2); // Z Scale
                break;
            case 5:
                floorScale.add(0, -0.3); // X Scale
                floorScale.add(1, -0.3); // Z Scale
                break;
            default:
                floorScale.add(0, 0.0); // X Scale
                floorScale.add(1, 0.0); // Z Scale
                break;
        }
        return floorScale;
    }
}
