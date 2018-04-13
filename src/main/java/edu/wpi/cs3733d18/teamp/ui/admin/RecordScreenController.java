package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RecordScreenController implements Initializable {
    DBSystem db = DBSystem.getInstance();
    ArrayList<Record> records;

    @FXML
    JFXButton backButton;

    @FXML
    TableView recordTable;

    @FXML
    JFXComboBox filterRecordComboBox;

    @FXML
    TableColumn colRequestType;

    @FXML
    TableColumn colSpecificType;

    @FXML
    TableColumn colTotalOfType;

    @FXML
    TableColumn colTotalTime;

    @FXML
    TableColumn colAvgTime;

    ObservableList<String> requestTypes = FXCollections.observableArrayList(
            "All",
            "Language Interpreter",
            "Religious Request"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb){
        filterRecordComboBox.setItems(requestTypes);
    }

    /**
     * Initialize the record table
     * @return
     */
    @FXML
    public boolean onStartUp() {
        records = db.getAllRecords();

        colRequestType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        colSpecificType.setCellValueFactory(new PropertyValueFactory<>("subType"));
        colTotalOfType.setCellValueFactory(new PropertyValueFactory<>("totalOfType"));
        colTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        colAvgTime.setCellValueFactory(new PropertyValueFactory<>("avgTime"));

        // Add all records to table
        for (Record curRecord: records) {
            recordTable.getItems().add(curRecord);
        }

        return true;
    }

    /**
     * When the back button is pressed the stage is brought back to the home screen
     * @param e action event
     * @throws IOException
     */
    @FXML
    public Boolean backButtonOp(ActionEvent e) {
        FXMLLoader loader;
        Stage stage;
        Parent root;

        loader = new FXMLLoader(getClass().getResource("/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1920, 1080));
        stage.setFullScreen(true);
        stage.show();
        return true;
    }

    /**
     *
     * @param e Action event
     * @return
     */
    public Boolean filterRecordComboBoxOp(ActionEvent e) {
        if (filterRecordComboBox.getValue().toString().equals("All")) {
            recordTable.getItems().clear();
            for (Record record: records) {
                recordTable.getItems().add(record);
            }
        }
        if (filterRecordComboBox.getValue().toString().equals("Language Interpreter")) {
            recordTable.getItems().clear();
            for (Record record: records) {
                if (db.RequestTypeToString(record.getRequestType()).equals("language interpreter")) {
                    recordTable.getItems().add(record);
                }
            }
        }
        else if (filterRecordComboBox.getValue().toString().equals("Religious Request")) {
            recordTable.getItems().clear();
            for (Record record: records) {
                if (db.RequestTypeToString(record.getRequestType()).equals("religion handler")) {
                    recordTable.getItems().add(record);
                }
            }
        }
        return true;
    }
}
