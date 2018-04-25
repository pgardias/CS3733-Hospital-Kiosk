package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Settings;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutScreenController {

    Thread thread;

    @FXML
    JFXButton backButton;

    @FXML
    JFXButton creditsButton;

    @FXML
    Label introLabel;

    @FXML
    Label teamLabel;

    @FXML
    Label finalLabel;

    @FXML
    BorderPane aboutScreenBorderPane;

    public void StartUp() {
        introLabel.setText(
                "We are a team of ten WPI students. This application is our project for the course CS3733-D18 Software Engineering. \nHere is our team:"
        );
        teamLabel.setText(
                "Professor Wilson Wong - Instructor\n" +
                        "Kyle Corry - Team Coach\n" +
                        "Kyle Savell - Lead Developer \n" +
                        "Ben Titus - Assistant Lead Developer\n" +
                        "Przemek Gardias - Assistant Lead Developer\n" +
                        "Jason King - Assistant Lead Developer\n" +
                        "Cem Alemdar - Project Manager\n" +
                        "Jarod Thompson - Assistant Project Manager\n" +
                        "Nick Pacheco - Product Owner\n" +
                        "Marc McFatter - UI/UX Engineer\n" +
                        "Harry Saperstein - Test Engineer\n" +
                        "Eoin O’Connell - Documentation Analyst\n"
        );
        finalLabel.setText(
                "Special Thanks To:\n" +
                        "WPI Computer Science Department,\n" +
                        "Brigham and Women’s Faulkner Hospital\n" +
                        "and\n" +
                        "Andrew Shinn\n"
        );

        aboutScreenBorderPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
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
        backButton.getScene().setRoot(root);
    }

    @FXML
    public Boolean creditsButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        CreditsScreenController creditsScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/CreditsScreen.fxml"));

        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        creditsScreenController = loader.getController();
        creditsScreenController.StartUp();
        creditsButton.getScene().setRoot(root);
        return true;
    }
}
