package edu.wpi.cs3733d18.teamp.ui;


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
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ReligiousController implements Initializable {

    ArrayList<String> locationWords = new ArrayList<String>();
    DBSystem db = DBSystem.getInstance();
    ServiceRequestNewPopUpController serviceRequestNewPopUpController;
    ServiceRequestScreen serviceRequestScreen;

    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    Label religiousRequestErrorLabel;

    @FXML
    JFXTextField religiousRequestLocationTxt = new JFXTextField();

    @FXML
    JFXComboBox religiousRequestComboBox;

    @FXML
    JFXTextArea religiousRequestInfoTxtArea;

    ObservableList<String> religions = FXCollections.observableArrayList(
            "Christianity",
            "Judaism",
            "Islam",
            "Hinduism",
            "Buddhism"
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
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLocationArray();

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(religiousRequestLocationTxt, locationWords);

        destBinding.setPrefWidth(religiousRequestLocationTxt.getPrefWidth());

        religiousRequestComboBox.setValue("Choose a religion");
        religiousRequestComboBox.setItems(religions);
    }

    /**
     * Calls startup
     * @param serviceRequestNewPopUpController
     */
    public void StartUp(ServiceRequestScreen serviceRequestScreen, ServiceRequestNewPopUpController serviceRequestNewPopUpController) {
        this.serviceRequestScreen = serviceRequestScreen;
        this.serviceRequestNewPopUpController = serviceRequestNewPopUpController;
    }

    /**
     * Cancels the form if the user decides they don't need to make a service request
     * @param e the action of clicking the button
     */
    @FXML
    public void cancelButtonOp(ActionEvent e){
        Stage stage = (Stage) cancelFormButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Submits the form for a new service request
     * @param e
     */
    @FXML
    public void submitFormButtonOp(ActionEvent e) {
        String religion = null;
        String nodeID = null;
        HashMap<String, Node> nodeSet = db.getAllNodes();


        Request.requesttype type = Request.requesttype.HOLYPERSON;
        try {
            nodeID = religiousRequestLocationTxt.getText();
            String religiousID = parseSourceInput(nodeID).getID();
            Node locationNode = nodeSet.get(religiousID);
            if (locationNode == null) {
                throw new NodeNotFoundException(religiousID);
            }
        } catch(NodeNotFoundException nnfe) {
            religiousRequestErrorLabel.setText("Please insert a valid location.");
            religiousRequestErrorLabel.setVisible(true);
            return;
        }
        try {
            religion = religiousRequestComboBox.getValue().toString();
            if(religion.equals("Choose a religion")) {
                throw new NothingSelectedException();
            }
        } catch(NothingSelectedException nse) {
            religiousRequestErrorLabel.setText("Please select a religion.");
            religiousRequestErrorLabel.setVisible(true);
            return;
        }
        String additionalInfo = religiousRequestInfoTxtArea.getText().replaceAll("\n", System.getProperty("line.separator"));
        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
        String totalInfo = "Religion: " + religion + " Additional Info: " + additionalInfo;
        Timestamp timeMade = new Timestamp(System.currentTimeMillis());
        Request newRequest = new Request(type, religion, nodeID, totalInfo, firstAndLastName, " ", timeMade, null, 0);
        db.createRequest(newRequest);
        serviceRequestScreen.refresh();
        Stage stage = (Stage) submitFormButton.getScene().getWindow();
        stage.close();
    }

    public Node parseSourceInput(String string) {
        Node aNode = new Node();
//        System.out.println("Input string: " + string);

        HashMap<String, Node> nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getShortName().compareTo(string) == 0) {
                aNode = node;
            }
        }

        return aNode;
    }
}
