package edu.wpi.cs3733d18.teamp.Database;

import edu.wpi.cs3733d18.teamp.Exceptions.DuplicateLongNameException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

import java.sql.*;
import java.util.HashMap;

public class NodeRepo {

    // Accesses Database
    // DBHandler

    // Database URL
    private static final String DB_URL = "jdbc:derby:KioskDB;create=true";

    // Database connection
    private static Connection conn;

    NodeRepo() {}

    /**
     * getAllNodes creates Node objects for every row in the NODE_INFO
     * table, and adds them to a Hashmap
     * @return Hashmap<String, Node> of all nodes in NODE_INFO table
     */
    HashMap<String, Node> getAllNodes() throws NodeNotFoundException {
        HashMap<String, Node> allNodes = new HashMap<>();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM NODE_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);

            if (!results.next()) {
                throw new NodeNotFoundException();
            } else {
                do {
                    Node node = new Node();

                    // Fill out node attributes with row from table
                    node.setID(results.getString("nodeID"));
                    node.setX(results.getInt("xcoord"));
                    node.setY(results.getInt("ycoord"));
                    node.setFloor(Node.stringToFloorType(results.getString("floor")));
                    node.setBuilding(Node.stringToBuildingType(results.getString("building")));
                    node.setLongName(results.getString("longName"));
                    node.setShortName(results.getString("shortName"));
                    node.setxDisplay(results.getInt("xcoord3d"));
                    node.setyDisplay(results.getInt("ycoord3d"));
                    node.setActive(results.getInt("isActive") == 1);
                    node.setType(Node.stringToNodeType(results.getString("nodeType")));

                    allNodes.put(node.getID(), node);
                }
                while (results.next());
            }

