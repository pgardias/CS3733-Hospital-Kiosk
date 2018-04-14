package edu.wpi.cs3733d18.teamp.Database;

import edu.wpi.cs3733d18.teamp.Employee;
import edu.wpi.cs3733d18.teamp.Exceptions.RequestNotFoundException;
import edu.wpi.cs3733d18.teamp.Request;

import java.sql.*;
import java.util.ArrayList;

public class RequestRepo {

    // Database URL
    private static final String DB_URL = "jdbc:derby:KioskDB;create=true";

    // Database connection
    private static Connection conn;

    // Constructor
    RequestRepo(){}

    /**
     * getAllRequests creates Request objects for every row in the REQUEST_INFO
     * table, and adds them to a Hashmap
     * @return Hashmap<String , Request> of all requests in REQUEST_INFO table
     */
    ArrayList<Request> getAllRequests() throws RequestNotFoundException {
        ArrayList<Request> allRequests = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM REQUEST_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);
            if(!results.next()) {
                throw new RequestNotFoundException();
            }else{
                do {
                    Request request = new Request();

                    request.setLocation(results.getString("location"));
                    request.setRequestID(results.getInt("requestID"));
                    request.setRequestType(StringToRequestType(results.getString("requestType")));
                    request.setSubType(results.getString("subType"));
                    request.setAdditionalInfo(results.getString("additionalInfo"));
                    request.setMadeBy(results.getString("madeBy"));
                    request.setCompletedBy(results.getString("completedBy"));
                    request.setTimeMade(results.getTimestamp("timeMade"));
                    request.setTimeCompleted(results.getTimestamp("timeCompleted"));
                    request.setCompleted(results.getInt("completed"));
                    request.setPriority(results.getInt("priority"));

                    allRequests.add(request);
                }while(results.next());
            }

