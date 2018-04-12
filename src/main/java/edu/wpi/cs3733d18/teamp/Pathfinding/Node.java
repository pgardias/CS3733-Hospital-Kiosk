package edu.wpi.cs3733d18.teamp.Pathfinding;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Node {
    public enum nodeType {
        KIOS {
            @Override
            public final String toString() {
                return "Kiosk";
            }
        },
        CONF {
            @Override
            public final String toString() {
                return "Conference";
            }
        },
        HALL {
            @Override
            public final String toString() {
                return "Hall";
            }
        },
        DEPT {
            @Override
            public final String toString() {
                return "Department";
            }
        },
        INFO {
            @Override
            public final String toString() {
                return "Information";
            }
        },
        LABS {
            @Override
            public final String toString() {
                return "Labs";
            }
        },
        REST {
            @Override
            public final String toString() {
                return "Restroom";
            }
        },
        SERV {
            @Override
            public final String toString() {
                return "Service";
            }
        },
        STAI {
            @Override
            public final String toString() {
                return "Stair";
            }
        },
        EXIT {
            @Override
            public final String toString() {
                return "Exit";
            }
        },
        RETL {
            @Override
            public final String toString() {
                return "Retail";
            }
        },
        ELEV {
            @Override
            public final String toString() {
                return "Elevator";
            }
        }
    }

    /**
     * stringToNodeType converts a String from the database to
     * a valid nodeType to be put in a Node object
     * @param str String to be converted
     * @return valid nodeType
     */
    public static nodeType stringToNodeType(String str) {
        switch (str) {
            case "KIOS":
            case "Kiosk":
                return nodeType.KIOS;
            case "CONF":
            case "Conference":
                return nodeType.CONF;
            case "HALL":
            case "Hall":
                return nodeType.HALL;
            case "DEPT":
            case "Department":
                return nodeType.DEPT;
            case "INFO":
            case "Information":
                return nodeType.INFO;
            case "LABS":
            case "Labs":
                return nodeType.LABS;
            case "REST":
            case "Restroom":
                return nodeType.REST;
            case "SERV":
            case "Service":
                return nodeType.SERV;
            case "STAI":
            case "Stairs":
                return nodeType.STAI;
            case "EXIT":
            case "Exit":
                return nodeType.EXIT;
            case "RETL":
            case "Retail":
                return nodeType.RETL;
            case "ELEV":
            case "Elevator":
                return nodeType.ELEV;
            default:
                System.out.println("EnumConstantNotPresentException THROWN WITH STRING: " + str);
                return null;
        }
    }

    public enum buildingType {
        BTM {
            @Override
            public final String toString() {
                return "Building for Transformative Medicine";
            }
        },
        SHAPIRO {
            @Override
            public final String toString() {
                return "Shapiro";
            }
        },
        TOWER {
            @Override
            public final String toString() {
                return "Tower";
            }
        },
        FRANCIS_45 {
            @Override
            public final String toString() {
                return "45 Francis";
            }
        },
        FRANCIS_15 {
            @Override
            public final String toString() {
                return "15 Francis";
            }
        }
    }

    /**
     * stringToBuildingType converts a String from the database to
     * a valid buildingType to be put in a Node object
     * @param str String to be converted
     * @return valid buildingType
     */
    public static buildingType stringToBuildingType(String str){
        switch (str) {
            case "BTM":
            case "Building for Transformative Medicine":
                return buildingType.BTM;
            case "SHAPIRO":
            case "Shapiro":
                return buildingType.SHAPIRO;
            case "TOWER":
            case "Tower":
                return buildingType.TOWER;
            case "FRANCIS_45":
            case "45 Francis":
                return buildingType.FRANCIS_45;
            case "FRANCIS_15":
            case "15 Francis":
                return buildingType.FRANCIS_15;
            default:
                System.out.println("EnumConstantNotPresentException THROWN WITH STRING: " + str);
                return null;
        }
    }

    public enum floorType {
        LEVEL_L2 {
            @Override
            public final String toString() {
                return "L2";
            }
        },
        LEVEL_L1 {
            @Override
            public final String toString() {
                return "L1";
            }
        },
        LEVEL_G {
            @Override
            public final String toString() {
                return "G";
            }
        },
        LEVEL_1 {
            @Override
            public final String toString() {
                return "1";
            }
        },
        LEVEL_2 {
            @Override
            public final String toString() {
                return "2";
            }
        },
        LEVEL_3 {
            @Override
            public final String toString() {
                return "3";
            }
        }
    }

    /**
     * stringToFloorType converts a String from the database to
     * a valid floorType to be put in a Node object
     * @param str String to be converted
     * @return valid floorType
     */
    public static floorType stringToFloorType(String str){
        switch (str) {
            case "LEVEL_L2":
            case "L2":
                return floorType.LEVEL_L2;
            case "LEVEL_L1":
            case "L1":
                return floorType.LEVEL_L1;
            case "LEVEL_G":
            case "G":
                return floorType.LEVEL_G;
            case "LEVEL_1":
            case "1":
                return floorType.LEVEL_1;
            case "LEVEL_2":
            case "2":
                return floorType.LEVEL_2;
            case "LEVEL_3":
            case "3":
                return floorType.LEVEL_3;
            default:
                System.out.println("EnumConstantNotPresentException THROWN WITH STRING: " + str);
                return null;
        }
    }

    private String id;
    private String longName;
    private String shortName;
    private Boolean isActive;
    private double x;
    private double y;
    private double xDisplay;
    private double yDisplay;
    private floorType floor;
    private buildingType building;
    private nodeType type;

    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private Node parent;

    /**
     * Empty Node constructor
     */
    public Node() {
    }

    /**
     * Constructor for Node entity
     *
     * @param longName long name
     * @param isActive state of the node
     * @param x        2D map x coordinate
     * @param y        2D map y coordinate
     * @param xDisplay 3D map x coordinate
     * @param yDisplay 3D map y coordinate
     * @param type     enum nodeType
     */
    public Node(String longName, Boolean isActive, double x, double y, double xDisplay, double yDisplay, floorType floor, buildingType building, nodeType type) {
        this.id = null;
        this.longName = longName;
        this.shortName = null;
        this.isActive = isActive;
        this.x = x;
        this.y = y;
        this.xDisplay = xDisplay;
        this.yDisplay = yDisplay;
        this.floor = floor;
        this.building = building;
        this.type = type;
        this.edges = null;
        this.parent = null;
    }

    /**
     * Checks if node has a parent
     *
     * @return boolean for if it has a parent or not
     */
    public Boolean hasParent() {
        if (parent != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Overrides the toString() method to print the ID
     */
    @Override
    public String toString() {
        return this.id;
    }

    /**
     * Gets the edge connecting two nodes
     *
     * @param n the node to find the edge between
     * @return the edge between teh two nodes, or null if none exist
     */
    public Edge getEdge(Node n) {
//        System.out.println("this node in getEdge: " + this.toString());
//        System.out.println("dest node in getEdge: " + n.toString());
        Edge edge = null;
        for (Edge e : this.edges) {
//            System.out.println("Edge in getEdge: " + e.toString());
//            System.out.println("node1 in getEdge: " + e.getNode1().toString());
//            System.out.println("node2 in getEdge: " + e.getNode2().toString());
            if (e.getStart().equals(n)) {
//                System.out.println("gucci");
                edge = e;
            }
            if (e.getEnd().equals(n)) {
//                System.out.println("gucci");
                edge = e;
            }
        }
        return edge;
    }

    /**
     * Checks if a node is the same as this node
     *
     * @param comp the node to compare to
     * @return boolean for if the nodes are equal or not
     */
    public boolean equals(Node comp) {
        if (this.id.equals(comp.id)) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the distance between this node and another node
     *
     * @param node the other node to calculate distance to
     * @return the distance value
     */
    public Double distanceBetweenNodes(Node node) {
        return Math.hypot(this.x - node.getX(), (this.y - node.getY()));
    }

    /**
     * Calculates the angle between this node and another node
     * @param node the other node to calculate distance to
     * @return the angle value (in radians)
     */
    public Double angleBetweenNodes(Node node) {
        return Math.atan2((node.getY() - this.y), (node.getX() - this.x));
    }

//    /**
//     * Gets a path between one node and this node
//     *
//     * @param n the node to find the path to
//     * @return array of nodes in the path
//     */
//    private ArrayList<Node> getPath(Node n) {
//        ArrayList<Node> tempPath = new ArrayList<Node>();
////        System.out.println("Node given: " + n.toString());
//        while (n.hasParent()) {
////            System.out.println("Parent: " + n.getParent());
//            tempPath.add(n);
//            n = n.getParent();
//        }
//        tempPath.add(n);
////        System.out.println("tempPath: " + tempPath);
//        ArrayList<Node> path = (ArrayList<Node>) tempPath.clone();
//
////        System.out.println("tempPath.size(): " + tempPath.size());
////        System.out.println("path: " + path);
////        System.out.println("path.size(): " + path.size());
//        for (int i = 0; i < tempPath.size() / 2; i++) {
//            path.set(tempPath.size() - i - 1, tempPath.get(i));
//            path.set(i, tempPath.get(tempPath.size() - i - 1));
//        }
//        return path;
//    }

//    /**
//     * A* algorithm that finds the shortest distance between two nodes
//     *
//     * @param destNode the end node for the path
//     * @return the list of nodes on the path to the destination node
//     */
//    public ArrayList<Node> AStar(Node destNode) {
////        System.out.println("This node: " + this + " Destination: " + destNode);
//
//        // Total cost from start node
//        HashMap<String, Double> gScore = new HashMap<String, Double>();
//
//        // Total cost to end node
//        HashMap<String, Double> fScore = new HashMap<String, Double>();
//
//        // Path of nodes to return
//        ArrayList<Node> path = new ArrayList<Node>();
//        path.add(this); //Add start node to path
//
//        // Nodes to explore in the future
//        // Really should be a key/value priority queue sorted by fScore
//        HashSet<Node> openSet = new HashSet<Node>();
//        openSet.add(this);
//
//        // Add gScore for current node
//        gScore.put(this.id, 0.0);
//
//        // add fScore for current node
//        fScore.put(this.id, this.distanceBetweenNodes(destNode));
//
//        // Nodes that have been explored
//        HashSet<Node> closedSet = new HashSet<Node>();
//        closedSet.add(this);
//
//        // Check the current node
////        if (this.equals(destNode)) {
////            System.out.println("We good");
////        } else {
////            System.out.println("Destination not equal to start...looking to neighbors");
////            System.out.println(edges.toString());
////        }
//
//        this.parent = null;
//
//        // Unexplored node with shortest distance to goal
//        // Initialize to first item in openSet
//        // (if only 1 item then it is the shortest to goal)
//        Node currentNode = this;
//
////        System.out.println("Start node: " + currentNode);
////        System.out.println("Edges of start: " + currentNode.getEdges());
//
//        // Start searching
//        while (!openSet.isEmpty()) {
////            System.out.println("Starting iteration on new Node <--------------------------------------------------------------");
//            // Temporary fScore value for comparing to current fScore
//            // Since we want the lowest fScore, make the default super large
//            double tempFScore = Double.POSITIVE_INFINITY;
//
////            System.out.println("getting next iterator for openset");
//
////            System.out.println("finding new node to explore");
//            // Get item in openSet that has smallest
//            for (Node tempNode : openSet) {
////                System.out.println("iterating through openSet");
////                System.out.println("currently iterating through: " + n.toString());
//                if (fScore.get(tempNode.getID()) < tempFScore) {
////                    System.out.println("Found possible node to explore: " + fScore.get(n.getID()) + " less than: " + tempFScore);
//                    tempFScore = fScore.get(tempNode.getID());
//                    currentNode = tempNode;
//                }
//            }
////            System.out.println("openSet: " + openSet);
////            System.out.println("closedSet: " + closedSet);
//            openSet.remove(currentNode);
//            closedSet.add(currentNode);
//
////            System.out.println("Found new node to explore: " + currentNode.toString());
////            System.out.println("This node has edges: " + currentNode.getEdges());
//
//            // Check if we found the goal
//            if (currentNode.equals(destNode)) {
////                System.out.println("We good");
//                path = getPath(currentNode);
//                return path;
//            }
//
//            // Add neighbors to openSet
//            for (Edge e : currentNode.getEdges()) {
//                // Temporary variable for comparing gScores
//                Double tempGScore = 0.0;
//
////                System.out.println("Adding new neighbor! <---------------------------------");
//
//                // Set neighbor to the node in edge that is not current node
//                Node neighbor;
//                if (e.getStart().equals(currentNode)) {
//                    neighbor = e.getEnd();
//                } else {
//                    neighbor = e.getStart();
//                }
//                if (!closedSet.contains(neighbor)) {
////                    System.out.println("Edge: " + e.toString());
////                    System.out.println("Neighbor: " + neighbor.toString());
////
////                    System.out.println("Current node gScore: " + gScore.get(currentNode.getID()));
////
////                    System.out.println("Distance between current node and neighbor: " + currentNode.distanceBetweenNodes(neighbor));
//
//                    // Add neighbor gScore value to gScore
//                    tempGScore = gScore.get(currentNode.getID()) + e.getWeight();// currentNode.distanceBetweenNodes(neighbor);
//
//                    if (!gScore.containsKey(neighbor.getID())) {
////                        System.out.println("setting parent");
//
//                        // Set parent for recreating the path later
//                        neighbor.setParent(currentNode);
//
////                        System.out.println("Parent: " + neighbor.getParent());
////
////                        System.out.println("putting tempGScore");
//
//                        // Set the gScore for neighbor to tempGScore
//                        gScore.put(neighbor.getID(), tempGScore);
//
//                        // Calculate tempFScore for readability
//                        tempFScore = gScore.get(neighbor.getID()) + destNode.distanceBetweenNodes(neighbor);
////                        System.out.println("tempFScore: " + tempFScore);
////
////                        System.out.println("Setting fScore");
//                        // Set fScore for neighbor to tempFScore
//                        fScore.put(neighbor.getID(), tempFScore);
//                    }
////                    System.out.println("adding neighbor");
//
//                    // Add neighbor nodes with weights to the openSet
//                    openSet.add(neighbor);
//                }
//            }
////            System.out.println("fScore: " + fScore);
////            System.out.println("gScore: " + gScore);
//        }
//
//        System.out.println("Path not found!");
//        return null;
//    }

    /**
     * converts the enum to ints
     * @return
     */
    public int floorToInt(){
        int floor = 0;
        switch(this.floor){
            case LEVEL_L2:
                floor = -2;
                break;
            case LEVEL_L1:
                floor = -1;
                break;
            case LEVEL_G:
                floor = 0;
                break;
            case LEVEL_1:
                floor = 1;
                break;
            case LEVEL_2:
                floor = 2;
                break;
            case LEVEL_3:
                floor = 3;
        }
        return floor;
    }

    public String floorsBetweenNodes(Node node){
        int floorsBetween = Math.abs(this.floorToInt() - node.floorToInt());
        String message = "";
        switch(floorsBetween){
            case 5:
                if(this.floorToInt() > 0){
                    message = "down 5 floors to floor " + node.getType().toString() + ".";
                }else{
                    message = "up 5 floors to floor " + this.getType().toString() + ".";
                }
                break;

            case 4:
                if (this.floorToInt() > 0){
                    message = "down 4 floors to floor " + node.getType().toString() + ".";
                }else{
                    message = "up 4 floors to floor " + this.getType().toString() + ".";
                }
                break;

            case 3:
                if (this.floorToInt() > 0){
                    message = "down 3 floors to floor " + node.getType().toString() + ".";
                }else{
                    message = "up 3 floors to floor " + this.getType().toString() + ".";
                }
                break;

            case 2:
                if(this.floorToInt() > node.floorToInt()){
                    message = "down 2 floors to floor " + node.getType().toString() + ".";
                }else{
                    message = "up 2 floors to floor " + this.getType().toString() + ".";
                }
                break;

            case 1:
                if(this.floorToInt() > node.floorToInt()){
                    message = "down 1 floors to floor " + node.getType().toString() + ".";
                }else{
                    message = "up 1 floors to floor " + this.getType().toString() + ".";
                }
                break;
        }
        return message;

    }


    // Getters
    public Node getParent() {
        return parent;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public String getShortName() {
        return shortName;
    }

    public String getID() {
        return id;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public String getLongName() {
        return longName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public double getxDisplay() {
        return xDisplay;
    }

    public double getyDisplay() {
        return yDisplay;
    }

    public floorType getFloor() {
        return floor;
    }

    public buildingType getBuilding() {
        return building;
    }

    public nodeType getType() {
        return type;
    }

    // Setters
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setxDisplay(double xDisplay) {
        this.xDisplay = xDisplay;
    }

    public void setyDisplay(double yDisplay) {
        this.yDisplay = yDisplay;
    }

    public void setFloor(floorType floor) {
        this.floor = floor;
    }

    public void setBuilding(buildingType building) {
        this.building = building;
    }

    public void setType(nodeType type) {
        this.type = type;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }
}