            results.close();
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return allNodes;
    }

    /**
     * getNodesOfType creates Node objects for every row in the NODE_INFO
     * table that is of the given type, and adds them to a Hashmap
     * @param type type to be filtered for
     * @return Hashmap<String, Node> of all nodes in NODE_INFO table
     */
    HashMap<String, Node> getNodesOfType(Node.nodeType type) throws NodeNotFoundException {
        HashMap<String, Node> nodes = new HashMap<>();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM NODE_INFO WHERE nodeType = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type.toString());
            ResultSet results = pstmt.executeQuery();

            if (!results.next()) {
                throw new NodeNotFoundException();
            } else {
                do {
                    Node node = new Node();

                    // Fill out node attributes with row from table
                    node.setID(results.getString("nodeID"));
                    node.setX(results.getInt("xcoord"));
                    node.setY(results.getInt("ycoord"));
                    node.setFloor(Node.stringToFloorType(results.getString("floor")));
                    node.setBuilding(Node.stringToBuildingType(results.getString("building")));
                    node.setLongName(results.getString("longName"));
                    node.setShortName(results.getString("shortName"));
                    node.setxDisplay(results.getInt("xcoord3d"));
                    node.setyDisplay(results.getInt("ycoord3d"));
                    node.setActive(results.getInt("isActive") == 1);
                    node.setType(node.getType());

                    nodes.put(node.getID(), node);
                }
                while (results.next());
            }

            results.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return nodes;
    }

    /**
     * getOneNode creates one Node objects for the row
     * of the database it selects with the given ID
     * @param nodeID ID of the node to be searched for
     * @return Node object filled with its attributes
     */
    Node getOneNode(String nodeID) throws NodeNotFoundException {
        Node node = new Node();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM NODE_INFO WHERE nodeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nodeID);
            ResultSet result = pstmt.executeQuery();

            if (!result.next()) {
                throw new NodeNotFoundException("NodeID: " + nodeID);
            }

                // Fill out node attributes with row from table
                node.setID(result.getString("nodeID"));
                node.setX(result.getInt("xcoord"));
                node.setY(result.getInt("ycoord"));
                node.setFloor(Node.stringToFloorType(result.getString("floor")));
                node.setBuilding(Node.stringToBuildingType(result.getString("building")));
                node.setLongName(result.getString("longName"));
                node.setShortName(result.getString("shortName"));
                node.setxDisplay(result.getInt("xcoord3d"));
                node.setyDisplay(result.getInt("ycoord3d"));
                node.setActive(result.getInt("isActive") == 1);
                node.setType(Node.stringToNodeType(result.getString("nodeType")));

            result.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return node;
    }

    /**
     * createNode adds the information from a node object into NODE_INFO,
     * generating necessary fields from that information
     * @param node Node to add
     * @return true if Node was created, false if something went wrong
     */
    Boolean createNode(Node node, Node connectedNode) throws DuplicateLongNameException {
        EdgeRepo edgeRepo = new EdgeRepo();
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Check database for other Nodes with user input long name
            checkLongName(node, conn);

            // Prepare statement
            String sql = "INSERT INTO NODE_INFO " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Fill statement and execute
            String newID = generateNodeID(node, conn);
            pstmt.setString(1, newID);
            pstmt.setInt(2, (int) node.getX());
            pstmt.setInt(3, (int) node.getY());
            pstmt.setString(4, node.getFloor().toString());
            pstmt.setString(5, node.getBuilding().toString());
            pstmt.setString(6, node.getType().name());
            pstmt.setString(7, node.getLongName());
            pstmt.setString(8, generateShortName(node, newID));
            pstmt.setString(9, "Team P");
            pstmt.setInt(10, (int) node.getxDisplay());
            pstmt.setInt(11, (int) node.getyDisplay());
            pstmt.setInt(12, 1);

            int success = pstmt.executeUpdate();

            Edge defaultEdge = new Edge();
            // Node chosen by user
            defaultEdge.setStart(connectedNode);
            // It needs this ID
            node.setID(newID);
            defaultEdge.setEnd(node);
            edgeRepo.createEdge(defaultEdge);

            pstmt.close();
            conn.close();

            return (success > 0);
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

    /**
     * modifyNode updates the value in a specific row in the
     * NODE_INFO table
     * @param node contains information to be updated
     * @return true if Node was updated, false if something went wrong
     */
    Boolean modifyNode(Node node) throws DuplicateLongNameException{
        EdgeRepo edgeRepo = new EdgeRepo();

        try {
            conn = DriverManager.getConnection(DB_URL);

            // Check database for other Nodes with user input long name
            checkLongName(node, conn);

            // Prepare statement
            String sql = "UPDATE NODE_INFO " +
                    "SET nodeID = ?, xcoord = ?, ycoord = ?, floor= ?, building = ?, nodeType = ?, longName = ?, " +
                    " shortName = ?, xcoord3d = ?, ycoord3d = ?, isActive = ? " +
                    "WHERE nodeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Fill statement and execute
            String newID = generateNodeID(node, conn);
            pstmt.setString(1, newID);
            pstmt.setInt(2, (int) node.getX());
            pstmt.setInt(3, (int) node.getY());
            pstmt.setString(4, node.getFloor().toString());
            pstmt.setString(5, node.getBuilding().toString());
            pstmt.setString(6, node.getType().name());
            pstmt.setString(7, node.getLongName());
            pstmt.setString(8, generateShortName(node, newID));
            pstmt.setInt(9, (int) node.getxDisplay());
            pstmt.setInt(10, (int) node.getyDisplay());
            pstmt.setInt(11, node.getActive() ? 1 : 0);
            pstmt.setString(12, node.getID()); // Specify which entry to update

            int success = pstmt.executeUpdate();

            String sql2 = "SELECT * FROM EDGE_INFO WHERE startNode = ? OR endNode = ?";
            pstmt = conn.prepareStatement(sql2);
            pstmt.setString(1, node.getID());
            pstmt.setString(2, node.getID());
            ResultSet results = pstmt.executeQuery();

            // Modify edges that contain this node to have the new ID
            String oldNodeID = node.getID();
            if (!results.next()) {
                //node is connected to no ID's
                return false;
            } do {
                if (results.getString("startNode").equals(oldNodeID)) {
                    Edge e = new Edge();
                    e.setID(oldNodeID + "_" + results.getString("endNode"));
                    node.setID(newID);
                    e.setStart(node);
                    Node otherNode = new Node();
                    otherNode.setID(results.getString("endNode"));
                    e.setEnd(otherNode);
                    e.setActive(true);
                    edgeRepo.modifyEdge(e);
                }
                else if (results.getString("endNode").equals(oldNodeID)) {
                    Edge e = new Edge();
                    e.setID(results.getString("startNode") + "_" + oldNodeID);
                    node.setID(newID);
                    e.setEnd(node);
                    Node otherNode = new Node();
                    otherNode.setID(results.getString("startNode"));
                    e.setStart(otherNode);
                    e.setActive(true);
                    edgeRepo.modifyEdge(e);
                }
            } while (results.next());

            pstmt.close();
            conn.close();

            return (success > 0);
        } catch(SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        }

        return false;
    }


    /**
     * deleteNode removes a node from the NODE_INFO table
     * as well as any edges it is connected to
     * @param nodeID ID of node to be removed
     * @return true if the node is deleted, false if something went wrong
     */
    Boolean deleteNode(String nodeID) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "DELETE FROM NODE_INFO WHERE nodeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nodeID);

            int success = pstmt.executeUpdate();

            // Delete all edges that connect to this node
            String sql2 = "DELETE FROM EDGE_INFO WHERE startNode = ? OR endNode = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setString(1, nodeID);
            pstmt2.setString(2, nodeID);

            pstmt2.executeUpdate();

            pstmt.close();
            conn.close();

            return (success > 0);
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

    /**
     * doesNodesExistHere looks through NODE_INFO for if a node
     * exists at given coordinates
     * @param xcoord x value of location
     * @param ycoord y value of location
     * @return true if a Node exists at these coordinates,
     * false if none exist here
     */
    Boolean doesNodeExistHere(int xcoord, int ycoord) {
        Boolean nodeExists = false;

        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "SELECT COUNT(*) FROM NODE_INFO WHERE xcoord = ? AND ycoord = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, xcoord);
            pstmt.setInt(2, ycoord);

            ResultSet results = pstmt.executeQuery();

            if (!results.next()) {
                nodeExists = false;
            }
            else if ((results.getInt(1) > 0)){
                nodeExists = true;
            }

            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return nodeExists;
    }

    /**
     * generateNodeID concatenates a node ID based on the
     * "Map Data Entry Instructions" given by Wong
     * @param node use node's fields to create ID
     * @param conn use this to access NODE_INFO
     * @return returns the unique String ID
     */
    public String generateNodeID(Node node, Connection conn) {
        String nodeNumber = "000";
        int max = 0;

        // Node Type naming convention
        try {
            // Get count of nodes with this type
            String sql = "SELECT nodeID FROM NODE_INFO WHERE nodeType = ? AND floor = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, node.getType().name());
            pstmt.setString(2, node.getFloor().toString());
            ResultSet results = pstmt.executeQuery();
            if (node.getType() == Node.nodeType.ELEV) {
                results.last();
                max = results.getRow();
                results.beforeFirst();
            }
            else {
                while (results.next()) {
                    String id = results.getString(1);
                    int numOfType;
                    numOfType = Integer.parseInt(id.substring(5, 8));
                    if (numOfType > max) {
                        max = numOfType;
                    }
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }

        if (node.getType() == Node.nodeType.ELEV) {
            int i = 65 + max;
            nodeNumber = String.valueOf((char) i);
        }
        else {
            // Convert int into 3 character string
            if (-1 < max && max < 9) {
                nodeNumber = "00" + (max + 1);
            }
            else if (max < 99) {
                nodeNumber = "0" + (max + 1);
            }
            else if (max < 999) {
                nodeNumber = String.valueOf((max + 1));
            }
        }

        // Floor naming convention
        String floorNumber;
        if (node.getFloor().toString().equals("1") || node.getFloor().toString().equals("2") ||
                node.getFloor().toString().equals("3") || node.getFloor().toString().equals("G")) {
            floorNumber = "0" + node.getFloor().toString();
        }
        else {
            floorNumber = node.getFloor().toString();
        }

        return "P" + node.getType().name() + nodeNumber + floorNumber;
    }

    /**
     * generateShortName generates a short name for the node
     * based off naming convention decided by team
     * @param node use this for node information
     * @param id will generate shortname if nodeID does not exist in node
     * @return the generated String for shortName
     */
    private String generateShortName(Node node, String id) {
        return node.getType().toString() + " " + id.substring(5,8) + " Floor " + node.getFloor();
    }

    private void checkLongName(Node node, Connection conn) throws DuplicateLongNameException{
        try {
            String sql = "SELECT nodeID FROM NODE_INFO " +
                    "WHERE longName = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, node.getLongName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString(1);
                conn.close();
                throw new DuplicateLongNameException(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
