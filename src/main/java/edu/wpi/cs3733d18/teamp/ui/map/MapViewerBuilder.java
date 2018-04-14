package edu.wpi.cs3733d18.teamp.ui.map;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.application.Application;
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
    private static final double MODEL_SCALE_FACTOR = 15;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard
    private static final int VIEWPORT_SIZE = 800;
    private static final Color lightColor = Color.rgb(244, 255, 250);
    private static final Color buildingColor = Color.rgb(255, 255, 255);

    private Group root;
    private PointLight pointLight;

    private static final int ROTATION_SPEED = 2;
    private static final int PAN_SPEED = 5;

    // Changeable global variables
    private double mouseInSceneX;
    private double mouseInSceneY;
    private int curXRotation;
    private int curYRotation;
    private int curZRotation;
    private double curXTranslation;
    private double curYTranslation;
    private double curZTranslation;

    // FXML variables
    @FXML
    AnchorPane threeDAnchorPane;

    @FXML
    Button leftButton;

    @FXML
    Button rightButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Group group = buildScene();
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

            // Left Mouse Button, Rotating
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    System.out.println("Mouse_Pressed");
                    mouseInSceneX = event.getSceneX();
                    mouseInSceneY = event.getSceneY();
                    isDragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    System.out.println("Drag_Detected");
                    isDragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    System.out.println("Mouse_Dragged");
                    double newMouseInSceneX = event.getSceneX();
                    double newMouseInSceneY = event.getSceneY();
                    double xChange = newMouseInSceneX - mouseInSceneX;
                    double yChange = newMouseInSceneY - mouseInSceneY;

                    //System.out.println("Previous getSceneX: "+mouseInSceneX+" xChange: "+xChange);
                    // Change Rotation Y
                    if (xChange < 0) {
                        curYRotation += ROTATION_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    } else if (xChange > 0) {
                        curYRotation -= ROTATION_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    }
                    // Change Rotation X
                    if (yChange < 0) {
                        curXRotation += ROTATION_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    } else if (yChange > 0) {
                        curXRotation -= ROTATION_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
                    }
                    mouseInSceneX = newMouseInSceneX;
                    mouseInSceneY = newMouseInSceneY;
                }
            }

            // Right Mouse Button, Panning
            else if (event.getButton() == MouseButton.SECONDARY) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    System.out.println("Mouse_Pressed");
                    mouseInSceneX = event.getSceneX();
                    mouseInSceneY = event.getSceneY();
                    isDragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    System.out.println("Drag_Detected");
                    isDragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    System.out.println("Mouse_Dragged");
                    double newMouseInSceneX = event.getSceneX();
                    double newMouseInSceneY = event.getSceneY();
                    double xChange = newMouseInSceneX - mouseInSceneX;
                    double yChange = newMouseInSceneY - mouseInSceneY;

                    // Change Panning X
                    if (xChange < 0) {
                        curXTranslation -= PAN_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.setTranslateX(curXTranslation);
                    } else if (xChange > 0) {
                        curXTranslation += PAN_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.setTranslateX(curXTranslation);
                    }
                    // Change Panning Y
                    if (yChange < 0) {
                        curYTranslation -= PAN_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.setTranslateY(curYTranslation);
                    } else if (yChange > 0) {
                        curYTranslation += PAN_SPEED;
                        Group group = (Group) threeDAnchorPane.getChildren().get(2);
                        MeshView mv = (MeshView) group.getChildren().get(0);
                        mv.setTranslateY(curYTranslation);
                    }

                    mouseInSceneX = newMouseInSceneX;
                    mouseInSceneY = newMouseInSceneY;
                }
            }
        }
    };

    /**
     * this lets the user scroll the wheel to move the zoom slider
     * This also readjusts the screen based on the bounds
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s){
        double newValue = (s.getDeltaY());
    }

    static MeshView[] loadMeshView() {
        File file = new File("C:/Users/Kyle/Documents/Iteration3/src/main/resources/models/B&W2ndFloor.obj");
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(file);
        //Mesh mesh = importer.getImport();

        return importer.getImport();
    }

    private Group buildScene() {
        MeshView[] meshViews = loadMeshView();
        for (int i = 0; i < meshViews.length; i++) {
            curXTranslation = (VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
            curYTranslation = (VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
            curZTranslation = (VIEWPORT_SIZE / 2);
            meshViews[i].setTranslateX(curXTranslation);
            meshViews[i].setTranslateY(curYTranslation);
            meshViews[i].setTranslateZ(curZTranslation);
            meshViews[i].setScaleX(MODEL_SCALE_FACTOR);
            meshViews[i].setScaleY(MODEL_SCALE_FACTOR);
            meshViews[i].setScaleZ(MODEL_SCALE_FACTOR);

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
        Group group = (Group)threeDAnchorPane.getChildren().get(2);
        MeshView mv = (MeshView)group.getChildren().get(0);
        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
    }

    public void rightButtonOp() {
        curYRotation -= 10;
        Group group = (Group)threeDAnchorPane.getChildren().get(2);
        MeshView mv = (MeshView)group.getChildren().get(0);
        mv.getTransforms().setAll(new Rotate(curYRotation, Rotate.Y_AXIS), new Rotate(curXRotation, Rotate.X_AXIS));
    }

}
