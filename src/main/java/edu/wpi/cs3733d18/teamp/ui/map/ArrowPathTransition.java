package edu.wpi.cs3733d18.teamp.ui.map;

import com.sun.javafx.geom.Path2D;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;

public class ArrowPathTransition {
    private Path path = new Path();
    private PathTransition pathAnim = new PathTransition();
    Shape shape;
    double X_OFFSET = 0;
    double Y_OFFSET = -19;
    double X_SCALE = 1920.0 / 5000.0;
    double Y_SCALE = 1065.216 / 2774.0;

    public ArrowPathTransition(ArrayList<Node> nodePath, Shape shape){
        this.shape = shape;
        double startx = (nodePath.get(0).getxDisplay() - X_OFFSET) * X_SCALE;
        double starty = (nodePath.get(0).getyDisplay() - Y_OFFSET) * Y_SCALE;
        path.getElements().add(new MoveTo(startx,starty));
        for (Node node: nodePath){
            double x = (node.getxDisplay() - X_OFFSET) * X_SCALE;
            double y = (node.getyDisplay() - Y_OFFSET) * Y_SCALE;
            path.getElements().add(new LineTo(x, y));
        }
    }

    public void setPathAnim(){
        pathAnim.setNode(shape);
        pathAnim.setPath(path);
        pathAnim.setOrientation(PathTransition.OrientationType.NONE);
        pathAnim.setInterpolator(Interpolator.LINEAR);
        pathAnim.setDuration(new Duration(6000));
        pathAnim.setCycleCount(Timeline.INDEFINITE);
    }

    public void playAnim(){
        pathAnim.play();
    }

    public void pauseAnim(){
        pathAnim.pause();
    }
}
