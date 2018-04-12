package edu.wpi.cs3733d18.teamp.ui;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.EmployeeNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ManageEmployeeScreenController implements Initializable {
    EmployeePopUpController employeePopUpController;
    DBSystem db = DBSystem.getInstance();
    HashMap<String, Employee> employees;

    @FXML
    JFXButton backButton;

    @FXML
    JFXButton addNewEmployeeButton;

    @FXML
    JFXButton modifyEmployeeInfoButton;

    @FXML
    JFXButton removeEmployeeButton;

    @FXML
    TableView<EmployeeTable> employeeListTableView;

    @FXML
    TableColumn<EmployeeTable, Integer> employeeIDTableViewColumn;

    @FXML
    TableColumn<EmployeeTable, String> userNameColumn;

    @FXML
    TableColumn<EmployeeTable, String> firstNameColumn;

    @FXML
    TableColumn<EmployeeTable, String> lastNameColumn;

    @FXML
    TableColumn<EmployeeTable, String> permissionsColumn;

    @FXML
    TableColumn<EmployeeTable, String> employeeTypeColumn;

    @FXML
    TableColumn<EmployeeTable, String> employeeSubTypeColumn;

    final ObservableList<EmployeeTable> employed = FXCollections.observableArrayList();


//    public void populateEmployeeTableView(){
//
//        employees = db.getAllEmployees();
//
//
//
//        for(HashMap.Entry<String, Employee> employee: employees.entrySet()){
//            System.out.println(employee.getValue().isAdminToString());
//            employed.add(new EmployeeTable(employee.getValue().getUserName(), employee.getValue().getFirstName(), employee.getValue().getLastName(),
//                    employee.getValue().isAdminToString(), employee.getValue().getEmployeeType().toString(), employee.getValue().getSubType()));
//        }
//
//        //employeeListTableView.setItems(employed);
//    }

    @FXML
    public boolean onStartUp(){
        employees = db.getAllEmployees();

        for(HashMap.Entry<String, Employee> employee: employees.entrySet()){
            System.out.println(employee.getValue().isAdminToString());
            employed.add(new EmployeeTable(employee.getValue().getEmployeeID(), employee.getValue().getUserName(), employee.getValue().getFirstName(), employee.getValue().getLastName(),
                    employee.getValue().isAdminToString(), employee.getValue().getEmployeeType().toString(), employee.getValue().getSubType()));
        }

        employeeListTableView.setItems(employed);
        return true;
    }

    @FXML
    public boolean backButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/AdminMenuScreen.fxml"));
        stage = (Stage) backButton.getScene().getWindow();
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        stage.setScene(new Scene(root, 1920, 1080));
        stage.setTitle("");
        stage.setFullScreen(true);
        stage.show();

        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        employeeIDTableViewColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTable, String>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTable, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTable, String>("lastName"));
        permissionsColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTable, String>("adminPermission"));
        employeeSubTypeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTable, String>("employeeSubType"));
        employeeTypeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeTable, String>("employeeType"));

        onStartUp();
    }

    @FXML
    public Boolean addNewEmployeeButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/ManageEmployee-EmployeeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }

        employeePopUpController = loader.getController();
        employeePopUpController.startUp(this);
        stage.setScene(new Scene(root, 1000, 950));
        stage.setTitle("New Employee");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(addNewEmployeeButton.getScene().getWindow());
        stage.show();
        return true;
    }

    @FXML
    public boolean modifyEmployeeInfoButtonOp(ActionEvent e){
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/ManageEmployee-EmployeeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }

        employeePopUpController = loader.getController();
        employeePopUpController.startUpModify(this);
        stage.setScene(new Scene(root, 1000, 950));
        stage.setTitle("New Employee");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(addNewEmployeeButton.getScene().getWindow());
        stage.show();

        return true;
    }

    @FXML
    public void removeEmployeeButtonOp(ActionEvent e){
        int employeeID = employeeListTableView.getSelectionModel().getSelectedItem().getEmployeeID();

        db.deleteEmployee(employeeID);

        refresh();
    }

    public void refresh() {
        for (int i = 0; i < employeeListTableView.getItems().size(); i++) {
            employeeListTableView.getItems().clear();
        }
        onStartUp();
    }
}

