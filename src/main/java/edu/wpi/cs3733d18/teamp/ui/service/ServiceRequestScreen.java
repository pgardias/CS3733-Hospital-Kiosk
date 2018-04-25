package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import edu.wpi.cs3733d18.teamR.RaikouAPI;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.RequestNotFoundException;
import edu.wpi.cs3733d18.teamp.api.Exceptions.ServiceException;
import edu.wpi.cs3733d18.teamp.api.TransportationRequest;
import edu.wpi.cs3733d18.teamp.ui.admin.ConfirmationPopUpController;
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
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ServiceRequestScreen implements Initializable{
    PopUpController popUpController;
    ConfirmationPopUpController confirmationPopUpController;
    DBSystem db = DBSystem.getInstance();
    ArrayList<Request> requests;
    int requestSize;
    Thread thread;
    ArrayList<Request> emergencyRequests = new ArrayList<>();
    ArrayList<Request> otherRequests = new ArrayList<>();

    ArrayList<TreeItem<ServiceRequestTable>> newRequestTableChildren;
    ArrayList<TreeItem<ServiceRequestTable>> inProgRequestTableChildren;
    ArrayList<TreeItem<ServiceRequestTable>> completedRequestTableChildren;

    @FXML
    JFXButton backButton;

    @FXML
    JFXButton claimRequestButton;

    @FXML
    JFXButton recordsButton;

    @FXML
    JFXButton completeRequestButton;

    @FXML
    Label serviceRequestErrorLabel;

    @FXML
    Label timeCreatedLabel;

    @FXML
    Label timeCompletedLabel;

    @FXML
    Label requestTypeLabel;

    @FXML
    Label locationLabel;

    @FXML
    Label createdByLabel;

    @FXML
    Label assignedToLabel;

    @FXML
    Label additionalInfoLabel;

    @FXML
    Label helloMessage;

    @FXML
    JFXTreeTableView<ServiceRequestTable> newRequestTable;

    @FXML
    JFXTreeTableView<ServiceRequestTable> inProgRequestTable;

    @FXML
    JFXTreeTableView<ServiceRequestTable> completedRequestTable;

    @FXML
    JFXTreeTableColumn<ServiceRequestTable, Integer> rID1;

    @FXML
    JFXTreeTableColumn<ServiceRequestTable, String> rType1;

    @FXML
    JFXTreeTableColumn<ServiceRequestTable, Integer> rID2;

    @FXML
    JFXTreeTableColumn<ServiceRequestTable, String> rType2;

    @FXML
    JFXTreeTableColumn<ServiceRequestTable, Integer> rID3;

    @FXML
    JFXTreeTableColumn<ServiceRequestTable, String> rType3;

    @FXML
    TreeItem<ServiceRequestTable> newRequestTableRoot;

    @FXML
    TreeItem<ServiceRequestTable> inProgRequestTableRoot;

    @FXML
    TreeItem<ServiceRequestTable> completedRequestTableRoot;

    @FXML
    AnchorPane serviceRequestScreenBorderPane;

    @FXML
    JFXComboBox newRequestComboBox;

    static ObservableList<String> requestTypes = FXCollections.observableArrayList(
            "Create New Service Request",
            "Language Interpreter Request",
            "Religious Request",
            "Computer Service Request",
            "Security Request",
            "Maintenance Request",
            "Sanitation Request",
            "Audio or Visual Help Request",
            "Gift Delivery Request",
            "Transportation Request",
            "Prescription Request"
    );

    MenuItem mi1 = new MenuItem("Claim Request");
    MenuItem mi2 = new MenuItem("Complete Request");
    MenuItem mi3 = new MenuItem("Delete Request");
    MenuItem mi4 = new MenuItem("Claim Request");
    MenuItem mi5 = new MenuItem("Complete Request");
    MenuItem mi6 = new MenuItem("Delete Request");
    ContextMenu menu1 = new ContextMenu();
    ContextMenu menu2 = new ContextMenu();

    /**
     * Initializes the hello message
     */
    @FXML
    public void onStartup() {
        helloMessage.setText("Hello, " + Main.currentUser.getFirstName() + " " + Main.currentUser.getLastName());
    }

    /**
     * Gets the list of service requests from the database and puts them in the appropriate tables
     */
    public void populateTableViews() {

        try {
            serviceRequestErrorLabel.setText("");
            requests = db.getAllRequests();

            for (Request r: requests) {
                if (r.getPriority() == 1) {
                    emergencyRequests.add(r);
                }
                else {
                    otherRequests.add(r);
                }
            }

        } catch (RequestNotFoundException rnfe) {
            serviceRequestErrorLabel.setText("There are currently no service requests.");
        }

        for (Request r: emergencyRequests) {
            if (r.isCompleted() == 0) {
                newRequestTableChildren.add(new TreeItem<>(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString())));
            }
            else if (r.isCompleted() == 1) {
                inProgRequestTableChildren.add(new TreeItem<>(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString())));
            }
            else {
                completedRequestTableChildren.add(new TreeItem<>(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString())));
            }
        }

        for (Request r: otherRequests) {
            if (r.isCompleted() == 0) {
                newRequestTableChildren.add(new TreeItem<>(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString())));
            }
            else if (r.isCompleted() == 1) {
                inProgRequestTableChildren.add(new TreeItem<>(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString())));
            }
            else {
                completedRequestTableChildren.add(new TreeItem<>(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString())));
            }
        }

        newRequestTableRoot.getChildren().setAll(newRequestTableChildren);
        inProgRequestTableRoot.getChildren().setAll(inProgRequestTableChildren);
        completedRequestTableRoot.getChildren().setAll(completedRequestTableChildren);

        newRequestTable.setRoot(newRequestTableRoot);
        inProgRequestTable.setRoot(inProgRequestTableRoot);
        completedRequestTable.setRoot(completedRequestTableRoot);

        newRequestTable.setShowRoot(false);
        inProgRequestTable.setShowRoot(false);
        completedRequestTable.setShowRoot(false);

        newRequestComboBox.setPromptText(" Create New Service Request");
    }

    /**
     * Initializes the tableviews
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        newRequestTableChildren = new ArrayList<>();
        inProgRequestTableChildren = new ArrayList<>();
        completedRequestTableChildren = new ArrayList<>();

        newRequestTableRoot = new TreeItem<>();
        inProgRequestTableRoot = new TreeItem<>();
        completedRequestTableRoot = new TreeItem<>();

        rID1 = new JFXTreeTableColumn<>("ID");
        rType1 = new JFXTreeTableColumn<>("Request Type");
        rID2 = new JFXTreeTableColumn<>("ID");
        rType2 = new JFXTreeTableColumn<>("Request Type");
        rID3 = new JFXTreeTableColumn<>("ID");
        rType3 = new JFXTreeTableColumn<>("Request Type");

        rID1.setPrefWidth(75);
        rID1.setResizable(false);
        rID1.setSortable(true);
        rID2.setPrefWidth(75);
        rID2.setResizable(false);
        rID2.setSortable(true);
        rID3.setPrefWidth(75);
        rID3.setResizable(false);
        rID3.setSortable(true);

        rType1.setPrefWidth(441);
        rType1.setResizable(false);
        rType1.setSortable(true);
        rType2.setPrefWidth(441);
        rType2.setResizable(false);
        rType2.setSortable(true);
        rType3.setPrefWidth(441);
        rType3.setResizable(false);
        rType3.setSortable(true);


        rID1.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestID"));
        rType1.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestType"));
        rID2.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestID"));
        rType2.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestType"));
        rID3.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestID"));
        rType3.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestType"));

        newRequestTable.getColumns().addAll(rID1, rType1);
        inProgRequestTable.getColumns().addAll(rID2, rType2);
        completedRequestTable.getColumns().addAll(rID3, rType3);

        populateTableViews();

        serviceRequestScreenBorderPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
        thread = new Thread(task);
        thread.start();

        mi1.setOnAction((ActionEvent event) -> {
            claimRequestButtonOp(event);
        });

        mi3.setOnAction((ActionEvent event) -> {
            deleteRequestOp();
        });

        mi5.setOnAction((ActionEvent event) -> {
            completeRequestButtonOp(event);
        });

        menu1.getItems().add(mi1);
        menu1.getItems().add(mi2);
        menu1.getItems().add(mi3);
        menu2.getItems().add(mi4);
        menu2.getItems().add(mi5);
        menu2.getItems().add(mi6);

        newRequestTable.setContextMenu(menu1);
        inProgRequestTable.setContextMenu(menu2);
        newRequestComboBox.setItems(requestTypes);
    }

    /**
     * Sets the available right click options based on the users information for the newRequestTable
     */
    public void disableMenu1OptionsOp() {
        menu1.getItems().get(1).setDisable(true);
        int requestID = newRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
        boolean claim = false;
        boolean delete = false;
        try {
            Request r = db.getOneRequest(requestID);
            if (r.getRequestType() == Request.requesttype.LANGUAGEINTERP || r.getRequestType() == Request.requesttype.HOLYPERSON) {
                if (Main.currentUser.getIsAdmin() ||
                        (db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType())) &&
                                Main.currentUser.getSubType().equals(r.getSubType()))) {
                    claim = true;
                }
                String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getMadeBy())) {
                    delete = true;
                }
            }
            else {
                if (Main.currentUser.getIsAdmin() || db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType()))) {
                    claim = true;
                }
                String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getMadeBy())) {
                    delete = true;
                }
            }
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }

        if (!claim) {
            menu1.getItems().get(0).setDisable(true);
        }
        if (!delete) {
            menu1.getItems().get(2).setDisable(true);
        }
    }

    /**
     * Sets the available right click options based on the users information for the inProgRequestTable
     */
    public void disableMenu2OptionsOp() {
        menu2.getItems().get(0).setDisable(true);
        menu2.getItems().get(2).setDisable(true);
        int requestID = inProgRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
        boolean emptype = false;
        try {
            Request r = db.getOneRequest(requestID);
            String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
            if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getCompletedBy())) {
                emptype = true;
            }
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }

        if (!emptype) {
            menu2.getItems().get(1).setDisable(true);
        }
    }

    public void deleteRequestOp() {
        int requestID = newRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            db.deleteRequest(r);
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
        refresh();
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


    /**
     * Fills in the gridpane with info from requests in the newRequestTable
     */
    public void onNewRequestTableClickOp() {
        try {
            int requestID = newRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
            try {
                Request r = db.getOneRequest(requestID);
                timeCreatedLabel.setText(r.convertTime(r.getTimeMade().getTime()));
                requestTypeLabel.setText(r.toString());
                locationLabel.setText(r.getLocation());
                createdByLabel.setText(r.getMadeBy());
                assignedToLabel.setText(r.getCompletedBy());
                additionalInfoLabel.setText(r.getAdditionalInfo());
            }
            catch (RequestNotFoundException re) {
                re.printStackTrace();
            }
        }
        catch (NullPointerException npe) {

        }
    }

    /**
     * Fills in the gridpane with info from requests in the inProgRequestTable
     */
    public void onInProgRequestTableClickOp() {
        try {
            int requestID = inProgRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
            try {
                Request r = db.getOneRequest(requestID);
                timeCreatedLabel.setText(r.convertTime(r.getTimeMade().getTime()));
                timeCompletedLabel.setText("");
                requestTypeLabel.setText(r.toString());
                locationLabel.setText(r.getLocation());
                createdByLabel.setText(r.getMadeBy());
                assignedToLabel.setText(r.getCompletedBy());
                additionalInfoLabel.setText(r.getAdditionalInfo());
            } catch (RequestNotFoundException re) {
                re.printStackTrace();
            }
        }
        catch (NullPointerException npe) {

        }
    }

    /**
     * Fills in the gridpane with info from requests in the completedRequestTable
     */
    public void onCompletedRequestTableClickOp() {
        try {
            int requestID = completedRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
            try {
                Request r = db.getOneRequest(requestID);
                timeCreatedLabel.setText(r.convertTime(r.getTimeMade().getTime()));
                timeCompletedLabel.setText(r.convertTime(r.getTimeCompleted().getTime()));
                requestTypeLabel.setText(r.toString());
                locationLabel.setText(r.getLocation());
                createdByLabel.setText(r.getMadeBy());
                assignedToLabel.setText(r.getCompletedBy());
                additionalInfoLabel.setText(r.getAdditionalInfo());
            } catch (RequestNotFoundException re) {
                re.printStackTrace();
            }
        }
        catch (NullPointerException npe) {

        }
    }

    /**
     * This button marks a service request as in progress
     * @param e action event
     */
    @FXML
    public void claimRequestButtonOp(ActionEvent e) {
        serviceRequestErrorLabel.setText("");
        try {
            int requestID = newRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
            try {
                Request r = db.getOneRequest(requestID);
                if (r.getRequestType() == Request.requesttype.LANGUAGEINTERP || r.getRequestType() == Request.requesttype.HOLYPERSON) {
                    if (Main.currentUser.getIsAdmin() ||
                            (db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType())) &&
                                    Main.currentUser.getSubType().equals(r.getSubType()))) {
                        r.setCompleted(1);
                        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                        r.setCompletedBy(firstAndLastName);
                        db.modifyRequest(r);
                    } else {
                        serviceRequestErrorLabel.setText("You are not authorized to claim this service request");
                    }
                } else {
                    if (Main.currentUser.getIsAdmin() || db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType()))) {
                        r.setCompleted(1);
                        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                        r.setCompletedBy(firstAndLastName);
                        db.modifyRequest(r);
                    } else {
                        serviceRequestErrorLabel.setText("You are not authorized to claim this service request");
                        serviceRequestErrorLabel.setVisible(true);
                    }
                }

            } catch (RequestNotFoundException re) {
                re.printStackTrace();
            }
        }
        catch (NullPointerException npe) {

        }
        refresh();
    }

    /**
     * This button marks a service request as complete
     * @param e action event
     */
    @FXML
    public void completeRequestButtonOp(ActionEvent e) {
        serviceRequestErrorLabel.setText("");
        try {
            int requestID = inProgRequestTable.getSelectionModel().getSelectedItem().getValue().getRequestID();
            try {
                Request r = db.getOneRequest(requestID);
                String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getCompletedBy())) {
                    r.setCompleted(2);
                    r.setCompletedBy(firstAndLastName);
                    db.completeRequest(r);
                } else {
                    serviceRequestErrorLabel.setText("You are not authorized to claim this service request");
                }
            } catch (RequestNotFoundException re) {
                re.printStackTrace();
            }
        }
        catch (NullPointerException npe) {

        }
        refresh();
    }

    /**
     * When the back button is pressed the stage is brought back to the home screen
     * @param e action event
     * @throws IOException
     */
    @FXML
    public Boolean backButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = (Stage) backButton.getScene().getWindow();
        if(Main.currentUser.getIsAdmin()) {
            loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
        }else{
            loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
            Main.logoutCurrentUser();
        }
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
     * When the records button is pressed the stages is brought to the records screen
     */
    @FXML
    public Boolean recordsButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        RecordScreenController recordScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/RecordScreen.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        recordScreenController = loader.getController();
        recordScreenController.onStartUp();
        recordsButton.getScene().setRoot(root);
        return true;
    }

    /**
     * opens the pop up for the new service request form
     * @param e action event
     */
    @FXML
    public Boolean selectNewRequestOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        String selection = newRequestComboBox.getValue().toString();

        if (selection.equals("Create New Service Request")) {
            return false;
        }
        else if (selection.equals("Transportation Request")) {

            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/FXML/general/ConfirmationPopUp.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return false;
            }

            confirmationPopUpController = loader.getController();
            confirmationPopUpController.StartUp(this);
            stage.setScene(new Scene(root, 600, 150));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(newRequestComboBox.getScene().getWindow());
            stage.showAndWait();

            if(confirmationPopUpController.getChoice()) {
                TransportationRequest tr = new TransportationRequest();

                try {
                    tr.run(0, 0, 0, 0, null, null, null);
                    stage = (Stage) newRequestComboBox.getScene().getWindow();
                    stage.setFullScreen(true);
                } catch (ServiceException se) {
                    se.printStackTrace();
                }
            }
            return true;
        }
        else if (selection.equals("Prescription Request")) {

            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/FXML/general/ConfirmationPopUp.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return false;
            }

            confirmationPopUpController = loader.getController();
            confirmationPopUpController.StartUp(this);
            stage.setScene(new Scene(root, 600, 150));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(newRequestComboBox.getScene().getWindow());
            stage.showAndWait();

            if(confirmationPopUpController.getChoice()) {
                RaikouAPI r = new RaikouAPI();

                try {
                    r.run(0, 0, 1920, 1080, null, null, null);
                    stage = (Stage) newRequestComboBox.getScene().getWindow();
                    stage.setFullScreen(true);
                } catch (edu.wpi.cs3733d18.teamR.ServiceException se) {
                    se.printStackTrace();
                }
            }
            return true;
        }
        else {
            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/FXML/service/FormContainer.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return false;
            }

            popUpController = loader.getController();
            popUpController.StartUp(this, selection);
            stage.setScene(new Scene(root, 1000, 950));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(newRequestComboBox.getScene().getWindow());
            stage.show();
            return true;
        }
    }

    /**
     * Called by various functions to clear the TableViews and calls populateTableViews() again
     */
    public void refresh() {
        newRequestTableChildren.clear();
        inProgRequestTableChildren.clear();
        completedRequestTableChildren.clear();

        emergencyRequests.clear();
        otherRequests.clear();

        populateTableViews();
    }
}

