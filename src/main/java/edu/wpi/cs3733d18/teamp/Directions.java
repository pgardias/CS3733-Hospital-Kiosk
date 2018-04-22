package edu.wpi.cs3733d18.teamp;

import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

import java.util.ArrayList;

public class Directions {
    private static final double STRAIGHT_BOUNDS = 20;
    private static final double SLIGHT_BOUNDS = 60;
    private static final double NORMAL_BOUNDS = 120;
    private static final double HARD_BOUNDS = 165;
    private static final double TURN_AROUND_BOUNDS = 180;
    private static final int STRAIGHT = 1;
    private static final int SLIGHT_LEFT = 2;
    private static final int LEFT = 3;
    private static final int HARD_LEFT = 4;
    private static final int SLIGHT_RIGHT = 5;
    private static final int RIGHT = 6;
    private static final int HARD_RIGHT = 7;
    private static final int ELEVATORS = 8;
    private static final int STAIRS = 9;
    private static final int TURN_AROUND = 10;

    private ArrayList<String> directions;


    public Directions(){

        this.directions = new ArrayList<>();
    }

    /**
     * Generate text directions based on an ArrayList<Node> path
     * @param path
     * @return ArrayList of Strings containing directions
     */
    public void generateTextDirections(ArrayList<Node> path) {
        ArrayList<String> directions = new ArrayList<>();
        double distance = 0.0;
        double pastDistance = 0.0;
        double angle = Math.toRadians(90.0);
        double pastAngle = Math.toRadians(90.0);
        double angleDiff;
        String words = "";
        String pastWords = "";
        String ft = "ft";
        Node node = null;
        Node pastNode = null;
        Node nextNode = null;
        boolean changeDirections = false;
        boolean floorChange = true;
        int direction;
        directions.add("Starting Route!");
        for (int i = 0; i < path.size()-1; i++) {
            // Set new nodes
            pastNode = node;
            node = path.get(i);
            nextNode = path.get(i+1);

            System.out.println("pastNode: " + pastNode + " node: " + node + " nextNode: " + nextNode);

            if (pastNode != null) {
                // Set new values according to new nodes
                distance += node.distanceBetweenNodes(pastNode);
                angle = node.angleBetweenNodes(nextNode);
                pastAngle = pastNode.angleBetweenNodes(node);

                direction = angleState(angle, pastAngle, node, pastNode);
                if(floorChange){
                    directions.add("Floor change to " + node.getFloor().toString());
                }
                floorChange = false;

                switch (direction) {
                    case STAIRS:
                        System.out.println("Stairs");
                        words = "Take the stairs " + floorsMessage(pastNode, node);
                        directions.add(words);
                        changeDirections = true;
                        floorChange = true;
                        break;

                    case ELEVATORS:
                        System.out.println("Elevator");
                        words = "Take the elevator " + floorsMessage(pastNode, node);
                        directions.add(words);
                        changeDirections = true;
                        floorChange = true;
                        break;

                    case TURN_AROUND:
                        System.out.println("Turn around");
                        words = "Turn around at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case HARD_LEFT:
                        System.out.println("Hard left");
                        words = "Make a hard left at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case HARD_RIGHT:
                        System.out.println("Hard right");
                        words = "Make a hard right at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case LEFT:
                        System.out.println("Left");
                        words = "Make a left at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case RIGHT:
                        System.out.println("Right");
                        words = "Make a right at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case SLIGHT_LEFT:
                        System.out.println("Slight left");
                        words = "Make a slight left at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case SLIGHT_RIGHT:
                        System.out.println("Slight right");
                        words = "Make a slight right at " + node.getLongName();
                        changeDirections = true;
                        break;

                    case STRAIGHT:
                        System.out.println("Straight");
                        if (!changeDirections) {
//                            distance += node.distanceBetweenNodes(pastNode);
                        } else {
//                            distance = 0;
                            changeDirections = false;
                        }
                        break;
                }

                if (changeDirections && !floorChange) {
                    System.out.println("direction changes!");
                    System.out.println("distance: " + distance * Main.settings.getFeetPerPixel());
                    directions.add(getDistanceString(distance, node, pastNode));
                    directions.add(words);
                    distance = 0;
                }

                System.out.println("");
            }
        }
        directions.add(getDistanceString(distance, nextNode, node));
        directions.add("You have arrived at your destination!");
        setDirections(directions);
    }


