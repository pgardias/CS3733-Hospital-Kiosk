/*
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Database.DBHandler;
import edu.wpi.cs3733d18.teamp.Edge;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Node;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTesting {

    private static DBSystem database;
    private static DBSystem data;
    private Node node1;
    private Node node2;
    private Edge edge1;

    @BeforeClass
    public static void initial(){
        data = database.getInstance();
    }

    @Before
    public void setUp(){

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

        edge1 = new Edge();

        edge1.setEnd(node1);
        edge1.setStart(node2);
    }

    @AfterClass
    public static void shutOff(){
        data.shutdown();
    }

    @Test(expected = NodeNotFoundException.class)
    public void testGetOneNode() throws Exception{
        System.out.println(node2.getID());
        data.getOneNode(node2.getID());
    }

//    @Test
//    public void testGetAllNodes() throws Exception{
//        assertEquals(85, data.getAllNodes().size());
//        data.createNode(node1);
//        assertEquals(86, data.getAllNodes().size());
//        data.deleteNode(node1.getID());
//    }

    @Test
    public void testGetAllEdges() throws Exception{
            try{
            System.out.println(data.getAllEdges().size());
            assertEquals(86, data.getAllEdges().size());
            data.createEdge(edge1);
            assertEquals(87, data.getAllEdges().size());
            data.deleteNode(edge1.getID());
        }catch (Exception e) {
        }
    }

}*/
