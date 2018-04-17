package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Employee;
import edu.wpi.cs3733d18.teamp.Exceptions.EmployeeNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


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
    Label employeeErrorLabel;

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

    ObservableList<String> employeeTypeList = FXCollections.observableArrayList(
            "DEFAULT",
            db.EmployeeTypeToString(Employee.employeeType.LANGUAGEINTERP),
            db.EmployeeTypeToString(Employee.employeeType.HOLYPERSON),
            db.EmployeeTypeToString(Employee.employeeType.COMPUTER),
            db.EmployeeTypeToString(Employee.employeeType.SECURITY),
            db.EmployeeTypeToString(Employee.employeeType.MAINTENANCE),
            db.EmployeeTypeToString(Employee.employeeType.SANITATION),
            db.EmployeeTypeToString(Employee.employeeType.AV),
            db.EmployeeTypeToString(Employee.employeeType.GIFTS),
            db.EmployeeTypeToString(Employee.employeeType.TRANSPORTATION)
    );

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

    ObservableList<String> devices = FXCollections.observableArrayList(
            "Desktop Computer",
            "Laptop",
            "Projector",
            "Podium",
            "TV",
            "Printer",
            "Fax Machine",
            "Pager"
    );

    ObservableList<String> situations = FXCollections.observableArrayList(
            "Patient Disrespecting Hospital Employees",
            "Patient Injuring Hospital Employees",
            "Patient Injuring Fellow Patients",
            "Visitor Refusing To Be Identified",
            "Visitor Disrespecting Hospital Employees",
            "Visitor Injuring Patients",
            "Visitor Injuring Hospital Employees",
            "Visitor Injuring Other Visitors",
            "Rouge Employee"
    );

    ObservableList<String> machines = FXCollections.observableArrayList(
            "Elevator",
            "Door",
            "Kiosk",
            "AC Unit",
            "Heater",
            "Furnace",
            "Piping",
            "Washing Machine",
            "Clothes Dryer"
    );

    ObservableList<String> messes = FXCollections.observableArrayList(
            "Clean Food Spill",
            "Clean Water Spill",
            "Clean Other Drink Spill",
            "Clean Medical Fluid Spill",
            "Clean Biohazard Spill",
            "Clean Vomit",
            "Disinfect Room/Hallway"
    );

    ObservableList<String> AVDevices = FXCollections.observableArrayList(
            "Hearing Aid",
            "Projector",
            "TV",
            "Radio",
            "IPad",
            "Drawing Pad",
            "White Board and Markers"
    );

    ObservableList<String> gifts = FXCollections.observableArrayList(
            "A Dozen Flowers",
            "Puzzle",
            "Stuffed Animal",
            "Balloons",
            "Chocolate",
            "Candy",
            "Sweatshirt",
            "T-shirt"
    );

    ObservableList<String> modeOfTransportation = FXCollections.observableArrayList(
            "Gurney",
            "Wheelchair",
            "Ambulance",
            "Car",
            "Helicopter"
    );


    @FXML
    public void submitButtonOp(ActionEvent event){
        Employee employee;
        String employeeSubType = "DEFAULT";

        passwordErrorLabel.setText("");
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        if (firstName.equals("") || lastName.equals("")) {
            employeeErrorLabel.setText("Please input a first and last name");
            return;
        }

        String userName = generateUsername();
        String password;
        boolean isAdmin;

        if (passwordPasswordField.getText().equals(reEnterPasswordPasswordField.getText())) {
            password = passwordPasswordField.getText();
            isAdmin = adminCheckBox.isSelected();

            Employee.employeeType employeeType = db.StringToEmployeeType(employeeTypeComboBox.getValue().toString());
            if (employeeSubTypeComboBox.getValue() != null)
                employeeSubType = employeeSubTypeComboBox.getValue().toString();


            switch (operationOnEmployee) {
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
                    } catch (EmployeeNotFoundException e) {
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
                case COMPUTER:
                    employeeSubTypeComboBox.setItems(devices);
                    specialtyLabel.setText("Electronic Devices");
                    break;
                case SECURITY:
                    employeeSubTypeComboBox.setItems(situations);
                    specialtyLabel.setText("Situations");
                    break;
                case MAINTENANCE:
                    employeeSubTypeComboBox.setItems(machines);
                    specialtyLabel.setText("Broken Machine");
                    break;
                case SANITATION:
                    employeeSubTypeComboBox.setItems(messes);
                    specialtyLabel.setText("Mess");
                    break;
                case AV:
                    employeeSubTypeComboBox.setItems(AVDevices);
                    specialtyLabel.setText("Audio/Visual Device");
                    break;
                case GIFTS:
                    employeeSubTypeComboBox.setItems(gifts);
                    specialtyLabel.setText("Gift");
                    break;
                case TRANSPORTATION:
                    employeeSubTypeComboBox.setItems(modeOfTransportation);
                    specialtyLabel.setText("Mode of Transportation");
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
            case "Language Interpreter":
                employeeSubTypeComboBox.setValue("Please choose a language");
                employeeSubTypeComboBox.setItems(languages);
                specialtyLabel.setText("Language");
                break;
            case "Religious Figure":
                employeeSubTypeComboBox.setValue("Please choose a religious leader");
                employeeSubTypeComboBox.setItems(religions);
                specialtyLabel.setText("Religion");
                break;
            case "Electronics Technician":
                employeeSubTypeComboBox.setValue("Please choose an electronic device");
                employeeSubTypeComboBox.setItems(devices);
                specialtyLabel.setText("Electronic Device");
                break;
            case "Security Guard":
                employeeSubTypeComboBox.setValue("Please choose a situation");
                employeeSubTypeComboBox.setItems(situations);
                specialtyLabel.setText("Situations");
                break;
            case "Facilities Maintenance":
                employeeSubTypeComboBox.setValue("Please choose a broken machine");
                employeeSubTypeComboBox.setItems(machines);
                specialtyLabel.setText("Broken Machine");
                break;
            case "Custodian":
                employeeSubTypeComboBox.setValue("Please select a mess");
                employeeSubTypeComboBox.setItems(messes);
                specialtyLabel.setText("Mess");
                break;
            case "Audio/Visual Worker":
                employeeSubTypeComboBox.setValue("Please select an Audio/Visual device");
                employeeSubTypeComboBox.setItems(AVDevices);
                specialtyLabel.setText("Audio/Visual Device");
                break;
            case "Delivery Man/Woman":
                employeeSubTypeComboBox.setValue("Please select a gift");
                employeeSubTypeComboBox.setItems(gifts);
                specialtyLabel.setText("Gift");
                break;
            case "Transportation Handler":
                employeeSubTypeComboBox.setValue("Please select a mode of transportation");
                employeeSubTypeComboBox.setItems(modeOfTransportation);
                specialtyLabel.setText("Mode of Transportation");
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
