package edu.wpi.cs3733d18.teamp.Database;

import java.io.FileNotFoundException;
import java.sql.*;

public class DBHandler {

    // Database URL
    private static final String DB_URL = "jdbc:derby:KioskDB;create=true";

    // Database connection
    private static Connection conn;
    private static DatabaseMetaData dbmd;

    // Initializes the database connection, adds tables and populates them
    static void init() {
        try {
            // Register JDBC driver
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Java DB Driver not found. Add the classpath to your module.");
            e.printStackTrace();
            return;
        }

        try {
            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            dbmd = conn.getMetaData();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }

        // Create Tables
        System.out.println("Creating tables...");
        try {
            Statement stmt = conn.createStatement();
            String sql;

            // Node table
            ResultSet rs = dbmd.getTables(null, "APP", "NODE_INFO", null);
            if (!rs.next()) {
                sql = "CREATE TABLE NODE_INFO " +
                        "(nodeID CHAR(10) not NULL, " +
                        " xcoord INT, " +
                        " ycoord INT, " +
                        " floor VARCHAR(3), " +
                        " building VARCHAR(50), " +
                        " nodeType CHAR(4), " +
                        " longName VARCHAR(255), " +
                        " shortName VARCHAR(255), " +
                        " teamAssigned VARCHAR(10), " +
                        " xcoord3d INT DEFAULT 0 , " +
                        " ycoord3d INT DEFAULT 0, " +
                        " isActive INT, " +
                        " PRIMARY KEY (nodeID))";
                stmt.executeUpdate(sql);
                CSVConverter nodeCSV = new CSVConverter("data/node_info.csv");
                nodeCSV.loadNodeData(conn);
            }

            // Edge table
            rs = dbmd.getTables(null, "APP", "EDGE_INFO", null);
            if (!rs.next()) {
                sql = "CREATE TABLE EDGE_INFO " +
                        "(edgeID CHAR(21) not NULL, " +
                        " startNode CHAR(10), " +
                        " endNode CHAR(10), " +
                        " active INT," +
                        " PRIMARY KEY (edgeID))";
                stmt.executeUpdate(sql);
                CSVConverter edgeCSV = new CSVConverter("data/edge_info.csv");
                edgeCSV.loadEdgeData(conn);
            }

            // Employee table
            rs = dbmd.getTables(null, "APP", "EMPLOYEE_INFO", null);
            if (!rs.next()) {
                sql = "CREATE TABLE EMPLOYEE_INFO " +
                        " (employeeID INT," +
                        "username VARCHAR(25) not NULL, " +
                        " firstName VARCHAR(25), " +
                        " lastName VARCHAR(25), " +
                        " password VARCHAR(25), " +
                        " isAdmin INT, " +
                        " employeeType VARCHAR(25), " +
                        " subType VARCHAR(25), " +
                        " PRIMARY KEY (employeeID))";
                stmt.executeUpdate(sql);
                CSVConverter employeeCSV = new CSVConverter("data/employee_info.csv");
                employeeCSV.loadEmployeeData(conn);
            }

            // Request table
            rs = dbmd.getTables(null, "APP", "REQUEST_INFO", null);
            if (!rs.next()) {
                sql = "CREATE TABLE REQUEST_INFO " +
                        "(requestID INT not NULL, " +
                        " requestType VARCHAR(25), " +
                        " subType VARCHAR(25), " +
                        " location VARCHAR(255), " +
                        " additionalInfo VARCHAR(255), " +
                        " madeBy VARCHAR(50)," +
                        " completedBy VARCHAR(50), " +
                        " timeMade TIMESTAMP, " +
                        " timeCompleted TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        " completed INT, " +
                        " PRIMARY KEY (requestID))";
                stmt.executeUpdate(sql);
                CSVConverter requestCSV = new CSVConverter("data/request_info.csv");
                requestCSV.loadRequestData(conn);
            }

            // Record table
            rs = dbmd.getTables(null, "APP", "RECORD_INFO", null);
            if (!rs.next()) {
                sql = "CREATE TABLE RECORD_INFO  " +
                        "(recordID INT not NULL, " +
                        "requestType VARCHAR(25), " +
                        "subType VARCHAR(50), " +
                        "totalOfType INT DEFAULT 0, " +
                        "totalTime INT DEFAULT 0, " + // In Minutes
                        "avgTime INT DEFAULT 0, " + // In Minutes
                        "PRIMARY KEY (recordID))";
                stmt.executeUpdate(sql);
                CSVConverter recordCSV = new CSVConverter("data/record_info.csv");
                recordCSV.loadRecordData(conn);
            }

            System.out.println("Tables created successfully...");
            stmt.close();

        }catch(SQLException se){
            // Handle errors for JDBC
            se.printStackTrace();
        }
    }// end init

    // Closes database connection, backs up table data and removes tables
    static void shutdown() {
        try {
            Statement stmt = conn.createStatement();
            dbmd = conn.getMetaData(); // Fetch metadata
            String sql;

            // Delete tables
            // Delete NODE_INFO table
            System.out.println("Deleting tables...");
            ResultSet rs = dbmd.getTables(null, "APP", "NODE_INFO", null);
            if (rs.next()) {
                CSVConverter nodeCSV = new CSVConverter("data/node_info.csv");
                nodeCSV.storeNodeData(conn);
                sql = "DROP TABLE NODE_INFO";
                stmt.executeUpdate(sql);
                System.out.println("NODE_INFO dropped");
            }

            // Delete EDGE_INFO
            rs = dbmd.getTables(null, "APP", "EDGE_INFO", null);
            if (rs.next()) {
                CSVConverter edgeCSV = new CSVConverter("data/edge_info.csv");
                edgeCSV.storeEdgeData(conn);
                sql = "DROP TABLE EDGE_INFO";
                stmt.executeUpdate(sql);
                System.out.println("EDGE_INFO dropped");
            }

            // Delete REQUEST_INFO
            rs = dbmd.getTables(null, "APP", "REQUEST_INFO", null);
            if (rs.next()) {
                CSVConverter requestCSV = new CSVConverter("data/request_info.csv");
                requestCSV.storeRequestData(conn);
                sql = "DROP TABLE REQUEST_INFO";
                stmt.executeUpdate(sql);
                System.out.println("REQUEST_INFO dropped");
            }

            // Delete EMPLOYEE_INFO
            rs = dbmd.getTables(null, "APP", "EMPLOYEE_INFO", null);
            if (rs.next()) {
                CSVConverter employeeCSV = new CSVConverter("data/employee_info.csv");
                employeeCSV.storeEmployeeData(conn);
                sql = "DROP TABLE EMPLOYEE_INFO";
                stmt.executeUpdate(sql);
                System.out.println("EMPLOYEE_INFO dropped");
            }

            // Delete RECORD_INFO
            rs = dbmd.getTables(null, "APP", "RECORD_INFO", null);
            if (rs.next()) {
                CSVConverter recordCSV = new CSVConverter("data/record_info.csv");
                recordCSV.storeRecordData(conn);
                sql = "DROP TABLE RECORD_INFO";
                stmt.executeUpdate(sql);
                System.out.println("RECORD_INFO dropped");
            }
            System.out.println("All tables deleted.");

            // Close connection
            conn.close();

        }catch(SQLException se){
            // Handle errors for JDBC
            se.printStackTrace();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }// end shutdown

    // All functions from Repositories



    // Getters
    Connection getConn() {
        try {
            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
        return conn;
    }
}
