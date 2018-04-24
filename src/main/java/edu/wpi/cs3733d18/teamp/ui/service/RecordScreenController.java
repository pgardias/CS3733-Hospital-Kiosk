package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.RecordNotFoundException;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Record;
import edu.wpi.cs3733d18.teamp.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RecordScreenController implements Initializable {
    DBSystem db = DBSystem.getInstance();
    private PieChart pieChart;
    private BarChart barChart;
    private Request.requesttype filterType;


    @FXML
    JFXButton backButton;

    @FXML
    Label errorLabel;

    @FXML
    GridPane statsGridPane;

    @FXML
    JFXComboBox filterRecordComboBox;

    @FXML
    JFXTreeTableView<RecordsTable> recordsTreeTable;

    @FXML
    JFXTreeTableColumn<RecordsTable, String> requestTypeCol;

    @FXML
    JFXTreeTableColumn<RecordsTable, String> subTypeCol;

    @FXML
    JFXTreeTableColumn<RecordsTable, Integer> totalOfTypeCol;

    @FXML
    JFXTreeTableColumn<RecordsTable, Integer> totalTimeCol;

    @FXML
    JFXTreeTableColumn<RecordsTable, Integer> avgTimeCol;

    @FXML
    TreeItem<RecordsTable> root;
    private ArrayList<Record> records;
    private ArrayList<TreeItem<RecordsTable>> children;

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
        records = new ArrayList<>();
        children = new ArrayList<>();
        root = new TreeItem<>();

        requestTypeCol = new JFXTreeTableColumn<>("Request Type");
        subTypeCol = new JFXTreeTableColumn<>("Sub Type");
        totalOfTypeCol = new JFXTreeTableColumn<>("Total Of Type");
        totalTimeCol = new JFXTreeTableColumn<>("Total Time");
        avgTimeCol = new JFXTreeTableColumn<>("Average Time");

        requestTypeCol.setPrefWidth(288);
        subTypeCol.setPrefWidth(288);
        totalOfTypeCol.setPrefWidth(288);
        totalTimeCol.setPrefWidth(288);
        avgTimeCol.setPrefWidth(288);

        requestTypeCol.setResizable(false);
        subTypeCol.setResizable(false);
        totalOfTypeCol.setResizable(false);
        totalTimeCol.setResizable(false);
        avgTimeCol.setResizable(false);

        requestTypeCol.setSortable(true);
        subTypeCol.setSortable(true);
        totalOfTypeCol.setSortable(true);
        totalTimeCol.setSortable(true);
        avgTimeCol.setSortable(true);


        requestTypeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("requestType"));
        subTypeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("subType"));
        totalOfTypeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalOfType"));
        totalTimeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalTime"));
        avgTimeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("avgTime"));

        recordsTreeTable.getColumns().setAll(requestTypeCol, subTypeCol, totalOfTypeCol, totalTimeCol, avgTimeCol);

    }



    /**
     * Initialize the record table
     * @return
     */
    @FXML
    public void onStartUp() {
        buildTable();
        setChartAll();
    }

    private void buildTable(){
        try{
            records = db.getAllRecords();
        }catch (RecordNotFoundException r){
            errorLabel.setText("No record data: No requests have been completed!");

            return;
            // set Label, exit scene
        }
        children.clear();

        for(Record record: records) {
            System.out.println(db.RequestTypeToString(record.getRequestType()));
            if(filterType != null && record.getRequestType().equals(filterType)) {

                children.add(new TreeItem<>(new RecordsTable(db.RequestTypeToString(record.getRequestType()), record.getSubType(),
                        record.getTotalOfType(), record.getTotalTime(), record.getAvgTime())));
            }else if (filterType == null){
                children.add(new TreeItem<>(new RecordsTable(db.RequestTypeToString(record.getRequestType()), record.getSubType(),
                        record.getTotalOfType(), record.getTotalTime(), record.getAvgTime())));
            }
        }
        root.getChildren().setAll(children);
        recordsTreeTable.setRoot(root);
        recordsTreeTable.setShowRoot(false);

        return;
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
    @FXML
    public boolean filterRecordComboBoxOp(ActionEvent e) {
        String type = filterRecordComboBox.getValue().toString();
        type = type.toLowerCase();
        statsGridPane.getChildren().remove(pieChart);
        statsGridPane.getChildren().remove(barChart);
        switch (type) {
            case "language interpreter":
                filterType = Request.requesttype.LANGUAGEINTERP;
                setChartSubType(LanguageInterpreterController.languages);
                break;
            case "religious request":
                filterType = Request.requesttype.HOLYPERSON;
                setChartSubType(ReligiousServiceController.religions);
                break;
            case "sanitation request":
                filterType = Request.requesttype.SANITATION;
                setChartSubType(SanitationController.messes);
                break;
            case "maintenance request":
                filterType = Request.requesttype.MAINTENANCE;
                setChartSubType(MaintenanceController.machines);
                break;
            case "computer service request":
                filterType = Request.requesttype.COMPUTER;
                setChartSubType(ComputerServiceController.devices);
                break;
            case "security request":
                filterType = Request.requesttype.SECURITY;
                setChartSubType(SecurityController.situations);
                break;
            case "audio/visual request":
                filterType = Request.requesttype.AV;
                setChartSubType(AudioVisualController.AVDevices);
                break;
            case "gift delivery request":
                filterType = Request.requesttype.GIFTS;
                setChartSubType(GiftDeliveryController.gifts);
                break;
            default:
                filterType = null;
                setChartAll();
        }
        buildTable();

        return true;
    }

    /**
     * Generates charts for all request types
     */
    private void setChartAll() {

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
