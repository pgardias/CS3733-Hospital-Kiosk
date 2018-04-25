package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.ui.home.ShakeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ManageEmployeeScreenController implements Initializable {
    ConfirmationPopUpController confirmationPopUpController;
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
    JFXTreeTableView<EmployeeTable> employeeTreeTableView;

    @FXML
    JFXTreeTableColumn<EmployeeTable, Integer> employeeIDTableViewColumn;

    @FXML
    JFXTreeTableColumn<EmployeeTable, String> userNameColumn;

    @FXML
    JFXTreeTableColumn<EmployeeTable, String> firstNameColumn;

    @FXML
    JFXTreeTableColumn<EmployeeTable, String> lastNameColumn;

    @FXML
    JFXTreeTableColumn<EmployeeTable, String> permissionsColumn;

    @FXML
    JFXTreeTableColumn<EmployeeTable, String> employeeTypeColumn;

    @FXML
    JFXTreeTableColumn<EmployeeTable, String> employeeSubTypeColumn;


    @FXML
    TreeItem<EmployeeTable> root;

    ArrayList<TreeItem<EmployeeTable>> employeeTableChildren;

    MenuItem mi7 = new MenuItem("Modify Employee");
    MenuItem mi8 = new MenuItem("Delete Employee");
    ContextMenu menu3 = new ContextMenu();

    @FXML
    public boolean onStartUp(){
        employees = db.getAllEmployees();

        for(HashMap.Entry<String, Employee> employee: employees.entrySet()){

            employeeTableChildren.add(new TreeItem<>(new EmployeeTable(employee.getValue().getEmployeeID(), employee.getValue().getUserName(),
                    employee.getValue().getFirstName(), employee.getValue().getLastName(), employee.getValue().isAdminToString(),
                    employee.getValue().getEmployeeType().toString(), employee.getValue().getSubType())));
        }

        root.getChildren().setAll(employeeTableChildren);

        employeeTreeTableView.setRoot(root);

        employeeTreeTableView.setShowRoot(false);
        return true;
    }

    @FXML
    public boolean backButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
        stage = (Stage) backButton.getScene().getWindow();
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        backButton.getScene().setRoot(root);


        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        employeeTableChildren = new ArrayList<>();
        root = new TreeItem<>();

        employeeIDTableViewColumn = new JFXTreeTableColumn<>("Employee ID");
        userNameColumn = new JFXTreeTableColumn<>("UserName");
        firstNameColumn = new JFXTreeTableColumn<>("First Name");
        lastNameColumn = new JFXTreeTableColumn<>("Last Name");
        permissionsColumn = new JFXTreeTableColumn<>("Admin Permission");
        employeeTypeColumn = new JFXTreeTableColumn<>("Employee Type");
        employeeSubTypeColumn = new JFXTreeTableColumn<>("Employee Subtype");

        employeeIDTableViewColumn.setPrefWidth(267);
        employeeIDTableViewColumn.setResizable(false);
        employeeIDTableViewColumn.setSortable(true);

        userNameColumn.setPrefWidth(267);
        userNameColumn.setResizable(false);
        userNameColumn.setSortable(true);

        firstNameColumn.setPrefWidth(267);
        firstNameColumn.setResizable(false);
        firstNameColumn.setSortable(true);

        lastNameColumn.setPrefWidth(267);
        lastNameColumn.setResizable(false);
        lastNameColumn.setSortable(true);

        permissionsColumn.setPrefWidth(267);
        permissionsColumn.setResizable(false);
        permissionsColumn.setSortable(true);

        employeeTypeColumn.setPrefWidth(267);
        employeeTypeColumn.setResizable(false);
        employeeTypeColumn.setSortable(true);

        employeeSubTypeColumn.setPrefWidth(267);
        employeeSubTypeColumn.setResizable(false);
        employeeSubTypeColumn.setSortable(true);


        employeeIDTableViewColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("employeeID"));
        userNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));
        permissionsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("adminPermission"));
        employeeTypeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("employeeType"));
        employeeSubTypeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("employeeSubType"));



        employeeTreeTableView.getColumns().addAll(employeeIDTableViewColumn, userNameColumn, firstNameColumn,lastNameColumn,
                permissionsColumn, employeeTypeColumn, employeeSubTypeColumn);

        mi7.setOnAction((ActionEvent event) -> {
            modifyEmployeeInfoButtonOp(event);
        });

        mi8.setOnAction((ActionEvent event) -> {
            removeEmployeeButtonOp(event);
        });

        menu3.getItems().add(mi7);
        menu3.getItems().add(mi8);
        employeeTreeTableView.setContextMenu(menu3);
    }

    @FXML
    public Boolean addNewEmployeeButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/admin/EmployeeForm.fxml"));

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
        loader = new FXMLLoader(getClass().getResource("/FXML/admin/EmployeeForm.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        if(employeeTreeTableView.getSelectionModel().getSelectedItem() != null) {

            employeePopUpController = loader.getController();
            employeePopUpController.startUpModify(this);
            stage.setScene(new Scene(root, 1000, 950));
            stage.setTitle("New Employee");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(addNewEmployeeButton.getScene().getWindow());
            stage.show();
        } else {
            ShakeTransition anim = new ShakeTransition(modifyEmployeeInfoButton);
            anim.playFromStart();
        }

        return true;
    }

    @FXML
    public void removeEmployeeButtonOp(ActionEvent e){
        // Delete employee popup
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/general/ConfirmationPopUp.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }

        confirmationPopUpController = loader.getController();
        confirmationPopUpController.StartUp(this);
        stage.setScene(new Scene(root, 600, 150));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(removeEmployeeButton.getScene().getWindow());
        stage.showAndWait();

        if (confirmationPopUpController.getChoice()) {
            int employeeID = employeeTreeTableView.getSelectionModel().getSelectedItem().getValue().getEmployeeID();

            db.deleteEmployee(employeeID);
            reBuildTable();
        }
    }

    public void reBuildTable() {
        employees = db.getAllEmployees();
        employeeTableChildren.clear();
        root.getChildren().setAll(employeeTableChildren);

        for(HashMap.Entry<String, Employee> employee: employees.entrySet()){
            employeeTableChildren.add(new TreeItem<>(new EmployeeTable(employee.getValue().getEmployeeID(), employee.getValue().getUserName(),
                    employee.getValue().getFirstName(), employee.getValue().getLastName(), employee.getValue().isAdminToString(),
                    employee.getValue().getEmployeeType().toString(), employee.getValue().getSubType())));
        }

        root.getChildren().setAll(employeeTableChildren);

        employeeTreeTableView.setRoot(root);
        employeeTreeTableView.setShowRoot(false);
    }

    public JFXTreeTableView<EmployeeTable> getEmployeeTreeTableView() {
        return employeeTreeTableView;
    }
}

