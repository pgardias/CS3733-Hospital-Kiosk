package edu.wpi.cs3733d18.teamp.ui.map;

import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.*;
import javafx.util.Duration;


import java.sql.Time;
import java.util.ArrayList;

public class ArrowPathTransition {
    private Path[] path;
    private PathTransition[] pathTransition;
    private RotateTransition[] rotateTransition;
    private FadeTransition[] fadeTransition;
    private SequentialTransition sequentialTransition = new SequentialTransition();
    Shape shape;
    double X_OFFSET = 0;
    double Y_OFFSET = -19;
    double X_SCALE = 1920.0 / 5000.0;
    double Y_SCALE = 1065.216 / 2774.0;
    double jumpDuration = 0;
    int size;

    /**
     * This intitializes the each rotation and path transition for the whole path of nodes and adds an arrow to go
     * through the path. It then adds each rotation and path pair to a sequential transition
     * This also figures out the amount of arrows that will be drawn as well as the current floor the path is drawn to
     * se the arrow to more transparent where appropriate
     *
     * @param nodePath the path of nodes to draw the path of the animation
     * @param shape the arrow that will travel along the path
     * @param arrowDispSetSize the size of the arrow display set that will be put into the animation
     * @param currentFloor the current floor the path was drawn on
     */
    public ArrowPathTransition(ArrayList<Node> nodePath, Polygon shape, int arrowDispSetSize, Node.floorType currentFloor){
        this.size = arrowDispSetSize;

        pathTransition = new PathTransition[nodePath.size() - 1];
        rotateTransition = new RotateTransition[nodePath.size() - 1];
        fadeTransition = new FadeTransition[nodePath.size() - 1];
        path = new Path[nodePath.size() - 1];
        this.shape = shape;
        double startx = (nodePath.get(0).getxDisplay() - X_OFFSET) * X_SCALE;
        double starty = (nodePath.get(0).getyDisplay() - Y_OFFSET) * Y_SCALE;

        ObservableList<Double> points = shape.getPoints();
        double arrowStartx = points.get(0);
        double arrowStartY = points.get(1);
        double arrowEndx = points.get(4);
        double arrowEndy = points.get(5);
        double arrowAngle = Math.atan2(arrowStartY - arrowEndy, arrowStartx - arrowEndx) * 180/Math.PI;

        Node currentNode = nodePath.get(0);
        Node pastNode = null;
        double currentAngle = arrowAngle;

        jumpDuration = 0;

        for (int i = 1; i < nodePath.size(); i++) {

            pastNode = currentNode;
            currentNode = nodePath.get(i);


            //Rotation Transition
            //desired angle the arrow should be based on path
            double desiredAngle = Math.atan2(pastNode.getyDisplay() - currentNode.getyDisplay(),
                    pastNode.getxDisplay() - currentNode.getxDisplay()) * 180 / Math.PI;

            //this subtracts the actual angle of the arrow to get what angle it should be rotated to
            double endAngle = desiredAngle - arrowAngle;
            //Setting up the actual animation
            rotateTransition[i - 1] = new RotateTransition();
            rotateTransition[i - 1].setNode(shape);
            rotateTransition[i - 1].setFromAngle(currentAngle);
            double changeInAngle = endAngle - currentAngle;
            if ((endAngle - currentAngle) > 180) changeInAngle = (endAngle - currentAngle) - 360;
            if ((endAngle - currentAngle) < -180) changeInAngle = (endAngle - currentAngle) + 360;
            rotateTransition[i - 1].setByAngle(changeInAngle);
            System.out.println("end angle - current Angle = " + (endAngle - currentAngle));
            rotateTransition[i - 1].setInterpolator(Interpolator.LINEAR);
            rotateTransition[i - 1].setDuration(new Duration(1));
            rotateTransition[i - 1].setCycleCount(1);
            currentAngle = endAngle;
            jumpDuration += 1;

            //Fade Transition
            fadeTransition[i-1] = new FadeTransition();
            fadeTransition[i - 1].setNode(shape);
            if (currentFloor.equals(currentNode.getFloor())){
                fadeTransition[i - 1].setToValue(1.0);
            }else {
                fadeTransition[i - 1].setToValue(0.5);
            }
            fadeTransition[i-1].setInterpolator(Interpolator.LINEAR);
            fadeTransition[i-1].setDuration(new Duration(1));
            fadeTransition[i-1].setCycleCount(1);


            //Path Transition
            //First part of path
            path[i-1] = new Path();
            double x = (pastNode.getxDisplay() - X_OFFSET) * X_SCALE;
            double y = (pastNode.getyDisplay() - Y_OFFSET) * Y_SCALE;
            path[i-1].getElements().add(new MoveTo(x, y));
            //Second part of path
            double x2 = (currentNode.getxDisplay() - X_OFFSET) * X_SCALE;
            double y2 = (currentNode.getyDisplay() - Y_OFFSET) * Y_SCALE;

            double distance = Math.sqrt(Math.pow(x2-x, 2) + Math.pow(y2-y,2));

            if (currentFloor.equals(currentNode.getFloor())) shape.setOpacity(0.5);
            else shape.setOpacity(1.0);

            path[i-1].getElements().add(new LineTo(x2, y2));
            pathTransition[i-1] = new PathTransition();
            pathTransition[i-1].setNode(shape);
            pathTransition[i-1].setPath(path[i-1]);
            pathTransition[i-1].setOrientation(PathTransition.OrientationType.NONE);
            pathTransition[i-1].setInterpolator(Interpolator.LINEAR);
            pathTransition[i-1].setDuration(new Duration(distance * 50));
            pathTransition[i-1].setCycleCount(1);

            jumpDuration += distance*50;


            sequentialTransition.getChildren().addAll(rotateTransition[i-1], fadeTransition[i-1], pathTransition[i-1]);

        }
    }

    public void setPathAnim(){
        //add them to the sequence
        sequentialTransition.setCycleCount(Timeline.INDEFINITE);

    }

    public void jumpToDuration(int jump){
        sequentialTransition.jumpTo(new Duration((size - jump) *jumpDuration/size));
    }

    public void playAnim(){
        sequentialTransition.play();
    }

    public void pauseAnim(){
       // pathTransition.pause();
    }
}
