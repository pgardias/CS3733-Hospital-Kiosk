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
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LanguageInterpreterController implements Initializable {

    ArrayList<String> locationWords = new ArrayList<String>();
    DBSystem db = DBSystem.getInstance();
    PopUpController popUpController;
    ServiceRequestScreen serviceRequestScreen;

    @FXML
    JFXButton cancelFormButton;

    @FXML
    JFXButton submitFormButton;

    @FXML
    Label languageInterpreterErrorLabel;

    @FXML
    JFXTextField languageInterpreterLocationTxt = new JFXTextField();

    @FXML
    JFXComboBox languageInterpreterComboBox;

    @FXML
    JFXTextArea languageInterpreterInfoTxtArea;

    static ObservableList<String> languages = FXCollections.observableArrayList(
            "French",
            "Spanish",
            "Portuguese",
            "Polish",
            "German",
            "Mandarin",
            "Turkish",
            "Japanese"
    );

    /**
     * Sets the array for the name of the locations
     */
    private void setLocationArray() {
        HashMap<String, Node> nodeSet;
        nodeSet = db.getAllNodes();
        for (Node node : nodeSet.values()) {
            locationWords.add(node.getLongName());
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

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(languageInterpreterLocationTxt, locationWords);

        destBinding.setPrefWidth(languageInterpreterLocationTxt.getPrefWidth());

        languageInterpreterComboBox.setItems(languages);
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
        String language = null;
        String nodeID = null;
        HashMap<String, Node> nodeSet = db.getAllNodes();

        Request.requesttype type = Request.requesttype.LANGUAGEINTERP;
        try {
            nodeID = languageInterpreterLocationTxt.getText();
            String interpreterID = parseSourceInput(nodeID).getID();
            Node locationNode = nodeSet.get(interpreterID);
            if (locationNode == null) {
                throw new NodeNotFoundException(interpreterID);
            }
        } catch (NodeNotFoundException nnfe) {
            languageInterpreterErrorLabel.setText("Please insert a valid location.");
            languageInterpreterErrorLabel.setVisible(true);
            return;
        }
        try {
            language = languageInterpreterComboBox.getSelectionModel().getSelectedItem().toString();
            if (language.equals("Select a language")) {
                throw new NothingSelectedException();
            }
        } catch (NothingSelectedException nse) {
            languageInterpreterErrorLabel.setText("Please select a language.");
            languageInterpreterErrorLabel.setVisible(true);
            return;
        }
        String additionalInfo = languageInterpreterInfoTxtArea.getText().replaceAll("\n", System.getProperty("line.separator"));
        String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
        String totalInfo = "Language: " + language + " Additional Info: " + additionalInfo;
        Timestamp timeMade = new Timestamp(System.currentTimeMillis());
        Request newRequest = new Request(type, language, nodeID, totalInfo, firstAndLastName, " ", timeMade, null, 0);
        db.createRequest(newRequest);
        serviceRequestScreen.refresh();
        Stage stage = (Stage) submitFormButton.getScene().getWindow();
        stage.close();
    }


    public Node parseSourceInput(String string) {
        Node aNode = new Node();

        HashMap<String, Node> nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getLongName().compareTo(string) == 0) {
                aNode = node;
            }
        }

        return aNode;
    }

}