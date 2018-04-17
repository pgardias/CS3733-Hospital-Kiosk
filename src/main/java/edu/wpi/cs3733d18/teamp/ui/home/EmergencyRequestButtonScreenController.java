package edu.wpi.cs3733d18.teamp.ui.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Employee;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Request;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.ACTIVE;

import java.io.IOException;
import java.sql.Timestamp;


public class EmergencyRequestButtonScreenController {
    private static final int FIRE = 1;
    private static final int MEDICAL = 2;
    private static final int ACTIVE_SHOOTER = 3;

    private int typeOfEmergency;
    ServiceRequestScreen serviceRequestScreen;
    MainController mainController;

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
    public void onStartUp(MainController mainController){
        this.mainController = mainController;
        emergencyPinPassword.requestFocus();
        activeShooterEmergencyButton.setVisible(false);
        fireEmergencyButton.setVisible(false);
        medicalEmergencyButton.setVisible(false);
    }

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
