package edu.wpi.cs3733d18.teamp.ui;

import com.jfoenix.controls.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Employee;
import edu.wpi.cs3733d18.teamp.Exceptions.EmployeeNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Observable;


public class EmployeePopUpController {

    ManageEmployeeScreenController manageEmployeeScreenController;
    DBSystem db = DBSystem.getInstance();
    String operationOnEmployee;
    int userID;

    @FXML
    Label employeeFormLabel;

    @FXML
    Label firstNameLabel;

    @FXML
    Label lastNameLabel;

    @FXML
    Label passwordLabel;

    @FXML
    Label reEnterPasswordLabel;

    @FXML
    Label jobLabel;

    @FXML
    Label specialtyLabel;

    @FXML
    Label passwordErrorLabel;

    @FXML
    JFXTextField firstNameTextField;

    @FXML
    JFXTextField lastNameTextField;

    @FXML
    JFXPasswordField passwordPasswordField;

    @FXML
    JFXPasswordField reEnterPasswordPasswordField;

    @FXML
    JFXComboBox employeeTypeComboBox;

    @FXML
    JFXComboBox employeeSubTypeComboBox;

    @FXML
    JFXCheckBox adminCheckBox;

    @FXML
    JFXButton cancelButton;

    @FXML
    JFXButton submitButton;

    ObservableList<String> employeeTypeList = FXCollections.observableArrayList("DEFAULT",
            db.EmployeeTypeToString(Employee.employeeType.LANGUAGEINTERP),
            db.EmployeeTypeToString(Employee.employeeType.HOLYPERSON));

    ObservableList<String> languages = FXCollections.observableArrayList(
            "Spanish",
            "French",
            "Portuguese",
            "Polish",
            "German",
            "Mandarin",
            "Turkish",
            "Japanese"
    );

    ObservableList<String> religions = FXCollections.observableArrayList(
            "Christianity",
            "Catholicism",
            "Jewish",
            "Islam"
            );


    @FXML
    public void submitButtonOp(ActionEvent event){
        Employee employee;
        String employeeSubType = "DEFAULT";

        passwordErrorLabel.setText("");
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String userName = generateUsername();
        String password;
        boolean isAdmin;

        if(passwordPasswordField.getText().equals(reEnterPasswordPasswordField.getText())){
            password = passwordPasswordField.getText();
            isAdmin = adminCheckBox.isSelected();

            Employee.employeeType employeeType = db.StringToEmployeeType(employeeTypeComboBox.getValue().toString());
            if(employeeSubTypeComboBox.getValue() != null)
                 employeeSubType = employeeSubTypeComboBox.getValue().toString();


            switch(operationOnEmployee){
                case "create":

                    employee = new Employee(userName, firstName, lastName, isAdmin, employeeType, employeeSubType);
                    employee.setPassword(password);
                    db.createEmployee(employee);

                    break;
                case "modify":
                    try {
                        employee = db.getOneEmployee(userID);

                        employee.setFirstName(firstName);
                        employee.setLastName(lastName);
                        employee.setUserName(userName);
                        employee.setPassword(password);
                        employee.setEmployeeType(employeeType);
                        employee.setSubType(employeeSubType);
                        employee.setIsAdmin(isAdmin);

                        db.modifyEmployee(employee);
                    } catch (EmployeeNotFoundException e){
                        e.printStackTrace();
                    }
                    break;

            }

            manageEmployeeScreenController.refresh();

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        } else {
            passwordErrorLabel.setText("Passwords do not match. Please re-enter passwords.");
        }
    }

    public String generateUsername(){
        String username;
        String firstInitial = firstNameTextField.getText().substring(0,1);
        String lastName = lastNameTextField.getText();

        firstInitial = firstInitial.toLowerCase();
        lastName = lastName.toLowerCase();
        username = firstInitial + lastName;

        return username;
    }


    public void startUp(ManageEmployeeScreenController manageEmployeeScreenController) {
        this.manageEmployeeScreenController = manageEmployeeScreenController;
        employeeSubTypeComboBox.setVisible(false);
        employeeFormLabel.setText("New Employee Form");
        employeeTypeComboBox.setValue("Please choose an employee job");
        employeeTypeComboBox.setItems(employeeTypeList);
        operationOnEmployee = "create";
    }

    public void startUpModify(ManageEmployeeScreenController manageEmployeeScreenController){
        this.manageEmployeeScreenController = manageEmployeeScreenController;
         userID = (manageEmployeeScreenController.employeeListTableView.getSelectionModel().getSelectedItem().getEmployeeID());
        operationOnEmployee = "modify";
        employeeFormLabel.setText("Modify Employee Information");

        try{
            Employee employee = db.getOneEmployee(userID);

            firstNameTextField.setText(employee.getFirstName());
            lastNameTextField.setText(employee.getLastName());

            passwordPasswordField.setText(employee.getPassword());


            employeeTypeComboBox.setItems(employeeTypeList);
            employeeTypeComboBox.setValue(db.EmployeeTypeToString(employee.getEmployeeType()));
            switch(employee.getEmployeeType()){
                case LANGUAGEINTERP:
                    employeeSubTypeComboBox.setItems(languages);
                    specialtyLabel.setText("Languages");
                    break;
                case HOLYPERSON:
                    employeeSubTypeComboBox.setItems(religions);
                    specialtyLabel.setText("Religions");
                    break;
                default:
                    employeeSubTypeComboBox.setVisible(false);

            }
            employeeSubTypeComboBox.setValue(employee.getSubType());

            adminCheckBox.setSelected(employee.getIsAdmin());
        } catch (EmployeeNotFoundException e){
            e.printStackTrace();
        }
    }

    public void setSubTypeComboBox(){
        employeeSubTypeComboBox.setVisible(true);
        specialtyLabel.setVisible(true);
        switch(employeeTypeComboBox.getValue().toString()){
            case "language interpreter":
                employeeSubTypeComboBox.setValue("Please choose a language");
                employeeSubTypeComboBox.setItems(languages);
                specialtyLabel.setText("Language");
                break;
            case "religious figure":
                employeeSubTypeComboBox.setValue("Please choose a religious leader");
                employeeSubTypeComboBox.setItems(religions);
                specialtyLabel.setText("Religion");
                break;
            default:
                employeeSubTypeComboBox.setVisible(false);
                employeeSubTypeComboBox.setValue(null);
                specialtyLabel.setVisible(false);


        }
    }

    @FXML
    public void cancelButtonOp(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }




}
