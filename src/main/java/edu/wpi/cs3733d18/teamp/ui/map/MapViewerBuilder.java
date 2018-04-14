package edu.wpi.cs3733d18.teamp.ui.map;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
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
    private static final Color jewelColor = Color.rgb(0, 190, 222);

    private Group root;
    private PointLight pointLight;

    // Changeable global variables
    private int curXRotation;
    private int curYRotation;
    private int curZRotation;

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
            meshViews[i].setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
            meshViews[i].setTranslateY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
            meshViews[i].setTranslateZ(VIEWPORT_SIZE / 2);
            meshViews[i].setScaleX(MODEL_SCALE_FACTOR);
            meshViews[i].setScaleY(MODEL_SCALE_FACTOR);
            meshViews[i].setScaleZ(MODEL_SCALE_FACTOR);

            /*PhongMaterial sample = new PhongMaterial(jewelColor);
            sample.setSpecularColor(lightColor);
            sample.setSpecularPower(16);
            meshViews[i].setMaterial(sample);*/

            meshViews[i].getTransforms().setAll(/*new Rotate(38, Rotate.Z_AXIS),*/ new Rotate(20, Rotate.X_AXIS));
            curXRotation = 20;
            curYRotation = 0;
            curZRotation = 0;
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
