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

public class AudioVisualController implements Initializable {

    ArrayList<String> locationWords = new ArrayList<String>();
    DBSystem db = DBSystem.getInstance();
    PopUpController popUpController;
    ServiceRequestScreen serviceRequestScreen;

    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    Label AVErrorLabel;

    @FXML
    JFXTextField AVLocationTxt = new JFXTextField();

    @FXML
    JFXComboBox AVComboBox;

    @FXML
    JFXTextArea AVInfoTxtArea;

    ObservableList<String> AVDevices = FXCollections.observableArrayList(
            "Hearing Aid",
            "Projector",
            "TV",
            "Radio",
            "IPad",
            "Drawing Pad",
            "White Board and Markers"
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

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(AVLocationTxt, locationWords);

        destBinding.setPrefWidth(AVLocationTxt.getPrefWidth());

        AVComboBox.setValue("Choose a device");
        AVComboBox.setItems(AVDevices);
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
        String AVDevice = null;
        String nodeID = null;
        HashMap<String, Node> nodeSet = db.getAllNodes();

        Request.requesttype type = Request.requesttype.AV;
        try {
            nodeID = AVLocationTxt.getText();
            String AVDeviceID = parseSourceInput(nodeID).getID();
            Node locationNode = nodeSet.get(AVDeviceID);
            if (locationNode == null) {
                throw new NodeNotFoundException(AVDeviceID);
            }
        } catch (NodeNotFoundException nnfe) {
            AVErrorLabel.setText("Please insert a valid location.");
            AVErrorLabel.setVisible(true);
            return;
        }
        try {
            AVDevice = AVComboBox.getValue().toString();
            if (AVDevice.equals("Choose a device")) {
                throw new NothingSelectedException();
            }
        } catch (NothingSelectedException nse) {
            AVErrorLabel.setText("Please select a device.");
            AVErrorLabel.setVisible(true);
            return;
        }
        String additionalInfo = AVInfoTxtArea.getText().replaceAll("\n", System.getProperty("line.separator"));
        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
        String totalInfo = "Audio or Visual Device: " + AVDevice + " Additional Info: " + additionalInfo;
        Timestamp timeMade = new Timestamp(System.currentTimeMillis());
        Request newRequest = new Request(type, AVDevice, nodeID, totalInfo, firstAndLastName, " ", timeMade, null, 0);
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