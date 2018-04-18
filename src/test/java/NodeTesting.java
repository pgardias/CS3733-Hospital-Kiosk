
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class NodeTesting {


        private Node node1;
        private Node node2;
        private Edge sampleEdge1;
        private Edge sampleEdge2;
        private ArrayList<Edge> edgeList;

        @Before
        public void setup(){
            node1 = new Node( "node1LongName",  true, 1, 1, 1234.9, 13, Node.floorType.LEVEL_1, Node.buildingType.FRANCIS_45, Node.nodeType.CONF);
            node1.setID("node1ID");

            node2 = new Node();
            node2.setID("Node2ID");

            edgeList = new ArrayList<>();

            sampleEdge1 = new Edge();
            sampleEdge2 = new Edge();
            sampleEdge1.setID("edgeID1");
            sampleEdge2.setID("edgeID2");
            sampleEdge1.setStart(node1);
            sampleEdge1.setEnd(node2);
            sampleEdge2.setStart(node2);
            sampleEdge2.setEnd(node1);


            edgeList.add(sampleEdge1);
            edgeList.add(sampleEdge2);
            node1.setEdges(edgeList);
            node2.setEdges(edgeList);

        }

//            edge1.setStart(node1);
//            edge1.setEnd(node2);
//            edgeList.add(edge1);
//            node1.addEdges(edgeList);
//            node2.addEdges(edgeList);

//    public Edge sampleEdge2() {
//        Edge edge1 = new Edge();
//
//        node2.setID("node1ID");
//        edge1.setStart(node2);
//        edge1.setEnd(node1);
//        edgeList.add(edge1);
//        node1.addEdges(edgeList);
//        node2.addEdges(edgeList);
//
//        return edge1;
//    }



    @Test
    public void testHasParent(){
        assertFalse(node1.hasParent());
        node1.setParent(node2);
        assertTrue(node1.hasParent());
    }

    @Test
    public void testEquals(){
        assertFalse(node1.equals(node2));
        node2.setID("node1ID");
        assertTrue(node1.equals(node2));
    }

    @Test
    public void testDistanceBetweenPoints(){
        System.out.println(node2.getID());
        node2.setX(1);
        node2.setY(1);
        assertEquals(0,node1.distanceBetweenNodes(node2), 0);

        node2.setX(2);
        node2.setY(1);
        assertEquals(1,node1.distanceBetweenNodes(node2), 0);

        node2.setX(1);
        node2.setY(2);
        assertEquals(1,node1.distanceBetweenNodes(node2), 0);
    }

    @Test
    public void testGetEdge() {
        assertFalse(sampleEdge1.equals(node1.getEdge(node2)));
        assertTrue(sampleEdge2.equals(node2.getEdge(node1)));
    }



    @Test
    public void testFloorsBetweenNodes(){
        Node nodeTest1 = new Node();
        nodeTest1.setFloor(Node.floorType.LEVEL_3);

        Node nodeTest2 = new Node();
        nodeTest2.setFloor(Node.floorType.LEVEL_L2);

        assertEquals("down 5 floors to floor L2.", nodeTest1.floorsBetweenNodes(nodeTest2));
    }


}