    private String getDistanceString(double distance, Node node, Node pastNode) {
        distance += node.distanceBetweenNodes(pastNode);
        return ("Go straight " + roundToNearest(distance * Main.settings.getFeetPerPixel(), 10) + " ft to " + node.getLongName());
    }


    private int roundToNearest(double value, int roundTo) {
        value += roundTo/2;
        int retVal = (int) value / roundTo;
        return retVal * roundTo;
    }
    private String floorsMessage(Node node1, Node node2){
        String message;
        if(node1.floorToInt() > node2.floorToInt()){
            message = "down ";
        } else{
            message = "up ";
        }
        int floorsBetween = node1.floorsBetweenNodes(node2);

        switch(floorsBetween){
            case 5:
                message += "5 floors to floor " + node2.getFloor().toString() + ".";
                break;

            case 4:
                message += "4 floors to floor " + node2.getFloor().toString() + ".";
                break;

            case 3:
                message += "3 floors to floor " + node2.getFloor().toString() + ".";
                break;

            case 2:
                message += "2 floors to floor " + node2.getFloor().toString() + ".";
                break;

            case 1:
                message += "1 floors to floor " + node2.getFloor().toString() + ".";
                break;
        }
        return message;
    }

    private int angleState(double angle, double pastAngle, Node node1, Node node2){
        double angleDiff = angle - pastAngle;
        angleDiff = Math.toDegrees(angleDiff);

        if (angleDiff < -180) {
            angleDiff += 360;
        }
        if (angleDiff > 180) {
            angleDiff -= 360;
        }

//        System.out.println("angleDiff: " + angleDiff);

        if (node1.getType().equals(Node.nodeType.STAI) && node2.getType().equals(Node.nodeType.STAI)) {
            return STAIRS;

        } else if (node1.getType().equals(Node.nodeType.ELEV) && node2.getType().equals(Node.nodeType.ELEV)) {
            return ELEVATORS;

        } else if (angleDiff <= -HARD_BOUNDS && angleDiff > -TURN_AROUND_BOUNDS) {
            return TURN_AROUND;

        } else if (angleDiff <= -NORMAL_BOUNDS && angleDiff > -HARD_BOUNDS) {
            return HARD_LEFT;

        } else if (angleDiff <= -SLIGHT_BOUNDS && angleDiff > -NORMAL_BOUNDS) {
            return LEFT;

        } else if (angleDiff <= -STRAIGHT_BOUNDS && angleDiff > -SLIGHT_BOUNDS) {
            return SLIGHT_LEFT;

        } else if (angleDiff <= STRAIGHT_BOUNDS && angleDiff >= -STRAIGHT_BOUNDS) {
            return STRAIGHT;

        } else if (angleDiff <= SLIGHT_BOUNDS && angleDiff > STRAIGHT_BOUNDS) {
            return SLIGHT_RIGHT;

        } else if (angleDiff <= NORMAL_BOUNDS && angleDiff > SLIGHT_BOUNDS) {
            return RIGHT;

        } else if (angleDiff <= HARD_BOUNDS && angleDiff > NORMAL_BOUNDS) {
            return HARD_RIGHT;

        } else if (angleDiff <= TURN_AROUND_BOUNDS && angleDiff > HARD_BOUNDS){
            return TURN_AROUND;
        }
        return 0;
    }



    public ArrayList<String> getDirections() {
        return directions;
    }

    public void setDirections(ArrayList<String> directions) {
        this.directions = directions;
    }

    public  void clearDirections(){
        this.directions.clear();
    }

}