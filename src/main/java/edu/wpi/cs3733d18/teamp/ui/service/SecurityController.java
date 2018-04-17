package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NothingSelectedException;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SecurityController implements Initializable {

    ArrayList<String> locationWords = new ArrayList<String>();
    DBSystem db = DBSystem.getInstance();
    PopUpController popUpController;
    ServiceRequestScreen serviceRequestScreen;

    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    Label securityErrorLabel;

    @FXML
    JFXTextField securityLocationTxt = new JFXTextField();

    @FXML
    JFXComboBox securityComboBox;

    @FXML
    JFXTextArea securityInfoTxtArea;

    ObservableList<String> situations = FXCollections.observableArrayList(
            "Patient Disrespecting Hospital Employees",
            "Patient Injuring Hospital Employees",
            "Patient Injuring Fellow Patients",
            "Visitor Refusing To Be Identified",
            "Visitor Disrespecting Hospital Employees",
            "Visitor Injuring Patients",
            "Visitor Injuring Hospital Employees",
            "Visitor Injuring Other Visitors",
            "Rogue Employee"
    );

    /**
     * Sets the array for the name of the locations
     */
    private void setLocationArray() {
        HashMap<String, Node> nodeSet;
        nodeSet = db.getAllNodes();
        for (Node node : nodeSet.values()) {
            locationWords.add(node.getShortName());
        }

    }

    /**
     * Initializes the drop down menu
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLocationArray();

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(securityLocationTxt, locationWords);

        destBinding.setPrefWidth(securityLocationTxt.getPrefWidth());

        securityComboBox.setValue("Choose a situation");
        securityComboBox.setItems(situations);
    }

    /**
     * Calls startup
     *
     * @param serviceRequestScreen
     * @param popUpController
     */
    public void StartUp(ServiceRequestScreen serviceRequestScreen, PopUpController popUpController) {
        this.serviceRequestScreen = serviceRequestScreen;
        this.popUpController = popUpController;
    }

    /**
     * Cancels the form if the user decides they don't need to make a service request
     *
     * @param e the action of clicking the button
     */
    @FXML
    public void cancelButtonOp(ActionEvent e) {
        Stage stage = (Stage) cancelFormButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Submits the form for a new service request
     *
     * @param e Action event
     */
    @FXML
    public void submitFormButtonOp(ActionEvent e) {
        String situation = null;
        String nodeID = null;
        HashMap<String, Node> nodeSet = db.getAllNodes();

        Request.requesttype type = Request.requesttype.SECURITY;
        try {
            nodeID = securityLocationTxt.getText();
            String situationID = parseSourceInput(nodeID).getID();
            Node locationNode = nodeSet.get(situationID);
            if (locationNode == null) {
                throw new NodeNotFoundException(situationID);
            }
        } catch (NodeNotFoundException nnfe) {
            securityErrorLabel.setText("Please insert a valid location.");
            securityErrorLabel.setVisible(true);
            return;
        }
        try {
            situation = securityComboBox.getValue().toString();
            if (situation.equals("Choose a situation")) {
                throw new NothingSelectedException();
            }
        } catch (NothingSelectedException nse) {
            securityErrorLabel.setText("Please select a situation.");
            securityErrorLabel.setVisible(true);
            return;
        }
        String additionalInfo = securityInfoTxtArea.getText().replaceAll("\n", System.getProperty("line.separator"));
        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
        String totalInfo = "Situation: " + situation + " Additional Info: " + additionalInfo;
        Timestamp timeMade = new Timestamp(System.currentTimeMillis());
        Request newRequest = new Request(type, situation, nodeID, totalInfo, firstAndLastName, " ", timeMade, null, 0);
        db.createRequest(newRequest);
        serviceRequestScreen.refresh();
        Stage stage = (Stage) submitFormButton.getScene().getWindow();
        stage.close();
    }


    public Node parseSourceInput(String string) {
        Node aNode = new Node();

        HashMap<String, Node> nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getShortName().compareTo(string) == 0) {
                aNode = node;
            }
        }

        return aNode;
    }
}