            results.close();
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return allRequests;
    }

    /**
     * getOneRequest creates one Request objects for the row
     * of the database it selects with the given ID
     *
     * @param requestID ID of the node to be searched for
     * @return Request object filled with its attributes
     */
    Request getOneRequest(int requestID) throws RequestNotFoundException{
        Request request = new Request();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM REQUEST_INFO WHERE requestID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, requestID);
            ResultSet results = pstmt.executeQuery();
            if(!results.next()){
                throw new RequestNotFoundException();
            }

            request.setLocation(results.getString("location"));
            request.setRequestID(results.getInt("requestID"));
            request.setRequestType(StringToRequestType(results.getString("requestType")));
            request.setSubType(results.getString("subType"));
            request.setAdditionalInfo(results.getString("additionalInfo"));
            request.setMadeBy(results.getString("madeBy"));
            request.setCompletedBy(results.getString("completedBy"));
            request.setTimeMade(results.getTimestamp("timeMade"));
            request.setTimeCompleted(results.getTimestamp("timeCompleted"));
            request.setCompleted(results.getInt("completed"));
            request.setPriority(results.getInt("priority"));

            results.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
            //throw new RequestNotFoundException();
        }

        return request;
    }

    /**
     * createRequest adds the information from a request object into REQUEST_INFO,
     * generating necessary fields from that information
     * @param request Request to add
     * @return true if request was created, false if something went wrong
     */
    Boolean createRequest(Request request) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "INSERT INTO REQUEST_INFO (requestID, requestType, subType, location, additionalInfo, madeBy, completedBy, timeMade, timeCompleted, completed, priority)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Convert timestamps
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            String timeMade = dateFormat.format(request.getTimeMade());

            // Fill statement and execute
            pstmt.setInt(1, generateRequestID());
            pstmt.setString(2, RequestTypeToString(request.getRequestType()));
            pstmt.setString(3, request.getSubType());
            pstmt.setString(4, request.getLocation());
            pstmt.setString(5, request.getAdditionalInfo());
            pstmt.setString(6, request.getMadeBy());
            pstmt.setString(7, request.getCompletedBy());
            pstmt.setTimestamp(8, request.getTimeMade());
            pstmt.setTimestamp(9, request.getTimeCompleted()); // Not completed by default
            pstmt.setInt(10, request.isCompleted());
            pstmt.setInt(11, request.getPriority());
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
     * modifyRequest modifies specific values in the REQUEST_INFO table for a given request
     * @param request request holding the information to be modifed
     * @return true if request was modified, false if not
     */
    Boolean modifyRequest(Request request){
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("MODIFY REQUEST: "+request.getTimeCompleted());

            // Prepare statement
            String sql = "UPDATE REQUEST_INFO " +
                         "SET completed = ?, requestType = ?, subType = ?, location = ?, additionalInfo = ?, completedBy = ?, priority = ?" +
                         "WHERE requestID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, request.isCompleted());
            pstmt.setString(2, RequestTypeToString(request.getRequestType()));
            pstmt.setString(3, request.getSubType());
            pstmt.setString(4, request.getLocation());
            pstmt.setString(5, request.getAdditionalInfo());
            pstmt.setString(6, request.getCompletedBy());
            pstmt.setInt(7, request.getPriority());
            pstmt.setInt(8, request.getRequestID());

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
     * completeRequest marks a request as completed in the REQUEST_INFO table
     * Completed requests can be viewed separately in the same table
     * @param request request to be set as complete
     * @return true if the request is deleted, false if something went wrong
     */
    Boolean completeRequest(Request request) {
        boolean success = false;
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "UPDATE REQUEST_INFO " +
                         "SET completed = ?, completedBy = ?, timeCompleted = ? " +
                         "WHERE requestID = ?";

            Timestamp now = new Timestamp(System.currentTimeMillis());

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, request.isCompleted());
            pstmt.setString(2, request.getCompletedBy());
            pstmt.setTimestamp(3, now);
            pstmt.setInt(4, request.getRequestID());

            // Make sure statement executes correctly
            System.out.println("Hello: " + pstmt.executeUpdate());
            if(pstmt.executeUpdate() >= 0){
                success = true;
            }

            pstmt.close();
            conn.close();

            // Update records
            request.setTimeCompleted(now);
            request.setCompleted(1);
            RecordRepo rr = new RecordRepo();
            rr.handleRequest(request);

            return success;
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return success;
    }

    /**
     * generateRequestID creates a request ID
     * by incrementing the amount of requests in REQUEST_INFO
     * @return new ID
     */
    private int generateRequestID() {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Get count of requests
            String sql = "SELECT COUNT(*) FROM REQUEST_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);
            results.next();
            int count = results.getInt(1);

            stmt.close();
            conn.close();

            return count + 1;
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return 0;
    }
    //Helper function to convert request Strings to enumerated types
    static Request.requesttype StringToRequestType(String type){
        Request.requesttype reqtype;

        switch(type){
            case "emergency":
                reqtype = Request.requesttype.EMERGENCY;
                break;

            case "security":
                reqtype = Request.requesttype.SECURITY;
                break;

            case "computer service":
                reqtype = Request.requesttype.COMPUTER;
                break;

            case "language interpreter":
                reqtype = Request.requesttype.LANGUAGEINTERP;
                break;

            case "religion handler":
                reqtype = Request.requesttype.HOLYPERSON;
                break;

            default:
                reqtype = Request.requesttype.DEFAULT;
        }

        return reqtype;
    }
    //Helper function to convert enumerated request types to Strings
    static String RequestTypeToString(Request.requesttype type) {
        String reqtype;

        switch (type) {
            case EMERGENCY:
                reqtype = "emergency";
                break;

            case SECURITY:
                reqtype = "security";
                break;

            case COMPUTER:
                reqtype = "computer service";
                break;

            case LANGUAGEINTERP:
                reqtype = "language interpreter";
                break;

            case HOLYPERSON:
                reqtype = "religion handler";
                break;

            default:
                reqtype = "default";
        }
        return reqtype;
    }
}
