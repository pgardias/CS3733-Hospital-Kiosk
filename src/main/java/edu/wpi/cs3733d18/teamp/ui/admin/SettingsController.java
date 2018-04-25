package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Settings;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import java.io.IOException;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext.PathfindingSetting;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsController {
    Thread thread;
    ConfirmationPopUpController confirmationPopUpController;
    boolean isSaved = false;


    @FXML
    JFXTextField feetPerPixelTextField;

    @FXML
    JFXButton submitButton;

    @FXML
    JFXButton backButton;

    @FXML
    JFXTextField timeoutDelayTextField;

    @FXML
    JFXComboBox algorithmComboBox;

    @FXML
    StackPane settingsScreenStackPane;

    ObservableList<String> algorithmTypes = FXCollections.observableArrayList(
            "A*",
            "Depth-first",
            "Breadth-first",
            "Dijkstra's",
            "Best-first"
    );

    /**
     * Sets the fields in the settings screen based upon the values in the
     * settings singleton.
     */
    @FXML
    public void onStartUp() {
        algorithmComboBox.setItems(algorithmTypes);
        timeoutDelayTextField.setText(Integer.toString(Settings.getTimeDelay() / 1000));
        switch (Settings.getPathfindingSettings()) {
            case AStar: {
                algorithmComboBox.setValue("A*");
                break;
            }
            case DepthFirst: {
                algorithmComboBox.setValue("Depth-first");
                break;
            }
            case BreadthFirst: {
                algorithmComboBox.setValue("Breadth-first");
                break;
            }
            case Dijkstra: {
                algorithmComboBox.setValue("Dijkstra's");
                break;
            }
            case BestFirst: {
                algorithmComboBox.setValue("BestFirst");
                break;
            }
            default: {
                System.out.println("Admin Settings Panel was unable to set the current Pathfinding Algorithm Context");
                break;
            }
        }
        feetPerPixelTextField.setText(Double.toString(Settings.getFeetPerPixel()));

        settingsScreenStackPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
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
         * Sets the settings singleton based upon the current inputs when the
         * submit button is pressed.
         */
        @FXML
        public void submitButtonOp () {
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
            confirmationPopUpController.setSettingsButtonOp("Submit");
            confirmationPopUpController.StartUp(this);
            stage.setScene(new Scene(root, 600, 150));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(submitButton.getScene().getWindow());
            stage.showAndWait();

            try {
                Settings.setTimeDelay(Integer.parseInt(timeoutDelayTextField.getText()) * 1000);
            } catch (NumberFormatException ne) {
                ne.printStackTrace();
                return;
            }
            if(confirmationPopUpController.getChoice()){
                switch (algorithmComboBox.getValue().toString()) {
                    case "A*":
                        Settings.setPathfindingAlgorithm(PathfindingSetting.AStar);
                        break;
                    case "Depth-first":
                        Settings.setPathfindingAlgorithm(PathfindingSetting.DepthFirst);
                        break;
                    case "Breadth-first":
                        Settings.setPathfindingAlgorithm(PathfindingSetting.BreadthFirst);
                        break;
                    case "Dijkstra's":
                        Settings.setPathfindingAlgorithm(PathfindingSetting.Dijkstra);
                        break;
                    case "Best-first":
                Settings.setPathfindingAlgorithm(PathfindingSetting.BestFirst);
                break;
            }
                try {
                    Settings.setFeetPerPixel(Double.parseDouble(feetPerPixelTextField.getText()));
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                    // TODO Set error label appropriately
                    return;
                }
            }
        }

        @FXML
        public void backButtonOp(ActionEvent e) {
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
            confirmationPopUpController.setSettingsButtonOp("Back");
            confirmationPopUpController.StartUp(this);
            stage.setScene(new Scene(root, 600, 150));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(submitButton.getScene().getWindow());
            stage.showAndWait();

            if(confirmationPopUpController.getChoice()) {

                loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
                try {
                    root = loader.load();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return;
                }
                backButton.getScene().setRoot(root);
            }
        }
}
