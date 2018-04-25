package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Employee;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Request;
import edu.wpi.cs3733d18.teamp.Settings;
import edu.wpi.cs3733d18.teamp.ui.MouseEventHandler;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.ACTIVE;

import java.io.IOException;
import java.sql.Timestamp;


public class EmergencyRequestButtonScreenController {
    private static final int FIRE = 1;
    private static final int MEDICAL = 2;
    private static final int ACTIVE_SHOOTER = 3;

    private int typeOfEmergency;
    HomeController homeController;
    private MouseEventHandler mouseEventHandler2;
    Thread thread;

    DBSystem db = DBSystem.getInstance();

    @FXML
    JFXButton activeShooterEmergencyButton;

    @FXML
    JFXButton fireEmergencyButton;

    @FXML
    JFXButton medicalEmergencyButton;

    @FXML
    JFXPasswordField emergencyPinPassword;

    @FXML
    JFXButton backButton;

    @FXML
    StackPane emergencyStackPane;

    @FXML
    public void onStartUp(HomeController homeController){
        this.homeController = homeController;
        emergencyPinPassword.requestFocus();
        activeShooterEmergencyButton.setVisible(false);
        fireEmergencyButton.setVisible(false);
        medicalEmergencyButton.setVisible(false);
        emergencyStackPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
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

    public boolean emergencyOp(){
        FXMLLoader loader;
        Scene scene;
        Parent root;

        Request.requesttype requestType = Request.requesttype.EMERGENCY;
        String subType;
        String nodeID = "PKIOS00102"; // default kiosk?
        Timestamp timeMade = new Timestamp(System.currentTimeMillis());

        switch(typeOfEmergency){
            case FIRE:
                subType = "FIRE";
                scene = fireEmergencyButton.getScene();
                break;
            case MEDICAL:
                subType = "MEDICAL";
                scene = medicalEmergencyButton.getScene();
                break;
            case ACTIVE_SHOOTER:
                subType = "ACTIVE SHOOTER";
                scene = activeShooterEmergencyButton.getScene();
                break;
             default:
                 scene = null;
                 return false;

        }

        String totalInfo = "Emergency: " + subType;
        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
        Request newRequest = new Request(requestType, subType, nodeID, totalInfo, firstAndLastName, " ", timeMade, null, 0);
        db.createRequest(newRequest);

        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.getMessage();
            ie.printStackTrace();
            return false;
        }


        scene.setRoot(root);
        return true;

    }

    @FXML
    public void fireEmergencyButtonOp(ActionEvent e){
        typeOfEmergency = FIRE;
        emergencyOp();
    }

    @FXML
    public void medicalEmergencyButtonOp(ActionEvent e){
        typeOfEmergency = MEDICAL;
        emergencyOp();
    }

    @FXML
    public void activeShooterEmergencyButtonOp(ActionEvent e){
        typeOfEmergency = ACTIVE_SHOOTER;
        emergencyOp();
    }

    @FXML
    public void emergencyPinPasswordOp(ActionEvent e){
        String pin = emergencyPinPassword.getText();

        if(pin.equals("2000")){
            fireEmergencyButton.setVisible(true);
            medicalEmergencyButton.setVisible(true);
            activeShooterEmergencyButton.setVisible(true);
        }else{
            ShakeTransition anim = new ShakeTransition(emergencyPinPassword);
            anim.playFromStart();
            emergencyPinPassword.setPromptText("PLEASE ENTER PIN HERE");
            emergencyPinPassword.clear();
        }
    }

    @FXML
    public Boolean backButtonOp(ActionEvent e) {
        Scene scene;
        Parent root;
        FXMLLoader loader;

        scene = backButton.getScene();

        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }


        scene.setRoot(root);
        return true;
    }
}
