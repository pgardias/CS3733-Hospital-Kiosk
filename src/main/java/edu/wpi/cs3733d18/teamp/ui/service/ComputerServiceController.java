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

public class ComputerServiceController implements Initializable {

    ArrayList<String> locationWords = new ArrayList<String>();
    DBSystem db = DBSystem.getInstance();
    PopUpController popUpController;
    ServiceRequestScreen serviceRequestScreen;

    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    Label computerServiceErrorLabel;

    @FXML
    JFXTextField computerServiceLocationTxt = new JFXTextField();

    @FXML
    JFXComboBox computerServiceComboBox;

    @FXML
    JFXTextArea computerServiceInfoTxtArea;

    ObservableList<String> devices = FXCollections.observableArrayList(
            "Desktop Computer",
            "Laptop",
            "Projector",
            "Podium",
            "TV",
            "Printer",
            "Fax Machine",
            "Pager"
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

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(computerServiceLocationTxt, locationWords);

        destBinding.setPrefWidth(computerServiceLocationTxt.getPrefWidth());

        computerServiceComboBox.setValue("Choose a device");
        computerServiceComboBox.setItems(devices);
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
        String device = null;
        String nodeID = null;
        HashMap<String, Node> nodeSet = db.getAllNodes();

        Request.requesttype type = Request.requesttype.COMPUTER;
        try {
            nodeID = computerServiceLocationTxt.getText();
            String computerID = parseSourceInput(nodeID).getID();
            Node locationNode = nodeSet.get(computerID);
            if (locationNode == null) {
                throw new NodeNotFoundException(computerID);
            }
        } catch (NodeNotFoundException nnfe) {
            computerServiceErrorLabel.setText("Please insert a valid location.");
            computerServiceErrorLabel.setVisible(true);
            return;
        }
        try {
            device = computerServiceComboBox.getValue().toString();
            if (device.equals("Choose a device")) {
                throw new NothingSelectedException();
            }
        } catch (NothingSelectedException nse) {
            computerServiceErrorLabel.setText("Please select a device.");
            computerServiceErrorLabel.setVisible(true);
            return;
        }
        String additionalInfo = computerServiceInfoTxtArea.getText().replaceAll("\n", System.getProperty("line.separator"));
        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
        String totalInfo = "Device: " + device + " Additional Info: " + additionalInfo;
        Timestamp timeMade = new Timestamp(System.currentTimeMillis());
        Request newRequest = new Request(type, device, nodeID, totalInfo, firstAndLastName, " ", timeMade, null, 0);
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