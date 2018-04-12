package edu.wpi.cs3733d18.teamp;

public class Employee {
    //set enum
    public enum employeeType {LANGUAGEINTERP, HOLYPERSON, ADMIN, DEFAULT}

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isAdmin;
    private employeeType type;
    private String subType;
    private int employeeID;

    // Constructors
    public Employee(){} // Empty constructor
    //Create an Employee object with a username, firstName, lastName, adminValue for privilage, employeeType, and subtype for specific requests
    //they can or cannot fulfill
    public Employee(String username, String firstName, String lastName, boolean isAdmin, employeeType type, String subType) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.type = type;
        this.subType = subType;
    }

    // Getters

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public employeeType getEmployeeType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String isAdminToString(){
        if(isAdmin){
            return "Admin";
        }
        return "Employee";
    }

    // Setters

    public void setUserName(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public void setIsAdmin(boolean admin) {
        this.isAdmin = admin;
    }
    
    public void setEmployeeType(employeeType empType){
        this.type = empType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }
}
