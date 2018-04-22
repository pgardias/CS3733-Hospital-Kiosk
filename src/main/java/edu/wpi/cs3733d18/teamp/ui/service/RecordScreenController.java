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
    private ArrayList<Record> records;
    private PieChart pieChart;
    private BarChart barChart;

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
        Parent root;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        backButton.getScene().setRoot(root);
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
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartAll();
                break;
            case "Language Interpreter":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("language interpreter")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(LanguageInterpreterController.languages);
                break;
            case  "Religious Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("religion handler")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(ReligiousServiceController.religions);
                break;
            case  "Sanitation Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("sanitation")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(SanitationController.messes);
                break;
            case  "Maintenance Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("maintenance")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(MaintenanceController.machines);
                break;
            case  "Security Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("security")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(SecurityController.situations);
                break;
            case  "Computer Service Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("computer service")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(ComputerServiceController.devices);
                break;
            case  "Audio/Visual Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("audio+visual")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(AudioVisualController.AVDevices);
                break;
            case  "Gift Delivery Request":
                recordTable.getItems().clear();
                for (Record record: records) {
                    if (db.RequestTypeToString(record.getRequestType()).equals("delivergift")) {
                        recordTable.getItems().add(record);
                    }
                }
                statsGridPane.getChildren().remove(pieChart);
                statsGridPane.getChildren().remove(barChart);
                setChartSubType(GiftDeliveryController.gifts);
                break;
        }
        return true;
    }

    /**
     * Generates charts for all request types
     */
    private Boolean setChartAll() {

        // Make the pie chart
        ObservableList<PieChart.Data> requestData = FXCollections.observableArrayList();
        int languageCount = db.countType("language interpreter"); // Add amount of language interpreter requests
        if (languageCount > 0) {
            requestData.add(new PieChart.Data("Language Interpreter", languageCount));
        }
        int religiousCount = db.countType("religion handler"); // Add amount of religious requests
        if (religiousCount > 0) {
            requestData.add(new PieChart.Data("Religious", religiousCount));
        }
        int sanitationCount = db.countType("sanitation"); // Add amount of sanitation requests
        if (sanitationCount > 0) {
            requestData.add(new PieChart.Data("Sanitation", sanitationCount));
        }
        int maintenanceCount = db.countType("maintenance"); // Add amount of maintenance requests
        if (maintenanceCount > 0) {
            requestData.add(new PieChart.Data("Maintenance", maintenanceCount));
        }
        int securityCount = db.countType("security"); // Add amount of security requests
        if (securityCount > 0) {
            requestData.add(new PieChart.Data("Security", securityCount));
        }
        int computerCount = db.countType("computer service"); // Add amount of computer service requests
        if (computerCount > 0) {
            requestData.add(new PieChart.Data("Computer Service", computerCount));
        }
        int avCount = db.countType("audio+visual"); // Add amount of AV requests
        if (avCount > 0) {
            requestData.add(new PieChart.Data("Audio/Visual", avCount));
        }
        int giftCount = db.countType("delivergift"); // Add amount of gift delivery requests
        if (giftCount > 0) {
            requestData.add(new PieChart.Data("Gift Delivery", giftCount));
        }
        PieChart requestPieChart = new PieChart(requestData);
        requestPieChart.setTitle("Request Types");
        requestPieChart.setClockwise(true);
        requestPieChart.setLabelLineLength(20);
        requestPieChart.setLabelsVisible(true);
        requestPieChart.setStartAngle(180);
        statsGridPane.add(requestPieChart, 0, 0);
        pieChart = requestPieChart;

        // Make the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(
                "Average Time"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time (Minutes)");
        BarChart<String, Number> requestBarChart = new BarChart<>(xAxis, yAxis);
        requestBarChart.setTitle("Average Time for Completion");
        if (languageCount > 0) { // Add average time for language interpreter requests
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.setName("Language Interpreter");
            series1.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("language interpreter")));
            requestBarChart.getData().add(series1);
        }
        if (religiousCount > 0) { // Add average time for religious requests
            XYChart.Series<String, Number> series2 = new XYChart.Series<>();
            series2.setName("Religious");
            series2.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("religion handler")));
            requestBarChart.getData().add(series2);
        }
        if (sanitationCount > 0) { // Add average time for sanitation requests
            XYChart.Series<String, Number> series3 = new XYChart.Series<>();
            series3.setName("Sanitation");
            series3.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("sanitation")));
            requestBarChart.getData().add(series3);
        }
        if (maintenanceCount > 0) { // Add average time for maintenance requests
            XYChart.Series<String, Number> series4 = new XYChart.Series<>();
            series4.setName("Maintenance");
            series4.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("maintenance")));
            requestBarChart.getData().add(series4);
        }
        if (securityCount > 0) { // Add average time for security requests
            XYChart.Series<String, Number> series5 = new XYChart.Series<>();
            series5.setName("Security");
            series5.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("security")));
            requestBarChart.getData().add(series5);
        }
        if (computerCount > 0) { // Add average time for computer service requests
            XYChart.Series<String, Number> series6 = new XYChart.Series<>();
            series6.setName("Computer Service");
            series6.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("computer service")));
            requestBarChart.getData().add(series6);
        }
        if (avCount > 0) { // Add average time for AV requests
            XYChart.Series<String, Number> series7 = new XYChart.Series<>();
            series7.setName("Audio/Visual");
            series7.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("audio+visual")));
            requestBarChart.getData().add(series7);
        }
        if (giftCount > 0) { // Add average time for gift delivery requests
            XYChart.Series<String, Number> series8 = new XYChart.Series<>();
            series8.setName("Gift Delivery");
            series8.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTime("delivergift")));
            requestBarChart.getData().add(series8);
        }
        statsGridPane.add(requestBarChart, 0, 1);
        barChart = requestBarChart;

        return true;
    }

    /**
     * Generates charts for language request types
     */
    private Boolean setChartSubType(ObservableList ol) {
        int curCount;

        //Make the pie chart
        ObservableList<PieChart.Data> curData = FXCollections.observableArrayList();
        for (Object item: ol) {
            curCount = db.countSubType((String)item);
            if (curCount > 0) { // If there is some of this item, add to pie chart
                curData.add(new PieChart.Data((String)item, curCount));
            }
        }
        PieChart curPieChart = new PieChart(curData);
        curPieChart.setTitle("Sanitation Types");
        curPieChart.setClockwise(true);
        curPieChart.setLabelLineLength(20);
        curPieChart.setLabelsVisible(true);
        curPieChart.setStartAngle(180);
        statsGridPane.add(curPieChart, 0, 0);
        pieChart = curPieChart;

        // Make the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(
                "Average Time"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time (Minutes)");
        BarChart<String, Number> curBarChart = new BarChart<>(xAxis, yAxis);
        curBarChart.setTitle("Average Time for Completion");
        for (Object item: ol) {
            curCount = db.countSubType((String)item);
            if (curCount > 0) { // Add average time for current request
                XYChart.Series<String, Number> series1 = new XYChart.Series<>();
                series1.setName((String)item);
                series1.getData().add(new XYChart.Data<>("Average Time", db.recordAverageTimeSub((String)item)));
                curBarChart.getData().add(series1);
            }
        }
        statsGridPane.add(curBarChart, 0, 1);
        barChart = curBarChart;

        return true;
    }
}
