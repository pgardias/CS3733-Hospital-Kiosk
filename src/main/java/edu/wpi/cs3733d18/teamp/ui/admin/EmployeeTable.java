package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeTable extends RecursiveTreeObject<EmployeeTable>{

    private SimpleIntegerProperty employeeID;
    private SimpleStringProperty username;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty adminPermission;
    private SimpleStringProperty employeeType;
    private SimpleStringProperty employeeSubType;

    public EmployeeTable(int employeeID, String username, String firstName, String lastName, String adminPermission, String employeeType,
                            String employeeSubType) {
        this.employeeID = new SimpleIntegerProperty(employeeID);
        this.username = new SimpleStringProperty(username);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.adminPermission = new SimpleStringProperty(adminPermission);
        this.employeeType = new SimpleStringProperty(employeeType);
        this.employeeSubType = new SimpleStringProperty(employeeSubType);
    }

    public int getEmployeeID() {
        return employeeID.get();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getAdminPermission() {
        return adminPermission.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getEmployeeSubType() {
        return employeeSubType.get();
    }

    public String getEmployeeType() {
        return employeeType.get();
    }


    public void setEmployeeID(int employeeID) {
        this.employeeID.set(employeeID);
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setAdminPermission(String adminPermission) {
        this.adminPermission.set(adminPermission);
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType.set(employeeType);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setEmployeeSubType(String employeeSubType) {
        this.employeeSubType.set(employeeSubType);
    }

    public SimpleStringProperty employeeSubTypeProperty() {
        return employeeSubType;
    }

    public SimpleStringProperty employeeTypeProperty() {
        return employeeType;
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }
}