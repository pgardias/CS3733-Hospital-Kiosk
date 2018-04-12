package edu.wpi.cs3733d18.teamp.Database;

import edu.wpi.cs3733d18.teamp.Exceptions.OrphanNodeException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Exceptions.EdgeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

import java.util.*;

public class MapStorage {

    // Persistent HashMap of nodes and edges to be kept up to date at all times
    private HashMap<String, Node> nodes = new HashMap<String, Node>();
    private HashMap<String, Edge> edges;

    MapStorage() {}

    /**
     * updateStorage gets all nodes, gets all edges, fills every edge with its two corresponding nodes,
     * and then fills every node with its corresponding edges. This allows for persistent storage,
     * which only will be updated on a node/edge create/modify/delete
     */
    Boolean update() throws NodeNotFoundException, EdgeNotFoundException {
        // Clear nodes Hashmap
        nodes.clear();
        // Initialize Repositories
        NodeRepo nodeRepo = new NodeRepo();
        EdgeRepo edgeRepo = new EdgeRepo();
        // Get all Nodes and edges
        HashMap<String, Node> allNodes = nodeRepo.getAllNodes();
        HashMap<String, Edge> allEdges = edgeRepo.getAllEdges();
        // For every node, add its edges to an ArrayList
        for (Node n : allNodes.values()) {
            ArrayList<Edge> edgeList;
                edgeList = edgeRepo.getEdgesForNode(n);
            for (Edge e : edgeList) {
                e.setStart(allNodes.get(e.getStart().getID()));
                e.setEnd(allNodes.get(e.getEnd().getID()));
            }
            n.setEdges(edgeList);
            nodes.put(n.getID(), n);
        }

        edges = allEdges;
        return true;
    }

    //Returns all nodes in the current HashMap of nodes corresponding to nodeIDs
    HashMap<String, Node> getNodes() {
        return nodes;
    }

    //Returns information about a node given a nodeID, throws exception if node is not in HashMap of nodes
    Node getOneNode(String nodeID) throws NodeNotFoundException {
        System.out.println("HERE");
        Node node = nodes.get(nodeID);
        if (node == null) {
            throw new NodeNotFoundException();
        }
        return node;
    }

    /**
     * getNodesOfType adds nodes to a new HashMap
     * if they have the same nodeType as the given one
     * @param nodeType NodeType to return
     * @return all nodes of this type
     */
    HashMap<String, Node> getNodesOfType(Node.nodeType nodeType) {
        HashMap<String, Node> nodesOfType = new HashMap<>();

        for(Map.Entry<String, Node> entry : nodes.entrySet()) {
            String key = entry.getKey();
            Node n = entry.getValue();

            if (n.getType().equals(nodeType)) {
                nodesOfType.put(key, n);
            }
        }
        return nodesOfType;
    }

    //Return all edges in the HashMap of edges, from database
    HashMap<String, Edge> getEdges() {
        return edges;
    }

    // Returns information about an edge if the edgeID is given, if edge doesn't exist in table, throws exception
    Edge getOneEdge(String edgeID) throws EdgeNotFoundException {
        Edge edge = edges.get(edgeID);
        if (edge == null) {
            throw new EdgeNotFoundException();
        }
        return edge;
    }

    Boolean willEdgeOrphanANode(String edgeID) throws OrphanNodeException {
        Edge edge = edges.get(edgeID);
        Node start = nodes.get(edge.getStart().getID());
        Node end = nodes.get(edge.getEnd().getID());
        if (start.getEdges().size() == 1) {
            throw new OrphanNodeException(start.getID());
        }
        else if (end.getEdges().size() == 1) {
            throw new OrphanNodeException(end.getID());
        }
        return true;
    }
}
