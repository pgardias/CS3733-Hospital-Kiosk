package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ManageEmployeeScreenController implements Initializable {
    DeletePopUpController deletePopUpController;
    EmployeePopUpController employeePopUpController;
    DBSystem db = DBSystem.getInstance();
    HashMap<String, Employee> employees;

    Thread thread;

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

    @FXML
    StackPane manageEmployeeScreenStackPane;

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

        manageEmployeeScreenStackPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
        thread = new Thread(task);
        thread.start();
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

    /**
     * Creates new thread that increments a counter while mouse is inactive, revert to homescreen if
     * timer reaches past a set value by administrator
     */
    Task task = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                int timeout = Settings.getTimeDelay();
                int counter = 0;

                while(counter <= timeout) {
                    Thread.sleep(5);
                    counter += 5;
                }
                Scene scene;
                Parent root;
                FXMLLoader loader;
                scene = backButton.getScene();

                loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
                try {
                    root = loader.load();
                    scene.setRoot(root);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            } catch (InterruptedException v) {
                System.out.println(v);
                thread = new Thread(task);
                thread.start();
                return null;
            }
            return null;
        }
    };

    /**
     * Handles active mouse events by interrupting the current thread and setting a new thread and timer
     * when the mouse moves. This makes sure that while the user is active, the screen will not time out.
     */
    EventHandler<MouseEvent> testMouseEvent = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            Originator localOriginator = new Originator();
            long start, now;
            localOriginator.setState("Active");
            localOriginator.saveStateToMemento();
            thread.interrupt();

            try{
                thread.join();
            } catch (InterruptedException ie){
                System.out.println(ie);
            }

            Task task2 = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        int timeout = Settings.getTimeDelay();
                        int counter = 0;

                        while(counter <= timeout) {
                            Thread.sleep(5);
                            counter += 5;
                        }
                        Scene scene;
                        Parent root;
                        FXMLLoader loader;
                        scene = backButton.getScene();

                        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
                        try {
                            root = loader.load();
                            scene.setRoot(root);
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    } catch (InterruptedException v) {
                        System.out.println(v);
                        thread = new Thread(task);
                        thread.start();
                        return null;
                    }
                    return null;
                }
            };

            thread = new Thread(task2);
            thread.start();
        }
    };


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

        deletePopUpController = loader.getController();
        deletePopUpController.StartUp(this);
        stage.setScene(new Scene(root, 600, 150));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(removeEmployeeButton.getScene().getWindow());
        stage.showAndWait();

        if (deletePopUpController.getChoice()) {
            int employeeID = employeeListTableView.getSelectionModel().getSelectedItem().getEmployeeID();

            db.deleteEmployee(employeeID);

            refresh();
        }
    }

    public void refresh() {
        for (int i = 0; i < employeeListTableView.getItems().size(); i++) {
            employeeListTableView.getItems().clear();
        }
        onStartUp();
    }
}

