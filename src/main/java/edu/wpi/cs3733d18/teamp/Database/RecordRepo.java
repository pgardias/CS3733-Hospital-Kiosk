package edu.wpi.cs3733d18.teamp.Database;

import edu.wpi.cs3733d18.teamp.Record;
import edu.wpi.cs3733d18.teamp.Request;

import java.sql.*;
import java.util.ArrayList;

public class RecordRepo {

    // Database URL
    private static final String DB_URL = "jdbc:derby:KioskDB;create=true";

    // Database connection
    private static Connection conn;

    // Constructor
    RecordRepo(){}
    /**
     * getRecordType will return all records of a specific type
     * @return List of all records
     */
    ArrayList<Record> getAllRecords() {
        ArrayList<Record> allRecords = new ArrayList<Record>();
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Execute query to get all records of type
            String sql = "SELECT * FROM RECORD_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);
            if (!results.next()) { // Return null if no records exist
                return null;
            }else{
                do {
                    Record record = new Record();

                    record.setRecordID(results.getInt("recordID"));
                    record.setRequestType(RequestRepo.StringToRequestType(results.getString("requestType")));
                    record.setSubType(results.getString("subType"));
                    record.setTotalOfType(results.getInt("totalOfType"));
                    record.setTotalTime(results.getInt("totalTime"));
                    record.setAvgTime(results.getInt("avgTime"));

                    allRecords.add(record);
                }while(results.next());
            }
            results.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return allRecords;
    }

    /**
     * getRecordType will return all records of a specific type
     * @param requestType The type of request to look for
     * @return List of all records of this broad type
     */
    ArrayList<Record> getRecordType(String requestType) {
        ArrayList<Record> typeRecords = new ArrayList<Record>();
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Execute query to get all records of type
            String sql = "SELECT * FROM RECORD_INFO WHERE requestType = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, requestType);
            ResultSet results = pstmt.executeQuery();
            if (!results.next()) { // Return null if no records exist
                return null;
            }else{
                do {
                    Record record = new Record();

                    record.setRecordID(results.getInt("recordID"));
                    record.setRequestType(RequestRepo.StringToRequestType(results.getString("requestType")));
                    record.setSubType(results.getString("subType"));
                    record.setTotalOfType(results.getInt("totalOfType"));
                    record.setTotalTime(results.getInt("totalTime"));
                    record.setAvgTime(results.getInt("avgTime"));

                    typeRecords.add(record);
                }while(results.next());
            }
            results.close();
            pstmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return typeRecords;
    }

    /**
     * getSubType will return all records of a specific type
     * @param subType The type of sub-request to look for
     * @return List of all records of this narrow type
     */
    Record getSubType(String subType) {
        Record subRecord = new Record();
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Execute query to get all records of type
            String sql = "SELECT * FROM RECORD_INFO WHERE subType = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subType);
            ResultSet results = pstmt.executeQuery();
            if (!results.next()) { // Return null if no records exist
                return null;
            }else{
                subRecord.setRecordID(results.getInt("recordID"));
                subRecord.setRequestType(RequestRepo.StringToRequestType(results.getString("requestType")));
                subRecord.setSubType(results.getString("subType"));
                subRecord.setTotalOfType(results.getInt("totalOfType"));
                subRecord.setTotalTime(results.getInt("totalTime"));
                subRecord.setAvgTime(results.getInt("avgTime"));
            }
            results.close();
            pstmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return subRecord;
    }

    /**
     * createRecord Creates a new record in the database
     * @param record The new record to insert
     * @return True if the insert is completed
     */
    private Boolean createRecord(Record record) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "INSERT INTO RECORD_INFO (recordID, requestType, subType, totalOfType, totalTime, avgTime)" +
                    "VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Convert timestamps
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            String timeMade = dateFormat.format(request.getTimeMade());

            // Fill statement and execute
            pstmt.setInt(1, record.getRecordID());
            pstmt.setString(2, RequestRepo.RequestTypeToString(record.getRequestType()));
            pstmt.setString(3, record.getSubType());
            pstmt.setInt(4, record.getTotalOfType());
            pstmt.setInt(5, record.getTotalTime());
            pstmt.setInt(6, record.getAvgTime());
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
     * updateRecord Finds the current record and updates it with new information from the request
     * @param record
     * @param request
     * @return true if record was able to be updated, false if exception is thrown (something went wrong)
     */
    private Boolean updateRecord(Record record, Request request) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "UPDATE RECORD_INFO SET totalOfType = ?, totalTime = ?, avgTime = ?" +
                    "WHERE subType = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Check time statistics to see if they can be updated
            long addedTime = 0;
            if (request.getTimeCompleted().getTime() != 0) { // Default timestamp is at epoch, 0 ms. Should always pass
                TimeConverter tc = new TimeConverter(request.getTimeMade(), request.getTimeCompleted());
                addedTime = tc.timeDiffMinutes();
            }

            // New sum of this type (add 1)
            int newSumType = record.getTotalOfType()+1;
            // New total time (prev total time + new difference)
            int newTotalMinutes = record.getTotalTime() + (int)addedTime;

            // Fill statement and execute
            pstmt.setInt(1, newSumType);
            pstmt.setInt(2, newTotalMinutes);
            pstmt.setInt(3, newTotalMinutes / newSumType);
            pstmt.setString(4, request.getSubType());
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
     * Counts the number of records in the record table to create new record ID
     * @return The new record ID
     */
    private int newID() {

        int count = 0;
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "SELECT count(*) FROM RECORD_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);

            if(!results.next()) { // If no records, is first one
                count = 1;
            }else { // Otherwise return count + 1
                count =  results.getInt(1) + 1;
            }

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return count;
    }


    /**
     * handleRequest will take in a request object and delegate data to create or update function
     * @param newRequest Request to delegate
     */
    Boolean handleRequest(Request newRequest) {

        // Check if record exists
        Record curRecord = getSubType(newRequest.getSubType());

        // If a record doesn't exist, create a new one
        if (curRecord == null) {
            TimeConverter tc = new TimeConverter(newRequest.getTimeMade(), newRequest.getTimeCompleted());
            int startTotal = (int)tc.timeDiffMinutes();
            // ID = Num Records + 1, TotalOfType = 1 (first), Total/Average time are the same = time difference
            createRecord(new Record(newID(), newRequest.getRequestType(), newRequest.getSubType(), 1, startTotal, startTotal));
        }

        // If record exists, update it
        else {
            System.out.println("UPDATING RECORD");
            updateRecord(curRecord, newRequest);
        }

        System.out.println("TIME AT END OF RECORD HANDLING: "+newRequest.getTimeCompleted());

        return true;
    }
}
