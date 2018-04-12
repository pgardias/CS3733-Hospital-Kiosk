import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

/*
import edu.wpi.cs3733d18.teamp.Database.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;

import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.NodeRepo;
import edu.wpi.cs3733d18.teamp.Database.DBHandler;


public class NodeRepoTests {

    private static DBSystem db;
    private Node node1;
    private Node node2;
    private Node node3;

    @BeforeClass
    public static void init(){
        db = DBSystem.getInstance();
        db.init();

    }

    @Before
    public void setup(){

        node1 = new Node();
        node1.setID("LABS00102");
        node1.setLongName("Node1LongName");
        node1.setType(Node.nodeType.LABS);
        node1.setX(12);
        node1.setY(12);
        node1.setxDisplay(23);
        node1.setyDisplay(23);
        node1.setBuilding(Node.buildingType.FRANCIS_45);
        node1.setFloor(Node.floorType.LEVEL_2);
        node2 = new Node();
        node2.setID("TEST");
    }


    @Test(expected = NodeNotFoundException.class)
    public void testGetOneNodeFail() throws Exception{
        db.getOneNode(node2.getID());
    }

    @Test
    public void testGetOneNodePass() throws Exception{
        try{
            db.getOneNode("DELEV00A02");
        }catch (NodeNotFoundException e) {
            assertNull(e);
        }
    }


    @Test
    public void testCreateNode() throws Exception{

        try{
            db.createNode(node1);
            db.getOneNode("PLABS00202");
        }catch (NodeNotFoundException e){
            assertNull(e);
        }
    }

    @Test(expected = NodeNotFoundException.class)
    public void testDeleteNode()throws Exception{
            db.deleteNode("PLABS00202");
            db.getOneNode("PLABS00202");
    }


    @Test
    public void testGetAllNodes()throws Exception{
        assertEquals(84, db.getAllNodes().size());
        db.createNode(node1);
        assertEquals(85, db.getAllNodes().size());
        db.deleteNode("PLABS00202");
    }


    @Test
    public void testEnum() {
        Node node1= new Node();
        node1.setBuilding(Node.buildingType.FRANCIS_45);
        node1.setFloor(Node.floorType.LEVEL_L1);
        node1.setType(Node.nodeType.DEPT);

        assertEquals("PDEPT005L1", db.generateID(node1));

    }


//    @Test
//    public void testEdges() {
//
//        NodeRepo nodeRepo = new NodeRepo();
//        EdgeRepo edgeRepo = new EdgeRepo();
//
//        Node node22 = new Node();
//        node22.setFloor("1");
//        node22.setLongName("fjhskhg");
//        node22.setType(Node.nodeType.CONF);
//        node22.setBuilding("building");
//
//        Node node5 = new Node();
//        node5.setFloor("2");
//        node5.setLongName("fjhg");
//        node5.setType(Node.nodeType.HALL);
//        node5.setBuilding("building");
//
//        db.createNode(node22);
//        nodeRepo.createNode(node5);
//
//
//        Edge edge1 = new Edge();
//        edge1.setStart(node22);
//        edge1.setEnd(node5);
//        edgeRepo.createEdge(edge1);
//
//        Node node6 = new Node();
//        node6.setFloor("2");
//        node6.setLongName("fjhg");
//        node6.setType(Node.nodeType.HALL);
//        node6.setBuilding("building");
//
//        try {
//
//            Node nodeA = nodeRepo.getOneNode("PCONF00301");
//            Node nodeB = nodeRepo.getOneNode("PHALL06202");
//
//            Edge edge1 = new Edge();
//            edge1.setStart(nodeA);
//            edge1.setEnd(nodeB);
//
//            edgeRepo.createEdge(edge1);
//
//
//            Node node6 = nodeRepo.getOneNode("PHALL05902");
//            Edge edgeA = edgeRepo.getOneEdge("PCONF00301_PHALL06202");
//            edgeA.setStart(node6);
//            edgeRepo.modifyEdge(edgeA);
//
//        } catch (NodeNotFoundException n) {
//            n.printStackTrace();
//        } catch (EdgeNotFoundException e) {
//            e.printStackTrace();
//        }

    //}

    @AfterClass
    public static void shutoff(){
        db.shutdown();
    }

}*/
