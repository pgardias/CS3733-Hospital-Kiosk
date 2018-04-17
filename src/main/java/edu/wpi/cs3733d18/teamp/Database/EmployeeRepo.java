package edu.wpi.cs3733d18.teamp.Database;

import edu.wpi.cs3733d18.teamp.Employee;
import edu.wpi.cs3733d18.teamp.Exceptions.AccessNotAllowedException;
import edu.wpi.cs3733d18.teamp.Exceptions.EmployeeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;

import java.sql.*;
import java.util.HashMap;

public class EmployeeRepo {

    // Database URL
    private static final String DB_URL = "jdbc:derby:KioskDB;create=true";

    // Database connection
    private static Connection conn;

    EmployeeRepo() {}
    /**
     * getAllEmployees creates Employee objects for every row in the EMPLOYEE_INFO
     * table, and adds them to a Hashmap
     * @return Hashmap<String , Employee> of all employees in EMPLOYEE_INFO table
     */
    HashMap<String, Employee> getAllEmployees() {
        HashMap<String, Employee> allEmployees = new HashMap<>();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EMPLOYEE_INFO";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                Employee employee = new Employee();

                // Fill out node attributes with row from table
                employee.setEmployeeID(results.getInt("employeeID"));
                employee.setUserName(results.getString("username"));
                employee.setPassword(results.getString("password"));
                employee.setFirstName(results.getString("firstName"));
                employee.setLastName(results.getString("lastName"));

                // Check if admin
                if (results.getInt("isAdmin") == 0) {
                    employee.setIsAdmin(false);
                } else if (results.getInt("isAdmin") == 1) {
                    employee.setIsAdmin(true);
                }
                employee.setEmployeeType(StringToEmployeeType(results.getString("employeeType")));
                employee.setSubType(results.getString("subtype"));

                allEmployees.put(employee.getUserName(), employee);
            }

