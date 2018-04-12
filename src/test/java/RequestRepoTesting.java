/*

import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Exceptions.RequestNotFoundException;
import org.junit.*;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RequestRepoTesting {

    private Timestamp timeMade1;
    private Timestamp timeCompleted;
    private boolean completed;

    private static DBHandler db;
    private Request request1;
    private Request request2;
    private Request request3;
    private Request request4;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private RequestRepo repo;
    private ArrayList<Request> requestArrayList;
    private boolean b,c;


    @BeforeClass
    public static void init(){
        db = new DBHandler();
        db.init();
    }

    @Before
    public void setup(){
        repo = new RequestRepo();
        timeMade1 = new Timestamp(100000000);
        timeCompleted = new Timestamp(System.currentTimeMillis());
        request1 = new Request("Transportation", "shortname", "Additional info about this node",
                32194, timeMade1);
        request2 = new Request("Gift", "shortername", "More additional info",
                23456, timeMade1);
        request3 = new Request("Transportation", "shortestname", "Even more additional info",
                34567, timeMade1);
        request4 = new Request("Gifts", "shortestname", "Even more additional info",
                34569, timeMade1);
        request4.setRequestID(3);


    }


    @Test
    public void testGetAllRequests() throws Exception{
        repo.createRequest(request1);
        repo.createRequest(request2);
        assertEquals(2, repo.getAllRequests().size());
        repo.createRequest(request3);
        assertEquals(3,  repo.getAllRequests().size());
    }

    @Test
    public void testCreateRequest() throws Exception{
        assertTrue(repo.createRequest(request1));
    }

    @Test
    public void testModifyRequest() {
        assertTrue(repo.modifyRequest(request4));
    }


    @Test(expected = RequestNotFoundException.class)
    public void testGetOneRequest() throws Exception{
        Request req = new Request();
        req.setRequestID(123123);
        repo.getOneRequest(req.getRequestID());
    }

    @Test
    public void testCompleteRequest(){
        request1.setRequestID(1);
        assertTrue(repo.completeRequest(request1));
    }









    @AfterClass
    public static void shutoff(){
        db.shutdown();
    }
}

*/