package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.RecordNotFoundException;
import edu.wpi.cs3733d18.teamp.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
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
    Label errorLabel;

    @FXML
    GridPane statsGridPane;

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
            "Religious Request",
            "Sanitation Request",
            "Maintenance Request",
            "Security Request",
            "Computer Service Request",
            "Audio/Visual Request",
            "Gift Delivery Request"
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
        try {
            records = db.getAllRecords();

            colRequestType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
            colSpecificType.setCellValueFactory(new PropertyValueFactory<>("subType"));
            colTotalOfType.setCellValueFactory(new PropertyValueFactory<>("totalOfType"));
            colTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
            colAvgTime.setCellValueFactory(new PropertyValueFactory<>("avgTime"));

            // Add all records to table
            for (Record curRecord : records) {
                recordTable.getItems().add(curRecord);
            }

            // Make starting charts
            setChartAll();

        } catch (RecordNotFoundException rnfe) {
            errorLabel.setText("No record data: No requests have been completed!");
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

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
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
     * Displays only the give type of requests
     * @param e Action event
     * @return True if everything runs properly
     */
    public Boolean filterRecordComboBoxOp(ActionEvent e) {
        String requestType = filterRecordComboBox.getValue().toString();
        switch(requestType) {
            case "All":
                recordTable.getItems().clear();
                for (Record record: records) {
                    recordTable.getItems().add(record);
                }
                break;
            case "Language Interpreter":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("language interpreter")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Religious Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("religion handler")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Sanitation Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("sanitation")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Maintenance Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("maintenance")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Security Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("security")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Computer Service Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("computer service")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Audio/Visual Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("audio+visual")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
            case  "Gift Delivery Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("delivergift")) {
                        recordTable.getItems().add(record);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Generates charts for all request types
     */
    private Boolean setChartAll() {

        // Make the pie chart
        ObservableList<PieChart.Data> transportData = FXCollections.observableArrayList();
        int languageCount = db.countType("language interpreter"); // Add amount of language interpreter requests
        if (languageCount > 0) {
            transportData.add(new PieChart.Data("Language Interpreter", languageCount));
        }
        int religiousCount = db.countType("religion handler"); // Add amount of religious requests
        if (religiousCount > 0) {
            transportData.add(new PieChart.Data("Religious", religiousCount));
        }
        int sanitationCount = db.countType("sanitation"); // Add amount of sanitation requests
        if (sanitationCount > 0) {
            transportData.add(new PieChart.Data("Sanitation", sanitationCount));
        }
        int maintenanceCount = db.countType("maintenance"); // Add amount of maintenance requests
        if (maintenanceCount > 0) {
            transportData.add(new PieChart.Data("Maintenance", maintenanceCount));
        }
        int securityCount = db.countType("security"); // Add amount of security requests
        if (securityCount > 0) {
            transportData.add(new PieChart.Data("Security", securityCount));
        }
        int computerCount = db.countType("computer service"); // Add amount of computer service requests
        if (computerCount > 0) {
            transportData.add(new PieChart.Data("Computer Service", computerCount));
        }
        int avCount = db.countType("audio+visual"); // Add amount of AV requests
        if (avCount > 0) {
            transportData.add(new PieChart.Data("Audio/Visual", avCount));
        }
        int giftCount = db.countType("delivergift"); // Add amount of gift delivery requests
        if (giftCount > 0) {
            transportData.add(new PieChart.Data("Gift Delivery", giftCount));
        }
        PieChart transportChart = new PieChart(transportData);
        transportChart.setTitle("Request Types");
        transportChart.setClockwise(true);
        transportChart.setLabelLineLength(20);
        transportChart.setLabelsVisible(true);
        transportChart.setStartAngle(180);
        statsGridPane.add(transportChart, 0, 0);

        // Make the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(
                "Average Time"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time (Minutes)");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Average Time for Completion");
        if (languageCount > 0) { // Add average time for gurneys
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.setName("Language Interpreter");
            series1.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("language interpreter")));
            barChart.getData().add(series1);
        }
        if (religiousCount > 0) { // Add average time for wheelchairs
            XYChart.Series<String, Number> series2 = new XYChart.Series<>();
            series2.setName("Religious");
            series2.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("religion handler")));
            barChart.getData().add(series2);
        }
        if (sanitationCount > 0) { // Add average time for ambulances
            XYChart.Series<String, Number> series3 = new XYChart.Series<>();
            series3.setName("Sanitation");
            series3.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("sanitation")));
            barChart.getData().add(series3);
        }
        if (maintenanceCount > 0) { // Add average time for cars
            XYChart.Series<String, Number> series4 = new XYChart.Series<>();
            series4.setName("Maintenance");
            series4.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("maintenance")));
            barChart.getData().add(series4);
        }
        if (securityCount > 0) { // Add average time for helicopters
            XYChart.Series<String, Number> series5 = new XYChart.Series<>();
            series5.setName("Security");
            series5.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("security")));
            barChart.getData().add(series5);
        }
        if (computerCount > 0) { // Add average time for helicopters
            XYChart.Series<String, Number> series6 = new XYChart.Series<>();
            series6.setName("Computer Service");
            series6.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("computer service")));
            barChart.getData().add(series6);
        }
        if (avCount > 0) { // Add average time for helicopters
            XYChart.Series<String, Number> series7 = new XYChart.Series<>();
            series7.setName("Audio/Visual");
            series7.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("audio+visual")));
            barChart.getData().add(series7);
        }
        if (giftCount > 0) { // Add average time for helicopters
            XYChart.Series<String, Number> series8 = new XYChart.Series<>();
            series8.setName("Gift Delivery");
            series8.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("delivergift")));
            barChart.getData().add(series8);
        }
        statsGridPane.add(barChart, 0, 1);

        return true;
    }
}