            results.close();
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }

        return allEmployees;
    }

    /**
     * getOneEmployee creates one Employee object for the row
     * of the database it selects with the given ID
     *
     * @param employeeID Username of the employee to be searched for
     * @return Employee object filled with its attributes
     */
    Employee getOneEmployee(int employeeID) throws EmployeeNotFoundException {
        Employee employee = new Employee();

        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EMPLOYEE_INFO WHERE employeeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeID);
            ResultSet results = pstmt.executeQuery();

            if(!results.next()) {
                throw new EmployeeNotFoundException();
            }

            // Fill out node attributes with row from table
            employee.setEmployeeID(results.getInt("employeeID"));
            employee.setUserName(results.getString("username"));
            employee.setPassword(results.getString("password"));
            employee.setFirstName(results.getString("firstName"));
            employee.setLastName(results.getString("lastName"));
            if (results.getInt("isAdmin") == 0) {
                employee.setIsAdmin(false);
            } else if (results.getInt("isAdmin") == 1) {
                employee.setIsAdmin(true);
            }
            employee.setEmployeeType(StringToEmployeeType(results.getString("employeeType")));
            employee.setSubType(results.getString("subtype"));

            results.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        }

        return employee;
    }

    /**
     * createEmployee adds the information from an Employee object into EMPLOYEE_INFO,
     * generating necessary fields from that information
     *
     * @param employee Employee to add
     * @return true if Employee was created, false if something went wrong
     */
    Boolean createEmployee(Employee employee) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "INSERT INTO EMPLOYEE_INFO " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Fill statement and execute
            pstmt.setInt(1, generateID());
            pstmt.setString(2, employee.getUserName());
            pstmt.setString(3, employee.getFirstName());
            pstmt.setString(4, employee.getLastName());
            pstmt.setString(5, employee.getPassword());
            if (employee.getIsAdmin()){
                pstmt.setInt(6, 1);
            }
            else{
                pstmt.setInt(6, 0);
            }
            pstmt.setString(7, EmployeeTypeToString(employee.getEmployeeType()));
            pstmt.setString(8, employee.getSubType());

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
     * modifyEmployee updates Employee fields to match given modifications with the employeeID being the matching value
     * @param employee
     * @return true if the update was able to resolve, false if there was a problem with updating the employee values
     */
    Boolean modifyEmployee(Employee employee) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "UPDATE EMPLOYEE_INFO " +
                    "SET username = ?, password = ?, firstName = ?, lastName = ?, isAdmin = ?, employeeType = ?, subType = ?" +
                    "WHERE employeeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Fill statement and execute
            pstmt.setString(1, employee.getUserName());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getFirstName());
            pstmt.setString(4, employee.getLastName());
            if (employee.getIsAdmin()){
                pstmt.setInt(5, 1);
            }
            else{
                pstmt.setInt(5, 0);
            }
            pstmt.setString(6, EmployeeTypeToString(employee.getEmployeeType()));
            pstmt.setString(7, employee.getSubType());
            pstmt.setInt(8, employee.getEmployeeID()); // Employee to update

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
     * deleteEmployee removes an employee from the EMPLOYEE_INFO table
     * @param employeeID EmployeeID of Employee to be removed
     * @return true if the Employee is deleted, false if something went wrong
     */
    Boolean deleteEmployee(int employeeID) {
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Prepare statement
            String sql = "DELETE FROM EMPLOYEE_INFO WHERE employeeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeID);

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
     * checkEmployeeLogin checks if the username and password
     * of an Employee are in the current Employee database
     * @param username Employee's username
     * @param password Employee's password
     * @return gives an Employee object if the username and password for the employee match the given values
     * in the database, gives an exception if otherwise
     */
    Employee checkEmployeeLogin(String username, String password) throws LoginInvalidException {
        Employee employee = new Employee();
        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EMPLOYEE_INFO WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet results = pstmt.executeQuery();

            if (!results.next()) {
                throw new LoginInvalidException();
            }

            employee.setUserName(results.getString("username"));
            employee.setPassword(results.getString("password"));
            employee.setFirstName(results.getString("firstName"));
            employee.setLastName(results.getString("lastName"));
            // Check if admin
            if (results.getInt("isAdmin") == 0) {
                employee.setIsAdmin(false);
            } else if (results.getInt("isAdmin") == 1) {
                employee.setIsAdmin(true);
            }
            employee.setEmployeeType(StringToEmployeeType(results.getString("employeeType")));
            employee.setSubType(results.getString("subType"));

            pstmt.close();
            conn.close();
            results.close();
            pstmt.close();
            conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
            throw new LoginInvalidException();
        }
        return employee; // Returns Employee object
    }

    /**
     * checkAdminLogin checks that the person logging into the
     * Admin page is an Admin by verifying their access privileges
     * @param username
     * @param password
     * @return an Employee that is an Admin, and throws an exception if the employee is not an Admin
     */
    Employee checkAdminLogin(String username, String password) throws LoginInvalidException, AccessNotAllowedException {
        Employee employee = new Employee();
        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EMPLOYEE_INFO WHERE username = ? AND password = ? AND isAdmin = 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet results = pstmt.executeQuery();
            if(!results.next()){
                throw new LoginInvalidException();
            }else {
                do {
                    employee.setUserName(results.getString("username"));
                    employee.setPassword(results.getString("password"));
                    employee.setFirstName(results.getString("firstName"));
                    employee.setLastName(results.getString("lastName"));
                    // Check if admin
                    if (results.getInt("isAdmin") == 0) {
                        employee.setIsAdmin(false);
                    } else if (results.getInt("isAdmin") == 1) {
                        employee.setIsAdmin(true);
                    }
                    employee.setEmployeeType(StringToEmployeeType(results.getString("employeeType")));
                    employee.setSubType(results.getString("subType"));

                } while (results.next());
            }
            if(!employee.getIsAdmin()){
                throw new AccessNotAllowedException();
            }

            results.close();
            pstmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new LoginInvalidException();
        }

        return employee;
    }

    /**
     * checkAdminLogin checks that the person logging into the
     * Admin page is an Admin by verifying their access privileges
     * @param id
     * @return an Employee that is an Admin, and throws an exception if the employee is not an Admin
     */
    Employee checkLoginID(String id) throws LoginInvalidException {
        Employee employee = new Employee();
        try {
            conn = DriverManager.getConnection(DB_URL);

            String sql = "SELECT * FROM EMPLOYEE_INFO WHERE employeeID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.valueOf(id));
            ResultSet results = pstmt.executeQuery();
            if(!results.next()){
                throw new LoginInvalidException();
            }else {
                do {
                    employee.setUserName(results.getString("username"));
                    employee.setPassword(results.getString("password"));
                    employee.setFirstName(results.getString("firstName"));
                    employee.setLastName(results.getString("lastName"));
                    // Check if admin
                    if (results.getInt("isAdmin") == 0) {
                        employee.setIsAdmin(false);
                    } else if (results.getInt("isAdmin") == 1) {
                        employee.setIsAdmin(true);
                    }
                    employee.setEmployeeType(StringToEmployeeType(results.getString("employeeType")));
                    employee.setSubType(results.getString("subType"));

                } while (results.next());
            }

            results.close();
            pstmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new LoginInvalidException();
        }

        return employee;
    }

    private int generateID(){
        int max;
        try {
            conn = DriverManager.getConnection(DB_URL);

            // Get count of requests
            String sql = "SELECT MAX (employeeID) FROM EMPLOYEE_INFO";
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(sql);
            results.next();
            max = results.getInt(1);

            stmt.close();
            conn.close();

            return max + 1;
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return 0;
    }

    public Employee.employeeType StringToEmployeeType(String type){

        Employee.employeeType emptype;

        switch(type){
            case "Language Interpreter":
                emptype = Employee.employeeType.LANGUAGEINTERP;
                break;

            case "Religious Figure":
                emptype = Employee.employeeType.HOLYPERSON;
                break;

            case "Electronics Technician":
                emptype = Employee.employeeType.COMPUTER;
                break;

            case "Security Guard":
                emptype = Employee.employeeType.SECURITY;
                break;

            case "Facilities Maintenance":
                emptype = Employee.employeeType.MAINTENANCE;
                break;

            case "Custodian":
                emptype = Employee.employeeType.SANITATION;
                break;

            case "Audio/Visual Worker":
                emptype = Employee.employeeType.AV;
                break;

            case "Delivery Man/Woman":
                emptype = Employee.employeeType.GIFTS;
                break;

            case "emergency":
                emptype = Employee.employeeType.EMERGENCY;
                break;

            case "Transportation Handler":
                emptype = Employee.employeeType.TRANSPORTATION;
                break;

            default:
                emptype = Employee.employeeType.DEFAULT;
        }

        return emptype;
    }
    //Helper method to convert enumerated employee types to strings
    public String EmployeeTypeToString(Employee.employeeType type) {

        String emptype;

        switch (type) {
            case LANGUAGEINTERP:
                emptype = "Language Interpreter";
                break;

            case HOLYPERSON:
                emptype = "Religious Figure";
                break;

            case COMPUTER:
                emptype = "Electronics Technician";
                break;

            case SECURITY:
                emptype = "Security Guard";
                break;

            case MAINTENANCE:
                emptype = "Facilities Maintenance";
                break;

            case SANITATION:
                emptype = "Custodian";
                break;

            case AV:
                emptype = "Audio/Visual Worker";
                break;

            case GIFTS:
                emptype = "Delivery Man/Woman";
                break;

            case EMERGENCY:
                emptype = "emergency";
                break;

            case TRANSPORTATION:
                emptype = "Transportation Handler";
                break;

            default:
                emptype = "default";
        }
        return emptype;
    }
}
