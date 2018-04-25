package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Settings;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenuController {

    Thread thread;

    @FXML
    JFXButton employeeButton;

    @FXML
    JFXButton mapManagementButton;

    @FXML
    JFXButton serviceRequestScreenButton;

    @FXML
    JFXButton settingsButton;

    @FXML
    JFXButton backButton;

    @FXML
    Label helloMessage;

    @FXML
    StackPane adminMenuScreenStackPane;

    /**
     * initializes welcome label
     */
    @FXML
    public void onStartup() {
        //displays name of user on startup
        helloMessage.setText("Hello " + Main.currentUser.getFirstName() + " " + Main.currentUser.getLastName() + ", ");
        adminMenuScreenStackPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
        thread = new Thread(task);
        thread.start();
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
     * loads the manage employees screen
     * @param e
     */
    @FXML
    public void employeeButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/ManageEmployeeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        employeeButton.getScene().setRoot(root);
    }

    /**
     * loads the map management screen
     * @param e
     */
    @FXML
    public void mapManagementButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        MapBuilderController mapBuilderController;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/MapBuilder.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        mapBuilderController = loader.getController();
        mapBuilderController.startUp();
        mapManagementButton.getScene().setRoot(root);
    }

    /**
     * loads the service request screen
     * @param e
     */
    @FXML
    public void serviceRequestScreenButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        ServiceRequestScreen serviceRequestScreen;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        serviceRequestScreen = loader.getController();
        serviceRequestScreen.onStartup();
        serviceRequestScreenButton.getScene().setRoot(root);
    }

    /**
     * brings you back to the home page
     * @param e
     */
    @FXML
    public void backButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        Main.logoutCurrentUser();
        backButton.getScene().setRoot(root);
    }

    /**
     * brings you to the settings page
     * @param e
     */
    @FXML
    public void settingsButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        SettingsController settingsController;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/SettingsScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        settingsController = loader.getController();
        settingsController.onStartUp();
        settingsButton.getScene().setRoot(root);
    }
}
