package edu.wpi.cs3733d18.teamp.Database;

import java.io.*;
import java.sql.*;

public class CSVConverter {

    // Attributes
    private String fileName;

    // Constructor
    CSVConverter(String file) {
        this.fileName = file;
    }

    // Helper function for loading CSV files into NODE_INFO table
    void loadNodeData(Connection conn) {

        String csvFile = this.fileName;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String sql = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); // Remove column headers

            // Prepare mass insert statement
            sql = "INSERT INTO NODE_INFO (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned, xcoord3d, ycoord3d, isActive) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] table_data = line.split(cvsSplitBy);

                // Enter data into prepared statement
                pstmt.setString(1, table_data[0]); // nodeID
                pstmt.setInt(2, Integer.parseInt(table_data[1])); // xcoord
                pstmt.setInt(3, Integer.parseInt(table_data[2])); // ycoord
                pstmt.setString(4, table_data[3]); // floor
                pstmt.setString(5, table_data[4]); // building
                pstmt.setString(6, table_data[5]); // nodeType
                pstmt.setString(7, table_data[6]); // longName
                pstmt.setString(8, table_data[7]); // shortName
                pstmt.setString(9, table_data[8]); // teamAssignment
                pstmt.setInt(10,  Integer.parseInt(table_data[9])); //xcoord3d
                pstmt.setInt(11,  Integer.parseInt(table_data[10])); //ycoord3d
                pstmt.setInt(12,  Integer.parseInt(table_data[11])); //isActive
                pstmt.executeUpdate();
            }

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function for loading CSV files into EDGE_INFO table
    void loadEdgeData(Connection conn) {

        String csvFile = this.fileName;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String sql = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); // Remove column headers

            //Prepare mass insert statement
            sql = "INSERT INTO EDGE_INFO (edgeID, startNode, endNode, active) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] table_data = line.split(cvsSplitBy);

                // Enter data into prepared statement
                pstmt.setString(1, table_data[0]); // edgeID
                pstmt.setString(2, table_data[1]); // startNode
                pstmt.setString(3, table_data[2]); // endNode
                pstmt.setInt(4, Integer.parseInt(table_data[3]));
                pstmt.executeUpdate();
            }

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function for loading CSV files into REQUEST_INFO table
    void loadRequestData(Connection conn) {

        String csvFile = this.fileName;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String sql = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); // Remove column headers

            //Prepare mass insert statement
            sql = "INSERT INTO REQUEST_INFO (requestID, requestType, subType, location, additionalInfo, madeBy, " +
                    "completedBy, timeMade, timeCompleted, completed) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] table_data = line.split(cvsSplitBy);

                // Enter data into prepared statement
                pstmt.setInt(1, Integer.parseInt(table_data[0])); // requestID
                pstmt.setString(2, table_data[1]); // requestType
                pstmt.setString(3, table_data[2]); // subType
                pstmt.setString(4, table_data[3]); // location
                pstmt.setString(5, table_data[4]); // additionalInfo
                pstmt.setString(6, table_data[5]); // madeBy
                pstmt.setString(7, table_data[6]); // completedBy
                pstmt.setString(8, table_data[7]); // timeMade //TODO convert to timestamps
                if (table_data[8].equals("null")) {
                    pstmt.setString(9, null); // timeCompleted
                } else {
                    pstmt.setString(9, table_data[8]); // timeCompleted
                }
                pstmt.setInt(10, Integer.parseInt(table_data[9])); // completed
                pstmt.executeUpdate();
            }

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function for loading CSV files into EmployeE_INFO table
    void loadEmployeeData(Connection conn) {

        String csvFile = this.fileName;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String sql = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); // Remove column headers

            //Prepare mass insert statement
            sql = "INSERT INTO EMPLOYEE_INFO (employeeID, username, firstName, lastName, password, isAdmin, employeeType, subType) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] table_data = line.split(cvsSplitBy);

                // Enter data into prepared statement
                pstmt.setInt(1, Integer.parseInt(table_data[0]));
                pstmt.setString(2, table_data[1]); // username
                pstmt.setString(3, table_data[2]); // firstName
                pstmt.setString(4, table_data[3]); // lastName
                pstmt.setString(5, table_data[4]); // password
                pstmt.setInt(6, Integer.parseInt(table_data[5])); // isAdmin
                pstmt.setString(7, table_data[6]); // employeeType
                pstmt.setString(8, table_data[7]); // subtype
                pstmt.executeUpdate();
            }

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function for loading CSV files into RECORD_INFO table
    public void loadRecordData(Connection conn) {

        String csvFile = this.fileName;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String sql = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); // Remove column headers

            //Prepare mass insert statement
            sql = "INSERT INTO RECORD_INFO (recordID, requestType, subType, totalOfType, totalTime, avgTime) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] table_data = line.split(cvsSplitBy);

                // Enter data into prepared statement
                pstmt.setInt(1, Integer.parseInt(table_data[0])); // recordID
                pstmt.setString(2, table_data[1]); // requestType
                pstmt.setString(3, table_data[2]); // subType
                pstmt.setInt(4, Integer.parseInt(table_data[3])); // totalOfType
                pstmt.setInt(5, Integer.parseInt(table_data[4])); // totalTime
                pstmt.setInt(6, Integer.parseInt(table_data[5])); // avgTime
                pstmt.executeUpdate();
            }

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function for loading NODE_INFO data into a CSV file
    void storeNodeData(Connection conn) throws FileNotFoundException, SQLException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT * FROM NODE_INFO";
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(sql);

        //Append header row
        sb.append("nodeID");
        sb.append(",");
        sb.append("xcoord");
        sb.append(",");
        sb.append("ycoord");
        sb.append(",");
        sb.append("floor");
        sb.append(",");
        sb.append("building");
        sb.append(",");
        sb.append("nodeType");
        sb.append(",");
        sb.append("longName");
        sb.append(",");
        sb.append("shortName");
        sb.append(",");
        sb.append("teamAssigned");
        sb.append(",");
        sb.append("xcoord3d");
        sb.append(",");
        sb.append("ycoord3d");
        sb.append(",");
        sb.append("isActive");
        sb.append("\n");

        while (results.next()) {
            sb.append(results.getString(1));
            sb.append(",");
            sb.append(results.getString(2));
            sb.append(",");
            sb.append(results.getString(3));
            sb.append(",");
            sb.append(results.getString(4));
            sb.append(",");
            sb.append(results.getString(5));
            sb.append(",");
            sb.append(results.getString(6));
            sb.append(",");
            sb.append(results.getString(7));
            sb.append(",");
            sb.append(results.getString(8));
            sb.append(",");
            sb.append(results.getString(9));
            sb.append(",");
            sb.append(results.getString(10));
            sb.append(",");
            sb.append(results.getString(11));
            sb.append(",");
            sb.append(results.getString(12));
            sb.append("\n");
        }

        results.close();
        pw.write(sb.toString());
        pw.close();
    }

    // Helper function for loading EDGE_INFO data into a CSV file
    void storeEdgeData(Connection conn) throws FileNotFoundException, SQLException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT * FROM EDGE_INFO";
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(sql);

        //Append header row
        sb.append("edgeID");
        sb.append(",");
        sb.append("startNode");
        sb.append(",");
        sb.append("endNode");
        sb.append(",");
        sb.append("active");
        sb.append("\n");

        while (results.next()) {
            sb.append(results.getString(1));
            sb.append(",");
            sb.append(results.getString(2));
            sb.append(",");
            sb.append(results.getString(3));
            sb.append(",");
            sb.append(results.getString(4));
            sb.append("\n");
        }

        results.close();
        pw.write(sb.toString());
        pw.close();
    }

    // Helper function for loading REQUEST_INFO data into a CSV file
    void storeRequestData(Connection conn) throws FileNotFoundException, SQLException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT * FROM REQUEST_INFO";
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(sql);

        //Append header row
        sb.append("requestID");
        sb.append(",");
        sb.append("requestType");
        sb.append(",");
        sb.append("subType");
        sb.append(",");
        sb.append("location");
        sb.append(",");
        sb.append("additionalInfo");
        sb.append(",");
        sb.append("madeBy");
        sb.append(",");
        sb.append("completedBy");
        sb.append(",");
        sb.append("timeMade");
        sb.append(",");
        sb.append("timeCompleted");
        sb.append(",");
        sb.append("completed");
        sb.append("\n");

        while (results.next()) {
            sb.append(results.getString(1));
            sb.append(",");
            sb.append(results.getString(2));
            sb.append(",");
            sb.append(results.getString(3));
            sb.append(",");
            sb.append(results.getString(4));
            sb.append(",");
            sb.append(results.getString(5));
            sb.append(",");
            sb.append(results.getString(6));
            sb.append(",");
            sb.append(results.getString(7));
            sb.append(",");
            sb.append(results.getString(8));
            sb.append(",");
            sb.append(results.getString(9));
            sb.append(",");
            sb.append(results.getString(10));
            sb.append("\n");
        }

        results.close();
        pw.write(sb.toString());
        pw.close();
    }

    // Helper function for loading EMPLOYEE_INFO data into a CSV file
    void storeEmployeeData(Connection conn) throws FileNotFoundException, SQLException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT * FROM EMPLOYEE_INFO";
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(sql);

        //Append header row
        sb.append("employeeID");
        sb.append(",");
        sb.append("username");
        sb.append(",");
        sb.append("firstName");
        sb.append(",");
        sb.append("lastName");
        sb.append(",");
        sb.append("password");
        sb.append(",");
        sb.append("isAdmin");
        sb.append(",");
        sb.append("employeeType");
        sb.append(",");
        sb.append("subtype");
        sb.append("\n");

        while (results.next()) {

            sb.append(results.getInt(1));
            sb.append(",");
            sb.append(results.getString(2));
            sb.append(",");
            sb.append(results.getString(3));
            sb.append(",");
            sb.append(results.getString(4));
            sb.append(",");
            sb.append(results.getString(5));
            sb.append(",");
            sb.append(results.getString(6));
            sb.append(",");
            sb.append(results.getString(7));
            sb.append(",");
            sb.append(results.getString(8));
            sb.append("\n");
        }

        results.close();
        pw.write(sb.toString());
        pw.close();
    }

    // Helper function for loading RECORD_INFO data into a CSV file
    public void storeRecordData(Connection conn) throws FileNotFoundException, SQLException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT * FROM RECORD_INFO";
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(sql);

        //Append header row
        sb.append("recordID");
        sb.append(",");
        sb.append("requestType");
        sb.append(",");
        sb.append("subType");
        sb.append(",");
        sb.append("totalOfType");
        sb.append(",");
        sb.append("totalTime");
        sb.append(",");
        sb.append("avgTime");
        sb.append("\n");

        while (results.next()) {
            sb.append(results.getString(1));
            sb.append(",");
            sb.append(results.getString(2));
            sb.append(",");
            sb.append(results.getString(3));
            sb.append(",");
            sb.append(results.getString(4));
            sb.append(",");
            sb.append(results.getString(5));
            sb.append(",");
            sb.append(results.getString(6));
            sb.append("\n");
        }

        results.close();
        pw.write(sb.toString());
        pw.close();
    }
}
