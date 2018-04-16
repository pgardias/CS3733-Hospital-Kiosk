package edu.wpi.cs3733d18.teamp.ui.map;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MapViewerBuilder implements Initializable{

    // Static global variables
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard
    private static final int VIEWPORT_SIZE = 800;
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

    // FXML variables
    @FXML
    AnchorPane threeDAnchorPane;

    @FXML
    Button leftButton;

    @FXML
    Button rightButton;

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
        Group group = buildScene("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&WWholeMap.obj");
        group.setScaleX(2);
        group.setScaleY(2);
        group.setScaleZ(2);
        group.setTranslateX(500);
        group.setTranslateY(100);
        threeDAnchorPane.getChildren().add(group);

        threeDAnchorPane.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);
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
                    if (xChange < 0) {
                        curYRotation += ROTATION_SPEED;
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    } else if (xChange > 0) {
                        curYRotation -= ROTATION_SPEED;
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    }
                    // Change Rotation X
                    if (yChange < 0) {
                        curXRotation += ROTATION_SPEED;
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    } else if (yChange > 0) {
                        curXRotation -= ROTATION_SPEED;
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
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
                        mv.setTranslateX(curXTranslation);
                    } else if (xChange > 0) { // Pan right
                        curXTranslation += PAN_SPEED;
                        mv.setTranslateX(curXTranslation);
                    }
                    // Change Panning Y
                    if (yChange < 0) { // Pan down
                        curYTranslation -= PAN_SPEED;
                        mv.setTranslateY(curYTranslation);
                    } else if (yChange > 0) { // Pan up
                        curYTranslation += PAN_SPEED;
                        mv.setTranslateY(curYTranslation);
                    }

                    mouseInSceneX = newMouseInSceneX;
                    mouseInSceneY = newMouseInSceneY;
                }
            }
        }
    };

    /**
     * this lets the user scroll the wheel to scale the 3D model
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s){
        double newZoom = (s.getDeltaY()); // Get value from scroll wheel
        MeshView mv = getMesh();
        //System.out.println("SCROLLING ZOOM. OLD DELTA: "+curZoom+" NEW DELTA: "+newZoom);
        if (newZoom > 0 && curScale + 5 <= 100) {
            curScale += 5;
            mv.setScaleX(curScale);
            mv.setScaleY(curScale);
            mv.setScaleZ(curScale);
        }
        else if (newZoom < 0 && curScale - 5 > 0) {
            curScale -= 5;
            mv.setScaleX(curScale);
            mv.setScaleY(curScale);
            mv.setScaleZ(curScale);
        }
        curZoom = newZoom; // Set next zoom value to compare to
    }

    static MeshView[] loadMeshView(String fileName) {
        File file = new File(fileName);
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(file);
        //Mesh mesh = importer.getImport();

        return importer.getImport();
    }

    private Group buildScene(String fileName) {
        MeshView[] meshViews = loadMeshView(fileName);
        for (int i = 0; i < meshViews.length; i++) {
            curXTranslation = (VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
            curYTranslation = (VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
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

    /*private PerspectiveCamera addCamera(Scene scene) {
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
        System.out.println("Near Clip: " + perspectiveCamera.getNearClip());
        System.out.println("Far Clip:  " + perspectiveCamera.getFarClip());
        System.out.println("FOV:       " + perspectiveCamera.getFieldOfView());

        scene.setCamera(perspectiveCamera);
        return perspectiveCamera;
    }*/

    public void leftButtonOp() {
        curYRotation += 10;
        MeshView mv = getMesh();
        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
    }

    public void rightButtonOp() {
        curYRotation -= 10;
        MeshView mv = getMesh();
        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
    }

    /**
     * Gets mesh value from group hierarchy for convenience
     * @return The MeshView we are looking for
     */
    private MeshView getMesh() {
        Group group = (Group)threeDAnchorPane.getChildren().get(3); // TODO index needs to be incremented whenever a new element is added to the pane
        return (MeshView)group.getChildren().get(0);
    }

    /**
     * Loads in a new mesh when switching floors
     */
    @FXML
    public void switchMesh(String fileName) {
        threeDAnchorPane.getChildren().remove(3);
        Group newRoot = buildScene(fileName);
        newRoot.setScaleX(2);
        newRoot.setScaleY(2);
        newRoot.setScaleZ(2);
        newRoot.setTranslateX(500);
        newRoot.setTranslateY(100);
        threeDAnchorPane.getChildren().add(newRoot);
    }

    @FXML
    public void floorL2ButtonOp(ActionEvent e) {
        switchMesh("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&WL2Floor.obj");
    }

    @FXML
    public void floorL1ButtonOp(ActionEvent e) {
        switchMesh("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&WL1Floor.obj");
    }

    @FXML
    public void floorGButtonOp(ActionEvent e) {
        switchMesh("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&WGFloor.obj");
    }

    @FXML
    public void floor1ButtonOp(ActionEvent e) {
        switchMesh("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&W1stFloor.obj");
    }

    @FXML
    public void floor2ButtonOp(ActionEvent e) {
        switchMesh("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&W2ndFloor.obj");
    }

    @FXML
    public void floor3ButtonOp(ActionEvent e) {
        switchMesh("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&W3rdFloor.obj");
    }

}
