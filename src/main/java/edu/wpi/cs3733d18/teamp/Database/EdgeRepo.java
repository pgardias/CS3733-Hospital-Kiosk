package edu.wpi.cs3733d18.teamp.Database;

import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Exceptions.EdgeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EdgeRepo {

    // Database URL
    private static final String DB_URL = "jdbc:derby:KioskDB;create=true";

    // Database connection
    private static Connection conn;

    EdgeRepo() {}

    /**
     * getAllEdges creates Node objects for every row in the EDGE_INFO
     * table, and adds them to a Hashmap
     *
     * @return Hashmap<String ,   Edge> of all edge in EDGE_INFO table
     */
    HashMap<String, Edge> getAllEdges() throws EdgeNotFoundException {
        HashMap<String, Edge> allEdges = new HashMap<>();
        NodeRepo repo = new NodeRepo();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EDGE_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);

            if (!results.next()) {
                throw new EdgeNotFoundException();
            } else {
                do {
                    // Find edge's nodes
                    Node node1 = repo.getOneNode(results.getString("startNode"));
                    Node node2 = repo.getOneNode(results.getString("endNode"));

                    // Fill out edge attributes with ID and Nodes
                    Edge edge = new Edge(results.getString("edgeID"), node1, node2);
                    edge.setActive(results.getBoolean("active"));

                    allEdges.put(edge.getID(), edge);
                }
                while (results.next());
            }

                results.close();
                stmt.close();
                conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch (NodeNotFoundException n) {
            n.printStackTrace();
        }

        return allEdges;
    }

    /**
     * getOneEdge creates one Edge objects for the row
     * of the database it selects with the given ID
     *
     * @param edgeID ID of the edge to be searched for
     * @return Edge object filled with its attributes
     */
    Edge getOneEdge(String edgeID) throws EdgeNotFoundException {
        NodeRepo repo = new NodeRepo();
        Edge edge = new Edge();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EDGE_INFO WHERE edgeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, edgeID);
            ResultSet results = pstmt.executeQuery();

            if (!results.next()) {
                throw new EdgeNotFoundException();
            }
            // Find edge's nodes
            Node node1 = repo.getOneNode(results.getString("startNode"));
            Node node2 = repo.getOneNode(results.getString("endNode"));

            // Fill out edge attributes with ID and Nodes
            edge = new Edge(results.getString("edgeID"), node1, node2);
            edge.setActive(results.getBoolean("active"));

            results.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }

        return edge;
    }

    /**
     * createEdge adds the information from a node object into NODE_INFO,
     * generating necessary fields from that information
     *
     * @param edge Edge to add
     * @return true if Edge was created, false if something went wrong
     */
    Boolean createEdge(Edge edge) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "INSERT INTO EDGE_INFO " +
                    "VALUES(?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Fill statement and execute
            pstmt.setString(1, edge.getStart().getID() + "_" + edge.getEnd().getID());
            pstmt.setString(2, edge.getStart().getID());
            pstmt.setString(3, edge.getEnd().getID());
            pstmt.setInt(4, 1);

            int success = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return (success > 0);
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

    /**
     * modifyEdge updates the value in a specific row in the
     * EGDE_INFO table
     * @param edge contains information to be updated
     * @return true if Edge was updated, false if something went wrong
     */
    Boolean modifyEdge(Edge edge) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "UPDATE EDGE_INFO " +
                    "SET edgeID = ?, startNode = ?, endNode = ?, active = ? " +
                    "WHERE edgeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Fill statement and execute
            pstmt.setString(1, edge.getStart().getID() + "_" + edge.getEnd().getID());
            pstmt.setString(2, edge.getStart().getID());
            pstmt.setString(3, edge.getEnd().getID());
            pstmt.setInt(4, edge.getActive() ? 1 : 0);
            pstmt.setString(5, edge.getID()); // Specify which entry to update

            int success = pstmt.executeUpdate();

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
     * deleteEdge removes a edge from the EDGE_INFO table
     * @param edgeID ID of edge to be removed
     * @return true if the edge is deleted, false if something went wrong
     */
    Boolean deleteEdge(String edgeID) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "DELETE FROM EDGE_INFO WHERE edgeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, edgeID);

            int success = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return (success > 0);
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

    /**
     * getEdgesForNode takes in a node and returns all edges to which the given node is connected
     * @param node
     * @return ArrayList of edges connected to the given node
     * @throws EdgeNotFoundException
     */
    ArrayList<Edge> getEdgesForNode(Node node) throws EdgeNotFoundException {
        ArrayList<Edge> edges = new ArrayList<>();
        NodeRepo repo = new NodeRepo();
        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EDGE_INFO WHERE startNode = ? OR endNode = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, node.getID());
            pstmt.setString(2, node.getID());
            ResultSet results = pstmt.executeQuery();

            if (!results.next()) {
                throw new EdgeNotFoundException();
            } else {
                do {
                    // Find edge's nodes
                    Node node1 = repo.getOneNode(results.getString("startNode"));
                    Node node2 = repo.getOneNode(results.getString("endNode"));

                    // Fill out edge attributes with ID and Nodes
                    Edge edge = new Edge(results.getString("edgeID"), node1, node2);
                    edge.setActive(results.getBoolean("active"));

                    edges.add(edge);
                }
                while (results.next());
            }

            results.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        return edges;
    }

}
